/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
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
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
//
// DelaunayClarkson.java
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

package org.contrib.algorithm.triangulation.visad;


/* The Delaunay triangulation algorithm in this class
 * is originally from hull by Ken Clarkson:
 *
 * Ken Clarkson wrote this.  Copyright (c) 1995 by AT&T..
 * Permission to use, copy, modify, and distribute this software for any
 * purpose without fee is hereby granted, provided that this entire notice
 * is included in all copies of any software which is or includes a copy
 * or modification of this software and in all copies of the supporting
 * documentation for such software.
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, NEITHER THE AUTHORS NOR AT&T MAKE ANY
 * REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY
 * OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */

/**
   DelaunayClarkson represents an O(N*logN) method
   with high overhead to find the Delaunay triangulation
   of a set of samples of R^DomainDimension.<P>
*/
public class DelaunayClarkson extends Delaunay {

/* ******* BEGINNING OF CONVERTED HULL CODE ******* */

  // <<<< Constants >>>>
  private static final double DBL_MANT_DIG = 53;
  private static final double FLT_RADIX = 2;
  private static final double DBL_EPSILON = 2.2204460492503131E-16;
  private static final double ln2 = Math.log(2);


  // <<<< Variables >>>>
  /* we need to have two indices for every pointer into basis_s and
     simplex arrays, because they are two-dimensional arrays of
     blocks.  ( _bn = block number ) */

  // for the pseudo-pointers
  private static final int INFINITY = -2;      // replaces infinity
  private static final int NOVAL = -1;         // replaces null

  private float[][] site_blocks;        // copy of samples array
  private int[][]   a3s;                // output array
  private int       a3size;             // output array maximum size
  private int       nts = 0;            // # output objects

  private static final int max_blocks = 10000; // max # basis/simplex blocks
  private static final int Nobj = 10000;
  private static final int MAXDIM = 8;         // max dimension

  private int    dim;
  private int    p;
  private long   pnum;
  private int    rdim,          // region dimension
                 cdim;          // # sites currently specifying region
  private int    exact_bits;
  private double b_err_min,
                 b_err_min_sq;
  private double ldetbound = 0;
  private int    failcount = 0;          // static: reduce_inner
  private int    lscale;                 // static: reduce_inner
  private double max_scale;              // static: reduce_inner
  private float  Sb;                     // static: reduce_inner
  private int    nsb = 0;                // # simplex blocks
  private int    nbb = 0;                // # basis_s blocks
  private int    ss = MAXDIM;            // static: search
  private int    ss2 = 2000;             // static: visit_triang
  private long   vnum = -1;              // static: visit_triang
  private int    p_neigh_vert = NOVAL;   // static: main

  // "void stuff" -- dummy variables to hold unused return information
  private int[] voidp = new int[1];
  private int[] voidp_bn = new int[1];

  // basis_s stuff
  private int[][]      bbt_next = new int[max_blocks][];
  private int[][]      bbt_next_bn = new int[max_blocks][];
  private int[][]      bbt_ref_count = new int[max_blocks][];
  private int[][]      bbt_lscale = new int[max_blocks][];
  private double[][]   bbt_sqa = new double[max_blocks][];
  private double[][]   bbt_sqb = new double[max_blocks][];
  private double[][][] bbt_vecs = new double[max_blocks][][];

  private int ttbp;
  private int ttbp_bn;
  private int ib;
  private int ib_bn;
  private int basis_s_list = NOVAL;
  private int basis_s_list_bn;
  private int pnb = NOVAL;
  private int pnb_bn;
  private int b = NOVAL;              // static: sees
  private int b_bn;

  // simplex stuff
  private int[][]   sbt_next = new int[max_blocks][];
  private int[][]   sbt_next_bn = new int[max_blocks][];
  private long[][]  sbt_visit = new long[max_blocks][];
  private short[][] sbt_mark = new short[max_blocks][];
  private int[][]   sbt_normal = new int[max_blocks][];
  private int[][]   sbt_normal_bn = new int[max_blocks][];
  private int[][]   sbt_peak_vert = new int[max_blocks][];
  private int[][]   sbt_peak_simp = new int[max_blocks][];
  private int[][]   sbt_peak_simp_bn = new int[max_blocks][];
  private int[][]   sbt_peak_basis = new int[max_blocks][];
  private int[][]   sbt_peak_basis_bn = new int[max_blocks][];
  private int[][][] sbt_neigh_vert = new int[max_blocks][][];
  private int[][][] sbt_neigh_simp = new int[max_blocks][][];
  private int[][][] sbt_neigh_simp_bn = new int[max_blocks][][];
  private int[][][] sbt_neigh_basis = new int[max_blocks][][];
  private int[][][] sbt_neigh_basis_bn = new int[max_blocks][][];

  private int   simplex_list = NOVAL;
  private int   simplex_list_bn;
  private int   ch_root;
  private int   ch_root_bn;
  private int   ns;                            // static: make_facets
  private int   ns_bn;
  private int[] st = new int[ss+MAXDIM+1];    // static: search
  private int[] st_bn = new int[ss+MAXDIM+1];
  private int[] st2 = new int[ss2+MAXDIM+1];    // static: visit_triang
  private int[] st2_bn = new int[ss2+MAXDIM+1];


  // <<<< Functions >>>>
  private int new_block_basis_s() {
    bbt_next[nbb] = new int[Nobj];
    bbt_next_bn[nbb] = new int[Nobj];
    bbt_ref_count[nbb] = new int[Nobj];
    bbt_lscale[nbb] = new int[Nobj];
    bbt_sqa[nbb] = new double[Nobj];
    bbt_sqb[nbb] = new double[Nobj];
    bbt_vecs[nbb] = new double[2*rdim][];
    for (int i=0; i<2*rdim; i++) bbt_vecs[nbb][i] = new double[Nobj];
    for (int i=0; i<Nobj; i++) {
      bbt_next[nbb][i] = i+1;
      bbt_next_bn[nbb][i] = nbb;
      bbt_ref_count[nbb][i] = 0;
      bbt_lscale[nbb][i] = 0;
      bbt_sqa[nbb][i] = 0;
      bbt_sqb[nbb][i] = 0;
      for (int j=0; j<2*rdim; j++) bbt_vecs[nbb][j][i] = 0;
    }
    bbt_next[nbb][Nobj-1] = NOVAL;
    basis_s_list = 0;
    basis_s_list_bn = nbb;
    nbb++;
    return basis_s_list;
  }

  private int reduce_inner(int v, int v_bn, int s, int s_bn, int k) {
    int q, q_bn;
    double dd,
           Sb = 0;
    double scale;

    bbt_sqa[v_bn][v] = 0;
    for (int i=0; i<rdim; i++) {
      bbt_sqa[v_bn][v] += bbt_vecs[v_bn][i][v] * bbt_vecs[v_bn][i][v];
    }
    bbt_sqb[v_bn][v] = bbt_sqa[v_bn][v];
    if (k <= 1) {
      for (int i=0; i<rdim; i++) {
        bbt_vecs[v_bn][i][v] = bbt_vecs[v_bn][rdim+i][v];
      }
      return 1;
    }
    for (int j=0; j<250; j++) {
      int    xx = rdim;
      double labound;

      for (int i=0; i<rdim; i++) {
        bbt_vecs[v_bn][i][v] = bbt_vecs[v_bn][rdim+i][v];
      }
      for (int i=k-1; i>0; i--) {
        q = sbt_neigh_basis[s_bn][i][s];
        q_bn = sbt_neigh_basis_bn[s_bn][i][s];
        dd = 0;
        for (int l=0; l<rdim; l++) {
          dd -= bbt_vecs[q_bn][l][q] * bbt_vecs[v_bn][l][v];
        }
        dd /= bbt_sqb[q_bn][q];
        for (int l=0; l<rdim; l++) {
          bbt_vecs[v_bn][l][v] += dd * bbt_vecs[q_bn][rdim+l][q];
        }
      }
      bbt_sqb[v_bn][v] = 0;
      for (int i=0; i<rdim; i++) {
        bbt_sqb[v_bn][v] += bbt_vecs[v_bn][i][v] * bbt_vecs[v_bn][i][v];
      }
      bbt_sqa[v_bn][v] = 0;
      for (int i=0; i<rdim; i++) {
        bbt_sqa[v_bn][v] += bbt_vecs[v_bn][rdim+i][v]
                          * bbt_vecs[v_bn][rdim+i][v];
      }

      if (2*bbt_sqb[v_bn][v] >= bbt_sqa[v_bn][v]) return 1;

      // scale up vector
      if (j < 10) {
        labound = Math.floor(Math.log(bbt_sqa[v_bn][v])/ln2) / 2;
        max_scale = exact_bits-labound-0.66*(k-2)-1;
        if (max_scale < 1) max_scale = 1;

        if (j == 0) {

          ldetbound = 0;
          Sb = 0;
          for (int l=k-1; l>0; l--) {
            q = sbt_neigh_basis[s_bn][l][s];
            q_bn = sbt_neigh_basis_bn[s_bn][l][s];
            Sb += bbt_sqb[q_bn][q];
            ldetbound += Math.floor(Math.log(bbt_sqb[q_bn][q])/ln2) / 2 + 1;
            ldetbound -= bbt_lscale[q_bn][q];
          }
        }
      }
      if (ldetbound - bbt_lscale[v_bn][v]
        + Math.floor(Math.log(bbt_sqb[v_bn][v])/ln2) / 2 + 1 < 0) {
        scale = 0;
      }
      else {
        lscale = (int) (Math.log(2*Sb/(bbt_sqb[v_bn][v]
                                     + bbt_sqa[v_bn][v]*b_err_min))/ln2) / 2;
        if (lscale > max_scale) lscale = (int) max_scale;
        else if (lscale < 0) lscale = 0;
        bbt_lscale[v_bn][v] += lscale;
        scale = (lscale < 20) ? 1 << lscale : Math.pow(2, lscale);
      }

      while (xx < 2*rdim) bbt_vecs[v_bn][xx++][v] *= scale;

      for (int i=k-1; i>0; i--) {
        q = sbt_neigh_basis[s_bn][i][s];
        q_bn = sbt_neigh_basis_bn[s_bn][i][s];
        dd = 0;
        for (int l=0; l<rdim; l++) {
          dd -= bbt_vecs[q_bn][l][q] * bbt_vecs[v_bn][rdim+l][v];
        }
        dd /= bbt_sqb[q_bn][q];
        dd = Math.floor(dd+0.5);
        for (int l=0; l<rdim; l++) {
          bbt_vecs[v_bn][rdim+l][v] += dd * bbt_vecs[q_bn][rdim+l][q];
        }
      }
    }
    if (failcount++ < 10) System.out.println("reduce_inner failed!");
    return 0;
  }

  private int reduce(int[] v, int[] v_bn, int rp, int s, int s_bn, int k) {
    if (v[0] == NOVAL) {
      v[0] = basis_s_list != NOVAL ? basis_s_list : new_block_basis_s();
      v_bn[0] = basis_s_list_bn;
      basis_s_list = bbt_next[v_bn[0]][v[0]];
      basis_s_list_bn = bbt_next_bn[v_bn[0]][v[0]];
      bbt_ref_count[v_bn[0]][v[0]] = 1;
    }
    else bbt_lscale[v_bn[0]][v[0]] = 0;
    if (rp == INFINITY) {
      bbt_next[v_bn[0]][v[0]] = bbt_next[ib_bn][ib];
      bbt_next_bn[v_bn[0]][v[0]] = bbt_next_bn[ib_bn][ib];
      bbt_ref_count[v_bn[0]][v[0]] = bbt_ref_count[ib_bn][ib];
      bbt_lscale[v_bn[0]][v[0]] = bbt_lscale[ib_bn][ib];
      bbt_sqa[v_bn[0]][v[0]] = bbt_sqa[ib_bn][ib];
      bbt_sqb[v_bn[0]][v[0]] = bbt_sqb[ib_bn][ib];
      for (int i=0; i<2*rdim; i++) {
        bbt_vecs[v_bn[0]][i][v[0]] = bbt_vecs[ib_bn][i][ib];
      }
    }
    else {
      double sum = 0;
      int sbt_nv = sbt_neigh_vert[s_bn][0][s];
      if (sbt_nv == INFINITY) {
        for (int i=0; i<dim; i++) {
          bbt_vecs[v_bn[0]][i+rdim][v[0]] = bbt_vecs[v_bn[0]][i][v[0]]
            = (double) site_blocks[i][rp];
        }
      }
      else {
        for (int i=0; i<dim; i++) {
          bbt_vecs[v_bn[0]][i+rdim][v[0]] = bbt_vecs[v_bn[0]][i][v[0]]
            = (double) (site_blocks[i][rp] - site_blocks[i][sbt_nv]);
        }
      }
      for (int i=0; i<dim; i++) {
        sum += bbt_vecs[v_bn[0]][i][v[0]] * bbt_vecs[v_bn[0]][i][v[0]];
      }
      bbt_vecs[v_bn[0]][2*rdim-1][v[0]] = sum;
      bbt_vecs[v_bn[0]][rdim-1][v[0]] = sum;
    }
    return reduce_inner(v[0], v_bn[0], s, s_bn, k);
  }

  private void get_basis_sede(int s, int s_bn) {
    int   k=1;
    int   q, q_bn;
    int[] curt = new int[1];
    int[] curt_bn = new int[1];

    if (sbt_neigh_vert[s_bn][0][s] == INFINITY && cdim > 1) {
      int t_vert, t_simp, t_simp_bn, t_basis, t_basis_bn;
      t_vert = sbt_neigh_vert[s_bn][0][s];
      t_simp = sbt_neigh_simp[s_bn][0][s];
      t_simp_bn = sbt_neigh_simp_bn[s_bn][0][s];
      t_basis = sbt_neigh_basis[s_bn][0][s];
      t_basis_bn = sbt_neigh_basis_bn[s_bn][0][s];
      sbt_neigh_vert[s_bn][0][s] = sbt_neigh_vert[s_bn][k][s];
      sbt_neigh_simp[s_bn][0][s] = sbt_neigh_simp[s_bn][k][s];
      sbt_neigh_simp_bn[s_bn][0][s] = sbt_neigh_simp_bn[s_bn][k][s];
      sbt_neigh_basis[s_bn][0][s] = sbt_neigh_basis[s_bn][k][s];
      sbt_neigh_basis_bn[s_bn][0][s] = sbt_neigh_basis_bn[s_bn][k][s];
      sbt_neigh_vert[s_bn][k][s] = t_vert;
      sbt_neigh_simp[s_bn][k][s] = t_simp;
      sbt_neigh_simp_bn[s_bn][k][s] = t_simp_bn;
      sbt_neigh_basis[s_bn][k][s] = t_basis;
      sbt_neigh_basis_bn[s_bn][k][s] = t_basis_bn;

      q = sbt_neigh_basis[s_bn][0][s];
      q_bn = sbt_neigh_basis_bn[s_bn][0][s];
      if ((q != NOVAL) && --bbt_ref_count[q_bn][q] == 0) {
        bbt_next[q_bn][q] = basis_s_list;
        bbt_next_bn[q_bn][q] = basis_s_list_bn;
        bbt_ref_count[q_bn][q] = 0;
        bbt_lscale[q_bn][q] = 0;
        bbt_sqa[q_bn][q] = 0;
        bbt_sqb[q_bn][q] = 0;
        for (int j=0; j<2*rdim; j++) bbt_vecs[q_bn][j][q] = 0;
        basis_s_list = q;
        basis_s_list_bn = q_bn;
      }

      sbt_neigh_basis[s_bn][0][s] = ttbp;
      sbt_neigh_basis_bn[s_bn][0][s] = ttbp_bn;
      bbt_ref_count[ttbp_bn][ttbp]++;
    }
    else {
      if (sbt_neigh_basis[s_bn][0][s] == NOVAL) {
        sbt_neigh_basis[s_bn][0][s] = ttbp;
        sbt_neigh_basis_bn[s_bn][0][s] = ttbp_bn;
        bbt_ref_count[ttbp_bn][ttbp]++;
      } else while (k < cdim && sbt_neigh_basis[s_bn][k][s] != NOVAL) k++;
    }
    while (k < cdim) {
      q = sbt_neigh_basis[s_bn][k][s];
      q_bn = sbt_neigh_basis_bn[s_bn][k][s];
      if (q != NOVAL && --bbt_ref_count[q_bn][q] == 0) {
        bbt_next[q_bn][q] = basis_s_list;
        bbt_next_bn[q_bn][q] = basis_s_list_bn;
        bbt_ref_count[q_bn][q] = 0;
        bbt_lscale[q_bn][q] = 0;
        bbt_sqa[q_bn][q] = 0;
        bbt_sqb[q_bn][q] = 0;
        for (int j=0; j<2*rdim; j++) bbt_vecs[q_bn][j][q] = 0;
        basis_s_list = q;
        basis_s_list_bn = q_bn;
      }
      sbt_neigh_basis[s_bn][k][s] = NOVAL;
      curt[0] = sbt_neigh_basis[s_bn][k][s];
      curt_bn[0] = sbt_neigh_basis_bn[s_bn][k][s];
      reduce(curt, curt_bn, sbt_neigh_vert[s_bn][k][s], s, s_bn, k);
      sbt_neigh_basis[s_bn][k][s] = curt[0];
      sbt_neigh_basis_bn[s_bn][k][s] = curt_bn[0];
      k++;
    }
  }

  private int sees(int rp, int s, int s_bn) {
    double  dd, dds;
    int     q, q_bn, q1, q1_bn, q2, q2_bn;
    int[]   curt = new int[1];
    int[]   curt_bn = new int[1];

    if (b == NOVAL) {
      b = (basis_s_list != NOVAL) ? basis_s_list : new_block_basis_s();
      b_bn = basis_s_list_bn;
      basis_s_list = bbt_next[b_bn][b];
      basis_s_list_bn = bbt_next_bn[b_bn][b];
    }
    else bbt_lscale[b_bn][b] = 0;
    if (cdim==0) return 0;
    if (sbt_normal[s_bn][s] == NOVAL) {
      get_basis_sede(s, s_bn);
      if (rdim==3 && cdim==3) {
        sbt_normal[s_bn][s] = basis_s_list != NOVAL ? basis_s_list
                                                    : new_block_basis_s();
        sbt_normal_bn[s_bn][s] = basis_s_list_bn;
        q = sbt_normal[s_bn][s];
        q_bn = sbt_normal_bn[s_bn][s];
        basis_s_list = bbt_next[q_bn][q];
        basis_s_list_bn = bbt_next_bn[q_bn][q];
        q1 = sbt_neigh_basis[s_bn][1][s];
        q1_bn = sbt_neigh_basis_bn[s_bn][1][s];
        q2 = sbt_neigh_basis[s_bn][2][s];
        q2_bn = sbt_neigh_basis_bn[s_bn][2][s];
        bbt_ref_count[q_bn][q] = 1;
        bbt_vecs[q_bn][0][q] = bbt_vecs[q1_bn][1][q1]
                  *bbt_vecs[q2_bn][2][q2]
             - bbt_vecs[q1_bn][2][q1]
                  *bbt_vecs[q2_bn][1][q2];
        bbt_vecs[q_bn][1][q] = bbt_vecs[q1_bn][2][q1]
                  *bbt_vecs[q2_bn][0][q2]
             - bbt_vecs[q1_bn][0][q1]
                  *bbt_vecs[q2_bn][2][q2];
        bbt_vecs[q_bn][2][q] = bbt_vecs[q1_bn][0][q1]
                  *bbt_vecs[q2_bn][1][q2]
             - bbt_vecs[q1_bn][1][q1]
                  *bbt_vecs[q2_bn][0][q2];
        bbt_sqb[q_bn][q] = 0;
        for (int i=0; i<rdim; i++) bbt_sqb[q_bn][q] += bbt_vecs[q_bn][i][q]
                                                     * bbt_vecs[q_bn][i][q];
        for (int i=cdim+1; i>0; i--) {
          int m = (i > 1) ? sbt_neigh_vert[ch_root_bn][i-2][ch_root]
                          : INFINITY;
          int j;
          for (j=0; j<cdim && m != sbt_neigh_vert[s_bn][j][s]; j++);
          if (j < cdim) continue;
          if (m == INFINITY) {
            if (bbt_vecs[q_bn][2][q] > -b_err_min) continue;
          }
          else {
            if (sees(m, s, s_bn) == 0) {
              continue;
            }
          }
          bbt_vecs[q_bn][0][q] = -bbt_vecs[q_bn][0][q];
          bbt_vecs[q_bn][1][q] = -bbt_vecs[q_bn][1][q];
          bbt_vecs[q_bn][2][q] = -bbt_vecs[q_bn][2][q];
          break;
        }
      }
      else {
        for (int i=cdim+1; i>0; i--) {
          int m = (i > 1) ? sbt_neigh_vert[ch_root_bn][i-2][ch_root]
                          : INFINITY;
          int j;
          for (j=0; j<cdim && m != sbt_neigh_vert[s_bn][j][s]; j++);
          if (j < cdim) continue;
          curt[0] = sbt_normal[s_bn][s];
          curt_bn[0] = sbt_normal_bn[s_bn][s];
          reduce(curt, curt_bn, m, s, s_bn, cdim);
          q = sbt_normal[s_bn][s] = curt[0];
          q_bn = sbt_normal_bn[s_bn][s] = curt_bn[0];
          if (bbt_sqb[q_bn][q] != 0) break;
        }
      }

      for (int i=0; i<cdim; i++) {
        q = sbt_neigh_basis[s_bn][i][s];
        q_bn = sbt_neigh_basis_bn[s_bn][i][s];
        if (q != NOVAL && --bbt_ref_count[q_bn][q] == 0) {
          bbt_next[q_bn][q] = basis_s_list;
          bbt_next_bn[q_bn][q] = basis_s_list_bn;
          bbt_ref_count[q_bn][q] = 0;
          bbt_lscale[q_bn][q] = 0;
          bbt_sqa[q_bn][q] = 0;
          bbt_sqb[q_bn][q] = 0;
          for (int l=0; l<2*rdim; l++) bbt_vecs[q_bn][l][q] = 0;
          basis_s_list = q;
          basis_s_list_bn = q_bn;
        }
        sbt_neigh_basis[s_bn][i][s] = NOVAL;
      }
    }
    if (rp == INFINITY) {
      bbt_next[b_bn][b] = bbt_next[ib_bn][ib];
      bbt_next_bn[b_bn][b] = bbt_next_bn[ib_bn][ib];
      bbt_ref_count[b_bn][b] = bbt_ref_count[ib_bn][ib];
      bbt_lscale[b_bn][b] = bbt_lscale[ib_bn][ib];
      bbt_sqa[b_bn][b] = bbt_sqa[ib_bn][ib];
      bbt_sqb[b_bn][b] = bbt_sqb[ib_bn][ib];
      for (int i=0; i<2*rdim; i++) {
        bbt_vecs[b_bn][i][b] = bbt_vecs[ib_bn][i][ib];
      }
    }
    else {
      double sum = 0;
      int sbt_nv = sbt_neigh_vert[s_bn][0][s];
      if (sbt_nv == INFINITY) {
        for (int l=0; l<dim; l++) {
          bbt_vecs[b_bn][l+rdim][b] = bbt_vecs[b_bn][l][b]
            = (double) site_blocks[l][rp];
        }
      }
      else {
        for (int l=0; l<dim; l++) {
          bbt_vecs[b_bn][l+rdim][b] = bbt_vecs[b_bn][l][b]
            = (double) (site_blocks[l][rp] - site_blocks[l][sbt_nv]);
        }
      }
      for (int l=0; l<dim; l++) {
        sum += bbt_vecs[b_bn][l][b] * bbt_vecs[b_bn][l][b];
      }
      bbt_vecs[b_bn][2*rdim-1][b] = bbt_vecs[b_bn][rdim-1][b] = sum;
    }
    q = sbt_normal[s_bn][s];
    q_bn = sbt_normal_bn[s_bn][s];
    for (int i=0; i<3; i++) {
      double sum = 0;
      dd = 0;
      for (int l=0; l<rdim; l++) {
        dd += bbt_vecs[b_bn][l][b] * bbt_vecs[q_bn][l][q];
      }
      if (dd == 0.0) return 0;
      for (int l=0; l<rdim; l++) {
        sum += bbt_vecs[b_bn][l][b] * bbt_vecs[b_bn][l][b];
      }
      dds = dd*dd/bbt_sqb[q_bn][q]/sum;
      if (dds > b_err_min_sq) return (dd < 0 ? 1 : 0);
      get_basis_sede(s, s_bn);
      reduce_inner(b, b_bn, s, s_bn, cdim);
    }
    return 0;
  }

  private int new_block_simplex() {
    sbt_next[nsb] = new int[Nobj];
    sbt_next_bn[nsb] = new int[Nobj];
    sbt_visit[nsb] = new long[Nobj];
    sbt_mark[nsb] = new short[Nobj];
    sbt_normal[nsb] = new int[Nobj];
    sbt_normal_bn[nsb] = new int[Nobj];
    sbt_peak_vert[nsb] = new int[Nobj];
    sbt_peak_simp[nsb] = new int[Nobj];
    sbt_peak_simp_bn[nsb] = new int[Nobj];
    sbt_peak_basis[nsb] = new int[Nobj];
    sbt_peak_basis_bn[nsb] = new int[Nobj];
    sbt_neigh_vert[nsb] = new int[rdim][];
    sbt_neigh_simp[nsb] = new int[rdim][];
    sbt_neigh_simp_bn[nsb] = new int[rdim][];
    sbt_neigh_basis[nsb] = new int[rdim][];
    sbt_neigh_basis_bn[nsb] = new int[rdim][];
    for (int i=0; i<rdim; i++) {
      sbt_neigh_vert[nsb][i] = new int[Nobj];
      sbt_neigh_simp[nsb][i] = new int[Nobj];
      sbt_neigh_simp_bn[nsb][i] = new int[Nobj];
      sbt_neigh_basis[nsb][i] = new int[Nobj];
      sbt_neigh_basis_bn[nsb][i] = new int[Nobj];
    }
    for (int i=0; i<Nobj; i++) {
      sbt_next[nsb][i] = i+1;
      sbt_next_bn[nsb][i] = nsb;
      sbt_visit[nsb][i] = 0;
      sbt_mark[nsb][i] = 0;
      sbt_normal[nsb][i] = NOVAL;
      sbt_peak_vert[nsb][i] = NOVAL;
      sbt_peak_simp[nsb][i] = NOVAL;
      sbt_peak_basis[nsb][i] = NOVAL;
      for (int j=0; j<rdim; j++) {
        sbt_neigh_vert[nsb][j][i] = NOVAL;
        sbt_neigh_simp[nsb][j][i] = NOVAL;
        sbt_neigh_basis[nsb][j][i] = NOVAL;
      }
    }
    sbt_next[nsb][Nobj-1] = NOVAL;
    simplex_list = 0;
    simplex_list_bn = nsb;

    nsb++;
    return simplex_list;
  }

  /**
     starting at s, visit simplices t such that test(s,i,0) is true,
     and t is the i'th neighbor of s;
     apply visit function to all visited simplices;
     when visit returns nonnull, exit and return its value.
  */
  private void visit_triang_gen(int s, int s_bn, int whichfunc,
                                int[] ret, int[] ret_bn) {
    int v;
    int v_bn;
    int t;
    int t_bn;
    int tms = 0;

    vnum--;
    if (s != NOVAL) {
      st2[tms] = s;
      st2_bn[tms] = s_bn;
      tms++;
    }
    while (tms != 0) {
      if (tms > ss2) {
        // JAVA: efficiency issue: how much is this stack hammered?
        ss2 += ss2;
        int[] newst2 = new int[ss2+MAXDIM+1];
        int[] newst2_bn = new int[ss2+MAXDIM+1];
        System.arraycopy(st2, 0, newst2, 0, st2.length);
        System.arraycopy(st2_bn, 0, newst2_bn, 0, st2_bn.length);
        st2 = newst2;
        st2_bn = newst2_bn;
      }
      tms--;
      t = st2[tms];
      t_bn = st2_bn[tms];
      if (t == NOVAL || sbt_visit[t_bn][t] == vnum) continue;
      sbt_visit[t_bn][t] = vnum;
      if (whichfunc == 1) {
        if (sbt_peak_vert[t_bn][t] == NOVAL) {
          v = t;
          v_bn = t_bn;
        }
        else {
          v = NOVAL;
          v_bn = NOVAL;
        }
        if (v != NOVAL) {
          ret[0] = v;
          ret_bn[0] = v_bn;
          return;
        }
      }
      else {
        int[] vfp = new int[cdim];

        if (t != NOVAL) {
          for (int j=0; j<cdim; j++) vfp[j] = sbt_neigh_vert[t_bn][j][t];
          for (int j=0; j<cdim; j++) {
            a3s[j][nts] = (vfp[j] == INFINITY) ? -1 : vfp[j];
          }
          nts++;
          if (nts > a3size) {
            // JAVA: efficiency issue, hammering an array
            a3size += a3size;
            int[][] newa3s = new int[rdim][a3size+MAXDIM+1];
            for (int i=0; i<rdim; i++) {
              System.arraycopy(a3s[i], 0, newa3s[i], 0, a3s[i].length);
            }
            a3s = newa3s;
          }
        }
      }
      for (int i=0; i<cdim; i++) {
        int j = sbt_neigh_simp[t_bn][i][t];
        int j_bn = sbt_neigh_simp_bn[t_bn][i][t];
        if ((j != NOVAL) && sbt_visit[j_bn][j] != vnum) {
          st2[tms] = j;
          st2_bn[tms] = j_bn;
          tms++;
        }
      }
    }
    ret[0] = NOVAL;
  }

  /**
     make neighbor connections between newly created simplices incident to p.
  */
  private void connect(int s, int s_bn) {
    int xb, xf;
    int sb, sb_bn;
    int sf, sf_bn;
    int tf, tf_bn;
    int ccj, ccj_bn;
    int xfi;

    if (s == NOVAL) return;
    for (int i=0; (sbt_neigh_vert[s_bn][i][s] != p) && (i<cdim); i++);
    if (sbt_visit[s_bn][s] == pnum) return;
    sbt_visit[s_bn][s] = pnum;
    ccj = sbt_peak_simp[s_bn][s];
    ccj_bn = sbt_peak_simp_bn[s_bn][s];
    for (xfi=0; (sbt_neigh_simp[ccj_bn][xfi][ccj] != s
              || sbt_neigh_simp_bn[ccj_bn][xfi][ccj] != s_bn)
                     && (xfi<cdim); xfi++);
    for (int i=0; i<cdim; i++) {
      int l;
      if (p == sbt_neigh_vert[s_bn][i][s]) continue;
      sb = sbt_peak_simp[s_bn][s];
      sb_bn = sbt_peak_simp_bn[s_bn][s];
      sf = sbt_neigh_simp[s_bn][i][s];
      sf_bn = sbt_neigh_simp_bn[s_bn][i][s];
      xf = sbt_neigh_vert[ccj_bn][xfi][ccj];
      if (sbt_peak_vert[sf_bn][sf] == NOVAL) {  // are we done already?
        for (l=0; (sbt_neigh_vert[ccj_bn][l][ccj]
                != sbt_neigh_vert[s_bn][i][s]) && (l<cdim); l++);
        sf = sbt_neigh_simp[ccj_bn][l][ccj];
        sf_bn = sbt_neigh_simp_bn[ccj_bn][l][ccj];
        if (sbt_peak_vert[sf_bn][sf] != NOVAL) continue;
      } else do {
        xb = xf;
        for (l=0; (sbt_neigh_simp[sf_bn][l][sf] != sb
                || sbt_neigh_simp_bn[sf_bn][l][sf] != sb_bn)
                && l<cdim; l++);
        xf = sbt_neigh_vert[sf_bn][l][sf];
        sb = sf;
        sb_bn = sf_bn;
        for (l=0; (sbt_neigh_vert[sb_bn][l][sb] != xb) && (l<cdim); l++);
        tf = sbt_neigh_simp[sf_bn][l][sf];
        tf_bn = sbt_neigh_simp_bn[sf_bn][l][sf];
        sf = tf;
        sf_bn = tf_bn;
      } while (sbt_peak_vert[sf_bn][sf] != NOVAL);

      sbt_neigh_simp[s_bn][i][s] = sf;
      sbt_neigh_simp_bn[s_bn][i][s] = sf_bn;
      for (l=0; (sbt_neigh_vert[sf_bn][l][sf] != xf) && (l<cdim); l++);
      sbt_neigh_simp[sf_bn][l][sf] = s;
      sbt_neigh_simp_bn[sf_bn][l][sf] = s_bn;

      connect(sf, sf_bn);
    }

  }

  /**
     visit simplices s with sees(p,s), and make a facet for every neighbor
     of s not seen by p.
  */
  private void make_facets(int seen, int seen_bn, int[] ret, int[] ret_bn) {
    int n, n_bn;
    int q, q_bn;
    int j;

    if (seen == NOVAL) {
      ret[0] = NOVAL;
      return;
    }
    sbt_peak_vert[seen_bn][seen] = p;

    for (int i=0; i<cdim; i++) {
      n = sbt_neigh_simp[seen_bn][i][seen];
      n_bn = sbt_neigh_simp_bn[seen_bn][i][seen];

      if (pnum != sbt_visit[n_bn][n]) {
        sbt_visit[n_bn][n] = pnum;
        if (sees(p, n, n_bn) != 0) make_facets(n, n_bn, voidp, voidp_bn);
      }
      if (sbt_peak_vert[n_bn][n] != NOVAL) continue;

      ns = (simplex_list != NOVAL) ? simplex_list : new_block_simplex();
      ns_bn = simplex_list_bn;
      simplex_list = sbt_next[ns_bn][ns];
      simplex_list_bn = sbt_next_bn[ns_bn][ns];
      sbt_next[ns_bn][ns] = sbt_next[seen_bn][seen];
      sbt_next_bn[ns_bn][ns] = sbt_next_bn[seen_bn][seen];
      sbt_visit[ns_bn][ns] = sbt_visit[seen_bn][seen];
      sbt_mark[ns_bn][ns] = sbt_mark[seen_bn][seen];
      sbt_normal[ns_bn][ns] = sbt_normal[seen_bn][seen];
      sbt_normal_bn[ns_bn][ns] = sbt_normal_bn[seen_bn][seen];
      sbt_peak_vert[ns_bn][ns] = sbt_peak_vert[seen_bn][seen];
      sbt_peak_simp[ns_bn][ns] = sbt_peak_simp[seen_bn][seen];
      sbt_peak_simp_bn[ns_bn][ns] = sbt_peak_simp_bn[seen_bn][seen];
      sbt_peak_basis[ns_bn][ns] = sbt_peak_basis[seen_bn][seen];
      sbt_peak_basis_bn[ns_bn][ns] = sbt_peak_basis_bn[seen_bn][seen];
      for (j=0; j<rdim; j++) {
        sbt_neigh_vert[ns_bn][j][ns] = sbt_neigh_vert[seen_bn][j][seen];
        sbt_neigh_simp[ns_bn][j][ns] = sbt_neigh_simp[seen_bn][j][seen];
        sbt_neigh_simp_bn[ns_bn][j][ns]
                       = sbt_neigh_simp_bn[seen_bn][j][seen];
        sbt_neigh_basis[ns_bn][j][ns] = sbt_neigh_basis[seen_bn][j][seen];
        sbt_neigh_basis_bn[ns_bn][j][ns]
                        = sbt_neigh_basis_bn[seen_bn][j][seen];
      }

      for (j=0; j<cdim; j++) {
        q = sbt_neigh_basis[seen_bn][j][seen];
        q_bn = sbt_neigh_basis_bn[seen_bn][j][seen];
        if (q != NOVAL) bbt_ref_count[q_bn][q]++;
      }

      sbt_visit[ns_bn][ns] = 0;
      sbt_peak_vert[ns_bn][ns] = NOVAL;
      sbt_normal[ns_bn][ns] = NOVAL;
      sbt_peak_simp[ns_bn][ns] = seen;
      sbt_peak_simp_bn[ns_bn][ns] = seen_bn;

      q = sbt_neigh_basis[ns_bn][i][ns];
      q_bn = sbt_neigh_basis_bn[ns_bn][i][ns];
      if (q != NOVAL && --bbt_ref_count[q_bn][q] == 0) {
        bbt_next[q_bn][q] = basis_s_list;
        bbt_next_bn[q_bn][q] = basis_s_list_bn;
        bbt_ref_count[q_bn][q] = 0;
        bbt_lscale[q_bn][q] = 0;
        bbt_sqa[q_bn][q] = 0;
        bbt_sqb[q_bn][q] = 0;
        for (int l=0; l<2*rdim; l++) bbt_vecs[q_bn][l][q] = 0;
        basis_s_list = q;
        basis_s_list_bn = q_bn;
      }
      sbt_neigh_basis[ns_bn][i][ns] = NOVAL;

      sbt_neigh_vert[ns_bn][i][ns] = p;
      for (j=0; (sbt_neigh_simp[n_bn][j][n] != seen
                  || sbt_neigh_simp_bn[n_bn][j][n] != seen_bn)
                  && j<cdim; j++);
      sbt_neigh_simp[seen_bn][i][seen] = sbt_neigh_simp[n_bn][j][n] = ns;
      sbt_neigh_simp_bn[seen_bn][i][seen] = ns_bn;
      sbt_neigh_simp_bn[n_bn][j][n] = ns_bn;
    }
    ret[0] = ns;
    ret_bn[0] = ns_bn;
  }

  /**
     p lies outside flat containing previous sites;
     make p a vertex of every current simplex, and create some new simplices.
  */
  private void extend_simplices(int s, int s_bn, int[] ret, int[] ret_bn) {
    int q, q_bn;
    int ns, ns_bn;

    if (sbt_visit[s_bn][s] == pnum) {
      if (sbt_peak_vert[s_bn][s] != NOVAL) {
        ret[0] = sbt_neigh_simp[s_bn][cdim-1][s];
        ret_bn[0] = sbt_neigh_simp_bn[s_bn][cdim-1][s];
      }
      else {
        ret[0] = s;
        ret_bn[0] = s_bn;
      }
      return;
    }
    sbt_visit[s_bn][s] = pnum;
    sbt_neigh_vert[s_bn][cdim-1][s] = p;
    q = sbt_normal[s_bn][s];
    q_bn = sbt_normal_bn[s_bn][s];
    if (q != NOVAL && --bbt_ref_count[q_bn][q] == 0) {
      bbt_next[q_bn][q] = basis_s_list;
      bbt_next_bn[q_bn][q] = basis_s_list_bn;
      bbt_ref_count[q_bn][q] = 0;
      bbt_lscale[q_bn][q] = 0;
      bbt_sqa[q_bn][q] = 0;
      bbt_sqb[q_bn][q] = 0;
      for (int j=0; j<2*rdim; j++) bbt_vecs[q_bn][j][q] = 0;
      basis_s_list = q;
      basis_s_list_bn = q_bn;
    }
    sbt_normal[s_bn][s] = NOVAL;

    q = sbt_neigh_basis[s_bn][0][s];
    q_bn = sbt_neigh_basis_bn[s_bn][0][s];
    if (q != NOVAL && --bbt_ref_count[q_bn][q] == 0) {
      bbt_next[q_bn][q] = basis_s_list;
      bbt_ref_count[q_bn][q] = 0;
      bbt_lscale[q_bn][q] = 0;
      bbt_sqa[q_bn][q] = 0;
      bbt_sqb[q_bn][q] = 0;
      for (int j=0; j<2*rdim; j++) bbt_vecs[q_bn][j][q] = 0;

      basis_s_list = q;
      basis_s_list_bn = q_bn;
    }
    sbt_neigh_basis[s_bn][0][s] = NOVAL;

    if (sbt_peak_vert[s_bn][s] == NOVAL) {
      int[] esretp = new int[1];
      int[] esretp_bn = new int[1];
      extend_simplices(sbt_peak_simp[s_bn][s],
                       sbt_peak_simp_bn[s_bn][s], esretp, esretp_bn);
      sbt_neigh_simp[s_bn][cdim-1][s] = esretp[0];
      sbt_neigh_simp_bn[s_bn][cdim-1][s] = esretp_bn[0];
      ret[0] = s;
      ret_bn[0] = s_bn;
      return;
    }
    else {
      ns = (simplex_list != NOVAL) ? simplex_list : new_block_simplex();
      ns_bn = simplex_list_bn;
      simplex_list = sbt_next[ns_bn][ns];
      simplex_list_bn = sbt_next_bn[ns_bn][ns];
      sbt_next[ns_bn][ns] = sbt_next[s_bn][s];
      sbt_next_bn[ns_bn][ns] = sbt_next_bn[s_bn][s];
      sbt_visit[ns_bn][ns] = sbt_visit[s_bn][s];
      sbt_mark[ns_bn][ns] = sbt_mark[s_bn][s];
      sbt_normal[ns_bn][ns] = sbt_normal[s_bn][s];
      sbt_normal_bn[ns_bn][ns] = sbt_normal_bn[s_bn][s];
      sbt_peak_vert[ns_bn][ns] = sbt_peak_vert[s_bn][s];
      sbt_peak_simp[ns_bn][ns] = sbt_peak_simp[s_bn][s];
      sbt_peak_simp_bn[ns_bn][ns] = sbt_peak_simp_bn[s_bn][s];
      sbt_peak_basis[ns_bn][ns] = sbt_peak_basis[s_bn][s];
      sbt_peak_basis_bn[ns_bn][ns] = sbt_peak_basis_bn[s_bn][s];
      for (int j=0; j<rdim; j++) {
        sbt_neigh_vert[ns_bn][j][ns] = sbt_neigh_vert[s_bn][j][s];
        sbt_neigh_simp[ns_bn][j][ns] = sbt_neigh_simp[s_bn][j][s];
        sbt_neigh_simp_bn[ns_bn][j][ns] = sbt_neigh_simp_bn[s_bn][j][s];
        sbt_neigh_basis[ns_bn][j][ns] = sbt_neigh_basis[s_bn][j][s];
        sbt_neigh_basis_bn[ns_bn][j][ns] = sbt_neigh_basis_bn[s_bn][j][s];
      }

      for (int j=0; j<cdim; j++) {
        q = sbt_neigh_basis[s_bn][j][s];
        q_bn = sbt_neigh_basis_bn[s_bn][j][s];
        if (q != NOVAL) bbt_ref_count[q_bn][q]++;
      }

      sbt_neigh_simp[s_bn][cdim-1][s] = ns;
      sbt_neigh_simp_bn[s_bn][cdim-1][s] = ns_bn;
      sbt_peak_vert[ns_bn][ns] = NOVAL;
      sbt_peak_simp[ns_bn][ns] = s;
      sbt_peak_simp_bn[ns_bn][ns] = s_bn;
      sbt_neigh_vert[ns_bn][cdim-1][ns] = sbt_peak_vert[s_bn][s];
      sbt_neigh_simp[ns_bn][cdim-1][ns] = sbt_peak_simp[s_bn][s];
      sbt_neigh_simp_bn[ns_bn][cdim-1][ns] = sbt_peak_simp_bn[s_bn][s];
      sbt_neigh_basis[ns_bn][cdim-1][ns] = sbt_peak_basis[s_bn][s];
      sbt_neigh_basis_bn[ns_bn][cdim-1][ns] = sbt_peak_basis_bn[s_bn][s];
      q = sbt_peak_basis[s_bn][s];
      q_bn = sbt_peak_basis_bn[s_bn][s];
      if (q != NOVAL) bbt_ref_count[q_bn][q]++;
      for (int i=0; i<cdim; i++) {
        int[] esretp = new int[1];
        int[] esretp_bn = new int[1];
        extend_simplices(sbt_neigh_simp[ns_bn][i][ns],
                         sbt_neigh_simp_bn[ns_bn][i][ns], esretp, esretp_bn);
        sbt_neigh_simp[ns_bn][i][ns] = esretp[0];
        sbt_neigh_simp_bn[ns_bn][i][ns] = esretp_bn[0];
      }
    }
    ret[0] = ns;
    ret_bn[0] = ns_bn;
    return;
  }

  /**
     return a simplex s that corresponds to a facet of the
     current hull, and sees(p, s).
  */
  private void search(int root, int root_bn, int[] ret, int[] ret_bn) {
    int s, s_bn;
    int tms = 0;

    st[tms] = sbt_peak_simp[root_bn][root];
    st_bn[tms] = sbt_peak_simp_bn[root_bn][root];
    tms++;
    sbt_visit[root_bn][root] = pnum;
    if (sees(p, root, root_bn) == 0) {
      for (int i=0; i<cdim; i++) {
        st[tms] = sbt_neigh_simp[root_bn][i][root];
        st_bn[tms] = sbt_neigh_simp_bn[root_bn][i][root];
        tms++;
      }
    }
    while (tms != 0) {
      if (tms > ss) {
        // JAVA: efficiency issue: how much is this stack hammered?
        ss += ss;
        int[] newst = new int[ss+MAXDIM+1];
        int[] newst_bn = new int[ss+MAXDIM+1];
        System.arraycopy(st, 0, newst, 0, st.length);
        System.arraycopy(st_bn, 0, newst_bn, 0, st_bn.length);
        st = newst;
        st_bn = newst_bn;
      }
      tms--;
      s = st[tms];
      s_bn = st_bn[tms];
      if (sbt_visit[s_bn][s] == pnum) continue;
      sbt_visit[s_bn][s] = pnum;
      if (sees(p, s, s_bn) == 0) continue;
      if (sbt_peak_vert[s_bn][s] == NOVAL) {
        ret[0] = s;
        ret_bn[0] = s_bn;
        return;
      }
      for (int i=0; i<cdim; i++) {
        st[tms] = sbt_neigh_simp[s_bn][i][s];
        st_bn[tms] = sbt_neigh_simp_bn[s_bn][i][s];
        tms++;
      }
    }
    ret[0] = NOVAL;
    return;
  }


  /**
   * construct a Delaunay triangulation of the points in the
   * samples array using Clarkson's algorithm
   * @param samples locations of points for topology - dimensioned
   *                float[dimension][number_of_points]
   * @throws VisADException a VisAD error occurred
   */
  public DelaunayClarkson(float[][] samples) throws Exception {
    int s, s_bn, q, q_bn;
    int root, root_bn;
    int k=0;
    int[] retp = new int[1];
    int[] retp_bn = new int[1];
    int[] ret2p = new int[1];
    int[] ret2p_bn = new int[1];
    int[] curt = new int[1];
    int[] curt_bn = new int[1];
    int s_num = 0;
    int nrs;

    // Start of main hull triangulation algorithm
    dim = samples.length;
    nrs = samples[0].length;
    for (int i=1; i<dim; i++) nrs = Math.min(nrs, samples[i].length);

    if (nrs <= dim) throw new Exception("DelaunayClarkson: "
                                          +"not enough samples");
    if (dim > MAXDIM) throw new Exception("DelaunayClarkson: "
                               +"dimension bound MAXDIM exceeded");

    // copy samples
    site_blocks = new float[dim][nrs];
    for (int j=0; j<dim; j++) {
      System.arraycopy(samples[j], 0, site_blocks[j], 0, nrs);
    }

/* WLH 29 Jan 98 - scale samples values as discussed in Delaunay.factory
    for (int j=0; j<dim; j++) {
      for (int kk=0; kk<nrs; kk++) {
        site_blocks[j][kk] = 100.0f * samples[j][kk];
      }
    }
*/

    exact_bits = (int) (DBL_MANT_DIG*Math.log(FLT_RADIX)/ln2);
    b_err_min = DBL_EPSILON*MAXDIM*(1<<MAXDIM)*MAXDIM*3.01;
    b_err_min_sq = b_err_min * b_err_min;

    cdim = 0;
    rdim = dim+1;
    if (rdim > MAXDIM) throw new Exception(
              "dimension bound MAXDIM exceeded; rdim="+rdim+"; dim="+dim);

    pnb = basis_s_list != NOVAL ? basis_s_list : new_block_basis_s();
    pnb_bn = basis_s_list_bn;
    basis_s_list = bbt_next[pnb_bn][pnb];
    basis_s_list_bn = bbt_next_bn[pnb_bn][pnb];
    bbt_next[pnb_bn][pnb] = NOVAL;

    ttbp = basis_s_list != NOVAL ? basis_s_list : new_block_basis_s();
    ttbp_bn = basis_s_list_bn;
    basis_s_list = bbt_next[ttbp_bn][ttbp];
    basis_s_list_bn = bbt_next_bn[ttbp_bn][ttbp];
    bbt_next[ttbp_bn][ttbp] = NOVAL;
    bbt_ref_count[ttbp_bn][ttbp] = 1;
    bbt_lscale[ttbp_bn][ttbp] = -1;
    bbt_sqa[ttbp_bn][ttbp] = 0;
    bbt_sqb[ttbp_bn][ttbp] = 0;
    for (int j=0; j<2*rdim; j++) bbt_vecs[ttbp_bn][j][ttbp] = 0;

    root = NOVAL;
    p = INFINITY;
    ib = (basis_s_list != NOVAL) ? basis_s_list : new_block_basis_s();
    ib_bn = basis_s_list_bn;
    basis_s_list = bbt_next[ib_bn][ib];
    basis_s_list_bn = bbt_next_bn[ib_bn][ib];
    bbt_ref_count[ib_bn][ib] = 1;
    bbt_vecs[ib_bn][2*rdim-1][ib] = bbt_vecs[ib_bn][rdim-1][ib] = 1;
    bbt_sqa[ib_bn][ib] = bbt_sqb[ib_bn][ib] = 1;

    root = (simplex_list != NOVAL) ? simplex_list : new_block_simplex();
    root_bn = simplex_list_bn;
    simplex_list = sbt_next[root_bn][root];
    simplex_list_bn = sbt_next_bn[root_bn][root];

    ch_root = root;
    ch_root_bn = root_bn;

    s = (simplex_list != NOVAL) ? simplex_list : new_block_simplex();
    s_bn = simplex_list_bn;
    simplex_list = sbt_next[s_bn][s];
    simplex_list_bn = sbt_next_bn[s_bn][s];
    sbt_next[s_bn][s] = sbt_next[root_bn][root];
    sbt_next_bn[s_bn][s] = sbt_next_bn[root_bn][root];
    sbt_visit[s_bn][s] = sbt_visit[root_bn][root];
    sbt_mark[s_bn][s] = sbt_mark[root_bn][root];
    sbt_normal[s_bn][s] = sbt_normal[root_bn][root];
    sbt_normal_bn[s_bn][s] = sbt_normal_bn[root_bn][root];
    sbt_peak_vert[s_bn][s] = sbt_peak_vert[root_bn][root];
    sbt_peak_simp[s_bn][s] = sbt_peak_simp[root_bn][root];
    sbt_peak_simp_bn[s_bn][s] = sbt_peak_simp_bn[root_bn][root];
    sbt_peak_basis[s_bn][s] = sbt_peak_basis[root_bn][root];
    sbt_peak_basis_bn[s_bn][s] = sbt_peak_basis_bn[root_bn][root];
    for (int i=0; i<rdim; i++) {
      sbt_neigh_vert[s_bn][i][s] = sbt_neigh_vert[root_bn][i][root];
      sbt_neigh_simp[s_bn][i][s] = sbt_neigh_simp[root_bn][i][root];
      sbt_neigh_simp_bn[s_bn][i][s] = sbt_neigh_simp_bn[root_bn][i][root];
      sbt_neigh_basis[s_bn][i][s] = sbt_neigh_basis[root_bn][i][root];
      sbt_neigh_basis_bn[s_bn][i][s] = sbt_neigh_basis_bn[root_bn][i][root];
    }
    for (int i=0;i<cdim;i++) {
      q = sbt_neigh_basis[root_bn][i][root];
      q_bn = sbt_neigh_basis_bn[root_bn][i][root];
      if (q != NOVAL) bbt_ref_count[q_bn][q]++;
    }
    sbt_peak_vert[root_bn][root] = p;
    sbt_peak_simp[root_bn][root] = s;
    sbt_peak_simp_bn[root_bn][root] = s_bn;
    sbt_peak_simp[s_bn][s] = root;
    sbt_peak_simp_bn[s_bn][s] = root_bn;
    while (cdim < rdim) {
      int oof = 0;

      if (s_num == 0) p = 0;
      else p++;
      for (int i=0; i<dim; i++) {
        site_blocks[i][p] = (float) Math.floor(site_blocks[i][p]+0.5);
      }
      s_num++;
      pnum = (s_num*dim-1)/dim + 2;

      cdim++;
      sbt_neigh_vert[root_bn][cdim-1][root] = sbt_peak_vert[root_bn][root];

      q = sbt_neigh_basis[root_bn][cdim-1][root];
      q_bn = sbt_neigh_basis_bn[root_bn][cdim-1][root];
      if (q != NOVAL && --bbt_ref_count[q_bn][q] == 0) {
        bbt_next[q_bn][q] = basis_s_list;
        bbt_next_bn[q_bn][q] = basis_s_list_bn;
        bbt_ref_count[q_bn][q] = 0;
        bbt_lscale[q_bn][q] = 0;
        bbt_sqa[q_bn][q] = 0;
        bbt_sqb[q_bn][q] = 0;
        for (int l=0; l<2*rdim; l++) bbt_vecs[q_bn][l][q] = 0;

        basis_s_list = q;
        basis_s_list_bn = q_bn;
      }
      sbt_neigh_basis[root_bn][cdim-1][root] = NOVAL;

      get_basis_sede(root, root_bn);
      if (sbt_neigh_vert[root_bn][0][root] == INFINITY) oof = 1;
      else {
        curt[0] = pnb;
        curt_bn[0] = pnb_bn;
        reduce(curt, curt_bn, p, root, root_bn, cdim);
        pnb = curt[0];
        pnb_bn = curt_bn[0];
        if (bbt_sqa[pnb_bn][pnb] != 0) oof = 1;
        else cdim--;
      }
      if (oof != 0) extend_simplices(root, root_bn, voidp, voidp_bn);
      else {
        search(root, root_bn, retp, retp_bn);
        make_facets(retp[0], retp_bn[0], ret2p, ret2p_bn);
        connect(ret2p[0], ret2p_bn[0]);
      }
    }

    for (int i=s_num; i<nrs; i++) {
      p++;
      s_num++;
      for (int j=0; j<dim; j++) {
        site_blocks[j][p] = (float) Math.floor(site_blocks[j][p]+0.5);
      }
      pnum = (s_num*dim-1)/dim + 2;
      search(root, root_bn, retp, retp_bn);
      make_facets(retp[0], retp_bn[0], ret2p, ret2p_bn);
      connect(ret2p[0], ret2p_bn[0]);
    }

    a3size = rdim*nrs;
    a3s = new int[rdim][a3size+MAXDIM+1];
    visit_triang_gen(root, root_bn, 1, retp, retp_bn);
    visit_triang_gen(retp[0], retp_bn[0], 0, voidp, voidp_bn);

    // deallocate memory
    /* NOTE: If this deallocation is not performed, more points
       could theoretically be added to the triangulation later */
    site_blocks = null;
    st = null;
    st_bn = null;
    st2 = null;
    st2_bn = null;
    sbt_next = null;
    sbt_next_bn = null;
    sbt_visit = null;
    sbt_mark = null;
    sbt_normal = null;
    sbt_normal_bn = null;
    sbt_peak_vert = null;
    sbt_peak_simp = null;
    sbt_peak_simp_bn = null;
    sbt_peak_basis = null;
    sbt_peak_basis_bn = null;
    sbt_neigh_vert = null;
    sbt_neigh_simp = null;
    sbt_neigh_simp_bn = null;
    sbt_neigh_basis = null;
    sbt_neigh_basis_bn = null;
    bbt_next = null;
    bbt_next_bn = null;
    bbt_ref_count = null;
    bbt_lscale = null;
    bbt_sqa = null;
    bbt_sqb = null;
    bbt_vecs = null;

/* ********** END OF CONVERTED HULL CODE ********** */
/*          (but still inside constructor)          */

    // compute number of triangles or tetrahedra
    int[] nverts = new int[nrs];
    for (int i=0; i<nrs; i++) nverts[i] = 0;
    int ntris = 0;
    boolean positive;
    for (int i=0; i<nts; i++) {
      positive = true;
      for (int j=0; j<rdim; j++) {
        if (a3s[j][i] < 0) positive = false;
      }
      if (positive) {
        ntris++;
        for (int j=0; j<rdim; j++) nverts[a3s[j][i]]++;
      }
    }
    Vertices = new int[nrs][];
    for (int i=0; i<nrs; i++) Vertices[i] = new int[nverts[i]];
    for (int i=0; i<nrs; i++) nverts[i] = 0;

    // build Tri & Vertices components
    Tri = new int[ntris][rdim];
    int a, b, c, d;
    int itri = 0;
    for (int i=0; i<nts; i++) {
      positive = true;
      for (int j=0; j<rdim; j++) {
        if (a3s[j][i] < 0) positive = false;
      }
      if (positive) {
        for (int j=0; j<rdim; j++) {
          Vertices[a3s[j][i]][nverts[a3s[j][i]]++] = itri;
          Tri[itri][j] = a3s[j][i];
        }
        itri++;
      }
    }

    // Deallocate remaining helper information
    a3s = null;

    // call more generic method for constructing Walk and Edges arrays
    finish_triang(samples);
  }

}

