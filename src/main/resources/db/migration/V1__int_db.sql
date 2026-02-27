CREATE TABLE achats
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    datep      datetime NULL,
    quantity DOUBLE NOT NULL,
    product_id VARCHAR(255) NULL,
    CONSTRAINT pk_achats PRIMARY KEY (id)
);

CREATE TABLE produits
(
    ref  VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    stock DOUBLE NOT NULL,
    CONSTRAINT pk_produits PRIMARY KEY (ref)
);

CREATE TABLE ventes
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    date_p     date         NOT NULL,
    quantity DOUBLE NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    CONSTRAINT pk_ventes PRIMARY KEY (id)
);

ALTER TABLE produits
    ADD CONSTRAINT uc_produits_name UNIQUE (name);

ALTER TABLE achats
    ADD CONSTRAINT FK_ACHATS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES produits (ref);

ALTER TABLE ventes
    ADD CONSTRAINT FK_VENTES_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES produits (ref);