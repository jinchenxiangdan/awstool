#!/bin/bash

# Shawn Jin
echo "uploading folder..."

# check the input argument 
if [[ $# -lt 2 ]]; then
    echo "Usage: bash awss3tool.sh [option] [target bucket]";
    echo "If you are using Java program, please check the parameters in Java."
    exit 1
fi
echo "parameter one is: $($1)."
echo "Done!"

