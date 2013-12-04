/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *    CustomizerCloseRequester.java
 *    Copyright (C) 2004-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.gui.beans;

import java.awt.Window;

/**
 * Customizers who want to be able to close the customizer window
 * themselves can implement this window. The KnowledgeFlow will
 * pass in the reference to the parent Window when constructing
 * the customizer. The customizer can then call dispose() the
 * Frame whenever it suits them.
 *
 * @author Mark Hall
 * @version $Revision: 8034 $
 */
public interface CustomizerCloseRequester {

  /**
   * A reference to the parent is passed in
   *
   * @param parent the parent Window
   */
  void setParentWindow(Window parent);
}
