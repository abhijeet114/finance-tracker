# 💰 Finance Tracker

A modern, full-stack personal finance tracking application built with Spring Boot and designed for scalability and maintainability.

## 🌟 Features

- **📊 Transaction Management**: Track income and expenses with detailed categorization
- **🔐 JWT Authentication**: Secure user authentication and authorization
- **👥 Multi-User Support**: Each user manages their own financial data
- **🏗️ Clean Architecture**: Three-layer architecture with proper separation of concerns
- **📱 RESTful API**: OpenAPI 3.0 documented endpoints
- **🐳 Docker Ready**: Complete containerization for easy deployment
- **🛡️ Production Ready**: Multiple environment configurations

## 🚀 Quick Start

### Using Docker (Recommended)
```bash
# Clone the repository
git clone <repository-url>
cd finance-tracker

# Start the complete application stack
docker-compose up --build
```

### Local Development
```bash
# Start PostgreSQL database
docker-compose up postgres -d

# Run the backend application
cd backend
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

## 🌐 Endpoints

Once running, access these endpoints:

| Service | URL | Description |
|---------|-----|-------------|
| **Application** | http://localhost:8080 | Main application |
| **API Documentation** | http://localhost:8080/swagger-ui.html | Interactive API docs |
| **Health Check** | http://localhost:8080/actuator/health | Application health |
| **Database** | localhost:5432 | PostgreSQL database |

## 🏗️ Architecture

### Technology Stack
- **Backend**: Spring Boot 3.x, Java 21, Spring Security
- **Database**: PostgreSQL 16 (H2 for testing)
- **Authentication**: JWT with BCrypt password encryption
- **Mapping**: MapStruct for efficient object mapping
- **Documentation**: OpenAPI 3.0 with Swagger UI
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose

### Project Structure
```
finance-tracker/
├── 📁 backend/                 # Spring Boot API
│   ├── 📁 src/main/java/       # Application source code
│   │   └── 📁 com/finance/tracker/
│   │       ├── 📁 controller/  # REST controllers
│   │       ├── 📁 service/     # Business logic
│   │       ├── 📁 repository/  # Data access layer
│   │       ├── 📁 entity/      # JPA entities
│   │       ├── 📁 dto/         # Data transfer objects
│   │       ├── 📁 mapper/      # MapStruct mappers
│   │       └── 📁 config/      # Configuration classes
│   ├── 📁 src/main/resources/  # Configuration files
│   ├── 📁 init-scripts/        # Database initialization
│   └── 📄 pom.xml             # Maven dependencies
├── 📁 frontend/                # Frontend (ready for implementation)
├── � docs/                    # Project documentation
├── �📄 docker-compose.yml      # Docker services configuration
└── � README.md               # This file
```

## 🔧 API Usage

### Authentication
```bash
# Register a new user
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe","email":"john@example.com","password":"securePassword123"}'

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe","password":"securePassword123"}'
```

### Transactions
```bash
# Create a transaction (requires JWT token)
curl -X POST http://localhost:8080/transactions \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"amount":100.50,"type":"INCOME","category":"Salary","description":"Monthly salary"}'

# Get user transactions
curl -X GET http://localhost:8080/transactions \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🗄️ Database

### Configuration
- **Development**: PostgreSQL on localhost:5432
- **Testing**: H2 in-memory database
- **Production**: PostgreSQL with environment variable configuration
- **Docker**: PostgreSQL container with persistent volumes

### Connection Details
- **Database**: finance_tracker
- **Username**: finance_user
- **Password**: finance_password
- **Port**: 5432

## 🚀 Deployment

### Environment Profiles
| Profile | Use Case | Database | Configuration |
|---------|----------|----------|---------------|
| `dev` | Local development | PostgreSQL | `application-dev.properties` |
| `prod` | Production deployment | PostgreSQL | `application-prod.properties` |
| `docker` | Container deployment | PostgreSQL | `application-docker.properties` |
| `test` | Unit/Integration tests | H2 in-memory | `application-test.properties` |

### Production Deployment
```bash
# Set environment variables
export JWT_SECRET=your-production-jwt-secret
export JWT_EXPIRATION=86400

# Run with production profile
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

## 📚 Documentation

Comprehensive documentation is available in the following files:

> 📋 **[View All Documentation](docs/README.md)** - Complete documentation index

| Document | Description |
|----------|-------------|
| [🏗️ Architecture](docs/ARCHITECTURE.md) | System architecture and design patterns |
| [🔐 Authentication](docs/AUTHENTICATION_GUIDE.md) | JWT implementation and security |
| [🗄️ Database](docs/DATABASE_CONFIGURATION.md) | Database setup and configuration |
| [🐳 Docker](docs/DOCKER.md) | Container deployment guide |
| [🔄 MapStruct](docs/ADVANCED_MAPSTRUCT_FEATURES.md) | Advanced mapping features |
| [📋 Three-Layer](docs/THREE_LAYER_ARCHITECTURE.md) | Architectural layers explanation |
| [⚙️ Backend](docs/BACKEND.md) | Backend development setup and details |
| [🎨 Frontend](docs/FRONTEND.md) | Frontend development setup and details |

## 🔧 Development

### Prerequisites
- **Java 21+**
- **Maven 3.6+**
- **Docker & Docker Compose**
- **PostgreSQL** (or use Docker)

### IDE Setup
1. Import the project as a Maven project
2. Install Lombok plugin for your IDE
3. Enable annotation processing for MapStruct

### Code Quality
- **MapStruct**: Type-safe object mapping
- **Lombok**: Reduced boilerplate code
- **Spring Boot DevTools**: Hot reloading
- **Comprehensive Testing**: Unit and integration tests

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📊 API Collection

Import the Postman collection for easy API testing:
- **File**: `Finance_Tracker_API_Updated.postman_collection.json`
- **Includes**: All authentication and transaction endpoints

## 🐛 Troubleshooting

### Common Issues
- **Port conflicts**: Ensure ports 8080 and 5432 are available
- **Database connection**: Verify PostgreSQL is running and accessible
- **JWT errors**: Check token expiration and secret configuration

### Logs
```bash
# View application logs
docker-compose logs -f finance-tracker-app

# View database logs
docker-compose logs -f postgres
```

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Built with ❤️ using Spring Boot and modern Java practices**
