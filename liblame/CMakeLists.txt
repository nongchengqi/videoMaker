# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.6)
project(lame)
get_filename_component(PARENT_DIR ../ ABSOLUTE)
# header
include_directories(src/main/cpp)
include_directories(src/main/cpp/include)
include_directories(src/main/cpp/frontend)
include_directories(src/main/cpp/libmp3lame)
include_directories(src/main/cpp/mpglib)
# pcm utils
aux_source_directory(src/main/cpp LAME_SRC)
aux_source_directory(src/main/cpp/frontend LAME_SHELL_SRC)
aux_source_directory(src/main/cpp/libmp3lame LAME_MP3_SRC)
aux_source_directory(src/main/cpp/mpglib LAME_MPG_SRC)

add_definitions(-DPLATFORM_ANDROID)
set(CMAKE_CXX_FLAGS "-std=c++11 -fexceptions -pthread")
set(CMAKE_C_FLAGS "-DSTDC_HEADERS -DHAVE_LIMITS_H -DHAVE_MPGLIB -DNO_ASM -DMAX_NOGAP=50")


# android wrapper
add_library(lame SHARED
        ${LAME_SRC}
        ${LAME_SHELL_SRC}
        ${LAME_MP3_SRC}
        ${LAME_MPG_SRC}
        )
find_library(android-log log)
target_link_libraries(lame ${android-log})


