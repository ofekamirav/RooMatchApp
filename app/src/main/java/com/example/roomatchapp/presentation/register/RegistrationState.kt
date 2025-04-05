package com.example.roomatchapp.presentation.register

data class RegistrationState(
    val userType: UserType? = null, // OWNER / ROOMMATE
    val email: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val birthDate: String = "",
    val gender: Gender? = null,
    val work: String = "",
    val attributes: List<Attribute> = emptyList(),
    val hobbies: List<Hobby> = emptyList(),
    val lookingForRoomies: List<LookingForRoomiesPreference> = emptyList(),
    val roommatesNumber: Int? = null,
    val minPropertySize: Int? = null,
    val maxPropertySize: Int? = null,
    val minPrice: Int? = null,
    val maxPrice: Int? = null,
    val condoPreference: List<CondoPreference> = emptyList()
)

enum class UserType {
    OWNER,
    ROOMMATE
}

enum class Gender {
    MALE,
    FEMALE,
    OTHER
}

enum class Attribute {
    SMOKER,
    STUDENT,
    PET_LOVER,
    PET_OWNER,
    VEGETARIAN,
    CLEAN,
    NIGHT_WORKER,
    IN_RELATIONSHIP,
    KOSHER,
    JEWISH,
    MUSLIM,
    CHRISTIAN,
    REMOTE_WORKER,
    ATHEIST,
    QUIET
}

enum class Hobby{
    MUSICIAN,
    SPORT,
    COOKER,
    PARTY,
    TV,
    GAMER,
    ARTIST,
    DANCER,
    WRITER
}

enum class CondoPreference {
    BALCONY,
    ELEVATOR,
    PET_ALLOWED,
    SHELTER,
    FURNISHED,
    PARKING
}

data class LookingForRoomiesPreference(
    val attribute: Attribute,
    val isDealbreaker: Boolean = false
)


