package com;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;


public class OtServlet extends HttpServlet implements Servlet {

	
	private Connection con = null;
	private Map<String, Object> parameters;
	private String reportName;
	private String reportPath ; 
	
	private static String report1Month  = "month1";
	private static String report1Year  = "year1";
	private static String report2StartingYear  = "year2";
	private static String report2StartingMonth  = "month2";
	private static String noOfMonths  = "NoOfMonths";
	private static String report2Month  = "month3";
	private static String report2Year  = "year3";
	
	
	private static String paraMonthspassed  = "monthspassed";
	private static String paraMonth = "month";	
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		getConnection();
	    parameters = new HashMap<String, Object>();
	    ServletContext sc = getServletContext();
	   

	    reportPath = sc.getRealPath("/WEB-INF/reports");
		setParameters(req);
		PrintWriter pw = resp.getWriter();
		
		try {
	           JasperReport jasperReport;
	           
	           if(reportPath != null)
	        	   jasperReport = JasperCompileManager.compileReport(reportPath+"/" +reportName);
	           else
	               jasperReport = JasperCompileManager.compileReport("/WebContent/WEB-INF/reports/"+reportName);
		       JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,parameters, con);
			   
	            
	            resp.setContentType("text/html");
	            
	            JRHtmlExporter htmlExporter = new JRHtmlExporter();
	            HttpSession session =  req.getSession();
	            session.setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE,jasperPrint);
	            htmlExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
	            htmlExporter.setParameter(JRExporterParameter.OUTPUT_WRITER,pw);
	            //avoid using small images for aligning
	            htmlExporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, false);
	            //specify the resource that is used to send the images to the browser 
	            htmlExporter.setParameter(JRHtmlExporterParameter.IMAGES_URI,"servlets/image?image=");
	            htmlExporter.exportReport();
	            pw.flush();
	            pw.close();
	    		} catch (JRException e) {
	    			
	    			log(e.toString()); 
					e.printStackTrace();
				}
	}

	/**
	 * convert the given value to the corresponding month
	 * @param monthVal value corresponding to a specific month
	 * @return month corresponding to the value 
	 */
	private String getMonth(int monthVal){
		String month = "January";
		switch (monthVal) {
		case 0:
			month = "January";
			break;
		case 1:
			month = "Februaty";
			break;
		case 2:
			month = "March";
			break;
		case 3:
			month = "April";
			break;
		case 4:
			month = "May";
			break;
		case 5:
			month = "June";
			break;
		case 6:
			month = "July";
			break;
		case 7:
			month = "August";
			break;
		case 8:
			month = "September";  
			break;
		case 9:
			month = "October";
			break;
		case 10:
			month = "November";
			break;
		case 11:
			month = "December";
			break;
		}
		
		return month;
	}
	
	
	/**
	 * get the database connection
	 */
	private void getConnection(){
		  try {
			        Class.forName("com.mysql.jdbc.Driver");
			        con = DriverManager.getConnection("jdbc:mysql://localhost/test2","root","123");
			   }catch( Exception e ){
		   	        e.printStackTrace();
		       }
	}
	
	
	/**
	 * set parameters of the reports
	 * @param req HttpServletRequest
	 */
	private void setParameters(HttpServletRequest req){
		String reportType = req.getParameter("view");
		if(reportType.equals("view"))
		{
			int month = Integer.parseInt(req.getParameter(report1Month));
			int year = Integer.parseInt(req.getParameter(report1Year));	
			int monthspassed = year*12 + month;
			
			parameters.put(paraMonthspassed,monthspassed);
			parameters.put(paraMonth, getMonth(month)+"  "+year);
			reportName = "report1.jrxml";
		}
		
		else if(reportType.equals(" view"))
		{
			int month = Integer.parseInt(req.getParameter(report2Month));
			int year = Integer.parseInt(req.getParameter(report2Year));	
			int numberOfMonths = Integer.parseInt(req.getParameter(noOfMonths));
			int monthspassed = year*12 + month;
			int startingYear = Integer.parseInt(req.getParameter(report2StartingYear));	
			int startingMonth = Integer.parseInt(req.getParameter(report2StartingMonth));
			int startingMonthspassed =startingYear*12 + startingMonth;
			String[] m = {"m1","m2","m3","m4","m5","m6","m7","m8","m9","m10","m11","m12"};
			
			for (int i = 0; i < numberOfMonths; i++) {
				parameters.put(m[i], startingMonthspassed++);
			}	
			parameters.put(paraMonthspassed,monthspassed);
			
			reportName = "report2.jrxml";
		}

	}
}

