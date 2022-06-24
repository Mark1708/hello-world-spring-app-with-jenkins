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