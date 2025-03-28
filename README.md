# RESTful API for Users and Cars

RESTful API for managing users and cars, with JWT authentication, developed in Java 8 and Spring Boot.

## Index

- [User Stories](#user-stories)
- [Solution](#solution)
- [Requirements](#requirements)
- [How to Run](#how-to-run)
- [API Endpoints](#api-endpoints)
- [Running Tests](#running-tests)
- [API Documentation](#api-documentation)
- [Deployment](#deployment)

## User Stories

1. **US-01: Integration with SonarQube for Code Analysis** As a developer, I want integration with SonarQube to ensure
   code quality and identify potential vulnerabilities. (✅)
2. **US-02: Integration with JFrog Artifactory for Artifact Management** As a software architect, I need an artifact
   repository to manage project dependencies and builds. (✅)
3. **US-03: CI/CD Pipeline Configuration in Jenkins** As a DevOps team, we want to automate the continuous integration
   and delivery process using Jenkins to ensure consistent builds and automated deployment. (⚠️)
4. **US-04 - Creation of Controller Advice**: As a user, I want to receive the corresponding errors for each exception
   thrown. (✅)
5. **US-05 - User Registration**: As a user, I want to register in the system by providing my personal data so that I
   can access the functionalities. (✅)
6. **US-06 - User Query**: As a user, I want to view the list of all users registered in the system. (✅)
7. **US-07 - User Query by ID**: As a user, I want to query the data of a specific user by providing their ID. (✅)
8. **US-08 - User Update**: As a registered user, I want to update my registration data in the system. (✅)
9. **US-09 - User Deletion**: As a registered user, I want to remove my account from the system. (✅)
10. **US-10 - User Login**: As a registered user, I want to log in to the system using my credentials to access
    authenticated functionalities. (✅)
11. **US-11 - Query Logged-in User Information**: As a logged-in user, I want to view my personal information, creation
    date, and last login. (⚠️)
12. **US-12 - Car Registration**: As a logged-in user, I want to register a new car in my account. (✅)
13. **US-13 - Query User's Cars**: As a logged-in user, I want to view all cars registered in my account. (✅)
14. **US-14 - Query Car by ID**: As a logged-in user, I want to query the data of a specific car registered in my
    account. (✅)
15. **US-15 - Car Update**: As a logged-in user, I want to update the data of a car registered in my account. (✅)
16. **US-16 - Car Deletion**: As a logged-in user, I want to remove a car registered in my account. (✅)
17. **US-17 - User and Car Ranking**: As a developer, I want to implement a ranking system that orders users and cars by
    usage frequency. (✅)
18. **US-18 - Documentation (Javadoc)**: As a developer, I want to generate and maintain Javadoc documentation to
    facilitate code understanding and maintenance. (✅)

## Solution

### Architecture

The project was developed using a layered architecture following REST principles and best development practices:

1. **Presentation Layer**: REST Controllers exposing the API endpoints.
2. **Service Layer**: Business logic and application rules.
3. **Persistence Layer**: JPA repositories for database access.
4. **Model Layer**: JPA entities and DTOs for data transfer.

### Technical Justification

#### Spring Boot

We used Spring Boot as the main framework due to its ease of configuration, robustness, and wide adoption in the
community. Spring Boot allowed us to focus on developing business functionalities, avoiding complex configurations.

#### H2 Database

The in-memory H2 database was chosen to simplify development and testing, as well as to meet the challenge requirement.
Being an in-memory database, it does not require additional installation and facilitates deployment.

#### Security with JWT

We implemented authentication and authorization using JWT (JSON Web Token), ensuring that protected routes can only be
accessed by authenticated users. The JWT token securely and efficiently carries user information.

#### Data Validation

We used Bean Validation for input data validation, ensuring data integrity and consistency. This allowed us to create
standardized error messages as requested.

#### Centralized Exception Handling

We implemented global exception handling with `@ControllerAdvice`, standardizing the API error responses according to
the requested format.

#### Scalability

The chosen architecture allows horizontal scaling of the application by adding more instances as needed. Additionally,
the separation into layers facilitates code maintenance and evolution.

#### User and Car Ranking

For the extra requirement, we implemented a car usage counting system that increments a counter each time a car is
queried. We used JPQL queries to order users and cars according to the specified criteria.

### Design Patterns Used

1. **MVC (Model-View-Controller)**: Clear separation between model, view, and control.
2. **DTO (Data Transfer Object)**: For data transfer between application layers.
3. **Repository Pattern**: Abstraction of the data access layer.
4. **Dependency Injection**: Dependency injection for loose coupling between components.
5. **Builder Pattern**: Used in tests for building complex objects.
6. **Filter Chain**: For processing HTTP requests and JWT authentication.

## Requirements

Make sure the following items are installed and configured on your system:
- Git
- Java 17
- Maven 3.6+
- Docker (v20.10 or higher)
- Ensure that ports 8080 and 50000 are free on your system for Jenkins.

### Compiling and Running with Maven

```bash
  mvn clean install
  mvn spring-boot:run
```

### Running with the JAR

```bash
  java -jar target/car-user-api-0.0.1-SNAPSHOT.jar
```

### Accessing the H2 Console

The H2 database console will be available at http://ec2-52-73-230-3.compute-1.amazonaws.com:8080/h2 with the following
settings:

- JDBC URL: `jdbc:h2:mem:cardb`
- Username: `sa`
- Password: (vazio)

## API Endpoints

### Public Routes (do not require authentication) - User Endpoints

- **POST** `/api/v1/users`: Register a new user.
- **GET** `/api/v1/users`: Get all users.
- **GET** `/api/v1/users/{id}`: Get user by ID.
- **PUT** `/api/v1/users/{id}`: Update user by ID.
- **DELETE** `/api/v1/users/{id}`: Delete user by ID.
- **POST** `/api/v1/users/login`: User login.
- **GET** `/api/v1/users/me`: Get logged-in user information.

### Authenticated Routes (require JWT token) - Car Endpoints

- **POST** `/api/v1/cars`: Register a new car.
- **GET** `/api/v1/cars`: Get all cars.
- **GET** `/api/v1/cars/{id}`: Get car by ID.
- **PUT** `/api/v1/cars/{id}`: Update car by ID.
- **DELETE** `/api/v1/cars/{id}`: Delete car by ID.
- **GET** `/api/v1/cars/ranking`: Get car ranking by usage frequency.

### Usage Examples

#### User Registration

```bash
  curl -X POST http://ec2-52-73-230-3.compute-1.amazonaws.com:8080/api/user \
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

### User Login

```bash
  curl -X POST http://ec2-52-73-230-3.compute-1.amazonaws.com:8080/api/signin \
-H "Content-Type: application/json" \
-d '{
    "login": "hello.world",
    "password": "h3ll0"
}'
```

### Query Logged-in User Information

```bash
  curl -X GET http://ec2-52-73-230-3.compute-1.amazonaws.com:8080/api/me \
-H "Authorization: Bearer {token-jwt}"
```

## Running Tests

To run unit tests

```bash
  mvn test
```

To run test coverage report:

```bash
  mvn verify
```

The coverage report will be available at `target/site/jacoco/index.html`

## API Documentation

The API documentation is available at the following URL:

```
   ec2-52-73-230-3.compute-1.amazonaws.com/swagger-ui/index.html
```

## Deployment

### Automatic Deployment via GitHub

You can also set up automatic deployment to AWS via GitHub by creating a pull request to the main branch. This can be
achieved using GitHub Actions. Here is an example of a GitHub Actions workflow `(.github/workflows/deploy.yml)`:

This workflow will automatically build, tag, and push the Docker image to AWS ECR and then update the ECS service to
deploy the new image whenever there is a push to the main branch.