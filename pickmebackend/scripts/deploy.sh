#! /bin/bash

REPOSITORY=/home/ec2-user/app/pickme-back
PROJECT_NAME=pickmebackend

echo "> 프로젝트 디렉토리로 경로 이동"

cd  $REPOSITORY

echo "> git pull"

git pull

echo "프로젝트 빌드 시작"

cd ./$PROJECT_NAME
./gradlew build

echo "> pickme 디렉토리로 이동"

cd $REPOSITORY

echo "> Build 파일 복사"

cp $REPOSITORY/$PROJECT_NAME/module-web/build/libs/*.jar $REPOSITORY/

echo "> 현재 구동중인 애플리케이션 pid 확인"

CURRENT_PID=$(pgrep -f ${PROJECT_NAME}*.jar)

echo "현재 구동중인 애플리케이션 pid: $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
    echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
    echo "> kill -15 $CURRENT_PID"
    kill -15 $CURRENT_PID
    sleep 5
fi

echo "> 새 애플리케이션 배포"

JAR_NAME=$(ls -tr $REPOSITORY/ | grep *.jar | tail -n 1)

echo "> JAR Name : $JAR_NAME"

nohup java -jar \
         -Dspring.config.location=$REPOSITORY/$PROJECT_NAME/module-web/src/main/resources/application.yml,$REPOSITORY/$PROJECT_NAME/module-api/src/main/resources/application.yml,$REPOSITORY/$PROJECT_NAME/module-common/src/main/resources/application.yml,/home/ec2-user/app/application-real-db.properties \
         $REPOSITORY/$JAR_NAME 2>&1 &
