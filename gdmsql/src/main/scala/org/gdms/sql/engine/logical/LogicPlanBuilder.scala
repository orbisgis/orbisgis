/* OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.sql.engine.logical

/**
 * This object contains all the logic for building a Logical Query Plan from the Abstract Syntactic Tree
 * returned by the parser (ANTLR for now).
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
import org.antlr.runtime.tree.CommonTree
import org.antlr.runtime.tree.Tree
import org.gdms.sql.engine.parsing.GdmSQLParser._
import org.gdms.data.values.SQLValueFactory
import org.gdms.data.values.Value
import org.gdms.data.values.ValueFactory
import org.gdms.sql.engine.SemanticException
import org.gdms.sql.engine.operations._
import org.gdms.sql.evaluator.AggregateEvaluator
import org.gdms.sql.evaluator.Expression
import org.gdms.sql.evaluator.Field
import org.gdms.sql.evaluator.FieldEvaluator
import org.gdms.sql.evaluator.IsNullEvaluator
import org.gdms.sql.function.FunctionManager
import org.gdms.sql.function.ScalarFunction

object LogicPlanBuilder {

  def buildLogicPlan(tree: Tree): Operation = {
    val a = buildOperationTree(tree)
    processOperationTree(a)
  }


  /**
   * Builds an abstract operation tree from an abstract syntactic tree.
   */
  private def buildOperationTree(node: Tree): Operation = {
    // output
    var end: Operation = null


    node.getType match {
      case T_SELECT => {
          val o = new Output()
          var last: Operation = o
          var lim: Int = -1
          var off: Int = 0
          getChilds(node).foreach { t => t.getType match {
              // everything between SELECT and FROM
              case T_COLUMN_LIST => {
                  // we remove the STAR column (*) from the children (no need to project anything
                  // in this case).
                  // and we parse what's left into a list of expressions
                  val exprs = getChilds(t).map { tr =>
                    (parseExpression(tr.getChild(0)), if (tr.getChildCount == 1) None else Some(tr.getChild(1).getText))
                  } ;
                  if (!exprs.isEmpty) {
                    val f = Projection(exprs)
                    // Projection gets inserted just before Output (o)
                    f.children = o.children
                    o.children = f :: Nil
                    last = f
                  }
                }
                // everything inside the FROM clause, including joins
              case T_FROM => {
                  val c = getChilds(t)
                  (c, c.head.getType) match {
                    // there is only one table --> we insert a scan after 'last'
                    case (head :: Nil, T_TABLE_ITEM) => {
                        val alias = if (head.getChildCount == 1) None else Some(head.getChild(1).getText)
                        last = insertAfter(last, Scan(getFullTableName(head.getChild(0)), alias))
                      }

                      // there is only one custom_query --> we insert a custom_query after 'last'
                    case (head :: Nil, T_TABLE_FUNCTION) => {
                        val res = doCustomQuery(head.getChild(0))
                        val alias = if (head.getChildCount == 1) None else Some(head.getChild(1).getText)
                        val cus = CustomQueryScan(head.getChild(0).getChild(0).getText, res._1, res._2, alias)
                        last = insertAfter(last, cus)
                      }

                    case (head :: Nil, T_TABLE_QUERY) => {
                        val s = SubQuery(head.getChild(1).getText)
                        s.children = buildOperationTree(head.getChild(0)) :: Nil

                        last = insertAfter(last, s)
                      }
                      // there is several tables (implicit join)
                    case _ => {
                        // we insert a Cross join between all tables
                        val j = Join(Cross())
                        last.addChild(j)
                        last = j
                        c foreach { ch => ch.getType match {
                            case T_TABLE_ITEM => {
                                val alias = if (ch.getChildCount == 1) None else Some(ch.getChild(1).getText)
                                last.addChild(Scan(getFullTableName(ch.getChild(0)), alias))
                              }
                            case T_TABLE_FUNCTION => {
                                val alias = if (ch.getChildCount == 1) None else Some(ch.getChild(1).getText)
                                val res = doCustomQuery(c.head)
                                val cus = CustomQueryScan(ch.getChild(0).getText, res._1, res._2, alias)
                                last.addChild(cus)
                              }
                            case T_TABLE_QUERY => {
                                val s = SubQuery(ch.getChild(1).getText)
                                s.children = buildOperationTree(ch.getChild(0)) :: Nil
                                last.addChild(s)
                              }
                            case T_JOIN => doCrossJoin(getChilds(ch), last)
                          }
                        }
                      }
                  }
                }
                // everything inside the WHERE clause
              case T_WHERE => {
                  val f = Filter(parseExpression(t.getChild(0)))

                  // we get the parent of 'f': either a Projection, or Output
                  // if we cannot find a projection
                  val parent = (o.allChildren filter (_.isInstanceOf[Projection]) headOption) getOrElse(o)
                  f.children = parent.children
                  parent.children = f :: Nil
                  last = f
                }
              case T_SELECT_PARAMS => { // limit & offset parameters
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
                  val c = getChilds(t)
                  val express = c map (item => 
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
                  val s = Sort(express)
                  // we get the parent of 'f': either a Projection, or Output
                  // if we cannot find a projection
                  val parent = (o.allChildren filter (_.isInstanceOf[Projection]) headOption) getOrElse(o)
                  s.children = parent.children
                  parent.children = s :: Nil
                  last = s
                }
              case T_GROUP => { // group by clause
                  val ex = getChilds(t) map (c => (parseExpression(c), None))
                  val g = new Grouping(ex)
                  val parent = (o.allChildren filter (_.isInstanceOf[Projection]) headOption) getOrElse(last)
                  g.children = parent.children
                  parent.children = g :: Nil
                }
              case a: Any => throw new SemanticException(a.toString + "  node: " + node.getText)
            }
          }
          if (lim != -1 || off != 0) {
            val l = LimitOffset(lim, off)
            l.children = o.children
            o.children = l :: Nil
          }
          end = o
        }
      case T_UPDATE => {
          val c = getChilds(node)
          // add a scan for the selected table
          var last: Operation = new Scan(getFullTableName(c.head), None, true)

          // gets all the expressions to update
          var e: Seq[(String, Expression)] = Nil
          c.tail.foreach { t => t.getType match {
              case T_UPDATE_SET => {
                  // tuples like (column_name, value)
                  val col = getChilds(t.getChild(0)) map (_.getChild(0).getText)
                  val expr = getChilds(t.getChild(1)) map (parseExpression)

                  e = col zip expr

                }
              case T_WHERE => {
                  val f = Filter(parseExpression(t.getChild(0)))

                  f.children = last :: Nil
                  last = f
                }
            }
          }
          end = new Update(e)
          end.children = last :: Nil
        }
      case T_INSERT => {
          val c = getChilds(node)
          val fields = if (node.getChildCount == 3) {
            Some(getChilds(node.getChild(2)) map (n => n.getText))
          } else {
            None
          }
          c(1).getType match {
            // static insert with INSERT INTO ... VALUES ( .. )
            case T_VALUES => {
                var e: List[Array[Expression]] = Nil
                val ch = getChilds(c(1))
                // rows are in (T_VALUES (T_VALUES row) (T_VALUE row) ... )
                ch foreach { g => e = (getChilds(g) map { t => t.getType match {
                        case T_DEFAULT => null
                        case _ => parseExpression(t)
                      }
                    } toArray) :: e
                }
                
                end = StaticInsert(getFullTableName(c(0)), e, fields)
              }
          }
        }
      case T_DELETE => {
          end = Delete()
          val s = Scan(getFullTableName(node.getChild(0)), None, true)
          if (node.getChildCount() == 2) {
            val filter = Filter(parseExpression(node.getChild(1).getChild(0)))
            filter.children = s :: Nil
            
            end.children = filter :: Nil
          } else {
            end.children = s :: Nil
          }
          
        }
      case T_CREATE_TABLE => {
          // building the select statement, resulting in an Output o
          if (node.getChild(1).getType == T_SELECT) {
            val o = buildOperationTree(node.getChild(1).asInstanceOf[CommonTree])
            val cr = CreateTableAs(getFullTableName(node.getChild(0)))
            cr.children = o :: Nil
            end = cr 
          } else {
            val ch = getChilds(node.getChild(1))
            val cols = ch map (c => (c.getChild(0).getText, c.getChild(1).getText))
            val cr = CreateTable(getFullTableName(node.getChild(0)), cols)
            end = cr 
          }
        }
      case T_CREATE_VIEW => {
          // building the select statement, resulting in an Output o
            val o = buildOperationTree(node.getChild(1).asInstanceOf[CommonTree])
            val cr = CreateView(getFullTableName(node.getChild(0)), node.getChildCount == 3)
            cr.children = o :: Nil
            end = cr 
        }
      case T_ALTER => {
          // alter table statement
          val children = getChilds(node)
          val name = getFullTableName(children.head)        
          // get all alter actions
          val elems = children.tail map { c => c.getType match {
              case T_ADD => AddColumn(c.getChild(0).getChild(0).getText.replace("\"", ""), c.getChild(0).getChild(1).getText)
              case T_ALTER => {
                  val exp = if (c.getChildCount != 2) Some(parseExpression(c.getChild(2))) else None
                  AlterTypeOfColumn(c.getChild(0).getText.replace("\"", ""), c.getChild(1).getText, exp)
                }
              case T_DROP => {
                  DropColumn(c.getChild(0).getText.replace("\"", ""), c.getChildCount != 1)
                }
              case T_RENAME => RenameColumn(c.getChild(0).getText.replace("\"", ""), c.getChild(1).getText.replace("\"", ""))
            }}
          end = AlterTable(name, elems)
        }
      case T_RENAME => {
          // alter table toto rename to titi
          end = RenameTable(getFullTableName(node.getChild(0)), getFullTableName(node.getChild(1)))
      }
      case T_DROP => {
          // drop table statement
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
          }
        }
      case T_INDEX => {
          node.getChild(0).getType match {
            case T_CREATE => {
                val table = getFullTableName(node.getChild(1))
                val col = node.getChild(2).getText.replace("\"", "")
                end = CreateIndex(table, col)
              }
            case T_DROP => {
                val table = getFullTableName(node.getChild(1))
                val col = node.getChild(2).getText.replace("\"", "")
                end = DropIndex(table, col)
              }
          }
        }
      case T_EXECUTOR => {
          val l = getChilds(node.getChild(0))
          val name = l(0).getText
          val li = if (l.tail.isEmpty) (Nil) else (getChilds(l(1)).map (parseExpression))
          end = ExecutorCall(name, li)
        }
      case a => throw new SemanticException(a.toString + "  node: " + node.getText)
    }
    end
  }

  private def doCrossJoin(c: List[Tree], last: Operation): Unit = {
    val chh = c.head
    chh.getType match {
      case T_TABLE_ITEM => last.addChild(Scan(getFullTableName(chh.getChild(0))))
      case T_TABLE_FUNCTION => {
          val res = doCustomQuery(chh)
          val alias = if (chh.getChildCount == 1) None else Some(chh.getChild(1).getText)
          val cus = CustomQueryScan(chh.getChild(0).getText, res._1, res._2, alias)
          last.addChild(cus)
        }
      case T_TABLE_QUERY => {
          val s = SubQuery(chh.getChild(1).getText)
          s.children = buildOperationTree(chh.getChild(0)) :: Nil
          last.addChild(s)
        }
    }

    c.tail foreach { ch =>
      ch.getType match {
        case T_CROSS =>
        case T_INNER_JOIN => doCrossJoin(getChilds(ch), last)
      }
    }
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
            val res = doCustomQuery(i)
            val alias = if (i.getChildCount == 1) None else Some(i.getChild(1).getText)
            val o = CustomQueryScan(i.getChild(0).getText, res._1, res._2, alias)
            Right(o) :: Nil
          }
        case T_TABLE_QUERY => {
            val s = SubQuery("")
            s.children = buildOperationTree(i.getChild(0)) :: Nil
            Right(s) :: Nil
          }
        case _ => { // argument is a (constant) expression
            exp = parseExpression(i) :: exp
            Nil
          }
      }}
    (exp reverse, d)
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

        // TODO: cube root operator
      case CBRT => throw new UnsupportedOperationException("Not yet implemented.")
      case AT_SIGN => {
          // converting ABS operator into the corresponding function
          Expression("Abs", List(left))
        }

        // TODO: bitwize NOT (or drop it?)
      case TILDE => throw new UnsupportedOperationException("Not yet implemented.")

        // TODO: factorial operator
      case FACTORIAL_PREFIX => throw new UnsupportedOperationException("Not yet implemented.")
      case T_NULL => Expression(ValueFactory.createNullValue[Value])

        // boolean unknown is stored as NULL for simplicity, just like PostgreSQL does.
      case T_UNKNOWN => Expression(ValueFactory.createNullValue[Value])

        // sql like operator. NOT FEATURE COMPLETE: limited by GDMS like operator.
      case T_LIKE => left like right
      case T_SELECT_COLUMN => {
          val rev = l.reverse
          val name = rev.head.getText.replace("\"", "")
          if (rev.tail.isEmpty) {
            Field(name)
          } else {
            Field(name, rev.tail.map(_.getText).reduceLeft(_ + "." + _))
          }
        }
        // all ISNULL, NOTNULL, IS NULL are encoded into the same expression
      case T_NULL_CHECK => Expression(IsNullEvaluator(left))
      case T_IN => {
          right.getType match {
            // TODO: converting IN with subquery into special joins
            case T_SELECT => throw new UnsupportedOperationException("Not yet implemented.")
            case T_EXPR_LIST => left in getChilds(right).map (parseExpression)
          }
        }
      case T_FUNCTION_CALL => {
          // evaluate parameters iif there is parameters (cf. grammar)
          val li = if (l.tail.isEmpty) (Nil) else (getChilds(right).map (parseExpression))
          Expression(left.getText, li)
        }
        
      case T_EXISTS => {
          throw new UnsupportedOperationException("Not yet implemented.")
        }
        // constant values are built from String by SQLValueFactory
      case a => Expression(SQLValueFactory.createValue(tree.getText, a))
    }
  }

  private def insertAfter(base: Operation, toInsert: Operation): Operation = {
    if (!base.children.isEmpty) {
      toInsert.children = toInsert.children ++ base.children
      base.children = Nil
    }
    base.addChild(toInsert)
  }

  private def getFullTableName(node: Tree) = {
    getChilds(node) map(_.getText) reduceLeft (_ + "." +  _)
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

  private def processOperationTree(op: Operation): Operation = {
    op.allChildren foreach { case p: Projection =>
        def findAggregateFunctions(e: Expression): Seq[(Expression, Option[String])] = {
          e.evaluator match {
            case a: AggregateEvaluator => {
                e.evaluator = FieldEvaluator(a.f.getName)
                (Expression(a), Some(a.f.getName)) :: Nil}
            case e => e.childExpressions flatMap (findAggregateFunctions)
          }
        }
        
        var parent: Option[Operation] = None
        var gr = p.children.find(_.isInstanceOf[Grouping])
        if (gr.isEmpty) {
          parent = p.children.find(_.isInstanceOf[Sort])
          gr = p.children.find(_.isInstanceOf[Sort]) flatMap (_.children.find(_.isInstanceOf[Grouping]))
        }
        
        if (!parent.isDefined) {
          parent = Some(p)
        }
        
        if (gr.isDefined) {
          // there is a Grouping
          val group = gr.get.asInstanceOf[Grouping]
          
          // directly referenced fields/aliases in GROUP BY
          val groupFields: List[FieldEvaluator] = group.exp flatMap (_._1.evaluator match {
              case a: FieldEvaluator => a :: Nil
              case _ => Nil
            })
          
          // selected items with aliases
          val fieldsAl = p.exp filter (_._2.isDefined)
          
          // directly aliases/fields in GROUP BY
          val aliases = groupFields map (_.name)
          
          // directly selected fields
          val selFields = p.exp flatMap (_._1.evaluator match {
              case a: FieldEvaluator => a :: Nil
              case _ => Nil
            })
          
          // check directly selected fields are referenced in GROUP BY clause
          selFields foreach {f => 
            if (!aliases.contains(f.name)) {
              throw new SemanticException("field " + f.name + " cannot be selected because it is not present in the GROUP BY clause.")
            }
          }
          
          // converts SELECT toto + 12 as titi FROM ... GROUP BY titi
          // into something like (pseudo-SQL): SELECT titi FROM ... GROUP BY toto + 12 as titi
          fieldsAl foreach {f =>
            if (aliases.contains(f._2.get)) {
              val t = f._1.evaluator
              // we replace the evaluator with a FieldEvaluator with the alias name
              f._1.evaluator = FieldEvaluator(f._2.get)
              
              // we replace the Field in the GROUP BY clause by the actual expression
              group.exp.find(g => g._1.evaluator match {
                  case a: FieldEvaluator => f._2.get == a.name
                  case _ => false
                }).get._1.evaluator = t
            }
          }
        }
        val aggF = p.exp flatMap (e => findAggregateFunctions(e._1))
        
        if (!aggF.isEmpty) {
          // special case of a Projection with Aggregated functions
          // an Aggregate is inserted afterwards, and the functions are
          // replaces with fields in the Projection
          // there is some AggregateEvaluator expressions
          val c = new Aggregate(aggF)
          c.children = parent.get.children
          parent.get.children = c :: Nil
        }
      case _ =>
        
    }
    op
  }
}
