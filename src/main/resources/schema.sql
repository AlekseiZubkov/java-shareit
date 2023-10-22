--drop table bookings;
--drop table comments;
--drop table items;
--drop table requests;
--drop table users;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);
CREATE TABLE IF NOT EXISTS requests
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR(512)                            NOT NULL,
    requestor   BIGINT                                  NOT NULL,
    CONSTRAINT requests_pk PRIMARY KEY (id),
    CONSTRAINT requests_users_null_fk FOREIGN KEY (requestor) REFERENCES  users(id)
);
CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR(255)                            NOT NULL,
    description VARCHAR(512)                            NOT NULL,
    available   BOOLEAN                                 NOT NULL,
    owner_id    BIGINT,
    request_id  BIGINT,
    CONSTRAINT items_pk PRIMARY KEY (id),
    CONSTRAINT items_users_null_fk FOREIGN KEY (owner_id) REFERENCES  users(id),
    CONSTRAINT items_requests_null_fk FOREIGN KEY (request_id) REFERENCES  requests(id)
);
CREATE TABLE IF NOT EXISTS bookings
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date  TIMESTAMP                               NOT NULL,
    end_date    TIMESTAMP                               NOT NULL,
    item_id     BIGINT                                  NOT NULL,
    booker_id   BIGINT,
    status      VARCHAR(50)                             NOT NULL,
    CONSTRAINT bookings_pk PRIMARY KEY (id),
    CONSTRAINT bookings_users_null_fk FOREIGN KEY (booker_id) REFERENCES  users(id),
    CONSTRAINT bookings_items_null_fk FOREIGN KEY (item_id) REFERENCES  items(id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text        VARCHAR(1024)                           NOT NULL,
    item_id     BIGINT                                  NOT NULL,
    author_id   BIGINT                                  NOT NULL,
    created    TIMESTAMP                                NOT NULL,
    CONSTRAINT comments_pk PRIMARY KEY (id),
    CONSTRAINT comments_users_null_fk FOREIGN KEY (author_id) REFERENCES  users(id),
    CONSTRAINT comments_items_null_fk FOREIGN KEY (item_id) REFERENCES  items(id)
);