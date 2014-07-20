package org.opu.servlet;

import java.io.PrintWriter;
import java.util.ListIterator;

import com.endeca.navigation.DimVal;
import com.endeca.navigation.DimValIdList;
import com.endeca.navigation.DimValList;
import com.endeca.navigation.Dimension;
import com.endeca.navigation.DimensionList;
import com.endeca.navigation.ENEQuery;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.ENEQueryToolkit;
import com.endeca.navigation.ERec;
import com.endeca.navigation.ERecList;
import com.endeca.navigation.HttpENEConnection;
import com.endeca.navigation.Navigation;
import com.endeca.navigation.PropertyMap;
import com.endeca.navigation.UrlENEQuery;
import com.endeca.navigation.UrlENEQueryParseException;

public class EndecaProcess {
	
	private String queryString,N;
	private PrintWriter out;
	private Navigation navigation;
	
	public EndecaProcess(String queryString,PrintWriter out,String N) {
		this.queryString=(queryString==null)?"N=0":queryString;
		this.out=out;
		this.N=(N==null)?"0":N;
	}

	public void startEndecaSearch() {
		Navigation navigation=createNavigation();
		printDimension();
		printBreadCrumb();
		recordShow();
		out.close();
	}
	
	private void recordShow() {
		
		//Record show
		out.println("<div>");
		out.println("<ol>");
		
		ERecList recordList= navigation.getERecs();
		ListIterator iterator= (ListIterator) recordList.listIterator();
		
		while(iterator.hasNext()) {
	      	ERec record= (ERec) iterator.next();		      	  
	      	PropertyMap propertyMap= record.getProperties();		    
	      	out.println( "<li> Record ="+propertyMap.get("p_relative_path")+"</li>");
	    }
		out.println("</ol>");
		out.println("</div>");
	}
	
	private void printBreadCrumb() {
		
		out.println("<div>");			
		DimensionList breadcrumbDimensionList = navigation.getDescriptorDimensions();
		for(Object element: breadcrumbDimensionList){				
			Dimension dimension = (Dimension)element;
			
			DimVal dimVal= dimension.getDescriptor();
			
			DimValIdList dValIdList= ENEQueryToolkit.removeDescriptor(navigation, dimVal);
			N=dValIdList.toString();
			out.println("<div>");
				out.println(dimension.getName()+" >> "+dimVal.getName() + "<a href=\"http://localhost:8080/MyServlet/endeca?N="+N+"&Ne="+dimension.getId()+"\">"+"Remove"+"</a>");				
			out.println("</div>");
		}
		out.println("</div>");
	}
	
	private void printDimension() {
		//Dimension
		DimensionList refinementDimensionList = this.navigation.getRefinementDimensions();
		out.println("<div>");
		out.println("<ul>");
		String Nvalue="0";
		
		for (int i = 0; i < refinementDimensionList.size(); i++) {				
			Dimension d = (Dimension) refinementDimensionList.get(i);				
			out.println("<li><a href=\"http://localhost:8080/MyServlet/endeca?N="+N+"&Ne="+d.getId()+"\">" + d.getName() + "</a></li>");				
			
			DimValList refinements = d.getRefinements();				
			if(refinements.size() > 0) {
				out.println("<ol>");		
				for (int j = 0; j < refinements.size(); j++) {
					DimVal dVal = (DimVal)refinements.get(j);
					DimValIdList dValIdList= ENEQueryToolkit.selectRefinement(navigation, dVal);
					N=dValIdList.toString();
					out.println("<li>"+"<a href=\"http://localhost:8080/MyServlet/endeca?N="+N+"&Ne="+d.getId()+"\">"+dVal.getName()+"</a></li>");				
				}
				out.println("</ol>");
			}
		}
		out.println("</div>");
	}
	
	private Navigation createNavigation() {				
		
		//out.println(this.queryString);
		//out.println(this.N);
		
		try {
			ENEQuery query= new UrlENEQuery(this.queryString,"UTF-8");
			query.setNavNumERecs(1);
			ENEQueryResults results=conn().query(query);
			navigation=results.getNavigation();
		} catch (UrlENEQueryParseException e) {
			e.printStackTrace();
		} catch (ENEQueryException e) {
			e.printStackTrace();
		}		
		return navigation;
	}
	
	private HttpENEConnection conn() {
		return new HttpENEConnection("dev-online.ibfd.org",5100);
	}
	
}
