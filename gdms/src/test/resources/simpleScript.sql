select register('/home/ebocher/Documents/data/BD_Topo_Nantes_shp/BATI_surface.shp','landcover');
select register('/tmp/test.shp','landcoverBuffer');
create table landcoverBuffer as select ST_Buffer(the_geom, 20) as the_geom from landcover where ST_area(the_geom) > 9000;
