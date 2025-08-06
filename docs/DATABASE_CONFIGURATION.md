# Database Configuration Changes

## Summary of Changes Made

This document outlines the changes made to configure PostgreSQL to run on port 5432:5432 and set up all environments to use PostgreSQL while keeping H2 for testing.

### 1. Docker Configuration Changes

**File:** `docker-compose.yml`
- **Changed:** PostgreSQL port mapping from `5434:5432` to `5432:5432`
- **Impact:** PostgreSQL container now runs on standard port 5432

### 2. Application Configuration Changes

#### Main Configuration (`application.properties`)
- **Changed:** Database URL from `localhost:5434` to `localhost:5432`
- **Impact:** Main application now connects to PostgreSQL on port 5432

#### Development Configuration (`application-dev.properties`)
- **Changed:** Switched from H2 in-memory database to PostgreSQL
- **Removed:** H2 console configuration
- **Added:** PostgreSQL configuration with localhost:5432
- **Impact:** Development environment now uses PostgreSQL instead of H2

#### Production Configuration (`application-prod.properties`)
- **Created:** New production profile configuration
- **Features:** 
  - PostgreSQL on port 5432
  - Environment variable support for JWT secret
  - Optimized logging for production
  - DDL auto set to `validate` for safety

#### Docker Configuration (`application-docker.properties`)
- **No changes needed:** Already correctly configured to use PostgreSQL

#### Test Configuration (`application-test.properties`)
- **No changes needed:** Already correctly configured to use H2 for testing

### 3. Dependency Configuration Changes

**File:** `pom.xml`
- **Changed:** H2 dependency scope from `runtime` to `test`
- **Impact:** H2 is now only available during testing, preventing accidental use in other environments

## Environment Summary

| Environment | Database | Port | Configuration File |
|-------------|----------|------|-------------------|
| Default | PostgreSQL | 5432 | `application.properties` |
| Development (`dev`) | PostgreSQL | 5432 | `application-dev.properties` |
| Production (`prod`) | PostgreSQL | 5432 | `application-prod.properties` |
| Docker (`docker`) | PostgreSQL | 5432 | `application-docker.properties` |
| Testing (`test`) | H2 (in-memory) | N/A | `application-test.properties` |

## How to Use

### For Local Development
```bash
# Start PostgreSQL with Docker
docker-compose up postgres -d

# Run application in development mode
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

### For Production
```bash
# Set environment variables
export JWT_SECRET=your-production-jwt-secret
export JWT_EXPIRATION=86400

# Run with production profile
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

### For Testing
```bash
# Tests will automatically use H2 in-memory database
./mvnw test
```

### For Docker
```bash
# Run the complete stack
docker-compose up
```

## Notes

1. **PostgreSQL Database:** All non-test environments now use PostgreSQL running on port 5432
2. **H2 Testing:** Tests continue to use H2 in-memory database for isolation and speed
3. **Production Safety:** Production profile uses `validate` DDL mode to prevent schema changes
4. **Environment Variables:** Production profile supports environment variables for sensitive configuration
