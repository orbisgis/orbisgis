/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */

package org.orbisgis.legend.analyzer

import org.orbisgis.core.renderer.se.fill.Fill
import org.orbisgis.core.renderer.se.fill.SolidFill
import org.orbisgis.core.renderer.se.parameter.Categorize
import org.orbisgis.core.renderer.se.parameter.Recode
import org.orbisgis.core.renderer.se.parameter.SeParameter
import org.orbisgis.core.renderer.se.parameter.color.Categorize2Color
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter
import org.orbisgis.core.renderer.se.parameter.color.Recode2Color
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral
import org.orbisgis.core.renderer.se.parameter.real.RealParameter
import org.orbisgis.core.renderer.se.parameter.real.Recode2Real
import org.orbisgis.legend.LegendStructure
import org.orbisgis.legend.analyzer.function.AbstractLiteralValidator
import org.orbisgis.legend.structure.categorize.Categorize2ColorLegend
import org.orbisgis.legend.structure.fill.CategorizedSolidFillLegend
import org.orbisgis.legend.structure.fill.RecodedSolidFillLegend
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFillLegend
import org.orbisgis.legend.structure.fill.constant.NullSolidFillLegend
import org.orbisgis.legend.structure.literal.ColorLiteralLegend
import org.orbisgis.legend.structure.literal.RealLiteralLegend
import org.orbisgis.legend.structure.recode.RecodedColor
import org.orbisgis.legend.structure.recode.RecodedReal

class FillAnalyzer extends AbstractLiteralValidator {

  var fill : Fill = new SolidFill

  def this(f : Fill) = {
    this
    fill = f
    setLegend(analyzeFill(fill))
  }

  private def analyzeFill(f : Fill) : LegendStructure = f match {
    case sf : SolidFill =>
      val col : ColorParameter = sf.getColor
      val opac : RealParameter = sf.getOpacity
      //We start by making some validation : No mixed analysis in Recode or in Categorize !
      if(!validateParams(col, opac)) 
        throw new UnsupportedOperationException("There are mixed analysis in this symbolizer")

      (col,opac) match {
        case (cc : ColorLiteral, co : RealLiteral) => buildConstant(sf,cc,co)
        case (rc : Recode2Color, ro : Recode2Real) => 
            new RecodedSolidFillLegend(sf,new RecodedColor(rc),new RecodedReal(ro))
        case (rc : ColorLiteral, ro : Recode2Real) =>
            new RecodedSolidFillLegend(sf,new RecodedColor(rc),new RecodedReal(ro))
        case (rc : Recode2Color, ro : RealLiteral) =>
            new RecodedSolidFillLegend(sf,new RecodedColor(rc),new RecodedReal(ro))
        case (cc : Categorize2Color, co : RealLiteral) =>
            new CategorizedSolidFillLegend(sf,new Categorize2ColorLegend(cc),new RealLiteralLegend(co))
        case _ => throw new UnsupportedOperationException("Not supported yet")
      }
    case null => new NullSolidFillLegend
    case _ => throw new UnsupportedOperationException("Such fill are not supported yet")
  }

  private def buildConstant(sf : SolidFill, cc : ColorLiteral, co : RealLiteral) : LegendStructure = {
    val cll = new ColorLiteralLegend(cc)
    val rll = new RealLiteralLegend(co)
    new ConstantSolidFillLegend(sf,cll,rll)
  }

  private  def validateParams(c : SeParameter, d : SeParameter) : Boolean = validateParam(c) && validateParam(d)

  /**
   * @param c The parameter to check
   * @return true if and only if :
   * <ul><li>s is a Recode without inner other analysis OR</li>
   * <li>s is a Categorize without inner other analysis OR</li>
   * <li> s is neither a Recode not a Categorize</li></ul>
   */
  private  def validateParam(c : SeParameter) : Boolean = c match {
    case a : Recode[_,_] => validateRecode(a)
    case b : Categorize[_,_] => validateCategorize(b)
    case _ => true
  }

}
