-- Used by GUI to export a DS into a DB

EXECUTE Export(
        ( SELECT ST_Transform(@{inputGeomField}, @{crs}) AS @{outputGeomField}, * EXCEPT (@{outputGeomField})
          FROM @{tableName} ),
        @{vendor}, @{host}, @{port}, @{dbName}, @{userName}, @{password}, @{outputSchema}, @{outputTableName}
        );
