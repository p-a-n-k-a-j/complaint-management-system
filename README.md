# Complaint Management System

A production-inspired Complaint Management System built with **Spring Boot**, designed around secure authentication, role-based access control, complaint lifecycle management, asynchronous event processing, and real-time notifications.

The project demonstrates modern backend engineering practices including:

* JWT Authentication & Refresh Token Rotation
* Spring Security
* Role-Based Access Control (RBAC)
* Soft Delete Account Recovery
* Event-Driven Email Notifications
* Real-Time WebSocket Updates
* Cloudinary File Management
* Dockerized Deployment
* Feature-Based Architecture
* Global Exception Handling
* Pagination & Filtering
* Transaction Management

---

# Architecture Overview

```text
Client
   |
   v
Spring Security Filter Chain
   |
   v
JWT Authentication Filter
   |
   v
Controllers
   |
   v
Services
   |
   v
Repositories
   |
   v
MySQL Database
```

---

# Core Modules

```text
Authentication
│
├── Registration
├── Login
├── OTP Verification
├── Forgot Password
├── Refresh Token
└── Account Recovery

User Profile
│
├── Profile Management
├── Image Upload
├── Image Removal
└── Cloudinary Integration

Complaint Management
│
├── Create Complaint
├── Update Complaint
├── Track Complaint
├── Complaint History
├── Attachments
└── Complaint Analytics

Administration
│
├── Complaint Assignment
├── Complaint Monitoring
├── User Monitoring
├── Status Management
└── Admin Management

Notification System
│
├── Async Email Events
├── Event Listeners
└── WebSocket Notifications
```

---

# Authentication Flow

```text
User
 |
 v
Send OTP
 |
 v
Verify OTP
 |
 v
Registration Request
 |
 v
Check Existing User
 |
 +----------------------------+
 |                            |
 | ACTIVE                     |
 |                            |
 +--> Registration Blocked
 |
 | DELETED
 |
 +--> Restore Existing Account
 |
 | BLOCKED / SUSPENDED
 |
 +--> Registration Blocked
 |
 v
Password Hashing
 |
 v
Create User
 |
 v
Create User Profile
 |
 v
Publish Registration Event
 |
 v
Async Welcome Email
```

---

# Soft Delete Recovery

One of the key features of this project is **Account Recovery using Soft Delete**.

Instead of permanently deleting user records:

```text
ACTIVE
   |
DELETE
   |
   v
DELETED
```

When the same user attempts registration again:

```text
User Exists?
      |
      v
Status = DELETED
      |
      v
Restore Existing Account
      |
      v
Update User Data
```

This preserves historical records while allowing account recovery.

---

# Login Flow

```text
User Login
    |
    v
Email Exists?
    |
    +--> No -> Invalid Credentials
    |
    v
Account Deleted?
    |
    +--> Yes -> Account Deleted
    |
    v
Account Active?
    |
    +--> No -> Account Blocked/Suspended
    |
    v
Password Match?
    |
    +--> No -> Invalid Credentials
    |
    v
Generate Tokens
    |
    +--> Access Token
    |
    +--> Refresh Token
    |
    v
Store Refresh Token
    |
    v
Return Tokens
```

---

# JWT Strategy

### Access Token

Contains:

* Email
* Roles
* Authorities

Purpose:

* API Authorization

Expiration:

* Short Lived

---

### Refresh Token

Contains:

* Email

Purpose:

* Generate New Access Token

Expiration:

* Long Lived

Stored in:

* Database

Security:

* Refresh Token Rotation
* Refresh Token Revocation

---

# Refresh Token Flow

```text
Refresh Request
      |
      v
Validate Token Type
      |
      v
Load User
      |
      v
Compare DB Refresh Token
      |
      +--> Invalid
      |
      v
Generate New Tokens
      |
      v
Replace Old Refresh Token
      |
      v
Return New Tokens
```

---

# Entity Relationship Diagram

```text
User
 |
 | One-To-One
 |
 v
UserProfile

User
 |
 | One-To-Many
 |
 v
Complaint

Complaint
 |
 | One-To-Many
 |
 v
ComplaintLog

Complaint
 |
 | One-To-Many
 |
 v
ComplaintAttachment
```

---

# Complaint Lifecycle

```text
User Creates Complaint
           |
           v
Ticket Generated
           |
           v
Complaint Submitted
           |
           v
Assigned To Admin
           |
           v
In Progress
           |
           v
Resolved
           |
           v
History Preserved
```

---

# Complaint Tracking

Each complaint maintains:

* Unique Ticket ID
* Complaint Logs
* Status History
* Admin Actions
* Attachments
* Resolution Information

---

# Real-Time Notifications

WebSocket notifications are used for:

### Complaint Assignment

```text
Super Admin
     |
Assign Complaint
     |
     +--> Notify Assigned Admin
     |
     +--> Notify User
```

### Complaint Status Change

```text
Admin
   |
Update Status
   |
   v
User Notification
```

---

# File Management

Cloudinary is used for:

### User Profile Images

* Upload Image
* Update Image
* Remove Image

### Complaint Attachments

* Upload Evidence
* Replace Attachments
* Remove Attachments

---

# Event Driven Email System

The project uses:

* ApplicationEventPublisher
* Event Listeners
* Async Processing

Purpose:

```text
User Registration
      |
Publish Event
      |
      v
Background Email Processing
      |
      v
Immediate API Response
```

The user does not wait for email delivery before receiving the API response.

---

# Security Features

* Spring Security
* JWT Authentication
* Refresh Token Rotation
* Password Encryption
* Role-Based Authorization
* Endpoint Protection
* Method Level Security
* Secure OTP Verification

---

# Role Hierarchy

```text
SUPER_ADMIN
      |
      +--> Manage Users
      |
      +--> Manage Admins
      |
      +--> Assign Complaints
      |
      +--> Monitor System

ADMIN
      |
      +--> Handle Assigned Complaints
      |
      +--> Update Complaint Status

USER
      |
      +--> Create Complaint
      |
      +--> Track Complaint
      |
      +--> Manage Profile
```

---

# Branching Strategy

Feature-based branching strategy was followed throughout development.

```text
main

├── feature/auth
├── feature/user-profile
├── feature/complaint
├── feature/async-events
├── feature/global-exception-handler
└── feature/docker-setup
```

This structure helps isolate development work and keeps the main branch stable.

---

# Technology Stack

### Backend

* Java
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate

### Database

* MySQL

### Authentication

* JWT
* Refresh Tokens

### Real-Time Communication

* WebSocket

### Cloud Storage

* Cloudinary

### DevOps

* Docker
* Docker Compose

### Email

* Java Mail
* Async Event Processing

---

# API Documentation

Detailed API documentation will be added here.

```text
Authentication APIs
User Profile APIs
Complaint APIs
Admin APIs
Super Admin APIs
Notification APIs
```

---

# Future Enhancements

* Redis Caching
* Background Complaint Analytics
* Scheduled Reports
* Audit Dashboard
* API Rate Limiting
* Monitoring & Metrics
* CI/CD Pipeline

---

## Developed By

**Pankaj Tirdiya**

Java Backend Developer

GitHub: https://github.com/p-a-n-k-a-j
