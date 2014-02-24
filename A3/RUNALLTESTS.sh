#!/bin/bash
#
# Assignment : 3
# Group      : 09
# Members    : g0dbloem, g2mccorm, g2scotts, g2watson
# File       : RUNALLTESTS.sh
#
# Test runner
#

# Where the compiler is located
WHERE=.

#
# Verify existence of parser
#
if [ ! -f $WHERE/dist/compiler488.jar ]; then
    echo "Parser not found!"
    exit 1
fi

#
# decider(INPUT): Decider for the CSC488 language
#
# return (INPUT \in LANGUAGE) ? 1 : 0;
#
function decider() {
    # Argument list
    INPUT=$1

    # Execute compiler and collect results
    ERRORS=`java -jar $WHERE/dist/compiler488.jar $INPUT 2>&1 | grep 'Semantic Error:' | wc -l`
    if [ $ERRORS -eq 0 ]; then return 1; else return 0; fi
}

#
# test(INPUT, MEMBER): Asserts that decider(INPUT) iff MEMBER
#
# return decider(INPUT) == MEMBER
#
function test() {
    # Argument list
    INPUT=$1
    MEMBER=$2

    # Run decider on input and return result
    decider $INPUT
    [ $? -eq $MEMBER ];
    return $?
}

#
# tests(SUITE, MEMBER): Run decider on each file in SUITE asserting that decider(INPUT) iff MEMBER
#
# return failure count
#
function tests() {
    # Argument list
    SUITE=$1
    MEMBER=$2

    # Failures
    FAILURES=0

    # Run all tests in input suite
    for TEST in tests/$1/*.488; do
        echo -n "Testing $TEST... "
        test $TEST $MEMBER
        RESULT=$?
        FAILURES=`expr $FAILURES + $RESULT`
        if [ $RESULT -ne 0 ]; then
            echo "FAILED!"
        else
            echo "SUCCESS!"
        fi
    done

    return $FAILURES
}

#
# passing(): Run all passing tests
#
# return failure count
#
function passing() {
    echo "Running passing tests..."
    tests pass 1
    return $?
}

#
# failing(): Run all failing tests
#
# return failure count
#
function failing() {
    echo "Running failing tests..."
    tests fail 0
    return $?
}

#
# all(): Run all tests and print results
#
function all() {
    passing
    PASSING_ERRORS=$?
    failing
    FAILING_ERRORS=$?
    ERRORS=`expr $PASSING_ERRORS + $FAILING_ERRORS`
    echo "Testing failures: $ERRORS"
}

# Run all tests
all
