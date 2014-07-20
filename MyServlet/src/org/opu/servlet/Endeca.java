package org.opu.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Endeca extends HttpServlet{
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException{			
		PrintWriter writer= response.getWriter();
			response.setContentType("text/html");
			writer.println("ENDECA SEARCH"+"<br/>");
			
			EndecaProcess process= new EndecaProcess(request.getQueryString(),writer,request.getParameter("N"));
			process.startEndecaSearch();
			//writer.close();	
			
	}
}
