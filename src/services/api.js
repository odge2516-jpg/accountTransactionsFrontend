// 模擬 API 服務層 - 暫時使用 localStorage
// 後續可以替換成實際的 Spring Boot API 調用

const USERS_KEY = 'account_system_users'
const TRANSACTIONS_KEY = 'account_system_transactions'

// 獲取所有用戶
const getUsers = () => {
  const users = localStorage.getItem(USERS_KEY)
  return users ? JSON.parse(users) : []
}

// 保存所有用戶
const saveUsers = (users) => {
  localStorage.setItem(USERS_KEY, JSON.stringify(users))
}

// 獲取所有交易記錄
const getTransactions = () => {
  const transactions = localStorage.getItem(TRANSACTIONS_KEY)
  return transactions ? JSON.parse(transactions) : []
}

// 保存所有交易記錄
const saveTransactions = (transactions) => {
  localStorage.setItem(TRANSACTIONS_KEY, JSON.stringify(transactions))
}

// 註冊
export const registerUser = (username, email, password) => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const users = getUsers()

      // 檢查是否已存在相同 email
      if (users.some(u => u.email === email)) {
        reject(new Error('此 Email 已被註冊'))
        return
      }

      const newUser = {
        id: Date.now().toString(),
        username,
        email,
        balance: 0,
        createdAt: new Date().toISOString()
      }

      users.push(newUser)
      saveUsers(users)

      resolve(newUser)
    }, 500)
  })
}

// 登入
export const loginUser = (email, password) => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const users = getUsers()
      const user = users.find(u => u.email === email)

      if (!user) {
        reject(new Error('Email 或密碼錯誤'))
        return
      }

      resolve(user)
    }, 500)
  })
}

// 更新用戶餘額
const updateUserBalance = (userId, newBalance) => {
  const users = getUsers()
  const userIndex = users.findIndex(u => u.id === userId)

  if (userIndex !== -1) {
    users[userIndex].balance = newBalance
    saveUsers(users)
  }
}

// 存款
export const deposit = (userId, amount, description) => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (amount <= 0) {
        reject(new Error('金額必須大於 0'))
        return
      }

      const users = getUsers()
      const user = users.find(u => u.id === userId)

      if (!user) {
        reject(new Error('用戶不存在'))
        return
      }

      const newBalance = user.balance + amount
      updateUserBalance(userId, newBalance)

      const transaction = {
        id: Date.now().toString(),
        userId,
        type: 'deposit',
        amount,
        date: new Date().toISOString(),
        description: description || '存款'
      }

      const transactions = getTransactions()
      transactions.push(transaction)
      saveTransactions(transactions)

      resolve({ transaction, newBalance })
    }, 500)
  })
}

// 提款
export const withdraw = (userId, amount, description) => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (amount <= 0) {
        reject(new Error('金額必須大於 0'))
        return
      }

      const users = getUsers()
      const user = users.find(u => u.id === userId)

      if (!user) {
        reject(new Error('用戶不存在'))
        return
      }

      if (user.balance < amount) {
        reject(new Error('餘額不足'))
        return
      }

      const newBalance = user.balance - amount
      updateUserBalance(userId, newBalance)

      const transaction = {
        id: Date.now().toString(),
        userId,
        type: 'withdraw',
        amount,
        date: new Date().toISOString(),
        description: description || '提款'
      }

      const transactions = getTransactions()
      transactions.push(transaction)
      saveTransactions(transactions)

      resolve({ transaction, newBalance })
    }, 500)
  })
}

// 轉帳
export const transfer = (fromUserId, toEmail, amount, description) => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (amount <= 0) {
        reject(new Error('金額必須大於 0'))
        return
      }

      const users = getUsers()
      const fromUser = users.find(u => u.id === fromUserId)
      const toUser = users.find(u => u.email === toEmail)

      if (!fromUser) {
        reject(new Error('用戶不存在'))
        return
      }

      if (!toUser) {
        reject(new Error('收款帳戶不存在'))
        return
      }

      if (fromUser.email === toEmail) {
        reject(new Error('不能轉帳給自己'))
        return
      }

      if (fromUser.balance < amount) {
        reject(new Error('餘額不足'))
        return
      }

      // 扣款
      updateUserBalance(fromUserId, fromUser.balance - amount)

      // 入帳
      updateUserBalance(toUser.id, toUser.balance + amount)

      const outTransaction = {
        id: Date.now().toString(),
        userId: fromUserId,
        type: 'transfer-out',
        amount,
        date: new Date().toISOString(),
        description: description || '轉帳',
        toUser: toUser.email
      }

      const inTransaction = {
        id: (Date.now() + 1).toString(),
        userId: toUser.id,
        type: 'transfer-in',
        amount,
        date: new Date().toISOString(),
        description: description || '轉帳',
        fromUser: fromUser.email
      }

      const transactions = getTransactions()
      transactions.push(outTransaction, inTransaction)
      saveTransactions(transactions)

      resolve({
        outTransaction,
        inTransaction,
        newBalance: fromUser.balance - amount
      })
    }, 500)
  })
}

// 獲取用戶的交易記錄
export const getUserTransactions = (userId) => {
  const transactions = getTransactions()

  return transactions
    .filter(t => t.userId === userId)
    .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
}
