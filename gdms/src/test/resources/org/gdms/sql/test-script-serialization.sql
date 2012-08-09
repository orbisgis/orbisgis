CREATE TABLE toto AS SELECT 42 AS hi, 'hulu' AS ho;

INSERT INTO toto VALUES (@{othervalue}, 'demo');

CREATE TABLE tata AS SELECT sum(hi) AS total, sum(strlength(ho)) AS len FROM toto;

DROP TABLE toto;