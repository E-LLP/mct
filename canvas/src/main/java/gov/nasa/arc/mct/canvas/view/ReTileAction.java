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
package gov.nasa.arc.mct.canvas.view;

import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.gui.View;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class ReTileAction extends ContextAwareAction {
    private static final long serialVersionUID = 5487339986221192656L;

    private static ResourceBundle bundle = ResourceBundle.getBundle("CanvasResourceBundle");
    
    private Collection<CanvasManifestation> selectedManifestations;
    
    public ReTileAction() {
        super(bundle.getString("ReTile"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (CanvasManifestation manifestation : selectedManifestations) {
            manifestation.retile();
            manifestation.computePreferredSize();
            manifestation.fireFocusPersist();
        }
    }
    
    @Override
    public boolean canHandle(ActionContext actionContext) {
        selectedManifestations = getCanvasManifestations(getSelectedManifestations(actionContext));

        return !selectedManifestations.isEmpty();
    }

    @Override
    public boolean isEnabled() {
        for (CanvasManifestation canvasManifestation: selectedManifestations) {
            if (canvasManifestation.isLocked())
                return false;
        }
        return true;
    }
    
    protected Collection<View> getSelectedManifestations(ActionContext actionContext) {
        return actionContext.getSelectedManifestations();
    }

    private Collection<CanvasManifestation> getCanvasManifestations(
                    Collection<View> selectedManifestations) {
        List<CanvasManifestation> selectedCanvasManifestations = new LinkedList<CanvasManifestation>();

        for (View viewManifestation : selectedManifestations) {
            if (viewManifestation instanceof CanvasManifestation) {
                selectedCanvasManifestations.add((CanvasManifestation) viewManifestation);
            }
        }
        return selectedCanvasManifestations;
    }
}
