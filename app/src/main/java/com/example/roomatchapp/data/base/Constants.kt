package com.example.roomatchapp.data.base

typealias EmptyCallback = () -> Unit
typealias StringCallback = (String?) -> Unit

object Constants {

    object Collections{
        const val ROOMMATES = "roommates"
        const val OWNERS = "owners"
        const val PROPERTIES = "properties"
    }
}