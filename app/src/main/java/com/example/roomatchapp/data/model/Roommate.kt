package com.example.roomatchapp.data.model

//import androidx.room.Entity
//import com.example.roomatchapp.data.base.Constants.Collections.ROOMMATES
import kotlinx.serialization.Serializable

//@Entity(tableName = ROOMMATES)
@Serializable
data class Roommate(
    val id: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val birthDate: String,
    val password: String,
    val refreshToken: String?=null,
    val profilePicture: String? = null, //Optional, can be null and will set by user later
    val work: String,
    val gender: Gender,
    val attributes: List<Attribute>,
    val hobbies: List<Hobby>,
    val lookingForRoomies: List<LookingForRoomiesPreference>,
    val lookingForCondo: List<LookingForCondoPreference>,
    val roommatesNumber: Int,
    val minPropertySize: Int,
    val maxPropertySize: Int,
    val minPrice: Int,
    val maxPrice: Int,
    val personalBio: String? = null,
    val preferredRadiusKm: Int = 10,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val resetToken: String? = null,
    val resetTokenExpiration: Long? = null
)


@Serializable
enum class Gender(val lable: String) {
    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other")
}

@Serializable
enum class Attribute {
    SMOKER,
    STUDENT,
    PET_LOVER,
    HAS_PET,
    VEGGIE,
    CLEAN,
    NIGHT_JOB,
    TAKEN,
    KOSHER,
    JEWISH,
    MUSLIM,
    CHRISTIAN,
    REMOTE_JOB,
    ATHEIST,
    QUIET
}

@Serializable
enum class Hobby{
    MUSICIAN,
    SPORT,
    COOKER,
    PARTY,
    TV,
    GAMER,
    ARTIST,
    DANCER,
    WRITER,
    YOGA,
    READER,
    TRAVELER
}


@Serializable
enum class CondoPreference {
    BALCONY,
    ELEVATOR,
    PET_VERIFY,
    SHELTER,
    FURNISHED,
    PARKING,
    ROOFTOP,
    GARDEN,
    GYM
}

@Serializable
data class LookingForCondoPreference(
    val preference: CondoPreference,
    val weight: Double,
    val setWeight: Boolean = false,
)

@Serializable
data class LookingForRoomiesPreference(
    val attribute: Attribute,
    val weight: Double,
    val setWeight: Boolean,
)


