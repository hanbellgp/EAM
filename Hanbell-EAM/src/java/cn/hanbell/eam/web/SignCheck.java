/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.web;

import cn.hanbell.eam.control.UserManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 *
 * @author CharlesTung
 */
public class SignCheck implements PhaseListener {

    @Override
    public void afterPhase(PhaseEvent event) {
        FacesContext fc = event.getFacesContext();
        if (fc.getViewRoot().getViewId().startsWith("/register")) {
            return;
        }
        try {
            boolean isLoginPage = fc.getViewRoot().getViewId().lastIndexOf("login.xhtml") > -1;
            if (!isLoginPage && !isSignIn(fc)) {
                fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "login");
            }
        } catch (Exception e) {
            fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "login");
        }
    }

    @Override
    public void beforePhase(PhaseEvent event) {
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    private boolean isSignIn(FacesContext fc) {
        UserManagedBean user = (UserManagedBean) fc.getExternalContext().getSessionMap().get("userManagedBean");
        if (user == null) {
            return false;
        } else {
            return user.getStatus();
        }
    }
}
