#!/usr/bin/env python3
"""
Environment variable loader for Python scripts.
This script loads environment variables from .env file.
"""

import os
import sys
from pathlib import Path

def load_env_file(env_file_path=".env"):
    """Load environment variables from .env file"""
    env_path = Path(env_file_path)
    
    if not env_path.exists():
        print(f"Warning: {env_file_path} file not found")
        return
    
    with open(env_path, 'r') as f:
        for line in f:
            line = line.strip()
            if line and not line.startswith('#') and '=' in line:
                key, value = line.split('=', 1)
                os.environ[key.strip()] = value.strip()

def get_env_var(var_name, required=True):
    """Get environment variable with optional requirement check"""
    value = os.getenv(var_name)
    if required and not value:
        print(f"Error: {var_name} environment variable is not set")
        sys.exit(1)
    return value

if __name__ == "__main__":
    # Load .env file when this module is imported
    load_env_file()
