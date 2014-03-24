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
import org.orbisgis.core.renderer.se.graphic.Graphic;
import org.orbisgis.core.renderer.se.graphic.PieChart;
import org.orbisgis.core.renderer.se.graphic.Slice;
import org.orbisgis.core.renderer.se.graphic.SliceListener;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
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
public class LegendUIPieChartPanel extends LegendUIComponent implements LegendUIGraphicComponent, SliceListener {

    private PieChart pie;

    private UomInput uom;
    private ComboBoxInput pieType;
    private LegendUIMetaRealPanel radius;
    private LegendUITransformPanel transform;

    private LegendUIMetaRealPanel holeRadius;
    private LegendUIMetaStrokePanel stroke;

    private ArrayList<NameInput> names;
    private ArrayList<ValueInput> values;
    private ArrayList<GapInput> gaps;
    private ArrayList<FillInput> fills;


    private String types[] = {"360°", "180°"};

	public LegendUIPieChartPanel(LegendUIController controller, LegendUIComponent parent, PieChart p) {
		super("Pie Chart", controller, parent, 0, false);
        this.pie = p;



        uom = new UomInput(pie);

        names = new ArrayList<NameInput>();
        gaps = new ArrayList<GapInput>();
        fills = new ArrayList<FillInput>();
        values = new ArrayList<ValueInput>();

        transform = new LegendUITransformPanel(controller, this, pie);

        pieType = new ComboBoxInput(types, (pie.getType() == PieChart.PieChartSubType.WHOLE ? 0 : 1)) {

            @Override
            protected void valueChanged(int i) {
                if (i==0){
                    pie.setType(PieChart.PieChartSubType.WHOLE);
                } else {
                    pie.setType(PieChart.PieChartSubType.HALF);
                }
            }
        };

        radius = new LegendUIMetaRealPanel("Radius", controller, this, pie.getRadius(), false) {

            @Override
            public void realChanged(RealParameter newReal) {
                pie.setRadius(newReal);
            }
        };
        radius.init();

        holeRadius = new LegendUIMetaRealPanel("Hole", controller, this, pie.getHoleRadius(), true) {

            @Override
            public void realChanged(RealParameter newReal) {
                pie.setHoleRadius(newReal);
            }
        };
        holeRadius.init();

        stroke = new LegendUIMetaStrokePanel(controller, this, pie, true);
        stroke.init();

        for (int i = 0;i < pie.getNumSlices();i++){
            Slice s = pie.getSlice(i);
            addSlice(s);
        }

        pie.registerListerner(this);
    }

    private void addSlice(Slice s){
            ValueInput valueInput = new ValueInput(s);
            FillInput fillInput = new FillInput(s);
            GapInput gapInput = new GapInput(s);

            names.add(new NameInput(s));
            values.add(valueInput);
            fills.add(fillInput);
            gaps.add(gapInput);
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
        LegendUIAbstractPanel content2 = new LegendUIAbstractPanel(controller);
        LegendUIAbstractPanel content3 = new LegendUIAbstractPanel(controller);

        LegendUIAbstractPanel sliceContainer = new LegendUIAbstractPanel(controller);


        LegendUIAbstractPanel slices = new LegendUIAbstractPanel(controller);
        slices.setLayout(new GridLayout(0, 5));

        slices.add(new JLabel("Name"));
        slices.add(new JLabel("Value"));
        slices.add(new JLabel("Fill"));
        slices.add(new JLabel("Gap"));
        slices.add(new JLabel(""));

        for (int i = 0;i < pie.getNumSlices();i++){
            slices.add(names.get(i));
            slices.add(values.get(i));
            slices.add(fills.get(i));
            slices.add(gaps.get(i));

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

            slices.add(tools);
        }

        sliceContainer.add(slices, BorderLayout.NORTH);

		JButton btnAdd = new JButton(OrbisGISIcon.getIcon("add"));

        sliceContainer.add(btnAdd, BorderLayout.SOUTH);

		btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Slice newSlice = new Slice();

                newSlice.setName("Slice " + (pie.getNumSlices() + 1));
                newSlice.setValue(new RealAttribute());
                newSlice.setFill(new SolidFill());
                newSlice.setGap(new RealLiteral(0));

                pie.addSlice(newSlice);
                addSlice(newSlice);
                controller.structureChanged(LegendUIPieChartPanel.this);
            }
        });


        content1a.add(pieType, BorderLayout.WEST);
        content1a.add(uom, BorderLayout.EAST);

        content1b.add(radius, BorderLayout.WEST);
        content1b.add(holeRadius, BorderLayout.EAST);
        content1.add(content1a, BorderLayout.NORTH);
        content1.add(content1b, BorderLayout.SOUTH);

        content2.add(transform, BorderLayout.CENTER);
        content3.add(sliceContainer, BorderLayout.WEST);
        content3.add(stroke, BorderLayout.EAST);

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
        return this.pie;
    }

    @Override
    public void sliceMoveUp(int i) {
        ValueInput val = values.remove(i);
        FillInput fill = fills.remove(i);
        GapInput gap = gaps.remove(i);
        NameInput name = names.remove(i);

        values.add(i-1, val);
        fills.add(i-1, fill);
        gaps.add(i-1, gap);
        names.add(i-1, name);

        controller.structureChanged(this);
    }

    @Override
    public void sliceMoveDown(int i) {
        ValueInput val = values.remove(i);
        FillInput fill = fills.remove(i);
        GapInput gap = gaps.remove(i);
        NameInput name = names.remove(i);

        values.add(i+1, val);
        fills.add(i+1, fill);
        gaps.add(i+1, gap);
        names.add(i+1, name);

        controller.structureChanged(this);
    }

    @Override
    public void sliceRemoved(int i) {
        ValueInput val = values.remove(i);
        FillInput fill = fills.remove(i);
        GapInput gap = gaps.remove(i);
        NameInput name = names.remove(i);

        controller.structureChanged(this);
    }

    private class NameInput extends TextInput {

        Slice s;

        public NameInput(Slice s){
            super(null, s.getName(), 10, false);
            this.s = s;
        }

        @Override
        protected void valueChanged(String s) {
            this.s.setName(s);
        }
    }

    private final class ValueInput extends LegendUIComponent {

        Slice s;
        LegendUIMetaRealPanel real;

        public ValueInput(Slice s){
            super("", LegendUIPieChartPanel.this.controller, LegendUIPieChartPanel.this , 0, false);
            this.s = s;
            real = new LegendUIMetaRealPanel("", ValueInput.this.controller, ValueInput.this, s.getValue(), false) {

                @Override
                public void realChanged(RealParameter newReal) {
                    ValueInput.this.s.setValue(newReal);
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

    private final class GapInput extends LegendUIComponent {

        Slice s;
        LegendUIMetaRealPanel real;

        public GapInput(Slice s){
            super("", LegendUIPieChartPanel.this.controller, LegendUIPieChartPanel.this , 0, false);
            this.s = s;
            real = new LegendUIMetaRealPanel(null, GapInput.this.controller, GapInput.this, s.getGap(), true) {

                @Override
                public void realChanged(RealParameter newReal) {
                    GapInput.this.s.setGap(newReal);
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

    private final class FillInput extends LegendUIComponent {

        Slice s;
        LegendUIMetaFillPanel fill;

        public FillInput (Slice s){
            super("Fill", LegendUIPieChartPanel.this.controller, LegendUIPieChartPanel.this, 0, true);

            this.s = s;

            fill = new LegendUIMetaFillPanel(this.controller, this, s, true);
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
            LegendUIPieChartPanel.this.pie.moveSliceUp(i);
        }
    }

    private class ActionMoveDown implements ActionListener {

        private int i;
        public ActionMoveDown(int i) {
            this.i = i;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LegendUIPieChartPanel.this.pie.moveSliceDown(i);
        }
    }


    private class ActionRemove implements ActionListener {

        private int i;
        public ActionRemove(int i) {
            this.i = i;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LegendUIPieChartPanel.this.pie.removeSlice(i);
        }
    }

}
