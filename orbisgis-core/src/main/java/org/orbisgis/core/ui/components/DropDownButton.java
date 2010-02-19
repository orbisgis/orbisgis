package org.orbisgis.core.ui.components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.orbisgis.core.Services;
import org.orbisgis.core.images.IconLoader;
import org.orbisgis.core.images.IconNames;

public abstract class DropDownButton extends JToggleButton 
	implements ChangeListener, PopupMenuListener, ActionListener{ 
		private final DropDownButton mainButton = this;		
		private boolean popupVisible = false; 
		private String iconFile ;
		private final static String dropIconFile = IconNames.BTN_DROPDOWN;

		public DropDownButton(String iconFile){ 			
			this.iconFile = iconFile;
			setIcon(calculIcon());
			mainButton.getModel().addChangeListener(this);
			mainButton.addActionListener(this); 			
		} 
		
		private ImageIcon calculIcon() {
			BufferedImage resultImage = null;
			BufferedImage firstImage = null;
			BufferedImage secondImage = null;
			try {
				firstImage = ImageIO.read(IconLoader.getIconUrl(iconFile));
				secondImage = ImageIO.read(IconLoader.getIconUrl(dropIconFile));				
			} catch (IOException e) {
				Services.getErrorManager().error("Error during image loding in drop down button");
			}	
			
			int w = firstImage.getWidth() + secondImage.getWidth();
			int h = Math.max(firstImage.getHeight(), secondImage.getHeight());
			resultImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = resultImage.createGraphics();
			g2.drawImage(firstImage, 0, 0, null);
			g2.drawImage(secondImage, firstImage.getWidth(), 0, null);
			return new ImageIcon(resultImage);
		}


		
		public void setIconFile(String iconFile){
			this.iconFile = iconFile;
			setIcon(calculIcon());
		}
		

		/*------------------------------[ ChangeListener ]---------------------------------------------------*/ 

		public void stateChanged(ChangeEvent e){ 
			if(e.getSource()==mainButton.getModel()){ 
				if(popupVisible && !mainButton.getModel().isRollover()){ 
					mainButton.getModel().setRollover(true); 
					return; 
				} 				
			}else{ 
			} 
		} 

		/*------------------------------[ ActionListener ]---------------------------------------------------*/ 

		public void actionPerformed(ActionEvent ae){ 
			JPopupMenu popup = getPopupMenu(); 
			popup.addPopupMenuListener(this); 
			popup.show(mainButton, 0, mainButton.getHeight()); 
		} 

		/*------------------------------[ PopupMenuListener ]---------------------------------------------------*/ 

		public void popupMenuWillBecomeVisible(PopupMenuEvent e){ 
			popupVisible = true; 
			mainButton.getModel().setRollover(true); 
			mainButton.getModel().setSelected(true); 
		} 

		public void popupMenuWillBecomeInvisible(PopupMenuEvent e){ 
			popupVisible = false; 

			mainButton.getModel().setRollover(false); 
			mainButton.getModel().setSelected(false); 
			((JPopupMenu)e.getSource()).removePopupMenuListener(this); 
		} 

		public void popupMenuCanceled(PopupMenuEvent e){ 
			popupVisible = false; 
		} 

		/*------------------------------[ Other Methods ]---------------------------------------------------*/ 

		protected abstract JPopupMenu getPopupMenu(); 

		public JToggleButton addToToolBar(JToolBar toolbar){ 
			toolbar.add(mainButton);
			return mainButton; 
		} 	
}

