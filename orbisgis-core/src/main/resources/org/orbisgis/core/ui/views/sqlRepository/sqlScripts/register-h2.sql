/*Register a H2 table. Usage: select register ('dbType',
'host', port, '[path+]dbName', 'user', 'password',
'tableName', 'dsName');*/
select register('h2','localhost', '0', '[path+]dbName','sa','','tableName', 'dsName');
