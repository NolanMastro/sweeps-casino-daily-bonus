import sys
import nodriver as uc






async def claim_daily(browser, email, password):
    page = await browser.get("https://login.chumbacasino.com/")
    email_form = await page.select("#login_email-input", timeout=30)
    await email_form.send_keys(email)

    password_form = await page.select("#login_password-input", timeout=30)
    await password_form.send_keys(password)

    login_button = await page.select("#login_submit-button", timeout=30)
    await login_button.click()




async def main():
    if len(sys.argv) != 3:
        print("Usage: !run test.py <email> <password>")
        return
    
    email = sys.argv[1]
    password = sys.argv[2]
    browser = await uc.start()

    await claim_daily(browser)



    

if __name__ == "__main__":
    main()
