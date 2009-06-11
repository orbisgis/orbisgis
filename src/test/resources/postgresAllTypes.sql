DROP TABLE "alltypes";
delete from geometry_columns where f_table_name='alltypes';
CREATE TABLE "alltypes" (
"f1" bigint,
"f2" bigserial,
"f5" boolean,
"f7" bytea,
"f8" varchar,
"f9" char(8),
"f12" date,
"f13" double precision,
"f15" integer primary key,
"f21" numeric,
"f25" real,
"f26" smallint,
"f27" serial,
"f28" text,
"f29" time,
"f30" timestamp);