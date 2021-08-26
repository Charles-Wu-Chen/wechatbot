# wechatbot
chatbot for wechat

# Releases

## Version 1
Add ehcache to detect and remove duplicate message as workaround of donut puppet issue. 20201212

##CI/CD commands

aws s3 cp . s3://wechatybot/script  --recursive --exclude="*" --include "*.sh"

in ec2:
aws s3 cp s3://wechatybot/script . --recursive --exclude="*" --include "*.sh"

## Copyright & License
* Code & Docs © 2020 Wechaty Contributors [https://github.com/wechaty](https://github.com/wechaty)
* Code released under the Apache-2.0 License
* Docs released under Creative Commons
