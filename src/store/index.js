import { reactive } from 'vue'

const store = {
  state: reactive({
    user: null,
    isAuthenticated: false
  }),

  setUser(user) {
    this.state.user = user
    this.state.isAuthenticated = !!user
    if (user) {
      localStorage.setItem('currentUser', JSON.stringify(user))
    }
  },

  updateBalance(newBalance) {
    if (this.state.user) {
      this.state.user.balance = newBalance
      localStorage.setItem('currentUser', JSON.stringify(this.state.user))
    }
  },

  logout() {
    this.state.user = null
    this.state.isAuthenticated = false
    localStorage.removeItem('currentUser')
  },

  loadUser() {
    const user = localStorage.getItem('currentUser')
    if (user) {
      this.state.user = JSON.parse(user)
      this.state.isAuthenticated = true
    }
  }
}

// 初始化時載入用戶資訊
store.loadUser()

export default {
  install(app) {
    app.config.globalProperties.$store = store
    app.provide('store', store)
  }
}
