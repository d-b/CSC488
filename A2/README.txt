This directory contains a minimal compiler skeleton that
is sufficient to develop the programming language grammar
for Assignment 2.

The files in this directory are:

build.xml 		an ANT build script for building the scanner, 
			parser and the driver program.
			Use:
			  'ant help' to get the list of targets
			  'ant gettools' to download JCup and JFlex DO THIS FIRST
			  'ant compile' or 'ant compiler488' to build everything
			  'ant dist' to create a run-time Jar
			
bin			Binary directory, all compiler class files go here

doc			Documentation directory
doc/javadoc		Javadoc for the compiler skeleton

dist			Distribution directory.  Holds  compiler488.jar file
			produced by 'ant dist'

lib			Library directory (see ant gettools)
			After 'ant gettools' it contains a local copy of the
			libraries required to build the scanner and parser.

compiler488/compiler

	Compiler488.java	a very simple driver program that invokes
				the scanner/parser on a file.

compiler488/parser

	csc488.cup		An input file for the JavaCUP parser generator.
				There's space at the end for you to add your
				grammar for Assignment 2.

	csc488.flex	An input file for the JFlex scanner generator.
			The file as provided is a correct scanner for the
			project language.  You should NOT change this file.

To run the test driver first build a distribution using 'ant dist',
then run using 'java -jar dist/compiler488.jar  inputFile'
A shell script  RUNCOMPILER.sh  that does this has been provided.
