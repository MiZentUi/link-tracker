--liquibase formatted sql

--changeset mizentui:tables

CREATE TABLE links
(
    id BIGSERIAL PRIMARY KEY,
    url TEXT NOT NULL UNIQUE,
    last_update TIMESTAMP NOT NULL
);

CREATE TABLE chats
(
    id BIGINT PRIMARY KEY
);

CREATE TABLE tags
(
    id BIGSERIAL PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    link_id BIGINT NOT NULL,
    name TEXT NOT NULL,

    FOREIGN KEY (chat_id) REFERENCES chats(id) ON DELETE CASCADE,
    FOREIGN KEY (link_id) REFERENCES links(id)
);

CREATE TABLE links_chats
(
    link_id BIGINT NOT NULL,
    chat_id BIGINT NOT NULL,

    PRIMARY KEY (link_id, chat_id),

    FOREIGN KEY (link_id) REFERENCES links(id) ON DELETE CASCADE,
    FOREIGN KEY (chat_id) REFERENCES chats(id) ON DELETE CASCADE
);
