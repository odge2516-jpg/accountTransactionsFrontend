# MySQL 資料庫配置指南

## 1. 安裝 MySQL

### Windows
1. 下載 MySQL Installer：https://dev.mysql.com/downloads/installer/
2. 執行安裝程式
3. 選擇 "Developer Default" 安裝類型
4. 設定 root 密碼

### macOS
```bash
brew install mysql
brew services start mysql
```

### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install mysql-server
sudo systemctl start mysql
sudo systemctl enable mysql
```

## 2. 建立資料庫

### 方法一：使用 MySQL Command Line

1. 登入 MySQL：
```bash
mysql -u root -p
```

2. 建立資料庫：
```sql
CREATE DATABASE account_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. 確認資料庫已建立：
```sql
SHOW DATABASES;
```

4. 退出 MySQL：
```sql
EXIT;
```

### 方法二：使用 MySQL Workbench

1. 開啟 MySQL Workbench
2. 連接到本地 MySQL 伺服器
3. 點擊 "Create a new schema" 圖標
4. 輸入資料庫名稱：`account_system`
5. 選擇字符集：`utf8mb4`
6. 選擇排序規則：`utf8mb4_unicode_ci`
7. 點擊 "Apply"

## 3. 配置後端連接

編輯 `backend/src/main/resources/application.properties`：

```properties
# 修改以下配置
spring.datasource.url=jdbc:mysql://localhost:3306/account_system?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Taipei
spring.datasource.username=root
spring.datasource.password=your_password  # 改為你的 MySQL 密碼
```

## 4. 資料表結構

Spring Boot 會自動建立以下資料表：

### users 表
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL
);
```

### transactions 表
```sql
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    description VARCHAR(500),
    to_user_email VARCHAR(255),
    from_user_email VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## 5. 驗證配置

### 啟動後端服務
```bash
cd backend
mvn spring-boot:run
```

### 檢查日誌
如果配置正確，你會看到類似以下的日誌：
```
Hibernate: create table users ...
Hibernate: create table transactions ...
Started AccountSystemApplication in X seconds
```

### 使用 MySQL Workbench 查看
1. 打開 MySQL Workbench
2. 連接到資料庫
3. 展開 `account_system` 資料庫
4. 確認 `users` 和 `transactions` 表已建立

## 6. 常見問題

### 問題 1：無法連接到 MySQL
**解決方案**：
- 確認 MySQL 服務正在運行
- 檢查用戶名和密碼是否正確
- 確認資料庫名稱是否正確

### 問題 2：時區錯誤
**解決方案**：
在連接字串中添加 `serverTimezone=Asia/Taipei`

### 問題 3：字符編碼問題
**解決方案**：
確保資料庫使用 UTF-8 編碼：
```sql
ALTER DATABASE account_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 問題 4：權限不足
**解決方案**：
```sql
GRANT ALL PRIVILEGES ON account_system.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

## 7. 測試資料

可以手動插入測試資料：

```sql
USE account_system;

-- 插入測試用戶
INSERT INTO users (username, email, password, balance, created_at)
VALUES ('測試用戶', 'test@example.com', 'password123', 10000.00, NOW());

-- 查詢用戶
SELECT * FROM users;
```

## 8. 安全建議

1. **不要在生產環境使用預設密碼**
2. **啟用密碼加密**（當前版本未實作）
3. **限制資料庫訪問權限**
4. **定期備份資料庫**
5. **使用 SSL 連接**

## 9. 備份與還原

### 備份資料庫
```bash
mysqldump -u root -p account_system > backup.sql
```

### 還原資料庫
```bash
mysql -u root -p account_system < backup.sql
```

## 10. 開發工具推薦

- **MySQL Workbench**：官方圖形化管理工具
- **DBeaver**：跨平台資料庫管理工具
- **phpMyAdmin**：網頁版資料庫管理工具
