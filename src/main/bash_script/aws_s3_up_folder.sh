#!/bin/bash

# errors
# -1    :
# 1     :
# 255   : path does not exist 
# 127   : command not found 


# Shawn Jin

# check the input argument 
if [[ $# -lt 2 ]]; then
    echo "Usage: bash awss3tool.sh [folder] [target bucket]";
    
    exit -1
fi

echo "parameter one is: $1."
echo "parameter one is: $2."

echo "uploading folder..."


echo "/usr/local/bin/aws s3 cp $1 s3://$2 --recursive"
aws s3 cp $1 s3://$2 --recursive


