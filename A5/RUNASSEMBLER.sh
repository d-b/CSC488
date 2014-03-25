#! /bin/sh
#  Location of directory containing  dist/assembler488.jar
WHERE=.
#  Compiler reads one source file from command line argument
#  Compiler option flags can be provided.
#  Output to standard output 
java -jar $WHERE/dist/assembler488.jar  $*
exit 0
