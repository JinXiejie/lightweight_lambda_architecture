#!/bin/bash
# run kafka native test shell script

# show runtime environment
SPARK_HOME=/opt/spark

# spark submit job
$SPARK_HOME/bin/spark-submit \
--class com.jhcomn.lambda.app.speed.Main \
--num-executors	2 \
--executor-memory 4G \
--executor-cores 4 \
--total-executor-cores 8 \
--conf spark.default.parallelism=20 \
--jars ./framework.jar \
./app.jar \
1>./speed_log/stdout.log \
2>./speed_log/stderr.log \
&
#--executor-memory 8G \
#--total-executor-cores 12 \
