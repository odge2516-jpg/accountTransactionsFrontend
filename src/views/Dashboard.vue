<template>
  <div>
    <Navbar />
    <div class="dashboard-container">
      <div class="container">
        <!-- 餘額卡片 -->
        <div class="balance-card">
          <h2><i class="bi bi-wallet2 me-2"></i>我的帳戶</h2>
          <p class="mb-1">用戶名稱：{{ user?.username }}</p>
          <p class="mb-1">Email：{{ user?.email }}</p>
          <div class="balance-amount">NT$ {{ formatAmount(user?.balance) }}</div>
        </div>

        <!-- 功能卡片 -->
        <div class="row g-4">
          <div class="col-md-6 col-lg-3">
            <div class="card transaction-card h-100" @click="goTo('/deposit')">
              <div class="card-body text-center">
                <div class="transaction-icon text-success">
                  <i class="bi bi-cash-coin"></i>
                </div>
                <h5 class="card-title">存款</h5>
                <p class="card-text text-muted">將資金存入您的帳戶</p>
              </div>
            </div>
          </div>

          <div class="col-md-6 col-lg-3">
            <div class="card transaction-card h-100" @click="goTo('/withdraw')">
              <div class="card-body text-center">
                <div class="transaction-icon text-danger">
                  <i class="bi bi-wallet2"></i>
                </div>
                <h5 class="card-title">提款</h5>
                <p class="card-text text-muted">從您的帳戶提取資金</p>
              </div>
            </div>
          </div>

          <div class="col-md-6 col-lg-3">
            <div class="card transaction-card h-100" @click="goTo('/transfer')">
              <div class="card-body text-center">
                <div class="transaction-icon text-info">
                  <i class="bi bi-arrow-left-right"></i>
                </div>
                <h5 class="card-title">轉帳</h5>
                <p class="card-text text-muted">轉帳給其他用戶</p>
              </div>
            </div>
          </div>

          <div class="col-md-6 col-lg-3">
            <div class="card transaction-card h-100" @click="goTo('/transactions')">
              <div class="card-body text-center">
                <div class="transaction-icon text-warning">
                  <i class="bi bi-list-ul"></i>
                </div>
                <h5 class="card-title">交易記錄</h5>
                <p class="card-text text-muted">查看所有交易歷史</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { inject } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from '../components/Navbar.vue'

export default {
  name: 'Dashboard',
  components: {
    Navbar
  },
  setup() {
    const store = inject('store')
    const router = useRouter()

    const goTo = (path) => {
      router.push(path)
    }

    const formatAmount = (amount) => {
      return amount?.toLocaleString() || '0'
    }

    return {
      user: store.state.user,
      goTo,
      formatAmount
    }
  }
}
</script>
