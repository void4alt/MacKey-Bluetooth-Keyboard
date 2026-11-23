#!/bin/sh

#
# Gradle wrapper script for Unix
#

# Resolve links: $0 may be a link
PRG="$0"
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done
SAVED="`pwd`"
cd "`dirname \"$PRG\"`/" >/dev/null
APP_HOME="`pwd -P`"
cd "$SAVED" >/dev/null

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn () {
    echo "$*"
}

die () {
    echo
    echo "$*"
    echo
    exit 1
}

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
else
    JAVACMD="java"
fi

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# Execute Gradle
if [ -f "$CLASSPATH" ]; then
    exec "$JAVACMD" "-Dorg.gradle.appname=$APP_BASE_NAME" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
else
    # Fallback to system gradle if wrapper jar is not yet present
    if command -v gradle >/dev/null 2>&1; then
        exec gradle "$@"
    else
        echo "Gradle wrapper JAR not found at $CLASSPATH."
        echo "Please open the project in Android Studio (which automatically provisions Gradle and wrapper JARs)"
        echo "or run: gradle wrapper"
        exit 1
    fi
fi

