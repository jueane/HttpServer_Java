docker stop httpserver_java

@echo  -----------------------
docker rm httpserver_java

@echo -----------------------
docker build -t httpjava .

@echo -----------------------
docker run -d --name httpserver_java -p 7777:7777 httpjava


