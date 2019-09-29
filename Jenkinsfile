pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Building..'
				mkdir /root/a
				mkdir /root/b
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}
