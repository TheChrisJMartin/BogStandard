#!/usr/bin/env bash
# Promote a member to admin. Twin: promote-admin.txt
set -euo pipefail
EMAIL="${1:?usage: ./promote-admin.sh user@example.com}"
psql "${DATABASE_URL:-dbname=$PGDATABASE}" -v ON_ERROR_STOP=1 \
  -c "UPDATE users SET role='admin', verified=TRUE WHERE lower(email)=lower('$EMAIL');"
echo "Promoted $EMAIL to admin (and marked verified)."
