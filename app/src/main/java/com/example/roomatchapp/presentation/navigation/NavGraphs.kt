package com.example.roomatchapp.presentation.navigation

import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.NavHostGraph


@NavHostGraph
annotation class RootNavGraph

@NavGraph<RootNavGraph>(start = true)
annotation class StartGraph

@NavGraph<StartGraph>
annotation class OwnerGraph

@NavGraph<StartGraph>
annotation class RoommateGraph