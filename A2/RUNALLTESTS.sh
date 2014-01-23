#!/bin/bash

compile() {
    INPUT=$1
    rm ./errortmp
    java -jar ./compiler/dist/compiler488.jar $INPUT 2>./errortmp >/dev/null
    ERRORS=`wc -l <./errortmp`
    if [ $ERRORS -eq 0 -a $EXPECTED -eq 0 ] ; then return 0 ; fi
    if [ $ERRORS -ne 0  -a $EXPECTED -ne 0 ] ; then return 0 ; fi
    echo "PROBLEM!"
    return 1
}

runtests() {
    SUITE=$1
    for T in tests/$1/*.488; do
        echo $T
        compile $T
        TESTRESULT=$(($TESTRESULT + $?));
    done
}

TESTRESULT=0
EXPECTED=0
runtests passing
EXPECTED=1
runtests failing
echo "Total unpassed tests:"$TESTRESULT
