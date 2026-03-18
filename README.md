# Complaint Management System (Spring Boot)

A modern backend system for managing complaints with secure authentication, role-based access, and real-time updates.

## 🚀 Features
- User Registration & Login (JWT based authentication)
- Role-Based Access (USER, ADMIN, SUPER_ADMIN)
- Complaint Create, Track, Update
- Email Notifications (status updates, verification)
- Secure OTP generation (SecureRandom)
- WebSocket support (real-time updates)
- Profile Management

🛠 Tech Stack
- Java
- Spring Boot
- Spring Security
- JPA / Hibernate
- PostgreSQL (or MySQL)
- JWT Authentication
- WebSocket
- Java Mail (SimpleMail)

🧠 Architecture
- Feature-based structure (auth, user, complaint)
- DTO-based request/response handling
- Global Exception Handling
- Transaction management (@Transactional)

📌 API Overview
- Auth APIs (Register, Login)
- User APIs (Profile, Update)
- Complaint APIs (Create, Status Update, Track)

⚙️ Setup
```bash
git clone https://github.com/p-a-n-k-a-j/complaint-management-system
