package ir.arminniromandi.arminshop.domain.repository

import ir.arminniromandi.arminshop.data.local.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getAllOrders(): Flow<List<OrderEntity>>
    suspend fun addOrders(orders: List<OrderEntity>): List<Long>
    suspend fun addOrder(order: OrderEntity): Long
    suspend fun updateOrderComment(orderId: Long, rating: Float, comment: String): Boolean
}
