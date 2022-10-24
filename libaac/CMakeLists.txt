# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.6)
project(aacenc)
get_filename_component(PARENT_DIR ../ ABSOLUTE)
# header
include_directories(src/main/cpp)
include_directories(src/main/cpp/include)
include_directories(src/main/cpp/frontend)
include_directories(src/main/cpp/libfaac)
include_directories(src/main/cpp/libfaac/kiss_fft)
# pcm utils
aux_source_directory(src/main/cpp AAC_ENC_SRC)
include_directories(src/main/cpp/include AAC_INC_SRC)
include_directories(src/main/cpp/frontend AAC_SHELL_SRC)
aux_source_directory(src/main/cpp/libfaac LIB_AAC_SRC)
aux_source_directory(src/main/cpp/libfaac/kiss_fft KISS_FFT_SRC)

add_definitions(-DHAVE_CONFIG_H)
#SET(CMAKE_CXX_FLAGS "-std=c++11")             # c++11
#SET(CMAKE_CXX_FLAGS_DEBUG "$ENV{CXXFLAGS} -O0 -Wall -g2 -ggdb")
#SET(CMAKE_CXX_FLAGS_RELEASE "$ENV{CXXFLAGS} -O3 -Wall")

# android wrapper
add_library(aacenc SHARED
        ${AAC_ENC_SRC}
        ${AAC_INC_SRC}
        ${AAC_SHELL_SRC}
        ${LIB_AAC_SRC}
        ${KISS_FFT_SRC}
        src/main/cpp/frontend/main.c
        src/main/cpp/frontend/mp4write.c
        src/main/cpp/frontend/input.c
        src/main/cpp/frontend/getopt.c
        )
find_library(android-log log)
target_link_libraries(aacenc ${android-log})


