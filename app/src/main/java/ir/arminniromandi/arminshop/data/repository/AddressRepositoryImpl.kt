package ir.arminniromandi.arminshop.data.repository

import ir.arminniromandi.arminshop.data.local.dao.AddressDao
import ir.arminniromandi.arminshop.data.local.entity.UserAddressEntity
import ir.arminniromandi.arminshop.domain.model.UserAddress
import ir.arminniromandi.arminshop.domain.repository.AddressRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddressRepositoryImpl @Inject constructor(
    private val addressDao: AddressDao
) : AddressRepository {

    override fun getAllAddresses(): Flow<List<UserAddress>> {
        return addressDao.getAllAddresses().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getDefaultAddress(): Flow<UserAddress?> {
        return addressDao.getDefaultAddress().map { it?.toDomainModel() }
    }

    override suspend fun getAddressById(id: Long): UserAddress? = withContext(Dispatchers.IO) {
        addressDao.getAddressById(id)?.toDomainModel()
    }

    override suspend fun addAddress(address: UserAddress): Long = withContext(Dispatchers.IO) {
        if (address.isDefault) {
            addressDao.clearDefaultFlags()
        }
        val entity = address.toEntity()
        val newId = addressDao.insertAddress(entity)
        if (address.isDefault) {
            addressDao.setDefaultAddress(newId)
        }
        newId
    }

    override suspend fun updateAddress(address: UserAddress): Unit = withContext(Dispatchers.IO) {
        if (address.isDefault) {
            addressDao.clearDefaultFlags()
        }
        addressDao.updateAddress(address.toEntity())
        if (address.isDefault) {
            addressDao.setDefaultAddress(address.id)
        }
    }

    override suspend fun setDefaultAddress(id: Long): Unit = withContext(Dispatchers.IO) {
        addressDao.clearDefaultFlags()
        addressDao.setDefaultAddress(id)
    }

    override suspend fun deleteAddress(id: Long): Unit = withContext(Dispatchers.IO) {
        addressDao.deleteAddress(id)
    }

    private fun UserAddressEntity.toDomainModel(): UserAddress {
        return UserAddress(
            id = id,
            recipientName = recipientName,
            fullAddress = fullAddress,
            postalCode = postalCode,
            phoneNumber = phoneNumber,
            isDefault = isDefault
        )
    }

    private fun UserAddress.toEntity(): UserAddressEntity {
        return UserAddressEntity(
            id = id,
            recipientName = recipientName,
            fullAddress = fullAddress,
            postalCode = postalCode,
            phoneNumber = phoneNumber,
            isDefault = isDefault
        )
    }
}
