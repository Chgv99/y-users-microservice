CREATE TABLE IF NOT EXISTS user_detail (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    first_name VARCHAR(250),
    last_name VARCHAR(250),
    FOREIGN KEY(user_id) REFERENCES _user(id)
);
