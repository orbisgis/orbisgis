DROP TABLE "gisapps";
CREATE TABLE "gisapps" ("gis" VARCHAR(10), "users" INTEGER, "version" VARCHAR, "id" SERIAL PRIMARY KEY);
INSERT INTO "gisapps" VALUES('orbisgis', 10, null, DEFAULT);
INSERT INTO "gisapps" VALUES('grass', 9, 1.1, DEFAULT);
INSERT INTO "gisapps" VALUES('kosmo', 8, 1.1, DEFAULT);
INSERT INTO "gisapps" VALUES('openjump', 7, 'a lot', DEFAULT);
INSERT INTO "gisapps" VALUES('qgis', 6, 'I do not know', DEFAULT);
INSERT INTO "gisapps" VALUES('orbiscad', 5, 1.0, DEFAULT);
