# 帳戶交易管理系統 - 個人專題

一個完整的帳戶交易管理系統，包含前端和後端。

## 專案結構

```
accountTransactionsFrontend/
├── src/                    # 前端源代碼 (Vue 3)
│   ├── components/         # Vue 組件
│   ├── views/              # 頁面組件
│   ├── router/             # 路由配置
│   ├── services/           # API 服務
│   ├── store/              # 狀態管理
│   └── assets/             # 靜態資源
├── backend/                # 後端源代碼 (Spring Boot)
│   └── src/main/java/      # Java 源代碼
└── package.json            # 前端依賴配置
```

## 功能特點

- ✅ 用戶註冊/登入
- ✅ 帳戶儀表板（顯示餘額和帳戶資訊）
- ✅ 存款功能
- ✅ 提款功能
- ✅ 轉帳功能
- ✅ 交易歷史記錄查詢

## 技術棧

### 前端
- Vue 3 (Composition API)
- Vue Router 4
- Bootstrap 5
- Vite

### 後端
- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- MySQL 8.0
- Maven

## 快速開始

### 前置需求

- Node.js 16+
- Java 17+
- MySQL 8.0+
- Maven 3.6+

### 1. 設置資料庫

```sql
CREATE DATABASE account_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 啟動後端

```bash
cd backend
# 修改 src/main/resources/application.properties 中的資料庫配置
mvn spring-boot:run
```

後端將在 `http://localhost:8080` 啟動

### 3. 啟動前端

```bash
# 在專案根目錄
npm install
npm run dev
```

前端將在 `http://localhost:5173` 啟動

## 開發環境

- **IDE 建議**：
  - 前端：Visual Studio Code
  - 後端：Eclipse / IntelliJ IDEA
- **Git 工具**：GitKraken

## API 文檔

詳見 `backend/README.md`

## 專案截圖

### 登入頁面
用戶可以使用 Email 和密碼登入系統

### 註冊頁面
新用戶可以註冊帳戶

### 帳戶儀表板
顯示用戶餘額和快速操作按鈕

### 交易功能
支援存款、提款和轉帳操作

### 交易記錄
查看所有交易歷史記錄

## 注意事項

1. 當前版本使用簡單的密碼驗證，生產環境應使用加密
2. 建議添加 JWT 身份驗證
3. 建議添加交易鎖定機制
4. 應添加更多的輸入驗證和安全措施

## 作者

個人專題作品

## 授權

MIT License
