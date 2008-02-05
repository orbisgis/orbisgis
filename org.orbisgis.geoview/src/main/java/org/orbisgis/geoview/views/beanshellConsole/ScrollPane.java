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

import java.awt.Color;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geocatalog.resources.TransferableResource;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.views.sqlConsole.actions.ActionsListener;
import org.orbisgis.geoview.views.toc.TransferableLayer;
import org.syntax.jedit.JEditTextArea;
import org.syntax.jedit.SyntaxStyle;
import org.syntax.jedit.TextAreaDefaults;
import org.syntax.jedit.tokenmarker.JavaTokenMarker;
import org.syntax.jedit.tokenmarker.Token;

import bsh.EvalError;
import bsh.Interpreter;

public class ScrollPane extends JScrollPane {
	
	JEditTextArea jEditTextArea;
	
    
	
	PrintStream out;
	
	private ActionsListener actionAndKeyListener;
	
	/**
	 * Default interpreter. A new Interpreter is created each time the beanshell panel is started.
	 */
	private Interpreter interpreter = new Interpreter();

	private GeoView2D geoview;



	private FileOutputStream fileOutputStream;



	private ByteArrayOutputStream scriptOutput;

	public ScrollPane(GeoView2D geoview) {
		this.geoview = geoview;
		setViewportView(getJEditTextArea());
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

			new Thread(interpreter).start();
        
		
			
			} catch (EvalError e) {
				jEditTextArea.setText( e.getErrorText());
			} 
    }
	
	public void setText(String text) {
		jEditTextArea.setText(text);
	}
	
	public FileOutputStream getFileOutputStream(){
		return fileOutputStream;
	}
	
	
	public JEditTextArea  getJEditTextArea(){
		TextAreaDefaults defaults = TextAreaDefaults.getDefaults();
        defaults.cols = 48;
        defaults.rows = 12;
        defaults.electricScroll = 0;
        SyntaxStyle[] styles = new SyntaxStyle[Token.ID_COUNT];
            styles[Token.COMMENT1] = new SyntaxStyle(new Color(0xDD0000),true,false);
            styles[Token.COMMENT2] = new SyntaxStyle(new Color(0xFF0000),true,false);
            styles[Token.KEYWORD1] = new SyntaxStyle(new Color(0x000066),false,true);
            styles[Token.KEYWORD2] = new SyntaxStyle(Color.black,false,true);
            styles[Token.KEYWORD3] = new SyntaxStyle(new Color(0x009900),false,true);
            styles[Token.LITERAL1] = new SyntaxStyle(new Color(0xCC0099),false,false);
            styles[Token.LITERAL2] = new SyntaxStyle(new Color(0xCC0099),false,true);
            styles[Token.LABEL] = new SyntaxStyle(new Color(0x990033),false,true);
            styles[Token.OPERATOR] = new SyntaxStyle(Color.black,false,true);
            styles[Token.INVALID] = new SyntaxStyle(Color.red,false,true);
        defaults.styles = styles;
		jEditTextArea = new JEditTextArea(defaults);
		jEditTextArea.setTokenMarker(new JavaTokenMarker());
		jEditTextArea.setFirstLine(0);
		return jEditTextArea;
	
		
	}


	public Interpreter getInterpreter() {
		
		return interpreter;
	}


	public String getOut() {
		return new String(scriptOutput.toByteArray());
	}
}