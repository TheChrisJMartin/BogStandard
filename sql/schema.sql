-- Bog Standard schema — PostgreSQL 16
-- Applied automatically on boot. Every statement is idempotent.

CREATE TABLE IF NOT EXISTS users (
  id              BIGSERIAL PRIMARY KEY,
  email           TEXT NOT NULL UNIQUE,
  display_name    TEXT NOT NULL,
  password_hash   TEXT NOT NULL,
  password_salt   TEXT NOT NULL,
  verified        BOOLEAN NOT NULL DEFAULT FALSE,
  role            TEXT NOT NULL DEFAULT 'member',
  suspended       BOOLEAN NOT NULL DEFAULT FALSE,
  leaderboard_opt_in BOOLEAN NOT NULL DEFAULT FALSE,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS email_tokens (
  id          BIGSERIAL PRIMARY KEY,
  user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  purpose     TEXT NOT NULL,
  token       TEXT NOT NULL UNIQUE,
  expires_at  TIMESTAMPTZ NOT NULL,
  used_at     TIMESTAMPTZ,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS email_tokens_user_idx ON email_tokens(user_id);

CREATE TABLE IF NOT EXISTS remember_tokens (
  id          BIGSERIAL PRIMARY KEY,
  user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  token_hash  TEXT NOT NULL UNIQUE,
  expires_at  TIMESTAMPTZ NOT NULL,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS venues (
  id              BIGSERIAL PRIMARY KEY,
  name            TEXT NOT NULL,
  venue_type      TEXT NOT NULL,
  address         TEXT,
  postcode        TEXT,
  town            TEXT,
  lat             NUMERIC(9,6) NOT NULL,
  lng             NUMERIC(9,6) NOT NULL,
  geo_bucket      INTEGER,
  opening_hours   TEXT,
  access_type     TEXT NOT NULL DEFAULT 'free',
  paid_amount     TEXT,
  contactless     BOOLEAN NOT NULL DEFAULT FALSE,
  status          TEXT NOT NULL DEFAULT 'published',
  source          TEXT NOT NULL DEFAULT 'user',
  verified        BOOLEAN NOT NULL DEFAULT FALSE,
  possibly_closed BOOLEAN NOT NULL DEFAULT FALSE,
  created_by      BIGINT REFERENCES users(id),
  notes           TEXT,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS venues_geo_idx ON venues(lat, lng);
CREATE INDEX IF NOT EXISTS venues_bucket_idx ON venues(geo_bucket);
CREATE INDEX IF NOT EXISTS venues_status_idx ON venues(status);
CREATE INDEX IF NOT EXISTS venues_town_idx ON venues(town);

CREATE TABLE IF NOT EXISTS facilities (
  id             BIGSERIAL PRIMARY KEY,
  venue_id       BIGINT NOT NULL REFERENCES venues(id) ON DELETE CASCADE,
  facility_type  TEXT NOT NULL,
  notes          TEXT,
  UNIQUE (venue_id, facility_type)
);
CREATE INDEX IF NOT EXISTS facilities_venue_idx ON facilities(venue_id);

CREATE TABLE IF NOT EXISTS amenities (
  id           BIGSERIAL PRIMARY KEY,
  facility_id  BIGINT NOT NULL REFERENCES facilities(id) ON DELETE CASCADE,
  key          TEXT NOT NULL,
  value        TEXT NOT NULL DEFAULT 'unknown',
  UNIQUE (facility_id, key)
);

CREATE TABLE IF NOT EXISTS reviews (
  id             BIGSERIAL PRIMARY KEY,
  user_id        BIGINT NOT NULL REFERENCES users(id),
  facility_id    BIGINT NOT NULL REFERENCES facilities(id) ON DELETE CASCADE,
  overall        INTEGER NOT NULL CHECK (overall BETWEEN 1 AND 5),
  cleanliness    INTEGER CHECK (cleanliness BETWEEN 1 AND 5),
  smell          INTEGER CHECK (smell BETWEEN 1 AND 5),
  safety         INTEGER CHECK (safety BETWEEN 1 AND 5),
  stocked        INTEGER CHECK (stocked BETWEEN 1 AND 5),
  accessibility  INTEGER CHECK (accessibility BETWEEN 1 AND 5),
  body           TEXT,
  visited_at     DATE,
  status         TEXT NOT NULL DEFAULT 'published',
  helpful_up     INTEGER NOT NULL DEFAULT 0,
  helpful_down   INTEGER NOT NULL DEFAULT 0,
  created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (user_id, facility_id)
);
CREATE INDEX IF NOT EXISTS reviews_facility_idx ON reviews(facility_id);

CREATE TABLE IF NOT EXISTS review_amenities (
  review_id    BIGINT NOT NULL REFERENCES reviews(id) ON DELETE CASCADE,
  key          TEXT NOT NULL,
  present      BOOLEAN NOT NULL,
  PRIMARY KEY (review_id, key)
);

CREATE TABLE IF NOT EXISTS photos (
  id           BIGSERIAL PRIMARY KEY,
  venue_id     BIGINT REFERENCES venues(id) ON DELETE CASCADE,
  review_id    BIGINT REFERENCES reviews(id) ON DELETE SET NULL,
  user_id      BIGINT NOT NULL REFERENCES users(id),
  path         TEXT NOT NULL,
  caption      TEXT,
  status       TEXT NOT NULL DEFAULT 'pending',
  created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS reports (
  id           BIGSERIAL PRIMARY KEY,
  user_id      BIGINT REFERENCES users(id),
  venue_id     BIGINT REFERENCES venues(id) ON DELETE CASCADE,
  review_id    BIGINT REFERENCES reviews(id) ON DELETE CASCADE,
  photo_id     BIGINT REFERENCES photos(id) ON DELETE CASCADE,
  reason       TEXT NOT NULL,
  notes        TEXT,
  status       TEXT NOT NULL DEFAULT 'open',
  resolved_by  BIGINT REFERENCES users(id),
  resolved_at  TIMESTAMPTZ,
  created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS reports_status_idx ON reports(status);

CREATE TABLE IF NOT EXISTS votes (
  user_id      BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  review_id    BIGINT NOT NULL REFERENCES reviews(id) ON DELETE CASCADE,
  value        INTEGER NOT NULL CHECK (value IN (-1, 1)),
  PRIMARY KEY (user_id, review_id)
);

CREATE TABLE IF NOT EXISTS submissions (
  id           BIGSERIAL PRIMARY KEY,
  user_id      BIGINT NOT NULL REFERENCES users(id),
  kind         TEXT NOT NULL,
  venue_id     BIGINT REFERENCES venues(id),
  payload      TEXT NOT NULL,
  status       TEXT NOT NULL DEFAULT 'pending',
  reviewer_id  BIGINT REFERENCES users(id),
  review_note  TEXT,
  created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
  reviewed_at  TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS contributions (
  user_id           BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
  reviews           INTEGER NOT NULL DEFAULT 0,
  venues_added      INTEGER NOT NULL DEFAULT 0,
  edits_approved    INTEGER NOT NULL DEFAULT 0,
  photos            INTEGER NOT NULL DEFAULT 0,
  helpful_received  INTEGER NOT NULL DEFAULT 0,
  badges            TEXT NOT NULL DEFAULT ''
);

CREATE TABLE IF NOT EXISTS audit_log (
  id           BIGSERIAL PRIMARY KEY,
  actor_id     BIGINT REFERENCES users(id),
  action       TEXT NOT NULL,
  target_type  TEXT,
  target_id    BIGINT,
  detail       TEXT,
  created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS rate_limits (
  key          TEXT PRIMARY KEY,
  hits         INTEGER NOT NULL DEFAULT 0,
  window_start TIMESTAMPTZ NOT NULL DEFAULT now()
);
