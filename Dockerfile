FROM openjdk:11

#work dir
WORKDIR /app/java-server

#add server dir to workdir  args are (src dist) respectivly
ADD /target/youtube-api-docker.jar .
EXPOSE 8080
##
ENTRYPOINT ["/bin/bash","-c"]
CMD ["java","-jar","./youtube-api-docker.jar"]
