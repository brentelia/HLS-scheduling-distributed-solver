# Distributed-HLS-scheduling-solver-

An Hadoop application for solving the scheduling problem in a High level synthesis context using cluster computing.
For more info about thw work done see the report file at https://github.com/brentelia/HLS-scheduling-distributed-solver/blob/master/Latex/Relazione.pdf


### Usage

The software need to be execute into an hadoop filesystem and it has been tested inside both a java 7 and java 8 environment.

In order to run the program first of all the Apache Hadoop Libraries must be installed onto the machine (here the version 2.6.0 is used).
Then a jar file must be creaded starting from the source code find in this repository (it can be easily done using java ide like eclipse or similar)
Finally the jar input file must be inserted into the hadoop filesystem using
'''
hdfs -put <input file> <path to the input file directory on the filesystem>  
'''

After this you can run the jar file
```
hadoop jar .<your jar name> MapReduce.ExplorerDriver <input directory path> <output directory path> <parallelism level>
```
Note that the output directory is created when the application is run and shouldn't exist berfore that.

#Credits
* [Apache Hadoop](https://hadoop.apache.org/) - The framework this application is based on.
## Authors

* **Andrea Caucchiolo** 
* **Elia Brentarolli**