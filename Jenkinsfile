pipeline {
    agent any

    environment {
        HEROKU_API_KEY = credentials('HRKU-0fc202e7-bbfe-4103-a9b6-3e3ceff0548e')
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
                    if (env.GIT_BRANCH == 'origin/develop') {
                        env.HEROKU_APP = 'challenge-cars-api-developer'
                        env.ENVIRONMENT = 'develop'
                        env.SPRING_PROFILE = 'dev'
                    } else if (env.GIT_BRANCH == 'origin/staging') {
                        env.HEROKU_APP = 'challenge-cars-api-staging'
                        env.ENVIRONMENT = 'staging'
                        env.SPRING_PROFILE = 'stag'
                    } else if (env.GIT_BRANCH == 'origin/production') {
                        env.HEROKU_APP = 'challenge-cars-api-production'
                        env.ENVIRONMENT = 'production'
                        env.SPRING_PROFILE = 'prod'
                    } else {
                        error "Branch não configurada para deploy: ${env.GIT_BRANCH}"
                    }
                    echo "Deploying to ${env.ENVIRONMENT} environment (${env.HEROKU_APP})"
                }
            }
        }

        stage('Checkout code') {
            steps {
                checkout scm
            }
        }

        stage('Code Quality Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh """
                    ./mvnw sonar:sonar \
                        -Dsonar.host.url=${SONAR_SERVER} \
                        -Dsonar.login=${SONAR_TOKEN}
                    """
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

        stage('Static Code Analysis') {
            steps {
                // Análise SonarQube
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar -B'
                }
                // Aguardar resultado da análise do SonarQube
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build Package') {
            steps {
                sh 'mvn package -DskipTests -B'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
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
                    // Login no registro do Heroku
                    sh "docker login --username=_ --password=${HEROKU_API_KEY} registry.heroku.com"

                    // Tag da imagem para o formato do Heroku
                    sh "docker tag ${env.HEROKU_APP} registry.heroku.com/${env.HEROKU_APP}/web"

                    // Push da imagem para o Heroku
                    sh "docker push registry.heroku.com/${env.HEROKU_APP}/web"

                    // Liberar a imagem (equivalente ao deploy)
                    sh "heroku container:release web --app ${env.HEROKU_APP}"
                }
            }
        }

        stage('Smoke Test') {
            steps {
                script {
                    // Verificar se a aplicação subiu corretamente
                    sh """
                        sleep 30
                        curl -s --retry 5 --retry-delay 10 https://${env.HEROKU_APP}.herokuapp.com/actuator/health | grep UP
                    """
                }
            }
        }
    }

    post {
        always {
            // Publicar relatórios de teste
            publishHTML(target: [
                allowMissing: false,
                alwaysLinkToLastBuild: false,
                keepAll: true,
                reportDir: 'target/site/jacoco',
                reportFiles: 'index.html',
                reportName: 'Code Coverage Report'
            ])

            // Limpeza
            sh "docker system prune -f"
            deleteDir()
        }
        success {
            // Notificação de sucesso
            echo "Deployment to ${env.ENVIRONMENT} successful!"
            // Exemplo de notificação Slack
            // slackSend channel: '#deployments', color: 'good', message: "Deployment to ${env.ENVIRONMENT} successful!"
        }
        failure {
            // Notificação de falha
            echo "Deployment to ${env.ENVIRONMENT} failed!"
            // slackSend channel: '#deployments', color: 'danger', message: "Deployment to ${env.ENVIRONMENT} failed!"
        }
    }
}