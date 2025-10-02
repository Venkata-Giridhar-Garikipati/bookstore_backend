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

- `GET /api/admin/analytics/dashboard` – Get dashboard analytics
- `PUT /api/admin/orders/{id}/status` – Update order status

> The complete API documentation is available in the Postman collection.

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

For questions or feedback, please reach out to [giridhar4249@gmail.com](mailto:giridhar4249@gmail.com).
