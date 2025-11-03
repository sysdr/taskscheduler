#!/bin/bash

# Check if Python 3 is available
if ! command -v python3 &> /dev/null; then
    echo "❌ Python 3 is required but not found. Please install Python 3."
    exit 1
fi

# Check if pika is installed
if ! python3 -c "import pika" 2>/dev/null; then
    echo "📦 Installing pika library..."
    pip3 install pika --user
fi

# Run the Python script
python3 "$(dirname "$0")/populate_data.py"

