#!/usr/bin/env bash
# Build bogstandard.war with javac + jar only.
# Twin: build.txt
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

VERSION="${VERSION:-0.12.1}"
BUILD_STAMP="$(date -u +%Y-%m-%dT%H:%M:%SZ)"

SERVLET_API="${SERVLET_API:-}"
if [[ -z "$SERVLET_API" ]]; then
  for c in \
    "${CATALINA_HOME:-/opt/tomcat}/lib/servlet-api.jar" \
    "${CATALINA_HOME:-/usr/share/tomcat10}/lib/servlet-api.jar" \
    /usr/share/tomcat10/lib/servlet-api.jar \
    /usr/share/java/servlet-api-5.jar \
    /usr/share/java/jakarta.servlet-api.jar
  do
    if [[ -f "$c" ]]; then SERVLET_API="$c"; break; fi
  done
fi
if [[ -z "${SERVLET_API}" || ! -f "$SERVLET_API" ]]; then
  echo "ERROR: servlet-api.jar not found. Set SERVLET_API=/path/to/servlet-api.jar" >&2
  exit 1
fi

rm -rf build
mkdir -p build/classes build/war/WEB-INF/classes build/war/WEB-INF/lib

find src -name '*.java' > build/sources.txt
if grep -q '__VERSION__' src/uk/co/donotpassgo/bogstandard/Version.java; then
  mkdir -p build/gen/uk/co/donotpassgo/bogstandard
  sed -e "s/__VERSION__/${VERSION}/" -e "s/__STAMP__/${BUILD_STAMP}/" \
    src/uk/co/donotpassgo/bogstandard/Version.java \
    > build/gen/uk/co/donotpassgo/bogstandard/Version.java
  grep -v 'Version.java' build/sources.txt > build/sources.nov.txt
  echo "build/gen/uk/co/donotpassgo/bogstandard/Version.java" >> build/sources.nov.txt
  javac --release 17 -encoding UTF-8 -cp "$SERVLET_API" -d build/classes @build/sources.nov.txt
else
  javac --release 17 -encoding UTF-8 -cp "$SERVLET_API" -d build/classes @build/sources.txt
fi

cp -a web/. build/war/
cp -a build/classes/. build/war/WEB-INF/classes/
mkdir -p build/war/WEB-INF/classes/sql
cp -a sql/. build/war/WEB-INF/classes/sql/

jar -cf build/bogstandard.war -C build/war .
cp build/bogstandard.war ./bogstandard.war
echo "Built bogstandard.war  version=${VERSION}  stamp=${BUILD_STAMP}"
