package ir.arminniromandi.arminshop.presentation.ui.screens

import android.widget.Toast
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
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalContext
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.arminniromandi.arminshop.presentation.ui.theme.ArminShopTheme
import ir.arminniromandi.arminshop.presentation.ui.theme.ShopDecorativeCircle
import ir.arminniromandi.arminshop.presentation.ui.theme.ShopPrimaryRedGradient
import ir.arminniromandi.arminshop.presentation.ui.theme.ShopSecondaryText
import ir.arminniromandi.arminshop.presentation.ui.theme.ShopTextFieldBorder
import ir.arminniromandi.arminshop.presentation.ui.theme.ShopTimerBg
import ir.arminniromandi.arminshop.presentation.ui.theme.ShopPlaceholder
import ir.arminniromandi.arminshop.presentation.viewmodels.LoginUiState
import ir.arminniromandi.arminshop.presentation.viewmodels.LoginViewModel

/**
 * Enum representing login steps
 */
enum class LoginStep {
    EMAIL,
    VERIFICATION_CODE,
    USER_INFO
}

@Preview
@Composable
private fun LoginScreenPreview() {
    ArminShopTheme {
        LoginScreenContent(
            uiState = LoginUiState(),
            onEmailChange = {},
            onEmailSubmit = {},
            onCodeChange = {},
            onCodeSubmit = {},
            onEditEmail = {},
            onResendCode = {},
            onFullNameChange = {},
            onUserInfoSubmit = {},
            onSkipUserInfo = {}
        )
    }
}

/**
 * Main Login Screen Composable adhering strictly to project theme & typography rules.
 */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoginScreenContent(
        modifier = modifier,
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onEmailSubmit = viewModel::submitEmail,
        onCodeChange = viewModel::onVerificationCodeChange,
        onCodeSubmit = viewModel::submitVerificationCode,
        onEditEmail = viewModel::editEmail,
        onResendCode = viewModel::resendCode,
        onFullNameChange = viewModel::onFullNameChange,
        onUserInfoSubmit = { viewModel.submitUserInfo(onLoginSuccess) },
        onSkipUserInfo = onLoginSuccess
    )
}

/**
 * Stateless Login Screen Composable adhering strictly to project theme & typography rules.
 */
@Composable
fun LoginScreenContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onEmailSubmit: () -> Unit,
    onCodeChange: (String) -> Unit,
    onCodeSubmit: () -> Unit,
    onEditEmail: () -> Unit,
    onResendCode: () -> Unit,
    onFullNameChange: (String) -> Unit,
    onUserInfoSubmit: () -> Unit,
    onSkipUserInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Force RTL layout direction for Persian UI
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Decorative background shapes using theme colors
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
                when (uiState.currentStep) {
                    LoginStep.EMAIL -> {
                        EmailStepContent(
                            email = uiState.email,
                            isSubmitEnabled = uiState.isEmailSubmitEnabled,
                            isError = uiState.isEmailError,
                            onEmailChange = onEmailChange,
                            onSubmit = onEmailSubmit
                        )
                    }

                    LoginStep.VERIFICATION_CODE -> {
                        VerificationCodeStepContent(
                            email = uiState.email,
                            code = uiState.verificationCode,
                            isSubmitEnabled = uiState.isCodeSubmitEnabled,
                            onCodeChange = onCodeChange,
                            isError = uiState.isCodeError,
                            timerText = uiState.formattedTimer,
                            onSubmit = onCodeSubmit,
                            onEditEmail = onEditEmail,
                            onResendCode = onResendCode
                        )
                    }

                    LoginStep.USER_INFO -> {
                        UserInfoStepContent(
                            fullName = uiState.fullName,
                            isSubmitEnabled = uiState.isUserInfoSubmitEnabled,
                            onFullNameChange = onFullNameChange,
                            onSubmit = onUserInfoSubmit,
                            onSkip = onSkipUserInfo
                        )
                    }
                }
            }

            // Loading Dialog ("لطفا صبر کنید")
            if (uiState.isLoading) {
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
            color = ShopDecorativeCircle,
            radius = width * 0.48f,
            center = Offset(width * 0.95f, height * 0.08f)
        )

        // Top-left small arc
        drawCircle(
            color = ShopDecorativeCircle.copy(alpha = 0.6f),
            radius = width * 0.28f,
            center = Offset(-width * 0.08f, height * 0.28f)
        )

        // Bottom-left soft circle
        drawCircle(
            color = ShopDecorativeCircle,
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
            ShoppingCartLogoIcon(
                modifier = Modifier.size(90.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // "OnlineShop" styled text using typography & theme colors
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                ) {
                    append("Online")
                }
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                ) {
                    append("Shop")
                }
            },
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(4.dp))

        // "فروشگاه اینترنتی آنلاین شاپ" text
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                ) {
                    append("فروشگاه اینترنتی ")
                }
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                ) {
                    append("آنلاین شاپ")
                }
            },
            style = MaterialTheme.typography.titleLarge,
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
    isSubmitEnabled: Boolean,
    isError: Boolean,
    onEmailChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        // Info description
        Text(
            text = "جهت ورود به فروشگاه اینترنتی آنلاین شاپ ایمیل خود را در کادر زیر وارد کرده و کد ارسالی به ایمیل خود را در مرحله بعد وارد کنید.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = ShopSecondaryText,
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
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Custom Email Input
        CustomTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = "ایمیل خود را وارد کنید",
            keyboardType = KeyboardType.Email,
            isError = isError,
            imeAction = ImeAction.Done,
            onImeAction = onSubmit
        )
        
        // Error message if email is invalid
        AnimatedVisibility(visible = isError) {
            Column {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "ایمیل وارد شده معتبر نیست",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        PrimaryButton(
            text = "تأیید و ادامه",
            onClick = { onSubmit()
                Toast.makeText(context, "کد تایید 1234 یا 0000", Toast.LENGTH_LONG).show()
            },
            enabled = isSubmitEnabled
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
    isSubmitEnabled: Boolean,
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
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Description text with user's email
        Text(
            text = "کد ۴ رقمی ارسال شده به $email خود را وارد کنید",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = ShopSecondaryText,
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
                        .background(ShopTimerBg)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = timerText,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        )

        // Error message if code is incorrect
        AnimatedVisibility(visible = isError) {
            Column {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "کد وارد شده اشتباه است",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    ),
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
            enabled = isSubmitEnabled
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom options row ("ویرایش ایمیل" & "ارسال مجدد کد")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ویرایش ایمیل",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.clickable { onEditEmail() }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onResendCode() }
            ) {
                Text(
                    text = "ارسال مجدد کد",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                ArrowLeftIcon(
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onBackground
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
    isSubmitEnabled: Boolean,
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
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
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
            enabled = isSubmitEnabled
        )

        Spacer(modifier = Modifier.height(12.dp))

        // "عبور از این مرحله" link
        Text(
            text = "عبور از این مرحله",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Medium
            ),
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
        Modifier.border(1.5.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(14.dp))
    } else {
        Modifier.border(1.dp, ShopTextFieldBorder, RoundedCornerShape(14.dp))
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
            .background(MaterialTheme.colorScheme.surface)
            .then(borderModifier)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = ShopPlaceholder
                        )
                    )
                }

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
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

            if (leadingContent != null) {
                Spacer(modifier = Modifier.width(8.dp))
                leadingContent()
            }
        }
    }
}

/**
 * Reusable Vibrant Primary Action Button using theme primary colors
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
            MaterialTheme.colorScheme.primary,
            ShopPrimaryRedGradient
        ) else listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            ShopPrimaryRedGradient.copy(alpha = 0.5f)
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
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        }
    }
}

/**
 * Loading Dialog ("لطفا صبر کنید")
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
            color = MaterialTheme.colorScheme.surface,
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
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.width(16.dp))

                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    strokeWidth = 2.5.dp
                )
            }
        }
    }
}

/**
 * Shopping Cart Logo Icon Placeholder
 */
@Composable
private fun ShoppingCartLogoIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

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
 * Left Arrow Icon for RTL back navigation
 */
@Composable
private fun ArrowLeftIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onBackground
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = w * 0.14f

        drawLine(
            color = tint,
            start = Offset(w * 0.85f, h * 0.5f),
            end = Offset(w * 0.15f, h * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
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

