# API RESTful de Usuários e Carros

API RESTful para gerenciamento de usuários e carros, com autenticação JWT, desenvolvida em Java 8 e Spring Boot.

## Índice

- [Estórias de Usuário](#estórias-de-usuário)
- [Solução](#solução)
- [Requisitos](#requisitos)
- [Como Executar](#como-executar)
- [Endpoints da API](#endpoints-da-api)
- [Executando Testes](#executando-testes)
- [Documentação da API](#documentação-da-api)
- [Deploy](#deploy)

## Estórias de Usuário

1. **US-01: Integração com SonarQube para Análise de Código** Como desenvolvedor, quero integração com SonarQube para garantir a qualidade do código e identificar vulnerabilidades potencial. (✅)
2. **US-02: Integração com JFrog Artifactory para Gerenciamento de Artefatos** Como arquiteto de software, preciso de um repositório de artefatos para gerenciar dependências e builds do projeto. (✅)
3. **US-03: Configuração do Pipeline CI/CD no Jenkins** Como equipe de DevOps, queremos automatizar o processo de integração e entrega contínua usando Jenkins, para garantir builds consistentes e implantação automatizada. (⚠️)
4. **US-04 - Criação do controller Advice**: Como usuário, quero receber os erros correspondentes a cada exceção lançada. (✅)
5. **US-05 - Cadastro de Usuário**: Como usuário, quero me cadastrar no sistema fornecendo meus dados pessoais para que eu possa ter acesso às funcionalidades.(✅)
6. **US-06 - Consulta de Usuários**: Como usuário, quero visualizar a lista de todos os usuários cadastrados no sistema.(✅)
7. **US-07 - Consulta de Usuário por ID**: Como usuário, quero consultar os dados de um usuário específico informando seu ID.(✅)
8. **US-08 - Atualização de Usuário**: Como usuário cadastrado, quero atualizar meus dados cadastrais no sistema.(✅)
9. **US-09 - Remoção de Usuário**: Como usuário cadastrado, quero remover minha conta do sistema.(✅)
10. **US-10 - Login de Usuário**: Como usuário cadastrado, quero fazer login no sistema utilizando minhas credenciais para ter acesso às funcionalidades autenticadas.
11. **US-11 - Consulta de Informações do Usuário Logado**: Como usuário logado, quero visualizar minhas informações pessoais, data de criação e último login.
12. **US-12 - Cadastro de Carro**: Como usuário logado, quero cadastrar um novo carro em minha conta.(✅)
13. **US-13 - Consulta de Carros do Usuário**: Como usuário logado, quero visualizar todos os carros cadastrados em minha conta.(✅)
14. **US-14 - Consulta de Carro por ID**: Como usuário logado, quero consultar os dados de um carro específico cadastrado em minha conta.(✅)
15. **US-15 - Atualização de Carro**: Como usuário logado, quero atualizar os dados de um carro cadastrado em minha conta.(✅)
16. **US-16 - Remoção de Carro**: Como usuário logado, quero remover um carro cadastrado em minha conta.(✅)
17. **US-17 - Ranking de Usuários e Carros**: Como desenvolvedor, quero implementar um sistema de ranking que ordene usuários e carros por frequência de uso.

## Solução

### Arquitetura

O projeto foi desenvolvido utilizando uma arquitetura em camadas seguindo os princípios REST e boas práticas de desenvolvimento:

1. **Camada de Apresentação**: Controllers REST que expõem os endpoints da API.
2. **Camada de Serviço**: Lógica de negócio e regras da aplicação.
3. **Camada de Persistência**: Repositórios JPA para acesso ao banco de dados.
4. **Camada de Modelo**: Entidades JPA e DTOs para transferência de dados.

### Justificativa Técnica

#### Spring Boot

Utilizamos o Spring Boot como framework principal devido à sua facilidade de configuração, robustez e ampla adoção na comunidade. O Spring Boot nos permitiu focar no desenvolvimento das funcionalidades de negócio, evitando configurações complexas.

#### Banco de Dados H2

O banco de dados H2 em memória foi escolhido para simplificar o desenvolvimento e testes, além de atender ao requisito do desafio. Por ser um banco em memória, não requer instalação adicional e facilita o deploy.

#### Segurança com JWT

Implementamos autenticação e autorização utilizando JWT (JSON Web Token), garantindo que as rotas protegidas só possam ser acessadas por usuários autenticados. O token JWT carrega informações do usuário de forma segura e eficiente.

#### Validação de Dados

Utilizamos Bean Validation para validação de dados de entrada, garantindo a integridade e consistência dos dados. Isso nos permitiu criar mensagens de erro padronizadas conforme solicitado.

#### Tratamento de Exceções Centralizado

Implementamos um tratamento global de exceções com `@ControllerAdvice`, padronizando as respostas de erro da API de acordo com o formato solicitado.

#### Escalabilidade

A arquitetura escolhida permite escalar horizontalmente a aplicação, adicionando mais instâncias conforme necessário. Além disso, a separação em camadas facilita a manutenção e evolução do código.

#### Ranking de Usuários e Carros

Para o requisito extra, implementamos um sistema de contagem de uso dos carros que incrementa um contador cada vez que um carro é consultado. Utilizamos queries JPQL para ordenar usuários e carros conforme os critérios especificados.

### Design Patterns Utilizados

1. **MVC (Model-View-Controller)**: Separação clara entre modelo, visualização e controle.
2. **DTO (Data Transfer Object)**: Para transferência de dados entre as camadas da aplicação.
3. **Repository Pattern**: Abstração da camada de acesso a dados.
4. **Dependency Injection**: Injeção de dependências para acoplamento fraco entre componentes.
5. **Builder Pattern**: Utilizado em testes para construção de objetos complexos.
6. **Filter Chain**: Para processamento de requisições HTTP e autenticação JWT.

## Requisitos
Certifique-se de que os seguintes itens estejam instalados e configurados no seu sistema:
- Git
- Java 17
- Maven 3.6+
- Docker (v20.10 ou superior)
- Verifique se as portas 8080 e 50000 estão livres no seu sistema para o Jenkins.

## Como Executar

### Iniciando o Jenkins
No diretório do projeto onde o docker-compose.yml está localizado, execute:

```bash
  docker-compose up -d
```
Após a execução do comando acima, acesse o Jenkins em `http://localhost:8080` e siga as instruções para configurar o Jenkins.

```bash
  docker-compose exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

### Compilando e Executando com Maven

```bash
  mvn clean install
  mvn spring-boot:run
```

### Executando com o JAR

```bash
java -jar target/car-users-api-0.0.1-SNAPSHOT.jar
```

### Acessando o Console H2

O console do banco de dados H2 estará disponível em `http://localhost:8080/h2-console` com as seguintes configurações:

- JDBC URL: `jdbc:h2:mem:cardb`
- Username: `sa`
- Password: (vazio)

## Endpoints da API

### Rotas Públicas (não requerem autenticação)

```
POST /api/signin - Login de usuário
GET /api/users - Listar todos os usuários
POST /api/users - Cadastrar novo usuário
GET /api/users/{id} - Buscar usuário por ID
DELETE /api/users/{id} - Remover usuário por ID
PUT /api/users/{id} - Atualizar usuário por ID
```

### Rotas Autenticadas (requerem token JWT)

```
GET /api/me - Informações do usuário logado
GET /api/cars - Listar carros do usuário logado
POST /api/cars - Cadastrar novo carro
GET /api/cars/{id} - Buscar carro por ID
DELETE /api/cars/{id} - Remover carro por ID
PUT /api/cars/{id} - Atualizar carro por ID
```

### Exemplos de Uso

#### Cadastro de Usuário

```bash
  curl -X POST http://localhost:8080/api/users \
-H "Content-Type: application/json" \
-d '{
    "firstName": "Hello",
    "lastName": "World",
    "email": "hello@world.com",
    "birthday": "1990-05-01",
    "login": "hello.world",
    "password": "h3ll0",
    "phone": "988888888",
    "cars": [
        {
            "year": 2018,
            "licensePlate": "PDV-0625",
            "model": "Audi",
            "color": "White"
        }
    ]
}'
```

#### Login de Usuário

```bash
  curl -X POST http://localhost:8080/api/signin \
-H "Content-Type: application/json" \
-d '{
    "login": "hello.world",
    "password": "h3ll0"
}'
```

#### Consulta de Informações do Usuário Logado

```bash
  curl -X GET http://localhost:8080/api/me \
-H "Authorization: Bearer {token-jwt}"
```

## Executando Testes

Para executar os testes unitários:

```bash
  mvn test
```

Para executar relatório de cobertura de testes:

```bash
  mvn verify
```

O relatório de cobertura estará disponível em `target/site/jacoco/index.html`

## Documentação da API

A documentação da API está disponível através do Swagger UI:

```
http://localhost:8080/swagger-ui/
```

## Deploy

### Heroku

Para fazer deploy no Heroku:

1. Instale o Heroku CLI
2. Faça login no Heroku
   ```bash
   heroku login
   ```
3. Crie um novo aplicativo Heroku
   ```bash
   heroku create car-users-api
   ```
4. Configure o remote do Git
   ```bash
   heroku git:remote -a car-users-api
   ```
5. Faça o push para o Heroku
   ```bash
   git push heroku main
   ```

### Docker

Você também pode executar a aplicação usando Docker:

```bash
docker build -t car-users-api .
docker run -p 8080:8080 car-users-api
```


Acesse os serviços:

Jenkins: http://localhost:8080

SonarQube: http://localhost:9000

JFrog: http://localhost:8081

Aplicação: http://localhost:8082