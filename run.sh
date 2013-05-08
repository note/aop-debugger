export CLASSPATH=:/usr/share/java/aspectjrt.jar:debugger.jar:.target
export JAVA_OPTS="-javaagent:/usr/share/java/aspectjweaver.jar -classpath ${CLASSPATH}"
JAVA_OPTS="-javaagent:/usr/share/java/aspectjweaver.jar -cp ${CLASSPATH}" play debug run
