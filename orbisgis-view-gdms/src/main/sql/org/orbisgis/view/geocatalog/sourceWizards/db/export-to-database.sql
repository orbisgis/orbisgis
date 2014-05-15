-- Used by GUI to export a DS into a DB

EXECUTE Export(
        ( SELECT  * 
          FROM @{tableName} ),
        @{vendor}, @{host}, @{port}, @{dbName}, @{userName}, @{password}, @{outputSchema}, @{outputTableName},@{ssl}
        );
