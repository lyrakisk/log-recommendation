# Recommending Log Placement Based on Code Vocabulary
>  Reproducibility package for my BSc Research Project. 

This repository contains the code that was used to conduct my Bachelor's [Research Project](https://repository.tudelft.nl/islandora/object/uuid:6c76c94e-2b89-4712-9125-ccc100f764b5?collection=education). For now, the code is suitable to analyze only Java projects.

## Requirements
- Java 11
- Python 3.6.9

## Installation
- Run `./utilities/install.sh` to install the extract-data tool. 
- Run `pip install -r requirements.txt` in your Python environment to install the dependencies. 

## Usage
The process that was followed consists of 3 steps: 
1. Extract the source code classes from the studied repository. 
2. Extract Java Methods from the classes. 
3. Run experiments.

**Note:** Steps 1,2 can be skipped if you wish to use our [datasets](/data) and just run the experiements.

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
- Run `./bin/extract-data dataset_path results_destination_folder`
- This step can take a significant amount of time if the dataset is large. In an average consumer's laptop, the Hadoop dataset, took ~45 minutes to finish all the experiments. 
- The results are 3 csv files that correspond to the Research Questions stated in the paper. Sample results can be found [here](/data/results).
