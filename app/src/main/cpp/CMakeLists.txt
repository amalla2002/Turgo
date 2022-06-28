cmake_minimum_required(VERSION 3.4.1)

add_library(
        native-lib
        SHARED
        native-lib.cpp)

find_library(
        native-lib
        log)

target_link_libraries(
        native-lib
        ${log_lib})