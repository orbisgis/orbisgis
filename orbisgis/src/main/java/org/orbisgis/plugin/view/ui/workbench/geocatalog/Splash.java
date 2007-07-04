package org.orbisgis.plugin.view.ui.workbench.geocatalog;
//Upadates: 2004.04.02, 2004.01.09


import java.awt.*;

import javax.swing.*;


/**
 * A splash screen to show while the main program is loading. A typical use
 * is:
 * <pre>
 *
 *   public static void main(String[] args) {
 *     Splash s = new Splash(delay1);
 *     new MainProgram();
 *     s.dispose(delay2);
 *   }  
 *
 * </pre>
 * The first line creates a Splash that will appear until another frame
 * hides it (MainProgram), but at least during "delay1" milliseconds.<br>
 * To distroy the Splash you can either call "s.dispose()" or
 * "s.dispose(delay2)", that will actually show the Splash for "delay2"
 * milliseconds and only then hide it.<br>
 * The picture to show must be in a file called "splash.png".
 */ 
 public class Splash extends JWindow {

  
  /**
   * Creates a Splash that will appear until another frame hides it, but at
   * least during "delay" milliseconds.
   * @param delay the delay in milliseconds
   */
  public Splash(int delay) {
    JPanel p = new JPanel();
    p.setLayout(new BorderLayout());
    p.add(new SplashPicture("splashball.png"),BorderLayout.CENTER);
    p.add(new JLabel("Version 0.1 - IRSTV CNRS-FR-2488"), BorderLayout.SOUTH);
    p.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
    getContentPane().add(p);
    setSize(500, 169);
    setLocationRelativeTo(null);
    setVisible(true);
    try {
      Thread.sleep(delay);
    }
    catch (Exception e) {}
  }
  
  
  /**
   * Shows the Splash during the specified time (in milliseconds) and then
   * hides it.
   * @param delay the delay in milliseconds
   */
  public void dispose(int delay) {
    dispose();
    Splash s = new Splash(delay);
    s.dispose();
  }

  
  /**
   * This class loads and shows a picture, that can be either in the same 
   * jar file than the program or not. If the picture is smaller than the 
   * available space, it will be centered. If the picture is bigger than
   * the available space, a zoom will be applied in order to fit exactly 
   * the space.
   */ 
  class SplashPicture extends JPanel {
    Image img;      
  
  
    public SplashPicture(String file) {
      img = new ImageIcon(getClass().getResource(file)).getImage();
      repaint(); 
    }
    
  
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (img == null) return;
      int w = img.getWidth(this);  
      int h = img.getHeight(this);
      boolean zoom = (w > getWidth() || h > getHeight());
      if (zoom) g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
      else g.drawImage(img, (getWidth()-w)/2, (getHeight()-h)/2, this);
    }
  }
}