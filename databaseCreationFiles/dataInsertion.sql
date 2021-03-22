SET SEARCH_PATH TO worldproject;

--Suppression de tous les éléments des tables

DELETE
FROM worldproject.legend_race;
DELETE
FROM worldproject.legend;
DELETE
FROM worldproject.character_language;
DELETE
FROM worldproject.character;
DELETE
FROM worldproject.country_race;
DELETE
FROM worldproject.race;
DELETE
FROM worldproject.language;
DELETE
FROM worldproject.alphabet;
DELETE
FROM worldproject.rank;
DELETE
FROM worldproject.country_side;
DELETE
FROM worldproject.war;
DELETE
FROM worldproject.side;
DELETE
FROM worldproject.accord_country;
DELETE
FROM worldproject.country;
DELETE
FROM worldproject.continent;
DELETE
FROM worldproject.accord;
DELETE
FROM worldproject.accordtype;
DELETE
FROM worldproject.article;
DELETE
FROM worldproject.world;
DELETE
FROM worldproject.user_account;

ALTER SEQUENCE article_id_seq RESTART WITH 1;
ALTER SEQUENCE world_id_seq RESTART WITH 1;
ALTER SEQUENCE user_account_id_seq RESTART WITH 1;

INSERT INTO
   user_account(email, firstname, lastname, password)
VALUES
   ('david.gonzalez@citycable.ch', 'David', 'González León', '1234');

INSERT INTO
   world(name, iduser, description)
VALUES
('testWorld', 1, 'This world is used for tests of the overall worldApp application');

INSERT INTO
   article(idworld, name, content)
VALUES
   ('1', 'nain', 'Basic dwarvish alphabet');
INSERT INTO
   alphabet(idarticle, writingsystem)
VALUES
   ('1', 'weird writing');
