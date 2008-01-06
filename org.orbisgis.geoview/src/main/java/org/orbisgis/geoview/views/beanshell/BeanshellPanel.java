package org.orbisgis.geoview.views.beanshell;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.renderer.style.BasicStyle;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.tools.Rectangle2DDouble;

import bsh.ClassPathException;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.util.JConsole;
import bsh.util.NameCompletion;
import bsh.util.NameCompletionTable;


public class BeanshellPanel extends JPanel {
	
	private GeoView2D geoview;

	public BeanshellPanel(final GeoView2D geoview) {
		super(new BorderLayout());
		this.geoview = geoview;
		
		
		
		try {
		final JConsole console = new JConsole();
		console.print("Datasource factory refere to as dsf" + "\n");
		add(console, BorderLayout.CENTER);
		Interpreter interpreter = new Interpreter(console);
		
        
		interpreter.setClassLoader(OrbisgisCore.getDSF().getClass().getClassLoader());
		interpreter.set("dsf", OrbisgisCore.getDSF());
		
		interpreter.setClassLoader(geoview.getViewContext().getClass().getClassLoader());
		interpreter.set("gc", geoview.getViewContext());
		console.print("GeoView context is available as gc" + "\n");
		
	
		
		interpreter.set("style", new BasicStyle());
		
		interpreter.eval("setAccessibility(true)");
		
				
		
		new Thread(interpreter).start();
		} catch (EvalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	

	

	
}