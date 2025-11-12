<template>
  <div>
    <Navbar />
    <div class="dashboard-container">
      <div class="container">
        <div class="table-responsive">
          <h2 class="mb-4">
            <i class="bi bi-list-ul me-2"></i>交易記錄
          </h2>

          <div v-if="transactions.length === 0" class="text-center text-muted py-5">
            <i class="bi bi-inbox" style="font-size: 48px;"></i>
            <p class="mt-3">尚無交易記錄</p>
          </div>

          <table v-else class="table table-hover">
            <thead class="table-light">
              <tr>
                <th>日期時間</th>
                <th>類型</th>
                <th>金額</th>
                <th>備註</th>
                <th>對方帳戶</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="transaction in transactions" :key="transaction.id">
                <td>{{ formatDate(transaction.date) }}</td>
                <td>
                  <span :class="getTypeClass(transaction.type)">
                    {{ getTypeLabel(transaction.type) }}
                  </span>
                </td>
                <td>
                  <span :class="getTypeClass(transaction.type)">
                    {{ getAmountSign(transaction.type) }}NT$ {{ formatAmount(transaction.amount) }}
                  </span>
                </td>
                <td>{{ transaction.description }}</td>
                <td>
                  <span v-if="transaction.toUser">轉給：{{ transaction.toUser }}</span>
                  <span v-else-if="transaction.fromUser">來自：{{ transaction.fromUser }}</span>
                  <span v-else>-</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, inject, onMounted } from 'vue'
import { getUserTransactions } from '../services/api'
import Navbar from '../components/Navbar.vue'

export default {
  name: 'TransactionHistory',
  components: {
    Navbar
  },
  setup() {
    const store = inject('store')
    const transactions = ref([])

    onMounted(() => {
      transactions.value = getUserTransactions(store.state.user.id)
    })

    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleString('zh-TW', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    const getTypeLabel = (type) => {
      const labels = {
        'deposit': '存款',
        'withdraw': '提款',
        'transfer-in': '轉入',
        'transfer-out': '轉出'
      }
      return labels[type] || type
    }

    const getTypeClass = (type) => {
      return `transaction-type-${type}`
    }

    const getAmountSign = (type) => {
      return (type === 'deposit' || type === 'transfer-in') ? '+' : '-'
    }

    const formatAmount = (amount) => {
      return amount.toLocaleString()
    }

    return {
      transactions,
      formatDate,
      getTypeLabel,
      getTypeClass,
      getAmountSign,
      formatAmount
    }
  }
}
</script>
