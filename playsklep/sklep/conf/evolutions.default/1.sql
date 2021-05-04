# --- !Ups

PRAGMA foreign_keys = true;

CREATE TABLE "actor" (
                         "actorId" VARCHAR NOT NULL PRIMARY KEY,
                         "firstName" VARCHAR NOT NULL,
                         "surname" VARCHAR NOT NULL
);

CREATE TABLE "comment" (
                           "commentId" VARCHAR NOT NULL PRIMARY KEY ,
                           "comment" VARCHAR NOT NULL,
                           "userId" VARCHAR NOT NULL,
                           "movieId" VARCHAR NOT NULL,
                           FOREIGN KEY ("userId") REFERENCES [user] ("userId") ON DELETE CASCADE ,
                           FOREIGN KEY ("movieId") REFERENCES movie("movieId") ON DELETE CASCADE
);

CREATE TABLE "director" (
                            "directorId" VARCHAR NOT NULL PRIMARY KEY,
                            "firstName" VARCHAR NOT NULL,
                            "surname" VARCHAR NOT NULL
);


CREATE TABLE "movie" (
                         "movieId" VARCHAR NOT NULL PRIMARY KEY,
                         "title" VARCHAR NOT NULL,
                         "publicationDate" VARCHAR NOT NULL,
                         "price" FLOAT NOT NULL,
                         "details" TEXT NOT NULL
);

CREATE TABLE "movieAndActor" (
                               "movieId" VARCHAR NOT NULL,
                               "actorId" VARCHAR NOT NULL,
                               PRIMARY KEY ("movieId", "actorId"),
                               FOREIGN KEY ("movieId") REFERENCES movie("movieId") ON DELETE CASCADE,
                               FOREIGN KEY ("actorId") REFERENCES actor("actorId") ON DELETE CASCADE
);

CREATE TABLE "movieAndDirector" (
                                  "movieId" VARCHAR NOT NULL,
                                  "directorId" VARCHAR NOT NULL,
                                  PRIMARY KEY ("movieId", "directorId"),
                                  FOREIGN KEY ("movieId") REFERENCES movie("movieId") ON DELETE CASCADE,
                                  FOREIGN KEY ("directorId") REFERENCES director("directorId") ON DELETE CASCADE
);

CREATE TABLE "movieAndType" (
                               "movieId" VARCHAR NOT NULL,
                               "typeId" VARCHAR NOT NULL,
                               PRIMARY KEY ("movieId", "typeId"),
                               FOREIGN KEY ("movieId") REFERENCES movie("movieId") ON DELETE CASCADE,
                               FOREIGN KEY ("typeId") REFERENCES type("typeId") ON DELETE CASCADE
);

CREATE TABLE "movieToOrder" (
                             "movieId" VARCHAR NOT NULL,
                             "price" FLOAT NOT NULL,
                             "orderId" VARCHAR NOT NULL,
                             PRIMARY KEY ("orderId", "movieId"),
                             FOREIGN KEY ("orderId") REFERENCES order("orderId") ON DELETE CASCADE,
                             FOREIGN KEY ("movieId") REFERENCES movie("movieId") ON DELETE CASCADE
);

CREATE TABLE "type" (
                             "typeId" VARCHAR NOT NULL PRIMARY KEY,
                             "type" VARCHAR NOT NULL
);

CREATE TABLE "order" (
                         "orderId" VARCHAR NOT NULL PRIMARY KEY ,
                         "userId" VARCHAR NOT NULL,
                         "payId" VARCHAR NOT NULL,
                         FOREIGN KEY ("userId") REFERENCES user("userId") ON DELETE CASCADE,
                         FOREIGN KEY ("payId") REFERENCES pay("payId") ON DELETE CASCADE
);

CREATE TABLE "pay" (
                           "payId" VARCHAR NOT NULL PRIMARY KEY,
                           "method" VARCHAR NOT NULL
);

CREATE TABLE "rate" (
                          "rateId" VARCHAR NOT NULL PRIMARY KEY ,
                          "userId" VARCHAR NOT NULL,
                          "movieId" VARCHAR NOT NULL,
                          "result" INT NOT NULL,
                          FOREIGN KEY ("userId") REFERENCES user("userId") ON DELETE CASCADE,
                          FOREIGN KEY ("movieId") REFERENCES movie("movieId") ON DELETE CASCADE
);

CREATE TABLE "user" (
                        "userId" VARCHAR NOT NULL PRIMARY KEY,
                        "firstName" VARCHAR NOT NULL,
                        "surname" VARCHAR NOT NULL,
                        "email" VARCHAR NOT NULL,
                        "role" VARCHAR NOT NULL
);


INSERT INTO actor VALUES ('1', 'Actor 1 Name', 'Actor 1 surname');
INSERT INTO actor VALUES ('2', 'Actor 2 Name', 'Actor 2 surname');

INSERT INTO director VALUES ('1', 'Director 1 Name', 'Director 1 surname');
INSERT INTO director VALUES ('2', 'Director 2 Name', 'Director 2 surname');

INSERT INTO movie VALUES ('1', 'Film title 1', '2015', 25.0, 'details 1');
INSERT INTO movie VALUES ('2', 'Film title 2', '2016', 15.0, 'details 2');

INSERT INTO movieAndActor VALUES ('1', '1');
INSERT INTO movieAndActor VALUES ('2', '2');

INSERT INTO movieAndDirector VALUES ('1', '1');
INSERT INTO movieAndDirector VALUES ('2', '2');

INSERT INTO movieAndType VALUES ('1', '1');
INSERT INTO movieAndType VALUES ('2', '2');

INSERT INTO type VALUES ('1', 'Type 1');
INSERT INTO type VALUES ('2', 'Type 2');

INSERT INTO payment VALUES ('1', 'Method 1');
INSERT INTO payment VALUES ('2', 'Method 2');



# --- !Downs

DROP TABLE actor IF EXISTS;
DROP TABLE comment IF EXISTS;
DROP TABLE director IF EXISTS;
DROP TABLE movie IF EXISTS;
DROP TABLE movieAndActor IF EXISTS;
DROP TABLE movieAndDirector IF EXISTS;
DROP TABLE movieAndType IF EXISTS;
DROP TABLE movieToOrder IF EXISTS;
DROP TABLE type IF EXISTS;
DROP TABLE [order] IF EXISTS;
DROP TABLE pay IF EXISTS;
DROP TABLE rate IF EXISTS;
DROP TABLE [user] IF EXISTS;
