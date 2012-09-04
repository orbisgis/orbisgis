SELECT SUM(@{fieldName}) as sum,
       MIN(@{fieldName}) as min,
       MAX(@{fieldName}) as max,
       AVG(@{fieldName}) as avg,
       StandardDeviation(@{fieldName}) as std
FROM  @{tableName};