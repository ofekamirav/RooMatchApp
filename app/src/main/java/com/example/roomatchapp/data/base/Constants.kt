package com.example.roomatchapp.data.base

typealias EmptyCallback = () -> Unit
typealias StringCallback = (String?) -> Unit

object Constants {

    object Collections{
        const val ROOMMATES = "roommates"
        const val OWNERS = "owners"
        const val PROPERTIES = "properties"
        const val MATCHES = "matches"
        const val CACHE_ENTITIES = "cache_entities"
        const val OWNER_ANALYTICS = "owner_analytics"
        const val SUGGESTED_MATCHES = "suggested_matches"
    }
}