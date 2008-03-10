/* Calculate the water density of a layer according to a grid*/
select register('grid');
create table grid as select creategrid(500, 500) from water;
select register('gridIntersectWithWater');
create table gridIntersectWithWater as select (a.the_geom,b.the_geom), a.index from grid a, water b where isValid(b.the_geom) and intersects(a.the_geom,b.the_geom);
select register('unionGridWater');
create table unionGridWater as select geomunion(the_geom),index from gridIntersectWithWater group by index;
select register('waterDensityPerCell');
create table waterDensityPerCell as select area(the_geom) as Area,index from unionGridWater;
select register('density');
create table density as select a.the_geom, a.index, b.Area/area(a.the_geom) from grid as a,waterDensityPerCell as b where a.index=b.index;