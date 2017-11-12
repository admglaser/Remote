package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import service.UploadService;

@SuppressWarnings("serial")
public class FileUploadServlet extends HttpServlet {

	@EJB
	UploadService uploadService;
	
	private Logger logger = Logger.getLogger(FileUploadServlet.class);
	
	public FileUploadServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		throw new ServletException("POST method required.");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		boolean isMultipartContent = ServletFileUpload.isMultipartContent(request);
		if (!isMultipartContent) {
			return;
		}
		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
		try {
			List<FileItem> fileItems = upload.parseRequest(request);
			if (fileItems.size() == 1) {
				FileItem fileItem = fileItems.get(0);
				if (fileItem instanceof DiskFileItem) {
					DiskFileItem diskFileItem = (DiskFileItem) fileItem;
					uploadService.add(diskFileItem);
					out.print("download/" + diskFileItem.getStoreLocation().getName());
				}
			}
		} catch (FileUploadException e) {
			logger.error(e.getMessage());
		}
	}

}
