package org.orbisgis.core.ui.views.sqlConsole.ui;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class PopUpColorMenu implements ActionListener {
  Component selectedComponent;

  public PopUpColorMenu() {
    JFrame frame = new JFrame();

    final JPopupMenu colorMenu = new JPopupMenu("Color");
    colorMenu.add(makeMenuItem("Red"));
    colorMenu.add(makeMenuItem("Green"));
    colorMenu.add(makeMenuItem("Blue"));

    MouseListener mouseListener = new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        checkPopup(e);
      }

      public void mouseClicked(MouseEvent e) {
        checkPopup(e);
      }

      public void mouseReleased(MouseEvent e) {
        checkPopup(e);
      }

      private void checkPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
          selectedComponent = e.getComponent();
          colorMenu.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    };

    frame.setLayout(new FlowLayout());
    JButton button = new JButton("Uno");
    button.addMouseListener(mouseListener);
    frame.add(button);
    button = new JButton("Due");
    button.addMouseListener(mouseListener);
    frame.add(button);
    button = new JButton("Tre");
    button.addMouseListener(mouseListener);
    frame.add(button);

    frame.getContentPane().addMouseListener(mouseListener);

    frame.setSize(200, 50);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }

  public void actionPerformed(ActionEvent e) {
    String color = e.getActionCommand();
    if (color.equals("Red"))
      selectedComponent.setBackground(Color.red);
    else if (color.equals("Green"))
      selectedComponent.setBackground(Color.green);
    else if (color.equals("Blue"))
      selectedComponent.setBackground(Color.blue);
  }

  private JMenuItem makeMenuItem(String label) {
    JMenuItem item = new JMenuItem(label);
    item.addActionListener(this);
    return item;
  }

  public static void main(String[] args) {
    new PopUpColorMenu();
  }
}