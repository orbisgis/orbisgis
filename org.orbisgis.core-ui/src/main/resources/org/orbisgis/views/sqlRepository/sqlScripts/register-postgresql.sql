/*Register a PostgreSQL/Postgis table. Usage: select register ('dbType', 'host', port, 'dbName', 'user', 'password', 'tableName', 'dsName');*/
select register('postgresql','host', 'port', 'dbName','user','password','tableName', 'dsName');
