#!/bin/bash
deploydir="/home/ec2-user/deployment"
logfile="${deploydir}/botoutput.log"

function mv_to_backup() {
  output=${deploydir}/bot_$(date +%Y-%m-%d_%H%M%S).bak

  if [ -e $logfile ]; then
    echo "found log and going to move to bk $output"
    mv $logfile $output
  else
    echo "not found $logfile"
  fi
}

pkill --signal KILL -f wechaty-examples
sleep 10
mv_to_backup
