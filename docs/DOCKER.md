# Docker Setup for Finance Tracker

This document provides instructions for running the Finance Tracker application using Docker and Docker Compose.

## Prerequisites

- Docker
- Docker Compose

## Quick Start

1. **Clone the repository and navigate to the project directory:**
   ```bash
   git clone <repository-url>
   cd finance-tracker
   ```

2. **Start the application with Docker Compose:**
   ```bash
   docker-compose up -d
   ```

   This will start:
   - PostgreSQL database on port 5432
   - Finance Tracker application on port 8080

3. **Access the application:**
   - Application: http://localhost:8080
   - Health check: http://localhost:8080/actuator/health
   - API documentation: http://localhost:8080/swagger-ui.html

## Docker Compose Services

### PostgreSQL Database
- **Image**: postgres:16-alpine
- **Port**: 5432
- **Database**: finance_tracker
- **Username**: finance_user
- **Password**: finance_password

### Finance Tracker Application
- **Port**: 8080
- **Profile**: docker
- **Auto-restart**: enabled

## Useful Commands

### Start services
```bash
docker-compose up -d
```

### Stop services
```bash
docker-compose down
```

### View logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f finance-tracker-app
docker-compose logs -f postgres
```

### Rebuild and restart
```bash
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

### Access PostgreSQL database
```bash
docker-compose exec postgres psql -U finance_user -d finance_tracker
```

### Remove volumes (warning: this will delete all data)
```bash
docker-compose down -v
```

## Environment Variables

The application uses the following environment variables in Docker:

- `SPRING_PROFILES_ACTIVE=docker`
- `SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/finance_tracker`
- `SPRING_DATASOURCE_USERNAME=finance_user`
- `SPRING_DATASOURCE_PASSWORD=finance_password`

## Development

### Local Development with Docker Database

If you want to run the application locally but use the Docker PostgreSQL database:

1. Start only the database:
   ```bash
   docker-compose up -d postgres
   ```

2. Run the application locally:
   ```bash
   ./mvnw spring-boot:run
   ```

### Database Initialization

Database initialization scripts are located in the `init-scripts/` directory. These scripts run automatically when the PostgreSQL container starts for the first time.

## Troubleshooting

### Application won't start
- Check if the database is healthy: `docker-compose ps`
- View application logs: `docker-compose logs finance-tracker-app`

### Database connection issues
- Verify PostgreSQL is running: `docker-compose logs postgres`
- Check network connectivity: `docker-compose exec finance-tracker-app ping postgres`

### Port conflicts
- Make sure ports 5432 and 8080 are not in use by other applications
- Change ports in docker-compose.yml if needed
