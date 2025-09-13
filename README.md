# Image Management Platform

Enterprise microservices platform for user management and image handling with Imgur/Dropbox integration, JWT authentication, and 100K RPM optimization.

## Architecture Overview

This is a monorepo containing microservices built with a modular architecture:

```
image-management-platform/
├── modules/
│   ├── common/              # Shared utilities, DTOs, exceptions
│   └── user-service/        # User management & image handling service
├── infra/
│   ├── docker/             # Docker configurations
│   └── monitoring/         # Prometheus, Grafana configs
├── ci/
│   └── pipelines/          # CI/CD pipeline configurations
└── docs/                   # Documentation
```

## Features

### Core Features
- **User Management**: Registration, authentication, profile management
- **Image Storage**: Imgur API with Dropbox fallback
- **Security**: JWT authentication with OAuth2
- **Performance**: 100K RPM optimization with Caffeine caching
- **Rate Limiting**: Token bucket algorithm with per-endpoint limits
- **Event-Driven**: Kafka integration for real-time events
- **Monitoring**: Performance metrics and cache statistics

## Technology Stack

| Category | Technologies |
|----------|-------------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.2.0, Spring Security, Spring Data JPA |
| **Database** | H2 (in-memory), PostgreSQL (production) |
| **Messaging** | Apache Kafka |
| **Caching** | Caffeine (In-Memory) |
| **Documentation** | OpenAPI 3, Swagger UI |
| **Testing** | JUnit 5, Mockito, Testcontainers |
| **Build** | Maven 3.9+ |
| **Containerization** | Docker, Docker Compose |
| **Monitoring** | Prometheus, Grafana |
| **CI/CD** | GitHub Actions, Jenkins |
| **Code Quality** | SonarQube, Checkstyle, SpotBugs |

## Prerequisites

- **Java 17+**
- **Maven 3.9+**
- **Docker & Docker Compose**
- **Imgur API Client ID** ([Get one here](https://api.imgur.com/oauth2/addclient))
- **Dropbox API Credentials** (Optional - for fallback storage)

## Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url>
cd image-management-platform
```

### 2. Set Environment Variables

```bash
export IMGUR_CLIENT_ID=your_imgur_client_id_here
```

### 3. Start Infrastructure Services

```bash
cd infra/docker
docker-compose up -d kafka prometheus grafana
```

### 4. Build and Run the Application

```bash
# Build all modules
mvn clean compile

# Run user service
cd modules/user-service
mvn spring-boot:run
```

### 5. Access the Application

- **API Documentation**: http://localhost:8080/api/swagger-ui.html
- **H2 Console**: http://localhost:8080/api/h2-console
- **Actuator Health**: http://localhost:8080/api/actuator/health
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)
- **Kafka UI**: http://localhost:8090

## Project Structure

### Modules

#### Common Module (`modules/common/`)
Shared components across all services:
- **DTOs**: `ApiResponse`, `ErrorResponse`
- **Exceptions**: Base exceptions with error codes
- **Utilities**: Validation, correlation ID management
- **Global Exception Handler**: Centralized error handling

#### User Service (`modules/user-service/`)
Main microservice providing:
- User registration and authentication
- JWT token management
- Image upload/management via Imgur API with Dropbox fallback
- Event publishing to Kafka
- Performance monitoring and caching
- Rate limiting and 100K RPM optimization

### Infrastructure (`infra/`)

#### Docker (`infra/docker/`)
- **Dockerfile**: Multi-stage build for user service
- **docker-compose.yml**: Complete development environment
- **monitoring/**: Prometheus and Grafana configurations

### CI/CD (`ci/pipelines/`)
- **GitHub Actions**: Complete CI/CD workflow
- **Jenkins**: Enterprise pipeline configuration

## API Documentation

### Authentication Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | Register new user |
| POST | `/api/v1/auth/login` | User login |
| POST | `/api/v1/auth/validate` | Validate JWT token |

### User Management Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/users/{userId}` | Get user by ID |
| GET | `/api/v1/users/username/{username}` | Get user by username |
| PUT | `/api/v1/users/{userId}` | Update user |
| DELETE | `/api/v1/users/{userId}` | Deactivate user |
| GET | `/api/v1/users/exists/username/{username}` | Check username availability |
| GET | `/api/v1/users/exists/email/{email}` | Check email availability |

### Image Management Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/images/upload/{userId}` | Upload image |
| GET | `/api/v1/images/user/{userId}` | Get user's images |
| GET | `/api/v1/images/{imageId}?username={username}` | Get specific image |
| PUT | `/api/v1/images/{imageId}?username={username}` | Update image |
| DELETE | `/api/v1/images/{imageId}?username={username}` | Delete image |

### Dropbox Storage Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/dropbox/upload/{userId}` | Upload directly to Dropbox |
| GET | `/api/v1/dropbox/download?dropboxPath={path}` | Download from Dropbox |
| GET | `/api/v1/dropbox/images/{userId}` | List user's Dropbox images |
| GET | `/api/v1/dropbox/download-zip/{userId}` | Download all images as ZIP |
| DELETE | `/api/v1/dropbox/delete?dropboxPath={path}` | Delete from Dropbox |

### Performance Monitoring Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/performance/metrics` | Get performance metrics (RPM, stats) |
| POST | `/api/v1/performance/reset` | Reset performance counters |
| GET | `/api/v1/performance/health` | Lightweight health check |

### Cache Management Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/cache/stats` | Get all cache statistics |
| GET | `/api/v1/cache/stats/{cacheName}` | Get specific cache stats |
| POST | `/api/v1/cache/clear` | Clear all caches |
| POST | `/api/v1/cache/clear/{cacheName}` | Clear specific cache |
| GET | `/api/v1/cache/info` | Get cache information |

## Testing

### Unit Tests
```bash
# Run all unit tests
mvn test

# Run tests for specific module
mvn test -pl modules/user-service
```

### Integration Tests
```bash
# Run integration tests
mvn verify -Pintegration-test
```

### Test Coverage
```bash
# Generate coverage report
mvn jacoco:report

# View report at: target/site/jacoco/index.html
```

## Deployment

### Local Development
```bash
# Start infrastructure services
docker-compose -f infra/docker/docker-compose.yml up -d

# Build and run user service
mvn spring-boot:run -pl modules/user-service
```

### Production Deployment

#### Using Docker
```bash
# Build production image
mvn clean package -Pdocker

# Run with production profile
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e IMGUR_CLIENT_ID=your_client_id \
  synchrony/user-service:latest
```

#### Using Kubernetes
```bash
# Apply Kubernetes manifests (when available)
kubectl apply -f infra/k8s/
```

## Monitoring & Observability

### Metrics
- **Application Metrics**: Custom business metrics
- **JVM Metrics**: Memory, GC, threads
- **HTTP Metrics**: Request rates, response times
- **Database Metrics**: Connection pool, query performance

### Health Checks
- **Liveness Probe**: `/api/actuator/health/liveness`
- **Readiness Probe**: `/api/actuator/health/readiness`
- **Custom Health Indicators**: Database, Kafka, Redis

### Logging
- **Structured Logging**: JSON format with correlation IDs
- **Log Levels**: Configurable per package
- **Log Aggregation**: Ready for ELK stack integration

## Security

### Authentication & Authorization
- **JWT Tokens**: Stateless authentication
- **OAuth2**: Resource server configuration
- **Password Encryption**: BCrypt hashing
- **CORS**: Configurable cross-origin requests

### Security Scanning
- **OWASP Dependency Check**: Vulnerability scanning
- **Trivy**: Container image scanning
- **SonarQube**: Code security analysis

## Configuration

### Application Properties
Key configuration properties in `application.properties`:

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/api

# Database Configuration
spring.datasource.url=jdbc:h2:mem:userdb
spring.datasource.username=sa
spring.datasource.password=password

# JWT Configuration
app.jwt.secret=your-secret-key
app.jwt.expiration=86400000

# Imgur API Configuration
app.imgur.client-id=${IMGUR_CLIENT_ID}
app.imgur.base-url=https://api.imgur.com/3

# Kafka Configuration
spring.kafka.bootstrap-servers=localhost:9092
app.kafka.topic.image-events=image-events

# Caching Configuration
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=10000,expireAfterWrite=30m

# Performance Configuration
server.tomcat.threads.max=400
server.tomcat.max-connections=10000
```

### Environment-Specific Profiles
- **dev**: Development environment
- **test**: Testing environment
- **docker**: Docker environment
- **prod**: Production environment

## Contributing

### Development Workflow
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass: `mvn verify`
6. Commit your changes: `git commit -m 'Add amazing feature'`
7. Push to the branch: `git push origin feature/amazing-feature`
8. Open a Pull Request

### Code Quality Standards
- **Test Coverage**: Minimum 80% line coverage
- **Code Style**: Checkstyle configuration enforced
- **Security**: OWASP dependency check required
- **Documentation**: JavaDoc for public APIs

## Performance Optimization (100K RPM)

### High-Performance Features
- **Caffeine Caching**: In-memory caching with 30-min TTL, 10K entries per cache
- **Rate Limiting**: Token bucket algorithm (1000 API, 100 Auth, 200 Upload requests/min)
- **Connection Pooling**: Optimized HikariCP (50 max connections)
- **Async Processing**: Non-blocking Kafka events and image processing
- **Thread Pool Optimization**: 200 max threads with dedicated pools

### Performance Monitoring
- **Real-time RPM tracking**: `/api/v1/performance/metrics`
- **Cache statistics**: `/api/v1/cache/stats` with hit rates and eviction counts
- **Rate limit headers**: `X-RateLimit-Remaining`, `X-RateLimit-Limit`
- **Health checks**: Lightweight `/api/v1/performance/health` endpoint

## Troubleshooting

### Common Issues

#### Application Won't Start
```bash
# Check if ports are available
netstat -tulpn | grep :8080

# Check Docker services
docker-compose ps

# View application logs
docker-compose logs user-service
```

#### Database Connection Issues
```bash
# Verify H2 console access
curl http://localhost:8080/api/h2-console

# Check database configuration
grep -r "datasource" modules/user-service/src/main/resources/
```

#### Kafka Connection Issues
```bash
# Check Kafka status
docker-compose exec kafka kafka-topics --list --bootstrap-server localhost:9092

# View Kafka logs
docker-compose logs kafka
```

#### Performance Issues
```bash
# Check current RPM
curl http://localhost:8080/api/v1/performance/metrics

# Check cache statistics
curl http://localhost:8080/api/v1/cache/stats

# Clear caches if needed
curl -X POST http://localhost:8080/api/v1/cache/clear
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Team

- **Synchrony Development Team**
- **Email**: team@synchrony.com
- **Documentation**: [Project Wiki](https://github.com/synchrony/image-management-platform/wiki)

## Links

- **API Documentation**: [Swagger UI](http://localhost:8080/api/swagger-ui.html)
- **Monitoring Dashboard**: [Grafana](http://localhost:3000)
- **Code Quality**: [SonarQube Dashboard](https://sonarcloud.io/project/overview?id=synchrony_image-management-platform)
- **Container Registry**: [Docker Hub](https://hub.docker.com/r/synchrony/user-service)

---