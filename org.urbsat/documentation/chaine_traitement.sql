call register ('c://tmp//grid.shp','grid');
create table grid as call CREATEGRID from batiments values (200, 200);
call register ('c://tmp//buildvolume.csv','buildvolume');
create table buildvolume as call BUILDVOLUME from batiments,grid values ('the_geom', 'the_geom');
call register ('c://tmp//balancedbuildvolume.csv','balancedbuildvolume');
create table buildvolume as call BALANCEDBUILDVOLUME from batiments,grid values ('the_geom', 'the_geom');
call register ('c://tmp//averagebuildheigt.csv','averagebuildheight');
create table averagebuildheight as call AVERAGEBUILDHEIGHT from batiments,grid values ('the_geom', 'the_geom');
call register ('c://tmp//standarddeviationbuildheigt.csv','standarddeviationbuildheight');
create table standarddeviationbuildheight as call STANDARDDEVIATIONBUILDHEIGHT from batiments,grid,averagebuildheight values ('the_geom', 'the_geom');
call register ('c://tmp//standarddeviationbuildheigt.csv','standarddeviationbuildbalanced');
create table standarddeviationbuildbalanced as call STANDARDDEVIATIONBUILDHEIGHT from batiments,grid,averagebuildheight values ('the_geom', 'the_geom')
