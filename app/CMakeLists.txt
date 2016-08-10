# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.

include_directories( src/main/cpp/HTKLib )
include_directories( src/main/cpp/HTKTools )

add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             # Associated headers in the same location as their source
             # file are automatically included.
             src/main/cpp/native-lib.cpp
             src/main/cpp/HTKTools/HVite.c
             src/main/cpp/HTKLib/HShell.c
             src/main/cpp/HTKLib/HMem.c
             src/main/cpp/HTKLib/HMath.c
             src/main/cpp/HTKLib/HSigP.c
             src/main/cpp/HTKLib/HAudio.c
             src/main/cpp/HTKLib/HWave.c
             src/main/cpp/HTKLib/HVQ.c
             src/main/cpp/HTKLib/HParm.c
             src/main/cpp/HTKLib/HLabel.c
             src/main/cpp/HTKLib/HModel.c
             src/main/cpp/HTKLib/HUtil.c
             src/main/cpp/HTKLib/HTrain.c
             src/main/cpp/HTKLib/HAdapt.c
             src/main/cpp/HTKLib/HMap.c
             src/main/cpp/HTKLib/HFB.c
             src/main/cpp/HTKLib/HDict.c
             src/main/cpp/HTKLib/HNet.c
             src/main/cpp/HTKLib/HRec.c
             src/main/cpp/HTKLib/esignal.c
             src/main/cpp/HTKLib/esig_edr.c
             src/main/cpp/HTKLib/esig_nat.c
             src/main/cpp/HTKLib/esig_asc.c)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because system libraries are included in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in the
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       native-lib

                       # Links the target library to the log library
                       # included in the NDK.
                       log )

# target_link_libraries( native-lib HTKLib )