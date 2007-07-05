call register('/tmp/grid.shp', 'grid');
call register('/tmp/madata.shp', 'madata');
create table grid as call CREATEGRID from landcover2000 values (100, 100);
create table madata as select Intersection(a.the_geom, b.the_geom) from grid a, landcover2000 b where Intersects(a.the_geom, b.the_geom);