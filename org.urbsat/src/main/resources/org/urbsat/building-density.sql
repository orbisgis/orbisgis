/*Calculate the building density of a layer according to a grid */
create table grid as select creategrid(500, 500) from batiments;
create table "gridIntersectWithBati" as select intersection(a.the_geom,b.the_geom) as the_geom, a.gid from grid a, batiments b where isValid(b.the_geom) and intersects(a.the_geom,b.the_geom);
create table exploded as select explode() from "gridIntersectWithBati";
create table "filterPointsAndLines" as select * from exploded where dimension(the_geom) = 2;
create table "unionGridBati" as select geomunion(the_geom) as the_geom,gid from "filterPointsAndLines" group by gid;
create table "batiDensityPerCell" as select area(the_geom) as Area,gid from "unionGridBati";
create table density as select a.the_geom, a.gid, (b.Area/area(a.the_geom))*100 as density from grid as a,"batiDensityPerCell" as b where a.gid=b.gid;