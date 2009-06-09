package org.orbisgis.core.ui.skin;

import java.awt.Color;

import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

import net.infonode.gui.laf.InfoNodeLookAndFeel;
import net.infonode.gui.laf.InfoNodeLookAndFeelTheme;

public class OrbisGISSkin {





	void getSink(){
		try {


			InfoNodeLookAndFeelTheme theme2 =
		        new InfoNodeLookAndFeelTheme("My Theme",
		                                     new Color(110, 120, 150),
		                                     new Color(0, 170, 0),
		                                     new Color(80, 80, 80),
		                                     Color.WHITE,
		                                     new Color(0, 170, 0),
		                                     Color.WHITE,
		                                     0.8);



			javax.swing.UIManager.setLookAndFeel(new InfoNodeLookAndFeel(theme2));


			SwingUtilities.updateComponentTreeUI(null);


		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


	}
}
