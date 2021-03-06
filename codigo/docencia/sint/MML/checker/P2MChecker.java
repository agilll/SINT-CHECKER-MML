/****************************************************************
 *    SERVICIOS DE INTERNET
 *    EE TELECOMUNICACIÓN
 *    UNIVERSIDAD DE VIGO
 *
 *    Checker de la práctica de MML
 *
 *    Autor: Alberto Gil Solla
 *    Curso : 2017-2018
 ****************************************************************/


// pide los datos de la práctica de un alumno y los compara con los que obtiene del profesor

// Query1 y Query2 emplean las clases Pelicula, Actor, Filmografia y Film de la propia implementación de la práctica

package docencia.sint.MML.checker;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import docencia.sint.Common.CommonSINT;
import docencia.sint.MML.CommonMML;



public class P2MChecker extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public int esProfesor = 0;  // 1 -> es el profesor,    0 -> es un alumno

	// en el init sólo se ordena crear el DocumentBuilder del CommonMMLChecker para llamar al servicio del alumno y analizar su respuesta

	public void init (ServletConfig servletConfig) 
				throws ServletException 
	{	
	      CommonMMLChecker.initLoggerMMLChecker(P2MChecker.class);
	      CommonMMLChecker.logMMLChecker("Init...");
	    
	    CommonMMLChecker.servletContextSintProf = servletConfig.getServletContext();
	    
	    // si hay parámetro 'passwd' en el contexto, se coge esa
	    // de lo contrario queda la fija que hay en CommonMMLChecker
	    
	    String passwdSintProf = CommonMMLChecker.servletContextSintProf.getInitParameter("passwd");
	    if (passwdSintProf != null) CommonSINT.PASSWD = passwdSintProf;
    	
	    CommonMMLChecker.createDocumentBuilder();
	}





	// procesa todas las solicitudes a este servlet

	public void doGet(HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException
	{
	    // si el parámetro p=si, es el profesor el que está ejecutando, de lo contrario es un alumno
	    // lo reflejamos en la variable CommonMMLChecker.esProfesor
		
	    esProfesor = 0;
	    
	    String profesor = request.getParameter("p");
	    if (profesor != null) {
    			if (profesor.equals("si")) esProfesor = 1;
	    }
	
	        
		// inicializamos un par de variables comunes
		
	    CommonMMLChecker.server_port = request.getServerName()+":"+request.getServerPort();
	    CommonMMLChecker.servicioProf = "http://"+CommonMMLChecker.server_port+CommonMMLChecker.PROF_CONTEXT+CommonMMLChecker.SERVICE_NAME;

	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();

	    // en 'screenP' se recibe la pantalla que se está solicitando

	    String screenP;
	    screenP = request.getParameter("screenP");

	    if (screenP == null) screenP="0"; // si screenP no existe, se está pidiendo la pantalla inicial (igual a screenP=0)

	    switch (screenP) {
			case "0":			// screenP=0, se está pidiendo la pantalla inicial del checker
				this.doGetHome(out);
				break;
					
			case "4":		// screenP=4, se está pidiendo un fichero de resultados de una corrección
				CommonMMLChecker.doGetRequestResultFile(request,out);
				break;
					
				
			// CONSULTA 1: la filmografía de un actor en una película de un año
					
			case "11":  	// screenP=11, se está pidiendo comprobar todas las llamadas del profesor
				Query1.doGetC1CheckSintprofCalls(request, out);
				break;
		
			case "12":    // screenP=12, se está pidiendo la pantalla para ordenar corregir un único servicio
				Query1.doGetC1CorrectOneForm(request,out,esProfesor);  
				break;
			case "121":	  // screenP=121, se pide el informe de la corrección de un servicio
				Query1.doGetC1CorrectOneReport(request,out,esProfesor);
				break;
		
			case "13":    // screenP=13, se está pidiendo la pantalla para ordenar corregir todos los servicios
				Query1.doGetC1CorrectAllForm(request,out);    
				break;
			case "131":		// screenP=131, se pide el informe de la corrección de todos los servicios
				Query1.doGetC1CorrectAllReport(request,out);
				break;
									
				
			// CONSULTA 2: peliculas de un actor (producidas en un país) en un idioma
	
			case "21":  	// screenP=21, se está pidiendo comprobar todas las llamadas del profesor
				Query2.doGetC2CheckSintprofCalls(request, out);
				break;
				
			case "22":     // screenP=22, se está pidiendo la pantalla para ordenar corregir un único servicio
				Query2.doGetC2CorrectOneForm(request,out,esProfesor);     
				break;
			case "221":	   // screenP=221, se pide el informe de la corrección de un servicio
				Query2.doGetC2CorrectOneReport(request,out,esProfesor);
				break;
					
					
			case "23":    // screenP=23, se está pidiendo la pantalla para ordenar corregir todos los servicios
				Query2.doGetC2CorrectAllForm(request,out);      
				break;
			case "231":	  // screenP=231, se pide el informe de la corrección de todos los servicios
				Query2.doGetC2CorrectAllReport(request,out);
				break;					
	    }	    
	    
	}



	// pantalla inicial para seleccionar acción

	public void doGetHome (PrintWriter out)
				throws IOException
	{
		out.println("<html>");
		CommonMMLChecker.printHead(out);
		CommonMMLChecker.printBodyHeader(out);
		
		out.println("<h2>CONSULTA 1 (diciembre): Filmografía de un miembro del reparto (de una película de un año)</h2>");
		
		out.println("<form>");
		out.println("<input type='hidden' name='screenP' value='12'>");
		if (esProfesor == 1) 
			out.println("<input type='hidden' name='p' value='si'>");
		out.println("<input class='menu' type='submit'  value='Corregir un servicio'>");
		out.println("</form>");
		
		// estas opciones sólo se le muestran al profesor
		
		if (esProfesor == 1) {
			out.println("<form>");
			out.println("<input type='hidden' name='screenP' value='13'>");
			out.println("<input type='hidden' name='p' value='si'>");
			out.println("<input class='menu'  type='submit'  value='Corregir todos los servicios'>");
			out.println("</form>");
			
			out.println("<form>");
			out.println("<input type='hidden' name='screenP' value='11'>");
			out.println("<input type='hidden' name='p' value='si'>");
			out.println("<input class='menu'  type='submit'  value='Comprobar las llamadas a sintprof'>");
			out.println("</form>");
		}

		
		out.println("<br><br>");



		out.println("<h2>CONSULTA 2 (junio): Películas de un actor (producidas en un país, en un determinado idioma)</h2>");
		
		out.println("<form>");
		out.println("<input type='hidden' name='screenP' value='22'>");
		if (esProfesor == 1) 
			out.println("<input type='hidden' name='p' value='si'>");
		out.println("<input class='menu' type='submit'  value='Corregir un servicio'>");
		out.println("</form>");
		
		// estas opciones sólo se le muestran al profesor
		
		if (esProfesor == 1) {
			out.println("<form>");
			out.println("<input type='hidden' name='screenP' value='23'>");
			out.println("<input type='hidden' name='p' value='si'>");
			out.println("<input class='menu'  type='submit'  value='Corregir todos los servicios'>");
			out.println("</form>");
			
			out.println("<form>");
			out.println("<input type='hidden' name='screenP' value='21'>");
			out.println("<input type='hidden' name='p' value='si'>");
			out.println("<input class='menu' type='submit'  value='Comprobar las llamadas a sintprof'>");
			out.println("</form>");
		}
		
		
		CommonSINT.printFoot(out, CommonMML.CREATED);
		out.println("</body></html>");
	}

}
