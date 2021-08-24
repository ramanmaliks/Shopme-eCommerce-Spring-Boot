package com.shopme.admin.export;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;


public class AbstractExporter {
	public void setRepsonseHeader(HttpServletResponse response, String contentType, String extension) throws IOException {
		//for file name creation with file extension
		DateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String timestamp = dateformatter.format(new Date());
		String fileName="users_"+timestamp + extension;
		
		response.setContentType(contentType);
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=" + fileName; 
		response.setHeader(headerKey, headerValue);
		
}
}