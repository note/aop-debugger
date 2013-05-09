PLAY_HOME=$(dirname $(which play))
ASPECTJWEAVER_PATH="${PLAY_HOME}/repository/cache/org.aspectj/aspectjweaver/jars/aspectjweaver-1.7.2.jar"
export CLASSPATH=:debugger.jar:.target
JAVA_OPTS="-javaagent:${ASPECTJWEAVER_PATH} -cp ${CLASSPATH}" play debug run
