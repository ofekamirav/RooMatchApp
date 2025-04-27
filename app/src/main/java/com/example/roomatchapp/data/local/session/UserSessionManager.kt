package com.example.roomatchapp.data.local.session

import com.example.roomatchapp.data.local.dao.PropertyOwnerDao
import com.example.roomatchapp.data.local.dao.RoommatesDao
import com.example.roomatchapp.data.model.Roommate

class UserSessionManager(
    private val roommateDao: RoommatesDao,
    private val ownerDao: PropertyOwnerDao
) {

    suspend fun saveRoommate(user: Roommate) {
        roommateDao.insert(user)
    }

//    suspend fun getLoggedInUser(): Roommate? {
//        return userDao.getCurrentUser()
//    }
//
//    suspend fun logout() {
//        userDao.deleteAllUsers()
//    }
}
