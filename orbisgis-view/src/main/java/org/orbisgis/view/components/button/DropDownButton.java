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
package org.orbisgis.view.components.button;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.EventHandler;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 * Drop Down Button, is a button that show a popup when clicked.
 */
public class DropDownButton extends JToggleButton implements
	ChangeListener, PopupMenuListener, ActionListener {
        private static final long serialVersionUID = 1L;
	    private boolean popupVisible = false;
        private JMenuItem selectedItem=null;
        private boolean buttonAsMenuItem = true; //Does this button will be replaced by the selected menu item
        private AtomicBoolean listenerInstalled = new AtomicBoolean(false);
        /**
         * Constructor, use selectMenu to set the icon and other properties to this button
         */
        public DropDownButton() {
            
        }

        /**
         * Initialise this control by using action
         * @param action
         */
        public DropDownButton(Action action) {
                super(action);
        }

        @Override
        public Component add(Component component, int i) {
                if(getComponentPopupMenu()==null) {
                        return super.add(component, i);
                } else {
                        setSelectedIfNone(component);
                        return getComponentPopupMenu().add(component,i);
                }
        }

        @Override
        public Component add(Component component) {
                if(getComponentPopupMenu()==null) {
                        return super.add(component);
                } else {
                        setSelectedIfNone(component);
                        return getComponentPopupMenu().add(component);
                }
        }
        private void setSelectedIfNone(Component component) {
            if(selectedItem==null && component instanceof JMenuItem) {
                setSelectedItem((JMenuItem)component);
            }
        }
        @Override
        public void remove(Component component) {
                if(getComponentPopupMenu()==null) {
                        super.remove(component);
                } else {
                        getComponentPopupMenu().remove(component);
                }
        }

        @Override
        public Component getComponent(int i) {
                if(getComponentPopupMenu()==null) {
                        return super.getComponent(i);
                } else {
                        return getComponentPopupMenu().getComponent(i);
                }
        }

        @Override
        public Component[] getComponents() {
                if(getComponentPopupMenu()==null) {
                        return super.getComponents();
                } else {
                        return getComponentPopupMenu().getComponents();
                }
        }

        /**
         * Does this button will be replaced by the selected menu item
         * @return 
         */
        public boolean isButtonAsMenuItem() {
            return buttonAsMenuItem;
        }
        
        
        /**
        * Does this button will be replaced by the selected menu item
        * @param buttonAsMenuItem 
        */
        public void setButtonAsMenuItem(boolean buttonAsMenuItem) {
            this.buttonAsMenuItem = buttonAsMenuItem;
        }
        
        
        /**
         * Constructor with a provided Icon
         * @param iconFile 
         */
        public DropDownButton(ImageIcon iconFile) {
		super(iconFile);
	}

        private void installListeners() {
                if(!listenerInstalled.getAndSet(true)) {
                        getModel().addChangeListener(this);
                        this.addActionListener(this);                        
                }
        }
        /**
         * Component initialisation
         */
        @Override
        public void addNotify() {
            super.addNotify();
            installListeners();
        }

        /**
         * @return The selected menu item, null if not set
         */
        public JMenuItem getSelectedItem() {
            return selectedItem;
        }
        
        /**
         * Set the selected menu item
         * If isButtonAsMenuItem, apply the icon and tooltip of the menuitem to this button
         * @param menu 
         */
        public void setSelectedItem(JMenuItem menu) {
            selectedItem = menu;     
            if(buttonAsMenuItem) {
                setIcon(menu.getIcon());
                setText(menu.getText());
                setToolTipText(menu.getToolTipText());
            }
        }
        
        /**
         * The user click on a menu item
         * @param ae 
         */
        public void onSelectMenuAction(ActionEvent ae) {
            Object src = ae.getSource();
            if(src instanceof JMenuItem) {           
                setSelectedItem((JMenuItem)src);
            }
        }
        /**
         * Merge the provided icon with the dropdown icon
         * @return 
         */
	private static ImageIcon mergeIcons(ImageIcon iconFile) {
		Image firstImage = iconFile.getImage();
		Image secondImage = OrbisGISIcon.getIconImage("btn_dropdown");

		int w = firstImage.getWidth(null) + secondImage.getWidth(null);
		int h = Math.max(firstImage.getHeight(null), secondImage
				.getHeight(null));
		BufferedImage resultImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resultImage.createGraphics();
		g2.drawImage(firstImage, 0, 0, null);
		g2.drawImage(secondImage, firstImage.getWidth(null), 0, null);
		return new ImageIcon(resultImage);
	}

        @Override
        public void setIcon(Icon icon) {
            if(icon == null) {
                    super.setIcon(null);
            } else if(icon instanceof ImageIcon) {
                super.setIcon(mergeIcons((ImageIcon)icon));
            }else{
                throw new IllegalArgumentException("DropDown button accept only ImageIcon");
            }
        }
        

	/*------------------------------[ ChangeListener ]---------------------------------------------------*/

	public void stateChanged(ChangeEvent e) {
		if (e.getSource().equals(this.getModel())) {
			if (popupVisible && !this.getModel().isRollover()) {
				this.getModel().setRollover(true);
			}
		}
	}
        /**
         * To update the button icon and properties when an item has been selected, add a listener
         * to each Item
         * @param jpm 
         */
        private void addRecursiveActionListener(MenuElement jpm) {
            if(jpm instanceof JPopupMenu || jpm instanceof JMenu) {
                for(MenuElement me : jpm.getSubElements()) {
                    addRecursiveActionListener(me);
                }
            } else if(jpm instanceof JMenuItem) {
                ((JMenuItem)jpm).addActionListener(EventHandler.create(ActionListener.class,this,"onSelectMenuAction",""));
            }
        }

        /**
         * The lifetime of the popup menu must be at most the same as this button
         * @param jpm 
         */
        @Override
        public void setComponentPopupMenu(JPopupMenu jpm) {
            jpm.addPopupMenuListener(this);
            if(buttonAsMenuItem) {
                addRecursiveActionListener(jpm);
            }
            super.setComponentPopupMenu(jpm);
        }

	/*------------------------------[ ActionListener ]---------------------------------------------------*/

	public void actionPerformed(ActionEvent ae) {
                //Trigger actionEvent on the last select menu item
                if(buttonAsMenuItem && selectedItem!=null) {
                    selectedItem.doClick();
                }
		JPopupMenu popup = getComponentPopupMenu();
		popup.show(this, 0, this.getHeight());
	}

	/*------------------------------[ PopupMenuListener ]---------------------------------------------------*/

	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		popupVisible = true;
		this.getModel().setRollover(true);
		this.getModel().setSelected(true);
	}

	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		popupVisible = false;
		this.getModel().setRollover(false);
		this.getModel().setSelected(false);
	}

	public void popupMenuCanceled(PopupMenuEvent e) {
		popupVisible = false;
	}

}
