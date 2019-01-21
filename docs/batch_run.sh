#!/bin/bash
# run kafka native test shell script

# show runtime environment
SPARK_HOME=/opt/spark

# spark submit job
$SPARK_HOME/bin/spark-submit \
--class com.jhcomn.lambda.app.batch.Main \
--num-executors 2 \
--executor-memory 4G \
--executor-cores 4 \
--total-executor-cores 12 \
--conf spark.default.parallelism=30 \
--jars ./framework.jar \
./app.jar \
1>./batch_log/stdout.log \
2>./batch_log/stderr.log \
&
