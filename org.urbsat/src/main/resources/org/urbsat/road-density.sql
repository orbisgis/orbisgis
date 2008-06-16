/*Calculate the road density of a layer according to a grid*/
select register('grid');
create table grid as select creategrid(500, 500) from road;
select register('gridIntersectWithRoad');
create table "gridIntersectWithRoad" as select intersection(a.the_geom,b.the_geom) as the_geom, a.index from grid a, road b where isValid(b.the_geom) and intersects(a.the_geom,b.the_geom);
select register('exploded');
create table exploded as select explode() from "gridIntersectWithRoad";
select register('filterPointsAndPolygons');
create table "filterPointsAndPolygons" as select * from exploded where dimension(the_geom) = 1;
select register('unionGridRoad');
create table "unionGridRoad" as select geomunion(the_geom) as the_geom,index from "filterPointsAndPolygons" group by index;
select register('RoadDensityPerCell');
create table "RoadDensityPerCell" as select length(the_geom) as length,index from "unionGridRoad";
select register('density');
create table density as select a.the_geom, a.index, (b.length/area(a.the_geom))*100 as density from grid as a,"RoadDensityPerCell" as b where a.index=b.index;