FROM alpine/java:21-jdk
COPY ../target/yandex-praktikum-kafka-2-0.0.1-SNAPSHOT.jar yandex-praktikum-kafka-2-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/yandex-praktikum-kafka-2-0.0.1-SNAPSHOT.jar"]