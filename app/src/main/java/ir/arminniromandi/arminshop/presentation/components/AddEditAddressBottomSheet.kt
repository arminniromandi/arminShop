package ir.arminniromandi.arminshop.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.arminniromandi.arminshop.domain.model.UserAddress
import ir.arminniromandi.arminshop.presentation.ui.theme.PrimaryOrangeRed
import ir.arminniromandi.arminshop.presentation.ui.theme.TextDarkColor
import ir.arminniromandi.arminshop.presentation.ui.theme.TextGrayColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAddressBottomSheet(
    isOpen: Boolean,
    addressToEdit: UserAddress? = null,
    onDismissRequest: () -> Unit,
    onSaveAddress: (id: Long, name: String, address: String, postalCode: String, phone: String, isDefault: Boolean) -> Unit
) {
    if (!isOpen) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var recipientName by remember { mutableStateOf("") }
    var fullAddress by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }

    var validationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(addressToEdit, isOpen) {
        if (addressToEdit != null) {
            recipientName = addressToEdit.recipientName
            fullAddress = addressToEdit.fullAddress
            postalCode = addressToEdit.postalCode
            phoneNumber = addressToEdit.phoneNumber
            isDefault = addressToEdit.isDefault
        } else {
            recipientName = ""
            fullAddress = ""
            postalCode = ""
            phoneNumber = ""
            isDefault = false
        }
        validationError = null
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (addressToEdit == null) "ثبت آدرس جدید" else "ویرایش آدرس",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkColor,
                            fontSize = 16.sp
                        )
                    )

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = TextGrayColor
                        )
                    }
                }

                if (validationError != null) {
                    Text(
                        text = validationError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                    )
                }

                // نام و نام خانوادگی گیرنده
                OutlinedTextField(
                    value = recipientName,
                    onValueChange = {
                        recipientName = it
                        validationError = null
                    },
                    label = { Text("نام و نام خانوادگی گیرنده") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryOrangeRed,
                        cursorColor = PrimaryOrangeRed,
                        focusedLabelColor = PrimaryOrangeRed,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                // نشانی پستی دقیق
                OutlinedTextField(
                    value = fullAddress,
                    onValueChange = {
                        fullAddress = it
                        validationError = null
                    },
                    label = { Text("آدرس پستی کامل (استان، شهر، خیابان، پلاک)") },
                    minLines = 2,
                    maxLines = 3,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryOrangeRed,
                        cursorColor = PrimaryOrangeRed,
                        focusedLabelColor = PrimaryOrangeRed,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black

                    )
                )

                // کد پستی
                OutlinedTextField(
                    value = postalCode,
                    onValueChange = {
                        if (it.length <= 10) postalCode = it
                        validationError = null
                    },
                    label = { Text("کد پستی (۱۰ رقم)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryOrangeRed,
                        cursorColor = PrimaryOrangeRed,
                        focusedLabelColor = PrimaryOrangeRed,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                // شماره همراه
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        if (it.length <= 11) phoneNumber = it
                        validationError = null
                    },
                    label = { Text("شماره همراه (مثال: ۰۹۱۲۳۴۵۶۷۸۹)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryOrangeRed,
                        cursorColor = PrimaryOrangeRed,
                        focusedLabelColor = PrimaryOrangeRed,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                // انتخاب به عنوان آدرس پیش‌فرض
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isDefault,
                        onCheckedChange = { isDefault = it },
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryOrangeRed)
                    )
                    Text(
                        text = "این آدرس به عنوان آدرس پیش‌فرض انتخاب شود",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextDarkColor,
                            fontSize = 13.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // دکمه تایید و ثبت
                Button(
                    onClick = {
                        if (recipientName.isBlank()) {
                            validationError = "لطفاً نام گیرنده را وارد نمایید"
                            return@Button
                        }
                        if (fullAddress.isBlank()) {
                            validationError = "لطفاً آدرس پستی را وارد نمایید"
                            return@Button
                        }
                        if (postalCode.length < 10) {
                            validationError = "کد پستی باید حداقل ۱۰ رقم باشد"
                            return@Button
                        }
                        if (phoneNumber.length < 10) {
                            validationError = "لطفاً شماره همراه معتبر وارد نمایید"
                            return@Button
                        }

                        onSaveAddress(
                            addressToEdit?.id ?: 0L,
                            recipientName.trim(),
                            fullAddress.trim(),
                            postalCode.trim(),
                            phoneNumber.trim(),
                            isDefault
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrangeRed),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = if (addressToEdit == null) "ثبت و ذخیره آدرس" else "ذخیره تغییرات",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}
