#!/usr/bin/env python3
#
# Assignment 3: Test runner
#
# Daniel Bloemendal
# Oren Watson
#
#   Documentation of test annotations in .488 files.
#  --------------------------------------------------
# %@output=Rest of line
# The text given in each annotation must be outputted in the order
# that the annotations appear in the file.
# %@input=Rest of line
# The text given is concatenated to the input to the program.
#


from __future__ import print_function

import os
import re
import sys
import subprocess
import tempfile

# Test directories
PATHS = {'passing': 'testing/pass',
         'failing': 'testing/fail'}

# Compiler location
COMPILER = 'dist/compiler488.jar'

def test(path, failing = False):
    # Regexp patterns
    patSuccess     = re.compile(r'^End of Code Generation$')
    patFailure     = re.compile(r'^Ended Code Generation with failures$')
    patFailBounds  = re.compile(r'^Error: subscript out of range for array')
    patFailExec    = re.compile(r'^Exception during Machine Execution')
    patOutputLine  = re.compile(r'.*%[\s%]*@output=(.*)')
    patInputLine   = re.compile(r'.*%[\s%]*@input=(.*)')
    patStartOutput = re.compile(r'Start Execution')

    # Lines of correct output:
    correctOutput = []

    # File for input lines:
    inFile = tempfile.TemporaryFile()

    # If passing, parse file for input and output lines
    if not failing:
        testfile = open(path)
        for x in testfile:
            match = patOutputLine.search(x)
            if match:
                correctOutput.append(match.groups()[0])
            else:
                match = patInputLine.search(x)
                if match:
                    inFile.write(bytes(match.groups()[0]+'\r\n','utf8'))
    inFile.seek(0)

    # Execute the test
    try:
        output = subprocess.check_output(['java', '-jar', COMPILER, '-B', 's', path], stdin=inFile, stderr=subprocess.STDOUT)
        lines  = output.decode('utf8').replace('\r', '').split('\n')
    except:
        # On exception consider the test a failure
        return False

    # Find a pattern
    def findpattern(pat, lines):
        for line in lines:
            if(pat.match(line)):
                return True
        return False

    # Successful case
    if not failing:
        # We expect successful code generation
        if not findpattern(patSuccess, lines):
            return False
        # Check output if specified
        success = True
        if correctOutput:
            i = 0
            comparingOutput = False
            for x in lines:
                if comparingOutput == True:
                    if i >= 0 and i < len(correctOutput) and x.strip() != correctOutput[i].strip():
                        if(success): print()
                        print('expected[{}]={} output[{}]={}'.format(i, correctOutput[i].strip(), i, x))
                        success = False
                    i = i + 1
                if patStartOutput.match(x):
                    comparingOutput = True
                    i = -1
        return success
    # Failing case
    else:
        failures = [findpattern(patFailure, lines),
                    findpattern(patFailBounds, lines),
                    findpattern(patFailExec, lines)]
        return any(failures)

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
