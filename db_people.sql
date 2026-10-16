-- Database: db_people
-- TP5 DPBO - PeopleData (Java Swing + MySQL)
--
-- Kolom disesuaikan dengan program TP4:
--   id, nama, thn_lahir, kategori, + keahlian (atribut tambahan TP4, nilai 1 - 10)
-- Kolom "email" dari contoh modul diganti menjadi "keahlian".
--
-- File ini aman di-import berulang kali: tabel person dibuat ulang
-- dan 10 data awal dimasukkan kembali.

CREATE DATABASE IF NOT EXISTS db_people CHARACTER SET utf8mb4;
USE db_people;

DROP TABLE IF EXISTS person;

CREATE TABLE person (
  id varchar(255) PRIMARY KEY,
  nama varchar(255) NOT NULL,
  thn_lahir int NOT NULL,
  kategori varchar(255) NOT NULL,
  keahlian int NOT NULL
);

INSERT INTO person (id, nama, thn_lahir, kategori, keahlian) VALUES
('KMN001', 'Aditya Pratama', 2002, 'Backend', 8),
('KMN002', 'Bunga Maharani', 2003, 'Frontend', 7),
('KMN003', 'Citra Lestari', 2001, 'UI/UX', 9),
('KMN004', 'Dimas Saputra', 2004, 'Mobile', 6),
('KMN005', 'Eka Putri', 2000, 'Data Science', 8),
('KMN006', 'Fajar Nugroho', 1999, 'DevOps', 9),
('KMN007', 'Gita Ramadhani', 2005, 'Frontend', 4),
('KMN008', 'Hendra Wijaya', 2003, 'Backend', 5),
('KMN009', 'Intan Permata', 2004, 'Data Science', 6),
('KMN010', 'Joko Prasetyo', 1998, 'Mobile', 10);
