# Explore DB MongoDB - Spring Boot Application

This is a Spring Boot 3+ application with Java 21 that demonstrates MongoDB integration with a RESTful API.

## Features

- Spring Boot 3.3.4 with Java 21
- MongoDB integration using Spring Data MongoDB
- RESTful API with CRUD operations
- Input validation
- Custom query methods
- Exception handling

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- MongoDB running on localhost:27017 (or update connection string in application.properties)

## Quick Start

1. **Start MongoDB**: Make sure MongoDB is running on your local machine
   ```bash
   # If using MongoDB Community Edition
   mongod
   
   # Or if using Docker
   docker run -d -p 27017:27017 --name mongodb mongo:latest
   ```

2. **Build and Run the application**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

3. **Test the API**: The application will start on http://localhost:8080

## API Endpoints

### User Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users/email/{email}` | Get user by email |
| GET | `/api/users/search?name={name}` | Search users by name |
| POST | `/api/users` | Create a new user |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |
| GET | `/api/users/count` | Get user count |

### Sample API Calls

**Create a user:**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "email": "john.doe@example.com"}'
```

**Get all users:**
```bash
curl http://localhost:8080/api/users
```

**Search users by name:**
```bash
curl "http://localhost:8080/api/users/search?name=John"
```

**Update a user:**
```bash
curl -X PUT http://localhost:8080/api/users/{USER_ID} \
  -H "Content-Type: application/json" \
  -d '{"name": "John Smith", "email": "john.smith@example.com"}'
```

**Delete a user:**
```bash
curl -X DELETE http://localhost:8080/api/users/{USER_ID}
```

## MongoDB Configuration

The application is configured to connect to MongoDB at `mongodb://localhost:27017/exploredatabase`. You can modify the connection settings in `src/main/resources/application.properties`:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/exploredatabase
spring.data.mongodb.database=exploredatabase
```

## Project Structure

```
src/
├── main/
│   ├── java/com/learning/explore/db/
│   │   ├── ExploreDbMongoApplication.java     # Main application class
│   │   ├── controller/
│   │   │   └── UserController.java            # REST controller
│   │   ├── model/
│   │   │   └── User.java                      # MongoDB document model
│   │   ├── repository/
│   │   │   └── UserRepository.java            # MongoDB repository
│   │   └── service/
│   │       └── UserService.java               # Business logic
│   └── resources/
│       └── application.properties             # Configuration
└── test/
    └── java/
        └── ... (test files)
```

## Dependencies Included

- `spring-boot-starter-data-mongodb` - MongoDB integration
- `spring-boot-starter-web` - Web and REST support
- `spring-boot-starter-validation` - Input validation
- `spring-boot-devtools` - Development tools
- `spring-boot-starter-test` - Testing support
- `de.flapdoodle.embed.mongo.spring3x` - Embedded MongoDB for testing

## Next Steps

1. Start experimenting with the API endpoints
2. Explore MongoDB queries in the repository layer
3. Add more complex data models and relationships
4. Implement pagination and sorting
5. Add authentication and authorization
6. Write unit and integration tests

## Learning Resources

- [Spring Data MongoDB Reference](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/)
- [MongoDB Java Driver Documentation](https://mongodb.github.io/mongo-java-driver/)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
