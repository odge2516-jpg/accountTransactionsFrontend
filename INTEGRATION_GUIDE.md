# 前後端整合指南

這份文件說明如何將 Vue 前端與 Spring Boot 後端整合。

## 目前狀態

### 前端 (Vue)
- 目前使用 **localStorage** 模擬後端 API
- 所有資料儲存在瀏覽器本地

### 後端 (Spring Boot)
- 已建立完整的 RESTful API
- 使用 MySQL 資料庫
- 提供所有必要的端點

## 整合步驟

### 1. 啟動後端服務

```bash
cd backend
mvn spring-boot:run
```

後端將在 `http://localhost:8080` 運行

### 2. 修改前端 API 服務

需要修改 `src/services/api.js` 來調用實際的後端 API，而不是使用 localStorage。

#### 安裝 Axios（如果尚未安裝）

```bash
npm install axios
```

#### 替換 api.js 內容

建立新的 `src/services/apiWithBackend.js`：

```javascript
import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

// 配置 axios
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 註冊
export const registerUser = async (username, email, password) => {
  try {
    const response = await api.post('/auth/register', {
      username,
      email,
      password
    })
    return response.data
  } catch (error) {
    throw new Error(error.response?.data?.message || '註冊失敗')
  }
}

// 登入
export const loginUser = async (email, password) => {
  try {
    const response = await api.post('/auth/login', {
      email,
      password
    })
    return response.data
  } catch (error) {
    throw new Error(error.response?.data?.message || '登入失敗')
  }
}

// 存款
export const deposit = async (userId, amount, description) => {
  try {
    const response = await api.post(`/transactions/deposit?userId=${userId}`, {
      amount,
      description
    })
    return {
      transaction: response.data,
      newBalance: response.data.amount // 需要從後端獲取新餘額
    }
  } catch (error) {
    throw new Error(error.response?.data?.message || '存款失敗')
  }
}

// 提款
export const withdraw = async (userId, amount, description) => {
  try {
    const response = await api.post(`/transactions/withdraw?userId=${userId}`, {
      amount,
      description
    })
    return {
      transaction: response.data,
      newBalance: response.data.amount
    }
  } catch (error) {
    throw new Error(error.response?.data?.message || '提款失敗')
  }
}

// 轉帳
export const transfer = async (fromUserId, toEmail, amount, description) => {
  try {
    const response = await api.post(`/transactions/transfer?userId=${fromUserId}`, {
      toEmail,
      amount,
      description
    })
    return {
      outTransaction: response.data,
      inTransaction: null,
      newBalance: response.data.amount
    }
  } catch (error) {
    throw new Error(error.response?.data?.message || '轉帳失敗')
  }
}

// 獲取用戶的交易記錄
export const getUserTransactions = async (userId) => {
  try {
    const response = await api.get(`/transactions/user/${userId}`)
    return response.data
  } catch (error) {
    throw new Error(error.response?.data?.message || '獲取交易記錄失敗')
  }
}
```

### 3. 後端改進建議

為了更好地支援前端，建議後端添加以下功能：

#### 3.1 返回更新後的用戶資訊

修改 `TransactionController.java`，在交易後返回更新的用戶資訊：

```java
@PostMapping("/deposit")
public ResponseEntity<TransactionWithBalanceResponse> deposit(
        @RequestParam Long userId,
        @Valid @RequestBody TransactionRequest request) {
    TransactionResponse transaction = transactionService.deposit(userId, request);
    User user = userService.findById(userId);

    TransactionWithBalanceResponse response = new TransactionWithBalanceResponse(
        transaction,
        user.getBalance()
    );

    return new ResponseEntity<>(response, HttpStatus.CREATED);
}
```

#### 3.2 添加獲取當前用戶資訊的端點

在 `AuthController.java` 添加：

```java
@GetMapping("/user/{userId}")
public ResponseEntity<UserResponse> getUserInfo(@PathVariable Long userId) {
    User user = userService.findById(userId);
    UserResponse response = new UserResponse(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getBalance(),
        user.getCreatedAt()
    );
    return ResponseEntity.ok(response);
}
```

### 4. 環境變數配置

建立 `.env` 檔案來管理環境變數：

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

在 `vite.config.js` 中使用：

```javascript
export default defineConfig({
  plugins: [vue()],
  define: {
    'import.meta.env.VITE_API_BASE_URL': JSON.stringify(
      process.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
    )
  }
})
```

### 5. 錯誤處理

添加統一的錯誤處理：

```javascript
// src/services/errorHandler.js
export const handleApiError = (error) => {
  if (error.response) {
    // 伺服器返回錯誤
    switch (error.response.status) {
      case 400:
        return error.response.data.message || '請求參數錯誤'
      case 401:
        return '未授權，請重新登入'
      case 404:
        return '請求的資源不存在'
      case 500:
        return '伺服器錯誤，請稍後再試'
      default:
        return error.response.data.message || '未知錯誤'
    }
  } else if (error.request) {
    // 請求已發送但沒有收到回應
    return '無法連接到伺服器，請檢查網路連接'
  } else {
    // 其他錯誤
    return error.message
  }
}
```

### 6. 測試整合

#### 6.1 測試流程

1. 啟動 MySQL 資料庫
2. 啟動 Spring Boot 後端
3. 啟動 Vue 前端
4. 測試以下功能：
   - 註冊新用戶
   - 登入
   - 存款
   - 提款
   - 轉帳
   - 查看交易記錄

#### 6.2 測試工具

使用 Postman 或 cURL 測試後端 API：

```bash
# 註冊
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"測試用戶","email":"test@example.com","password":"password123"}'

# 登入
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# 存款
curl -X POST "http://localhost:8080/api/transactions/deposit?userId=1" \
  -H "Content-Type: application/json" \
  -d '{"amount":1000,"description":"測試存款"}'
```

### 7. 部署建議

#### 開發環境
- 前端：`http://localhost:5173`
- 後端：`http://localhost:8080`

#### 生產環境
- 前端：打包後部署到靜態伺服器
- 後端：部署到 Tomcat 或使用 Docker

```bash
# 前端打包
npm run build

# 後端打包
cd backend
mvn clean package
```

### 8. 安全性考慮

1. **添加 JWT 身份驗證**
2. **加密密碼**（使用 BCrypt）
3. **HTTPS 連接**
4. **CSRF 保護**
5. **輸入驗證**
6. **SQL 注入防護**（JPA 已提供）

### 9. 效能優化

1. **前端快取**
2. **後端快取**（Redis）
3. **資料庫索引**
4. **分頁查詢**
5. **壓縮回應**

### 10. 監控與日誌

- 使用 Spring Boot Actuator 監控後端健康狀態
- 添加日誌記錄（Logback）
- 前端錯誤追蹤（Sentry）

## 快速切換模式

目前專案支援兩種模式：

### 模式 1：純前端模式（當前）
- 使用 `src/services/api.js`
- 資料儲存在 localStorage

### 模式 2：前後端整合模式
- 將 `src/services/apiWithBackend.js` 重命名為 `api.js`
- 或在 `api.js` 中導入後端 API

```javascript
// 在 api.js 中切換
// import * from './apiLocal'  // 本地模式
import * from './apiWithBackend'  // 後端模式
```

## 總結

完成整合後，系統將具備：
- ✅ 真實的資料庫持久化
- ✅ 多用戶支援
- ✅ 更安全的資料處理
- ✅ 可擴展的架構
- ✅ 生產環境就緒
