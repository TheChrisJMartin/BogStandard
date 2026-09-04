#!/usr/bin/env bash
# Deploy bogstandard.war to Tomcat, cleaning the exploded directory first.
# Twin: deploy.txt
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
WAR="${ROOT}/bogstandard.war"
CATALINA_HOME="${CATALINA_HOME:-/opt/tomcat}"
WEBAPPS="${WEBAPPS:-$CATALINA_HOME/webapps}"

if [[ ! -f "$WAR" ]]; then
  echo "No bogstandard.war — run ./build.sh first" >&2
  exit 1
fi

echo "Cleaning stale exploded WAR at $WEBAPPS/bogstandard"
rm -rf "$WEBAPPS/bogstandard" "$WEBAPPS/bogstandard.war"
cp "$WAR" "$WEBAPPS/bogstandard.war"
echo "Copied $WAR -> $WEBAPPS/bogstandard.war"
echo "Tomcat will explode it. Smoke-test: GET $BASE_URL/health"
