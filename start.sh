
OLD_BUILD_ID=$BUILD_ID
echo $OLD_BUILD_ID
BUILD_ID=dontKillMe

#此处放入shell脚本或者shell命令

nohup java -jar target/HttpServer-1.0-SNAPSHOT.jar &>out.txt &


BUILD_ID=$OLD_BUILD_ID
echo $BUILD_ID



