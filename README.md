# Digital Bookstore Management System – Backend

This repository contains the **backend source code** for the **Digital Bookstore Management System**, a full-stack e-commerce application built with **Java Spring Boot**.

---

## ✨ Features

### Phase 1: Core Book & Category Management
- **Category Management:** Full CRUD operations for book categories.
- **Book Management:** Full CRUD operations for books, including details, pricing, and stock.
- **Data Validation:** Robust validation for all incoming data.
- **Layered Architecture:** Clean and scalable design (Controller → Service → Repository).
- **Relational Mapping:** JPA entities with one-to-many and many-to-one relationships.

### Phase 2: User & Admin Roles with JWT Security
- **Secure Authentication:** JWT-based authentication and authorization.
- **Role-Based Access Control (RBAC):** Distinct privileges for ADMIN and USER roles.
- **User Management:** User registration, login, and profile management.
- **Protected Endpoints:** Admin-only access for managing categories and books.

### Phase 3: E-commerce Functionality
- **Shopping Cart:** Add, update, remove, and view items in the cart.
- **Order Processing:** Place orders from the cart with real-time stock reduction.
- **Order Management:** View order history, track order status, and manage orders (admin).
- **Transactional Operations:** Ensure data consistency during order placement.

### Phase 4: Advanced Features & AI Integration
- **Reviews & Ratings:** Users can review and rate books they have purchased.
- **Advanced Search:** Dynamic search with filters for keyword, category, price, and rating.
- **Admin Analytics:** Dashboard with insights on sales, revenue, and top-selling books.
- **File Upload:** Secure image upload for book covers.
- **Email Notifications:** Asynchronous email notifications for registration and order updates.

**Future Scope : AI Integration:**
- **Recommendation Engine:** Personalized book recommendations.
- **Intelligent Chatbot:** AI-powered customer support.
- **Predictive Analytics:** Demand forecasting and inventory optimization.

---

## 🚀 Technologies Used

**Backend**
- **Framework:** Java 17, Spring Boot 3
- **Security:** Spring Security, JWT
- **Data:** Spring Data JPA, Hibernate
- **Database:** MySQL
- **API Testing:** Postman
- **Build Tool:** Maven

---

## ⚙️ Setup and Installation

### Prerequisites
- Java JDK 17+
- Apache Maven 3.6+
- MySQL 8.0+
- Postman (for API testing)
- IDE (IntelliJ IDEA or VS Code recommended)

### Configuration

1. **Clone the repository:**
```bash
git clone https://github.com/your-username/digital-bookstore-backend.git
cd digital-bookstore-backend
```

2. **Database Setup:**

   Create a MySQL database named `digital_bookstore`.
   
   Update `src/main/resources/application.properties` with your MySQL username and password:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/digital_bookstore
spring.datasource.username=your-username
spring.datasource.password=your-password
```

3. **JWT Configuration:**

   Set your JWT secret key in `application.properties`:

```properties
app.jwtSecret=your-super-secret-key-that-is-long-enough
```

4. **Run the application:**

```bash
mvn spring-boot:run
```

The application will start on http://localhost:8080.

---

## 📦 API Endpoints

### Authentication

- `POST /api/auth/signup` – Register a new user
- `POST /api/auth/signin` – Authenticate a user and get a JWT token

### Books

- `GET /api/books` – Get all books
- `GET /api/books/{id}` – Get a book by ID
- `POST /api/books` – Create a new book (**Admin only**)

### Cart

- `GET /api/cart` – Get the current user's cart
- `POST /api/cart/add` – Add an item to the cart

### Orders

- `POST /api/orders` – Place an order from the cart
- `GET /api/orders/my-orders` – Get the current user's order history

### Admin

- `GET /api/orders` – Get all order from the users
- `GET /api/analytics/dashboard` – Get dashboard analytics
- `PUT /api/orders/{id}/status` – Update order status

> The complete API documentation is available in the Postman collection.

---

## 🏛️ System Architecture

### Architecture Overview

The Digital Bookstore follows a **layered architecture** pattern with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                     Client Layer                             │
│            (Web Browser / Mobile App / Postman)              │
└──────────────────────┬──────────────────────────────────────┘
                       │ HTTP/HTTPS
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                  Presentation Layer                          │
│                   REST Controllers                           │
│  ┌────────────┐ ┌────────────┐ ┌──────────────────────┐    │
│  │   Auth     │ │   Book     │ │   Cart & Order       │    │
│  │ Controller │ │ Controller │ │    Controllers       │    │
│  └────────────┘ └────────────┘ └──────────────────────┘    │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                   Security Layer                             │
│              JWT Authentication Filter                       │
│         Spring Security Configuration                        │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                   Business Layer                             │
│                   Service Classes                            │
│  ┌────────────┐ ┌────────────┐ ┌──────────────────────┐    │
│  │   User     │ │   Book     │ │   Cart & Order       │    │
│  │  Service   │ │  Service   │ │     Services         │    │
│  └────────────┘ └────────────┘ └──────────────────────┘    │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                  Persistence Layer                           │
│            Spring Data JPA Repositories                      │
│  ┌────────────┐ ┌────────────┐ ┌──────────────────────┐    │
│  │   User     │ │   Book     │ │   Cart & Order       │    │
│  │ Repository │ │ Repository │ │   Repositories       │    │
│  └────────────┘ └────────────┘ └──────────────────────┘    │
└──────────────────────┬──────────────────────────────────────┘
                       │ JPA/Hibernate
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                    Database Layer                            │
│                      MySQL 8.0+                              │
│         (Books, Users, Orders, Cart, Reviews)                │
└─────────────────────────────────────────────────────────────┘
```

### Key Architectural Patterns

#### 1. **Layered Architecture**
- **Controller Layer:** Handles HTTP requests/responses, input validation, and API documentation
- **Service Layer:** Contains business logic, transactions, and orchestration
- **Repository Layer:** Data access abstraction using Spring Data JPA
- **Entity Layer:** Domain models mapped to database tables

#### 2. **Security Architecture**

```
Request Flow with JWT Authentication:

Client Request → JWT Filter → Authentication → Authorization → Controller
                      ↓              ↓              ↓
                 Validate      Load User     Check Roles
                   Token       Details        & Permissions
```

**Security Components:**
- **JWT Token Provider:** Generates and validates JWT tokens
- **User Details Service:** Loads user-specific data
- **Authentication Filter:** Intercepts requests and validates tokens
- **Password Encoder:** BCrypt for secure password hashing

#### 3. **Database Schema Design**

```
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│   Category  │────────<│     Book     │>────────│   Review    │
│             │  1:N    │              │  1:N    │             │
│ - id        │         │ - id         │         │ - id        │
│ - name      │         │ - title      │         │ - rating    │
│ - desc      │         │ - price      │         │ - comment   │
└─────────────┘         │ - stock      │         │ - user_id   │
                        │ - category_id│         │ - book_id   │
                        └──────────────┘         └─────────────┘
                               ↑                        ↑
                               │                        │
                               │ N:1                    │ N:1
                               │                        │
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│    User     │────────<│  CartItem    │         │    Order    │
│             │  1:N    │              │         │             │
│ - id        │         │ - id         │<────────│ - id        │
│ - username  │         │ - quantity   │  1:N    │ - total     │
│ - email     │         │ - user_id    │         │ - status    │
│ - password  │         │ - book_id    │         │ - user_id   │
│ - role      │         └──────────────┘         └─────────────┘
└─────────────┘                                         │
       │                                                │ 1:N
       │                                                ↓
       │                                         ┌─────────────┐
       └────────────────────────────────────────>│ OrderItem   │
                                          1:N    │             │
                                                 │ - id        │
                                                 │ - quantity  │
                                                 │ - price     │
                                                 │ - order_id  │
                                                 │ - book_id   │
                                                 └─────────────┘
```

#### 4. **Transaction Management**

Critical operations are wrapped in transactions:
- **Order Placement:** Atomically creates order, updates cart, reduces stock
- **Cart Operations:** Ensures consistency between cart and book availability
- **Payment Processing:** Rollback on failure to maintain data integrity

#### 5. **Exception Handling Architecture**

```
Controller → Service → Repository
     ↓           ↓          ↓
     └──────> Exception ───┘
                  ↓
       Global Exception Handler
                  ↓
       Standardized Error Response
       (HTTP Status + Message + Timestamp)
```


### Scalability Considerations

- **Stateless Design:** JWT tokens enable horizontal scaling
- **Database Connection Pooling:** HikariCP for efficient connection management
- **Async Processing:** Email notifications and analytics run asynchronously

---

## 🏗️ Project Structure

```text
src/main/java/com/bookstore/
├── controller/     # REST API controllers
├── dto/            # Data Transfer Objects
├── entity/         # JPA entities
├── exception/      # Custom exceptions and global handler
├── repository/     # Spring Data JPA repositories
├── security/       # Spring Security and JWT configuration
├── service/        # Business logic
└── specification/  # Dynamic query specifications
```

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a **pull request** or open an **issue**.

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 📧 Contact

For questions or feedback, please reach out to [venkatagiridhargarikipati@gmail.com](mailto:venkatagiridhargarikipati@gmail.com).
