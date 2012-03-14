grammar GdmSQL;

options {
	language=Java;
	memoize=true;
	output=AST;
        backtrack=true;
}

tokens {
    T_RESERVED = 'reserved';
    T_TABLE_NAME = 'table_name';

    T_ROOT = 't_root';    
    T_COLUMN_LIST = 't_column_list';
    T_COLUMN_ITEM = 't_column_item';
    T_SELECT_COLUMN = 't_select_column';
    T_SELECT_COLUMN_STAR = 't_select_column_star';
    T_SELECT_PARAMS = 't_select_params';
    T_SELECT_LIMIT = 't_select_limit';
    T_SELECT_OFFSET = 't_select_offset';
    T_FUNCTION_CALL = 't_function_call';
    T_EXPR_LIST = 't_expr_list';
    T_NULL_CHECK = 't_null_check';
    T_UPDATE_SET = 't_update_set';
    T_UPDATE_COLUMNS = 't_update_columns';
    T_UPDATE_EXPRS = 't_update_exprs';
    T_TABLE_ITEM = 't_table_item';
    T_TABLE_QUERY = 't_table_query';
    T_TABLE_FUNCTION = 't_table_function';
    T_INNER_JOIN = 't_inner_join';
    T_OUTER_JOIN = 't_outer_join';
    T_CREATE_TABLE = 't_create_table';
    T_CREATE_VIEW = 't_create_view';
    T_EXECUTOR = 't_executor';

    T_ADD = 'ADD';
    T_ALL = 'ALL';
    T_ALTER = 'ALTER';
    T_AND = 'AND';
    T_AS = 'AS';
    T_ASC = 'ASC';
    T_BETWEEN = 'BETWEEN';
    T_CASE = 'CASE';
    T_CHAR = 'CHAR';
    T_CHECK = 'CHECK';
    T_COLUMN = 'COLUMN';
    T_CREATE = 'CREATE';
    T_DEFAULT = 'DEFAULT';
    T_DELETE = 'DELETE';
    T_DESC = 'DESC';
    T_DISTINCT = 'DISTINCT';
    T_DROP = 'DROP';
    T_ELSE = 'ELSE';
    T_EXCEPT = 'EXCEPT';
    T_EXISTS = 'EXISTS';
    T_FALSE = 'FALSE';
    T_FOR = 'FOR';
    T_FROM = 'FROM';
    T_GRANT = 'GRANT';
    T_GROUP = 'GROUP';
    T_HAVING = 'HAVING';
    T_IF = 'IF';
    T_IN = 'IN';
    T_INCREMENT = 'INCREMENT';
    T_INDEX = 'INDEX';
    T_INSERT = 'INSERT';
    T_INTERSECT = 'INTERSECT';
    T_INTO = 'INTO';
    T_IS = 'IS';
    T_ISNULL = 'ISNULL';
    T_LIKE = 'LIKE';
    T_LIKE2 = 'LIKE2';
    T_LIKE4 = 'LIKE4';
    T_LIKEC = 'LIKEC';
    T_LOCK = 'LOCK';
    T_MINUS = 'MINUS';
    T_MODE = 'MODE';
    T_MODIFY = 'MODIFY';
    T_NOT = 'NOT';
    T_NOTFOUND = 'NOTFOUND';
    T_NOTNULL = 'NOTNULL';
    T_NOWAIT = 'NOWAIT';
    T_NULL = 'NULL';
    T_NUMBER = 'NUMBER';
    T_OF = 'OF';
    T_ON = 'ON';
    T_OPTION = 'OPTION';
    T_OR = 'OR';
    T_ORDER = 'ORDER';
    T_PRIVILEGES = 'PRIVILEGES';
    T_PUBLIC = 'PUBLIC';
    T_RAW = 'RAW';
    T_RENAME = 'RENAME';
    T_REPLACE = 'REPLACE';
    T_ROW = 'ROW';
    T_ROWID = 'ROWID';
    T_ROWLABEL = 'ROWLABEL';
    T_ROWNUM = 'ROWNUM';
    T_ROWS = 'ROWS';
    T_SELECT = 'SELECT';
    T_SESSION = 'SESSION';
    T_SET = 'SET';
    T_SHARE = 'SHARE';
    T_SIZE = 'SIZE';
    T_START = 'START';
    T_SUCCESSFUL = 'SUCCESSFUL';
    T_SYSDATE = 'SYSDATE';
    T_TABLE = 'TABLE';
    T_THEN = 'THEN';
    T_TO = 'TO';
    T_TRIGGER = 'TRIGGER';
    T_TRUE = 'TRUE';
    T_TYPE = 'TYPE';
    T_UID = 'UID';
    T_UNION = 'UNION';
    T_UNIQUE = 'UNIQUE';
    T_UNKNOWN = 'UNKNOWN';
    T_UPDATE = 'UPDATE';
    T_USER = 'USER';
    T_VALIDATE = 'VALIDATE';
    T_VALUES = 'VALUES';
    T_VARCHAR = 'VARCHAR';
    T_VARCHAR2 = 'VARCHAR2';
    T_VIEW = 'VIEW';
    T_WHERE = 'WHERE';
    T_WITH = 'WITH';

    T_AT = 'AT';
    T_ADMIN = 'ADMIN';
    T_AFTER = 'AFTER';
    T_ANALYZE = 'ANALYZE';
    T_AUTHORIZATION = 'AUTHORIZATION';
    T_BEFORE = 'BEFORE';
    T_BEGIN = 'BEGIN';
    T_BLOCK = 'BLOCK';
    T_CACHE = 'CACHE';
    T_CALL = 'CALL';
    T_CANCEL = 'CANCEL';
    T_CASCADE = 'CASCADE';
    T_CHANGE = 'CHANGE';
    T_CHARACTER = 'CHARACTER';
    T_CLOSE = 'CLOSE';
    T_COMMIT = 'COMMIT';
    T_CONSTRAINT = 'CONSTRAINT';
    T_CONSTRAINTS = 'CONSTRAINTS';
    T_CONTENTS = 'CONTENTS';
    T_CONTINUE = 'CONTINUE';
    T_CURSOR = 'CURSOR';
    T_DATA = 'DATA';
    T_DATABASE = 'DATABASE';
    T_DATAFILE = 'DATAFILE';
    T_DAY = 'DAY';
    T_DBA = 'DBA';
    T_DBTIMEZONE = 'DBTIMEZONE';
    T_DEC = 'DEC';
    T_DECLARE = 'DECLARE';
    T_DISABLE = 'DISABLE';
    T_EACH = 'EACH';
    T_ENABLE = 'ENABLE';
    T_END = 'END';
    T_ESCAPE = 'ESCAPE';
    T_EVENTS = 'EVENTS';
    T_EXCEPT = 'EXCEPT';
    T_EXCEPTIONS = 'EXCEPTIONS';
    T_EXEC = 'EXEC';
    T_EXECUTE = 'EXECUTE';
    T_EXPLAIN = 'EXPLAIN';
    T_EXTENT = 'EXTENT';
    T_FETCH = 'FETCH';
    T_FLUSH = 'FLUSH';
    T_FORCE = 'FORCE';
    T_FOREIGN = 'FOREIGN';
    T_FOUND = 'FOUND';
    T_FUNCTION = 'FUNCTION';
    T_GO = 'GO';
    T_GOTO = 'GOTO';
    T_GROUPS = 'GROUPS';
    T_INCLUDING = 'INCLUDING';
    T_INITRANS = 'INITRANS';
    T_INSTANCE = 'INSTANCE';
    T_KEY = 'KEY';
    T_LOCAL = 'LOCAL';
    T_LOCKED = 'LOCKED';
    T_MANAGE = 'MANAGE';
    T_MANUAL = 'MANUAL';
    T_MAXVALUE = 'MAXVALUE';
    T_MINVALUE = 'MINVALUE';
    T_MODULE = 'MODULE';
    T_MONTH = 'MONTH';
    T_NEW = 'NEW';
    T_NEXT = 'NEXT';
    T_NO = 'NO';
    T_NOMAXVALUE = 'NOMAXVALUE';
    T_NOMINVALUE = 'NOMINVALUE';
    T_NONE = 'NONE';
    T_NOORDER = 'NOORDER';
    T_NORMAL = 'NORMAL';
    T_NOSORT = 'NOSORT';
    T_OFF = 'OFF';
    T_OFFSET = 'OFFSET';
    T_OLD = 'OLD';
    T_ONLY = 'ONLY';
    T_OPEN = 'OPEN';
    T_OWN = 'OWN';
    T_PACKAGE = 'PACKAGE';
    T_PARALLEL = 'PARALLEL';
    T_PLAN = 'PLAN';
    T_PRECISION = 'PRECISION';
    T_PRIMARY = 'PRIMARY';
    T_PRIVATE = 'PRIVATE';
    T_PROCEDURE = 'PROCEDURE';
    T_PROFILE = 'PROFILE';
    T_PURGE = 'PURGE';
    T_READ = 'READ';
    T_RECOVER = 'RECOVER';
    T_REFERENCES = 'REFERENCES';
    T_REFERENCING = 'REFERENCING';
    T_RESTRICTED = 'RESTRICTED';
    T_REUSE = 'REUSE';
    T_ROLE = 'ROLE';
    T_ROLES = 'ROLES';
    T_ROLLBACK = 'ROLLBACK';
    T_SCHEMA = 'SCHEMA';
    T_SECOND = 'SECOND';
    T_SECTION = 'SECTION';
    T_SEGMENT = 'SEGMENT';
    T_SEQUENCE = 'SEQUENCE';
    T_SESSIONTIMEZONE = 'SESSIONTIMEZONE';
    T_SHARED = 'SHARED';
    T_SNAPSHOT = 'SNAPSHOT';
    T_SKIP = 'SKIP';
    T_SOME = 'SOME';
    T_SORT = 'SORT';
    T_SQL = 'SQL';
    T_STATEMENT = 'STATEMENT';
    T_STATISTICS = 'STATISTICS';
    T_STOP = 'STOP';
    T_STORAGE = 'STORAGE';
    T_SWITCH = 'SWITCH';
    T_SYSTEM = 'SYSTEM';
    T_TABLES = 'TABLES';
    T_TABLESPACE = 'TABLESPACE';
    T_TEMPORARY = 'TEMPORARY';
    T_THREAD = 'THREAD';
    T_TRANSACTION = 'TRANSACTION';
    T_TRIGGERS = 'TRIGGERS';
    T_TRUNCATE = 'TRUNCATE';
    T_UNDER = 'UNDER';
    T_UNLIMITED = 'UNLIMITED';
    T_UNTIL = 'UNTIL';
    T_USE = 'USE';
    T_USING = 'USING';
    T_WAIT = 'WAIT';
    T_WHEN = 'WHEN';
    T_WORK = 'WORK';
    T_WRITE = 'WRITE';
    T_YEAR = 'YEAR';
    T_ZONE = 'ZONE';

    T_AUTOMATIC = 'AUTOMATIC';
    T_BFILE = 'BFILE';
    T_BLOB = 'BLOB';
    T_BY = 'BY';
    T_CAST = 'CAST';
    T_CLOB = 'CLOB';
    T_COLUMN_VALUE = 'COLUMN_VALUE';
    T_CROSS = 'CROSS';
    T_CUBE = 'CUBE';
    T_DECREMENT = 'DECREMENT';
    T_DENSE_RANK = 'DENSE_RANK';
    T_DIMENSION = 'DIMENSION';
    T_EMPTY = 'EMPTY';
    T_EQUALS_PATH = 'EQUALS_PATH';
    T_FIRST_VALUE = 'FIRST_VALUE';
    T_FULL = 'FULL';
    T_GROUPING = 'GROUPING';
    T_IGNORE = 'IGNORE';
    T_INFINITE = 'INFINITE';
    T_INNER = 'INNER';
    T_INTERVAL = 'INTERVAL';
    T_ITERATE = 'ITERATE';
    T_JOIN = 'JOIN';
    T_KEEP = 'KEEP';
    T_LAST = 'LAST';
    T_LAST_VALUE = 'LAST_VALUE';
    T_LEAD = 'LEAD';
    T_LEFT = 'LEFT';
    T_MAIN = 'MAIN';
    T_MEASURES = 'MEASURES';
    T_MEMBER = 'MEMBER';
    T_MODEL = 'MODEL';
    T_NAN = 'NAN';
    T_NATIONAL = 'NATIONAL';
    T_NATURAL = 'NATURAL';
    T_NAV = 'NAV';
    T_NCHAR = 'NCHAR';
    T_NCLOB = 'NCLOB';
    T_NULLS = 'NULLS';
    T_NVARCHAR = 'NVARCHAR';
    T_NVARCHAR2 = 'NVARCHAR2';
    T_OBJECT_ID = 'OBJECT_ID';
    T_OBJECT_VALUE = 'OBJECT_VALUE';
    T_OUTER = 'OUTER';
    T_OVER = 'OVER';
    T_PARTITION = 'PARTITION';
    T_PIVOT = 'PIVOT';
    T_POSITIVE = 'POSITIVE';
    T_PRESENT = 'PRESENT';
    T_RANK = 'RANK';
    T_REFERENCE = 'REFERENCE';
    T_REGEXP_LIKE = 'REGEXP_LIKE';
    T_RIGHT = 'RIGHT';
    T_ROLLUP = 'ROLLUP';
    T_ROW_NUMBER = 'ROW_NUMBER';
    T_RULES = 'RULES';
    T_SAMPLE = 'SAMPLE';
    T_SEARCH = 'SEARCH';
    T_SEQUENTIAL = 'SEQUENTIAL';
    T_SETS = 'SETS';
    T_SINGLE = 'SINGLE';
    T_THE = 'THE';
    T_UNBOUNDED = 'UNBOUNDED';
    T_UNDER_PATH = 'UNDER_PATH';
    T_UPDATED = 'UPDATED';
    T_UPSERT = 'UPSERT';
    T_UROWID = 'UROWID';
    T_VARIANCE = 'VARIANCE';
    T_VARYING = 'VARYING';
    T_VAR_POP = 'VAR_POP';
    T_VAR_SAMP = 'VAR_SAMP';
    T_XML = 'XML';
    T_XMLDATA = 'XMLDATA';

    T_ERRORS = 'ERRORS';
    T_FIRST = 'FIRST';
    T_LIMIT = 'LIMIT';
    T_LOG = 'LOG';
    T_REJECT = 'REJECT';
    T_RETURN = 'RETURN';
    T_RETURNING = 'RETURNING';

    T_MERGE = 'MERGE';
    T_MATCHED = 'MATCHED';

    T_FOLLOWING = 'FOLLOWING';
    T_RANGE = 'RANGE';
    T_UNPIVOT = 'UNPIVOT';

    T_VALUE = 'VALUE';

    T_EXCLUDE = 'EXCLUDE';
    T_INCLUDE = 'INCLUDE';
    T_MIVALUE = 'MIVALUE';
    T_PRECEDING = 'PRECEDING';
    T_RESPECT = 'RESPECT';
}

@lexer::header {
package org.gdms.sql.parser;
}
@parser::header {
package org.gdms.sql.parser;
}

@lexer::members {
@Override
public void reportError(RecognitionException e) {
        throw new IllegalArgumentException(e);
}
}

@members {
public Object recoverFromMismatchedSet(IntStream input, RecognitionException e, BitSet follow)
        throws RecognitionException {
        throw e;
}

protected Object recoverFromMismatchedToken(IntStream input, int ttype, BitSet follow) throws RecognitionException {
        throw new MismatchedTokenException(ttype, input);
}

}

@rulecatch {
catch (RecognitionException ex) {
        throw ex;
}
}

// main rule

start_rule
	: statement+
        -> ^(T_ROOT statement+ )
        ;

statement
        : ( select_statement
          | update_statement
          | insert_statement
          | delete_statement
          | create_table_statement
          | create_view_statement
          | alter_table_statement
          | drop_table_statement
          | drop_view_statement
          | create_index_statement
          | drop_index_statement
          | call_statement
          )
          SEMI!
        ;

// SELECT

select_statement
        : T_SELECT ( T_DISTINCT | T_UNIQUE | T_ALL )? select_list table_reference_list
        where_block? group_by_clause? having_clause? order_by_clause? limit_offset_block? union_clause?
        -> ^(T_SELECT T_UNIQUE? T_DISTINCT? select_list table_reference_list where_block? group_by_clause? having_clause?
           order_by_clause? limit_offset_block? union_clause?)
        ;

select_list
        : select_list_first select_list_next*
        -> ^(T_COLUMN_LIST select_list_first select_list_next* )
        ;

select_list_first
        : select_column_star -> ^(T_COLUMN_ITEM select_column_star)
        | expression_main ( T_AS? alias=LONG_ID )?
        -> ^(T_COLUMN_ITEM expression_main $alias? )
        | a=ASTERISK select_star_except? -> ^(T_COLUMN_ITEM ^(T_SELECT_COLUMN_STAR $a select_star_except? ))
        ;

select_list_next
        : COMMA ( expression_main ( T_AS? alias=LONG_ID )?
        -> ^(T_COLUMN_ITEM expression_main $alias? )
        | a=ASTERISK select_star_except? -> ^(T_COLUMN_ITEM ^(T_SELECT_COLUMN_STAR $a select_star_except? ))
        )
        ;

select_column
        : LONG_ID (DOT LONG_ID)*
        -> ^(T_SELECT_COLUMN LONG_ID+ )
        ;

select_column_star
        : LONG_ID (DOT LONG_ID)* DOT ASTERISK select_star_except?
        -> ^(T_SELECT_COLUMN_STAR LONG_ID+ ASTERISK select_star_except? )
        ;

select_star_except
        : T_EXCEPT LONG_ID (COMMA LONG_ID)*
        -> ^(T_EXCEPT LONG_ID+ )
        ;

where_block
        : T_WHERE^ expression_cond
        ;

table_reference_list
        : T_FROM table_reference_item (COMMA table_reference_item)*
         -> ^(T_FROM table_reference_item+)
        ;

table_reference_item
        : join_clause
        | LPAREN! join_clause RPAREN!
        | table_reference
        ;

table_reference
        : table=table_id ( T_AS? alias=LONG_ID )?
         -> ^( T_TABLE_ITEM $table $alias? )
        | LPAREN select_statement RPAREN T_AS? alias=LONG_ID
         -> ^( T_TABLE_QUERY select_statement $alias )
        | custom_query_call ( T_AS? alias=LONG_ID )?
        -> ^( T_TABLE_FUNCTION custom_query_call $alias? )
        ;

subquery
        : select_statement
         -> ^( T_TABLE_QUERY select_statement )
        ;

join_clause
	:	table_reference join_clause_content+
         -> ^( T_JOIN table_reference join_clause_content+)
	;

join_clause_content
        : inner_cross_join_clause | outer_join_clause
        ;

inner_cross_join_clause
	:	T_INNER? T_JOIN table_reference join_condition
         -> ^( T_INNER_JOIN table_reference join_condition )
    	|	(a=T_CROSS | a=T_NATURAL T_INNER?) T_JOIN table_reference
         -> ^( T_INNER_JOIN table_reference $a )
	;

join_condition
        : T_ON expression_cond -> ^( T_ON expression_cond)
        | T_USING LPAREN select_column_list RPAREN -> ^( T_USING select_column_list )
        ;

outer_join_clause
	: outer_join_type T_JOIN table_reference join_condition
          -> ^( T_OUTER_JOIN table_reference outer_join_type join_condition )
        | T_NATURAL outer_join_type T_JOIN table_reference
          -> ^( T_OUTER_JOIN table_reference outer_join_type T_NATURAL )
	;

outer_join_type
 	:	( T_FULL | T_LEFT | T_RIGHT ) ( T_OUTER! )?
	;

select_column_list
        : select_column (COMMA select_column)*
        ;

limit_offset_block
        : limit_block? offset_block -> ^(T_SELECT_PARAMS limit_block? offset_block)
        | limit_block -> ^(T_SELECT_PARAMS limit_block)
        ;

limit_block
        : T_LIMIT (a=NUMBER | a=T_ALL) -> ^(T_SELECT_LIMIT $a)
        | T_FETCH (T_FIRST | T_NEXT)? a=NUMBER? (T_ROW | T_ROWS)? T_ONLY -> ^(T_SELECT_LIMIT $a?)
        ;

offset_block
        : T_OFFSET a=NUMBER (T_ROW | T_ROWS)?
        -> ^(T_SELECT_OFFSET $a)
        ;

order_by_clause
        : T_ORDER T_BY order_by_item ( COMMA order_by_item )*
        -> ^(T_ORDER order_by_item+)
        ;

order_by_item
        : a=expression_main ( b=T_ASC | b=T_DESC )? ( T_NULLS ( c=T_FIRST | c=T_LAST) )?
        -> ^(T_COLUMN_ITEM $a $b? $c? )
        ;

group_by_clause
        : T_GROUP T_BY expression_main ( COMMA expression_main )*
        -> ^(T_GROUP expression_main+ )
        ;

having_clause
        : T_HAVING^ expression_cond
        ;

union_clause
        : T_UNION select_statement
        -> ^(T_UNION select_statement)
        ;

// UPDATE

update_statement
        : T_UPDATE ta=table_id T_SET
          update_set (COMMA update_set)*
          where_block?
        -> ^(T_UPDATE $ta update_set+ where_block?)
        ;

update_set
        : update_single_set -> update_single_set
        | LPAREN update_field_list RPAREN EQ LPAREN update_assign_list RPAREN
        -> ^(T_UPDATE_SET update_field_list update_assign_list )
        ;

update_single_set
        : update_field EQ update_expression
        -> ^(T_UPDATE_SET ^(T_UPDATE_COLUMNS update_field) ^(T_UPDATE_EXPRS update_expression) )
        ;

update_expression
        : expression_main
        | T_DEFAULT
        ;

update_field_list
        : update_field (COMMA update_field)*
        -> ^(T_UPDATE_COLUMNS update_field+ )
        ;

update_field
        : id=LONG_ID
        -> ^(T_SELECT_COLUMN $id )
        ;

update_assign_list
        : update_expression (COMMA update_expression)*
        -> ^(T_UPDATE_EXPRS update_expression+)
        ;

// INSERT

insert_statement
        : T_INSERT T_INTO ta=table_id insert_statement_field_list?
        ( T_VALUES multiple_insert_value_list
        -> ^(T_INSERT $ta multiple_insert_value_list insert_statement_field_list?)
        | select_statement
        -> ^(T_INSERT $ta select_statement insert_statement_field_list?)
        )
        ;

insert_statement_field_list
        : LPAREN LONG_ID (COMMA LONG_ID)* RPAREN
          -> ^(T_COLUMN_LIST LONG_ID+)
        ;

multiple_insert_value_list
        : LPAREN insert_value_list RPAREN (COMMA LPAREN insert_value_list RPAREN)*
          -> ^(T_VALUES insert_value_list+)
        ;

insert_value_list
        : insert_value_list_item (COMMA insert_value_list_item)*
        -> ^(T_VALUES insert_value_list_item+)
        ;

insert_value_list_item
        : expression_main | T_DEFAULT
        ;

// DELETE FROM

delete_statement
        : T_DELETE T_FROM table_id where_block?
          -> ^(T_DELETE table_id where_block?)
        ;

// Special custom query expressions

custom_query_call
        : name=LONG_ID LPAREN custom_query_expression_list? RPAREN
        -> ^( T_FUNCTION_CALL $name custom_query_expression_list? )
        ;

custom_query_expression_list
        : custom_query_expression_main ( COMMA custom_query_expression_main )*
        -> ^( T_EXPR_LIST custom_query_expression_main+ )
        ;

custom_query_expression_main
        : table_reference
        | subquery
        | NUMBER
        | QUOTED_STRING
        | bool_const
        ;

// CREATE TABLE

create_table_statement
        : T_CREATE T_TABLE table=table_id (
          T_AS select_statement  -> ^(T_CREATE_TABLE $table select_statement)
        | LPAREN create_table_params RPAREN -> ^(T_CREATE_TABLE $table create_table_params)
        )
        ;

create_table_params
        : column_def (COMMA column_def)*
        -> ^(T_CREATE_TABLE column_def*)
        ;

column_def
        : name=LONG_ID type=LONG_ID
        -> ^(T_TABLE_ITEM $name $type)
        ;

// ALTER TABLE

alter_table_statement
        : T_ALTER T_TABLE table=table_id 
        ( alter_action (COMMA alter_action)* -> ^(T_ALTER $table alter_action*)
        | T_RENAME T_TO newname=table_id -> ^(T_RENAME $table $newname)
        )
        ;

alter_action
        :
        ( T_ADD T_COLUMN? column_def -> ^(T_ADD column_def)
        | T_ALTER T_COLUMN? name=LONG_ID (T_SET T_DATA)? T_TYPE type=LONG_ID (T_USING expression_main)?
          -> ^(T_ALTER $name $type expression_main?)
        | T_DROP T_COLUMN? (T_IF T_EXISTS)? name=LONG_ID -> ^(T_DROP $name T_IF?)
        | T_RENAME T_COLUMN? name=LONG_ID T_TO newname=LONG_ID
          -> ^(T_RENAME $name $newname)
        )
        ;

// DROP TABLE

drop_table_statement
        : T_DROP T_TABLE (T_IF T_EXISTS)? table_id (COMMA table_id)* T_PURGE?
          -> ^(T_DROP ^(T_TABLE table_id+) T_IF? T_PURGE?)
        ;

// CREATE VIEW

create_view_statement
        : T_CREATE (T_OR T_REPLACE)? T_VIEW table=table_id 
          T_AS select_statement  -> ^(T_CREATE_VIEW $table select_statement T_OR?)
        ;

// DROP VIEW

drop_view_statement
        : T_DROP T_VIEW (T_IF T_EXISTS)? table_id (COMMA table_id)*
          -> ^(T_DROP ^(T_VIEW table_id+) T_IF?)
        ;

// CREATE INDEX

create_index_statement
        : T_CREATE T_INDEX T_ON table_id LPAREN a=LONG_ID RPAREN
        -> ^(T_INDEX T_CREATE table_id $a )
        ;

// DROP INDEX

drop_index_statement
        : T_DROP T_INDEX T_ON table_id LPAREN a=LONG_ID RPAREN
        -> ^(T_INDEX T_DROP table_id $a )
        ;

// CALL statement

call_statement
        : (T_CALL | T_EXECUTE) function_call
        -> ^( T_EXECUTOR function_call)
        ;

// All expressions

expression_main
        : expression_cond
        | expression_concat
        ;

// Scalar expressions

expression_concat
        : (expression_add -> expression_add)
        (concat_operator e=expression_add -> ^(concat_operator $expression_concat $e) )*
        ;

expression_add
        : (expression_mul -> expression_mul)
        (arithm_operator e=expression_mul -> ^(arithm_operator $expression_add $e) )*
        ;

expression_mul
        : (expression_pow -> expression_pow)
        ( ASTERISK e2=expression_pow -> ^(ASTERISK $expression_mul $e2)
        | DIVIDE e2=expression_pow -> ^(DIVIDE $expression_mul $e2)
        | MODULO e2=expression_pow -> ^(MODULO $expression_mul $e2)
        )*
        ;

expression_pow
        : (expression_in -> expression_in)
        ( EXPONENT e2=expression_in -> ^(EXPONENT $expression_pow $e2)
        )*
        ;

expression_is_null
        : a=expression_concat  (
          T_IS ( T_NULL -> ^(T_NULL_CHECK $a)
               | T_NOT T_NULL -> ^(T_NOT ^(T_NULL_CHECK $a))
               )
        | T_ISNULL -> ^(T_NULL_CHECK $a)
        | T_NOTNULL -> ^(T_NOT ^(T_NULL_CHECK $a))
        )
        ;

expression_final
        : 
        ( function_call -> function_call
        | select_column -> select_column
        | LPAREN expression_concat RPAREN -> expression_concat
        | NUMBER -> NUMBER
        | QUOTED_STRING -> QUOTED_STRING
        | bool_const -> bool_const
        | expression_cast -> expression_cast
        ) (CASTCOLON type=LONG_ID -> ^(T_CAST $expression_final $type) )?
        ;

expression_in
        : (o=SQRT | o=CBRT | o=FACTORIAL_PREFIX | o=AT_SIGN | o=TILDE | o=MINUS) expression_in
          -> ^($o expression_in)
        | expression_final
        ;

function_call
        : name=LONG_ID LPAREN (expression_list? | ASTERISK ) RPAREN
        -> ^( T_FUNCTION_CALL $name expression_list? )
        ;

expression_cast
        : T_CAST LPAREN expression_concat T_AS type=LONG_ID RPAREN
        -> ^(T_CAST expression_concat $type)
        ;

expression_list
        : expression_main ( COMMA expression_main )*
        -> ^( T_EXPR_LIST expression_main+ )
        ;

// Boolean expressions

expression_cond
        : (expression_cond_or -> expression_cond_or)
        ( T_IN LPAREN expression_expr_in_content RPAREN -> ^(T_IN $expression_cond expression_expr_in_content)
        | T_NOT T_IN LPAREN expression_expr_in_content RPAREN
          -> ^(T_NOT ^(T_IN $expression_cond expression_expr_in_content))
        | T_LIKE s=expression_final -> ^(T_LIKE $expression_cond $s)
        | T_NOT T_LIKE s=expression_final -> ^(T_NOT ^(T_LIKE $expression_cond $s))
        )?
        // nice hack to support more grammar without parenthesis
        (T_OR e2=expression_cond_and -> ^(T_OR $expression_cond $e2) )*
        ;

expression_expr_in_content
        : select_statement
        | expression_list
        ;

expression_cond_or
        : (expression_cond_and -> expression_cond_and)
          (T_OR e2=expression_cond_and -> ^(T_OR $expression_cond_or $e2) )*
        ;

expression_cond_and
        : (expression_cond_not -> expression_cond_not)
          (T_AND e2=expression_cond_not -> ^(T_AND $expression_cond_and $e2) )*
        ;

expression_cond_not
        : T_NOT^ expression_cond_is
        | expression_cond_is
        ;

expression_cond_is
        : (expression_cond_final -> expression_cond_final)
          (T_IS 
          ( T_TRUE -> $expression_cond_is
          | T_NOT T_FALSE -> $expression_cond_is
          | T_NOT T_TRUE -> ^(T_NOT $expression_cond_is)
          | T_FALSE -> ^(T_NOT $expression_cond_is)
          | (T_UNKNOWN | T_NULL) -> ^(T_NULL_CHECK $expression_cond_is)
          | T_NOT (T_UNKNOWN | T_NULL) -> ^(T_NOT ^(T_NULL_CHECK $expression_cond_is))
          ))?
        ;

expression_exists
        : T_EXISTS LPAREN subquery RPAREN
        -> ^(T_EXISTS subquery)
        ;

expression_cond_final
        : expression_comp
        | expression_exists
        | bool_const
        | LPAREN! expression_cond RPAREN!
        ;

expression_comp
        : (expression_concat -> expression_concat)
          (comp_operator e2=expression_concat -> ^(comp_operator $expression_comp $e2) )?
        
        ;

// Operators

bool_operator
        : T_OR | T_AND
        ;

arithm_operator
        : PLUS | MINUS
        ;

comp_operator
        : LTH | LEQ | GTH | GEQ | EQ | NOT_EQ
        ;

bool_const
        : T_TRUE | T_FALSE | T_UNKNOWN | T_NULL
        ;

concat_operator
        : DOUBLEVERTBAR
        ;

// Identifier

table_id
        : LONG_ID (DOT LONG_ID)* -> ^(T_TABLE_NAME LONG_ID+)
        ;

LONG_ID
        : ID | DOUBLEQUOTED_STRING
        ;

// Lexer tokens

fragment
ID : ID_START ID_NEXT* ;

fragment
ID_START : 'a'..'z' | 'A'..'Z'
        ;

fragment
ID_NEXT : ID_START | '0'..'9' | '$' | '#' | '_'
        ;

SEMI
	:	';'
        ;

WS      : ( ' ' | '\t' | '\n' | '\r' )+ { skip(); }
        ;

NESTED_ML_COMMENT
        :  '/*'
           (options {greedy=false;} : NESTED_ML_COMMENT | . )*
           '*/' { skip(); }
        ;

SL_COMMENT
        : '--' ( ~('\r'|'\n')* ) '\r'? ('\n' | EOF) { skip(); }
        ;

DOT
	:	POINT
	;
fragment
POINT
	:	'.'
	;
COMMA
	:	','
	;
EXPONENT
	:	'**' | '^'
	;
ASTERISK
	:	'*'
	;
AT_SIGN
	:	'@'
	;
RPAREN
	:	')'
	;
LPAREN
	:	'('
	;
RBRACK
	:	']'
	;
LBRACK
	:	'['
	;
PLUS
	:	'+'
	;
MINUS
	:	'-'
	;
DIVIDE
	:	'/'
	;
EQ
	:	'='
	;

SQRT
        :       '|/'
        ;

CBRT
        :       '||/'
        ;

FACTORIAL
        :       '!'
        ;

FACTORIAL_PREFIX
        :       '!!'
        ;

CASTCOLON
        :       '::'
        ;

TILDE
        :       '~'
        ;

MODULO
	:	'%'
	;
LLABEL
	:	'<<'
	;
RLABEL
	:	'>>'
	;
ASSIGN
	:	':='
	;
ARROW
	:	'=>'
	;
VERTBAR
	:	'|'
	;
DOUBLEVERTBAR
	:	'||'
	;
NOT_EQ
	:	'<>' | '!=' | '^='
	;
LTH
	:	'<'
	;
LEQ
	:	'<='
	;
GTH
	:	'>'
	;
GEQ
	:	'>='
	;
 NUMBER
	:	(	( NUM POINT NUM ) => NUM POINT NUM
		|	POINT NUM
		|	NUM
		)
		( 'E' ( PLUS | MINUS )? NUM )?
    ;
fragment
NUM     : DIGIT+
	; 

fragment
DIGIT   : '0'..'9'
        ;

QUOTE
	:	'\''
	;
fragment
DOUBLEQUOTED_STRING
	:	'"' ( ~('"') )* '"'
	;

QUOTED_STRING
	:	( 'n'|'N' )? '\'' ( '\'\'' | ~('\'') )* '\''
	;