--liquibase formatted sql

--chageset mizentui:indexes

CREATE INDEX links_url_idx ON links (url);

CREATE INDEX tags_link_id_idx ON tags (link_id);

CREATE INDEX lc_link_id_idx ON links_chats (link_id);
CREATE INDEX lc_chat_id_idx ON links_chats (chat_id);
