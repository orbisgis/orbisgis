/*Calculate the water density of a layer according to a grid*/
select register('grid');
create table grid as select creategrid(500, 500) from water;
select register('gridIntersectWithWater');
create table "gridIntersectWithWater" as select intersection(a.the_geom,b.the_geom) as the_geom, a.index from grid a, Water b where isValid(b.the_geom) and intersects(a.the_geom,b.the_geom);
select register('exploded');
create table exploded as select explode() from "gridIntersectWithWater";
select register('filterPointsAndLines');
create table "filterPointsAndLines" as select * from exploded where dimension(the_geom) = 2;
select register('unionGridbuilding');
create table "unionGridbuilding" as select geomunion(the_geom) as the_geom,index from "filterPointsAndLines" group by index;
select register('buildingDensityPerCell');
create table "buildingDensityPerCell" as select area(the_geom) as Area,index from "unionGridbuilding";
select register('density');
create table density as select a.the_geom, a.index, (b.Area/area(a.the_geom))*100 as density from grid as a,"buildingDensityPerCell" as b where a.index=b.index;