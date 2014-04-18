/*
 *    Geotools - OpenSource mapping toolkit
 *    (C) 2002, Centre for Computational Geography
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.gdms.driver.shapefile;

/** Safe enumeration of MultiPatch Part Types with int and name, just like ShapeType
 * was for Shape Types. Quite unuseful for the moment... To be "resource bundled" or completed if necessary.
 * @author Stephane Bitot
 */
public final class PartType {

  /** Represents an Outer Ring shape (id = 2). */  
  public static final PartType OUTER_RING  = new PartType(2,"Outer Ring");
  /** Represents an Inner Ring shape (id = 3). */  
  public static final PartType INNER_RING  = new PartType(3,"Inner Ring");
  /** Represents a First Ring shape (id = 4). */  
  public static final PartType FIRST_RING  = new PartType(4,"First Ring");
  /** Represents a Ring shape (id = 5). */  
  public static final PartType RING  = new PartType(5,"Ring");
  /** Represents an Undefined part (id = -1). */  
  public static final PartType UNDEFINED = new PartType(-1,"Undefined");
  


  /** The integer id of this PartType. */  
  public final int id;
  /** The human-readable name for this PartType.<br>
   * Could easily use ResourceBundle for internationalization.
   */  
  public final String name;
  
  /** Creates a new instance of PartType. Hidden on purpose.
   * @param id The id.
   * @param name The name.
   */
  protected PartType(int id,String name) {
    this.id = id;
    this.name = name;
  }
  
  /** Get the name of this PartType.
   * @return The name.
   */  
  public String toString() { return name; }
  
    /** Determine the PartType for the id.
   * @param id The id to search for.
   * @return The PartType for the id.
   */  
  public static PartType forID(int id) {
    PartType t;
    switch (id) {
      case 2:
        t = OUTER_RING;
        break;
      case 3:
        t = INNER_RING;
        break;
      case 4:
        t = FIRST_RING;
        break;
      case 5:
        t = RING;
        break;
      default:
        t = UNDEFINED;
        break;
    }
    return t;
  }

}