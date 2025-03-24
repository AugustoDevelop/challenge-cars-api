pipeline {
    agent any

    environment {
        MVN_HOME = tool 'Maven-3.9'
        JAVA_HOME = tool 'JDK-17'
        SONAR_SERVER = credentials('sonarqube-url') // URL do SonarQube
        SONAR_TOKEN = credentials('sonarqube-token') // Token do SonarQube
        HEROKU_API_KEY = credentials('heroku-api-key') // Credencial do Heroku
        PATH = "${JAVA_HOME}/bin:${MVN_HOME}/bin:${PATH}"
    }

    stages {
        stage('Detect Environment') {
            steps {
                script {
                    if (env.BRANCH_NAME.contains('develop')) {
                        env.HEROKU_APP = 'challenge-cars-api-developer'
                        env.ENVIRONMENT = 'develop'
                        env.SPRING_PROFILE = 'dev'
                    } else if (env.BRANCH_NAME.contains('staging')) {
                        env.HEROKU_APP = 'challenge-cars-api-staging'
                        env.ENVIRONMENT = 'staging'
                        env.SPRING_PROFILE = 'stag'
                    } else if (env.BRANCH_NAME.contains('production')) {
                        env.HEROKU_APP = 'challenge-cars-api-production'
                        env.ENVIRONMENT = 'production'
                        env.SPRING_PROFILE = 'prod'
                    } else {
                        error "Branch não configurada para deploy: ${env.BRANCH_NAME}"
                    }
                    echo "Deploying to ${env.ENVIRONMENT} environment (${env.HEROKU_APP})"
                }
            }
        }

        stage('Checkout code') {
            steps {
                checkout scm
                sh 'ls -l'
            }
        }

        stage('Code Quality Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh """
                    ./mvnw sonar:sonar \
                        -Dsonar.host.url=$SONAR_SERVER \
                        -Dsonar.login=$SONAR_TOKEN
                    """
                }
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Compile and Unit Tests') {
            steps {
                sh 'mvn clean compile -B'
                sh 'mvn test -B'
                junit '**/target/surefire-reports/*.xml'
                jacoco(
                    execPattern: '**/target/jacoco.exec',
                    classPattern: '**/target/classes',
                    sourcePattern: '**/src/main/java',
                    exclusionPattern: '**/src/test*'
                )
            }
        }

        stage('Build Package') {
            steps {
                sh 'mvn package -DskipTests -B'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Publish JaCoCo Report') {
            steps {
                script {
                    // Verifica se o diretório do relatório existe antes de publicá-lo
                    def reportDir = 'target/site/jacoco'
                    if (fileExists(reportDir)) {
                        publishHTML([
                            reportName: 'Code Coverage Report',
                            reportDir: reportDir,
                            reportFiles: 'index.html'
                        ])
                    } else {
                        echo "JaCoCo report directory not found: ${reportDir}"
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    app = docker.build("${env.HEROKU_APP}", "--build-arg SPRING_PROFILE=${env.SPRING_PROFILE} .")
                }
            }
        }

        stage('Integration Tests') {
            steps {
                script {
                    sh 'mvn verify -P integration-test -DskipUnitTests -B'
                    junit '**/target/failsafe-reports/*.xml'
                }
            }
        }

        stage('Push to Heroku') {
            steps {
                script {
                    echo "Deploying Docker image to Heroku (${env.HEROKU_APP})"

                    // Login no registro do Heroku
                    sh "docker login --username=_ --password=$HEROKU_API_KEY registry.heroku.com"

                    // Tag da imagem para o formato do Heroku
                    sh "docker tag ${env.HEROKU_APP} registry.heroku.com/${env.HEROKU_APP}/web"

                    // Push da imagem para o Heroku
                    sh "docker push registry.heroku.com/${env.HEROKU_APP}/web"

                    // Liberar a imagem (deploy)
                    sh "heroku container:release web --app ${env.HEROKU_APP}"
                }
            }
        }

        stage('Smoke Test') {
            steps {
                script {
                    echo "Running Smoke Test for ${env.HEROKU_APP}"
                    retry(3) {
                        sh """
                            sleep 30
                            curl -s --retry 5 --retry-delay 10 https://${env.HEROKU_APP}.herokuapp.com/actuator/health | grep UP
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            publishHTML(target: [
                allowMissing: false,
                alwaysLinkToLastBuild: false,
                keepAll: true,
                reportDir: 'target/site/jacoco',
                reportFiles: 'index.html',
                reportName: 'Code Coverage Report'
            ])
            sh "docker system prune -f"
            deleteDir()
        }
        success {
            echo "Deployment to ${env.ENVIRONMENT} successful!"
        }
        failure {
            echo "Deployment to ${env.ENVIRONMENT} failed!"
        }
    }
}
