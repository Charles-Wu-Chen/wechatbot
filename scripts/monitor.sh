#!/bin/bash

botstartcount=0
deploydir="/home/ec2-user/deployment"
logfile=${deploydir}"/botoutput.log"
day=$(date +%Y-%m-%d_%H%M%S)

echo "$day monitor started"
if [ -e $logfile ]; then
  botstartcount=$(grep "logout logged out at" $logfile | wc -l)
fi

if [ $botstartcount -ge 1 ]; then
  echo "too many login count $botstartcount , time to restart"
  ${deploydir}/stop_bot.sh
  sleep 10
  ${deploydir}/start_bot.sh
  echo "restarted"
else
  echo "nothing to do count is  $botstartcount"
fi
