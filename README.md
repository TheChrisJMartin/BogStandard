# Bog Standard

Mobile-first public toilet finder and review platform. Registered members
rate loos **per facility type** (Male / Female / Gender-neutral / Accessible /
Baby-change / Changing Places), record amenities, find the nearest usable
loo, and help keep the map accurate.

- **Live:** https://games.donotpassgo.co.uk/bogstandard
- **Stack:** Jakarta Servlets on Tomcat 10/11, PostgreSQL 16, Leaflet
- **Build:** `javac` + `jar` only — no Maven, no bundled runtime jars
- **Version:** 0.12.1

## Quick start

```bash
cp setenv.example.sh local.setenv.sh
./build.sh
./deploy.sh
```

See `docs/DEPLOY.md` for the full pipeline.

Seed venues are flagged `source=seed`, `verified=false` and must be
field-verified before they are treated as authoritative.
