# Account Service API Testing Guide

## 🚀 Service Overview

The Account Service is a Spring Boot microservice that manages bank accounts in a microservices architecture. It runs on **port 8081** and provides REST APIs for account management and balance operations.

## 🔐 Authentication

This service uses OAuth2 JWT authentication. All API calls (except health checks) require a valid JWT token from the auth-service.

### Getting JWT Token

First, get a JWT token from your auth-service (running on port 8080):

```bash
curl -X POST http://localhost:8080/auth/realms/banking/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password&client_id=banking-client&username=your-username&password=your-password"
```

**Note:** Replace `your-username` and `your-password` with actual credentials from your auth-service.

## 📋 API Endpoints

### 1. Account Management APIs

#### Create Account
```bash
curl -X POST http://localhost:8081/api/accounts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "accountHolderName": "John Doe",
    "accountType": "SAVINGS",
    "balance": 1000.00
  }'
```

**Response:** HTTP 201 with created account details

#### Get All User Accounts
```bash
curl -X GET http://localhost:8081/api/accounts \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:** HTTP 200 with array of user's accounts

#### Get Account by ID
```bash
curl -X GET http://localhost:8081/api/accounts/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:** HTTP 200 with account details or HTTP 404 if not found

#### Get Account by Account Number
```bash
curl -X GET http://localhost:8081/api/accounts/number/123456789012 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:** HTTP 200 with account details or HTTP 404 if not found

#### Get Active Accounts Only
```bash
curl -X GET http://localhost:8081/api/accounts/active \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:** HTTP 200 with array of active accounts

#### Update Account
```bash
curl -X PUT http://localhost:8081/api/accounts/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "accountHolderName": "John Smith",
    "accountType": "CHECKING"
  }'
```

**Response:** HTTP 200 with updated account details

#### Update Account Status
```bash
curl -X PUT "http://localhost:8081/api/accounts/1/status?status=INACTIVE" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:** HTTP 200 with updated account details

#### Delete Account
```bash
curl -X DELETE http://localhost:8081/api/accounts/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:** HTTP 204 (No Content) on success

#### Get Account Count
```bash
curl -X GET http://localhost:8081/api/accounts/count \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:** HTTP 200 with count number

### 2. Balance Management APIs

#### Add to Account Balance
```bash
curl -X PUT "http://localhost:8081/api/accounts/1/add-balance?amount=500.00" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:** HTTP 200 with updated account details

#### Subtract from Account Balance
```bash
curl -X PUT "http://localhost:8081/api/accounts/1/subtract-balance?amount=100.00" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:** HTTP 200 with updated account details or HTTP 400 if insufficient balance

#### Update Account Balance
```bash
curl -X PUT "http://localhost:8081/api/accounts/1/balance?balance=1500.00" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:** HTTP 200 with updated account details

### 3. Microservices Integration APIs

These APIs are designed to be called by other microservices (like transaction-service) and don't require JWT authentication:

#### Add to Balance (for other services)
```bash
curl -X POST "http://localhost:8081/api/account-balance/1/add?amount=200.00"
```

**Response:** HTTP 200 with updated account details

#### Subtract from Balance (for other services)
```bash
curl -X POST "http://localhost:8081/api/account-balance/1/subtract?amount=50.00"
```

**Response:** HTTP 200 with updated account details or HTTP 400 if insufficient balance

#### Get Account Balance (for other services)
```bash
curl -X GET http://localhost:8081/api/account-balance/1/balance
```

**Response:** HTTP 200 with balance amount

#### Check Sufficient Balance (for other services)
```bash
curl -X GET "http://localhost:8081/api/account-balance/1/sufficient?amount=100.00"
```

**Response:** HTTP 200 with boolean value (true/false)

#### Update Account Balance (for other services)
```bash
curl -X PUT "http://localhost:8081/api/account-balance/1/balance?newBalance=2000.00"
```

**Response:** HTTP 200 with updated account details

### 4. Health and Monitoring APIs

#### Service Health Check
```bash
curl -X GET http://localhost:8081/actuator/health
```

**Response:** HTTP 200 with health status

#### Service Info
```bash
curl -X GET http://localhost:8081/actuator/info
```

**Response:** HTTP 200 with service information

## 🧪 Testing Scenarios

### Scenario 1: Complete Account Lifecycle
```bash
# 1. Create account
curl -X POST http://localhost:8081/api/accounts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"accountHolderName": "Test User", "accountType": "SAVINGS", "balance": 1000.00}'

# 2. Get all accounts
curl -X GET http://localhost:8081/api/accounts -H "Authorization: Bearer YOUR_JWT_TOKEN"

# 3. Add to balance
curl -X PUT "http://localhost:8081/api/accounts/1/add-balance?amount=500.00" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# 4. Subtract from balance
curl -X PUT "http://localhost:8081/api/accounts/1/subtract-balance?amount=200.00" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# 5. Update account
curl -X PUT http://localhost:8081/api/accounts/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"accountHolderName": "Updated User", "accountType": "CHECKING"}'

# 6. Get account count
curl -X GET http://localhost:8081/api/accounts/count -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Scenario 2: Microservices Integration
```bash
# 1. Check balance (for transaction-service)
curl -X GET http://localhost:8081/api/account-balance/1/balance

# 2. Check sufficient balance
curl -X GET "http://localhost:8081/api/account-balance/1/sufficient?amount=100.00"

# 3. Add to balance (for deposits)
curl -X POST "http://localhost:8081/api/account-balance/1/add?amount=300.00"

# 4. Subtract from balance (for withdrawals)
curl -X POST "http://localhost:8081/api/account-balance/1/subtract?amount=150.00"
```

## 📊 Expected Responses

### Success Responses
- **HTTP 200**: Successful GET, PUT operations
- **HTTP 201**: Successful POST (create) operations
- **HTTP 204**: Successful DELETE operations

### Error Responses
- **HTTP 400**: Bad Request (validation errors, insufficient balance)
- **HTTP 401**: Unauthorized (missing/invalid JWT token)
- **HTTP 403**: Forbidden (user doesn't own the account)
- **HTTP 404**: Not Found (account doesn't exist)
- **HTTP 500**: Internal Server Error

### Sample Error Response
```json
{
  "error": "Account not found with ID: 999"
}
```

## 🔧 Account Types and Statuses

### Account Types
- `SAVINGS`: Savings account
- `CHECKING`: Checking account
- `BUSINESS`: Business account
- `CREDIT`: Credit account

### Account Statuses
- `ACTIVE`: Active account
- `INACTIVE`: Inactive account
- `SUSPENDED`: Suspended account
- `CLOSED`: Closed account

## 🚀 Quick Start Testing

1. **Start the service**: `mvn spring-boot:run`
2. **Get JWT token** from auth-service
3. **Test health check**: `curl http://localhost:8081/actuator/health`
4. **Create an account** using the create account API
5. **Test balance operations** using the balance management APIs
6. **Test microservices integration** using the account-balance APIs

## 📝 Notes

- All user-specific APIs require JWT authentication
- Microservices integration APIs don't require authentication
- Account numbers are auto-generated as 12-digit numbers
- Balance operations are atomic and thread-safe
- All APIs return JSON responses
- The service runs on port 8081
- Database tables are auto-created on startup

## 🐛 Troubleshooting

### Common Issues
1. **401 Unauthorized**: Check JWT token validity
2. **403 Forbidden**: User doesn't own the account
3. **404 Not Found**: Account doesn't exist
4. **400 Bad Request**: Check request parameters and validation

### Debug Tips
- Check service logs for detailed error messages
- Verify JWT token is not expired
- Ensure account ID exists before operations
- Check balance before subtraction operations
