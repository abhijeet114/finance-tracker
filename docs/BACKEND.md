# Finance Tracker Backend

Spring Boot REST API for the Finance Tracker application.

## Technology Stack

- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **Spring Security with JWT**
- **PostgreSQL**
- **MapStruct** for DTO mapping
- **Maven** for dependency management
- **Docker** for containerization

## Quick Start

### Local Development

1. Make sure PostgreSQL is running (or use Docker Compose from root directory)
2. Update `application-dev.properties` with your database configuration
3. Run the application:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Docker Development

From the root directory:
```bash
docker-compose up --build
```

### Testing

Run all tests:
```bash
./mvnw test
```

## API Endpoints

The API will be available at `http://localhost:8080`

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

### Transactions
- `GET /api/transactions` - Get user transactions
- `POST /api/transactions` - Create new transaction
- `PUT /api/transactions/{id}` - Update transaction
- `DELETE /api/transactions/{id}` - Delete transaction

### User Management
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update user profile

## Configuration

The application supports multiple profiles:
- `dev` - Development profile
- `prod` - Production profile  
- `docker` - Docker environment profile
- `test` - Testing profile

## Database

The application uses PostgreSQL with automatic schema generation via Hibernate DDL.

Initial database setup scripts are located in `init-scripts/` directory.
