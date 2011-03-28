/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */

/*
package org.orbisgis.core.renderer.se.parameter.real;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gdms.data.DataSource;

**
 *
 * @author maxence
 *
public class RealParameterFactory {

	*
	 * Convert a string into its corresponding RealParameter expression tree
	 * 
	 * Supported operators are : + - * / log sqrt
	 * Attributes names can be enclose within square brackets e.g: <GDI_2010>
	 * Grouping can be done with parenthesis
	 *
	 * Examples : Log(<population>)*(0.0034) + 4
	 *    
	 * 
	 * @param expression 
	 * @return RealParameter expression tree
	 *
	public static RealParameter createFromString(String expression, DataSource ds) {
		try {
			String expr = expression.trim();
			if (expr.length() == 0) {
				return null;
			}
			ArrayList<Token> tokens = tokenize(expr);
			RealParameter p = createFromList(tokens, ds);
			System.out.println("Created from list: " + p);
			return p;
		} catch (Exception ex) {
			Logger.getLogger(RealParameterFactory.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	private static RealParameter createFromList(List<Token> tokens, DataSource ds) {
		int i = findTopToken(tokens);

		Token top = tokens.get(i);
		TokenType type = top.getType();

		System.out.println("Type: " + type);

		RealParameter param;

		if (type == TokenType.ADD || type == TokenType.SUB
				|| type == TokenType.MUL || type == TokenType.DIV) {
			RealBinaryOperator p = new RealBinaryOperator();
			if (type == TokenType.ADD) {
				p.setOperator(RealBinaryOperator.RealBinaryOperatorType.ADD);
			} else if (type == TokenType.SUB) {
				p.setOperator(RealBinaryOperator.RealBinaryOperatorType.SUB);
			} else if (type == TokenType.MUL) {
				p.setOperator(RealBinaryOperator.RealBinaryOperatorType.MUL);
			} else if (type == TokenType.DIV) {
				p.setOperator(RealBinaryOperator.RealBinaryOperatorType.DIV);
			}

			p.setLeftValue(createFromList(tokens.subList(0, i), ds));
			p.setRightValue(createFromList(tokens.subList(i + 1, tokens.size()), ds));
			param = p;
		} else if (type == TokenType.LOG || type == TokenType.SQRT) {
			RealUnaryOperator p = new RealUnaryOperator();
			if (type == TokenType.LOG) {
				p.setOperator(RealUnaryOperator.RealUnitaryOperatorType.LOG);
			} else {
				p.setOperator(RealUnaryOperator.RealUnitaryOperatorType.SQRT);
			}
			p.setOperand(createFromList(tokens.subList(1, tokens.size()), ds));

			param = p;

		} else if (type == TokenType.ATTR) {
			RealAttribute p = null;
			System.out.println("Attr ->" + top.token.substring(1, top.token.length() - 1) + "<-");
			p = new RealAttribute(top.token.substring(1, top.token.length() - 1));
			return p;
		} else if (type == TokenType.CST) {
			RealLiteral p = new RealLiteral(top.token);
			return p;
		} else if (type == TokenType.GROUP) {
			return createFromString(top.token.substring(1, top.token.length() - 1), ds);
		} else {
			param = null;
		}


		while (!tokens.isEmpty()) {
			System.out.println(tokens.remove(0));
		}

		return param;
	}

	private static ArrayList<Token> tokenize(String expr) throws Exception {
		ArrayList<Token> tokens = new ArrayList<Token>();
		String str = expr;

		boolean wantBinaryOp = false;

		while (str.length() > 0) {
			System.out.println("Str is ->" + str + "<-");
			str = str.trim();

			if (wantBinaryOp) {
				wantBinaryOp = false;
				if (str.charAt(0) == '*') {
					tokens.add(new Token("*"));
					str = str.substring(1);
				} else if (str.charAt(0) == '/') {
					tokens.add(new Token("/"));
					str = str.substring(1);
				} else if (str.charAt(0) == '+') {
					tokens.add(new Token("+"));
					str = str.substring(1);
				} else if (str.charAt(0) == '-') {
					tokens.add(new Token("-"));
					str = str.substring(1);
				} else {
					throw new Exception("Invalid string");
				}
			} else {
				if (str.length() >= 3 && str.substring(0, 3).equalsIgnoreCase("log")) {
					tokens.add(new Token("log"));
					str = str.substring(3);
				} else if (str.length() >= 4 && str.substring(0, 4).equalsIgnoreCase("sqrt")) {
					tokens.add(new Token("sqrt"));
					str = str.substring(4);
				} else if (str.charAt(0) == '(') {
					wantBinaryOp = true;
					int i1 = 0;
					int i2;
					int k;
					do {
						k = i1 + 1;
						i1 = str.indexOf(')', k);
						i2 = str.indexOf('(', k);
						System.out.println("i1 => " + i1 + "    ;;;; i2 => " + i2);
					} while (i1 > i2 && i2 > -1);
					if (i1 > -1) {
						tokens.add(new Token(str.substring(0, i1 + 1)));
					} else {
						throw new Exception("Unclosed \"(\"");
					}
					str = str.substring(i1 + 1);
				} else if (str.charAt(0) == '<') {
					wantBinaryOp = true;
					int i = str.indexOf('>');
					if (i > -1) {
						tokens.add(new Token(str.substring(0, i + 1)));
					} else {
						throw new Exception("Unclosed \"<\"");
					}
					str = str.substring(i + 1);
				} else if (str.matches("^\\d.*") || str.matches("^-.*") || str.matches("^\\+.*") || str.matches("^\\..*")) {
					wantBinaryOp = true;
					int i = 0;
					char charAt = str.charAt(i);
					while (i < str.length()
							&& ((charAt >= '0' && charAt <= '9')
							|| charAt == '.'
							|| charAt == 'E'
							|| charAt == 'e'
							|| charAt == '+'
							|| charAt == '-')) {
						i++;
						if (i < str.length()) {
							charAt = str.charAt(i);
						}
					}

					tokens.add(new Token(str.substring(0, i)));
					str = str.substring(i);
				} else {
					throw new Exception("Invalid string");
				}

			}
		}

		for (Token t : tokens) {
			System.out.println("Token: ->" + t + "<-");
		}
		System.out.println();

		return tokens;
	}

	private static int findTopToken(List<Token> tokens) {
		List<Token> clone = new ArrayList<Token>();

		int i;
		for (i = 0; i < tokens.size(); i++) {
			clone.add(tokens.get(i));
		}

		Collections.sort(clone);

		System.out.println("Find: " + clone.get(0));

		return tokens.indexOf(clone.get(0));
	}

	private enum TokenType {
		ADD, SUB, MUL, DIV, LOG, SQRT, ATTR, CST, GROUP, NA;
	}

	private static final class Token implements Comparable<Token> {

		String token;
		TokenType type;

		Token(String t) throws Exception {
			token = t.trim();
			setType();

			if (type == TokenType.NA) {
				throw new Exception("Invalid token: " + token);
			}

		}

		@Override
		public String toString() {
			return "Token is ->" + token + "<- (" + type + ")";
		}

		public TokenType getType() {
			return type;
		}

		public void setType() {
			if (token.equals("*")) {
				type = TokenType.MUL;
			} else if (token.equals("/")) {
				type = TokenType.DIV;
			} else if (token.equals("+")) {
				type = TokenType.ADD;
			} else if (token.equals("-")) {
				type = TokenType.SUB;
			} else if (token.equalsIgnoreCase("sqrt")) {
				type = TokenType.SQRT;
			} else if (token.equalsIgnoreCase("log")) {
				type = TokenType.LOG;
			} else if (token.matches("\\(.*\\)")) {
				type = TokenType.GROUP;
			} else if (token.matches("<.*>")) {
				type = TokenType.ATTR;
			} else if (isNumeric(token)) {
				type = TokenType.CST;
			} else {
				type = TokenType.NA;
			}
		}

		private boolean isNumeric(String str) {
			try {
				Double.parseDouble(str);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}

		@Override
		public int compareTo(Token t) {
			if (this.type.ordinal() < t.type.ordinal()) {
				return -1;
			} else if (this.type.ordinal() == t.type.ordinal()) {
				return 0;
			} else {
				return 1;
			}
		}
	}
}
*/
