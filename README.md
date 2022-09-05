# hello-world-spring-app-with-jenkins
![Jenkins](https://img.shields.io/badge/-jenkins-0a0a0a?style=for-the-badge&logo=jenkins)
![Spring Boot](https://img.shields.io/badge/-springboot-0a0a0a?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/-postgresql-0a0a0a?style=for-the-badge&logo=postgresql)
![Flyway](https://img.shields.io/badge/-flyway-0a0a0a?style=for-the-badge&logo=flyway)
![Docker](https://img.shields.io/badge/-Docker-0a0a0a?style=for-the-badge&logo=Docker)
<br/>
> Памятка по подключению Jenkins

## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Status](#status)
* [Inspiration](#inspiration)
* [Contact](#contact)

## General info

# 1. Установка
> brew install jenkins-lts

# 2. Запуск jenkins
> brew services start jenkins-lts

# 3. Вводим пароль админа из конфигурации
### При первом старте проекта потребуется ключ initialAdmin
> cat /Users/markguranov/.jenkins/secrets/initialAdminPassword

# 4. Устанавливаем утилиты
#### Выбираем _"Install suggested plugins"_

# 5. Настройка Глобальной конфигурации Jenkins
### Настройки Jenkins -> Конфигурация глобальных инструментов
<img src="https://github.com/Mark1708/hello-world-spring-app-with-jenkins/blob/master/assets/setup_jdk.png?raw=true" width="900">
<img src="https://github.com/Mark1708/hello-world-spring-app-with-jenkins/blob/master/assets/setup_maven.png?raw=true" width="900">
<img src="https://github.com/Mark1708/hello-world-spring-app-with-jenkins/blob/master/assets/setup_docker.png?raw=true" width="900">

# 6. Создаём Pipeline для Jenkins
<img src="https://github.com/Mark1708/hello-world-spring-app-with-jenkins/blob/master/assets/create_pipeline.png?raw=true" width="900">

# 7. Проводим настройку Pipeline
## 7.1. General
<img src="https://github.com/Mark1708/hello-world-spring-app-with-jenkins/blob/master/assets/pipeline_setup_general.png?raw=true" width="900">

## 7.2. Build Trigger
### Включаем триггер
<img src="https://github.com/Mark1708/hello-world-spring-app-with-jenkins/blob/master/assets/pipeline_setup_trigger.png?raw=true" width="900">

### Стартуем ngrok 
####(Т.к. Webhook нельзя делать на локальный хост)
> ./ngrok http 8080

<img src="https://github.com/Mark1708/hello-world-spring-app-with-jenkins/blob/master/assets/ngrok.png?raw=true" width="900">

### Добавляем Webhook на Github
<img src="https://github.com/Mark1708/hello-world-spring-app-with-jenkins/blob/master/assets/github_webhook.png?raw=true" width="900">

## 7.3. Pipeline
<img src="https://github.com/Mark1708/hello-world-spring-app-with-jenkins/blob/master/assets/pipeline_setup_pipeline.png?raw=true" width="900">

# 8. Создаём Dockerfile-packaged в директории нашего проекта
```
FROM alpine:3.10.3 as packager
RUN apk --no-cache add openjdk11-jdk openjdk11-jmods
ENV JAVA_MINIMAL="/opt/java-minimal"
# build minimal JRE
RUN /usr/lib/jvm/java-11-openjdk/bin/jlink \
    --verbose \
    --add-modules \
        java.base,java.sql,java.naming,java.desktop,java.management,java.security.jgss,java.instrument \
    --compress 2 --strip-debug --no-header-files --no-man-pages \
    --release-info="add:IMPLEMENTOR=radistao:IMPLEMENTOR_VERSION=radistao_JRE" \
    --output "$JAVA_MINIMAL"


FROM alpine:3.10.3
LABEL maintainer="Mark Gurianov mark1708.work@gmail.com"
ENV JAVA_HOME=/opt/java-minimal
ENV PATH="$PATH:$JAVA_HOME/bin"
COPY --from=packager "$JAVA_HOME" "$JAVA_HOME"
COPY /target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","/app.jar"]
```

# 9. Создаём Jenkinsfile(Pipeline script) в директории нашего проекта
```
pipeline {
    agent any
    options {
        skipStagesAfterUnstable()
    }
    environment {
        DOCKERHUB_CREDENTIALS = credentials('jenkins.dockerhub')
        IMAGE_BASE = 'markstav/jenkins-ci-cd-project'
        IMAGE_TAG = "v$BUILD_NUMBER"
      	IMAGE_NAME = "${env.IMAGE_BASE}:${env.IMAGE_TAG}"
      	IMAGE_NAME_LATEST = "${env.IMAGE_BASE}:latest"
      	DOCKERFILE_NAME = "Dockerfile-packaged"
    }
    stages {
        stage('CloneGitRepo') {
            agent any
            steps {
                git credentialsId: '32647e76-60a3-42e4-a5f1-a0d6c5d4fa53', url: 'https://github.com/Mark1708/hello-world-spring-app-with-jenkins.git'
            }
        }

        stage('MavenCompile') {
            agent any
            steps {
                sh './mvnw compile'
            }
        }

        stage('MavenTest') {
            agent any
            steps {
                sh './mvnw test'
            }
        }

        stage('MavenPackage') {
            agent any
            steps {
                sh './mvnw package'
            }
        }

        stage('BuildImage') {
            agent any
            steps {
                sh 'docker build -t $IMAGE_NAME -f Dockerfile-packaged .'
                sh 'docker tag $IMAGE_NAME $IMAGE_NAME_LATEST'
            }
        }

        stage('LoginDockerhub') {
            agent any
			steps {
				sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
			}
		}

        stage('PushImage') {
            agent any
            steps {
                sh 'docker push $IMAGE_NAME'
                sh 'docker push $IMAGE_NAME_LATEST'
                sh "docker rmi ${env.IMAGE_NAME} ${env.IMAGE_NAME_LATEST}"
            }
        }

        stage('Deploy') {
            agent any
            steps {
                echo 'Deploy to cloud'
            }
        }

    }

    post {
		always {
			sh 'docker logout'
		}
	}
}
```

### 10. DockerHub && Github подготовка креденшелов
#### DockerHub
<img src="https://github.com/Mark1708/hello-world-spring-app-with-jenkins/blob/master/assets/credential_docker.png?raw=true" width="900">
<img src="https://github.com/Mark1708/hello-world-spring-app-with-jenkins/blob/master/assets/dockerhub_token.png?raw=true" width="900">


#### Github
Отправляемся сюда, чтобы сгенерить безопасный способ аутентификации
> http://localhost:8080/job/JOB_NAME/pipeline-syntax/

<img src="https://github.com/Mark1708/hello-world-spring-app-with-jenkins/blob/master/assets/github_credential.png?raw=true" width="900">


### 11. Проверяем и наслаждаемся🤤
<img src="https://github.com/Mark1708/hello-world-spring-app-with-jenkins/blob/master/assets/demo_pipeline.png?raw=true" width="900">
<img src="https://github.com/Mark1708/hello-world-spring-app-with-jenkins/blob/master/assets/demo_dockerhub.png?raw=true" width="900">

## Technologies
* Java - version 11
* Spring Boot - version 2.7.2
* PostgreSQL
* Flyway
* Docker
* Jenkins

## Status
Проект _закончен_

## Inspiration
Проект сделан в образовательных целях

## Contact
Created by [Gurianov Mark](https://mark1708.github.io/) - feel free to contact me!
#### +7(962)024-50-04 | mark1708.work@gmail.com | [github](http://github.com/Mark1708)

![Readme Card](https://github-readme-stats.vercel.app/api/pin/?username=mark1708&repo=hello-world-spring-app-with-jenkins&theme=chartreuse-dark&show_icons=true)
