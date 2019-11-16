$projectname=((Get-Location) | Get-Item).Name.ToLower()
$imagename=$projectname
Write-Output $projectname

$fileExist=Test-Path .\target\*.jar

if($fileExist)
{
    Write-Output "----- stop service  ..."
    
    docker stop $projectname

    docker rm $projectname

    docker rmi $imagename

    Write-Output "----- begin building ..."

    docker build -t $imagename .

    docker run -dit --name $projectname -p 7777:7777 $imagename
}else
{
    Write-Output "must package first"
}