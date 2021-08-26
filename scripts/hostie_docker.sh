#!/bin/bash
export WECHATY_PUPPET_PADPLUS_TOKEN=puppet_padplus_129d8c1d9d408b6f
export WECHATY_HOSTIE_PORT=8788
export WECHATY_PUPPET=wechaty-puppet-padplus
export WECHATY_LOG=verbose

docker run \
  -d -e WECHATY_LOG="$WECHATY_LOG" \
  -e WECHATY_PUPPET="$WECHATY_PUPPET" \
  -e WECHATY_PUPPET_PADPLUS_TOKEN="$WECHATY_PUPPET_PADPLUS_TOKEN" \
  -e WECHATY_HOSTIE_PORT="$WECHATY_HOSTIE_PORT" \
  -e WECHATY_TOKEN="$WECHATY_PUPPET_PADPLUS_TOKEN" \
  -p "$WECHATY_HOSTIE_PORT:$WECHATY_HOSTIE_PORT" \
  wechaty/wechaty 