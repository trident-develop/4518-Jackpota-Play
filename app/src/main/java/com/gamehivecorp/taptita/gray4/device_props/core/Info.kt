package com.gamehivecorp.taptita.gray4.device_props.core

fun interface Info {
    suspend fun collect(vararg args: Any?): String
}