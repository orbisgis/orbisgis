/*
 * JPanelUniqueSymbolLegend.java
 *
 * Created on 27 de febrero de 2008, 18:20
 */

package org.orbisgis.editorViews.toc.actions.cui.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.ColorPicker;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.FlowLayoutPreviewWindow;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.LegendListDecorator;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table.SymbolValueCellRenderer;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table.SymbolValuePOJO;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table.SymbolValueTableModel;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.renderer.classification.Range;
import org.orbisgis.renderer.classification.RangeMethod;
import org.orbisgis.renderer.legend.DefaultIntervalLegend;
import org.orbisgis.renderer.legend.Interval;
import org.orbisgis.renderer.legend.IntervalLegend;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.LegendFactory;
import org.orbisgis.renderer.legend.NullSymbol;
import org.orbisgis.renderer.legend.Symbol;
import org.orbisgis.renderer.legend.SymbolFactory;
import org.orbisgis.renderer.legend.UniqueSymbolLegend;
import org.orbisgis.renderer.legend.UniqueValueLegend;
import org.sif.UIFactory;

/**
 *
 * @author  david
 */
public class JPanelIntervalClassifiedLegend extends javax.swing.JPanel implements ILegendPanelUI{

	private String identity="Interval classified legend";
	private int constraint=0;
	private ILayer layer=null;
	private IntervalLegend leg = null;

	private LegendListDecorator dec = null;

    /** Creates new form JPanelUniqueSymbolLegend */
    public JPanelIntervalClassifiedLegend(IntervalLegend leg, int constraint, ILayer layer ) {
    	this.constraint=constraint;
    	this.layer=layer;
    	this.leg=leg;
        initComponents();
        initCombo();
        initList();
    }
    public JPanelIntervalClassifiedLegend(int constraint, ILayer layer) {
        this(LegendFactory.createIntervalLegend(), constraint, layer);
    }
    
    private void initCombo() {

    	ArrayList<String> comboValuesArray = new ArrayList<String>();
    	try {
			int numFields = layer.getDataSource().getFieldCount();
			for (int i=0; i<numFields; i++){
				int fieldType = layer.getDataSource().getFieldType(i).getTypeCode();
				if (fieldType==Type.BYTE || 
						fieldType==Type.SHORT ||
						fieldType==Type.INT ||
						fieldType==Type.LONG ||
						fieldType==Type.FLOAT ||
						fieldType==Type.DOUBLE
					){
					comboValuesArray.add(layer.getDataSource().getFieldName(i));
				}
			}
		} catch (DriverException e) {
			System.out.println("Driver Exception: "+e.getMessage());
		}
    	
		String [] comboValues = new String[comboValuesArray.size()];
		
		comboValues = comboValuesArray.toArray(comboValues);
		
    	DefaultComboBoxModel model = (DefaultComboBoxModel)jComboBoxClasificationField.getModel();
    	DefaultComboBoxModel modelType = (DefaultComboBoxModel)jComboBoxTypeOfInterval.getModel();
    	
    	for (int i=0; i < comboValues.length; i++){
    		model.addElement(comboValues[i]);
    	}
    	
    	modelType.addElement("Quantiles");
    	modelType.addElement("Equivalences");
    	modelType.addElement("Moyennes");
    	modelType.addElement("Standard");
    	
    	modelType.setSelectedItem("Standard");
    	
    	String field = leg.getClassificationField();
		jComboBoxClasificationField.setSelectedItem(field);
		
		if (!(leg.getDefaultSymbol() instanceof NullSymbol)){
			jCheckBoxRestOfValues.setSelected(true);
		}else{
			jCheckBoxRestOfValues.setSelected(false);
		}
		    	
		SpinnerNumberModel spinnerMod = new SpinnerNumberModel(0, 0, 32, 1);
		jSpinnerNumberOfIntervals.setModel(spinnerMod);
	}
    
	private void initList() {
    	jTable1.setModel(new SymbolValueTableModel());
    	jTable1.setRowHeight(25);
    	
		SymbolValueTableModel mod = (SymbolValueTableModel)jTable1.getModel();
		
		
		ArrayList<Interval> intervals = ((DefaultIntervalLegend)leg).getIntervals();

	
		jTable1.setDefaultRenderer(Symbol.class, new SymbolValueCellRenderer());
	
		DefaultIntervalLegend dleg = (DefaultIntervalLegend)leg;
		for (int i=0; i<intervals.size(); i++){
			Symbol sOfInterval=dleg.getSymbolFor(intervals.get(i).getMinValue());
			
			SymbolValuePOJO poj = new SymbolValuePOJO();
			poj.setSym(sOfInterval);
			poj.setVal(intervals.get(i).getIntervalString());
			poj.setLabel(sOfInterval.getName());
			poj.setValueType(intervals.get(i).getMinValue().getType());
			mod.addSymbolValue(poj);
		}
		
		jTable1.addMouseListener(new MouseListener(){

			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount()>1){
					int col = jTable1.getSelectedColumn();
					if (col==0){
						//FlowLayoutPreviewWindow flpw = new FlowLayoutPreviewWindow();
						//flpw.setConstraint(constraint);
						int row = jTable1.getSelectedRow();
						SymbolValueTableModel mod = (SymbolValueTableModel) jTable1.getModel();
						UniqueSymbolLegend usl = LegendFactory.createUniqueSymbolLegend();
						usl.setSymbol((Symbol)mod.getValueAt(row, 0));
						JPanelUniqueSymbolLegend jpusl = new JPanelUniqueSymbolLegend(usl, constraint, true);
						
						if (UIFactory.showDialog(jpusl)){
							Symbol sym = jpusl.getSymbolComposite();
							
							mod.setValueAt(sym, row, col);
						}
					}
				}
			}

			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		mod.addTableModelListener(new TableModelListener(){

			public void tableChanged(TableModelEvent e) {
				jTable1Changed(e);				
			}
			
		});
	}

	protected void jTable1Changed(TableModelEvent e) {
		dec.setLegend(getLegend());
	}
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jComboBoxClasificationField = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButtonCalculeIntervals = new javax.swing.JButton();
        jButtonAdd = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jComboBoxTypeOfInterval = new javax.swing.JComboBox();
        jCheckBoxRestOfValues = new javax.swing.JCheckBox();
        jButtonSecondColor = new javax.swing.JButton();
        jButtonFirstColor = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jSpinnerNumberOfIntervals = new javax.swing.JSpinner();

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Classification field:");

        jComboBoxClasificationField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxClasificationFieldActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Symbol", "Value", "Label"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButtonCalculeIntervals.setText("Calcule intervals");
        jButtonCalculeIntervals.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCalculeIntervalsActionPerformed(evt);
            }
        });

        jButtonAdd.setText("Add");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });

        jButtonDelete.setText("Delete");
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });

        jLabel2.setText("Type of interval: ");

        jComboBoxTypeOfInterval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxTypeOfIntervalActionPerformed(evt);
            }
        });

        jCheckBoxRestOfValues.setText("Rest of values");
        jCheckBoxRestOfValues.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxRestOfValues.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxRestOfValuesActionPerformed(evt);
            }
        });

        jButtonSecondColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSecondColorActionPerformed(evt);
            }
        });

        jButtonFirstColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFirstColorActionPerformed(evt);
            }
        });

        jLabel3.setText("First color:");

        jLabel4.setText("Final color:");

        jLabel5.setText("Number of intervals: ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(91, 91, 91)
                .addComponent(jButtonCalculeIntervals, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAdd, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDelete, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                .addGap(152, 152, 152))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(27, 27, 27)
                .addComponent(jComboBoxTypeOfInterval, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxRestOfValues)
                .addContainerGap(62, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonFirstColor, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSecondColor, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinnerNumberOfIntervals, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxClasificationField, 0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBoxClasificationField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jCheckBoxRestOfValues)
                    .addComponent(jComboBoxTypeOfInterval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jButtonSecondColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonFirstColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(jSpinnerNumberOfIntervals, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jButtonDelete)
                    .addComponent(jButtonAdd)
                    .addComponent(jButtonCalculeIntervals))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSecondColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSecondColorActionPerformed
    	ColorPicker picker = new ColorPicker();
    	if (UIFactory.showDialog(picker)){
	    	Color color = picker.getColor();
	    	jButtonSecondColor.setBackground(color);
    	}
    }//GEN-LAST:event_jButtonSecondColorActionPerformed

    private void jButtonFirstColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFirstColorActionPerformed
    	ColorPicker picker = new ColorPicker();
    	if (UIFactory.showDialog(picker)){
	    	Color color = picker.getColor();
	    	jButtonFirstColor.setBackground(color);
    	}
    }//GEN-LAST:event_jButtonFirstColorActionPerformed

    private void jComboBoxTypeOfIntervalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxTypeOfIntervalActionPerformed
        
    }//GEN-LAST:event_jComboBoxTypeOfIntervalActionPerformed

    private void jComboBoxClasificationFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxClasificationFieldActionPerformed
        
    }//GEN-LAST:event_jComboBoxClasificationFieldActionPerformed

    private void jButtonCalculeIntervalsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCalculeIntervalsActionPerformed
    	RangeMethod rm=null;
		try {
			rm = new RangeMethod(layer.getDataSource(), (String)jComboBoxClasificationField.getSelectedItem(),
					(Integer)jSpinnerNumberOfIntervals.getValue());
		

			int typeOfIntervals = jComboBoxTypeOfInterval.getSelectedIndex();
			
			switch (typeOfIntervals) {
			case 0:
				rm.disecEquivalences();
				break;
			case 1:
				rm.disecQuantiles();
				break;
			case 2:
				rm.disecMoyennes();
				break;
			case 3:
				rm.disecStandard();
				break;
			}
		
		
		} catch (DriverException e) {
			System.out.println("Driver Exception: "+e.getMessage());
			return;
		}

		Range[] ranges = rm.getRanges();

		SymbolValueTableModel mod = (SymbolValueTableModel)jTable1.getModel();
		
		mod.removeAll();
		
		for (int i=0; i <ranges.length; i++){
			Range r = ranges[i];
			
			Symbol s = createRandomSymbol(constraint);
			String rangeValue = String.valueOf(r.getMinRange())+" - "+String.valueOf(r.getMaxRange());
			String label = rangeValue;
			
			SymbolValuePOJO poj = new SymbolValuePOJO();
			poj.setSym(s);
			poj.setVal(rangeValue);
			poj.setLabel(label);
			poj.setValueType(Type.DOUBLE);
			
			mod.addSymbolValue(poj);
		}
    	
    }//GEN-LAST:event_jButtonCalculeIntervalsActionPerformed

    protected Symbol createRandomSymbol(int constraint){
		Symbol s;
		
		Random rand = new Random();
		
		int r1 = rand.nextInt(255);
		int r2 = rand.nextInt(255);
		int g1 = rand.nextInt(255);
		int g2 = rand.nextInt(255);
		int b1 = rand.nextInt(255);
		int b2 = rand.nextInt(255);
		
		Color outline = new Color(r1, g1, b1);
		Color fill = new Color(r2, g2, b2);
		
		switch (constraint) {
		case GeometryConstraint.LINESTRING:
		case GeometryConstraint.MULTI_LINESTRING:
			Stroke stroke = new BasicStroke(1);
			s=SymbolFactory.createLineSymbol(outline, (BasicStroke)stroke);
			break;
		case GeometryConstraint.POINT:
		case GeometryConstraint.MULTI_POINT:
			int size = 10;
			s=SymbolFactory.createCirclePointSymbol(outline, fill, size);
			break;
		case GeometryConstraint.POLYGON:
		case GeometryConstraint.MULTI_POLYGON:
			Stroke strokeP = new BasicStroke(1);
			s=SymbolFactory.createPolygonSymbol(strokeP, outline, fill);
			break;
		case GeometryConstraint.MIXED:
		default:
			Symbol sl=createRandomSymbol(GeometryConstraint.LINESTRING);
			Symbol sc=createRandomSymbol(GeometryConstraint.POINT);
			Symbol sp=createRandomSymbol(GeometryConstraint.POLYGON);
			Symbol [] arraySym={sl, sc, sp};
			
			s=SymbolFactory.createSymbolComposite(arraySym);
			break;
		}
		return s;
	}
    
    
    protected Symbol createDefaultSymbol(int constraint){
		Symbol s;
		
		switch (constraint) {
		case GeometryConstraint.LINESTRING:
		case GeometryConstraint.MULTI_LINESTRING:
			Color color = jButtonFirstColor.getBackground();
			Stroke stroke = new BasicStroke(1);
			s=SymbolFactory.createLineSymbol(color, (BasicStroke)stroke);
			break;
		case GeometryConstraint.POINT:
		case GeometryConstraint.MULTI_POINT:
			Color outline = jButtonFirstColor.getBackground();
			Color fillColor = jButtonSecondColor.getBackground();
			int size = 10;
			s=SymbolFactory.createCirclePointSymbol(outline, fillColor, size);
			break;
		case GeometryConstraint.POLYGON:
		case GeometryConstraint.MULTI_POLYGON:
			Color outlineP = jButtonFirstColor.getBackground();
			Color fillColorP = jButtonSecondColor.getBackground();
			Stroke strokeP = new BasicStroke(1);
			s=SymbolFactory.createPolygonSymbol(strokeP, outlineP, fillColorP);
			break;
		case GeometryConstraint.MIXED:
		default:
			Symbol sl=createRandomSymbol(GeometryConstraint.LINESTRING);
			Symbol sc=createRandomSymbol(GeometryConstraint.POINT);
			Symbol sp=createRandomSymbol(GeometryConstraint.POLYGON);
			Symbol [] arraySym={sl, sc, sp};
			
			s=SymbolFactory.createSymbolComposite(arraySym);
			break;
		}
		return s;
	}
	
    
    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
SymbolValueTableModel mod = (SymbolValueTableModel)jTable1.getModel();
		
    	int rowCount = mod.getRowCount();
    	
    	SymbolValuePOJO poj= new SymbolValuePOJO();
    	if (rowCount<32){
	    	Symbol sym = createRandomSymbol(constraint);
	    	String val="";
	    	String label = "";
	    	int type = Type.DOUBLE;
	    	if (rowCount>0){
	    		sym = (Symbol)mod.getValueAt(rowCount-1, 0);
	    		val = (String)mod.getValueAt(rowCount-1, 1);
	    		label = (String)mod.getValueAt(rowCount-1, 2);
	    		type = (Integer)mod.getValueAt(rowCount-1, 3);
	    	} 
	    	poj.setSym(sym);
			poj.setVal(val);
			poj.setLabel(label);
			poj.setValueType(type);
    	}else{
    		JOptionPane.showMessageDialog(this, "More than 32 different intervals found. Showing only 32");
    	}
		
		
		((SymbolValueTableModel)jTable1.getModel()).addSymbolValue(poj);
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
    	 SymbolValueTableModel mod = (SymbolValueTableModel)jTable1.getModel();
         int [] rows = jTable1.getSelectedRows();
         while (rows.length>0){
         	mod.deleteSymbolValue(rows[0]);
         	rows = jTable1.getSelectedRows();
         }
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jCheckBoxRestOfValuesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxRestOfValuesActionPerformed
        dec.setLegend(getLegend());
    }//GEN-LAST:event_jCheckBoxRestOfValuesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonCalculeIntervals;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonFirstColor;
    private javax.swing.JButton jButtonSecondColor;
    private javax.swing.JCheckBox jCheckBoxRestOfValues;
    private javax.swing.JComboBox jComboBoxClasificationField;
    private javax.swing.JComboBox jComboBoxTypeOfInterval;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinnerNumberOfIntervals;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
    public String toString(){
    	//return "Unique symbol";
    	return identity;
    }

	public Component getComponent() {
		// TODO Auto-generated method stub
		return this;
	}

	public String getInfoText() {
		// TODO Auto-generated method stub
		return "Set Interval classified legend to the selected layer";
	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return "Interval classified legend";
	}

	public void setIdentity(String id){
		identity=id;
	}

	public String getIdentity(){
		return identity;
	}
	public Legend getLegend() {
		IntervalLegend legend = LegendFactory.createIntervalLegend();
		
		SymbolValueTableModel mod = (SymbolValueTableModel)jTable1.getModel();
		
		for (int i=0; i<mod.getRowCount(); i++){
			SymbolValuePOJO pojo = (SymbolValuePOJO)mod.getValueAt(i, -1);
			
			Symbol s = pojo.getSym();
			s.setName(pojo.getLabel());
			String [] vals = pojo.getVal().split(" - ");
			Value minVal=null;
			Value maxVal=null;
			try {
				minVal = ValueFactory.createValueByType(vals[0], pojo.getValueType());
				maxVal = ValueFactory.createValueByType(vals[1], pojo.getValueType());
			} catch (Exception e) {
				System.out.println("Exception raised: "+e.getMessage());
			} 
			
			
			legend.addInterval(minVal, true, maxVal, false, s);
		}
		try {
			legend.setClassificationField((String)jComboBoxClasificationField.getSelectedItem());
		} catch (DriverException e) {
			System.out.println("Driver Exception: "+e.getMessage());
		}
		legend.setName(dec.getLegend().getName());
		if (jCheckBoxRestOfValues.isSelected()){
			legend.setDefaultSymbol(createDefaultSymbol(constraint));
		}
		
		return legend;
	}

	public void setDecoratorListener(LegendListDecorator dec){
		this.dec=dec;
	}

}
