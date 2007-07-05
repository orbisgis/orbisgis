call register('/tmp/madata.shp', 'fer');
create table fer as select Intersection(a.the_geom, b.the_geom) from bzh5_communes a, landcover2000 b;