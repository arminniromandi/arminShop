package ir.arminniromandi.arminshop.domain.repository

import ir.arminniromandi.arminshop.domain.model.UserAddress
import kotlinx.coroutines.flow.Flow

interface AddressRepository {
    fun getAllAddresses(): Flow<List<UserAddress>>
    fun getDefaultAddress(): Flow<UserAddress?>
    suspend fun getAddressById(id: Long): UserAddress?
    suspend fun addAddress(address: UserAddress): Long
    suspend fun updateAddress(address: UserAddress)
    suspend fun setDefaultAddress(id: Long)
    suspend fun deleteAddress(id: Long)
}
