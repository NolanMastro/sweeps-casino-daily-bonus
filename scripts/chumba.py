import sys
import nodriver as uc






async def claim_daily(browser, casino_email, casino_password, gmail, gmail_password):
    page = await browser.get("https://login.chumbacasino.com/")
    email_form = await page.select("#login_email-input", timeout=30)
    await email_form.send_keys(email)

    password_form = await page.select("#login_password-input", timeout=30)
    await password_form.send_keys(password)

    login_button = await page.select("#login_submit-button", timeout=30)
    await login_button.click()




async def main():
    if len(sys.argv) != 4:
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
