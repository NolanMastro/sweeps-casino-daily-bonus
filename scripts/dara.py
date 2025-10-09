import sys
import nodriver as uc
from nodriver import *
import asyncio





async def claim_daily(browser, casino_email, casino_password):
    page = await browser.get("https://daracasino.com/login",new_tab=True, new_window=False)
    email_form = await page.find("Email", timeout=30, best_match=True)
    await email_form.send_keys(casino_email)
    await asyncio.sleep(0.5)

    password_form = await page.select("#__layout > div > div.login-wrap > div > div > div.login-box.limit-height > div.login-body > div.input-wrap > div:nth-child(2) > div > input[type=password]", timeout=30)
    await password_form.send_keys(casino_password)
    await asyncio.sleep(0.5)


    login_button = await page.select("#__layout > div > div.login-wrap > div > div > div.login-box.limit-height > div.login-footer > div.btn-wrap > button", timeout=10)
    await login_button.click()

    await asyncio.sleep(5)
    
    
    try:
        spin_and_win = await page.find("Spin & Win")
        await spin_and_win.click()
        await asyncio.sleep(15)
        print(f"[Success] claimed daily bonus for {casino_email}")
    except:
        print(f"[Fail] Daily bonus already claimed for {casino_email}")






async def main():
    if len(sys.argv) < 2:
        print("Usage: !run test.py <email> <password>")
        return
    
    casino_email = sys.argv[1]
    casino_password = sys.argv[2]

    browser = await uc.start(
        user_data_dir=r"C:\Code\sweepsDaily\sweepsDaily\chrome_data_dir"
    )

    await claim_daily(browser, casino_email, casino_password)



    

if __name__ == "__main__":
    uc.loop().run_until_complete(main())
