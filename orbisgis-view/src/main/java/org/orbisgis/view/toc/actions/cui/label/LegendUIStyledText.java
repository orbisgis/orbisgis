/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.toc.actions.cui.label;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import org.orbisgis.core.renderer.se.common.Halo;
import org.orbisgis.core.renderer.se.label.StyledText;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import org.orbisgis.view.toc.actions.cui.LegendUIAbstractPanel;
import org.orbisgis.view.toc.actions.cui.LegendUIComponent;
import org.orbisgis.view.toc.actions.cui.LegendUIController;
import org.orbisgis.view.toc.actions.cui.LegendUIHaloPanel;
import org.orbisgis.view.toc.actions.cui.components.UomInput;
import org.orbisgis.view.toc.actions.cui.fill.LegendUIMetaFillPanel;
import org.orbisgis.view.toc.actions.cui.parameter.real.LegendUIMetaRealPanel;
import org.orbisgis.view.toc.actions.cui.parameter.string.LegendUIMetaStringPanel;
import org.orbisgis.view.toc.actions.cui.stroke.LegendUIMetaStrokePanel;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 *
 * @author Maxence Laurent
 */
public abstract class LegendUIStyledText extends LegendUIComponent {

    private StyledText sText;
    private LegendUIMetaStringPanel text;
    private UomInput uom;
    private LegendUIMetaStringPanel fontFamily;
    private LegendUIMetaRealPanel fontSize;
    private LegendUIMetaStringPanel fontWeight;
    private LegendUIMetaStringPanel fontStyle;
    private LegendUIMetaStrokePanel stroke;
    private LegendUIMetaFillPanel fill;
    private LegendUIHaloPanel halo;

    public LegendUIStyledText(LegendUIController controller, LegendUIComponent parent, StyledText styledText, boolean isNullable) {
        super("Text", controller, parent, 0, isNullable);


        this.setBorder(BorderFactory.createTitledBorder("Text & Font"));

        this.sText = styledText;

        this.text = new LegendUIMetaStringPanel("Text", controller, this,sText.getText(), false) {

            @Override
            public void stringChanged(StringParameter newString) {
                sText.setText(newString);
            }
        };
        text.init();

        uom = new UomInput(sText);

        fontFamily = new LegendUIMetaStringPanel("Family", controller, this, sText.getFontFamily(), false) {

            @Override
            public void stringChanged(StringParameter newString) {
                sText.setFontFamily(newString);
            }
        };
        fontFamily.init();

        fontSize = new LegendUIMetaRealPanel("Size", controller, this, sText.getFontSize(), false) {

            @Override
            public void realChanged(RealParameter newReal) {
                sText.setFontSize(newReal);
            }
        };
        fontSize.init();

        fontWeight = new LegendUIMetaStringPanel("Weight", controller, this, sText.getFontWeight(), false) {

            @Override
            public void stringChanged(StringParameter newString) {
                sText.setFontWeight(newString);
            }
        };
        fontWeight.init();

        fontStyle = new LegendUIMetaStringPanel("Style", controller, this, sText.getFontStyle(), false) {

            @Override
            public void stringChanged(StringParameter newString) {
                sText.setFontStyle(newString);
            }
        };
        fontStyle.init();

        stroke = new LegendUIMetaStrokePanel(controller, this, styledText, true);
        stroke.init();

        fill = new LegendUIMetaFillPanel(controller, this, styledText, true);
        fill.init();

        halo = new LegendUIHaloPanel(controller, this, sText.getHalo()) {

            @Override
            protected void haloChanged(Halo halo) {
                sText.setHalo(halo);
				controller.structureChanged(this);
            }
        };
    }

    @Override
    public Icon getIcon() {
        return OrbisGISIcon.getIcon("palette");
    }

    @Override
    protected void mountComponent() {
        LegendUIAbstractPanel content1 = new LegendUIAbstractPanel(controller);

        LegendUIAbstractPanel content2 = new LegendUIAbstractPanel(controller);
        LegendUIAbstractPanel content2a = new LegendUIAbstractPanel(controller);
        LegendUIAbstractPanel content2b = new LegendUIAbstractPanel(controller);

        LegendUIAbstractPanel content3 = new LegendUIAbstractPanel(controller);


        content1.add(text, BorderLayout.WEST);
        content1.add(uom, BorderLayout.EAST);

        content2a.add(fontFamily, BorderLayout.WEST);
        content2a.add(fontSize, BorderLayout.EAST);

        content2b.add(fontWeight, BorderLayout.WEST);
        content2b.add(fontStyle, BorderLayout.EAST);

        content2.add(content2a, BorderLayout.NORTH);
        content2.add(content2b, BorderLayout.SOUTH);

        content3.add(stroke, BorderLayout.WEST);
        content3.add(halo, BorderLayout.CENTER);
        content3.add(fill, BorderLayout.EAST);

        editor.add(content1, BorderLayout.NORTH);
        editor.add(content2, BorderLayout.CENTER);
        editor.add(content3, BorderLayout.SOUTH);
    }

    @Override
    public Class getEditedClass() {
        return StyledText.class;
    }

    @Override
    protected void turnOff() {
        textChanged(null);
    }

    @Override
    protected void turnOn() {
        textChanged(sText);
    }

    public abstract void textChanged(StyledText newText);
}
