package org.orbisgis.geoview.sld;

import java.io.IOException;
import java.util.List;

import org.orbisgis.geoview.renderer.sdsOrGrRendering.LineSymbolizerRenderer;
import org.orbisgis.geoview.renderer.sdsOrGrRendering.PointSymbolizerRenderer;
import org.orbisgis.geoview.renderer.sdsOrGrRendering.PolygonSymbolizerRenderer;
import org.orbisgis.geoview.renderer.style.sld.FeatureTypeStyle;
import org.orbisgis.geoview.renderer.style.sld.LineSymbolizer;
import org.orbisgis.geoview.renderer.style.sld.PointSymbolizer;
import org.orbisgis.geoview.renderer.style.sld.PolygonSymbolizer;
import org.orbisgis.geoview.renderer.style.sld.Rule;
import org.orbisgis.geoview.renderer.style.sld.SLDParser;
import org.orbisgis.geoview.renderer.style.sld.Symbolizer;

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


			System.out.println("Return min the filter :" + rules.get(0).getFilter(0).toString());


			/*for (int j = 0; j < ruleCount; j++) {

				String type = rules.get(j).getSymbolizer().getType();
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
			}*/

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
