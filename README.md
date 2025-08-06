# Finance Tracker

A full-stack finance tracking application with Spring Boot backend and modern frontend.

## Project Structure

```
finance-tracker/
├── backend/                    # Spring Boot backend application
│   ├── src/                   # Java source code
│   ├── pom.xml               # Maven configuration
│   ├── Dockerfile            # Backend Docker configuration
│   ├── init-scripts/         # Database initialization scripts
│   └── logs/                 # Application logs
├── frontend/                  # Frontend application
│   └── README.md             # Frontend-specific documentation
├── docker-compose.yml        # Docker compose configuration
└── README.md                 # This file
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- Node.js (for frontend development)

### Backend Development
Navigate to the `backend/` directory and follow the instructions in the backend documentation.

### Frontend Development
Navigate to the `frontend/` directory and follow the instructions in the frontend README.

### Running with Docker
From the root directory:
```bash
docker-compose up --build
```

This will start:
- PostgreSQL database on port 5432
- Backend API on port 8080
- Frontend (when implemented) on port 3000

## Documentation

- [Architecture Overview](ARCHITECTURE.md)
- [Authentication Guide](AUTHENTICATION_GUIDE.md)
- [Database Configuration](DATABASE_CONFIGURATION.md)
- [Docker Guide](DOCKER.md)
- [Advanced MapStruct Features](ADVANCED_MAPSTRUCT_FEATURES.md)
- [Three Layer Architecture](THREE_LAYER_ARCHITECTURE.md)

## API Documentation

The API documentation is available via the Postman collection: `Finance_Tracker_API_Updated.postman_collection.json`
