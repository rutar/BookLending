# Book Lending Application Documentation

## Table of Contents
1. [Introduction](#introduction)
2. [Technical Stack](#technical-stack)
3. [System Architecture](#system-architecture)
4. [Backend](#backend)
    - [API Endpoints](#api-endpoints)
    - [Controllers](#controllers)
    - [Services](#services)
    - [Models](#models)
5. [Frontend](#frontend)
    - [Components](#components)
    - [Services](#frontend-services)
6. [Database](#database)
7. [Authentication and Authorization](#authentication-and-authorization)
8. [Testing](#testing)
9. [Deployment](#deployment)
10. [Future Enhancements](#future-enhancements)

## 1. Introduction

The Book Lending Application is a crowdsourced platform that allows users to borrow and lend books. It provides a user-friendly interface for managing book reservations, borrowing, and returning processes.

### Key Features:
- User registration and authentication
- Book search functionality
- Book reservation and cancellation
- Marking books as received and returned
- Adding and removing books for lending (for book owners)
- Role-based access control (User and Admin roles)

## 2. Technical Stack

- **Backend:** Java 17+, Spring Boot
- **Frontend:** Angular
- **Database:** PostgreSQL
- **API Documentation:** Swagger (OpenAPI specification)
- **Containerization:** Docker

## 3. System Architecture

The application follows a typical client-server architecture:

1. **Client (Frontend):** Angular-based single-page application (SPA)
2. **Server (Backend):** Spring Boot REST API
3. **Database:** PostgreSQL for data persistence

## 4. Backend

### API Endpoints

The backend exposes RESTful API endpoints for various functionalities. Here's an overview of the main endpoints:

- `/api/auth`: Authentication endpoints
- `/api/users`: User management endpoints
- `/api/books`: Book management endpoints
- `/api/actions`: Book lending action endpoints (reserve, cancel, etc.)

Detailed API documentation is available through Swagger UI at `/swagger-ui.html` when running the application.

### Controllers

The application uses several controllers to handle different aspects of the system:

1. **AuthController:** Handles user authentication
2. **UserController:** Manages user-related operations
3. **BookController:** Handles book-related operations
4. **ActionController:** Manages book lending actions

Example of AuthController:

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest authRequest) {
        return authService.authenticateAndGenerateToken(
            authRequest.getUsername(), 
            authRequest.getPassword()
        );
    }
}
```

### Services

Services contain the business logic of the application. Key services include:

- **AuthService:** Handles user authentication and token generation
- **UserService:** Manages user-related operations
- **BookService:** Handles book-related operations
- **ActionService:** Manages book lending actions

### Models

The main domain models include:

- **User:** Represents a user in the system
- **Book:** Represents a book in the lending library
- **Action:** Represents a lending action (e.g., reservation, return)

## 5. Frontend

The frontend is built with Angular and consists of several components and services.

### Components

1. **LoginComponent:** Handles user authentication
2. **DashboardComponent:** Admin dashboard for managing books
3. **HomeComponent:** User home page for borrowing books
4. **AddBookComponent:** Form for adding new books
5. **ConfirmationModalComponent:** Reusable confirmation modal

Example of LoginComponent:

```typescript
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  username: string = '';
  password: string = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  login() {
    this.authService.login(this.username, this.password).subscribe(
      (token: string) => {
        this.authService.saveToken(token);
        const roleName = this.authService.getRoleNameFromToken();
        // Navigate based on user role
        // ...
      },
      (error) => {
        this.notificationService.openDialog('Invalid username or password.', true);
      }
    );
  }
}
```

### Frontend Services

1. **AuthService:** Handles authentication-related operations
2. **BookService:** Manages book-related API calls
3. **NotificationService:** Provides a centralized way to display notifications

## 6. Database

The application uses PostgreSQL as its primary database. Key tables include:

- `users`: Stores user information
- `books`: Stores book information
- `actions`: Stores lending actions

## 7. Authentication and Authorization

The application uses JWT (JSON Web Tokens) for authentication. The process works as follows:

1. User logs in with username and password
2. Server validates credentials and generates a JWT
3. Client stores the JWT and includes it in the Authorization header for subsequent requests
4. Server validates the JWT for protected endpoints

Role-based access control is implemented to distinguish between regular users and administrators.

## 8. Testing

The application includes unit tests and integration tests for both backend and frontend components. Tests are written using JUnit for the backend and Jasmine/Karma for the frontend.

## 9. Deployment

The application is containerized using Docker, allowing for easy deployment and scaling. A `docker-compose.yml` file is provided to orchestrate the deployment of the backend, frontend, and database services.

## 10. Future Enhancements

Potential areas for future development include:

- Implementing a rating system for borrowers and lenders
- Adding a notification system for due dates and available books
- Integrating with external book databases for richer book information
- Implementing a recommendation system based on user preferences and borrowing history

This documentation provides an overview of the Book Lending Application. For more detailed information on specific components or processes, please refer to the inline code documentation and comments.