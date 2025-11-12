<template>
  <nav class="navbar navbar-expand-lg navbar-custom">
    <div class="container-fluid">
      <router-link to="/dashboard" class="navbar-brand navbar-brand-custom">
        <i class="bi bi-bank me-2"></i>帳戶交易系統
      </router-link>

      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav ms-auto">
          <li class="nav-item">
            <router-link to="/dashboard" class="nav-link nav-link-custom">
              <i class="bi bi-house-door me-1"></i>首頁
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/deposit" class="nav-link nav-link-custom">
              <i class="bi bi-cash-coin me-1"></i>存款
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/withdraw" class="nav-link nav-link-custom">
              <i class="bi bi-wallet2 me-1"></i>提款
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/transfer" class="nav-link nav-link-custom">
              <i class="bi bi-arrow-left-right me-1"></i>轉帳
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/transactions" class="nav-link nav-link-custom">
              <i class="bi bi-list-ul me-1"></i>交易記錄
            </router-link>
          </li>
          <li class="nav-item">
            <span class="nav-link" style="color: #667eea; font-weight: bold;">
              <i class="bi bi-person-circle me-1"></i>{{ username }}
            </span>
          </li>
          <li class="nav-item">
            <a href="#" class="nav-link btn-logout" @click.prevent="handleLogout">
              <i class="bi bi-box-arrow-right me-1"></i>登出
            </a>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script>
import { inject } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'Navbar',
  setup() {
    const store = inject('store')
    const router = useRouter()

    const handleLogout = () => {
      store.logout()
      router.push('/login')
    }

    return {
      username: store.state.user?.username,
      handleLogout
    }
  }
}
</script>
