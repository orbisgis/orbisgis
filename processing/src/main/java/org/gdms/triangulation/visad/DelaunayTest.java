package org.gdms.triangulation.visad;

//
// DelaunayTest.java
//

/*
VisAD system for interactive analysis and visualization of numerical
data.  Copyright (C) 1996 - 2008 Bill Hibbard, Curtis Rueden, Tom
Rink, Dave Glowacki, Steve Emmerson, Tom Whittaker, Don Murray, and
Tommy Jasmin.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Library General Public
License as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Library General Public License for more details.

You should have received a copy of the GNU Library General Public
License along with this library; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA 02111-1307, USA
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;

/**
   DelaunayTest provides a graphical demonstration of implemented
   Delaunay triangulation algorithms, in 2-D or 3-D.
*/
public class DelaunayTest {

  public static final int CLARKSON = 1;
  public static final int WATSON = 2;
  public static final int FAST = 3;

  public static final int NONE = 1;
  public static final int BOXES = 2;
  public static final int TRIANGLES = 3;
  public static final int VERTICES = 4;

  /** Run 'java DelaunayTest' for usage instructions */
  public static void main(String[] argv) throws Exception,
                                                RemoteException {
    boolean problem = false;
    int numpass = 0;
    int dim = 0;
    int points = 0;
    int type = 0;
    int l = 1;
    boolean test = false;
    if (argv.length < 3) problem = true;
    else {
      try {
        dim = Integer.parseInt(argv[0]);
        points = Integer.parseInt(argv[1]);
        type = Integer.parseInt(argv[2]);
        if (argv.length > 3) l = Integer.parseInt(argv[3]);
        test = argv.length > 4;
        if (dim < 2 || dim > 3 || points < 1 || type < 1 || l < 1 || l > 4) {
          problem = true;
        }
        if (dim == 3 && type > 2) {
          System.out.println("Only Clarkson and Watson support " +
                             "3-D triangulation.\n");
          System.exit(2);
        }
      }
      catch (NumberFormatException exc) {
        problem = true;
      }
    }
    if (problem) {
      System.out.println("Usage:\n" +
        "   java DelaunayTest dim points type [label] [test]\n" +
        "dim    = The dimension of the triangulation\n" +
        "         2 = 2-D\n" +
        "         3 = 3-D\n" +
        "points = The number of points to triangulate.\n" +
        "type   = The triangulation method to use:\n" +
        "         1 = Clarkson\n" +
        "         2 = Watson\n" +
        "         3 = Fast\n" +
        "     X + 3 = Fast with X improvement passes\n" +
        "label  = How to label the diagram:\n" +
        "         1 = No labels (default)\n" +
        "         2 = Vertex boxes\n" +
        "         3 = Triangle numbers\n" +
        "         4 = Vertex numbers\n" +
        "test   = Whether to test the triangulation (default: no)\n");
      System.exit(1);
    }
    if (type > 3) {
      numpass = type - 3;
      type = 3;
    }

    float[][] samples = null;
    if (dim == 2) samples = new float[2][points];
    else samples = new float[3][points];

    float[] samp0 = samples[0];
    float[] samp1 = samples[1];
    float[] samp2 = null;
    if (dim == 3) samp2 = samples[2];

    for (int i=0; i<points; i++) {
      samp0[i] = (float) (500 * Math.random());
      samp1[i] = (float) (500 * Math.random());
    }
    if (dim == 3) {
      for (int i=0; i<points; i++) {
        samp2[i] = (float) (500 * Math.random());
      }
    }
    visTriang(makeTriang(samples, type, numpass, test), samples, l);
  }

  /**
   * Triangulates the given samples according to the specified algorithm.
   *
   * @param type One of CLARKSON, WATSON, FAST
   * @param numpass Number of improvement passes
   * @param test Whether to test the triangulation for errors
   */
  public static Delaunay makeTriang(float[][] samples, int type,
    int numpass, boolean test) throws Exception, RemoteException
  {
    int dim = samples.length;
    int points = samples[0].length;
    System.out.print("Triangulating " + points + " points " +
                     "in " + dim + "-D with ");

    long start = 0;
    long end = 0;
    Delaunay delaun = null;
    if (type == CLARKSON) {
      System.out.println("the Clarkson algorithm.");
      start = System.currentTimeMillis();
      delaun = (Delaunay) new DelaunayClarkson(samples);
      end = System.currentTimeMillis();
    }
    else if (type == WATSON) {
      System.out.println("the Watson algorithm.");
      start = System.currentTimeMillis();
      delaun = (Delaunay) new DelaunayWatson(samples);
      end = System.currentTimeMillis();
    }
    
    float time = (end - start) / 1000f;
    System.out.println("Triangulation took " + time + " seconds.");
    if (numpass > 0) {
      System.out.println("Improving samples: " + numpass + " pass" +
                         (numpass > 1 ? "es..." : "..."));
      start = System.currentTimeMillis();
      delaun.improve(samples, numpass);
      end = System.currentTimeMillis();
      time = (end - start) / 1000f;
      System.out.println("Improvement took " + time + " seconds.");
    }
    if (test) {
      System.out.print("Testing triangulation integrity...");
      if (delaun.test(samples)) System.out.println("OK");
      else System.out.println("FAILED!");
    }
    return delaun;
  }

  /**
   * Displays the results for the given Delaunay triangulation of the
   * specified samples in a window.
   *
   * @param delaun The triangulation to visualize
   * @param samples The samples corresponding to the triangulation
   * @param label One of NONE, BOXES, TRIANGLES, VERTICES
   */
  public static void visTriang(Delaunay delaun, float[][] samples,
    int labels) throws Exception, RemoteException
  {
    int dim = samples.length;
    int points = samples[0].length;

    // set up final variables
    final int label = labels;
    final int[][] tri = delaun.Tri;
    final int[][] edges = delaun.Edges;
    final int numedges = delaun.NumEdges;

    // set up frame
    JFrame frame = new JFrame();
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });

    float[] samp0 = samples[0];
    float[] samp1 = samples[1];
    float[] samp2 = null;
    if (dim == 3) samp2 = samples[2];

    if (dim == 2) {
      // set up GUI components in 2-D
      final float[] s0 = samp0;
      final float[] s1 = samp1;
      JComponent jc = new JComponent() {
        public void paint(Graphics gr) {

          // draw triangles
          for (int i=0; i<tri.length; i++) {
            int[] t = tri[i];
            gr.drawLine((int) s0[t[0]], (int) s1[t[0]],
                        (int) s0[t[1]], (int) s1[t[1]]);
            gr.drawLine((int) s0[t[1]], (int) s1[t[1]],
                        (int) s0[t[2]], (int) s1[t[2]]);
            gr.drawLine((int) s0[t[2]], (int) s1[t[2]],
                        (int) s0[t[0]], (int) s1[t[0]]);
          }

          // draw labels if specified
          if (label == 2) {        // vertex boxes
            for (int i=0; i<s0.length; i++) {
              gr.drawRect((int) s0[i]-2, (int) s1[i]-2, 4, 4);
            }
          }
          else if (label == 3) {   // triangle numbers
            for (int i=0; i<tri.length; i++) {
              int t0 = tri[i][0];
              int t1 = tri[i][1];
              int t2 = tri[i][2];
              int avgX = (int) ((s0[t0] + s0[t1] + s0[t2])/3);
              int avgY = (int) ((s1[t0] + s1[t1] + s1[t2])/3);
              gr.drawString(String.valueOf(i), avgX-4, avgY);
            }
          }
          else if (label == 4) {   // vertex numbers
            for (int i=0; i<s0.length; i++) {
              gr.drawString("" + i, (int) s0[i], (int) s1[i]);
            }
          }
        }
      };
      frame.getContentPane().add(jc);
    }
    else {
     //Do nothing if 3D
    }
    frame.setSize(new Dimension(510, 530));
    frame.setTitle("Triangulation results");
    frame.setVisible(true);
  }

}

