package managed.login;

import java.io.IOException;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;

import entity.Account;
import managed.SessionBean;
import service.AccountService;
import util.Constants;
import util.FacesUtils;

@ManagedBean
@ViewScoped
public class LoginBean {

	private String username;
	private String password;
	
	private UIComponent loginButton;
	
	@EJB
	private AccountService accountService;
	
	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean sessionBean;

	public void loginCheck() throws IOException {
		Account user = accountService.getAccount(username, password);
		if (user != null) {
			sessionBean.setUser(user);;
			FacesUtils.redirect(Constants.PAGE_DEVICES);
		} else {
			FacesUtils.addMessage("Invalid username or password.", FacesMessage.SEVERITY_ERROR, loginButton);
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UIComponent getLoginButton() {
		return loginButton;
	}

	public void setLoginButton(UIComponent loginButton) {
		this.loginButton = loginButton;
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

}
