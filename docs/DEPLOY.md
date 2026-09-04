# Deploying Bog Standard

Context path `/bogstandard` behind Apache at
`https://games.donotpassgo.co.uk/bogstandard`.

## Prerequisites

- Tomcat 10 or 11 (Jakarta Servlet 5/6)
- PostgreSQL 16 with an empty database `bogstandard`
- `postgresql.jar` (JDBC 42.x) in `$CATALINA_HOME/lib` — **do not** bundle it
- Environment variables from `setenv.example.sh` in `$CATALINA_HOME/bin/setenv.sh`

## Build

```bash
export SERVLET_API=$CATALINA_HOME/lib/servlet-api.jar
./build.sh
```

## Deploy (avoid the stale exploded-WAR trap)

```bash
CATALINA_HOME=/opt/tomcat ./deploy.sh
```

If you copy the WAR by hand:

```bash
rm -rf $CATALINA_HOME/webapps/bogstandard $CATALINA_HOME/webapps/bogstandard.war
cp bogstandard.war $CATALINA_HOME/webapps/bogstandard.war
```

## Smoke test

GET `/bogstandard/health` — expect HTTP 200 and JSON with version + db status.

On first boot the listener applies `sql/schema.sql` then `sql/seed-gloucestershire.sql`.
Both are idempotent.
