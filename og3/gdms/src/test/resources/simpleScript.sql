select register('/home/ebocher/Documents/devs/og3/platform/gdms/src/test/resources/landcover2000.shp','landcover');
select register('/tmp/test.shp','landcoverBuffer');
create table landcoverBuffer as select ST_Buffer(the_geom, 20) as the_geom from landcover where ST_area(the_geom) > 9000;
