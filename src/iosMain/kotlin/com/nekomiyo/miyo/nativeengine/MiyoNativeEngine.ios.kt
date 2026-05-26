package com.nekomiyo.miyo.nativeengine

actual object MiyoNativeEngine {
    actual fun smoke(): String = "miyo-native-engine:ios-stub"

    actual fun validateProjectJson(json: String): MiyoValidationResult = when {
        json.isBlank() -> MiyoValidationResult(MiyoValidationStatus.EmptyInput, "project json is empty")
        json.trim().let { it.startsWith("{") && it.endsWith("}") } -> {
            MiyoValidationResult(MiyoValidationStatus.Ok, "project json accepted by iOS foundation stub")
        }
        else -> MiyoValidationResult(MiyoValidationStatus.InvalidShape, "project json must look like a JSON object")
    }
}
