#! /bin/bash
javafiles=$(find src -name *.java)
javadoc -cp .:lib/javax.mail.jar:lib/poi-4.0.1.jar -d doc/ -noqualifier all -link http://docs.oracle.com/javase/8/docs/api/ -link https://docs.oracle.com/javase/8/javafx/api/ $javafiles
