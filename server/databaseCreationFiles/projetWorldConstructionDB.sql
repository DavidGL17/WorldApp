SET SEARCH_PATH TO worldProject;

DROP SCHEMA IF EXISTS worldProject CASCADE;
CREATE SCHEMA worldProject;

/*TODO Structurer les classes, puis gérer les commandes depuis la. Regarder la structure de comment on gérait l'affichage dans le miniprojet 2*/
CREATE TABLE user_account (
   id        SERIAL,
   email     VARCHAR(255) NOT NULL UNIQUE,
   CHECK ( email LIKE '%@%.%'),
   firstName VARCHAR(255) NOT NULL,
   lastName  VARCHAR(255) NOT NULL,
   password  VARCHAR(255) NOT NULL,
   PRIMARY KEY (id)
);

/*Amélioration possible : un monde peut être modifié par plusieurs personnes*/
CREATE TABLE world (
   id          SERIAL,
   name        VARCHAR(255) NOT NULL,
   idUser      INTEGER      NOT NULL,
   description TEXT,
   CONSTRAINT fk_world_idUser FOREIGN KEY (idUser) REFERENCES user_account (id) ON UPDATE CASCADE ON DELETE CASCADE,
   PRIMARY KEY (id)
);

CREATE TABLE article (
   id          SERIAL,
   idWorld     INTEGER      NOT NULL,
   name        VARCHAR(255) NOT NULL,
   content     TEXT,
   last_update DATE DEFAULT CURRENT_DATE,
   CONSTRAINT fk_article_idWorld FOREIGN KEY (idWorld) REFERENCES world (id) ON UPDATE CASCADE ON DELETE CASCADE,
   PRIMARY KEY (id)
);

CREATE TABLE alphabet (
   idArticle     INTEGER,
   writingSystem VARCHAR(255),
   CONSTRAINT fk_alphabet_idArticle FOREIGN KEY (idArticle) REFERENCES article (id) ON UPDATE CASCADE ON DELETE CASCADE,
   PRIMARY KEY (idArticle)
);

CREATE TABLE language (
   idArticle  INTEGER,
   idAlphabet INTEGER NOT NULL,
   CONSTRAINT fk_language_idArticle FOREIGN KEY (idArticle) REFERENCES article (id) ON UPDATE CASCADE ON DELETE CASCADE,
   CONSTRAINT fk_language_idAlphabet FOREIGN KEY (idAlphabet) REFERENCES alphabet (idArticle) ON UPDATE CASCADE,
   PRIMARY KEY (idArticle)
);

CREATE TABLE race (
   idArticle  INTEGER,
   idLanguage INTEGER NOT NULL,
   heightMin  INTEGER NOT NULL,
   heightMax  INTEGER NOT NULL,
   CONSTRAINT fk_race_idArticle FOREIGN KEY (idArticle) REFERENCES article (id) ON UPDATE CASCADE ON DELETE CASCADE,
   CONSTRAINT fk_race_idLanguage FOREIGN KEY (idLanguage) REFERENCES language (idArticle) ON UPDATE CASCADE,
   PRIMARY KEY (idArticle)
);

CREATE TABLE legend (
   idArticle INTEGER,
   CONSTRAINT fk_legend_idArticle FOREIGN KEY (idArticle) REFERENCES article (id) ON UPDATE CASCADE ON DELETE CASCADE,
   PRIMARY KEY (idArticle)
);


CREATE TABLE legend_race (
   idLegend INTEGER,
   idRace   INTEGER,
   CONSTRAINT fk_legend_race_idLegend FOREIGN KEY (idLegend) REFERENCES legend (idArticle) ON UPDATE CASCADE ON DELETE CASCADE,
   CONSTRAINT fk_legend_race_idRace FOREIGN KEY (idRace) REFERENCES race (idArticle) ON UPDATE CASCADE ON DELETE CASCADE,
   PRIMARY KEY (idLegend, idRace)
);

CREATE TABLE character (
   idArticle INTEGER,
   idRace    INTEGER,
   CONSTRAINT fk_character_idArticle FOREIGN KEY (idArticle) REFERENCES article (id) ON UPDATE CASCADE ON DELETE CASCADE,
   CONSTRAINT fk_character_idRace FOREIGN KEY (idRace) REFERENCES race (idArticle) ON UPDATE CASCADE,
   PRIMARY KEY (idArticle)
);

CREATE TABLE character_language (
   idCharacter INTEGER,
   idLanguage  INTEGER,
   CONSTRAINT fk_character_language_idCharacter FOREIGN KEY (idCharacter) REFERENCES character (idArticle) ON UPDATE CASCADE ON DELETE CASCADE,
   CONSTRAINT fk_character_language_idLanguage FOREIGN KEY (idLanguage) REFERENCES language (idArticle) ON UPDATE CASCADE ON DELETE CASCADE,
   PRIMARY KEY (idCharacter, idLanguage)
);

CREATE TABLE continent (
   idArticle INTEGER,
   CONSTRAINT fk_continent_idArticle FOREIGN KEY (idArticle) REFERENCES article (id) ON UPDATE CASCADE ON DELETE CASCADE,
   PRIMARY KEY (idArticle)
);

CREATE TABLE country (
   idArticle   INTEGER,
   idContinent INTEGER,
   CONSTRAINT fk_country_idArticle FOREIGN KEY (idArticle) REFERENCES article (id) ON UPDATE CASCADE ON DELETE CASCADE,
   CONSTRAINT fk_country_idContinent FOREIGN KEY (idContinent) REFERENCES continent (idArticle) ON UPDATE CASCADE ON DELETE NO ACTION,
   PRIMARY KEY (idArticle)
);

CREATE TABLE country_race (
   idCountry INTEGER,
   idRace    INTEGER,
   CONSTRAINT fk_country_race_idCountry FOREIGN KEY (idCountry) REFERENCES country (idArticle) ON UPDATE CASCADE ON DELETE CASCADE,
   CONSTRAINT fk_country_race_idRace FOREIGN KEY (idRace) REFERENCES race (idArticle) ON UPDATE CASCADE ON DELETE CASCADE,
   PRIMARY KEY (idCountry, idRace)
);

CREATE TABLE rank (
   idArticle        INTEGER,
   idCountry        INTEGER NOT NULL,
   salary           DOUBLE PRECISION,
   additionalRights TEXT,
   CONSTRAINT fk_rank_idArticle FOREIGN KEY (idArticle) REFERENCES article (id) ON UPDATE CASCADE ON DELETE CASCADE,
   CONSTRAINT fk_rank_idCountry FOREIGN KEY (idCountry) REFERENCES country (idArticle) ON UPDATE CASCADE ON DELETE CASCADE,
   PRIMARY KEY (idArticle)
);

CREATE TABLE side (
   idArticle INTEGER,
   CONSTRAINT fk_rank_idArticle FOREIGN KEY (idArticle) REFERENCES article (id) ON UPDATE CASCADE ON DELETE CASCADE,
   PRIMARY KEY (idArticle)
);

CREATE TABLE country_side (
   idCountry INTEGER,
   idSide    INTEGER,
   CONSTRAINT fk_country_side_idCountry FOREIGN KEY (idCountry) REFERENCES country (idArticle) ON UPDATE CASCADE ON DELETE CASCADE,
   CONSTRAINT fk_country_side_idSide FOREIGN KEY (idSide) REFERENCES side (idArticle) ON UPDATE CASCADE ON DELETE CASCADE,
   PRIMARY KEY (idCountry, idSide)
);

CREATE TABLE war (
   idArticle     INTEGER,
   idSide1       INTEGER      NOT NULL,
   idSide2       INTEGER      NOT NULL,
   CHECK (idSide1 != idSide2),
   dateBeginning VARCHAR(255) NOT NULL,
   dateEnd       VARCHAR(255),
   deathCount    INTEGER,
   CONSTRAINT fk_war_idArticle FOREIGN KEY (idArticle) REFERENCES article (id) ON UPDATE CASCADE ON DELETE CASCADE,
   CONSTRAINT fk_war_idSide1 FOREIGN KEY (idSide1) REFERENCES side (idArticle) ON UPDATE CASCADE,
   CONSTRAINT fk_war_idSide2 FOREIGN KEY (idSide2) REFERENCES side (idArticle) ON UPDATE CASCADE,
   PRIMARY KEY (idArticle)
);

CREATE TABLE accordType (
   idArticle INTEGER,
   CONSTRAINT fk_accordType_idArticle FOREIGN KEY (idArticle) REFERENCES article (id) ON UPDATE CASCADE ON DELETE CASCADE,
   PRIMARY KEY (idArticle)
);

CREATE TABLE accord (
   idArticle     INTEGER,
   idAccordType  INTEGER      NOT NULL,
   dateBeginning VARCHAR(255) NOT NULL,
   dateEnd       VARCHAR(255),
   CONSTRAINT fk_accord_idArticle FOREIGN KEY (idArticle) REFERENCES article (id) ON UPDATE CASCADE ON DELETE CASCADE,
   CONSTRAINT fk_accord_idAccordType FOREIGN KEY (idAccordType) REFERENCES accordType (idArticle) ON UPDATE CASCADE,
   PRIMARY KEY (idArticle)
);

CREATE TABLE accord_country (
   idAccord  INTEGER,
   idCountry INTEGER,
   CONSTRAINT fk_accord_country_idAccord FOREIGN KEY (idAccord) REFERENCES accord (idArticle) ON UPDATE CASCADE ON DELETE CASCADE,
   CONSTRAINT fk_accord_country_idCountry FOREIGN KEY (idCountry) REFERENCES country (idArticle) ON UPDATE CASCADE ON DELETE CASCADE,
   PRIMARY KEY (idAccord, idCountry)
);

DROP TRIGGER IF EXISTS insert_update_date_trigger ON article;
DROP FUNCTION IF EXISTS insert_update_date_trigger();
CREATE FUNCTION update_update_date()
   RETURNS TRIGGER
   LANGUAGE plpgsql AS
$$
BEGIN
   new.last_update = NOW();
   RETURN new;
END;
$$;

CREATE TRIGGER update_update_date_trigger
   BEFORE UPDATE
   ON article
   FOR EACH ROW
EXECUTE PROCEDURE update_update_date();

