#! /bin/sh
#  Location of directory containing  dist/compiler488.jar
WHERE=.
#  Compiler reads one source file from command line argument
#  Output to standard output 
java -jar $WHERE/dist/compiler488.jar  $1
exit 0
