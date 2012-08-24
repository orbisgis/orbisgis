SELECT COUNT() as count,
       SUM(@{fieldName}) as sum,
       MIN(@{fieldName}) as min,
       MAX(@{fieldName}) as max,
       AVG(@{fieldName}) as avg,
       StandardDeviation() as std
FROM  @{tableName};