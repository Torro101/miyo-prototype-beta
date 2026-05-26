#include "miyo_engine.h"

#include <cstddef>

namespace {

constexpr const char* kSmokeMessage = "miyo-native-engine:ok";
constexpr const char* kValidationOk = "project json accepted by foundation stub";
constexpr const char* kValidationEmpty = "project json is empty";
constexpr const char* kValidationInvalidShape = "project json must look like a JSON object";

bool has_non_whitespace(const char* text, std::size_t length) {
    if (text == nullptr || length == 0) {
        return false;
    }

    for (std::size_t i = 0; i < length; ++i) {
        const char c = text[i];
        if (c != ' ' && c != '\n' && c != '\r' && c != '\t') {
            return true;
        }
    }

    return false;
}

MiyoValidationResult result(MiyoValidationStatus status, const char* message) {
    return MiyoValidationResult{status, message};
}

} // namespace

const char* miyo_engine_smoke(void) {
    return kSmokeMessage;
}

MiyoValidationResult miyo_engine_validate_project_json(const char* json, std::size_t length) {
    if (!has_non_whitespace(json, length)) {
        return result(MIYO_VALIDATION_EMPTY_INPUT, kValidationEmpty);
    }

    std::size_t start = 0;
    while (start < length && (json[start] == ' ' || json[start] == '\n' || json[start] == '\r' || json[start] == '\t')) {
        ++start;
    }

    std::size_t end = length;
    while (end > start && (json[end - 1] == ' ' || json[end - 1] == '\n' || json[end - 1] == '\r' || json[end - 1] == '\t')) {
        --end;
    }

    if (json[start] != '{' || json[end - 1] != '}') {
        return result(MIYO_VALIDATION_INVALID_SHAPE, kValidationInvalidShape);
    }

    return result(MIYO_VALIDATION_OK, kValidationOk);
}
