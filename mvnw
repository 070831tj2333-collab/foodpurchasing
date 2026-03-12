#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements. See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership. The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup script
# ----------------------------------------------------------------------------

if [ -z "$MAVEN_SKIP_RC" ]; then
  if [ -f /usr/local/etc/mavenrc ]; then . /usr/local/etc/mavenrc; fi
  if [ -f /etc/mavenrc ]; then . /etc/mavenrc; fi
  if [ -f "$HOME/.mavenrc" ]; then . "$HOME/.mavenrc"; fi
fi

cygwin=false
darwin=false
mingw=false
case "$(uname)" in
  CYGWIN*) cygwin=true ;;
  MINGW*) mingw=true ;;
  Darwin*)
    darwin=true
    if [ -z "$JAVA_HOME" ]; then
      if [ -x "/usr/libexec/java_home" ]; then
        JAVA_HOME="$(/usr/libexec/java_home)"
        export JAVA_HOME
      else
        JAVA_HOME="/Library/Java/Home"
        export JAVA_HOME
      fi
    fi
    ;;
esac

if [ -z "$JAVA_HOME" ]; then
  if [ -r /etc/gentoo-release ]; then
    JAVA_HOME=$(java-config --jre-home)
  fi
fi

if $cygwin; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=$(cygpath --unix "$JAVA_HOME")
  [ -n "$CLASSPATH" ] && CLASSPATH=$(cygpath --path --unix "$CLASSPATH")
fi

if $mingw; then
  [ -n "$JAVA_HOME" ] && [ -d "$JAVA_HOME" ] && JAVA_HOME="$(cd "$JAVA_HOME" && pwd)"
fi

if [ -z "$JAVA_HOME" ]; then
  javaExecutable="$(which javac)"
  if [ -n "$javaExecutable" ] && ! [ "$(expr "$javaExecutable" : '\([^ ]*\)')" = "no" ]; then
    readLink=$(which readlink)
    if [ -n "$readLink" ]; then
      if $darwin; then
        javaHome="$(dirname "$javaExecutable")"
        javaExecutable="$(cd "$javaHome" && pwd -P)/javac"
      else
        javaExecutable="$(readlink -f "$javaExecutable")"
      fi
      javaHome="$(dirname "$javaExecutable")"
      javaHome=$(expr "$javaHome" : '\(.*\)/bin')
      JAVA_HOME="$javaHome"
      export JAVA_HOME
    fi
  fi
fi

if [ -z "$JAVACMD" ]; then
  if [ -n "$JAVA_HOME" ]; then
    if [ -x "$JAVA_HOME/jre/sh/java" ]; then
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD="$(command -v java)"
  fi
fi

if [ ! -x "$JAVACMD" ]; then
  echo "Error: JAVA_HOME is not defined correctly. We cannot execute $JAVACMD" >&2
  exit 1
fi

find_maven_basedir() {
  if [ -z "$1" ]; then
    echo "Path not specified to find_maven_basedir" >&2
    return 1
  fi
  basedir="$1"
  wdir="$1"
  while [ "$wdir" != '/' ]; do
    if [ -d "$wdir/.mvn" ]; then
      basedir=$wdir
      break
    fi
    if [ -d "${wdir}" ]; then
      wdir="$(cd "$wdir/.." && pwd)"
    fi
  done
  printf '%s' "$(cd "$basedir" && pwd)"
}

concat_lines() {
  if [ -f "$1" ]; then
    tr -s '\r\n' ' ' <"$1"
  fi
}

BASE_DIR=$(find_maven_basedir "$(dirname "$0")")
if [ -z "$BASE_DIR" ]; then
  exit 1
fi

MAVEN_PROJECTBASEDIR=${MAVEN_BASEDIR:-"$BASE_DIR"}
export MAVEN_PROJECTBASEDIR

WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
if [ ! -r "$WRAPPER_JAR" ]; then
  echo "Error: Could not find or read $WRAPPER_JAR" >&2
  exit 1
fi

MAVEN_OPTS="$(concat_lines "$MAVEN_PROJECTBASEDIR/.mvn/jvm.config") $MAVEN_OPTS"

if $cygwin; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=$(cygpath --path --windows "$JAVA_HOME")
  [ -n "$CLASSPATH" ] && CLASSPATH=$(cygpath --path --windows "$CLASSPATH")
  [ -n "$MAVEN_PROJECTBASEDIR" ] && MAVEN_PROJECTBASEDIR=$(cygpath --path --windows "$MAVEN_PROJECTBASEDIR")
fi

export MAVEN_CMD_LINE_ARGS="$MAVEN_CONFIG $*"
exec "$JAVACMD" \
  $MAVEN_OPTS \
  $MAVEN_DEBUG_OPTS \
  -classpath "$WRAPPER_JAR" \
  "-Dmaven.multiModuleProjectDirectory=${MAVEN_PROJECTBASEDIR}" \
  org.apache.maven.wrapper.MavenWrapperMain "$@"
