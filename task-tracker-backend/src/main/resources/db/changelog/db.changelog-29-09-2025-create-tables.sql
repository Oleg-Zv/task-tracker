--liquibase formate

--changeset zhv:create-table-users
CREATE TABLE IF NOT EXISTS users(
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(64) NOT NULL UNIQUE,
    firstname VARCHAR(32) NOT NULL,
    lastname VARCHAR(64) NOT NULL,
    password VARCHAR(255)NOT NULL,
    role VARCHAR(32) NOT NULL
);

--changeset zhv:create-table-tasks
CREATE TABLE IF NOT EXISTS tasks(
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(32) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(32) NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id)ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    done_at TIMESTAMP WITH TIME ZONE
 );
