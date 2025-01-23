pipeline {
	agent any
	environment {
		gradleVersion = 'gradle8'
		gradleHome = "${tool gradleVersion}"
	}


	stages {

		stage('Checkout') {
			steps {

			}
		}


		stage('Build'){

			steps {
				withCredentials([
				usernamePassword(credentialsId: 'NEXUS1', usernameVariable: 'NEXUS1_USERNAME', passwordVariable: 'NEXUS1_PASSWORD')
				]) {
					sh 'chmod +x gradlew'
					sh '''
						export NEXUS1_USERNAME=${NEXUS1_USERNAME}
						export NEXUS1_PASSWORD=${NEXUS1_PASSWORD}
						./gradlew shadowJar --info  --stacktrace
					'''
				}
			}

		}

	}

	post {
		always {
			archiveArtifacts artifacts: '**/build/libs/PlayerTimers-*.jar', allowEmptyArchive: true
		}
	}
}