import sys
import nodriver as uc
import asyncio






async def claim_daily(browser, casino_email, casino_password, gmail, gmail_password):
    page = await browser.get("https://sheeshcasino.com/")
    login_button = await page.find("Log in", best_match=True, timeout=20)
    await login_button.click()


    email_form = await page.find("Email", best_match=True, timeout=20)
    await email_form.send_keys(casino_email)

    password_form = await page.find("password", best_match=True, timeout=30)
    await password_form.send_keys(casino_password)
    await asyncio.sleep(0.5)

    login_button = await page.select("#pr_id_25 > div > form > div > button", timeout=20)
    await login_button.click()


    await browser.cdp.send("Browser.grantPermissions", {
        "origin": "https://sheeshcasino.com/",
        "permissions": ["geolocation"]
    })

    await asyncio.sleep(2)


    daily_button = await page.select("body > div.relative.w-full.h-full > main > div.relative.flex-1 > div.desktop-closed.block.w-\[62px\].peer.overflow-hidden.transition-all.duration-300.pt-1.pb-3.h-full > div > div > button:nth-child(14)", timeout=20)
    await daily_button.click()



    claim_button = await page.select("body > div.relative.w-full.h-full > main > div.relative.flex-1 > div.ml-0.peer-\[\.desktop-closed\]\:md\:ml-\[62px\].peer-\[\.desktop-opened\]\:ml-\[230px\].absolute.inset-0.flex.pt-2.transition-all.duration-300.pb-\[calc\(3\.6rem\+env\(safe-area-inset-bottom\)\)\].sm\:pb-0 > div > div.bg-\[\#050F1FE5\].px-3.sm\:px-10.py-5.rounded-\[32px\].shadow-\[inset_0px_0px_3\.4px_0px_\#FFFFFF1A\,_0px_0px_0px_5px_\#00000014\].max-w-2xl.w-full.text-white > button", timeout=20)
    await claim_button.click()



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
