# ElderCare Backend

> Backend service for the **ElderCare** system, built with **Spring Boot** using a **Feature-based Architecture** to provide a scalable, maintainable, and collaborative codebase.

---

# Table of Contents

* Overview
* Technology Stack
* Project Structure
* Architecture
* Request Flow
* Folder Description
* Module Structure
* Security
* Development Convention
* Getting Started
* Future Modules

---

# Overview

ElderCare Backend được xây dựng theo **Feature-based Architecture**, trong đó mỗi nghiệp vụ (Feature) được tách thành một module độc lập.

Kiến trúc này giúp:

* Dễ mở rộng (Scalable)
* Dễ bảo trì (Maintainable)
* Giảm xung đột khi nhiều thành viên cùng phát triển
* Tăng khả năng tái sử dụng mã nguồn
* Quản lý source code rõ ràng theo từng nghiệp vụ

---

# Technology Stack

| Technology        | Description                    |
| ----------------- | ------------------------------ |
| Java 21           | Programming Language           |
| Spring Boot       | Backend Framework              |
| Spring Data JPA   | ORM Framework                  |
| Spring Security   | Authentication & Authorization |
| JWT               | Token-based Authentication     |
| Maven             | Dependency Management          |
| PostgreSQL        | Database                       |
| Lombok            | Reduce Boilerplate Code        |
| Swagger / OpenAPI | API Documentation              |

---

# Project Structure

```text
backend
│
├── src
│   └── main
│       ├── java
│       │   └── com.eldercare
│       │
│       ├── common
│       │   ├── constants
│       │   ├── enums
│       │   ├── response
│       │   └── utils
│       │
│       ├── config
│       │
│       ├── exception
│       │
│       ├── modules
│       │   ├── auth
│       │   │   ├── controller
│       │   │   ├── dto
│       │   │   ├── mapper
│       │   │   ├── service
│       │   │   │   └── impl
│       │   │   └── test
│       │   │
│       │   ├── user
│       │   │   ├── controller
│       │   │   ├── dto
│       │   │   ├── entity
│       │   │   ├── mapper
│       │   │   ├── repository
│       │   │   ├── service
│       │   │   │   └── impl
│       │   │   └── test
│       │   │
│       │   └── ...
│       │
│       ├── security
│       │   ├── JwtAuthenticationFilter.java
│       │   ├── JwtTokenProvider.java
│       │   └── SecurityConfig.java
│       │
│       └── BackendApplication.java
│
├── resources
└── test
```

---

# Architecture

Dự án sử dụng **Feature-based Architecture**.

Mỗi feature được tổ chức thành một module độc lập, bao gồm đầy đủ các thành phần cần thiết để xử lý một nghiệp vụ.

Ví dụ:

```text
modules
└── user
    ├── controller
    ├── dto
    ├── entity
    ├── mapper
    ├── repository
    ├── service
    └── test
```

Mỗi module có thể được phát triển gần như độc lập với các module khác.

---

# Request Flow

Một HTTP Request sẽ được xử lý theo luồng sau:

```text
Client
   │
   ▼
Controller
   │
   ▼
Service
   │
   ▼
Repository
   │
   ▼
Database
```

Sau khi truy vấn dữ liệu:

```text
Database
   │
   ▼
Repository
   │
   ▼
Service
   │
   ▼
Mapper
   │
   ▼
Response DTO
   │
   ▼
Controller
   │
   ▼
Client
```

---

# Folder Description

## common

Chứa các thành phần dùng chung cho toàn bộ hệ thống.

Ví dụ:

* Constants
* Enums
* Common Response
* Utility Classes

---

## config

Chứa các lớp cấu hình của Spring Boot.

Ví dụ:

* Swagger Configuration
* CORS Configuration
* Bean Configuration

---

## exception

Quản lý Exception toàn hệ thống.

Ví dụ:

* Global Exception Handler
* Custom Exception
* Error Response

---

## modules

Chứa toàn bộ các nghiệp vụ của hệ thống.

Mỗi thư mục trong `modules` tương ứng với một chức năng độc lập.

Ví dụ:

* auth
* user
* appointment
* patient
* doctor
* notification

---

## security

Quản lý toàn bộ cơ chế xác thực và phân quyền.

Bao gồm:

### SecurityConfig

* Cấu hình Spring Security
* Cấu hình Authorization
* Password Encoder
* Authentication Manager

### JwtAuthenticationFilter

* Đọc JWT từ Request Header
* Xác thực Token
* Đưa thông tin User vào Security Context

### JwtTokenProvider

* Sinh JWT
* Kiểm tra JWT
* Lấy thông tin User từ JWT

---

# Module Structure

Mỗi module đều có cấu trúc giống nhau.

## controller

Tiếp nhận HTTP Request và trả Response.

Không chứa Business Logic.

---

## dto

Đối tượng trao đổi dữ liệu giữa Client và Server.

Bao gồm:

* Request DTO
* Response DTO

---

## entity

Mapping với các bảng trong Database.

---

## repository

Làm việc với Database thông qua Spring Data JPA.

Chỉ thực hiện:

* CRUD
* Query
* Pagination

Không chứa Business Logic.

---

## service

Chứa Business Logic của module.

Thực hiện:

* Validate dữ liệu
* Xử lý nghiệp vụ
* Transaction
* Gọi Repository

---

## service/impl

Chứa phần hiện thực của các interface Service.

Ví dụ:

```text
UserService
        │
        ▼
UserServiceImpl
```

Điều này giúp:

* Dễ mở rộng
* Dễ Unit Test
* Tuân theo Dependency Injection

---

## mapper

Chuyển đổi dữ liệu giữa:

* DTO ↔ Entity

Có thể sử dụng:

* MapStruct
* ModelMapper
* Manual Mapping

---

## test

Chứa Unit Test hoặc Integration Test của module.

Ví dụ:

* UserServiceTest
* UserControllerTest

---

# Authentication Flow

```text
Client

↓

POST /login

↓

AuthController

↓

AuthService

↓

Validate Username & Password

↓

Generate JWT

↓

Return Access Token
```

Các request tiếp theo:

```text
Client

↓

Authorization: Bearer <JWT>

↓

JwtAuthenticationFilter

↓

Validate JWT

↓

Security Context

↓

Controller

↓

Service

↓

Repository

↓

Database
```

---

# Development Convention

* Không viết Business Logic trong Controller.
* Repository chỉ thao tác Database.
* Service xử lý toàn bộ nghiệp vụ.
* DTO chỉ dùng để truyền dữ liệu.
* Mapper chịu trách nhiệm chuyển đổi dữ liệu.
* Mỗi feature phải được đặt trong thư mục `modules`.
* Không truy cập trực tiếp Repository từ Controller.
* Mỗi module nên độc lập và hạn chế phụ thuộc vào module khác.

---

# Getting Started

## Clone project

```bash
git clone <repository-url>
```

## Move to backend

```bash
cd backend
```

## Install dependencies

```bash
mvn clean install
```

## Run project

```bash
mvn spring-boot:run
```

Hoặc chạy trực tiếp class:

```
BackendApplication.java
```

---

# Future Modules

Khi hệ thống phát triển, chỉ cần thêm module mới vào thư mục `modules`.

Ví dụ:

```text
modules
├── auth
├── user
├── patient
├── doctor
├── appointment
├── medical-record
├── notification
├── payment
├── report
└── ...
```

Mỗi module vẫn tuân theo cùng một cấu trúc, giúp toàn bộ dự án thống nhất và dễ bảo trì.

---

# Summary

ElderCare Backend sử dụng **Feature-based Architecture** kết hợp với **Spring Boot** nhằm xây dựng một hệ thống có cấu trúc rõ ràng, dễ mở rộng và phù hợp cho quá trình phát triển theo nhóm.

Kiến trúc này giúp mỗi thành viên có thể làm việc trên một module riêng biệt, giảm xung đột khi merge code, đồng thời đảm bảo tính nhất quán và khả năng bảo trì lâu dài của dự án.
