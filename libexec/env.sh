
## Define realpath() if not defined.
#type realpath 2>&1 | grep -q function;
#if [ $? != 0 ]; then
# Resolve real path of given path.
realpath()
{
	local path="$1"; shift;
	local UNAME=`uname`;
	if [ "x$UNAME" = 'xLinux' ]; then
		readlink -f "$path";
	elif [ "x$UNAME" = 'xFreeBSD' ]; then
		realpath "$path";
	elif [ "x$UNAME" = 'xDarwin' ]; then
		# See: http://codesnippets.joyent.com/tag/realpath
		# Or use coreutils from macports instead:
		/opt/local/bin/greadlink -f "$path";
	else
		echo "$path"; # fallback to nothing.
	fi;
}
#fi;

#type relpath 2>&1 | grep -q function;
#if [ $? != 0 ]; then
relpath()
{
	local path="$1"; shift;
	local base=`pwd`;
	echo "$path" | sed -e "s,^$base/,,";
}
#fi;


export APP_HOME=${APP_HOME:="`dirname $0`/.."}

# Define other CHTNL paths.
DIR_BIN="$APP_HOME/bin";
DIR_ETC="$APP_HOME/etc";
DIR_LIB="$APP_HOME/lib";
DIR_LIBEXEC="$APP_HOME/libexec";
DIR_LOG="$APP_HOME/logs"
DIR_PID="$APP_HOME/pids"
if [ "$DIR_PID" = "" ]; then
    DIR_PID="$APP_HOME/pids"
fi

CLASSPATH="`relpath "$DIR_ETC"`";
for jar_file in $APP_HOME/*.jar; do
	jar_file=`relpath "$jar_file"`;
	CLASSPATH="$CLASSPATH:$jar_file";
done;
for jar_file in $DIR_LIB/*.jar; do
	jar_file=`relpath "$jar_file" "$APP_HOME"`;
	CLASSPATH="$CLASSPATH:$jar_file";
done;

#echo "CLASSPATH: $CLASSPATH";
#echo "----------------------------------------------------------------------";

if [ -e "/home/y/bin/java" ]; then
	JAVA_HOME="/home/y/share/yjava_jdk/java/"
fi


JAVA_LIBRARY_PATH="$JAVA_LIBRARY_PATH:$HADOOP_HOME/lib/native/Linux-amd64-64"
JVM_OPTS="$JVM_OPTS -DAPP.name=APP";
JVM_OPTS="$JVM_OPTS -DAPP.APP_HOME=$APP_HOME";
JVM_OPTS="$JVM_OPTS -DAPP.version=@ANT_VERSION@";
JVM_OPTS="$JVM_OPTS -Djava.library.path=$JAVA_LIBRARY_PATH";
JVM_OPTS="$JVM_OPTS -Dfile.encoding=UTF-8";
JVM_OPTS="$JVM_OPTS -Xmx1024m -Xms1024m -Xmn128m -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=70 -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+HeapDumpOnOutOfMemoryError -verbose:gc -Xloggc:gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=3 -XX:GCLogFileSize=10M";
