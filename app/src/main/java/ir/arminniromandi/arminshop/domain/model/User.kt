package ir.arminniromandi.arminshop.domain.model

data class User(
    val email: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val birthDate: String = "",
    val gender: String = ""
)
