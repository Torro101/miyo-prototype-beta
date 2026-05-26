package com.nekomiyo.miyo.nativeengine

enum class MiyoValidationStatus {
    Ok,
    EmptyInput,
    InvalidShape,
    NativeUnavailable,
}

data class MiyoValidationResult(
    val status: MiyoValidationStatus,
    val message: String,
)

expect object MiyoNativeEngine {
    fun smoke(): String
    fun validateProjectJson(json: String): MiyoValidationResult
}
