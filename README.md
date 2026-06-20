# Recipe Aggregator

A microservices-based web application for parsing, storing, and searching recipes from external websites. The system consists of a parser service, a recipe service, and a frontend application.

## Architecture Overview

The application follows a microservices architecture with the following components:

- **Parser Service**: Extracts recipe data from websites using web scraping and JSON-LD parsing
- **Recipe Service**: Stores, manages, and provides search capabilities for recipes
- **Frontend**: React-based user interface for interacting with the system
- **Message Queue**: Kafka for asynchronous communication between services
- **Storage**: PostgreSQL (user data), Elasticsearch (recipe search), Redis (caching)
- **Security**: JWT-based authentication with token cookies

## Technology Stack

### Backend
- Java 21
- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- Spring Data Elasticsearch
- Spring Kafka
- Spring Cloud Vault

### Frontend
- React 19
- React Router
- React Scripts

### Infrastructure
- PostgreSQL
- Elasticsearch
- Redis
- Apache Kafka
- Docker & Docker Compose
- Vault (secrets management)

### Build Tools
- Maven
- Node.js/npm

## Key Features

### Parser Service
- **Web Scraping**: Extracts recipe data from supported websites using Jsoup
- **JSON-LD Parsing**: Parses structured recipe data from JSON-LD scripts
- **Caching**: Redis-based caching for visited URLs and parsed recipes
- **Scheduled Parsing**: Automated recipe fetching at configurable intervals
- **Kafka Integration**: Sends parsed recipes to Kafka for processing

### Recipe Service
- **Kafka Consumer**: Processes recipe messages from Kafka
- **Elasticsearch Storage**: Indexes recipes for efficient searching
- **Advanced Search**: Search by name, ingredients, and filterable parameters
- **User Management**: Registration, authentication, and authorization
- **Favorites**: Users can save and manage favorite recipes
- **CSRF Protection**: Built-in CSRF token protection

### Frontend
- **React 19**: Modern React with hooks and functional components
- **Routing**: React Router for navigation
- **API Integration**: RESTful API communication with the backend

## API Endpoints

### Authentication
```
POST /api/v1/auth/reg     - User registration
POST /api/v1/auth/login   - User login
```

### User Account
```
GET    /api/v1/account/favorite     - Get favorite recipes
POST   /api/v1/account/favorite     - Add recipe to favorites
DELETE /api/v1/account/favorite     - Remove recipe from favorites
```

### Search
```
GET  /api/v1/search/name/{name}              - Search by recipe name
POST /api/v1/search/ingredients              - Search by ingredients
POST /api/v1/search/with-filtering           - Search with filters
```

### CSRF
```
GET /csrf                                    - Get CSRF token
```

## Getting Started

### Prerequisites
- Docker and Docker Compose
- Java 21
- Maven
- Node.js and npm

### Running with Docker Compose

```bash
# Build all services
docker-compose build

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### Running Locally

1. **Start Infrastructure**:
```bash
docker-compose up -d kafka postgres elasticsearch redis vault
```

2. **Build and Run Parser Service**:
```bash
cd parser-service-api
mvn clean install
mvn spring-boot:run
```

3. **Build and Run Recipe Service**:
```bash
cd recipe-service-api
mvn clean install
mvn spring-boot:run
```

4. **Run Frontend**:
```bash
cd frontend
npm install
npm start
```
## Security

- JWT-based authentication with cookie storage
- CSRF protection with token-based validation
- Password encryption using BCrypt
- Role-based access control
- Secure HTTP-only cookies

## Data Flow

1. **Recipe Parsing Flow**:
   ```
   Parser Service -> Web Scraping -> Recipe Extraction -> Kafka -> Recipe Service -> Elasticsearch
   ```

2. **Search Flow**:
   ```
   Frontend -> Recipe Service -> Elasticsearch -> Filtered Results -> Frontend
   ```

3. **Authentication Flow**:
   ```
   Frontend -> Login/Register -> JWT Token -> Cookie -> Subsequent Requests
   ```
   
## Monitoring

- **Health Checks**: Spring Boot Actuator endpoints available
- **Logging**: Structured logging with logback configuration
