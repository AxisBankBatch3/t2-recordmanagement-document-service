FROM openjdk:11
ADD target/document.jar document.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","document.jar"]