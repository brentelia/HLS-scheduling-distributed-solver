# Distributed HLS scheduling solver

This repository contains an Hadoop application for solving the scheduling problem in a High level synthesis context using cluster computing.
For more info about the work done see the report file at https://github.com/brentelia/HLS-scheduling-distributed-solver/blob/master/Latex/Relazione.pdf

## Structure
The repository is divided into the following folders:
* Latex : this folder contains all the files used to generate the pdf report.
* MapReduce : this folder contains all the java class that are used inside the Hadoop framework (i.e. that extends the hadoop classes) or the class which implements core funcions for the program.
* structure: this folder contains the class used to represent data in the program.
* test : this folder contains the files used to test the application functionalities locally (not on the cluster)
* util : this folder contains other files that are used to support the application.
* exec.sh and exec_log.sh : two scripts used to run multiple test in sequence using only one script. 
##  Usage

The software need to be execute into an hadoop filesystem and it has been tested inside both a java 7 and java 8 environment.

In order to run the program first of all the Apache Hadoop Libraries must be installed onto the machine (here the version 2.6.0 is used).
Then a jar file must be creaded starting from the source code find in this repository (it can be easily done using java ide like eclipse or similar)
Finally the jar input file must be inserted into the hadoop filesystem using
```
hdfs -put <input file> <path to the input file directory on the filesystem>  
```

After this you can run the jar file
```
hadoop jar <your jar name> MapReduce.ExplorerDriver <input directory path> <output directory path> <parallelism level>
```
Note that the output directory is created when the application is run and shouldn't exist berfore that.

Otherwise you can run one of the two bash scripts in this repository to run all the tests. In order to do that just run one of them (note that to run the exec_log.sh, which redirect all the outputs into a log.txt file, the exec.sh file is required) in the same directory of the tests folder. The script parameters are the same as the hadoop command show before:
```
./exec.sh <jar file> MapReduce.ExplorerDriver <input directory path> <output directory path> <parallelism level>
```

## Credits
* [Apache Hadoop](https://hadoop.apache.org/) - The framework this application is based on.

## Authors

* **Andrea Caucchiolo** 
* **Elia Brentarolli**
