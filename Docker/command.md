```bash
docker run --name bilipic -d -p 8088:8088 -v /home/dockerdata//bilipic/bilipic.log:/bilipic.log -v /etc/localtime:/etc/localtime:ro sprbt/bilipic
```

```dockerfile
FROM java:8-alpine
ADD bilibilipicture-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8088
ENTRYPOINT ["java","-jar","-Duser.timezone=GMT+8","/app.jar"]
```

