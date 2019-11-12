docker stop httpserver_java

docker rm httpserver_java

docker rmi httpjavajm

docker build -t httpjavajm .

docker run -d --name httpserver_java -p 7777:7777 httpjavajm


