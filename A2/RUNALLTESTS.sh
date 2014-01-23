#!/bin/bash

compile() {
    INPUT=$1
    java -jar ./compiler/dist/compiler488.jar $INPUT
}

runtests() {
    SUITE=$1
    for T in tests/$1/*.488; do
        echo $T
        compile $T
    done
}

runtests passing
runtests failing

