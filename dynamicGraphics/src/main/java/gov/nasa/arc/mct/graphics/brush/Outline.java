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
package gov.nasa.arc.mct.graphics.brush;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;


/**
 * Outline draws the shape of its object as a solid outline. 
 * 
 * @author vwoeltje
 *
 */
public class Outline extends Brush {
	
	private Color color;

	/**
	 * Construct a brush which will outline a shape
	 * @param c the color with which ot outline the shape
	 */
	public Outline(Color c) {
		color = c;
	}


	@Override
	protected void drawTransformed(Shape s, Graphics2D g2) {
		Rectangle b = s.getBounds();
		Stroke originalStroke = g2.getStroke();
		Color  originalColor  = g2.getColor();
		
		
		g2.setColor(color);

		g2.setStroke(new BasicStroke(Math.min(b.width, b.height) / 20.0f));
		g2.draw(s);

		
		g2.setStroke(originalStroke);
		g2.setColor(originalColor);
	}



}
