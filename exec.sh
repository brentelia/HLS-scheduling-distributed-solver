#! /bin/bash 

EXE=$1
INPUT_DIR=$2
OUTPUT_DIR=$3
PAR_LV=$4


echo "Jar file: $1"
echo "Input directory: $2"
echo "Output directory: $3"
echo "Pararellism level: $4"
echo "BEGINNING TESTS"
echo "================================"
if test "$#" -eq 4 ; then
	tests=("DAG" "CFG" "EVIL" "FREE" "XTEA" "MULTI" )

	for file in "${tests[@]}"
	do
		echo "==================================="
                echo "Now testing $file"
	        hadoop fs -rm -r "$OUTPUT_DIR"
	        hadoop fs -put "test/${file}.txt" "$INPUT_DIR"
	        time (hadoop jar ./"$EXE" MapReduce.ExplorerDriver "$INPUT_DIR" "$OUTPUT_DIR" "$PAR_LV")
	        hadoop fs -get "$OUTPUT_DIR"
	        mv output "${file}_output"
	        hadoop fs -rm "$INPUT_DIR"/"${file}.txt" 
	done
	hadoop fs -rm -r "$OUTPUT_DIR"
	echo "===================================="
	echo "Tests done"
	
else
	 echo "Illegal number of parameter: please give the jar path, input and output directory in HDFS and the parallelism level"
fi
