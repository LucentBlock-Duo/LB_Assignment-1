pipeline {
    agent any

    environment {
        APPLICATION_YAML = credentials('APPLICATION')
        APPLICATION_SECURITY_YAML = credentials('APPLICATION_SECURITY')
        DOCKER_HUB_USERNAME = credentials('DOCKER_HUB_USERNAME')
        DOCKER_HUB_ACCESS_TOKEN = credentials('DOCKER_HUB_ACCESS_TOKEN')
        AMD64_DOCKER_IMAGE_TAG = credentials('AMD64_DOCKER_IMAGE_TAG')
        DOCKER_IMAGE_TAG = credentials('DOCKER_IMAGE_TAG')
        KUBECONFIG=credentials('KUBECONFIG')
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
                sh 'kubectl apply -f /var/jenkins_home/workspace/lcb/kubernetes/lcb-was-configmap.yaml'
                sh 'kubectl apply -f /var/jenkins_home/workspace/lcb/kubernetes/lcb-was-deploy.yaml'
                sh 'kubectl apply -f /var/jenkins_home/workspace/lcb/kubernetes/mysql-deploy.yaml'
            }
        }

        stage('Apply Services') {
            steps {
                sh 'kubectl apply -f /var/jenkins_home/workspace/lcb/kubernetes/lcb-was-service.yaml'
                sh 'kubectl apply -f /var/jenkins_home/workspace/lcb/kubernetes/mysql-service.yaml'
            }
        }

        stage('Rollout deployment') {
            steps {
                sh 'kubectl rollout restart deployment lcb-was'
            }
        }
    }
}
