--
-- Piotr Majcherczyk
-- nr indeksu 334695
--
-- Deklaracja bazy danych

CREATE TABLE Identity (
	iid BIGINT NOT NULL,
	nick VARCHAR(25),
	PRIMARY KEY (iid)
) DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE Tweet (
	tid BIGINT NOT NULL,
	sender BIGINT,
	text VARCHAR(150),
	PRIMARY KEY (tid)
) DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE Retweet (
	tid BIGINT NOT NULL,
	sender BIGINT,
	receiver BIGINT,
	PRIMARY KEY (tid)
);

CREATE TABLE Base (
	iid BIGINT NOT NULL,
	PRIMARY KEY (iid)
);