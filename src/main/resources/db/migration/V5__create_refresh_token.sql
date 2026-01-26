CREATE TABLE refresh_token (
    id BIGSERIAL PRIMARY KEY,
    user_uuid UUID NOT NULL UNIQUE,
    token VARCHAR NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked BOOLEAN,
    FOREIGN KEY(user_uuid) REFERENCES _user(uuid)
)