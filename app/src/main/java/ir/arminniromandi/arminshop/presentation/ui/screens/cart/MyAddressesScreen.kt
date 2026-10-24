package ir.arminniromandi.arminshop.presentation.ui.screens.cart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.arminniromandi.arminshop.domain.model.UserAddress
import ir.arminniromandi.arminshop.presentation.components.AddEditAddressBottomSheet
import ir.arminniromandi.arminshop.presentation.components.TopInfoMinify
import ir.arminniromandi.arminshop.presentation.ui.theme.PrimaryOrangeRed
import ir.arminniromandi.arminshop.presentation.ui.theme.ScreenBackground
import ir.arminniromandi.arminshop.presentation.ui.theme.TextDarkColor
import ir.arminniromandi.arminshop.presentation.ui.theme.TextGrayColor
import ir.arminniromandi.arminshop.presentation.viewmodels.AddressViewModel
import ir.arminniromandi.arminshop.presentation.viewmodels.CartSharedViewModel

@Composable
fun MyAddressesScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    isSelectionMode: Boolean = false,
    cartViewModel: CartSharedViewModel = hiltViewModel(),
    addressViewModel: AddressViewModel = hiltViewModel()
) {
    // Collect directly from database (Room via AddressViewModel)
    val addresses by addressViewModel.addresses.collectAsStateWithLifecycle()
    val defaultAddress by addressViewModel.defaultAddress.collectAsStateWithLifecycle()
    val currentSelectedAddress by addressViewModel.selectedAddress.collectAsStateWithLifecycle()

    val activeSelectedAddress = currentSelectedAddress ?: defaultAddress ?: addresses.firstOrNull()

    var isBottomSheetOpen by remember { mutableStateOf(false) }
    var addressToEdit by remember { mutableStateOf<UserAddress?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Header with Back Arrow and Title "آدرس های من" (Image 4 style)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "بازگشت",
                        tint = TextDarkColor
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "آدرس های من",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TextDarkColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )




            }

            // Address List from Room Database
            if (addresses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "هیچ آدرسی در دیتابیس ثبت نشده است",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextGrayColor,
                            fontSize = 14.sp
                        )
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(addresses, key = { it.id }) { address ->
                        val isSelected = activeSelectedAddress?.id == address.id

                        AddressSelectionCard(
                            address = address,
                            isSelected = isSelected,
                            onSelect = {
                                addressViewModel.selectAddress(address)
                                cartViewModel.selectAddress(address)
                            },
                            onSetDefault = {
                                addressViewModel.setDefaultAddress(address.id)
                                cartViewModel.selectAddress(address)
                            },
                            onEdit = {
                                addressToEdit = address
                                isBottomSheetOpen = true
                            },
                            onDelete = {
                                addressViewModel.deleteAddress(address.id)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sticky Bottom Button: "+ ثبت آدرس جدید" (Image 4 design)
            if (isSelectionMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            if (activeSelectedAddress != null) {
                                cartViewModel.selectAddress(activeSelectedAddress)
                            }
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrangeRed),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text(
                            text = "انتخاب و ادامه خرید",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                    }

                    Button(
                        onClick = {
                            addressToEdit = null
                            isBottomSheetOpen = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF0EC)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Text(
                            text = "+ آدرس جدید",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = PrimaryOrangeRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            } else {
                // Solid Orange "+ ثبت آدرس جدید" button matching Image 4
                Button(
                    onClick = {
                        addressToEdit = null
                        isBottomSheetOpen = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrangeRed),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "+ ثبت آدرس جدید",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                }
            }
        }
    }

    // Add / Edit Address Bottom Sheet (saves directly to Room database)
    AddEditAddressBottomSheet(
        isOpen = isBottomSheetOpen,
        addressToEdit = addressToEdit,
        onDismissRequest = {
            isBottomSheetOpen = false
            addressToEdit = null
        },
        onSaveAddress = { id, name, address, postal, phone, isDef ->
            addressViewModel.saveAddress(
                id = id,
                recipientName = name,
                fullAddress = address,
                postalCode = postal,
                phoneNumber = phone,
                isDefault = isDef,
                onSuccess = { saved ->
                    cartViewModel.selectAddress(saved)
                    isBottomSheetOpen = false
                    addressToEdit = null
                }
            )
        }
    )
}


/**
 * Address card matching Image 4:
 * - Shows recipient, address, postal code, phone number
 * - Highlight border if default or selected
 * - Horizontal divider
 * - Bottom row: Default radio button, Edit button, Delete button
 */
@Composable
fun AddressSelectionCard(
    address: UserAddress,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onSetDefault: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = if (isSelected) BorderStroke(1.5.dp, PrimaryOrangeRed) else null,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Recipient
            AddressItemFieldRow(label = "گیرنده:", value = address.recipientName)

            // Address
            AddressItemFieldRow(label = "آدرس:", value = address.fullAddress)

            // Postal Code
            AddressItemFieldRow(label = "کد پستی:", value = address.postalCode)

            // Phone
            AddressItemFieldRow(label = "شماره همراه:", value = address.phoneNumber)

            HorizontalDivider(
                color = Color(0xFFEEEEEE),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Bottom Actions Row: Radio button "آدرس پیش فرض" + "ویرایش" + "حذف"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Radio button and label
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onSetDefault() }
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSetDefault() },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = PrimaryOrangeRed,
                            unselectedColor = Color(0xFFBDBDBD)
                        ),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "آدرس پیش فرض",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextDarkColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                // Edit & Delete Action Pills
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Edit Button (Soft Orange Pill)
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onEdit() },
                        color = Color(0xFFFFF0EC),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "ویرایش",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = PrimaryOrangeRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    // Delete Button (Soft Gray Pill)
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onDelete() },
                        color = Color(0xFFF1F3F5),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "حذف",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color(0xFF757575),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddressItemFieldRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextDarkColor,
                fontSize = 12.sp
            ),
            modifier = Modifier.width(82.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF424242),
                fontSize = 12.sp,
                lineHeight = 17.sp
            ),
            modifier = Modifier.weight(1f)
        )
    }
}
