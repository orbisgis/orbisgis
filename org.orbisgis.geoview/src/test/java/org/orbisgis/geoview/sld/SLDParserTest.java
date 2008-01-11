/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 licence. It is produced  by the geomatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
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
package org.orbisgis.geoview.sld;

import java.io.IOException;
import java.util.List;

import org.gdms.sql.evaluator.Expression;
import org.orbisgis.geoview.renderer.style.sld.FeatureTypeStyle;
import org.orbisgis.geoview.renderer.style.sld.LineSymbolizer;
import org.orbisgis.geoview.renderer.style.sld.PointSymbolizer;
import org.orbisgis.geoview.renderer.style.sld.PolygonSymbolizer;
import org.orbisgis.geoview.renderer.style.sld.Rule;
import org.orbisgis.geoview.renderer.style.sld.SLDParser;

import com.ximpleware.EOFException;
import com.ximpleware.EncodingException;
import com.ximpleware.EntityException;
import com.ximpleware.NavException;
import com.ximpleware.ParseException;
import com.ximpleware.xpath.XPathEvalException;
import com.ximpleware.xpath.XPathParseException;

public class SLDParserTest {


	public static void main(String[] args) {

		//String path = "..//..//datas2tests//sld//densityBySymbols.sld";

		String path = "..//..//datas2tests//sld//density.sld";

		//String path = "..//..//datas2tests//sld//greenline.sld";

		//String path = "..//..//datas2tests//sld//redRoads.sld";


		try {

		SLDParser parser = new SLDParser(path);


		parser.read();


		List<FeatureTypeStyle> featureTypeStyles = parser.getFeatureTypeStyles();


		for (int i = 0; i < featureTypeStyles.size(); i++) {

			int ruleCount = featureTypeStyles.get(i).getRuleCount();

			System.out.println("Number of rules : " + ruleCount);

			List<Rule> rules = featureTypeStyles.get(i).getRules();


			

			for (int j = 0; j < ruleCount; j++) {

				String type = rules.get(j).getSymbolizer().getType();
				System.out.println("Return the filter :" + rules.get(j).getFilter(0).toString());
				Expression n =rules.get(j).getFilter(0).getExpression();
				
				System.out.println("Symbolizer type : " + type);
			

			if (type.equalsIgnoreCase("sld:PointSymbolizer")){

					PointSymbolizer pointSymbolizer = (PointSymbolizer) rules.get(j).getSymbolizer();

					System.out.println(pointSymbolizer.toString());
				}
				else if (type.equalsIgnoreCase("sld:LineSymbolizer")) {

					LineSymbolizer lineSymbolizer = (LineSymbolizer) rules.get(j).getSymbolizer();

					System.out.println(lineSymbolizer.toString());
					
				}

				if (type.equalsIgnoreCase("sld:PolygonSymbolizer")){

					PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) rules.get(j).getSymbolizer();


					System.out.println(polygonSymbolizer.toString());
				}
			}

		}




		} catch (EncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EOFException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathEvalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NavException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
