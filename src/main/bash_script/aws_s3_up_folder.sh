#!/bin/bash

# errors
# 2     : invalid paramaters
# 255   : path does not exist 
# 127   : command not found 


# Shawn Jin

# check the input argument 
if [[ $# -lt 2 ]]; then
    echo "Usage: bash aws_s3_up_folder.sh [folder] [target bucket]"
    exit 2
fi

echo "uploading folder..."
folder_name=$(basename "$1")


echo "/usr/local/bin/aws s3 cp $1 s3://$2/$folder_name --recursive" > upload_log
aws s3 cp "$1" s3://"$2"/"$folder_name" --recursive


