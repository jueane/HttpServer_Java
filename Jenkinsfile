pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Building..'
				
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Package') {
            steps {
                echo 'Package..'
				sh 'mvn package'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
        stage('Run') {
            steps {
                echo 'Run....'
				sh 'stop.sh'
				sh 'start.sh'
				
            }
        }
		
    }
}
