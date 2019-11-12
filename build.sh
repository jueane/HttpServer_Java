docker stop httpserver_java || true

docker rm httpserver_java	|| true

docker build -t httpjavajm .

docker run -d --name httpserver_java -p 7777:7777 -v /root/webapps/mysite_symbol:/myapp/webroot httpjavajm


