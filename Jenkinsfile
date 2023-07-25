pipeline {
    agent any

    environment {
        APPLICATION_YAML = credentials('APPLICATION')
        APPLICATION_SECURITY_YAML = credentials('APPLICATION_SECURITY')
        APPLICATION_MAIL_YAML = credentials('APPLICATION_MAIL')
        DOCKER_HUB_USERNAME = credentials('DOCKER_HUB_USERNAME')
        DOCKER_HUB_ACCESS_TOKEN = credentials('DOCKER_HUB_ACCESS_TOKEN')
        AMD64_DOCKER_IMAGE_TAG = credentials('AMD64_DOCKER_IMAGE_TAG')
        DOCKER_IMAGE_TAG = credentials('DOCKER_IMAGE_TAG')
    }

    stages {
        stage('Checkout code') {
            steps {
                // This checks out the source from the GitHub repository
                checkout scm 
            }
        }

        stage('Log in to Docker Hub') {
            steps {
                sh 'echo $DOCKER_HUB_ACCESS_TOKEN | docker login -u $DOCKER_HUB_USERNAME --password-stdin'
            }
        }

        stage('Cleanup Storage') {
            steps {
                sh 'docker system prune -f -a --volumes'
            }
        }

        stage('Generate required YAML files') {
            steps {
                sh 'cat $APPLICATION_YAML > ./assignment-2/src/main/resources/application.yml'
                sh 'cat $APPLICATION_SECURITY_YAML > ./assignment-2/src/main/resources/application-security.yml'
                sh 'cat $APPLICATION_MAIL_YAML > ./assignment-2/src/main/resources/application-mail.yml'
            }
        }

        stage('Build Docker image for AMD64') {
            steps {
                sh 'docker build -t $AMD64_DOCKER_IMAGE_TAG ./assignment-2'
            }
        }

        stage('Push Backend Docker image for AMD64') {
            steps {
                sh 'docker push $AMD64_DOCKER_IMAGE_TAG'
            }
        }

        stage('Manifest Image Tag') {
            steps {
                sh 'docker manifest create $DOCKER_IMAGE_TAG --amend $AMD64_DOCKER_IMAGE_TAG'
                sh 'docker manifest push -p $DOCKER_IMAGE_TAG'
            }
        }

        stage('Apply Deployments') {
            steps {
                sh 'kubectl apply -f ./kubernetes/lb-api.yml'
                sh 'kubectl apply -f ./kubernetes/lb-mysql.yml'
            }
        }

        stage('Apply Services') {
            steps {
                sh 'kubectl apply -f ./kubernetes/lb-api-service.yml'
                sh 'kubectl apply -f ./kubernetes/lb-mysql-service.yml'
            }
        }

        stage('Rollout deployment') {
            steps {
                sh 'kubectl rollout restart deployment lb-api-deployment'
            }
        }
    }
}
