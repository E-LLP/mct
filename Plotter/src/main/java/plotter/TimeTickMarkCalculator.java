/*******************************************************************************
 * Mission Control Technologies, Copyright (c) 2009-2012, United States Government
 * as represented by the Administrator of the National Aeronautics and Space 
 * Administration. All rights reserved.
 *
 * The MCT platform is licensed under the Apache License, Version 2.0 (the 
 * "License"); you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations under 
 * the License.
 *
 * MCT includes source code licensed under additional open source licenses. See 
 * the MCT Open Source Licenses file included with this distribution or the About 
 * MCT Licenses dialog available at runtime from the MCT Help menu for additional 
 * information. 
 *******************************************************************************/
package plotter;



/**
 * Implementation of {@link TickMarkCalculator} for time axes.
 * @author Adam Crume
 */
public class TimeTickMarkCalculator implements TickMarkCalculator {
	private static final long MINUTE = 60 * 1000;

	private static final long HOUR = 60 * MINUTE;

	private static final long DAY = 24 * HOUR;


	@Override
	public double[][] calculateTickMarks(Axis axis) {
		double start = axis.getStart();
		double end = axis.getEnd();
		double min = start;
		double max = end;
		if(min > max) {
			double tmp = max;
			max = min;
			min = tmp;
		}
		double diff = max - min;

		double spacing;
		double spacing2;
		// TODO: Clean this up
		if(diff > 2 * DAY) {
			double days = diff / DAY;
			// d should be days scaled to between 1 and 10
			double d = days / Math.pow(10, Math.floor(Math.log10(days)));
			double adj;
			if(d < 2) {
				d *= 2;
				adj = .2;
			} else if(d > 5) {
				d *= .5;
				adj = .5;
			} else {
				adj = .5;
			}
			spacing = DAY * days / d;
			spacing2 = spacing * adj;
		} else if(diff > 2 * HOUR) {
			double hours = diff / HOUR;
			if(hours > 24) {
				spacing = 12 * HOUR;
				spacing2 = 6 * HOUR;
			} else if(hours > 12) {
				spacing = 6 * HOUR;
				spacing2 = 2 * HOUR;
			} else {
				spacing = 2 * HOUR;
				spacing2 = HOUR;
			}
		} else if(diff > 2 * MINUTE) {
			double minutes = diff / MINUTE;
			if(minutes > 60) {
				spacing = 30 * MINUTE;
				spacing2 = 10 * MINUTE;
			} else if(minutes > 30) {
				spacing = 15 * MINUTE;
				spacing2 = 5 * MINUTE;
			} else if(minutes > 20) {
				spacing = 10 * MINUTE;
				spacing2 = 5 * MINUTE;
			} else if(minutes > 10) {
				spacing = 5 * MINUTE;
				spacing2 = MINUTE;
			} else {
				spacing = 2 * MINUTE;
				spacing2 = MINUTE;
			}
		} else if(diff > 2 * 1000) {
			double seconds = diff / 1000;
			if(seconds > 60) {
				spacing = 30 * 1000;
				spacing2 = 10 * 1000;
			} else if(seconds > 30) {
				spacing = 15 * 1000;
				spacing2 = 5 * 1000;
			} else if(seconds > 20) {
				spacing = 10 * 1000;
				spacing2 = 5 * 1000;
			} else if(seconds > 10) {
				spacing = 5 * 1000;
				spacing2 = 1000;
			} else {
				spacing = 2000;
				spacing2 = 1000;
			}
		} else {
			// d should be diff scaled to between 1 and 10
			double d = diff / Math.pow(10, Math.floor(Math.log10(diff)));
			double adj;
			if(d < 2) {
				d *= 2;
				adj = .2;
			} else if(d > 5) {
				d /= 2;
				adj = .5;
			} else {
				adj = .5;
			}
			spacing = diff / d;
			spacing2 = spacing * adj;
		}

		double[] majorVals;
		double[] minorVals;
		if(min >= 0) {
			// In this case, both ends of the axis are non-negative.  Go from min to max.
			double startCount = Math.ceil(min / spacing);
			double endCount = Math.floor(max / spacing);
			majorVals = new double[(int)(endCount - startCount) + 1];
			double value = startCount * spacing;
			for(int i = 0; i < majorVals.length; i++) {
				majorVals[i] = value;
				value += spacing;
			}

			startCount = Math.ceil(min / spacing2);
			endCount = Math.floor(max / spacing2);
			minorVals = new double[(int) (endCount - startCount) + 1];
			value = startCount * spacing2;
			for(int i = 0; i < minorVals.length; i++) {
				minorVals[i] = value;
				value += spacing2;
			}
		} else if(max <= 0) {
			// In this case, both ends of the axis are non-positive.  Go from max to min.
			double startCount = Math.floor(max / spacing);
			double endCount = Math.ceil(min / spacing);
			majorVals = new double[(int) (startCount - endCount) + 1];
			double value = startCount * spacing;
			for(int i = 0; i < majorVals.length; i++) {
				majorVals[i] = value;
				value -= spacing;
			}

			startCount = Math.floor(max / spacing2);
			endCount = Math.ceil(min / spacing2);
			minorVals = new double[(int) (startCount - endCount) + 1];
			value = startCount * spacing2;
			for(int i = 0; i < minorVals.length; i++) {
				minorVals[i] = value;
				value -= spacing2;
			}
		} else {
			// In this case, one end is positive and one is negative.
			// Start from 0 and go in both directions to minimize rounding error.

			// Calculate the number of major ticks.  There should be a better way.
			int majorCount = 0;
			double value = 0;
			while(value <= max) {
				value += spacing;
				majorCount++;
			}
			value = -spacing;
			while(value >= min) {
				value -= spacing;
				majorCount++;
			}

			majorVals = new double[majorCount];
			value = 0;
			int i = 0;
			while(value <= max) {
				majorVals[i] = value;
				i++;
				value += spacing;
			}
			value = -spacing;
			while(value >= min) {
				majorVals[i] = value;
				i++;
				value -= spacing;
			}

			// Calculate the number of minor ticks.  There should be a better way.
			int minorCount = 0;
			value = 0;
			while(value <= max) {
				value += spacing2;
				minorCount++;
			}
			value = -spacing2;
			while(value >= min) {
				value -= spacing2;
				minorCount++;
			}

			minorVals = new double[minorCount];
			value = 0;
			i = 0;
			while(value <= max) {
				minorVals[i] = value;
				i++;
				value += spacing2;
			}
			value = -spacing2;
			while(value >= min) {
				minorVals[i] = value;
				i++;
				value -= spacing2;
			}
		}

		return new double[][] {majorVals, minorVals};
	}
}
