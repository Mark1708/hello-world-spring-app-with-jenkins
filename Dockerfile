FROM openjdk:11-jre-slim
LABEL maintainer="markstav"
EXPOSE 8085
COPY ./target/jenkins-ci-cd-project.jar jenkins-ci-cd-project.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar ./jenkins-ci-cd-project.jar"]
