package com.example.roomatchapp.utils

import com.example.roomatchapp.data.model.Match

sealed class SwipeAction {
    data class LikeRoommates(val match: Match): SwipeAction()
    data class LikeProperty(val match: Match): SwipeAction()
    data class FullLike(val match: Match): SwipeAction()
    data class Dislike(val match: Match): SwipeAction()
}
