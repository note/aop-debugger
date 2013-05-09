PLAY_HOME=$(dirname $(which play))
ASPECTJRT_PATH="${PLAY_HOME}/repository/cache/org.aspectj/aspectjrt/jars/aspectjrt-1.7.2.jar"
echo $ASPECTJRT_PATH
ajc -sourceroots debugger -outxml -classpath ../.target:${ASPECTJRT_PATH} -outjar ../debugger.jar
