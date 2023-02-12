#!/bin/bash

PROJECT_ROOT="/home/ubuntu/app/deploy"
JAR_NAME="$PROJECT_ROOT/relayRun-0.0.1-SNAPSHOT.jar"

APP_LOG="$PROJECT_ROOT/application.log"
ERROR_LOG="$PROJECT_ROOT/error.log"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

echo "> build 파일명 : $JAR_NAME" >> $DEPLOY_LOG

echo "> 현재 실행중인 애플리케이션 pid 확인" >> $DEPLOY_LOG
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다." >> $DEPLOY_LOG
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

# jar 실행
echo "> DEPLOY_JAR 배포" >> $DEPLOY_LOG
nohup java -jar $JAR_NAME > $APP_LOG 2> $ERROR_LOG &