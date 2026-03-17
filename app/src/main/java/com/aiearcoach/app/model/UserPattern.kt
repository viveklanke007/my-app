package com.aiearcoach.app.model

data class UserPattern(
    val event: String,
    val pattern: String,
    val time: String,
    val emotion: String? = null
)
