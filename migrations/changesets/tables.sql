--liquibase formatted sql

--chageset mizentui:tables

CREATE TABLE links
(
    id INT PRIMARY KEY,
    url TEXT NOT NULL UNIQUE
)

CREATE TABLE chats
(
    id BIGINT PRIMARY KEY
)

CREATE TABLE tags
(
    id INT PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    link_id BIGINT NOT NULL,
    name TEXT NOT NULL,

    FOREIGN KEY (chat_id) REFERENCES chats(id) ON DELETE CASCADE,
    FOREIGN KEY (link_id) REFERENCES links(id)
);

CREATE TABLE links_chats
(
    link_id INT NOT NULL,
    chat_id BIGINT NOT NULL,

    PRIMARY KEY (link_id, chat_id),

    FOREIGN KEY (link_id) REFERENCES links(id) ON DELETE CASCADE,
    FOREIGN KEY (chat_id) REFERENCES chats(id) ON DELETE CASCADE
);
