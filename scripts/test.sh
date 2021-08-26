output=bot_$(date +%Y-%m-%d_%H%M%S).bak
logfile="./botoutput.log"
if [ -e $logfile ]; then
	    echo "found log and going to move to bk $output"
	        mv ./botoutput.log $output
	else
		    echo "not found $logfile"
fi
