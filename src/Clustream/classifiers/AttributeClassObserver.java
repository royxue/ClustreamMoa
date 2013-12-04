/*
 *    AttributeClassObserver.java
 *    Copyright (C) 2007 University of Waikato, Hamilton, New Zealand
 *    @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package Clustream.classifiers;

import Clustream.addon.MOAObject;

public interface AttributeClassObserver extends MOAObject {

	public void observeAttributeClass(double attVal, int classVal, double weight);

	public double probabilityOfAttributeValueGivenClass(double attVal,
			int classVal);

	public AttributeSplitSuggestion getBestEvaluatedSplitSuggestion(
			SplitCriterion criterion, double[] preSplitDist, int attIndex,
			boolean binaryOnly);

}
