#!/bin/bash

# errors
# 1     :
# 255   : path does not exist
# 127   : command not found/ No such file or directory
# Shawn Jin

if [[ $# -lt 2 ]]; then
    echo "Usage: bash aws_s3_download.sh [target bucket] [target position]"
    exit 1
fi
echo "aws s3 cp s3://$1 $2 " > download_log

if [[ ! -e $2 ]]; then
  echo "Target file doesn't exist, creating folder..." >> download_log
  mkdir "$2"
fi



#if [[ -d $1 ]]; then
#    echo "downloading folder..." >> download_log
#    aws s3 cp s3://"$1" "$2" --recursive
#else
#    echo "downloading file..." >> download_log
#    aws s3 cp s3://"$1" "$2"
#fi

download_success=1
aws s3 cp s3://"$1" "$2"
if [[ $? -eq 1 ]]; then
    folder_name=$(basename "$1")
    echo "creating folder $folder_name... " >> download_log

    mkdir "$folder_name"
    echo "aws s3 cp s3://$1 $2/$folder_name --recursive" >> download_log
    aws s3 cp s3://"$1" "$2/$folder_name" --recursive
    # shellcheck disable=SC2181
    if [[ $? -eq 0 ]]; then
        download_success=0
    fi
else
  download_success=0
fi

exit "$download_success"

