import os
import sys
from pathlib import Path

def create_env_file():
    env_path = Path(".env")
    
    if env_path.exists():
        response = input(".env file already exists. Do you want to overwrite it? (y/N): ")
        if response.lower() != 'y':
            print("Setup cancelled.")
            return
    
    print("Setting up environment variables for the sweeps casino daily bonus bot...")
    print("You can leave fields empty to skip them.\n")
    
    print("=== Discord Bot Configuration ===")
    discord_token = input("Discord Bot Token: ").strip()
    discord_guild_id = input("Discord Guild ID: ").strip()
    
    print("\n=== 2captcha API Configuration ===")
    twocaptcha_key = input("2captcha API Key: ").strip()
    
    print("\n=== Chrome Data Directory ===")
    chrome_data_dir = input("Chrome Data Directory Path: ").strip()
    with open(env_path, 'w') as f:
        f.write("# Discord Bot Configuration\n")
        if discord_token:
            f.write(f"DISCORD_BOT_TOKEN={discord_token}\n")
        else:
            f.write("DISCORD_BOT_TOKEN=\n")
            
        if discord_guild_id:
            f.write(f"DISCORD_GUILD_ID={discord_guild_id}\n")
        else:
            f.write("DISCORD_GUILD_ID=\n")
            
        f.write("\n# 2captcha API Configuration\n")
        if twocaptcha_key:
            f.write(f"TWOCAPTCHA_API_KEY={twocaptcha_key}\n")
        else:
            f.write("TWOCAPTCHA_API_KEY=\n")
            
        f.write("\n# Chrome Data Directory\n")
        if chrome_data_dir:
            f.write(f"CHROME_DATA_DIR={chrome_data_dir}\n")
        else:
            f.write("CHROME_DATA_DIR=\n")
    
    print(f"\n.env file created at {env_path.absolute()}")
    print("\nNext steps:")
    print("1. Edit the .env file to add any missing values")
    print("2. Make sure the .env file is in your .gitignore")
    print("3. Run your Java application or Python scripts")

def check_env_file():
    env_path = Path(".env")
    
    if not env_path.exists():
        print(".env file not found")
        return False
    
    print(".env file found")
    
    with open(env_path, 'r') as f:
        lines = f.readlines()
    
    print("\nCurrent environment variables:")
    for line in lines:
        line = line.strip()
        if line and not line.startswith('#') and '=' in line:
            key, value = line.split('=', 1)
            if value:
                display_value = value[:4] + "..." if len(value) > 4 else value
                print(f"  {key}: {display_value}")
            else:
                print(f"  {key}: (not set)")
    
    return True

def main():
    print("Sweeps Casino Daily Bonus Bot - Environment Setup")
    print("=" * 50)
    
    if len(sys.argv) > 1 and sys.argv[1] == "check":
        check_env_file()
        return
    
    if check_env_file():
        response = input("\nDo you want to recreate the .env file? (y/N): ")
        if response.lower() != 'y':
            print("Setup cancelled.")
            return
    
    create_env_file()

if __name__ == "__main__":
    main()
