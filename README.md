# Sweeps Casino Daily Bonus Bot

A Discord bot that automates claiming daily bonuses from various sweeps casinos. This bot uses browser automation to log into casino accounts and claim daily bonuses automatically.

## Features

- **Discord Integration**: Easy-to-use slash commands for managing casino accounts
- **Multi-Casino Support**: Works with Chumba Casino, Modo Casino, and Dara Casino
- **Automated CAPTCHA Solving**: Uses 2captcha service for Modo Casino CAPTCHAs
- **Gmail Integration**: Automatically retrieves 2FA codes for Chumba Casino
- **Secure Storage**: Environment variables and encrypted local storage for sensitive data
- **Browser Automation**: Uses Playwright for reliable web automation

## Quick Start

### Prerequisites

- Java 11 or higher
- Python 3.8 or higher
- Chrome browser installed
- Discord bot token
- 2captcha API key (for Modo Casino)

### 1. Clone and Setup

```bash
git clone [<repository-url>](https://github.com/NolanMastro/sweeps-casino-daily-bonus.git)
cd sweeps-casino-daily-bonus
```

### 2. Environment Configuration

Run the setup script to configure your environment:

```bash
python3 setup_env.py
```

This will create a `.env` file with the following structure:

```env
# Discord Bot Configuration
DISCORD_BOT_TOKEN=your_discord_bot_token_here
DISCORD_GUILD_ID=your_discord_guild_id_here

# 2captcha API Configuration
TWOCAPTCHA_API_KEY=your_2captcha_api_key_here

# Chrome Data Directory
CHROME_DATA_DIR=path_to_your_chrome_data_directory
```

Alternatively, you can copy the `.env.example` file and fill in your values:

```bash
cp .env.example .env
# Then edit .env with your actual values
```

### 3. Install Python Dependencies

```bash
pip install -r requirements.txt
playwright install chromium
```

### 4. Run the Discord Bot

```bash
# Compile Java files
javac -cp "lib/JDA-6.0.0-rc.5-withDependencies.jar" src/*.java -d bin/

# Start the bot
java -cp "bin:lib/JDA-6.0.0-rc.5-withDependencies.jar" App

# invite the discord bot to your server @ https://discord.com/developers/
```

## Discord Commands

| Command | Description | Example |
|---------|-------------|---------|
| `/run <service> <email> <password>` | Run casino automation | `/run chumba user@email.com password123` |
| `/setgmail <email> <app_password>` | Save Gmail credentials | `/setgmail user@gmail.com app_password` |
| `/help` | Show available commands | `/help` |

### Supported Services
- `chumba` - Chumba Casino
- `modo` - Modo Casino  
- `dara` - Dara Casino

## Supported Casinos

### Chumba Casino
- **Features**: Daily bonus claiming with 2FA support
- **Requirements**: Gmail account for 2FA code retrieval
- **Setup**: Use `/setgmail` command to save Gmail credentials

### Modo Casino
- **Features**: Daily bonus claiming with CAPTCHA solving
- **Requirements**: 2captcha API key
- **Setup**: Add `TWOCAPTCHA_API_KEY` to your `.env` file

### Dara Casino
- **Features**: Simple daily bonus claiming
- **Requirements**: Casino account credentials only

## Manual Script Usage

You can also run the Python scripts directly:

```bash
# Chumba Casino (requires Gmail for 2FA)
python3 scripts/chumba.py casino_email casino_password gmail gmail_app_password

# Modo Casino (uses 2captcha for CAPTCHAs)
python3 scripts/modo.py casino_email casino_password

# Dara Casino (simple login)
python3 scripts/dara.py casino_email casino_password
```

## Security & Privacy

### Environment Variables
- All sensitive data is stored in environment variables
- The `.env` file is automatically excluded from git
- Never commit API keys or passwords to version control

### Data Storage
- Gmail credentials are stored locally per Discord user
- Casino passwords are not stored permanently
- All data is encrypted and user-specific

### Best Practices
- Use Gmail App Passwords instead of your regular password
- Keep your Discord bot token secure
- Monitor your 2captcha API usage and balance
- Use a dedicated Chrome profile for automation

## Troubleshooting

### Check Environment Setup
```bash
python3 setup_env.py check
```

### Common Issues

**Bot not responding to commands**
- Verify `DISCORD_BOT_TOKEN` and `DISCORD_GUILD_ID` are correct
- Ensure bot has proper permissions in your Discord server
- Check that slash commands are registered (restart bot if needed)

**Chrome automation fails**
- Ensure Chrome is not running when scripts execute
- Verify `CHROME_DATA_DIR` path exists and is accessible
- Check that Playwright is properly installed

**2captcha errors**
- Verify your API key has sufficient balance
- Check that the API key is correctly set in `.env`
- Ensure you have credits available for CAPTCHA solving

**Gmail 2FA issues**
- Use Gmail App Passwords, not your regular password
- Ensure 2FA is enabled on your Gmail account
- Check that IMAP is enabled in Gmail settings

### Debug Mode
Run individual scripts with verbose output to debug issues:

```bash
python3 scripts/chumba.py --debug casino_email casino_password gmail gmail_password
```

## Project Structure

```
sweeps-casino-daily-bonus/
├── src/                    # Java Discord bot source code
│   ├── App.java           # Main bot application
│   ├── UserSession.java   # User session management
│   └── UserStorage.java   # Local data storage
├── scripts/               # Python automation scripts
│   ├── chumba.py          # Chumba Casino automation
│   ├── modo.py            # Modo Casino automation
│   ├── dara.py            # Dara Casino automation
│   ├── test.py            # Parameter testing script
│   └── env_loader.py       # Environment variable loader
├── lib/                   # Java dependencies
│   └── JDA-6.0.0-rc.5-withDependencies.jar
├── bin/                   # Compiled Java classes
├── setup_env.py           # Environment setup script
├── requirements.txt       # Python dependencies
├── .env.example           # Environment variables template
├── users.json             # Local user data storage
├── .gitignore            # Git ignore rules
├── LICENSE               # MIT License
└── README.md              # This file
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## Disclaimer

This bot is for educational purposes only. Please ensure you comply with:
- Terms of service of each casino platform
- Local laws and regulations regarding online gambling
- Discord's Terms of Service
- Responsible gambling practices

Use at your own risk. The authors are not responsible for any account suspensions or other consequences.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

If you encounter issues:
1. Check the troubleshooting section above
2. Verify your environment setup
3. Check the Discord bot logs for error messages
4. Ensure all dependencies are properly installed

For additional help, please open an issue on GitHub with:
- Your operating system
- Error messages (if any)
- Steps to reproduce the issue
- Your environment setup (without sensitive data)
