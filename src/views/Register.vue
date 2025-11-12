<template>
  <div class="auth-container">
    <div class="auth-card">
      <h2><i class="bi bi-person-plus me-2"></i>註冊新帳戶</h2>

      <div v-if="error" class="alert alert-danger" role="alert">
        {{ error }}
      </div>

      <form @submit.prevent="handleSubmit">
        <div class="mb-3">
          <label for="username" class="form-label">用戶名稱</label>
          <input
            type="text"
            class="form-control"
            id="username"
            v-model="username"
            required
            placeholder="請輸入用戶名稱"
          />
        </div>

        <div class="mb-3">
          <label for="email" class="form-label">Email</label>
          <input
            type="email"
            class="form-control"
            id="email"
            v-model="email"
            required
            placeholder="請輸入 Email"
          />
        </div>

        <div class="mb-3">
          <label for="password" class="form-label">密碼</label>
          <input
            type="password"
            class="form-control"
            id="password"
            v-model="password"
            required
            placeholder="請輸入密碼（至少 6 個字元）"
          />
        </div>

        <div class="mb-3">
          <label for="confirmPassword" class="form-label">確認密碼</label>
          <input
            type="password"
            class="form-control"
            id="confirmPassword"
            v-model="confirmPassword"
            required
            placeholder="請再次輸入密碼"
          />
        </div>

        <button type="submit" class="btn btn-primary w-100 mb-3" :disabled="loading">
          <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
          {{ loading ? '註冊中...' : '註冊' }}
        </button>
      </form>

      <div class="text-center">
        <p class="mb-0">
          已經有帳戶？
          <router-link to="/login" class="text-decoration-none fw-bold">立即登入</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, inject } from 'vue'
import { useRouter } from 'vue-router'
import { registerUser } from '../services/api'

export default {
  name: 'Register',
  setup() {
    const username = ref('')
    const email = ref('')
    const password = ref('')
    const confirmPassword = ref('')
    const error = ref('')
    const loading = ref(false)

    const store = inject('store')
    const router = useRouter()

    const handleSubmit = async () => {
      error.value = ''

      // 驗證
      if (password.value !== confirmPassword.value) {
        error.value = '密碼與確認密碼不符'
        return
      }

      if (password.value.length < 6) {
        error.value = '密碼長度至少需要 6 個字元'
        return
      }

      loading.value = true

      try {
        const user = await registerUser(username.value, email.value, password.value)
        store.setUser(user)
        router.push('/dashboard')
      } catch (err) {
        error.value = err.message
      } finally {
        loading.value = false
      }
    }

    return {
      username,
      email,
      password,
      confirmPassword,
      error,
      loading,
      handleSubmit
    }
  }
}
</script>
