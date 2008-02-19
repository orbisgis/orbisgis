/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
package org.orbisgis.geoview.views.beanshellConsole;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.TextArea;
import java.awt.TextComponent;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.text.BadLocationException;

import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geocatalog.resources.TransferableResource;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.views.beanshellConsole.actions.ActionsListener;
import org.orbisgis.geoview.views.toc.TransferableLayer;

import com.Ostermiller.Syntax.HighlightedDocument;


import bsh.EvalError;
import bsh.Interpreter;

public class ScriptPanel2 extends JPanel implements DropTargetListener{

	
	 /** The document holding the text being edited. */
    private HighlightedDocument document = new HighlightedDocument();

    private JTextPane jTextPane;	


	PrintStream out;

	/**
	 * Default interpreter. A new Interpreter is created each time the beanshell panel is started.
	 */
	private Interpreter interpreter = new Interpreter();

	private GeoView2D geoview;
	
	private ActionsListener actionAndKeyListener;


	private FileOutputStream fileOutputStream;



	private ByteArrayOutputStream scriptOutput;

	

	public ScriptPanel2(GeoView2D geoview,final ActionsListener actionAndKeyListener) {
		this.geoview = geoview;		
		this.actionAndKeyListener = actionAndKeyListener;
		
		setLayout(new BorderLayout());
		
		//Create a scroll pane wrapped around the text pane
        JScrollPane scrollPane = new JScrollPane(getJTextPane());
        document.setHighlightStyle(HighlightedDocument.JAVA_STYLE);
        
		this.add(scrollPane, BorderLayout.CENTER);
		initInterpreter();
	}


	private void initInterpreter() {



            interpreter = new Interpreter();
            try {
            interpreter.set("bshEditor", this);


				scriptOutput = new ByteArrayOutputStream();
				PrintStream outStream = new PrintStream(scriptOutput);
				interpreter.setOut(outStream);
				interpreter.setErr(outStream);


            interpreter.setClassLoader(OrbisgisCore.getDSF().getClass()
					.getClassLoader());
			interpreter.set("dsf", OrbisgisCore.getDSF());

			interpreter.setClassLoader(geoview.getViewContext().getClass()
					.getClassLoader());
			interpreter.set("gc", geoview.getViewContext());

			interpreter.eval("setAccessibility(true)");

			

			} catch (EvalError e) {
				jTextPane.setText( e.getErrorText());
			}
    }

	public void setText(String text) {
		jTextPane.setText(text);
	}

	public FileOutputStream getFileOutputStream(){
		return fileOutputStream;
	}


	public JTextPane  getJTextPane(){
		    
		jTextPane = new JTextPane(document);
		
		jTextPane.setDropTarget(new DropTarget(this, this));
		jTextPane.addKeyListener(actionAndKeyListener);
		jTextPane.setAutoscrolls(true);
		
		return jTextPane;


	}


	public Interpreter getInterpreter() {

		return interpreter;
	}


	public String getOut() {

		return new String(scriptOutput.toByteArray());
	}


	public void dragEnter(DropTargetDragEvent dtde) {
		
		
	}


	public void dragExit(DropTargetEvent dte) {
		
		
	}


	public void dragOver(DropTargetDragEvent dtde) {
	
		
	}


	public void drop(DropTargetDropEvent dtde) {
		final Transferable t = dtde.getTransferable();
		String script = null;

		try {
			if ((t.isDataFlavorSupported(TransferableResource
					.getResourceFlavor()))
					|| (t.isDataFlavorSupported(TransferableLayer
							.getLayerFlavor()))) {
				dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				String s = (String) t.getTransferData(DataFlavor.stringFlavor);
				dtde.getDropTargetContext().dropComplete(true);
				script = s;
			} else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				String s = (String) t.getTransferData(DataFlavor.stringFlavor);
				dtde.getDropTargetContext().dropComplete(true);
				script = s;
			}
		} catch (IOException e) {
			dtde.rejectDrop();
		} catch (UnsupportedFlavorException e) {
			dtde.rejectDrop();
		}
	
		if (script != null) {
			// Cursor position
			int position = jTextPane.getCaretPosition();
			try {
				jTextPane.getDocument().insertString(position, script,null);
			} catch (BadLocationException e) {				
				e.printStackTrace();
			}
			// Replace the cursor at end line
			jTextPane.requestFocus();
		}
		dtde.rejectDrop();
		
		actionAndKeyListener.setButtonsStatus();
		
	}


	public void dropActionChanged(DropTargetDragEvent dtde) {
		
		
	}


}