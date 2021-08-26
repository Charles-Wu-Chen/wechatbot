#!/bin/bash
deploydir="/home/ec2-user/deployment"
logfile="${deploydir}/botoutput.log"
export WECHATY_PUPPET_HOSTIE_TOKEN=puppet_donut_17125aeebb3f5ed9
nohup java -jar -Dlog4j.configurationFile=/home/ec2-user/deployment/log4j2.xml /home/ec2-user/deployment/wechaty-examples-1.0.0-SNAPSHOT-jar-with-dependencies.jar > $logfile 2> boterror.log &

