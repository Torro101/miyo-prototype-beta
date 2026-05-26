#pragma once

#include <stddef.h>

#ifdef __cplusplus
extern "C" {
#endif

#define MIYO_ENGINE_VERSION "0.1.0-foundation"

typedef enum MiyoValidationStatus {
    MIYO_VALIDATION_OK = 0,
    MIYO_VALIDATION_EMPTY_INPUT = 1,
    MIYO_VALIDATION_INVALID_SHAPE = 2
} MiyoValidationStatus;

typedef struct MiyoValidationResult {
    MiyoValidationStatus status;
    const char* message;
} MiyoValidationResult;

const char* miyo_engine_smoke(void);
MiyoValidationResult miyo_engine_validate_project_json(const char* json, size_t length);

#ifdef __cplusplus
}
#endif
