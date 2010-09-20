#! /bin/sh
CLASSPATH=slug.jar
for i in `ls ./lib/*.jar`
do
  CLASSPATH=${CLASSPATH}:${i}
done


java -classpath ${CLASSPATH} -Djava.util.logging.config.file=etc/logging.properties com.ldodds.slug.Scutter $*
