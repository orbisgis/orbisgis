package org.gdms.sql.instruction;

import java.util.HashMap;

import org.gdms.data.DataSourceFactory;
import org.gdms.sql.parser.ASTSQLAndExpr;
import org.gdms.sql.parser.ASTSQLBetweenClause;
import org.gdms.sql.parser.ASTSQLColRef;
import org.gdms.sql.parser.ASTSQLCompareExpr;
import org.gdms.sql.parser.ASTSQLCompareExprRight;
import org.gdms.sql.parser.ASTSQLCompareOp;
import org.gdms.sql.parser.ASTSQLCustom;
import org.gdms.sql.parser.ASTSQLExistsClause;
import org.gdms.sql.parser.ASTSQLFunction;
import org.gdms.sql.parser.ASTSQLFunctionArgs;
import org.gdms.sql.parser.ASTSQLInClause;
import org.gdms.sql.parser.ASTSQLIsClause;
import org.gdms.sql.parser.ASTSQLLValueElement;
import org.gdms.sql.parser.ASTSQLLValueList;
import org.gdms.sql.parser.ASTSQLLikeClause;
import org.gdms.sql.parser.ASTSQLLiteral;
import org.gdms.sql.parser.ASTSQLLvalue;
import org.gdms.sql.parser.ASTSQLLvalueTerm;
import org.gdms.sql.parser.ASTSQLNotExpr;
import org.gdms.sql.parser.ASTSQLOrExpr;
import org.gdms.sql.parser.ASTSQLOrderBy;
import org.gdms.sql.parser.ASTSQLOrderByElem;
import org.gdms.sql.parser.ASTSQLOrderByList;
import org.gdms.sql.parser.ASTSQLOrderDirection;
import org.gdms.sql.parser.ASTSQLProductExpr;
import org.gdms.sql.parser.ASTSQLSelect;
import org.gdms.sql.parser.ASTSQLSelectCols;
import org.gdms.sql.parser.ASTSQLSelectList;
import org.gdms.sql.parser.ASTSQLSumExpr;
import org.gdms.sql.parser.ASTSQLTableList;
import org.gdms.sql.parser.ASTSQLTableRef;
import org.gdms.sql.parser.ASTSQLTerm;
import org.gdms.sql.parser.ASTSQLUnaryExpr;
import org.gdms.sql.parser.ASTSQLUnion;
import org.gdms.sql.parser.ASTSQLWhere;
import org.gdms.sql.parser.Node;
import org.gdms.sql.parser.SimpleNode;
import org.gdms.sql.parser.Token;

/**
 * Clase con distintos m�todos de utilidad
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class Utilities {
	private static HashMap<Class, Class> adapters = new HashMap<Class, Class>();

	static {
		adapters.put(ASTSQLAndExpr.class, AndExprAdapter.class);
		adapters.put(ASTSQLCompareExpr.class, CompareExprAdapter.class);
		adapters.put(ASTSQLNotExpr.class, NotExprAdapter.class);
		adapters.put(ASTSQLOrExpr.class, OrExprAdapter.class);
		adapters.put(ASTSQLProductExpr.class, ProductExprAdapter.class);
		adapters.put(ASTSQLSelect.class, SelectAdapter.class);
		adapters.put(ASTSQLSumExpr.class, SumExprAdapter.class);
		adapters.put(ASTSQLTerm.class, TermAdapter.class);
		adapters.put(ASTSQLUnaryExpr.class, UnaryExprAdapter.class);
		adapters.put(ASTSQLUnion.class, UnionAdapter.class);
		adapters.put(ASTSQLCompareExprRight.class,
				CompareExprRigthAdapter.class);
		adapters.put(ASTSQLCompareOp.class, CompareOpAdapter.class);
		adapters.put(ASTSQLCustom.class, CustomAdapter.class);
		adapters.put(ASTSQLLikeClause.class, LikeClauseAdapter.class);
		adapters.put(ASTSQLInClause.class, InClauseAdapter.class);
		adapters.put(ASTSQLBetweenClause.class, BetweenClauseAdapter.class);
		adapters.put(ASTSQLIsClause.class, IsClauseAdapter.class);
		adapters.put(ASTSQLExistsClause.class, ExistsClauseAdapter.class);
		adapters.put(ASTSQLFunction.class, FunctionAdapter.class);
		adapters.put(ASTSQLFunctionArgs.class, FunctionArgsAdapter.class);
		adapters.put(ASTSQLLiteral.class, LiteralAdapter.class);
		adapters.put(ASTSQLColRef.class, ColRefAdapter.class);
		adapters.put(ASTSQLLvalue.class, LValueAdapter.class);
		adapters.put(ASTSQLLValueElement.class, LValueElementAdapter.class);
		adapters.put(ASTSQLLValueList.class, LValueListAdapter.class);
		adapters.put(ASTSQLLvalueTerm.class, LValueTermAdapter.class);
		adapters.put(ASTSQLOrderBy.class, OrderByAdapter.class);
		adapters.put(ASTSQLOrderByList.class, OrderByListAdapter.class);
		adapters.put(ASTSQLOrderByElem.class, OrderByElemAdapter.class);
		adapters.put(ASTSQLOrderDirection.class, OrderDirectionAdapter.class);
		adapters.put(ASTSQLSelectCols.class, SelectColsAdapter.class);
		adapters.put(ASTSQLSelectList.class, SelectListAdapter.class);
		adapters.put(ASTSQLTableList.class, TableListAdapter.class);
		adapters.put(ASTSQLTableRef.class, TableRefAdapter.class);
		adapters.put(ASTSQLWhere.class, WhereAdapter.class);
	}

	/**
	 * Obtienen el tipo de un nodo del arbol sint�ctico de entrada en caso de
	 * que dicho nodo tenga un solo token. Si el nodo tiene varios token's se
	 * devuelve un -1
	 * 
	 * @param n
	 *            Nodo cuyo tipo se quiere conocer
	 * 
	 * @return Tipo del token del nodo. Una constante de la interfaz
	 *         SQLEngineConstants
	 */
	public static int getType(Node n) {
		SimpleNode node = (SimpleNode) n;

		if (node.first_token == node.last_token) {
			return node.first_token.kind;
		}

		return -1;
	}

	/**
	 * Obtiene el texto de un nodo
	 * 
	 * @param n
	 *            Nodo del cual se quiere obtener el texto
	 * 
	 * @return Texto del nodo
	 */
	public static String getText(Node n) {
		return getText((SimpleNode) n);
	}

	/**
	 * Obtiene el texto de un nodo
	 * 
	 * @param s
	 *            Nodo del cual se quiere obtener el texto
	 * 
	 * @return Texto del nodo
	 */
	public static String getText(SimpleNode s) {
		String ret = "";

		for (Token tok = s.first_token; tok != s.last_token.next; tok = tok.next) {
			ret += (" " + tok.image);
		}

		return ret.trim();
	}

	/**
	 * Construye un arbol de adaptadores correspondiente al arbol sint�ctico
	 * cuya raiz es el nodo que se pasa como par�metro. El �rbol se construir�
	 * mientras se encuentren clases adaptadoras. En el momento que no se
	 * encuentre la clase adaptadora de un nodo no se seguir� profundizando por
	 * esa rama. Despues de la construcci�n del arbol se invoca el m�todo
	 * calculateLiteralCondition de todos los adaptadores del arbol que sean
	 * instancias de Expression
	 * 
	 * @param root
	 *            Nodo raiz
	 * @param sql
	 *            DOCUMENT ME!
	 * @param ds
	 *            DOCUMENT ME!
	 * 
	 * @return Adaptador raiz
	 */
	public static Adapter buildTree(Node root, String sql, DataSourceFactory ds) {
		Adapter rootAdapter = recursiveBuildTree(root);
		rootAdapter.setInstructionContext(new InstructionContext());
		rootAdapter.getInstructionContext().setSql(sql);
		rootAdapter.getInstructionContext().setDSFActory(ds);

		return rootAdapter;
	}

	/**
	 * M�todo recursivo para la creaci�n del arbol de adaptadores
	 * 
	 * @param root
	 *            raiz del sub�rbol
	 * 
	 * @return raiz del arbol creado o null si no se encuentra la clase
	 *         adaptadora
	 */
	private static Adapter recursiveBuildTree(Node root) {
		Adapter a;

		try {
			a = getAdapter(root);
		} catch (Exception e) {
			// e.printStackTrace();
			// No se encontr� la clase adaptadora
			return null;
		}

		a.setEntity(root);

		Adapter[] childs = new Adapter[root.jjtGetNumChildren()];
		int index = 0;
		for (int i = 0; i < root.jjtGetNumChildren(); i++) {
			Adapter child = recursiveBuildTree(root.jjtGetChild(i));

			if (child != null) {
				child.setParent(a);

				// Se encontr� la clase adaptadora
				childs[index] = child;
				index++;
			}
		}
		Adapter[] trueChilds = new Adapter[index];
		if (index != root.jjtGetNumChildren()) {
			System.arraycopy(childs, 0, trueChilds, 0, index);
			a.setChilds(trueChilds);
		} else {
			a.setChilds(childs);
		}

		return a;
	}

	/**
	 * Obtiene una instancia nueva de la clase adaptadora de un nodo
	 * 
	 * @param node
	 *            nodo de cual se quiere obtener la clase adaptadora
	 * 
	 * @return instancia de la clase adaptadora
	 * 
	 * @throws InstantiationException
	 *             Si no se puede instanciar la clase
	 * @throws IllegalAccessException
	 *             Si no se puede acceder a la clase
	 */
	private static Adapter getAdapter(Node node) throws InstantiationException,
			IllegalAccessException {
		return (Adapter) ((Class) adapters.get(node.getClass())).newInstance();
	}

	/**
	 * Devuelve true si todas las expresiones que se pasan en el array son
	 * literales
	 * 
	 * @param childs
	 *            conjunto de adaptadores
	 * 
	 * @return true si se cumple que para cada elemento del array childs que es
	 *         Expresion, es literal
	 */
	public static boolean checkExpressions(Adapter[] childs) {
		for (int i = 0; i < childs.length; i++) {
			if (!(childs[i] instanceof Expression)) {
				continue;
			}

			if (!((Expression) childs[i]).isLiteral()) {
				return false;
			}
		}

		return true;
	}

	/*
	 * Establece las tablas de la instrucci�n y la fuente de datos resultante de
	 * la cl�usula from
	 * 
	 * @param root raiz del arbol de adaptadores donde se aplicar� el m�todo
	 * @param tables tablas de la clausula from @param source fuente de datos de
	 * la que obtiene los valores los objetos field, resultado de la clausula
	 * from
	 * 
	 * public static void setTablesAndSource(Adapter root, DataSource[] tables,
	 * DataSource source) { if (root instanceof FieldSupport) { FieldSupport fs =
	 * (FieldSupport) root; fs.setDataSource(source); fs.setTables(tables); }
	 * Adapter[] hijos = root.getChilds(); for (int i = 0; i < hijos.length;
	 * i++) { setTablesAndSource(hijos[i], tables, source); } }* public static
	 * void setTablesAndSource(Adapter root, DataSource table, DataSource
	 * source){ setTablesAndSource(root, new DataSource[]{table}, source); }
	 */

	/**
	 * Simplifica las expresiones del �rbol de adaptadores
	 * 
	 * @param root
	 *            raiz del arbol que se simplifica
	 */
	public static void simplify(Adapter root) {
		if (root instanceof Expression) {
			Expression ex = (Expression) root;
			ex.simplify();
		}

		Adapter[] hijos = root.getChilds();

		for (int i = 0; i < hijos.length; i++) {
			simplify(hijos[i]);
		}
	}
}
