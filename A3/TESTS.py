#!/usr/bin/env python3
#
# Assignment 3: Test runner
#
# Daniel Bloemendal
#

from __future__ import print_function

import os
import re
import sys
import subprocess

# Test directories
PATHS = {'passing': 'tests/pass',
         'failing': 'tests/fail'}

# Compiler location
COMPILER = 'dist/compiler488.jar'

def test(path, failing = False):
    # Regexp patterns
    patSingleLine = re.compile(r'%@line=(\d+)')
    patMultiLine  = re.compile(r'%@line=\[(\d+(,\d+)*)\]')
    patError      = re.compile(r'(?P<file>[^:]*):(?P<line>[^:]*):(?P<column>[^:]*): (?P<action>S\d+):')
    patSuccess    = re.compile(r'^End of Semantic Analysis$')
    patFailure    = re.compile(r'^Ended Semantic Analysis with failures$')
    patAction     = re.compile(r'(S\d+)')

    # Parse error type
    action = None
    match = patAction.match(os.path.basename(path))
    if match: action = match.groups()[0]

    # Lines where errors are expected
    expected = []

    # If failing parse file for lines
    if failing:
        testfile = open(path)
        for x in testfile:
            l = x.replace(' ', '').replace('\t', '')
            match = patSingleLine.match(l)
            if match: expected.append(int(match.groups()[0]))
            else:
                match = patMultiLine.match(l)
                if match: expected += [int(x) for x in match.groups()[0].split(',')]
        testfile.close()

    # Execute the test
    output = subprocess.check_output(['java', '-jar', COMPILER, '-X', path], stderr=subprocess.STDOUT)
    lines  = output.decode('utf8').replace('\r', '').split('\n')

    # Successful case
    if not failing:
        for x in lines:
            if patSuccess.match(x): return True
        return False
    # Failing case
    else:
        errors  = {}
        failure = False
        for x in lines:
            match = patFailure.match(x)
            if match: failure = True
            match = patError.match(x)
            if not match: continue
            errinfo = match.groupdict()
            errmap = errors.get(int(errinfo['line']), {})
            errmap[errinfo['action']] = errinfo
            errors[int(errinfo['line'])] = errmap
        # If a failure did not occur bail out
        if not failure: return False
        # First check expected errors
        for num in expected:
            if (num not in errors) \
            or (action and (action not in errors[num] \
                         or errors[num][action]['action'] != action)): return False
        return True

def run(directory, failing = False):
    failures = 0
    for path in sorted(filter(lambda x: x.endswith('.488'), os.listdir(directory))):
        target = os.path.join(directory, path)
        print('Testing {}... '.format(target), end='')
        sys.stdout.flush()
        result = test(target, failing)
        print('SUCCESS!' if result else 'FAILURE!')
        if not result: failures += 1
    return failures

def main():
    print('Running passing tests...')
    failures  = run(PATHS['passing'], False)
    print()
    print('Running failing tests...')
    failures += run(PATHS['failing'], True)
    print()
    print('Total failures: {}'.format(failures))

if __name__ == '__main__':
    main()
