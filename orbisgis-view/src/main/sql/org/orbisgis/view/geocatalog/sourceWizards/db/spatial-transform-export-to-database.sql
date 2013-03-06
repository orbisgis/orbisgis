-- Used by GUI to export a spatial datasource into a DB
EXECUTE Export(
        ( SELECT ST_Transform(@{inputGeomField}, @{crs}) AS @{outputGeomField}, * EXCEPT (@{inputGeomField}, @{outputGeomField})
          FROM @{tableName} ),
        @{vendor}, @{host}, @{port}, @{dbName}, @{userName}, @{password}, @{outputSchema}, @{outputTableName},@{ssl}
        );