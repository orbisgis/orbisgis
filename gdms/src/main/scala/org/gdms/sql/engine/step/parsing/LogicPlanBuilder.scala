/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.sql.engine.step.parsing

import org.antlr.runtime.tree.CommonTree
import org.antlr.runtime.tree.Tree
import org.gdms.sql.parser.GdmSQLParser._
import org.gdms.data.values.SQLValueFactory
import org.gdms.data.values.Value
import org.gdms.data.values.ValueFactory
import org.gdms.sql.engine.SemanticException
import org.gdms.sql.engine.operations._
import org.gdms.sql.evaluator._

/**
 * This object contains all the logic for building a Logical Query Plan from the Abstract Syntactic Tree
 * returned by the parser (currently ANTLR).
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
object LogicPlanBuilder {

  /**
   * Builds an abstract operation tree from an abstract syntactic tree.
   */
  def buildOperationTree(node: Tree): Operation = {
    // output
    var end: Operation = null


    node.getType match {
      case T_SELECT => {
          // limit/offset
          var lim: Int = -1
          var off: Int = 0
          
          // distinct
          var distinct = false
          
          // projection
          var projexpr: List[(Expression, Option[String])] = Nil
          
          // joins
          var upperjoin: Operation = null
          
          // filter
          var filter: Expression = null
          
          // sort
          var sort: List[(Expression, Boolean)] = Nil
          
          // group
          var group: List[(Expression, Option[String])] = Nil
          
          // having
          var having: Expression = null
          
          // union
          var union: Operation = null
          
          getChilds(node).foreach { t => t.getType match {
              // everything between SELECT and FROM
              case T_COLUMN_LIST => {
                  // AST:
                  // ^(T_COLUMN_LIST ^(T_COLUMN_ITEM ...) ^(T_COLUMN_ITEM ...) ...)
                  
                  val exprs = getChilds(t).map { tr =>
                    (parseExpression(tr.getChild(0)), if (tr.getChildCount == 1) None else Some(tr.getChild(1).getText))
                  } ;
                  projexpr = exprs
                }
                // everything inside the FROM clause, including joins
              case T_FROM => {
                  // AST:
                  // ^(T_FROM 
                  //      ^(T_TABLE_ITEM table_id alias?)
                  //    | ^(T_TABLE_QUERY select_statement alias)
                  //    | ^(T_TABLE_FUNCTION custom_query_call alias?)
                  //    | ^(T_TABLE_VALUES multiple_insert_value_list  alias)
                  //    | ^(T_JOIN table_ref ^(INNER_JOIN/OUTER_JOIN ... )+)
                  //    )
                  val c = getChilds(t)
                  
                  val (joins, normal) = c.partition(_.getType == T_JOIN)
                  
                  def parse(ll: List[Tree]): Operation = {
                    ll match {
                      case x :: Nil => parseTableRef(x)
                      case x :: xs => Join(Cross(), parseTableRef(x), parse(xs))
                      case Nil => throw new IllegalStateException("Internal error: this cannot happen...")
                    }
                  }
                  
                  upperjoin = parse(normal)
                  
                  var ends: List[Operation] = Nil
                  joins foreach {join =>
                    val childs = getChilds(join)
                    val table = parseTableRef(childs.head)
                    val types = childs.tail
                    var lastJoin = doCrossJoin(table, types.head)
                      
                    for (x <- types.tail) {
                      lastJoin = doCrossJoin(lastJoin , x)
                    }
                      
                    ends = lastJoin :: ends
                  }
                    
                  ends foreach {n => upperjoin = Join(Cross(), upperjoin, n) }
                }
                // everything inside the WHERE clause
              case T_WHERE => {
                  // AST:
                  // ^(T_WHERE expression_cond)
                  filter = parseExpression(t.getChild(0))
                }
              case T_SELECT_PARAMS => { // limit & offset parameters
                  // AST:
                  // ^(T_SELECT_PARAMS ^(T_SELECT_LIMIT ..) ^(T_SELECT OFFSET ..))
                  val c = getChilds(t)
                  c foreach { t => t.getType match {
                      case T_SELECT_LIMIT => {
                          if (t.getChildCount == 1) {
                            lim = Integer.parseInt(t.getChild(0).getText)
                          }
                        }
                      case T_SELECT_OFFSET => {
                          off = Integer.parseInt(t.getChild(0).getText)
                        }
                    }}
                }
              case T_ORDER => { // order by clause
                  // AST:
                  // ^(T_ORDER ^(T_COLUMN_ITEM (T_ASC | T_DESC)? (T_FIRST | T_LAST)?)+)
                  val c = getChilds(t)
                  sort = c map (item => 
                    (parseExpression(item.getChild(0)),
                     if (item.getChildCount == 1) false
                     else {
                        item.getChild(1).getType match {
                          case T_ASC => false
                          case T_DESC => true
                        }
                      }
                    )
                  )
                }
              case T_GROUP => { // group by clause
		  // AST:
		  // ^(T_GROUP expression_main+ )
                  group = getChilds(t) map (c => (parseExpression(c), None))
                }
              case T_HAVING => {
                  // AST:
                  // ^(T_HAVING expression_cond )
                  having = parseExpression(t.getChild(0))
                }
              case T_UNION => {
                  // AST:
                  // ^(T_UNION select_command)
                  
                  // we step over the output, getting the first child instead
                  union = buildOperationTree(t.getChild(0)).children.head
                  
                }
              case T_DISTINCT => {
                  // AST : T_DISTINCT alone
                  distinct = true
                }
              case a => throw new IllegalStateException("Internal error: parsing found" + a.toString + 
                                                        "  node: " + node.getText)
            }
          }
          
          if (projexpr.isEmpty) {
            // the parser does not allow empty projection. This should never happen.
            throw new SemanticException("Internal Error: empty projection. This should never happen.")
          }
          
          // Special case of "SELECT select_list ;" (without FROM clause)
          // converted into "SELECT ... FROM VALUES (...);""
          if (upperjoin == null) {
            var newproj: List[(Expression, Option[String])] = Nil
            var vals: List[Expression] = Nil
            projexpr.zipWithIndex foreach { z =>
              newproj = (Field("exp" + z._2), z._1._2) :: newproj
              vals = z._1._1 :: vals
            }
            projexpr = newproj.reverse
            upperjoin = ValuesScan(vals.reverse :: Nil, None, false)
          }
          
          var down: Operation = upperjoin
          if (filter != null) {
            down = Filter(filter, down)
          }
          if (!group.isEmpty) {
            down = Grouping(group, down)
          }
          if (having != null) {
            down = Filter(having, down, true)
          }
          if (!sort.isEmpty) {
            down = Sort(sort, down)
          }
          if (lim != -1 || off != 0) {
            down = LimitOffset(lim, off, down)
          }
          
          down = Projection(projexpr, down)
          
          if (distinct) {
            down = Distinct(down)
          }
          if (union != null) {
            down = Union(down, union)
          }
          
          end = Output(down)
        }
      case T_UPDATE => {
	  // AST:
	  // ^(T_UPDATE table_id update_set+ ^(T_WHERE expression_cond)?)
          val c = getChilds(node)
          // add a scan for the selected table
          var last: Operation = new Scan(getFullTableName(c.head), None, true)

          // gets all the expressions to update
          var e: List[(String, Expression)] = Nil
          c.tail.foreach { t => t.getType match {
              case T_UPDATE_SET => {
		  // AST:
		  // ^(T_UPDATE_SET ^(T_UPDATE_COLUMNS update_field+) ^(T_UPDATE_EXPRS (expression_main | T_DEFAULT)+) )

                  // tuples like (column_name, value)
                  val col = getChilds(t.getChild(0)) map (_.getChild(0).getText.replace("\"", ""))
                  val expr = getChilds(t.getChild(1)) map (parseExpression)

                  e = (col zip expr) ::: e

                }
              case T_WHERE => {
                  last = Filter(parseExpression(t.getChild(0)), last)
                }
            }
          }
          end = Update(e reverse, last)
        }
      case T_INSERT => {
          val c = getChilds(node)
          val fields = if (node.getChildCount == 3) {
            Some(getChilds(node.getChild(2)) map (_.getText.replace("\"", "")))
          } else {
            None
          }
          c(1).getType match {
            // static insert with INSERT INTO ... VALUES ( .. )
            case T_VALUES => {
		// AST:
		// ^(T_INSERT table_id 
		//   ^(T_VALUES ^(T_VALUES (expression_main | T_DEFAULT)+ )+) <-- rows
		//   ^(T_COLUMN_LIST LONG_ID+)? <-- field names
		//  )
                var e: List[List[Expression]] = Nil
                val ch = getChilds(c(1))
                ch foreach { g => e = (getChilds(g) map { t => t.getType match {
                        case T_DEFAULT => Expression(ValueFactory.createNullValue[Value])
                        case _ => parseExpression(t)
                      }
                    }) :: e
                }
                e = e reverse
                
                val valscan = ValuesScan(e, None, false)
                
                end = Insert(getFullTableName(c(0)), fields, valscan)
              }
            case T_SELECT => {
                val s = buildOperationTree(c(1))
                // step over the Output
                end = Insert(getFullTableName(c(0)), fields, s.children.head)
              }
          }
        }
      case T_DELETE => {
	  // AST:
	  // ^(T_DELETE table_id ^(T_WHERE expression_cond)?)
          val scan = Scan(getFullTableName(node.getChild(0)), None, true)
          if (node.getChildCount() == 2) {
            val filter = Filter(parseExpression(node.getChild(1).getChild(0)), scan)
            end = Delete(filter)
          } else {
            end = Delete(scan)
          }
          
        }
      case T_CREATE_TABLE => {
          // building the select statement, resulting in an Output o
          if (node.getChild(1).getType == T_SELECT) {
	    // AST:
	    // ^(T_CREATE_TABLE table_id select_statement)
            val o = buildOperationTree(node.getChild(1).asInstanceOf[CommonTree])
            end = CreateTableAs(getFullTableName(node.getChild(0)), o)
          } else {
	    // AST:
	    // ^(T_CREATE_TABLE table_id ^(T_CREATE_TABLE ^(T_TABLE_ITEM name type 
            // ^(T_TABLE_CONSTRAINT cons)*
            // )*))
            val ch = getChilds(node.getChild(1))
            val cols = ch map {c => (c.getChild(0).getText.replace("\"", ""), c.getChild(1).getText,
                                     if (c.getChildCount ==2) { Nil } else {
                  val cch = getChilds(c).drop(2)
                  cch flatMap {el => el.getChild(0).getType match {
                      case T_NULL => if (el.getChildCount == 2) {
                          Seq(NotNull)
                        } else Nil
                      case T_UNIQUE => Seq(Unique)
                      case T_PRIMARY => Seq(PrimaryKey)
                    }} : Seq[ConstraintType]
                }
              )}
            val cr = CreateTable(getFullTableName(node.getChild(0)), cols)
            end = cr 
          }
        }
      case T_CREATE_VIEW => {
	  // AST:
	  // ^(T_CREATE_VIEW table_id select_statement T_OR?)

          // building the select statement, resulting in an Output o
          val o = buildOperationTree(node.getChild(1).asInstanceOf[CommonTree])
          end = CreateView(getFullTableName(node.getChild(0)), node.getChildCount == 3, o)
        }
      case T_ALTER => {
          // alter table statement
	  // AST:
	  // ^(T_ALTER table_id alter_action*)

          val children = getChilds(node)
          val name = getFullTableName(children.head)        
          // get all alter actions
          val elems = children.tail map { c => c.getType match {
	      // AST:
	      // ^(T_ADD ^(T_TABLE_ITEM name type))
              case T_ADD => AddColumn(c.getChild(0).getChild(0).getText.replace("\"", ""), c.getChild(0).getChild(1).getText)

                // AST:
                // ^(T_ALTER name newType expression_main?)
              case T_ALTER => {
                  val exp = if (c.getChildCount != 2) Some(parseExpression(c.getChild(2))) else None
                  AlterTypeOfColumn(c.getChild(0).getText.replace("\"", ""), c.getChild(1).getText, exp)
                }

                // AST:
                // ^(T_DROP name T_IF?)
              case T_DROP => {
                  DropColumn(c.getChild(0).getText.replace("\"", ""), c.getChildCount != 1)
                }

                // AST:
                // ^(T_RENAME name newname)
              case T_RENAME => RenameColumn(c.getChild(0).getText.replace("\"", ""), c.getChild(1).getText.replace("\"", ""))
            }}
          end = AlterTable(name, elems)
        }
      case T_RENAME => {
          // alter table toto rename to titi
	  // AST:
	  // ^(T_RENAME old=table_id new=table_id)
          end = RenameTable(getFullTableName(node.getChild(0)), getFullTableName(node.getChild(1)))
        }
      case T_DROP => {
          // drop table statement
	  // AST:
	  // ^(T_DROP ^(T_TABLE table_id+) T_IF? T_PURGE?)
          val names = getChilds(node.getChild(0)) map (getFullTableName)
          var ifE = false
          var drop = false
          getChilds(node).tail foreach { _.getType match {
              case T_IF => ifE = true
              case T_PURGE => drop = true
            }}
          node.getChild(0).getType match {
            case T_TABLE => end = DropTables(names, ifE, drop)
            case T_VIEW => end = DropViews(names, ifE)
            case T_SCHEMA => end = DropSchemas(names, ifE, drop)
          }
        }
      case T_INDEX => {
          node.getChild(0).getType match {
            case T_CREATE => {
	        // AST:
	        // ^(T_INDEX T_CREATE table_id field)
                val table = getFullTableName(node.getChild(1))
                val col = node.getChild(2).getText.replace("\"", "")
                end = CreateIndex(table, col)
              }
            case T_DROP => {
	        // AST:
	        // ^(T_INDEX T_CREATE table_id field )
                val table = getFullTableName(node.getChild(1))
                val col = node.getChild(2).getText.replace("\"", "")
                end = DropIndex(table, col)
              }
          }
        }
      case T_EXECUTOR => {
	  // AST:
	  // ^( T_EXECUTOR function_call)
          val l = getChilds(node.getChild(0))
          val name = l(0).getText
          val li = if (l.tail.isEmpty) (Nil) else (getChilds(l(1)).map (parseExpression))
          end = ExecutorCall(name, li)
        }
      case T_SET => {
          // AST:
          // ^(T_SET ^(T_SELECT_COLUMN ...) (T_DEFAULT | QUOTED_STRING)
          val p = getFullTableName(node.getChild(0))
          val c = node.getChild(1)
          val v = c.getType match {
            case QUOTED_STRING => Some(c.getText.substring(1, c.getText.length - 1))
            case T_DEFAULT => None
          }
          end = Set(Some(p), v)
        }
      case T_RESET => {
          // AST:
          // ^(T_RESET (^(T_SELECT_COLUMN ...) | T_ALL) )
          val c = node.getChild(0)
          val p = c.getType match {
            case T_SELECT_COLUMN => Some(getFullTableName(c))
            case T_ALL => None
          }
          end = Set(p, None)
        }
      case T_SHOW => {
          // AST:
          // ^(T_SHOW (^(T_SELECT_COLUMN ...) | T_ALL) )
          val c = node.getChild(0)
          val p = c.getType match {
            case T_SELECT_COLUMN => Some(getFullTableName(c))
            case T_ALL => None
          }
          end = Show(p)
        }
      case T_FUNCTION => {
          // AST:
          // ^(T_FUNCTION ^((T_CREATE | T_DROP) ...)
          val c = node.getChild(0)
          
          def unquote(s: Tree) = s.getText.substring(1, s.getText.length - 1)
          
          c.getType match {
            case T_CREATE => {
                // AST:
                // ^(T_CREATE name as language T_OR?)
                end = CreateFunction(c.getChild(0).getText, unquote(c.getChild(1)), unquote(c.getChild(2)), c.getChildCount != 3)
              }
            case T_DROP => {
                // AST:
                // ^(T_DROP name T_IF?)
                end = DropFunction(c.getChild(0).getText, c.getChildCount != 1)
              }
          }
        }
      case a => throw new SemanticException(a.toString + "  node: " + node.getText)
    }
    end
  }

  private def doCrossJoin(left: Operation, content: Tree): Join = {
    val join = content.getType match {
      case T_INNER_JOIN => {
          val inner = content.getChild(1).getType match {
            case T_CROSS => Cross()
            case T_NATURAL => Natural()
            case T_ON => Inner(parseExpression(content.getChild(1).getChild(0)), false)
          }
          Join(inner, left, parseTableRef(content.getChild(0)))
        }
      case T_OUTER_JOIN => {
          var reverse = false
          val exp = content.getChild(2).getType match {
            case T_NATURAL => None
            case T_ON => Some(parseExpression(content.getChild(2).getChild(0)))
          }
          val outer = content.getChild(1).getType match {
            case T_LEFT => OuterLeft(exp)
            case T_RIGHT => {
                reverse = true;
                OuterLeft(exp)
              }
            case T_FULL => OuterFull(exp)
          }
          
          val elms = if (reverse) {
            (parseTableRef(content.getChild(0)), left)
          } else {
            (left, parseTableRef(content.getChild(0)))
          }
          Join(outer, elms._1, elms._2)
        }
    }
    
    join
  }
  
  private def parseTableRef(head: Tree): Operation = {
    head.getType match {
      // there is only one table --> we insert a scan after 'last'
      case T_TABLE_ITEM => {
          // AST:
          // ^(T_TABLE_ITEM table_id alias?)
          val alias = if (head.getChildCount == 1) None else Some(head.getChild(1).getText)
          Scan(getFullTableName(head.getChild(0)), alias)
        }

        // there is only one custom_query --> we insert a custom_query after 'last'
      case T_TABLE_FUNCTION => {
          // AST:
          // ^(T_TABLE_FUNCTION ^(T_FUNCTION_CALL name ...) alias?)
          val res = doCustomQuery(head.getChild(0))
          val alias = if (head.getChildCount == 1) None else Some(head.getChild(1).getText)
          CustomQueryScan(head.getChild(0).getChild(0).getText, res._1, res._2, alias)
        }

      case T_TABLE_QUERY => {
          // AST:
          // ^( T_TABLE_QUERY select_statement )
          SubQuery(head.getChild(1).getText, buildOperationTree(head.getChild(0)))
        }
    
      case T_TABLE_VALUES => {
          // AST:
          // ^(T_TABLE_VALUES 
          //   ^(T_VALUES ^(T_VALUES expression_main+ )+) <-- rows
          //   alias
          //  )
          val alias = if (head.getChildCount == 1) None else Some(head.getChild(1).getText)
          var e: List[List[Expression]] = Nil
          val ch = getChilds(head.getChild(0))
          ch foreach { g => e = (getChilds(g) map (parseExpression(_))) :: e }
          e = e reverse
                
          val s = ValuesScan(e, alias, false)
          s
        }}
  }

  private def doCustomQuery(ch: Tree) : (Seq[Expression],Seq[Either[String,Operation]]) = {
    // checks argument of the function
    val c: List[Tree] = ch.getChild(1) match {
      case null => Nil // no arguments
      case a => getChilds(a) // 1 or more
    }
    var exp: List[Expression] = Nil
    val d = c flatMap {i => i.getType match {
        case T_TABLE_ITEM => { // argument is a table name (-> String)
            Left(getFullTableName(i.getChild(0))) :: Nil
          }
        case T_TABLE_FUNCTION => { // argument is the result of an other custom query
            val res = doCustomQuery(i.getChild(0))
            val alias = if (i.getChildCount == 1) None else Some(i.getChild(1).getText)
            val o = CustomQueryScan(i.getChild(0).getChild(0).getText, res._1, res._2, alias)
            Right(o) :: Nil
          }
        case T_TABLE_QUERY => {
            val s = SubQuery("",  buildOperationTree(i.getChild(0)))
            Right(s) :: Nil
          }
        case T_TABLE_VALUES => {
            val alias = if (i.getChildCount == 1) None else Some(i.getChild(1).getText)
            var e: List[List[Expression]] = Nil
            val ch = getChilds(i.getChild(0))
            ch foreach { g => e = (getChilds(g) map (parseExpression)) :: e }
            e = e reverse
                
            Right(ValuesScan(e, alias, false)) :: Nil
          }
        case _ => { // argument is a (constant) expression
            exp = parseExpression(i) :: exp
            Nil
          }
      }}
    (exp reverse, d reverse)
  }

  /**
   * Main method for parsing scalar expressions.
   * @param tree an AST corresponding to the expression
   * @return the Expression object
   */
  private def parseExpression(tree: Tree) : Expression = {
    // for readability
    // enables us to use (left + right) without parseExpression() everywhere
    implicit def p(t: Tree): Expression = parseExpression(t)
    val l = getChilds(tree)
    def left = l(0)
    def right = l(1)

    tree.getType match {
      case PLUS => left + right
      case MINUS => {
          l.length match {
            // -a  (unary operator)
            case 1 => -left
            case _ => left - right
          }
        }
      case ASTERISK => left * right
      case DIVIDE => left / right
      case MODULO => left % right
      case EXPONENT => left ^ right

        // string concat operator
      case DOUBLEVERTBAR => left || right
      case T_AND => left & right
      case T_OR => left | right
      case T_NOT => !left

        // real SQL equality (taking NULL into account)
      case EQ => left sqlEquals right
      case NOT_EQ => !(left sqlEquals right)
      case GTH => left > right
      case GEQ => left >= right
      case LTH => left < right
      case LEQ => left <= right
      case SQRT => {
          // converting SQRT operator into the corresponding function
          Expression("Sqrt", List(left))
        }

      case CBRT => Expression("cbrt", List(left))
      case AT_SIGN => {
          // converting ABS operator into the corresponding function
          Expression("Abs", List(left))
        }

        
      case TILDE => { if (l.tail.isEmpty) {
            // TODO: bitwize NOT (or drop it?)
            throw new UnsupportedOperationException("Not yet implemented.")
          } else {
            left ~ right
          }
        }
      case ITILDE => left.~(right, true)
      case NOTILDE => !(left ~ right)
      case NOITILDE => !(left.~(right, true))

        // TODO: factorial operator
      case FACTORIAL_PREFIX => throw new UnsupportedOperationException("Not yet implemented.")
      case T_NULL => Expression(ValueFactory.createNullValue[Value])

        // boolean unknown is stored as NULL for simplicity, just like PostgreSQL does.
      case T_UNKNOWN => Expression(ValueFactory.createNullValue[Value])
        
      case T_CAST => left -> SQLValueFactory.getTypeCodeFromSqlIdentifier(l(1).getText)

        // sql like operator.
      case T_LIKE => left like right
        // sql like operator.
      case T_ILIKE => left.like(right, true)
        // sql imilar to operator.
      case T_SIMILAR => left similarTo right
      case T_SELECT_COLUMN => {
	  // AST:
	  // ^(T_SELECT_COLUMN LONG_ID+ )
          val rev = l.reverse
          val name = rev.head.getText.replace("\"", "")
          if (rev.tail.isEmpty) {
            Field(name)
          } else {
            Field(name, rev.tail.map(_.getText).reduceLeft(_ + "." + _))
          }
        }
      case T_SELECT_COLUMN_STAR => {
	  // AST:
	  // ^(T_SELECT_COLUMN_STAR LONG_ID* ASTERISK select_star_except? )
          var rev = l.reverse
          var except: Seq[String] = List.empty
          if (rev.head.getType == T_EXCEPT) {
            except = getChilds(rev.head).map (_.getText.replace("\"", ""))
            rev = rev.tail
          }
          if (rev.tail.isEmpty) {
            Field.star(except, None)
          } else {
            Field.star(except, Some(rev.tail.map(_.getText).reduceLeft(_ + "." + _)))
          }
        }
        // all ISNULL, NOTNULL, IS NULL are encoded into the same expression
      case T_NULL_CHECK => Expression(IsNullEvaluator(left))
      case T_IN => {
          right.getType match {
            // AST:
            // ^(T_IN expression_main ^(T_SELECT ... ))
            case T_SELECT => left in buildOperationTree(right)

              // AST:
              // ^(T_IN expression_main ^(T_EXPR_LIST expression_main+ ))
            case T_EXPR_LIST => left in getChilds(right).map (parseExpression)
          }
        }
      case T_BETWEEN => (l(0) >= l(1)) & (l(0) <= l(2))
      case T_FUNCTION_CALL => {
	  // AST:
	  // ^( T_FUNCTION_CALL name ^(T_EXPR_LIST expression_main+ )? )
          // evaluate parameters iif there is parameters (cf. grammar)
          val li = if (l.tail.isEmpty) (Nil) else (getChilds(right).map (parseExpression))
          Expression(left.getText, li)
        }
        
      case T_EXISTS => {
          Expression(ExistsEvaluator(buildOperationTree(left)))
        }
        // constant values are built from String by SQLValueFactory
      case a => Expression(SQLValueFactory.createValue(tree.getText, a))
    }
  }

  private def getFullTableName(node: Tree) = {
    getChilds(node) map(_.getText) reduceLeft (_ + "." +  _) replace("\"", "")
  }
  

  /**
   * Utility method to get the children of a Tree into a Scala list
   */
  private def getChilds(tree: Tree): List[Tree] = {
    var c: List[Tree] = Nil
    // note: the range is built decrementing to 0 because elements are appened
    // at the beginning of the list (for performance...) which reverses the list
    if (tree.getChildCount != 0) {
      for (i <- tree.getChildCount to 0 by -1) {
        c = tree.getChild(i) :: c
      } }
    // not sure this is useful, be it seem ANTLR can sometimes append null items...
    c.filter ( _ != null )
  }
}
