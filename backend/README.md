# 帳戶交易管理系統 - 後端 API

基於 Spring Boot 的帳戶交易管理系統後端 API

## 技術棧

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- MySQL 8.0
- Maven

## 功能特點

- 用戶註冊和登入
- 存款功能
- 提款功能
- 轉帳功能
- 交易歷史記錄查詢
- RESTful API 設計
- 異常處理
- 資料驗證

## 資料庫配置

### 建立資料庫

```sql
CREATE DATABASE account_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 配置檔案

編輯 `src/main/resources/application.properties`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/account_system
spring.datasource.username=root
spring.datasource.password=your_password
```

## 執行專案

### 使用 Maven

```bash
cd backend
mvn spring-boot:run
```

### 使用 Eclipse

1. 匯入專案：File -> Import -> Existing Maven Projects
2. 選擇 backend 目錄
3. 右鍵點擊專案 -> Run As -> Spring Boot App

## API 端點

### 認證相關

- `POST /api/auth/register` - 用戶註冊
- `POST /api/auth/login` - 用戶登入

### 交易相關

- `POST /api/transactions/deposit?userId={userId}` - 存款
- `POST /api/transactions/withdraw?userId={userId}` - 提款
- `POST /api/transactions/transfer?userId={userId}` - 轉帳
- `GET /api/transactions/user/{userId}` - 獲取用戶交易記錄

## 資料模型

### User (用戶)

- id: Long
- username: String
- email: String (unique)
- password: String
- balance: BigDecimal
- createdAt: LocalDateTime

### Transaction (交易)

- id: Long
- userId: Long (外鍵)
- type: Enum (DEPOSIT, WITHDRAW, TRANSFER_IN, TRANSFER_OUT)
- amount: BigDecimal
- description: String
- toUserEmail: String (僅轉帳時)
- fromUserEmail: String (僅轉帳時)
- createdAt: LocalDateTime

## 注意事項

1. 當前版本密碼未加密，生產環境應使用 BCrypt 或其他加密方式
2. 應添加身份驗證機制（JWT 等）
3. 建議添加交易鎖定機制防止併發問題
4. 應添加日誌記錄
5. 應添加單元測試和整合測試
