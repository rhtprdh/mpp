pipeline {
    agent any

    stages {
        
        stage('Checkout Code') {
            steps {
                git url: 'https://github.com/rhtprdh/mpp.git', branch: 'main'
            }
        }

        stage('Build All Microservices') {
            steps {
                sh 'mvn -pl config-server -am clean install -DskipTests'
                sh 'mvn -pl service-registry -am clean install -DskipTests'
                sh 'mvn -pl api-gateway -am clean install -DskipTests'
                sh 'mvn -pl auth-service -am clean install -DskipTests'
                sh 'mvn -pl product-service -am clean install -DskipTests'
                sh 'mvn -pl inventory-service -am clean install -DskipTests'
                sh 'mvn -pl order-service -am clean install -DskipTests'
                sh 'mvn -pl notification-service -am clean install -DskipTests'
            }
        }

        stage('Build Docker Images') {
            steps {
                sh 'docker build -t config-server:latest ./config-server'
                sh 'docker build -t service-registry:latest ./service-registry'
                sh 'docker build -t api-gateway:latest ./api-gateway'
                sh 'docker build -t auth-service:latest ./auth-service'
                sh 'docker build -t product-service:latest ./product-service'
                sh 'docker build -t inventory-service:latest ./inventory-service'
                sh 'docker build -t order-service:latest ./order-service'
                sh 'docker build -t notification-service:latest ./notification-service'
            }
        }

        stage('Deploy Using Docker Compose') {
            steps {
                sh 'docker-compose down'
                sh 'docker-compose up -d --build'
            }
        }

    }
}
