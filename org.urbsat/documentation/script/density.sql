select register('c://tmp//grid.shp', 'grid');
create table grid as select creategrid(500,500) from bati;
select register('c://tmp//bati_inter.shp', 'bati_inter');
create table bati_inter as select intersection(a.the_geom,b.the_geom), a.index from grid as a, bati as b where intersects(a.the_geom, b.the_geom);
