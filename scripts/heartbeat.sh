#!/bin/bash

RANDOM=$$
while true; do
curl -v -X POST localhost:8080/messages -H 'Content-Type:application/json' -d '{"toID": "wxid_1194601945911", "content": '$RANDOM', "messageType":"Text", "messageChannel": "Person"}'
sleep 300
done

