package util;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FacesUtils {

	public static void addMessage(String message, Severity severity, UIComponent component) {
		FacesMessage facesMessage = new FacesMessage(severity, message, message);
		FacesContext context = FacesContext.getCurrentInstance();
		if (component == null) {
			context.addMessage(null, facesMessage);
		} else {
			context.addMessage(component.getClientId(context), facesMessage);
		}
	}

	public static void redirect(String page) throws IOException {
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		response.sendRedirect(page);
	}
	
	public static void reload() throws IOException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
	}

}
