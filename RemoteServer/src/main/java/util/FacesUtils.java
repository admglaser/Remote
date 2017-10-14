package util;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

public class FacesUtils {

	public static void addMessage(String message, Severity severity, UIComponent loginButton) {
		FacesMessage facesMessage = new FacesMessage(severity, null, message);
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(loginButton.getClientId(context), facesMessage);
	}

	public static void redirect(String string) throws IOException {
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		response.sendRedirect(string);
	}

}
