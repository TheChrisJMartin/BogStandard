#!/usr/bin/env bash
# Example Tomcat setenv.sh fragment for Bog Standard.
# Twin: setenv.example.txt

export CATALINA_OPTS="$CATALINA_OPTS -Dfile.encoding=UTF-8"

export PGHOST="${PGHOST:-localhost}"
export PGPORT="${PGPORT:-5432}"
export PGDATABASE="${PGDATABASE:-bogstandard}"
export PGUSER="${PGUSER:-bogstandard}"
export PGPASSWORD="${PGPASSWORD:-change-me}"

export BASE_URL="${BASE_URL:-https://games.donotpassgo.co.uk/bogstandard}"
export SESSION_SECRET="${SESSION_SECRET:-change-this-session-secret}"

export SMTP_HOST="${SMTP_HOST:-smtp.example.com}"
export SMTP_PORT="${SMTP_PORT:-587}"
export SMTP_USER="${SMTP_USER:-}"
export SMTP_PASS="${SMTP_PASS:-}"
export SMTP_FROM="${SMTP_FROM:-Bog Standard <noreply@donotpassgo.co.uk>}"

export TILE_URL="${TILE_URL:-https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png}"
export TILE_ATTR="${TILE_ATTR:-&copy; OpenStreetMap contributors}"
export UPLOAD_DIR="${UPLOAD_DIR:-/var/lib/bogstandard/uploads}"
export DISPOSABLE_DOMAINS="${DISPOSABLE_DOMAINS:-mailinator.com,guerrillamail.com,10minutemail.com,tempmail.com}"
