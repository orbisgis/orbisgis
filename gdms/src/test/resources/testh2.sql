DROP TABLE IF EXISTS POINT;
CREATE TABLE point (id IDENTITY PRIMARY KEY, nom VARCHAR(10), nom2 VARCHAR(100), length DECIMAL(20, 2), area DOUBLE, start DATE, prenom VARCHAR(100), the_geom BLOB);
INSERT INTO point VALUES(0, 'BOCHER', 'bocher', 215.45, 222,'2007-06-15', 'ERWAN', GeomFromText('POINT(0 1)', '-1'));
INSERT INTO point VALUES(1, 'BOCHER', 'bocher', 216.45, 222,'2007-06-15', 'ERWAN', GeomFromText('POINT(0 1)', '-1'));
INSERT INTO point VALUES(2, 'BOCHER', 'bocher', 217.45, 222,'2007-06-15', 'ERWAN', GeomFromText('POINT(0 1)', '-1'));
INSERT INTO point VALUES(3, 'BOCHER', 'bocher', 218.45, 222,'2007-06-15', 'ERWAN', GeomFromText('POINT(0 1)', '-1'));
