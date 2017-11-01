# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_VERBOSE_MAKEFILE on)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add_library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.


add_library( # Specifies the name of the library
             libswscale

             # Sets the library as static library
             STATIC IMPORTED)
set_target_properties( libswscale
                       PROPERTIES
                       IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/src/main/ffmpeg/libs/libswscale/libswscale.a)

add_library( # Specifies the name of the library
             libavutil

             # Sets the library as static library
             STATIC IMPORTED)
set_target_properties( libavutil
                       PROPERTIES
                       IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/src/main/ffmpeg/libs/libavutil/libavutil.a)

# Specifies a path to native header files.
include_directories(${CMAKE_SOURCE_DIR}/src/main/ffmpeg/libs/libswscale/include/)
include_directories(${CMAKE_SOURCE_DIR}/src/main/ffmpeg/libs/libavutil/include/)

add_library( # Specifies the name of the library.
             demo_scaling

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/ffmpeg/demo_scaling.c )
include_directories(src/main/ffmpeg/include/)
target_include_directories( demo_scaling PRIVATE
                            ${CMAKE_SOURCE_DIR}/src/main/ffmpeg/libs/libswscale/include/
                            ${CMAKE_SOURCE_DIR}/src/main/ffmpeg/libs/libavutil/include/
                            )
target_link_libraries( demo_scaling libswscale libavutil )