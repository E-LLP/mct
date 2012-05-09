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
package gov.nasa.arc.mct.policy;

import gov.nasa.arc.mct.components.AbstractComponent;

/**
 * This policy prevents model changes on a twiddled component.
 * @author nshi
 *
 */
public class DisallowModelChangeForTwiddledComponent implements Policy {

    private static final String EM_STR = "";

    @Override
    public ExecutionResult execute(PolicyContext context) {
        final ExecutionResult trueResult = new ExecutionResult(context, true, EM_STR);
        AbstractComponent component = context.getProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), AbstractComponent.class);
        if (component == null)
            return trueResult;
                
        boolean isTwiddledComponent = component.isTwiddledComponent();
        return new ExecutionResult(context, !isTwiddledComponent, isTwiddledComponent ? "Cannot add objects to a twiddled object" : EM_STR);
    }

}
