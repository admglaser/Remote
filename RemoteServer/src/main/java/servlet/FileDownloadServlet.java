package servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.disk.DiskFileItem;

import service.UploadService;

@SuppressWarnings("serial")
public class FileDownloadServlet extends HttpServlet {

	@EJB
	UploadService uploadService;
	
	public FileDownloadServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String requestURI = request.getRequestURI();
		String name = requestURI.replace("/remote/download/", "");
		
		DiskFileItem diskFileItem = uploadService.get(name);
		InputStream inputStream = diskFileItem.getInputStream();
		
		response.setHeader("size", String.valueOf(inputStream.available()));
		response.setHeader("Content-disposition","attachment; filename=" + diskFileItem.getName());
		
		OutputStream out = response.getOutputStream();
		byte[] buffer = new byte[4096];
		int length;
		while ((length = inputStream.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}
		
		inputStream.close();
		out.flush();
		
		diskFileItem.delete();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
