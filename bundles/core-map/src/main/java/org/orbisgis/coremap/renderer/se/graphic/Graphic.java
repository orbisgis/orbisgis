/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.coremap.renderer.se.graphic;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.ExternalGraphicType;
import net.opengis.se._2_0.core.GraphicType;
import net.opengis.se._2_0.core.MarkGraphicType;
import net.opengis.se._2_0.core.PointTextGraphicType;
import net.opengis.se._2_0.thematic.AxisChartType;
import net.opengis.se._2_0.thematic.PieChartType;

import org.orbisgis.coremap.map.MapTransform;
import org.orbisgis.coremap.renderer.se.AbstractSymbolizerNode;
import org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;

/**
 * Generic class to represent graphic symbols as defined in SE.
 * @todo create subclasses: AlternativeGraphic, GraphicReference
 * @author Maxence Laurent
 */
public abstract class Graphic extends AbstractSymbolizerNode {

    /**
     * This static method is a convenience to create a new {@code Graphic}
     * specialized type from a {@code JAXBElement} instance, that embeds a
     * {@code GraphicType}.
     * @param gr
     * @return
     * @throws org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle
     */
    public static Graphic createFromJAXBElement(JAXBElement<? extends GraphicType> gr) throws InvalidStyle {
        try {
            if (gr.getDeclaredType() == ExternalGraphicType.class) {
                return new ExternalGraphic((JAXBElement<ExternalGraphicType>) gr);
            }

            if (gr.getDeclaredType() == MarkGraphicType.class) {
                return new MarkGraphic((JAXBElement<MarkGraphicType>) gr);
            }

            if (gr.getDeclaredType() == PointTextGraphicType.class) {
                return new PointTextGraphic((JAXBElement<PointTextGraphicType>) gr);
            }

            if (gr.getDeclaredType() == PieChartType.class) {
                return new PieChart((JAXBElement<PieChartType>) gr);
            }

            if (gr.getDeclaredType() == AxisChartType.class) {
                return new AxisChart((JAXBElement<AxisChartType>) gr);
            }

        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            return null;
        } catch(URISyntaxException e){
                throw new  InvalidStyle("There's a malformed URI in your style", e);
        }

        return null;
    }

	@Override
	public String toString(){
		return this.getClass().getSimpleName();
	}

    /**
     * Return graphic bounds. Bounds center point shall match CRS origin !
     * @param map
     * @param selected
     * @param mt
     * @param at
     * @return
     * @throws ParameterException
     * @throws IOException
     */
    public abstract Rectangle2D getBounds(Map<String,Object> map, MapTransform mt)
            throws ParameterException, IOException;

    /**
     * Draw this graphic using {@code g2}.
     * @param g2
     * @param map
     * @param selected
     * @param mt
     * @param at
     * @throws ParameterException
     * @throws IOException
     */
    public abstract void draw(Graphics2D g2, Map<String,Object> map,
            boolean selected, MapTransform mt, AffineTransform at) throws ParameterException, IOException;

    /**
     * Get a JAXB representation of this {@code Graphic}
     * @return
     * A {@code JAXBElement} that contains a {@code GraphicType} specialization.
     */
    public abstract JAXBElement<? extends GraphicType> getJAXBElement();

    /**
     * Update the inner graphic.
     */
    public abstract void updateGraphic();
}
