cmake_minimum_required(VERSION 3.22)

project(miyo_engine LANGUAGES CXX)

add_library(miyo_engine STATIC
    src/miyo_engine.cpp
)

target_include_directories(miyo_engine
    PUBLIC
        ${CMAKE_CURRENT_SOURCE_DIR}/include
)

target_compile_features(miyo_engine PUBLIC cxx_std_17)

set_target_properties(miyo_engine PROPERTIES
    CXX_EXTENSIONS OFF
)
