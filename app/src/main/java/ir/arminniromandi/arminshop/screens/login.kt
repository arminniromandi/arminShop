package ir.arminniromandi.arminshop.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Enum representing login steps
 */
enum class LoginStep {
    EMAIL,
    VERIFICATION_CODE,
    USER_INFO
}

/**
 * Color palette used in Login Screen
 */
private object LoginColors {
    val Background = Color(0xFFFFF6EE)
    val DecorativeCircle = Color(0xFFFDE8D7)
    val PrimaryRed = Color(0xFFFA4A23)
    val PrimaryRedGradient = Color(0xFFE53517)
    val DarkText = Color(0xFF1E1E1E)
    val SecondaryText = Color(0xFF666666)
    val TextFieldBg = Color.White
    val TextFieldBorder = Color(0xFFE0E0E0)
    val ErrorRed = Color(0xFFD32F2F)
    val TimerBg = Color(0xFFF3ECE4)
    val Placeholder = Color(0xFF9E9E9E)
}


@Preview
@Composable
private fun preView() {

    LoginScreen()

}

/**
 * Main Login Screen Composable containing all steps and dialogs
 */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit = {}
) {
    // Force RTL layout direction for Persian UI
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var currentStep by remember { mutableStateOf(LoginStep.EMAIL) }
        var email by remember { mutableStateOf("") }
        var verificationCode by remember { mutableStateOf("") }
        var fullName by remember { mutableStateOf("") }

        var isLoading by remember { mutableStateOf(false) }
        var isCodeError by remember { mutableStateOf(false) }
        var timerSeconds by remember { mutableIntStateOf(201) } // ~03:21 in Persian (۳۴:۲۱ / ۰۳:۲۱)

        val coroutineScope = rememberCoroutineScope()

        // Timer countdown effect for code verification screen
        LaunchedEffect(currentStep, timerSeconds) {
            if (currentStep == LoginStep.VERIFICATION_CODE && timerSeconds > 0) {
                delay(1000L)
                timerSeconds--
            }
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(LoginColors.Background)
        ) {
            // Decorative background shapes
            LoginBackground()

            // Main Content Container
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(56.dp))

                // Header Logo and Title
                LoginHeader()

                Spacer(modifier = Modifier.height(36.dp))

                // Step content according to current step state
                when (currentStep) {
                    LoginStep.EMAIL -> {
                        EmailStepContent(
                            email = email,
                            onEmailChange = { email = it },
                            onSubmit = {
                                if (email.isNotBlank()) {
                                    isLoading = true
                                    coroutineScope.launch {
                                        delay(1200L) // Simulate network call
                                        isLoading = false
                                        currentStep = LoginStep.VERIFICATION_CODE
                                        timerSeconds = 201
                                        isCodeError = false
                                    }
                                }
                            }
                        )
                    }

                    LoginStep.VERIFICATION_CODE -> {
                        VerificationCodeStepContent(
                            email = email,
                            code = verificationCode,
                            onCodeChange = {
                                verificationCode = it
                                isCodeError = false
                            },
                            isError = isCodeError,
                            timerText = formatTimer(timerSeconds),
                            onSubmit = {
                                if (verificationCode.isNotBlank()) {
                                    isLoading = true
                                    coroutineScope.launch {
                                        delay(1500L) // Simulate API verification
                                        isLoading = false
                                        // Demo logic: code "1234" succeeds, otherwise triggers error state
                                        if (verificationCode == "1234" || verificationCode == "0000") {
                                            currentStep = LoginStep.USER_INFO
                                        } else {
                                            isCodeError = true
                                        }
                                    }
                                }
                            },
                            onEditEmail = {
                                currentStep = LoginStep.EMAIL
                                verificationCode = ""
                                isCodeError = false
                            },
                            onResendCode = {
                                timerSeconds = 201
                                isCodeError = false
                                verificationCode = ""
                            }
                        )
                    }

                    LoginStep.USER_INFO -> {
                        UserInfoStepContent(
                            fullName = fullName,
                            onFullNameChange = { fullName = it },
                            onSubmit = {
                                isLoading = true
                                coroutineScope.launch {
                                    delay(1000L)
                                    isLoading = false
                                    onLoginSuccess()
                                }
                            },
                            onSkip = {
                                onLoginSuccess()
                            }
                        )
                    }
                }
            }

            // Loading Dialog ("لطفا صبر کنید")
            if (isLoading) {
                PleaseWaitDialog()
            }
        }
    }
}

/**
 * Background soft decorative circles
 */
@Composable
private fun LoginBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Top-right soft circle
        drawCircle(
            color = LoginColors.DecorativeCircle,
            radius = width * 0.48f,
            center = Offset(width * 0.95f, height * 0.08f)
        )

        // Top-left small arc
        drawCircle(
            color = LoginColors.DecorativeCircle.copy(alpha = 0.6f),
            radius = width * 0.28f,
            center = Offset(-width * 0.08f, height * 0.28f)
        )

        // Bottom-left soft circle
        drawCircle(
            color = LoginColors.DecorativeCircle,
            radius = width * 0.58f,
            center = Offset(width * 0.18f, height * 0.95f)
        )
    }
}

/**
 * Top Header containing Logo placeholder and title slogan
 */
@Composable
private fun LoginHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo Placeholder Container (120.dp size)
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder Icon (Can be replaced with Image(painterResource(R.drawable.your_logo), ...))
            ShoppingCartLogoIcon(
                modifier = Modifier.size(90.dp),
                tint = LoginColors.PrimaryRed
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // "OnlineShop" styled text
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = LoginColors.PrimaryRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                ) {
                    append("Online")
                }
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFF1C2238),
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                ) {
                    append("Shop")
                }
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // "فروشگاه اینترنتی آنلاین شاپ" text
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = LoginColors.DarkText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                ) {
                    append("فروشگاه اینترنتی ")
                }
                withStyle(
                    style = SpanStyle(
                        color = LoginColors.PrimaryRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                ) {
                    append("آنلاین شاپ")
                }
            },
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Step 1: Email Input Screen
 */
@Composable
private fun EmailStepContent(
    email: String,
    onEmailChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        // Info description
        Text(
            text = "جهت ورود به فروشگاه اینترنتی آنلاین شاپ ایمیل خود را در کادر زیر وارد کرده و کد ارسالی به ایمیل خود را در مرحله بعد وارد کنید.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = LoginColors.SecondaryText,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Start
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Label: "ایمیل:"
        Text(
            text = "ایمیل:",
            style = MaterialTheme.typography.titleMedium.copy(
                color = LoginColors.DarkText,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Custom Email Input
        CustomTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = "ایمیل خود را وارد کنید",
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done,
            onImeAction = onSubmit
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        PrimaryButton(
            text = "تأیید و ادامه",
            onClick = onSubmit,
            enabled = email.isNotBlank()
        )
    }
}

/**
 * Step 2: Verification Code Input Screen
 */
@Composable
private fun VerificationCodeStepContent(
    email: String,
    code: String,
    onCodeChange: (String) -> Unit,
    isError: Boolean,
    timerText: String,
    onSubmit: () -> Unit,
    onEditEmail: () -> Unit,
    onResendCode: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        // Label: "کد تأیید:"
        Text(
            text = "کد تأیید:",
            style = MaterialTheme.typography.titleMedium.copy(
                color = LoginColors.DarkText,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Description text with user's email
        Text(
            text = "کد ۴ رقمی ارسال شده به $email خود را وارد کنید",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = LoginColors.SecondaryText,
                fontSize = 13.sp,
                lineHeight = 20.sp
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Text field with Timer chip inside
        CustomTextField(
            value = code,
            onValueChange = onCodeChange,
            placeholder = "کد تأیید را وارد کنید",
            keyboardType = KeyboardType.Number,
            isError = isError,
            imeAction = ImeAction.Done,
            onImeAction = onSubmit,
            leadingContent = {
                // Timer badge chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(LoginColors.TimerBg)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = timerText,
                        color = LoginColors.DarkText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        )

        // Error message if code is incorrect (as shown in Image 3)
        AnimatedVisibility(visible = isError) {
            Column {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "کد وارد شده اشتباه است",
                    color = LoginColors.ErrorRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        PrimaryButton(
            text = "تأیید و ادامه",
            onClick = onSubmit,
            enabled = code.isNotBlank()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom options row ("ویرایش ایمیل" & "ارسال مجدد کد")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "ویرایش ایمیل"
            Text(
                text = "ویرایش ایمیل",
                color = LoginColors.DarkText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onEditEmail() }
            )

            // "ارسال مجدد کد" with arrow
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onResendCode() }
            ) {
                Text(
                    text = "ارسال مجدد کد",
                    color = LoginColors.DarkText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(6.dp))
                ArrowLeftIcon(
                    modifier = Modifier.size(16.dp),
                    tint = LoginColors.DarkText
                )
            }
        }
    }
}

/**
 * Step 3: User Info / Name Input Screen
 */
@Composable
private fun UserInfoStepContent(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        // Label: "اطلاعات کاربری:"
        Text(
            text = "اطلاعات کاربری:",
            style = MaterialTheme.typography.titleMedium.copy(
                color = LoginColors.DarkText,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Custom Name Input
        CustomTextField(
            value = fullName,
            onValueChange = onFullNameChange,
            placeholder = "نام و نام خانوادگی خود را وارد کنید",
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
            onImeAction = onSubmit
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button: "ثبت اطلاعات"
        PrimaryButton(
            text = "ثبت اطلاعات",
            onClick = onSubmit,
            enabled = fullName.isNotBlank()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // "عبور از این مرحله" link
        Text(
            text = "عبور از این مرحله",
            color = LoginColors.DarkText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSkip() },
            textAlign = TextAlign.Start
        )
    }
}

/**
 * Reusable Rounded Custom Text Field matching design
 */
@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    onImeAction: () -> Unit = {},
    isError: Boolean = false,
    leadingContent: (@Composable () -> Unit)? = null
) {
    val borderModifier = if (isError) {
        Modifier.border(1.5.dp, LoginColors.ErrorRed, RoundedCornerShape(14.dp))
    } else {
        Modifier.border(1.dp, LoginColors.TextFieldBorder, RoundedCornerShape(14.dp))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(14.dp),
                clip = false
            )
            .clip(RoundedCornerShape(14.dp))
            .background(LoginColors.TextFieldBg)
            .then(borderModifier)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Input Text Box
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = LoginColors.Placeholder,
                        fontSize = 14.sp
                    )
                }

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = LoginColors.DarkText,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Start
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        imeAction = imeAction
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onImeAction() },
                        onNext = { onImeAction() }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Optional Leading/Trailing content inside text field (like Timer)
            if (leadingContent != null) {
                Spacer(modifier = Modifier.width(8.dp))
                leadingContent()
            }
        }
    }
}

/**
 * Reusable Vibrant Primary Action Button
 */
@Composable
private fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = if (enabled) listOf(
            LoginColors.PrimaryRed,
            LoginColors.PrimaryRedGradient
        ) else listOf(
            LoginColors.PrimaryRed.copy(alpha = 0.5f),
            LoginColors.PrimaryRedGradient.copy(alpha = 0.5f)
        )
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .shadow(
                elevation = if (enabled) 2.dp else 0.dp,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Loading Dialog ("لطفا صبر کنید") shown during API verification
 */
@Composable
private fun PleaseWaitDialog() {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .padding(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "لطفا صبر کنید",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = LoginColors.DarkText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )

                Spacer(modifier = Modifier.width(16.dp))

                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.Gray,
                    strokeWidth = 2.5.dp
                )
            }
        }
    }
}

/**
 * Placeholder Vector Icon for Shopping Cart / Brand Logo
 */
@Composable
private fun ShoppingCartLogoIcon(
    modifier: Modifier = Modifier,
    tint: Color = LoginColors.PrimaryRed
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Shopping cart body path
        val path = Path().apply {
            moveTo(w * 0.15f, h * 0.25f)
            lineTo(w * 0.3f, h * 0.25f)
            lineTo(w * 0.42f, h * 0.62f)
            lineTo(w * 0.8f, h * 0.62f)
            lineTo(w * 0.9f, h * 0.35f)
            lineTo(w * 0.33f, h * 0.35f)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = w * 0.07f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        // Wheels
        drawCircle(
            color = tint,
            radius = w * 0.07f,
            center = Offset(w * 0.45f, h * 0.78f)
        )
        drawCircle(
            color = tint,
            radius = w * 0.07f,
            center = Offset(w * 0.75f, h * 0.78f)
        )
    }
}

/**
 * Vector Icon for Left Arrow (RTL back navigation)
 */
@Composable
private fun ArrowLeftIcon(
    modifier: Modifier = Modifier,
    tint: Color = LoginColors.DarkText
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = w * 0.14f

        // Line
        drawLine(
            color = tint,
            start = Offset(w * 0.85f, h * 0.5f),
            end = Offset(w * 0.15f, h * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        // Arrow head pointing left
        val path = Path().apply {
            moveTo(w * 0.45f, h * 0.2f)
            lineTo(w * 0.15f, h * 0.5f)
            lineTo(w * 0.45f, h * 0.8f)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

/**
 * Formats countdown seconds to MM:SS string with Persian digits
 */
private fun formatTimer(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val rawString = String.format(Locale.US, "%02d:%02d", minutes, seconds)
    return rawString.toPersianDigits()
}

/**
 * Converts Western digits to Persian digits
 */
private fun String.toPersianDigits(): String {
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return this.map { char ->
        if (char in '0'..'9') persianDigits[char - '0'] else char
    }.joinToString("")
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
