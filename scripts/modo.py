import sys
import nodriver as uc
from nodriver import *
from twocaptcha import TwoCaptcha
import asyncio
import random


solver = TwoCaptcha("apikey")#TODO convert to .env

async def solve_captcha_with_retry(sitekey, url, max_retries=5):
    for attempt in range(max_retries):
        try:
            print(f"Solving CAPTCHA (attempt {attempt + 1}/{max_retries})...")
            result = solver.recaptcha(sitekey=sitekey, url=url)
            print("CAPTCHA solved.")
            return result['code']
        except Exception as e:
            print(f"CAPTCHA solve failed: {e}")
            await asyncio.sleep(random.randint(10, 30))


async def claim_daily(browser, casino_email, casino_password):
    page = await browser.get("https://login.modo.us/login?",new_tab=True, new_window=False)
    await asyncio.sleep(10)
    #login_button = await page.select("#login-cta", timeout=30)
    #await login_button.click()
    await asyncio.sleep(5)

    email_form = await page.select("#\\31 -email", timeout=30)
    await email_form.send_keys(casino_email)
    await asyncio.sleep(0.5)

    password_form = await page.select(r"#\31 -password", timeout=10)
    await password_form.send_keys(casino_password)

    token = await solve_captcha_with_retry(
        sitekey="6Le4JGgpAAAAAFBQxpOYBQcveZJ_3_a1iuzSaolM",
        url="https://modo.us/amoe"
        )

    await page.evaluate(f'''
        const reduceObjectToArray = (obj) => Object.keys(obj).reduce(function (r, k) {{
            return r.concat(k, obj[k]);
        }}, []);
    
        const client = ___grecaptcha_cfg.clients[0];
        let result = reduceObjectToArray(client).filter(c => Object.prototype.toString.call(c) === "[object Object]");

        result = result.flatMap(r => reduceObjectToArray(r));
        result = result.filter(c => Object.prototype.toString.call(c) === "[object Object]");

        const reqObj = result.find(r => r.callback);
        if (reqObj && typeof reqObj.callback === 'function') {{
            reqObj.callback("{token}");
        }}
    ''')

    await asyncio.sleep(2)


    login_button = await page.select(r"#\31 -submit", timeout=10)
    await login_button.click()
    await asyncio.sleep(5)#logged in
    
    
    daily_bonus_button = await page.select("#__next > div > div > div.MuiStack-root.css-1g4yje1 > div > div.MuiStack-root.css-1hssair > div > div:nth-child(1) > button > div", timeout=10)
    await daily_bonus_button.click()

    await asyncio.sleep(2)

    final_button = await page.select("body > div.MuiDialog-root.MuiModal-root.css-111hvl9 > div.MuiDialog-container.MuiDialog-scrollPaper.css-fh1hs4 > div > div > div.MuiDialogActions-root.MuiDialogActions-spacing.css-jatgw8 > div", timeout=10)
    text = final_button.text.strip()
    if text == "Claimed":
        print(f"[Fail] Daily bonus already claimed for {casino_email}")
    elif text == "Claim":
        await final_button.click()
        print(f"[Success] claimed daily bonus for {casino_email}")
        await asyncio.sleep(5)
    else:
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
