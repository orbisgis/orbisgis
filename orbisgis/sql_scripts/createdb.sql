call register('h2','',0,'/tmp/database/erwan','','','grid2','grid2');
create table grid2 as call CREATEGRID from landcover2000 values (1000, 1000);