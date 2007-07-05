call register('h2','',0,'/tmp/database/erwan','','','grid4','grid4');
call register('h2','',0,'/tmp/database/erwan','','','intersection','intersection');
create table grid4 as call CREATEGRID from landcover2000 values (100, 100);
create table intersection as select Intersection(a.the_geom, b.the_geom) from grid a, landcover2000 b where Intersects(a.the_geom, b.the_geom);
select * from intersection;