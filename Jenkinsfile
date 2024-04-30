pipeline {
    agent any

	tools {
		  maven 'MAVEN3'
  		jdk 'OracleJDK11'
  		
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
				echo $SCANNER_HOME
				// sh '''
				// 	$SCANNER_HOME/bin/sonar-scanner -X -Dsonar.url=http://192.168.121.121:9000/ -Dsonar.login=${SONAR_TOKEN} -Dsonnar.projectName=shopping \
				// 	-Dsonar.java.binaries=. \
				// 	-Dsonar.projectKey=shopping
				// '''
			}
		}
    }
}
