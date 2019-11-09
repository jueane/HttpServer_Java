JENKINS_NODE_COOKIE=dontKillMe

echo ok
nohup java -jar target/HttpServer-1.0-SNAPSHOT.jar &>out.txt
