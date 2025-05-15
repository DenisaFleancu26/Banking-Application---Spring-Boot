# Banking Application

A **Spring Boot** based banking application featuring secure user management and core banking operations.

---

## Features

- **User registration & login** with Spring Security and JWT authentication
- **Account operations**:
    - Credit (deposit) money
    - Debit (withdraw) money
    - Transfer funds between accounts
- **Account statement generation** with PDF export (using iTextPDF)
- **Account statement sent via email** using Spring Mail
  ([Download Account Statement PDF](./src/main/resources/static/file/BankStatement.pdf))
- **API documentation** available through Swagger UI (springdoc-openapi)

---

## Technologies & Dependencies

- **Spring Boot 3.4.5**
- **Spring Data JPA** (MySQL database)
- **Spring Security** with JWT for authentication
- **Spring Validation** for input validation
- **Spring Mail** for sending emails
- **iTextPDF** for PDF generation
- **springdoc-openapi** for API documentation
- **Lombok** for boilerplate code reduction
- **MySQL Connector/J** as JDBC driver
- **JSON Web Token (jjwt)** for token handling

Full Maven dependencies list is included in the `pom.xml`.

---

## Usage

- Register a new user with required information including email, password, and personal details.
- Log in to receive a JWT token
- Use the token to authenticate requests for account credit, debit, transfer, and statement retrieval
- Account statements can be emailed in PDF format

---
