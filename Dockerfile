FROM alpine/java:21-jdk
VOLUME /tmp
COPY build/libs/rag-kotlin-all*.jar app.jar
ENTRYPOINT ["java", "-jar","/app.jar"]
