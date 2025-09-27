# Account Service

A Spring Boot microservice for managing bank accounts in an online banking portal microservices architecture.

## Features

- **Account Management**: Create, read, update, and delete bank accounts
- **Balance Management**: Add, subtract, and update account balances
- **Microservices Integration**: Provides APIs for other services to manage account balances
- **Security**: OAuth2 JWT-based authentication and authorization
- **Database**: MySQL with JPA/Hibernate for data persistence
- **REST APIs**: Comprehensive REST endpoints for account operations

## Technology Stack

- Spring Boot 3.3.4
- Spring Data JPA
- Spring Security with OAuth2 Resource Server
- MySQL Database
- Lombok
- Maven

## Database Schema

### Account Entity
- `id`: Primary key
- `account_number`: Unique account number
- `account_holder_name`: Name of account holder
- `balance`: Current account balance
- `account_type`: SAVINGS, CHECKING, BUSINESS, CREDIT
- `status`: ACTIVE, INACTIVE, SUSPENDED, CLOSED
- `user_id`: Reference to user from auth-service
- `created_at`, `updated_at`: Timestamps

## Microservices Architecture

This service is part of a microservices architecture with the following services:

- **auth-service** (Port 8080): Authentication and authorization
- **account-service** (Port 8081): Account management and balance operations
- **transaction-service** (Port 8082): Transaction processing and history

### Service Communication

The account-service provides APIs that can be called by other microservices:

- **Balance Management APIs**: For transaction-service to update account balances
- **Account Validation APIs**: For other services to validate account existence
- **Balance Query APIs**: For other services to check account balances

## API Endpoints

### Account Management

#### Create Account
```
POST /api/accounts
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

{
  "accountHolderName": "John Doe",
  "accountType": "SAVINGS",
  "balance": 1000.00
}
```

#### Get Account by ID
```
GET /api/accounts/{id}
Authorization: Bearer <JWT_TOKEN>
```

#### Get Account by Account Number
```
GET /api/accounts/number/{accountNumber}
Authorization: Bearer <JWT_TOKEN>
```

#### Get User Accounts
```
GET /api/accounts
Authorization: Bearer <JWT_TOKEN>
```

#### Get Active User Accounts
```
GET /api/accounts/active
Authorization: Bearer <JWT_TOKEN>
```

#### Update Account
```
PUT /api/accounts/{id}
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

{
  "accountHolderName": "John Smith",
  "accountType": "CHECKING"
}
```

#### Update Account Status
```
PUT /api/accounts/{id}/status?status=INACTIVE
Authorization: Bearer <JWT_TOKEN>
```

#### Delete Account
```
DELETE /api/accounts/{id}
Authorization: Bearer <JWT_TOKEN>
```

#### Get Account Count
```
GET /api/accounts/count
Authorization: Bearer <JWT_TOKEN>
```

#### Update Account Balance
```
PUT /api/accounts/{id}/balance?balance=1500.00
Authorization: Bearer <JWT_TOKEN>
```

#### Add to Account Balance
```
PUT /api/accounts/{id}/add-balance?amount=500.00
Authorization: Bearer <JWT_TOKEN>
```

#### Subtract from Account Balance
```
PUT /api/accounts/{id}/subtract-balance?amount=100.00
Authorization: Bearer <JWT_TOKEN>
```

### Microservices Integration APIs

These APIs are designed to be called by other microservices (like transaction-service):

#### Add to Balance (for other services)
```
POST /api/account-balance/{accountId}/add?amount=500.00
```

#### Subtract from Balance (for other services)
```
POST /api/account-balance/{accountId}/subtract?amount=100.00
```

#### Get Account Balance (for other services)
```
GET /api/account-balance/{accountId}/balance
```

#### Check Sufficient Balance (for other services)
```
GET /api/account-balance/{accountId}/sufficient?amount=100.00
```

#### Update Account Balance (for other services)
```
PUT /api/account-balance/{accountId}/balance?newBalance=1500.00
```

## Configuration

### Application Properties
```properties
spring.application.name=account-service
spring.datasource.url=jdbc:mysql://localhost:3306/account_db
spring.datasource.username=root
spring.datasource.password=1234
server.port=8081

# OAuth2 Configuration
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/auth/realms/banking
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8080/auth/realms/banking/protocol/openid-connect/certs
```

## Security

- All endpoints require valid JWT token
- User can only access their own accounts and transactions
- OAuth2 Resource Server configuration for JWT validation
- CORS enabled for cross-origin requests

## Running the Application

1. Ensure MySQL is running with `account_db` database created
2. Update database credentials in `application.properties`
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
4. The service will start on port 8081

## Dependencies

- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter OAuth2 Resource Server
- Spring Boot Starter Validation
- MySQL Connector
- Lombok

## Database Setup

Create the MySQL database:
```sql
CREATE DATABASE account_db;
```

The application will automatically create the required tables on startup.

## Authentication

This service expects JWT tokens from the auth-service. Make sure the auth-service is running and properly configured to issue JWT tokens that this service can validate.
