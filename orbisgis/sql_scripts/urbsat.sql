call register ('/tmp/grid.shp','gridtable');
create table gridtable as call CreateGrid from landcover2000 values (1000,1000);
call register ('/tmp/grass.shp','grasstable');
create table grasstable as select * from landcover2000 where type='grassland';
call register ('/tmp//result.csv','result');
create table result as call  DENSITY from grasstable, gridtable values ('the_geom', 'the_geom');
call register ('/tmp/build.shp','buildtable');
create table buildtable as select * from landcover2000 where type='built up areas' ;
call register ('/tmp/result2.csv','result2');
create table result2 as call  DENSITY from buildtable, gridtable values ('the_geom', 'the_geom');
call register ('/tmp/result3.csv','result3');
create table result3 as select result.index, result.density as grass, result2.density as build from result,result2 where result.index = result2.index