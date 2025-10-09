import sys
import nodriver as uc
import asyncio
import imaplib
import email
import re
from bs4 import BeautifulSoup



def get_chumba_code(email_address: str, app_password: str) -> str | None:
    try:
        mail = imaplib.IMAP4_SSL("imap.gmail.com", 993)
        mail.login(email_address, app_password)
        mail.select("inbox")

        status, data = mail.search(None, '(FROM "support@chumbacasino.com")')
        if status != "OK" or not data[0]:
            return None

        latest_email_id = data[0].split()[-1]
        status, msg_data = mail.fetch(latest_email_id, "(RFC822)")
        if status != "OK":
            return None

        raw_email = msg_data[0][1]
        msg = email.message_from_bytes(raw_email)

        body = ""
        if msg.is_multipart():
            for part in msg.walk():
                ctype = part.get_content_type()
                if ctype in ["text/plain", "text/html"]:
                    payload = part.get_payload(decode=True)
                    if payload:
                        body += payload.decode(errors="ignore")
        else:
            body = msg.get_payload(decode=True).decode(errors="ignore")

        soup = BeautifulSoup(body, "html.parser")
        text = soup.get_text()

        match = re.search(r"\b\d{6}\b", text)
        code = match.group(0) if match else None

        #delete mail for no later errors
        if code:
            mail.store(latest_email_id, '+FLAGS', '\\Deleted')
            mail.expunge()

        mail.logout()
        return code

    except Exception:
        return None




async def claim_daily(browser, casino_email, casino_password, gmail, gmail_password):
    page = await browser.get("https://login.chumbacasino.com/")
    email_form = await page.select("#login_email-input", timeout=30)
    await email_form.send_keys(casino_email)

    password_form = await page.select("#login_password-input", timeout=30)
    await password_form.send_keys(casino_password)

    while True:
        try:
            login_button = await page.find("log in", best_match=True)
            await login_button.click()
            break
        except:
            await asyncio.sleep(5)
            continue
    
    send_code = await page.find("send code", best_match=True)
    await send_code.click()
    
    code = None
    for i in range(5):
        code = get_chumba_code(gmail, gmail_password)
        if code:
            break
        await asyncio.sleep(10)


    #input 2fa code on page
    for i in range(6):
        elem = await page.select(f"#otp-code-input-{i}")
        await elem.send_keys(code[i])

    await asyncio.sleep(2)

    verify_button = await page.find("verify", best_match=True)
    await verify_button.click()

    #logged in!
    get_coins = await page.find("get coins", best_match=True)
    await get_coins.click()

    daily_bonus = await page.find("Daily Bonus", best_match=True)
    await daily_bonus.click()

    await page.scroll_down(150)

    try:
        claim = await page.find("Claim", best_match=True, timeout=5)
        await claim.click()
        await asyncio.sleep(2)
        print(f"[Success] claimed daily bonus for {casino_email}")
    except:
        print(f"[Fail] Daily bonus already claimed for {casino_email}")





async def main():
    if len(sys.argv) < 5:
        print("Usage: !run test.py <email> <password>")
        return
    
    casino_email = sys.argv[1]
    casino_password = sys.argv[2]
    gmail = sys.argv[3]
    gmail_password = sys.argv[4]

    browser = await uc.start()

    await claim_daily(browser, casino_email, casino_password, gmail, gmail_password)



    

if __name__ == "__main__":
    uc.loop().run_until_complete(main())
