# ML-Based Logging Recommenders
> Making Logging Great Again by leveraging source code analysis and ML/DL!

Getting started is easy as ABC:
1. Run `./utilities/download-project.sh` to download the subject
2. Run `./utilities/install.sh` to build and "install" the src2ML utility
3. Run `./bin/src2ml download/cloudstack` to classify the source files

[src2ML](src2ML) is an utility that we will be working on to analyze Java projects.
* The file [App.java](src2ML/src/main/java/nl/tudelft/ewi/se/src2ml/App.java) is the current app entry point
* The package [src2ml.project](src2ML/src/main/java/nl/tudelft/ewi/se/src2ml/project) contains API and
  classes to help you retrieve information from a java project.
  If you feel the need to do so, you can reuse these base classes to write your own Main class.

What's next?
* So far we are only retrieving the path of java files and classifying them.
  **How can we extend the existing code to analyze the retrieved source files?**


# Extract classes from the repository
- Run `./utilities/extract-classes source destination`
- We separate this step so that someone can change the script to include test classes. 

# extract-data 

## requirements
- Java 11

## Installation
- Run `./utilities/install.sh`

## Run
- Run `./bin/extract-data source destination`
