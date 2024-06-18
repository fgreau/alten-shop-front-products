CREATE TABLE product
(
    id               BIGINT PRIMARY KEY,
    code             VARCHAR(255) UNIQUE,
    name             VARCHAR(255)   NOT NULL,
    description      TEXT,
    price            DECIMAL(19, 2) NOT NULL,
    quantity         INTEGER        NOT NULL,
    inventory_status VARCHAR(255)   NOT NULL,
    category         VARCHAR(255)   NOT NULL,
    image            VARCHAR(255),
    rating           DECIMAL(3, 1),
    deleted          BOOLEAN NOT NULL DEFAULT false
);
