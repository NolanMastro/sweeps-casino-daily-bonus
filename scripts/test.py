import sys

def main():
    if len(sys.argv) < 5:
        print("Usage: python test.py <casino_email> <casino_password> <gmail> <gmail_password>")
        return

    casino_email = sys.argv[1]
    casino_password = sys.argv[2]
    gmail = sys.argv[3]
    gmail_password = sys.argv[4]

    print("=== Parameters Received ===")
    print(f"Casino Email: {casino_email}")
    print(f"Casino Password: {casino_password}")
    print(f"Gmail: {gmail}")
    print(f"Gmail App Password: {gmail_password}")
    print("===========================")

if __name__ == "__main__":
    main()
