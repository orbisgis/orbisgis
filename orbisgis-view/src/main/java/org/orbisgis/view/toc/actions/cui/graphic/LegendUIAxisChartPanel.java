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
package org.orbisgis.view.toc.actions.cui.graphic;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.graphic.AxisChart;
import org.orbisgis.core.renderer.se.graphic.Category;
import org.orbisgis.core.renderer.se.graphic.CategoryListener;
import org.orbisgis.core.renderer.se.graphic.Graphic;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.graphic.PieChart;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.view.toc.actions.cui.LegendUIAbstractPanel;
import org.orbisgis.view.toc.actions.cui.LegendUIComponent;
import org.orbisgis.view.toc.actions.cui.LegendUIController;
import org.orbisgis.view.toc.actions.cui.components.ComboBoxInput;
import org.orbisgis.view.toc.actions.cui.components.TextInput;
import org.orbisgis.view.toc.actions.cui.components.UomInput;
import org.orbisgis.view.toc.actions.cui.fill.LegendUIMetaFillPanel;
import org.orbisgis.view.toc.actions.cui.parameter.real.LegendUIMetaRealPanel;
import org.orbisgis.view.toc.actions.cui.stroke.LegendUIMetaStrokePanel;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 *
 * @author Maxence Laurent
 */
public class LegendUIAxisChartPanel extends LegendUIComponent implements LegendUIGraphicComponent, CategoryListener {

    private AxisChart chart;
    private UomInput uom;
    private ComboBoxInput chartType;
    private LegendUIMetaRealPanel axisLength;
    private LegendUIMetaRealPanel measure;
    private LegendUIMetaRealPanel cWidth;
    private LegendUIMetaRealPanel cGap;
    private LegendUIMetaRealPanel normalize;
    private LegendUIMetaStrokePanel lineStroke;
    private LegendUIMetaFillPanel areaFill;
    private LegendUITransformPanel transform;

    private ArrayList<NameInput> names;
    private ArrayList<ValueInput> values;
    private ArrayList<GraphicInput> graphics;
    private ArrayList<FillInput> fills;
    private ArrayList<StrokeInput> strokes;

    private String types[] = {"Orthogonal", "Polar", "Stacked"};

    public LegendUIAxisChartPanel(LegendUIController controller, LegendUIComponent parent, AxisChart c) {
        super("Axis Chart", controller, parent, 0, false);
        this.chart = c;

        uom = new UomInput(chart);

        names = new ArrayList<NameInput>();
        graphics = new ArrayList<GraphicInput>();
        fills = new ArrayList<FillInput>();
        strokes = new ArrayList<StrokeInput>();
        values = new ArrayList<ValueInput>();

        int currentType;
        switch (chart.getSubtype()) {
            case POLAR:
                currentType = 1;
                break;
            case STACKED:
                currentType = 2;
                break;
            case ORTHO:
            default:
                currentType = 0;
                break;

        }

        transform = new LegendUITransformPanel(controller, this, c);

        chartType = new ComboBoxInput(types, currentType) {

            @Override
            protected void valueChanged(int i) {
                if (i == 0) {
                    chart.setSubtype(AxisChart.AxisChartSubType.ORTHO);
                } else if (i == 1) {
                    chart.setSubtype(AxisChart.AxisChartSubType.POLAR);
                } else {
                    chart.setSubtype(AxisChart.AxisChartSubType.STACKED);
                }
            }
        };

        axisLength = new LegendUIMetaRealPanel("Axis Length", controller, this, chart.getAxisScale().getAxisLength(), false) {

            @Override
            public void realChanged(RealParameter newReal) {
                chart.getAxisScale().setAxisLength(newReal);
            }
        };
        axisLength.init();


        measure = new LegendUIMetaRealPanel("Corresponding Measure", controller, this, chart.getAxisScale().getMeasureValue(), false) {

            @Override
            public void realChanged(RealParameter newReal) {
                chart.getAxisScale().setMeasure(newReal);
            }
        };
        measure.init();


        cGap = new LegendUIMetaRealPanel("Gap", controller, this, chart.getCategoryGap(), true) {

            @Override
            public void realChanged(RealParameter newReal) {
                chart.setCategoryGap(newReal);
            }
        };
        cGap.init();

        cWidth = new LegendUIMetaRealPanel("Width", controller, this, chart.getCategoryWidth(), true) {

            @Override
            public void realChanged(RealParameter newReal) {
                chart.setCategoryWidth(newReal);
            }
        };
        cWidth.init();

        normalize = new LegendUIMetaRealPanel("Normalize", controller, this, chart.getNormalizeTo(), true) {

            @Override
            public void realChanged(RealParameter newReal) {
                chart.setNormalizeTo(newReal);
            }
        };
        normalize.init();


        lineStroke = new LegendUIMetaStrokePanel(controller, this, chart, true);
        lineStroke.init();

        areaFill = new LegendUIMetaFillPanel(controller, this, chart, true);
        areaFill.init();

        for (int i = 0; i < chart.getNumCategories(); i++) {
            try {
                Category cat = chart.getCategory(i);
                addCategory(cat);
            } catch (ParameterException ex) {
            }
        }

        chart.registerListerner(this);
    }

    private void addCategory(Category cat) {
        ValueInput valueInput = new ValueInput(cat);
        FillInput fillInput = new FillInput(cat);
        StrokeInput strokeInput = new StrokeInput(cat);
        GraphicInput graphicInput = new GraphicInput(cat);

        names.add(new NameInput(cat));
        values.add(valueInput);
        fills.add(fillInput);
        strokes.add(strokeInput);
        graphics.add(graphicInput);
    }

    @Override
    public Icon getIcon() {
        return OrbisGISIcon.getIcon("palette");
    }

    @Override
    protected void mountComponent() {
        LegendUIAbstractPanel content1 = new LegendUIAbstractPanel(controller);
        LegendUIAbstractPanel content1a = new LegendUIAbstractPanel(controller);
        LegendUIAbstractPanel content1b = new LegendUIAbstractPanel(controller);
        LegendUIAbstractPanel content1c = new LegendUIAbstractPanel(controller);

        LegendUIAbstractPanel content2 = new LegendUIAbstractPanel(controller);
        LegendUIAbstractPanel content3 = new LegendUIAbstractPanel(controller);
        LegendUIAbstractPanel content3a = new LegendUIAbstractPanel(controller);
        LegendUIAbstractPanel content3b = new LegendUIAbstractPanel(controller);

        LegendUIAbstractPanel catContainer = new LegendUIAbstractPanel(controller);


        LegendUIAbstractPanel categories = new LegendUIAbstractPanel(controller);

        GridLayout grid = new GridLayout(0, 6);
        categories.setLayout(grid);


        categories.add(new JLabel("Name"));
        categories.add(new JLabel("Value"));
        categories.add(new JLabel("Fill"));
        categories.add(new JLabel("Stroke"));
        categories.add(new JLabel("Graphic"));
        categories.add(new JLabel(""));


        for (int i = 0; i < chart.getNumCategories(); i++) {
            categories.add(names.get(i));
            categories.add(values.get(i));
            categories.add(fills.get(i));
            categories.add(strokes.get(i));
            categories.add(graphics.get(i));

            LegendUIAbstractPanel tools = new LegendUIAbstractPanel(controller);

            JButton btnRm = new JButton(OrbisGISIcon.getIcon("remove"));
            btnRm.setMargin(new Insets(0, 0, 0, 0));
            btnRm.addActionListener(new ActionRemove(i));

            JButton btnUp = new JButton(OrbisGISIcon.getIcon("go-up"));
            btnUp.setMargin(new Insets(0, 0, 0, 0));
            btnUp.addActionListener(new ActionMoveUp(i));

            JButton btnDown = new JButton(OrbisGISIcon.getIcon("go-down"));
            btnDown.setMargin(new Insets(0, 0, 0, 0));
            btnDown.addActionListener(new ActionMoveDown(i));

            tools.add(btnUp, BorderLayout.WEST);
            tools.add(btnDown, BorderLayout.CENTER);
            tools.add(btnRm, BorderLayout.EAST);

            categories.add(tools);
        }

        catContainer.add(categories, BorderLayout.NORTH);

        JButton btnAdd = new JButton(OrbisGISIcon.getIcon("add"));


        btnAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Category newCat = new Category();

                newCat.setName("Category " + (chart.getNumCategories() + 1));
                newCat.setMeasure(new RealAttribute());
                newCat.setFill(new SolidFill());
                newCat.setStroke(new PenStroke());

                chart.addCategory(newCat);
                addCategory(newCat);
                controller.structureChanged(LegendUIAxisChartPanel.this);
            }
        });
        catContainer.add(btnAdd, BorderLayout.SOUTH);

        content1a.add(chartType, BorderLayout.WEST);
        content1a.add(normalize, BorderLayout.CENTER);
        content1a.add(uom, BorderLayout.EAST);

        content1b.add(cWidth, BorderLayout.WEST);
        content1b.add(cGap, BorderLayout.EAST);

        content1c.add(axisLength, BorderLayout.WEST);
        content1c.add(measure, BorderLayout.EAST);


        content1.add(content1a, BorderLayout.NORTH);
        content1.add(content1b, BorderLayout.CENTER);
        content1.add(content1c, BorderLayout.SOUTH);

        content2.add(transform);

        content3a.add(lineStroke, BorderLayout.WEST);
        content3a.add(areaFill, BorderLayout.EAST);
        content3b.add(catContainer, BorderLayout.CENTER);

        content3.add(content3a, BorderLayout.NORTH);
        content3.add(content3b, BorderLayout.SOUTH);

        editor.add(content1, BorderLayout.NORTH);
        editor.add(content2, BorderLayout.CENTER);
        editor.add(content3, BorderLayout.SOUTH);
    }

    @Override
    protected void turnOff() {
        throw new UnsupportedOperationException("Unreachable code.");
    }

    @Override
    protected void turnOn() {
        throw new UnsupportedOperationException("Unreachable code.");
    }

    @Override
    public Class getEditedClass() {
        return PieChart.class;
    }

    @Override
    public Graphic getGraphic() {
        return this.chart;
    }

    @Override
    public void categoryMoveUp(int i) {
        ValueInput val = values.remove(i);
        FillInput fill = fills.remove(i);
        StrokeInput stroke = strokes.remove(i);
        GraphicInput gap = graphics.remove(i);
        NameInput name = names.remove(i);

        values.add(i - 1, val);
        fills.add(i - 1, fill);
        strokes.add(i - 1, stroke);
        graphics.add(i - 1, gap);
        names.add(i - 1, name);

        controller.structureChanged(this);
    }

    @Override
    public void categoryMoveDown(int i) {
        ValueInput val = values.remove(i);
        FillInput fill = fills.remove(i);
        StrokeInput stroke = strokes.remove(i);
        GraphicInput gap = graphics.remove(i);
        NameInput name = names.remove(i);

        values.add(i + 1, val);
        fills.add(i + 1, fill);
        strokes.add(i + 1, stroke);
        graphics.add(i + 1, gap);
        names.add(i + 1, name);

        controller.structureChanged(this);
    }

    @Override
    public void categoryRemoved(int i) {
        ValueInput val = values.remove(i);
        FillInput fill = fills.remove(i);
        StrokeInput stroke = strokes.remove(i);
        GraphicInput gap = graphics.remove(i);
        NameInput name = names.remove(i);

        controller.structureChanged(this);
    }

    private class NameInput extends TextInput {

        Category c;

        public NameInput(Category c) {
            super(null, c.getName(), 15, false);
            this.c = c;
        }

        @Override
        protected void valueChanged(String s) {
            this.c.setName(s);
        }
    }

    private final class ValueInput extends LegendUIComponent {

        Category c;
        LegendUIMetaRealPanel real;

        public ValueInput(Category c) {
            super("", LegendUIAxisChartPanel.this.controller, LegendUIAxisChartPanel.this, 0, false);
            this.c = c;
            real = new LegendUIMetaRealPanel("", ValueInput.this.controller, ValueInput.this, c.getMeasure(), false) {

                @Override
                public void realChanged(RealParameter newReal) {
                    ValueInput.this.c.setMeasure(newReal);
                }
            };
            real.init();
        }

        @Override
        public Icon getIcon() {
            return OrbisGISIcon.getIcon("palette");
        }

        @Override
        protected void mountComponent() {
            editor.add(real);
        }

        @Override
        protected void turnOff() {
        }

        @Override
        protected void turnOn() {
        }

        @Override
        public Class getEditedClass() {
            return real.getEditedClass();
        }
    }

    private final class GraphicInput extends LegendUIComponent {

        Category c;
        LegendUICompositeGraphicPanel gr;

        public GraphicInput(Category c) {
            super("", LegendUIAxisChartPanel.this.controller, LegendUIAxisChartPanel.this, 0, false);
            this.c = c;
            if (c.getGraphicCollection() == null) {
                c.setGraphicCollection(new GraphicCollection());
            }
            gr = new LegendUICompositeGraphicPanel(controller, this, c.getGraphicCollection());
            gr.extractFromParent();
        }

        @Override
        public Icon getIcon() {
            return OrbisGISIcon.getIcon("palette");
        }

        @Override
        protected void mountComponent() {
            editor.add(gr);
        }

        @Override
        protected void turnOff() {
        }

        @Override
        protected void turnOn() {
        }

        @Override
        public Class getEditedClass() {
            return gr.getEditedClass();
        }
    }

    private final class StrokeInput extends LegendUIComponent {

        Category c;
        LegendUIMetaStrokePanel stroke;

        public StrokeInput(Category c) {
            super("", LegendUIAxisChartPanel.this.controller, LegendUIAxisChartPanel.this, 0, false);

            this.c = c;

            stroke = new LegendUIMetaStrokePanel(this.controller, this, c, false);
            stroke.extractFromParent();
            stroke.init();
        }

        @Override
        public Icon getIcon() {
            return OrbisGISIcon.getIcon("palette");
        }

        @Override
        protected void mountComponent() {
            //fill.extractFromParent();
            editor.add(stroke);
        }

        @Override
        protected void turnOff() {
        }

        @Override
        protected void turnOn() {
        }

        @Override
        public Class getEditedClass() {
            return stroke.getEditedClass();
        }
    }

    private final class FillInput extends LegendUIComponent {

        Category c;
        LegendUIMetaFillPanel fill;

        public FillInput(Category c) {
            super("", LegendUIAxisChartPanel.this.controller, LegendUIAxisChartPanel.this, 0, false);

            this.c = c;

            fill = new LegendUIMetaFillPanel(this.controller, this, c, false);
            fill.extractFromParent();
            fill.init();
        }

        @Override
        public Icon getIcon() {
            return OrbisGISIcon.getIcon("palette");
        }

        @Override
        protected void mountComponent() {
            //fill.extractFromParent();
            editor.add(fill);
        }

        @Override
        protected void turnOff() {
        }

        @Override
        protected void turnOn() {
        }

        @Override
        public Class getEditedClass() {
            return fill.getEditedClass();
        }
    }

    private class ActionMoveUp implements ActionListener {

        private int i;

        public ActionMoveUp(int i) {
            this.i = i;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LegendUIAxisChartPanel.this.chart.moveCategoryUp(i);
        }
    }

    private class ActionMoveDown implements ActionListener {

        private int i;

        public ActionMoveDown(int i) {
            this.i = i;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LegendUIAxisChartPanel.this.chart.moveCategoryDown(i);
        }
    }

    private class ActionRemove implements ActionListener {

        private int i;

        public ActionRemove(int i) {
            this.i = i;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LegendUIAxisChartPanel.this.chart.removeCategory(i);
        }
    }
}
