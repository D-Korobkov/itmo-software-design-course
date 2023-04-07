CREATE TABLE IF NOT EXISTS new_gym_membership_event
(
    id              BIGSERIAL PRIMARY KEY,
    owner           VARCHAR(256)             NOT NULL,
    expires_in_days INT                      NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS prolong_gym_membership_event
(
    id         BIGINT NOT NULL REFERENCES new_gym_membership_event (id),
    extra_days INT    NOT NULL
);

CREATE TABLE IF NOT EXISTS customer_use_pass_event
(
    id         BIGINT                   NOT NULL REFERENCES new_gym_membership_event (id),
    event_type VARCHAR(4)               NOT NULL, -- in/out
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
