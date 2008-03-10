/* Calculate the road density of a layer according to a grid*/
select register('grid');
create table grid as select creategrid(500, 500) from road;
select register('gridIntersectWithRoad');
create table gridIntersectWithRoad as select intersection(a.the_geom,b.the_geom), a.index from grid a, road b where isValid(b.the_geom) and	intersects(a.the_geom,b.the_geom);
select register('unionGridRoad');
create table unionGridRoad as select geomunion(the_geom),index from gridIntersectWithRoad group by index;
select register('roadDensityPerCell');
create table roadDensityPerCell as select area(the_geom) as Area,index from unionGridRoad;
select register('density');
create table density as select a.the_geom, a.index, b.Area/area(a.the_geom) from grid as a,roadDensityPerCell as b where a.index=b.index;