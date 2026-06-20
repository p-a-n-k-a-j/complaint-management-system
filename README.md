# Complaint Management System

A modern Complaint Management System built using Spring Boot that enables secure complaint registration, tracking, assignment, and resolution through a role-based workflow.

The system provides authentication, complaint lifecycle management, real-time notifications, profile management, email communication, file attachments, and administrative monitoring capabilities.

Designed using a feature-based architecture, the application focuses on scalability, maintainability, and real-world workflow management.

---

## 🚀 Key Features

### 🔐 Authentication & Authorization

* JWT-based Authentication
* Access Token & Refresh Token mechanism
* Refresh Token Rotation
* Role-Based Access Control (RBAC)
* Secure Password Hashing
* Email Verification using OTP
* Forgot Password & Password Reset
* Account Status Validation

Supported Account Statuses:

* ACTIVE
* SUSPENDED
* BLOCKED
* DELETED

---

### 👤 User Management

Users can:

* Register and verify their email
* Login securely
* Update profile information
* Upload profile image
* Remove profile image
* View own profile
* Delete account
* Recover previously deleted account through re-registration

Profile images are managed using Cloudinary.

---

### 📩 Complaint Management

Users can:

* Create complaints
* Update complaints
* Delete complaints
* View complaint details
* Search complaint by Ticket ID
* Track complaint status
* View complaint history
* View complaint logs
* Upload attachments
* Update attachments
* Delete attachments
* View complaint statistics

Supported Complaint Features:

* Complaint Categories
* Priority Levels
* Ticket Tracking
* Complaint History
* Status Tracking
* Remarks
* Attachments

---

### 👨‍💼 Admin Operations

Admins can:

* View assigned complaints
* Update complaint status
* Add remarks
* View workload statistics
* View today's assigned updates

---

### 👑 Super Admin Operations

Super Admins can:

* View all complaints
* Assign complaints to admins
* Reassign complaints
* Monitor user activity
* Monitor admin performance
* Manage user account status
* Convert users into admins
* Register new admins
* View user statistics
* View admin statistics

---

### 🔔 Real-Time Notifications

The application uses WebSocket-based communication for real-time updates.

Notifications are triggered when:

* Complaint status changes
* Complaint gets assigned
* Complaint gets reassigned
* Administrative actions occur

This allows users and administrators to receive updates instantly without refreshing the application.

---

### 📧 Email Notification System

The application provides automated email communication for:

* Email Verification
* Welcome Emails
* Password Reset
* Account Status Updates
* Admin Registration
* Account Reactivation

Email processing is performed asynchronously using:

* ApplicationEventPublisher
* Event Listeners
* @Async Processing

This prevents users from waiting for email operations to complete before receiving responses.

---

## 🏗 System Architecture

The project follows a feature-based architecture.

Modules include:

* Authentication
* User Profile
* Complaints
* Email Events
* Notifications
* Security
* Exception Handling

Benefits:

* Better separation of concerns
* Easier maintenance
* Scalable code organization
* Independent feature development

---

## 🔄 Authentication Flow

### Registration

User Registration

↓

Email OTP Verification

↓

Account Status Validation

↓

Password Encryption

↓

User Creation

↓

User Profile Creation

↓

Welcome Email Event

↓

Registration Successful

### Login

Email + Password

↓

Credential Validation

↓

Account Status Validation

↓

Access Token Generation

↓

Refresh Token Generation

↓

Refresh Token Persistence

↓

Authentication Successful

### Refresh Token Flow

Refresh Token

↓

Token Validation

↓

Database Verification

↓

Refresh Token Rotation

↓

New Access Token

↓

New Refresh Token

---

## 📋 Complaint Lifecycle

Complaint Created

↓

Status: PENDING

↓

Assigned by Super Admin

↓

Handled by Admin

↓

Status Updates

↓

Complaint Logs Generated

↓

Real-Time Notifications

↓

RESOLVED

---

## 🗄 Database Design

### Entity Relationships

User

↔ One-to-One ↔ UserProfile

User

↔ One-to-Many ↔ Complaint

Complaint

↔ One-to-Many ↔ ComplaintLog

Complaint

↔ One-to-Many ↔ ComplaintAttachment

---

## ☁ File Management

Profile images and complaint attachments are stored using Cloudinary.

Stored Metadata:

* File URL
* Public ID

Supported Operations:

* Upload
* Update
* Delete

---

## ⚙️ Technical Highlights

* Spring Security
* JWT Authentication
* Refresh Token Management
* WebSocket Integration
* Cloudinary Integration
* Async Event Processing
* DTO-based Communication
* Transaction Management
* Global Exception Handling
* Pagination Support
* Validation Support
* Role-Based Access Control

---

## 🌳 Git Workflow

The project follows a feature branch strategy.

### Branches

#### main

Production-ready code.

#### feature/auth

Authentication and authorization implementation.

#### feature/user-profile

User profile management.

#### feature/complaint

Complaint handling and workflow implementation.

#### feature/async-events

Asynchronous email processing and event-driven communication.

#### feature/global-exception-handler

Centralized exception handling.

#### feature/docker-setup

Docker and Docker Compose configuration.

---

## 🛠 Tech Stack

### Backend

* Java
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate

### Database

* PostgreSQL / MySQL

### Authentication

* JWT
* Refresh Tokens

### Real-Time Communication

* WebSocket

### File Storage

* Cloudinary

### Email Service

* Java Mail
* SMTP

### DevOps

* Docker
* Docker Compose

---

## 🔮 Future Enhancements

* Redis Integration for OTP & Caching
* Background Email Queue Processing
* Dedicated Chat System
* Audit Logging
* Analytics Dashboard
* Rate Limiting
* API Monitoring
* Microservice Migration

---

## 👨‍💻 Developer

Pankaj Tirdiya

Java Backend Developer

GitHub:
https://github.com/p-a-n-k-a-j
