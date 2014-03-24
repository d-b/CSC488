This directory contains a compiler skeleton that is sufficient 
to develop the complete compiler for Assignments 3 .. 5.

The files in this directory are:

build.xml 		an ANT build script for building the scanner, 
			parser and the driver program.
			Use:
			  'ant help' to get the list of targets
			  'ant gettools' to download JCup and JFlex DO THIS FIRST
			  'ant compiler488' to build everything
			  'ant dist' to create a run-time Jar
			
bin			Binary directory, all compiler class files go here

doc			Documentation directory
doc/javadoc		Javadoc for the compiler skeleton

dist			Distribution directory.  Holds  compiler488.jar file
			produced by 'ant dist'

lib			Library directory (see ant gettools)
			After 'ant gettools' it contains a local copy of the
			libraries required to build the scanner and parser.

FIRST run  "ant gettools"  to install a local copy of JavaCUP and JFlex in lib

To build the complete compiler run  "ant  compiler488"

To build a compiler packaged as a jar file that can be run anywhere
run  "ant  dist"   This creates  dist/compiler488.jar

This compiler can be run using "java -jar dist/compiler488.jar  inputFile.488"

A shell script  RUNCOMPILER.sh  that does this has been provided.
Fill in the shell variable WHERE in this script to the location of 
the directory containing  dist/compiler488.jar

For Assignment 3 you might want to add the compiler flag "-X" to
supress execution of the compiled program since there isn't one yet.

