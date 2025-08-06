# Finance Tracker API - Complete Authentication Implementation

## Overview
This API now includes complete JWT-based authentication and authorization with both manually implemented and OpenAPI-generated endpoints. Every transaction is linked to the authenticated user who created it.

## Implementation Approach

### Generated vs Manual Implementation

**OpenAPI Generated (Recommended):**
- All authentication endpoints are now defined in `openapi.yml`
- Controllers implement the generated interfaces (`AuthenticationApi`, `UserApi`, `TransactionsApi`)
- Provides consistent API documentation and client generation
- Type-safe interfaces with proper OpenAPI annotations

**Why Some Controllers Were Manual Initially:**
- Authentication logic is framework-specific (Spring Security + JWT)
- DTOs and business logic remain custom for flexibility
- Controllers act as adapters between generated interfaces and business services

## Authentication Endpoints

### Register a New User
```
POST /auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "john_doe",
  "email": "john@example.com"
}
```

### Login
```
POST /auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "john_doe",
  "email": "john@example.com"
}
```

## User Management

### Get User Profile
```
GET /user/profile
Authorization: Bearer {your_jwt_token}
```

**Response:**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER",
  "createdAt": "2024-01-01T10:00:00Z"
}
```

## Transaction Endpoints (Protected)

All transaction endpoints now require authentication via JWT token in the Authorization header.

### Get All Transactions (User-specific)
```
GET /transactions
Authorization: Bearer {your_jwt_token}
```

Returns only transactions created by the authenticated user.

### Get Transaction by ID (User-specific)
```
GET /transactions/{id}
Authorization: Bearer {your_jwt_token}
```

Returns the transaction only if it was created by the authenticated user.

### Create Transaction
```
POST /transactions
Authorization: Bearer {your_jwt_token}
Content-Type: application/json

{
  "amount": 100.50,
  "type": "INCOME",
  "category": "Salary",
  "description": "Monthly salary"
}
```

The transaction will be automatically linked to the authenticated user.

### Update Transaction (User-specific)
```
PUT /transactions/{id}
Authorization: Bearer {your_jwt_token}
Content-Type: application/json

{
  "amount": 150.00,
  "type": "INCOME",
  "category": "Bonus",
  "description": "Performance bonus"
}
```

Only the user who created the transaction can update it.

### Delete Transaction (User-specific)
```
DELETE /transactions/{id}
Authorization: Bearer {your_jwt_token}
```

Only the user who created the transaction can delete it.

## Architecture

### Code Generation
```
OpenAPI Spec (openapi.yml)
    ↓ (Maven OpenAPI Generator Plugin)
Generated Interfaces:
- AuthenticationApi
- UserApi  
- TransactionsApi
- Model classes (AuthResponse, LoginRequest, etc.)
    ↓ (Implemented by)
Controllers:
- AuthController implements AuthenticationApi
- UserController implements UserApi
- TransactionController implements TransactionsApi
    ↓ (Uses)
Services:
- AuthService (custom business logic)
- TransactionService (custom business logic)
    ↓ (Persists via)
Repositories & Entities
```

### Security Layer
```
HTTP Request
    ↓
JwtAuthenticationFilter (validates JWT)
    ↓
SecurityConfig (authorization rules)
    ↓
Controller (API interface implementation)
    ↓
Service (business logic + user context)
    ↓
Repository (data access with user filtering)
```

## Security Features

1. **JWT Token Authentication**: All protected endpoints require a valid JWT token
2. **User Isolation**: Users can only access their own transactions
3. **Password Encryption**: User passwords are encrypted using BCrypt
4. **Token Expiration**: JWT tokens expire after 24 hours (configurable)
5. **Authorization**: Each transaction operation checks if the user owns the transaction
6. **OpenAPI Security Schemes**: Properly documented in API specification

## Generated API Documentation

The application now automatically generates:
- **Swagger UI**: Available at `/swagger-ui.html` when running
- **OpenAPI JSON**: Available at `/v3/api-docs`
- **Type-safe client SDKs**: Can be generated for any language

## Configuration

The following configuration properties control JWT behavior:

```properties
# JWT secret key (should be at least 32 characters)
app.jwt.secret=myVerySecureJwtSecretKeyThatIsAtLeast32CharactersLong

# JWT token expiration time in seconds (default: 24 hours)
app.jwt.expiration=86400
```

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    enabled BOOLEAN DEFAULT TRUE,
    account_non_expired BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE
);
```

### Updated Transactions Table
The transactions table now includes a `user_id` foreign key:
```sql
ALTER TABLE transactions ADD COLUMN user_id UUID NOT NULL;
ALTER TABLE transactions ADD CONSTRAINT fk_transactions_user 
    FOREIGN KEY (user_id) REFERENCES users(id);
```

## Benefits of the OpenAPI Approach

1. **API-First Development**: API contract is defined before implementation
2. **Automatic Documentation**: Swagger UI generated automatically
3. **Type Safety**: Generated interfaces ensure consistency
4. **Client Generation**: Can generate client SDKs for frontend/mobile apps
5. **Contract Testing**: API spec can be used for contract testing
6. **Team Collaboration**: Frontend and backend teams can work in parallel

## Testing the API

1. **Register a new user** using `/auth/register`
2. **Copy the JWT token** from the response
3. **Use the token** in the `Authorization: Bearer {token}` header for all subsequent API calls
4. **Create, read, update, and delete transactions** - they will be automatically linked to your user account
5. **Access Swagger UI** at `http://localhost:8080/swagger-ui.html` for interactive testing

Each user will only see and can only modify their own transactions, providing complete data isolation and security.
