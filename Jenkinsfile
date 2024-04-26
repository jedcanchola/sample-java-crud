pipeline {
    agent any

	tools {
  		jdk 'OracleJDK17'
  		maven 'MAVEN3'
	}

	environment {
		SCANNER_HOME= tool 'sonar-scanner'
	}

    stages {
        stage('Git Checkout') {
            steps {
                git branch: 'main', changelog: false, poll: false, url: 'https://github.com/jedcanchola/sample-java-crud.git'
            }
        }
        stage('Compile') {
          	steps {
				sh 'mvn clean compile'
          	}
        }
		stage('Sonarqube Analysis'){
			steps {
				sh '''
					$SCANNER_HOME/bin/sonar-scanner -Dsonar.url=http://192.168.121.121:9000/ -Dsonar.login=${SONAR_TOKEN} -Dsonnar.projectName=shopping \
					-Dsonar.java.binaries=. \
					-Dsonar.projectKey=shopping
				'''
			}
		}
    }
}
