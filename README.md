# Flight Booking System

![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-green.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)

A comprehensive RESTful API for flight booking management built with Spring Boot.

## Features

- 🔐 JWT Authentication with refresh token support
- 🔢 Two-factor authentication for enhanced security
- 👥 Role-based authorization (Admin/User)
- ✈️ Airline and airport management
- 🗓️ Flight scheduling and status tracking
- 💺 Seat allocation with different classes
- 🎫 Ticket generation and check-in functionality
- 📱 Booking management with email notifications
- 🌐 Cross-origin resource sharing enabled

## Tech Stack

- **Java 17**: Core programming language
- **Spring Boot 3.4.5**: Application framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Data persistence
- **PostgreSQL**: Database
- **JWT**: Stateless authentication
- **TOTP**: Two-factor authentication
- **Flyway**: Database migrations
- **Maven**: Build automation
- **Lombok**: Boilerplate code reduction

## Installation & Setup

### Prerequisites

- JDK 17+
- PostgreSQL 14+
- Maven 3.6+
- SMTP server for email notifications

### Quick Start

1. Clone the repository:

   ```bash
   git clone https://github.com/yourusername/flight-booking-system.git
   cd flight-booking-system
   ```

2. Configure database in `application.properties`:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/flight_booking
   spring.datasource.username=postgres
   spring.datasource.password=your_password
   ```

3. Configure email settings in `application.properties`:

   ```properties
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=your_email@gmail.com
   spring.mail.password=your_app_password
   ```

4. Run database migrations:

   ```bash
   ./mvnw flyway:migrate
   ```

5. Build and run the application:
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

The application will start on port 8085 with context path `/api`.

## API Documentation

### Authentication

| Method | Endpoint                     | Description         | Access |
| ------ | ---------------------------- | ------------------- | ------ |
| POST   | /auth/signup                 | Register a new user | Public |
| POST   | /auth/signin                 | Login               | Public |
| POST   | /auth/verify-2fa             | Verify 2FA code     | Public |
| POST   | /auth/refresh-token          | Refresh JWT token   | Public |
| POST   | /auth/request-password-reset | Request reset       | Public |
| POST   | /auth/reset-password         | Reset password      | Public |

### User Management

| Method | Endpoint               | Description      | Access        |
| ------ | ---------------------- | ---------------- | ------------- |
| GET    | /users/me              | Get current user | Authenticated |
| POST   | /users/update-password | Update password  | Authenticated |
| POST   | /users/setup-2fa       | Setup 2FA        | Authenticated |
| POST   | /users/confirm-2fa     | Confirm 2FA      | Authenticated |
| POST   | /users/disable-2fa     | Disable 2FA      | Authenticated |
| GET    | /users                 | Get all users    | Admin         |
| DELETE | /users/{id}            | Delete user      | Admin         |

### Airline Management

| Method | Endpoint       | Description       | Access        |
| ------ | -------------- | ----------------- | ------------- |
| GET    | /airlines      | Get all airlines  | Authenticated |
| GET    | /airlines/{id} | Get airline by ID | Authenticated |
| POST   | /airlines      | Create airline    | Admin         |
| PUT    | /airlines/{id} | Update airline    | Admin         |
| DELETE | /airlines/{id} | Delete airline    | Admin         |

### Airport Management

| Method | Endpoint              | Description         | Access        |
| ------ | --------------------- | ------------------- | ------------- |
| GET    | /airports             | Get all airports    | Authenticated |
| GET    | /airports/{id}        | Get airport by ID   | Authenticated |
| GET    | /airports/code/{code} | Get airport by code | Authenticated |
| POST   | /airports             | Create airport      | Admin         |
| PUT    | /airports/{id}        | Update airport      | Admin         |
| DELETE | /airports/{id}        | Delete airport      | Admin         |

### Flight Management

| Method | Endpoint                | Description            | Access        |
| ------ | ----------------------- | ---------------------- | ------------- |
| GET    | /flights                | Get all flights        | Authenticated |
| GET    | /flights/{id}           | Get flight by ID       | Authenticated |
| GET    | /flights/search/by-code | Search flights by code | Authenticated |
| POST   | /flights                | Create flight          | Admin         |
| PUT    | /flights/{id}           | Update flight          | Admin         |
| PATCH  | /flights/{id}/status    | Update flight status   | Admin         |
| DELETE | /flights/{id}           | Delete flight          | Admin         |

### Booking and Tickets

| Method | Endpoint               | Description           | Access          |
| ------ | ---------------------- | --------------------- | --------------- |
| POST   | /bookings              | Create booking        | Authenticated   |
| GET    | /bookings/user         | Get user bookings     | Authenticated   |
| PATCH  | /bookings/{id}/status  | Update booking status | Authenticated\* |
| GET    | /tickets/user          | Get user tickets      | Authenticated   |
| POST   | /tickets/{id}/check-in | Check-in ticket       | Authenticated\* |
| POST   | /tickets/{id}/cancel   | Cancel ticket         | Authenticated\* |

\* User must be owner of the booking/ticket or be an admin

## Testing

### Sample User Signup

```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "role": ["user"]
}
```

### Sample Login

```json
{
  "username": "testuser",
  "password": "password123"
}
```

### Sample Flight Creation (Admin)

```json
{
  "airline": {
    "id": 1
  },
  "flightNumber": "AL1001",
  "departureDate": "2025-06-01T10:00:00",
  "arrivalDate": "2025-06-01T12:00:00",
  "originAirport": {
    "id": 1
  },
  "destinationAirport": {
    "id": 2
  },
  "status": "SCHEDULED"
}
```

### Sample Booking Creation

```json
[1, 2] // Array of flight seat IDs
```

## Project Structure

```
src/
├── main/
│   ├── java/Fligh/Booking/
│   │   ├── config/              # Configuration classes
│   │   ├── controller/          # REST controllers
│   │   ├── dto/                 # Data transfer objects
│   │   │   ├── request/         # Request models
│   │   │   └── response/        # Response models
│   │   ├── exception/           # Exception handlers
│   │   ├── model/               # Data models/entities
│   │   │   └── enums/           # Enumeration types
│   │   ├── repository/          # Data repositories
│   │   ├── security/            # Security configurations
│   │   │   ├── jwt/             # JWT utilities
│   │   │   └── services/        # Security services
│   │   ├── service/             # Business logic
│   │   └── FlightBookingApplication.java  # Main class
│   └── resources/
│       ├── application.properties  # Application properties
│       └── db/migration/           # Flyway migrations
└── test/
    └── java/Fligh/Booking/         # Test classes
```

## Database Schema

![Database Schema](https://placeholder-for-db-schema-image.com/schema.png)

## Roadmap

- [ ] Docker containerization
- [ ] API documentation with Swagger/OpenAPI
- [ ] Payment integration
- [ ] Loyalty points system
- [ ] Flight search engine with filters
- [ ] Baggage management
- [ ] Integration with external flight APIs

## Contributing

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add some amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Spring Boot team for the great framework
- All contributors who have invested time and effort
