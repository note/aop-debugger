PLAY_HOME=$(dirname $(which play))
ASPECTJRT_PATH="${PLAY_HOME}/repository/cache/org.aspectj/aspectjrt/jars/aspectjrt-1.7.2.jar"
ajc -sourceroots app/debugger -outxml -classpath target/scala-2.10/classes:${ASPECTJRT_PATH} -outjar debugger.jar -1.6
