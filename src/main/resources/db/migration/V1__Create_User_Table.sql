CREATE TABLE IF NOT EXISTS user_table (
    id SERIAL PRIMARY KEY,
    user_name TEXT NOT NULL,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    password_hash TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS hash_salt_table (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES user_table(id) NOT NULL,
    salt TEXT NOT NULL
);