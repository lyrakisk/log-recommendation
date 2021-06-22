# Recommending Log Placement Based on Code Vocabulary
>  Reproducibility package for my BSc Research Project. 

This repository contains the code that was used to conduct my Bachelor's [Research Project](/docs/log_recommendation_paper.pdf). For now, the code is suitable to analyze only Java projects.

## Requirements
- Java 11
- Python 3.6.9

## Installation
- Run `./utilities/install.sh`

## Usage
The process that was followed consists of 3 steps: 
1. Extract the source code classes from the studied repository. 
2. Extract Java Methods from the classes. 
3. Run experiments.

### 1. Extract classes from the studied repository
- Run `./utilities/extract-classes path-to-repository destination`
- The result of this step is a folder created at the selected destination with all the source code classes in the studied repository.
### 2. Extract Java Methods from the classes.  
- Run `./bin/extract-data source destination`
- The result of this step is a JSON file that contains all the extracted methods along with the following information:
  - `LOC`: lines of code
  - `logged`: True if the method is logged
  - `name`: name of the method
  - `statements`: an array that contains the method's statements
  - `body`: the method's body 

### 3. Run Experiments
- Run `./bin/extract-data source destination`
