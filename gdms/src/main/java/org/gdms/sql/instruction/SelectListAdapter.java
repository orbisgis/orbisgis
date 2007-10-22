/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
/*
 * Created on 12-oct-2004
 */
package org.gdms.sql.instruction;

import java.util.ArrayList;

import org.gdms.sql.parser.SQLEngineConstants;
import org.gdms.sql.parser.SimpleNode;
import org.gdms.sql.parser.Token;

/**
 * Adaptador
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class SelectListAdapter extends Adapter {
	private Expression[] fieldExpressions;

	private String[] fieldAliases;

	/**
	 * Obtiene las expresiones de los campos y los alias
	 */
	private void selectList() {
		Adapter[] hijos = getChilds();

		ArrayList<Expression> exprs = new ArrayList<Expression>();

		ArrayList<String> aliases = new ArrayList<String>();
		for (int i = 0; i < hijos.length; i++) {
			exprs.add((Expression) hijos[i]);
			SimpleNode node = hijos[i].getEntity();
			Token token = node.first_token;
			while (token != node.last_token) {
				token = token.next;
			}
			token = token.next;
			String alias = null;
			if (token.kind == SQLEngineConstants.AS) {
				token = token.next;
				alias = token.image;
			}
			aliases.add(alias);
		}

		fieldAliases = (String[]) aliases.toArray(new String[0]);
		fieldExpressions = (Expression[]) exprs.toArray(new Expression[0]);
	}

	/**
	 * Obtiene las expresiones de los campos
	 *
	 * @return
	 */
	public Expression[] getFieldsExpression() {
		if (fieldExpressions == null) {
			selectList();
		}

		return fieldExpressions;
	}

	/**
	 * Obtiene los alias de los campos
	 *
	 * @return
	 */
	public String[] getFieldsAlias() {
		if (fieldAliases == null) {
			selectList();
		}

		return fieldAliases;
	}
}
