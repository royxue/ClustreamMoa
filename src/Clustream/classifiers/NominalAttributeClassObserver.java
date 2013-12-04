/*
 *    NominalAttributeClassObserver.java
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

import weka.core.Utils;

import Clustream.addon.AbstractMOAObject;
import Clustream.addon.AutoExpandVector;
import Clustream.addon.DoubleVector;

public class NominalAttributeClassObserver extends AbstractMOAObject implements
		AttributeClassObserver {

	private static final long serialVersionUID = 1L;

	protected double totalWeightObserved = 0.0;

	protected double missingWeightObserved = 0.0;

	protected AutoExpandVector<DoubleVector> attValDistPerClass = new AutoExpandVector<DoubleVector>();

	public void observeAttributeClass(double attVal, int classVal, double weight) {
		if (Utils.isMissingValue(attVal)) {
			this.missingWeightObserved += weight;
		} else {
			int attValInt = (int) attVal;
			DoubleVector valDist = this.attValDistPerClass.get(classVal);
			if (valDist == null) {
				valDist = new DoubleVector();
				this.attValDistPerClass.set(classVal, valDist);
			}
			valDist.addToValue(attValInt, weight);
		}
		this.totalWeightObserved += weight;
	}

	public double probabilityOfAttributeValueGivenClass(double attVal,
			int classVal) {
		DoubleVector obs = this.attValDistPerClass.get(classVal);
		return obs != null ? (obs.getValue((int) attVal) + 1.0)
				/ (obs.sumOfValues() + obs.numValues()) : 0.0;
	}

	public double totalWeightOfClassObservations() {
		return this.totalWeightObserved;
	}

	public double weightOfObservedMissingValues() {
		return this.missingWeightObserved;
	}

	public AttributeSplitSuggestion getBestEvaluatedSplitSuggestion(
			SplitCriterion criterion, double[] preSplitDist, int attIndex,
			boolean binaryOnly) {
		AttributeSplitSuggestion bestSuggestion = null;
		int maxAttValsObserved = getMaxAttValsObserved();
		if (!binaryOnly) {
			double[][] postSplitDists = getClassDistsResultingFromMultiwaySplit(maxAttValsObserved);
			double merit = criterion.getMeritOfSplit(preSplitDist,
					postSplitDists);
			bestSuggestion = new AttributeSplitSuggestion(
					new NominalAttributeMultiwayTest(attIndex), postSplitDists,
					merit);
		}
		for (int valIndex = 0; valIndex < maxAttValsObserved; valIndex++) {
			double[][] postSplitDists = getClassDistsResultingFromBinarySplit(valIndex);
			double merit = criterion.getMeritOfSplit(preSplitDist,
					postSplitDists);
			if ((bestSuggestion == null) || (merit > bestSuggestion.merit)) {
				bestSuggestion = new AttributeSplitSuggestion(
						new NominalAttributeBinaryTest(attIndex, valIndex),
						postSplitDists, merit);
			}
		}
		return bestSuggestion;
	}

	public int getMaxAttValsObserved() {
		int maxAttValsObserved = 0;
		for (DoubleVector attValDist : this.attValDistPerClass) {
			if ((attValDist != null)
					&& (attValDist.numValues() > maxAttValsObserved)) {
				maxAttValsObserved = attValDist.numValues();
			}
		}
		return maxAttValsObserved;
	}

	public double[][] getClassDistsResultingFromMultiwaySplit(
			int maxAttValsObserved) {
		DoubleVector[] resultingDists = new DoubleVector[maxAttValsObserved];
		for (int i = 0; i < resultingDists.length; i++) {
			resultingDists[i] = new DoubleVector();
		}
		for (int i = 0; i < this.attValDistPerClass.size(); i++) {
			DoubleVector attValDist = this.attValDistPerClass.get(i);
			if (attValDist != null) {
				for (int j = 0; j < attValDist.numValues(); j++) {
					resultingDists[j].addToValue(i, attValDist.getValue(j));
				}
			}
		}
		double[][] distributions = new double[maxAttValsObserved][];
		for (int i = 0; i < distributions.length; i++) {
			distributions[i] = resultingDists[i].getArrayRef();
		}
		return distributions;
	}

	public double[][] getClassDistsResultingFromBinarySplit(int valIndex) {
		DoubleVector equalsDist = new DoubleVector();
		DoubleVector notEqualDist = new DoubleVector();
		for (int i = 0; i < this.attValDistPerClass.size(); i++) {
			DoubleVector attValDist = this.attValDistPerClass.get(i);
			if (attValDist != null) {
				for (int j = 0; j < attValDist.numValues(); j++) {
					if (j == valIndex) {
						equalsDist.addToValue(i, attValDist.getValue(j));
					} else {
						notEqualDist.addToValue(i, attValDist.getValue(j));
					}
				}
			}
		}
		return new double[][] { equalsDist.getArrayRef(),
				notEqualDist.getArrayRef() };
	}

	public void getDescription(StringBuilder sb, int indent) {
		// TODO Auto-generated method stub

	}

}
