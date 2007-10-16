DROP TABLE "gisapps" IF EXISTS;
CREATE CACHED TABLE "gisapps" ("id" IDENTITY PRIMARY KEY, "gis" VARCHAR(10), "users" INTEGER, "version" VARCHAR);
INSERT INTO "gisapps" VALUES(0, 'orbisgis', 10, null);
INSERT INTO "gisapps" VALUES(1, 'grass', 9, 1.1);
INSERT INTO "gisapps" VALUES(2, 'kosmo', 8, 1.1);
INSERT INTO "gisapps" VALUES(3, 'openjump', 7, 'a lot');
INSERT INTO "gisapps" VALUES(4, 'qgis', 6, 'I do not know');
INSERT INTO "gisapps" VALUES(5, 'orbiscad', 5, 1.0);
