package filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import entity.Account;
import managed.SessionBean;
import util.Constants;

@WebFilter(filterName = "filter", urlPatterns = "*")
public class LoginFilter implements Filter {

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	
	}

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession();
		String page = request.getRequestURL().toString();

		if (page.endsWith(Constants.PAGE_ACCOUNT) && !isLoggedIn(session)) {
			response.sendRedirect(Constants.PAGE_LOGIN);
			return;
		}
					
		if (page.endsWith(Constants.PAGE_DEVICES) && !isLoggedIn(session)) {
			response.sendRedirect(Constants.PAGE_LOGIN);
			return;
		}
					
		if (page.endsWith(Constants.PAGE_CLIENTS)) {
			if (!isLoggedIn(session)) {
				response.sendRedirect(Constants.PAGE_LOGIN);
				return;
			} else if (!isAdmin(session)) {
				response.sendRedirect(Constants.PAGE_INDEX);
				return;
			}
		}
		
		chain.doFilter(req, res);
	}
	
	private boolean isLoggedIn(HttpSession session) {
		SessionBean sessionBean = (SessionBean) session.getAttribute("sessionBean");
		if (sessionBean != null) {
			Account user = sessionBean.getUser();
			return user != null;
		}
		return false;
	}
	
	private boolean isAdmin(HttpSession session) {
		SessionBean sessionBean = (SessionBean) session.getAttribute("sessionBean");
		if (sessionBean != null) {
			Account user = sessionBean.getUser();
			return user != null && user.isAdmin();
		}
		return false;
	}

}
