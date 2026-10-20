package ir.arminniromandi.arminshop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ir.arminniromandi.arminshop.data.local.entity.UserAddressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {

    @Query("SELECT * FROM user_addresses ORDER BY isDefault DESC, id DESC")
    fun getAllAddresses(): Flow<List<UserAddressEntity>>

    @Query("SELECT * FROM user_addresses WHERE id = :id LIMIT 1")
    fun getAddressById(id: Long): UserAddressEntity?

    @Query("SELECT * FROM user_addresses WHERE isDefault = 1 LIMIT 1")
    fun getDefaultAddress(): Flow<UserAddressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAddress(address: UserAddressEntity): Long

    @Update
    fun updateAddress(address: UserAddressEntity): Int

    @Query("UPDATE user_addresses SET isDefault = 0")
    fun clearDefaultFlags(): Int

    @Query("UPDATE user_addresses SET isDefault = 1 WHERE id = :id")
    fun setDefaultAddress(id: Long): Int

    @Query("DELETE FROM user_addresses WHERE id = :id")
    fun deleteAddress(id: Long): Int
}
