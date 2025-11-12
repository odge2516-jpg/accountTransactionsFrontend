<template>
  <div class="auth-container">
    <div class="auth-card">
      <h2><i class="bi bi-box-arrow-in-right me-2"></i>登入帳戶</h2>

      <div v-if="error" class="alert alert-danger" role="alert">
        {{ error }}
      </div>

      <form @submit.prevent="handleSubmit">
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
            placeholder="請輸入密碼"
          />
        </div>

        <button type="submit" class="btn btn-primary w-100 mb-3" :disabled="loading">
          <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
          {{ loading ? '登入中...' : '登入' }}
        </button>
      </form>

      <div class="text-center">
        <p class="mb-0">
          還沒有帳戶？
          <router-link to="/register" class="text-decoration-none fw-bold">立即註冊</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, inject } from 'vue'
import { useRouter } from 'vue-router'
import { loginUser } from '../services/api'

export default {
  name: 'Login',
  setup() {
    const email = ref('')
    const password = ref('')
    const error = ref('')
    const loading = ref(false)

    const store = inject('store')
    const router = useRouter()

    const handleSubmit = async () => {
      error.value = ''
      loading.value = true

      try {
        const user = await loginUser(email.value, password.value)
        store.setUser(user)
        router.push('/dashboard')
      } catch (err) {
        error.value = err.message
      } finally {
        loading.value = false
      }
    }

    return {
      email,
      password,
      error,
      loading,
      handleSubmit
    }
  }
}
</script>
