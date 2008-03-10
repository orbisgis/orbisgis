/* Calculate the grass density of a layer according to a grid*/
select register('grid');
create table grid as select creategrid(500, 500) from vegetation;
select register('gridIntersectWithVegetation');
create table gridIntersectWithVegetation as select intersection(a.the_geom,b.the_geom), a.index from grid a, vegetation b where isValid(b.the_geom) and intersects(a.the_geom,b.the_geom);
select register('unionGridVegetation');
create table unionGridVegetation as select geomunion(the_geom),index from gridIntersectWithVegetation group by index;
select register('vegetationDensityPerCell');
create table vegetationDensityPerCell as select area(the_geom) as Area,index from unionGridVegetation;
select register('density');
create table density as select a.the_geom, a.index, b.Area/area(a.the_geom) from grid as a,vegetationDensityPerCell as b where a.index=b.index;