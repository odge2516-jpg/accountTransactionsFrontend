<template>
  <div>
    <Navbar />
    <div class="dashboard-container">
      <div class="container">
        <div class="form-card">
          <h2 class="text-info mb-4">
            <i class="bi bi-arrow-left-right me-2"></i>轉帳
          </h2>

          <div class="alert alert-info">
            可用餘額：<strong>NT$ {{ formatAmount(user?.balance) }}</strong>
          </div>

          <div v-if="error" class="alert alert-danger" role="alert">
            {{ error }}
          </div>

          <div v-if="success" class="alert alert-success" role="alert">
            {{ success }}
          </div>

          <form @submit.prevent="handleSubmit">
            <div class="mb-3">
              <label for="toEmail" class="form-label">收款人 Email</label>
              <input
                type="email"
                class="form-control"
                id="toEmail"
                v-model="toEmail"
                required
                placeholder="請輸入收款人 Email"
              />
            </div>

            <div class="mb-3">
              <label for="amount" class="form-label">轉帳金額</label>
              <input
                type="number"
                class="form-control"
                id="amount"
                v-model.number="amount"
                required
                min="1"
                :max="user?.balance"
                step="0.01"
                placeholder="請輸入轉帳金額"
              />
            </div>

            <div class="mb-3">
              <label for="description" class="form-label">備註（可選）</label>
              <input
                type="text"
                class="form-control"
                id="description"
                v-model="description"
                placeholder="請輸入備註"
              />
            </div>

            <button type="submit" class="btn btn-info w-100 mb-2" :disabled="loading">
              <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
              {{ loading ? '處理中...' : '確認轉帳' }}
            </button>

            <button type="button" class="btn btn-secondary w-100" @click="goBack">
              返回
            </button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, inject } from 'vue'
import { useRouter } from 'vue-router'
import { transfer } from '../services/api'
import Navbar from '../components/Navbar.vue'

export default {
  name: 'Transfer',
  components: {
    Navbar
  },
  setup() {
    const toEmail = ref('')
    const amount = ref(null)
    const description = ref('')
    const error = ref('')
    const success = ref('')
    const loading = ref(false)

    const store = inject('store')
    const router = useRouter()

    const handleSubmit = async () => {
      error.value = ''
      success.value = ''

      if (!amount.value || amount.value <= 0) {
        error.value = '請輸入有效的金額'
        return
      }

      if (amount.value > store.state.user.balance) {
        error.value = '餘額不足'
        return
      }

      if (toEmail.value === store.state.user.email) {
        error.value = '不能轉帳給自己'
        return
      }

      loading.value = true

      try {
        const result = await transfer(
          store.state.user.id,
          toEmail.value,
          amount.value,
          description.value
        )
        store.updateBalance(result.newBalance)
        success.value = `成功轉帳 NT$ ${amount.value.toLocaleString()} 給 ${toEmail.value}`
        toEmail.value = ''
        amount.value = null
        description.value = ''

        setTimeout(() => {
          router.push('/dashboard')
        }, 1500)
      } catch (err) {
        error.value = err.message
      } finally {
        loading.value = false
      }
    }

    const goBack = () => {
      router.push('/dashboard')
    }

    const formatAmount = (amount) => {
      return amount?.toLocaleString() || '0'
    }

    return {
      user: store.state.user,
      toEmail,
      amount,
      description,
      error,
      success,
      loading,
      handleSubmit,
      goBack,
      formatAmount
    }
  }
}
</script>
