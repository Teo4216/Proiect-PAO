-- Schema baza de date — Aplicatie Bancara
-- Compatible cu SQLite (implicit) si MySQL

DROP TABLE IF EXISTS carduri_bancare;
DROP TABLE IF EXISTS tranzactii;
DROP TABLE IF EXISTS conturi;
DROP TABLE IF EXISTS clienti;

CREATE TABLE clienti (
    id     INTEGER PRIMARY KEY AUTOINCREMENT,
    cnp    TEXT    NOT NULL UNIQUE,
    nume   TEXT    NOT NULL,
    email  TEXT
);

CREATE TABLE conturi (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    iban       TEXT    NOT NULL UNIQUE,
    tip        TEXT    NOT NULL,
    sold       REAL    NOT NULL DEFAULT 0.0,
    dobanda    REAL             DEFAULT 0.0,
    client_cnp TEXT    NOT NULL,
    FOREIGN KEY (client_cnp) REFERENCES clienti(cnp) ON DELETE CASCADE
);

CREATE TABLE tranzactii (
    id        TEXT PRIMARY KEY,
    suma      REAL NOT NULL,
    tip       TEXT NOT NULL,
    data      TEXT NOT NULL,
    cont_iban TEXT,
    FOREIGN KEY (cont_iban) REFERENCES conturi(iban) ON DELETE SET NULL
);

CREATE TABLE carduri_bancare (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    numar_card TEXT    NOT NULL UNIQUE,
    cont_iban  TEXT    NOT NULL,
    FOREIGN KEY (cont_iban) REFERENCES conturi(iban) ON DELETE CASCADE
);
