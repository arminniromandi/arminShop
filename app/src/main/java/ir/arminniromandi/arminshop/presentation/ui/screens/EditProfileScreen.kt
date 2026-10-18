package ir.arminniromandi.arminshop.presentation.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.arminniromandi.arminshop.presentation.components.UserInfoTopProfile
import ir.arminniromandi.arminshop.presentation.ui.theme.ArminShopTheme
import ir.arminniromandi.arminshop.presentation.ui.theme.PrimaryOrangeRed
import ir.arminniromandi.arminshop.presentation.ui.theme.ScreenBackground
import ir.arminniromandi.arminshop.presentation.ui.theme.TextDarkColor
import ir.arminniromandi.arminshop.presentation.viewmodels.EditProfileViewModel

class DateVisualTransformation(private val mask: String = "____/__/__") : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val rawDigits = text.text.map { char ->
            when (char) {
                '۰' -> '0'; '۱' -> '1'; '۲' -> '2'; '۳' -> '3'; '۴' -> '4'
                '۵' -> '5'; '۶' -> '6'; '۷' -> '7'; '۸' -> '8'; '۹' -> '9'
                else -> char
            }
        }.filter { it.isDigit() }.take(8).joinToString("")

        val out = StringBuilder()
        var digitIndex = 0

        for (i in mask.indices) {
            val maskChar = mask[i]
            if (maskChar == '_') {
                if (digitIndex < rawDigits.length) {
                    out.append(rawDigits[digitIndex])
                    digitIndex++
                } else {
                    out.append('_')
                }
            } else {
                out.append(maskChar)
            }
        }

        val formattedText = out.toString()

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val clampedOffset = offset.coerceIn(0, rawDigits.length)
                if (clampedOffset <= 0) return 0
                var digitsCount = 0
                for (i in mask.indices) {
                    if (mask[i] == '_') {
                        digitsCount++
                        if (digitsCount == clampedOffset) {
                            return i + 1
                        }
                    }
                }
                return mask.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                val clampedOffset = offset.coerceIn(0, mask.length)
                var digitsCount = 0
                for (i in 0 until clampedOffset) {
                    if (mask[i] == '_') {
                        digitsCount++
                    }
                }
                return digitsCount.coerceIn(0, rawDigits.length)
            }
        }

        return TransformedText(
            text = AnnotatedString(formattedText),
            offsetMapping = offsetMapping
        )
    }
}

@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val fullName by viewModel.fullName.collectAsStateWithLifecycle()
    val phoneNumber by viewModel.phoneNumber.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()
    val birthDate by viewModel.birthDate.collectAsStateWithLifecycle()
    val gender by viewModel.gender.collectAsStateWithLifecycle()
    val isSaved by viewModel.isSaved.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(isSaved) {
        if (isSaved) {
            Toast.makeText(context, "تغییرات با موفقیت ثبت شد", Toast.LENGTH_SHORT).show()
            viewModel.resetSavedState()
            onNavigateBack()
        }
    }

    EditProfileContent(
        modifier = modifier,
        fullName = fullName,
        phoneNumber = phoneNumber,
        email = email,
        birthDate = birthDate,
        gender = gender,
        onFullNameChange = viewModel::onFullNameChange,
        onPhoneNumberChange = viewModel::onPhoneNumberChange,
        onEmailChange = viewModel::onEmailChange,
        onBirthDateChange = viewModel::onBirthDateChange,
        onGenderChange = viewModel::onGenderChange,
        onSubmit = viewModel::saveChanges
    )
}

@Composable
fun EditProfileContent(
    modifier: Modifier = Modifier,
    fullName: String,
    phoneNumber: String,
    email: String,
    birthDate: String,
    gender: String,
    onFullNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onBirthDateChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val scrollState = rememberScrollState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(ScreenBackground)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top User Info Card Header
            UserInfoTopProfile(
                modifier = Modifier.fillMaxWidth(),
                onEditProfileClick = {},
                userName = "",
                onChangeAvatarClick = {},
                phoneNumber = "",
                showInfo = false
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Form Inputs Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Full Name
                EditProfileInputField(
                    label = "نام و نام خانوادگی:",
                    value = fullName,
                    onValueChange = onFullNameChange,
                    placeholder = "نام و نام خانوادگی"
                )

                // 2. Phone Number
                EditProfileInputField(
                    label = "شماره همراه:",
                    value = phoneNumber,
                    onValueChange = onPhoneNumberChange,
                    placeholder = "۰۹۱۷۵۶۴۲۳۴۵۶",
                    keyboardType = KeyboardType.Phone
                )

                // 3. Email
                EditProfileInputField(
                    label = "ایمیل:",
                    value = email,
                    onValueChange = onEmailChange,
                    placeholder = "مثال: bahramafshari@gmail.com",
                    keyboardType = KeyboardType.Email
                )

                // 4. Birth Date with ____/__/__ Input Mask
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "تاریخ تولد:",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkColor,
                            fontSize = 15.sp
                        )
                    )

                    val cleanBirthDate = birthDate.map { char ->
                        when (char) {
                            '۰' -> '0'; '۱' -> '1'; '۲' -> '2'; '۳' -> '3'; '۴' -> '4'
                            '۵' -> '5'; '۶' -> '6'; '۷' -> '7'; '۸' -> '8'; '۹' -> '9'
                            else -> char
                        }
                    }.filter { it.isDigit() }.take(8).joinToString("")

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DateRange,
                                contentDescription = "Calendar",
                                tint = Color(0xFF616161),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                BasicTextField(
                                    value = cleanBirthDate,
                                    onValueChange = { newValue: String ->
                                        val digits = newValue.map { char ->
                                            when (char) {
                                                '۰' -> '0'; '۱' -> '1'; '۲' -> '2'; '۳' -> '3'; '۴' -> '4'
                                                '۵' -> '5'; '۶' -> '6'; '۷' -> '7'; '۸' -> '8'; '۹' -> '9'
                                                else -> char
                                            }
                                        }.filter { it.isDigit() }.take(8).joinToString("")
                                        onBirthDateChange(digits)
                                    },
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        color = TextDarkColor,
                                        fontSize = 15.sp,
                                        letterSpacing = 1.sp
                                    ),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    visualTransformation = DateVisualTransformation("____/__/__"),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // 5. Gender Radio Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "جنسیت:",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkColor,
                            fontSize = 15.sp
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // "زن" Option
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { onGenderChange("زن") }
                                .padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "زن",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkColor
                                )
                            )
                            RadioButton(
                                selected = (gender == "زن"),
                                onClick = { onGenderChange("زن") },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryOrangeRed)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // "مرد" Option
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { onGenderChange("مرد") }
                                .padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "مرد",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkColor
                                )
                            )
                            RadioButton(
                                selected = (gender == "مرد"),
                                onClick = { onGenderChange("مرد") },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryOrangeRed)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 6. Submit Changes Button
                Button(
                    onClick = onSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrangeRed)
                ) {
                    Text(
                        text = "ثبت تغییرات",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun EditProfileInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextDarkColor,
                fontSize = 15.sp
            )
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF9E9E9E))
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextDarkColor),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditProfilePreview() {
    ArminShopTheme {
        EditProfileContent(
            fullName = "بهرام افشاری",
            phoneNumber = "۰۹۱۷۵۶۴۲۳۴۵۶",
            email = "",
            birthDate = "",
            gender = "مرد",
            onFullNameChange = {},
            onPhoneNumberChange = {},
            onEmailChange = {},
            onBirthDateChange = {},
            onGenderChange = {},
            onSubmit = {}
        )
    }
}
