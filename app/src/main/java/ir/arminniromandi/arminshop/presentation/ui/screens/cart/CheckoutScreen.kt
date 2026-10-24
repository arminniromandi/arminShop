package ir.arminniromandi.arminshop.presentation.ui.screens.cart

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.arminniromandi.arminshop.domain.model.UserAddress
import ir.arminniromandi.arminshop.presentation.components.AddEditAddressBottomSheet
import ir.arminniromandi.arminshop.presentation.components.PaymentSuccessDialog
import ir.arminniromandi.arminshop.presentation.components.formatPersianPrice
import ir.arminniromandi.arminshop.presentation.ui.theme.PrimaryOrangeRed
import ir.arminniromandi.arminshop.presentation.ui.theme.ScreenBackground
import ir.arminniromandi.arminshop.presentation.ui.theme.TextDarkColor
import ir.arminniromandi.arminshop.presentation.ui.theme.TextGrayColor
import ir.arminniromandi.arminshop.presentation.viewmodels.AddressViewModel
import ir.arminniromandi.arminshop.presentation.viewmodels.CartSharedViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddresses: () -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    cartViewModel: CartSharedViewModel = hiltViewModel(),
    addressViewModel: AddressViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val subtotalPrice by cartViewModel.subtotalPrice.collectAsStateWithLifecycle()
    val discountAmount by cartViewModel.discountAmount.collectAsStateWithLifecycle()
    val grandTotal by cartViewModel.grandTotal.collectAsStateWithLifecycle()
    val defaultAddress by cartViewModel.defaultAddress.collectAsStateWithLifecycle()
    val selectedAddressFromCart by cartViewModel.selectedAddress.collectAsStateWithLifecycle()
    val selectedAddressFromVm by addressViewModel.selectedAddress.collectAsStateWithLifecycle()

    val orderSuccessInfo by cartViewModel.orderSuccessInfo.collectAsStateWithLifecycle()

    val activeAddress: UserAddress? = selectedAddressFromCart ?: selectedAddressFromVm ?: defaultAddress

    var isAddAddressOpen by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(ScreenBackground)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header with Back Arrow and Title "سبد خرید"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "سبد خرید",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TextDarkColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
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



            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (activeAddress != null) {
                    // Address Details Card (Image 2 style)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AddressDetailRow(label = "گیرنده:", value = activeAddress.recipientName)
                            AddressDetailRow(label = "آدرس:", value = activeAddress.fullAddress)
                            AddressDetailRow(label = "کد پستی:", value = activeAddress.postalCode)
                            AddressDetailRow(label = "شماره همراه:", value = activeAddress.phoneNumber)
                        }
                    }

                    // Button: "به آدرس دیگری برود"
                    OutlinedButton(
                        onClick = onNavigateToAddresses,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(PrimaryOrangeRed.copy(alpha = 0.6f))
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFFFFF8F5),
                            contentColor = PrimaryOrangeRed
                        )
                    ) {
                        Text(
                            text = "به آدرس دیگری برود",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryOrangeRed,
                                fontSize = 14.sp
                            )
                        )
                    }

                    // Delivery Notice
                    Text(
                        text = "تقریبا تا ۵ روز آینده این محصول بدست شما میرسد",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextGrayColor,
                            fontSize = 12.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // Empty Address State (NO FAKE DATA)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = PrimaryOrangeRed,
                                modifier = Modifier.size(40.dp)
                            )

                            Text(
                                text = "هیچ آدرسی ثبت نشده است",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkColor,
                                    fontSize = 15.sp
                                )
                            )

                            Text(
                                text = "برای ارسال و تحویل سفارش، لطفاً ابتدا نشانی پستی خود را ثبت فرمایید.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextGrayColor,
                                    fontSize = 12.sp
                                ),
                                textAlign = TextAlign.Center
                            )

                            Button(
                                onClick = { isAddAddressOpen = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrangeRed),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddLocationAlt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ثبت آدرس جدید",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Summary Card (Image 2 style)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // جمع قیمت
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "جمع قیمت:",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextGrayColor, fontSize = 13.sp)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = formatPersianPrice(subtotalPrice),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = TextDarkColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "تومان", style = MaterialTheme.typography.bodySmall.copy(color = TextGrayColor))
                        }
                    }

                    // تخفیف
                    if (discountAmount > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "تخفیف:",
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextGrayColor, fontSize = 13.sp)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = formatPersianPrice(discountAmount),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = PrimaryOrangeRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "تومان", style = MaterialTheme.typography.bodySmall.copy(color = PrimaryOrangeRed))
                            }
                        }
                    }

                    // هزینه ارسال: ۱۰۰،۰۰۰ تومان
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "هزینه ارسال:",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextGrayColor, fontSize = 13.sp)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = formatPersianPrice(cartViewModel.shippingFee),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = TextDarkColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "تومان", style = MaterialTheme.typography.bodySmall.copy(color = TextGrayColor))
                        }
                    }

                    // مبلغ نهایی
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "مبلغ نهایی:",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextGrayColor, fontSize = 13.sp)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = formatPersianPrice(grandTotal),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = TextDarkColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "تومان", style = MaterialTheme.typography.bodySmall.copy(color = TextDarkColor))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // دکمه پرداخت نهایی
                    Button(
                        onClick = {
                            if (activeAddress == null) {
                                Toast.makeText(context, "لطفاً ابتدا آدرس تحویل را ثبت کنید", Toast.LENGTH_SHORT).show()
                                isAddAddressOpen = true
                            } else {
                                cartViewModel.completePayment(activeAddress)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrangeRed),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "پرداخت نهایی",
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

        // Add Address Bottom Sheet (If user clicked add from empty state)
        AddEditAddressBottomSheet(
            isOpen = isAddAddressOpen,
            onDismissRequest = { isAddAddressOpen = false },
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
                        isAddAddressOpen = false
                    }
                )
            }
        )

        // Payment Success Dialog
        PaymentSuccessDialog(
            orderInfo = orderSuccessInfo,
            onDismiss = { cartViewModel.dismissOrderSuccess() },
            onNavigateHome = onNavigateToHome
        )
    }
}

@Composable
fun AddressDetailRow(
    label: String,
    value: String
) {
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
            modifier = Modifier.width(84.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF424242),
                fontSize = 12.sp,
                lineHeight = 18.sp
            ),
            modifier = Modifier.weight(1f)
        )
    }
}
