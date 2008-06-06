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
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.h2.command.ddl.CreateLinkedTable;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.FlowLayoutPreviewWindow;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.LegendListDecorator;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.SymbolListDecorator;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table.ButtonCanvas;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table.SymbolCellEditor;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table.SymbolValueTableModel;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table.SymbolValueCellRenderer;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table.SymbolValuePOJO;
import org.orbisgis.layerModel.ILayer;
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
public class JPanelUniqueValueLegend extends javax.swing.JPanel implements ILegendPanelUI{
    
	private String identity="Unique value legend";
	private int constraint=0;
	UniqueValueLegend leg = null;
	ILayer layer=null;
	
	private LegendListDecorator dec = null;
	
    public JPanelUniqueValueLegend(UniqueValueLegend leg, int constraint, ILayer layer ) {
    	this.constraint=constraint;
    	this.leg=leg;
    	this.layer=layer;
        initComponents();
		initCombo();
        initList();
    }
    
    private void initCombo() {

    	ArrayList<String> comboValuesArray = new ArrayList<String>();
    	try {
			int numFields = layer.getDataSource().getFieldCount();
			for (int i=0; i<numFields; i++){
				int fieldType = layer.getDataSource().getFieldType(i).getTypeCode();
				if (fieldType!=Type.GEOMETRY &&
						fieldType!=Type.RASTER 
					){
					comboValuesArray.add(layer.getDataSource().getFieldName(i));
				}
			}
		} catch (DriverException e) {
			System.out.println("Driver Exception: "+e.getMessage());
		}
    	
		String [] comboValues = new String[comboValuesArray.size()];
		
		comboValues = comboValuesArray.toArray(comboValues);
    	
    	
    	DefaultComboBoxModel model = (DefaultComboBoxModel)jComboBox1.getModel();
    	
    	for (int i=0; i < comboValues.length; i++){
    		model.addElement(comboValues[i]);
    	}
    	
    	String field = leg.getClassificationField();
		jComboBox1.setSelectedItem(field);
		
		if (!(leg.getDefaultSymbol() instanceof NullSymbol)){
			jCheckBoxRestOfValues.setSelected(true);
		}else{
			jCheckBoxRestOfValues.setSelected(false);
		}
    	
	}

	private void initList() {
    	jTable1.setModel(new SymbolValueTableModel());
    	jTable1.setRowHeight(25);
    	
		SymbolValueTableModel mod = (SymbolValueTableModel)jTable1.getModel();
		
		
		Value [] val = leg.getClassificationValues();
	
		//jTable1.setDefaultEditor(Symbol.class, new SymbolCellEditor());
		jTable1.setDefaultRenderer(Symbol.class, new SymbolValueCellRenderer());
	
		
		for (int i=0; i<val.length; i++){
			
			SymbolValuePOJO poj = new SymbolValuePOJO();
			poj.setSym(leg.getValueSymbol(val[i]));
			poj.setVal(val[i]);
			poj.setLabel(leg.getValueSymbol(val[i]).getName());
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
	
	protected Symbol createRandomSymbol(int constraint){
		Symbol s;
		
		Random rand = new Random();
		
		int g2 = rand.nextInt(255);
		int r2 = rand.nextInt(255);
		int b2 = rand.nextInt(255);
		
		Color outline = Color.black;
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
	

	public JPanelUniqueValueLegend( int constraint, ILayer layer) {
        this(LegendFactory.createUniqueValueLegend(), constraint, layer);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jCheckBoxRestOfValues = new javax.swing.JCheckBox();
        jCheckBoxOrder = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButtonAddAll = new javax.swing.JButton();
        jButtonAddOne = new javax.swing.JButton();
        jButtonDel = new javax.swing.JButton();

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Classification field:");

        jCheckBoxRestOfValues.setText("rest of values");
        jCheckBoxRestOfValues.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxRestOfValues.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxRestOfValuesActionPerformed(evt);
            }
        });

        jCheckBoxOrder.setText("order");
        jCheckBoxOrder.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxOrderActionPerformed(evt);
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

        jButtonAddAll.setText("Add all");
        jButtonAddAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddAllActionPerformed(evt);
            }
        });

        jButtonAddOne.setText("Add");
        jButtonAddOne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddOneActionPerformed(evt);
            }
        });

        jButtonDel.setText("Delete");
        jButtonDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBoxRestOfValues)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBoxOrder)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(91, 91, 91)
                .addComponent(jButtonAddAll, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAddOne, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDel, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                .addGap(137, 137, 137))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxRestOfValues)
                    .addComponent(jCheckBoxOrder))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jButtonDel)
                    .addComponent(jButtonAddOne)
                    .addComponent(jButtonAddAll))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAddAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddAllActionPerformed
        SpatialDataSourceDecorator sdsd=layer.getDataSource();
        String selitem = (String)jComboBox1.getSelectedItem();
        if (jComboBox1.getSelectedIndex()==-1){
        	return;
        }
        
        ((SymbolValueTableModel)jTable1.getModel()).deleteAllSymbols();
        
        ArrayList<String> alreadyAdded = new ArrayList<String>();
        try {
        	int fieldindex = sdsd.getFieldIndexByName(selitem);
        	
			long rowcount = sdsd.getRowCount();

			for (int i=0; i <rowcount; i++){
				if (alreadyAdded.size()==32){
					JOptionPane.showMessageDialog(this, "More than 32 differnt values found. Showing only 32");
					break;
				}
				
				Value[] vals = sdsd.getRow(i);
				
				Value val = vals[fieldindex];
				
				if (val.isNull()){
					continue;
				}
								
				if (!alreadyAdded.contains(val.toString())){
					
					alreadyAdded.add(val.toString());
					
					Symbol sym = createRandomSymbol(constraint);
					
					SymbolValuePOJO poj = new SymbolValuePOJO();
					
					poj.setSym(sym);
					poj.setVal(val);
					poj.setLabel(val.toString());
					
					((SymbolValueTableModel)jTable1.getModel()).addSymbolValue(poj);
				}
				
			}
			
		} catch (DriverException e) {
			System.out.println("Driver Exception: "+e.getMessage());
		}
        
    }//GEN-LAST:event_jButtonAddAllActionPerformed

    private void jButtonAddOneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddOneActionPerformed
    	SymbolValueTableModel mod = (SymbolValueTableModel)jTable1.getModel();
		
    	int rowCount = mod.getRowCount();
    	
    	SymbolValuePOJO poj= new SymbolValuePOJO();
    	if (rowCount<32){
	    	Symbol sym = createRandomSymbol(constraint);
	    	Value val=null;
	    	String label = "";
	    	if (rowCount>0){
	    		sym = (Symbol)mod.getValueAt(rowCount-1, 0);
	    		val = (Value)mod.getValueAt(rowCount-1, 3);
	    		label = (String)mod.getValueAt(rowCount-1, 2);
	    	} 
	    	poj.setSym(sym);
			poj.setVal(val);
			poj.setLabel(label);
    	}else{
    		JOptionPane.showMessageDialog(this, "More than 32 differnt values found. Showing only 32");
    	}
		
		
		((SymbolValueTableModel)jTable1.getModel()).addSymbolValue(poj);
    }//GEN-LAST:event_jButtonAddOneActionPerformed

    private void jButtonDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDelActionPerformed
        SymbolValueTableModel mod = (SymbolValueTableModel)jTable1.getModel();
        int [] rows = jTable1.getSelectedRows();
        SymbolValuePOJO [] pojos = new SymbolValuePOJO[rows.length];
        for (int i=0; i<rows.length; i++){
        	pojos[i]=(SymbolValuePOJO)mod.getValueAt(rows[i], -1);
        }
        mod.deleteSymbolPojos(pojos);
        
        jTable1.getSelectionModel().clearSelection();
    }//GEN-LAST:event_jButtonDelActionPerformed

    private void jCheckBoxRestOfValuesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxRestOfValuesActionPerformed
    	dec.setLegend(getLegend());
    }//GEN-LAST:event_jCheckBoxRestOfValuesActionPerformed

    private void jCheckBoxOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxOrderActionPerformed
        SymbolValueTableModel mod = (SymbolValueTableModel)jTable1.getModel();
        mod.setOrdered(jCheckBoxOrder.isSelected());
        jTable1.validate();
        jTable1.repaint();
    }//GEN-LAST:event_jCheckBoxOrderActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddAll;
    private javax.swing.JButton jButtonAddOne;
    private javax.swing.JButton jButtonDel;
    private javax.swing.JCheckBox jCheckBoxOrder;
    private javax.swing.JCheckBox jCheckBoxRestOfValues;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
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
		return "Set an Unique value legend to the selected layer";
	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return "Unique value legend";
	}
	
	public void setIdentity(String id){
		identity=id;
	}
	
	public String getIdentity(){
		return identity;
	}

	public Legend getLegend() {
		
		UniqueValueLegend legend = LegendFactory.createUniqueValueLegend();
		
		SymbolValueTableModel mod = (SymbolValueTableModel)jTable1.getModel();
		
		for (int i=0; i<mod.getRowCount(); i++){
			SymbolValuePOJO pojo = (SymbolValuePOJO)mod.getValueAt(i, -1);
			
			Symbol s = pojo.getSym();
			s.setName(pojo.getLabel());
			Value val=ValueFactory.createNullValue();
			try {
				val = pojo.getVal();
			} catch (NumberFormatException e) {
				System.out.println("Number Format Exception: "+e.getMessage());
			} 
			
			legend.addClassification(val, s);
		}
		try {
			legend.setClassificationField((String)jComboBox1.getSelectedItem());
		} catch (DriverException e) {
			System.out.println("Driver Exception: "+e.getMessage());
		}
		legend.setName(dec.getLegend().getName());
		if (jCheckBoxRestOfValues.isSelected()){
			legend.setDefaultSymbol(createRandomSymbol(constraint));
		}
		
		return legend;
	}
	
	public void setDecoratorListener(LegendListDecorator dec){
		this.dec=dec;
	}
    
}
