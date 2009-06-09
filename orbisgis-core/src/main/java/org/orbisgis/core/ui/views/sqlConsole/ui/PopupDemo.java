package org.orbisgis.core.ui.views.sqlConsole.ui;
/*
 * Copyright (c) Ian F. Darwin, http://www.darwinsys.com/, 1996-2002.
 * All rights reserved. Software written by Ian F. Darwin and others.
 * $Id: LICENSE,v 1.8 2004/02/09 03:33:38 ian Exp $
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * Java, the Duke mascot, and all variants of Sun's Java "steaming coffee
 * cup" logo are trademarks of Sun Microsystems. Sun's, and James Gosling's,
 * pioneering role in inventing and promulgating (and standardizing) the Java 
 * language and environment is gratefully acknowledged.
 * 
 * The pioneering role of Dennis Ritchie and Bjarne Stroustrup, of AT&T, for
 * inventing predecessor languages C and C++ is also gratefully acknowledged.
 */

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

/**
 * Provide a pop-up menu using a Frame. On most platforms, changing the mouse
 * "settings" changes how the isPopupTrigger() method behaves instantly - which
 * is as it should be!
 */
public class PopupDemo extends Frame {
  /** "main" method - for testing. */
  public static void main(String[] av) {
    new PopupDemo("Hello").setVisible(true);
  }

  /** Construct the main program */
  public PopupDemo(String title) {
    super(title);

    setLayout(new FlowLayout());
    add(new PopupContainer("Hello, and welcome to the world of Java"));
    pack();
    setVisible(true);
  }
}

/*
 * A component to demonstrate use of PopupMenu. The user has to ask for the menu
 * to popup in Java's platform-dependant way (e.g., right mouse click on X
 * Windows, MS-Windows).
 * 
 * Alternately, you could watch for keypress events and provide your own
 * platform-independant keyboard popup menu character such as M for Menu (not
 * CTRL/M; Mac's don't have a CTRL key).
 */

class PopupContainer extends Component {
  PopupMenu m;

  PopupContainer(String s) {
    m = new PopupMenu(s);
    m.add(new MenuItem("Open"));
    m.add(new MenuItem("Close"));
    MenuItem qB;
    m.add(qB = new MenuItem("Exit"));
    class Quitter implements ActionListener {
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    }
    qB.addActionListener(new Quitter());

    add(m); // add Popup to Component

    enableEvents(AWTEvent.MOUSE_EVENT_MASK);
  }

  public void processMouseEvent(MouseEvent me) {
    System.err.println("MouseEvent: " + me);
    if (me.isPopupTrigger())
      m.show(this, me.getX(), me.getY());
    else
      super.processMouseEvent(me);
  };

  /** Compute our minimum size */
  public Dimension getMinimumSize() {
    return new Dimension(200, 200);
  }

  final int PREF_PAD = 10;

  /** Computer our best size */
  public Dimension getPreferredSize() {
    Dimension d = getMinimumSize();
    return new Dimension(d.width + PREF_PAD, d.height + PREF_PAD);
  }

  /** Computer our maximum allowed size */
  public Dimension getMaximumSize() {
    Dimension d = getMinimumSize();
    return new Dimension(d.width * 2, d.height * 2);
  }
}
