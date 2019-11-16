image_name=httpserver_java
container_name=$image_name

docker stop $container_name || true

docker rm $container_name   || true

docker rmi $image_name   || true

docker build -t $image_name .

docker run -dit --name $container_name -p 7777:7777 -v /root/webapps/mysite_symbol:/myapp/webroot $image_name
