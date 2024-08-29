#!/bin/bash

# 실행 중이거나 중지된 컨테이너 목록을 가져옴
containers=$(docker ps -qa)

# 컨테이너가 존재하면 삭제
#if [ -n "$containers" ]; then
#  sudo docker rm -f $containers
#else
#  echo "No containers to remove."
#fi
