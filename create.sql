CREATE TABLE clients (
    id INT PRIMARY KEY NOT NULL,
    credential_type INT NOT NULL,
    credential_number TEXT NOT NULL,
    name TEXT NOT NULL,
    gender TEXT NOT NULL,
    nation TEXT NOT NULL,
    birth TIMESTAMP NOT NULL,

    household TEXT,
    bank TEXT,
    bank_card TEXT,
    bank_phone TEXT,
    phone TEXT,
    shipping_address TEXT
);

CREATE TABLE characters(
    id INT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL
);

CREATE TABLE clients_characters(
    client_id INT NOT NULL REFERENCES clients(id),
    character_id INT NOT NULL REFERENCES characters(id)
);

CREATE TABLE users(
    id INT PRIMARY KEY NOT NULL,
    client_id INT REFERENCES clients(id),
    name TEXT UNIQUE NOT NULL,
    password TEXT
);

CREATE TABLE orders(
    id INT PRIMARY KEY NOT NULL,
    client_id INT REFERENCES clients(id),
    buy_goods TEXT,
    buy_time TIMESTAMP,
    money INT,
    periods INT,
    interest_rate FLOAT,
    region TEXT,
    buy_type TEXT,
    merchant TEXT,
    repayment TEXT
);
