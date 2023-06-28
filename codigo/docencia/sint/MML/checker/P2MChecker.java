/****************************************************************
 *    SERVICIOS DE INTERNET
 *    EE TELECOMUNICACIÓN
 *    UNIVERSIDAD DE VIGO
 *
 *    Checker de la práctica de MML
 *
 *    Autor: Alberto Gil Solla
 *    Curso : 2021-2022
 ****************************************************************/


// pide los resultados de la práctica de un alumno y los compara con los que obtiene del profesor

// Query1 y Query2 emplean las clases Movie y Cast de la propia implementación de la práctica

package docencia.sint.MML2021.checker;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import docencia.sint.Common.CommonSINT;
import docencia.sint.Common.MsCC;



public class P2MChecker extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String ENGLISH_URI = "P2MeCheck";  // called URI for English version

	public int esProfesor = 0;  // 1 -> es el profesor,    0 -> es un alumno

	// en el init sólo se ordena crear el DocumentBuilder del CommonMMLChecker, que será usado para llamar al servicio del alumno y analizar su respuesta

	public void init (ServletConfig servletConfig)   throws ServletException
	{
      CommonMMLChecker.initLoggerMMLChecker(P2MChecker.class);
      CommonMMLChecker.logMMLChecker("Init...");

	    CommonMMLChecker.servletContextSintProf = servletConfig.getServletContext();

	    // si hay parámetro 'passwd' en el contexto (server.xml), se coge esa
	    // de lo contrario queda la fija que hay en CommonSINT

	    String passwdSintProf = CommonMMLChecker.servletContextSintProf.getInitParameter("passwd");
	    if (passwdSintProf != null) CommonSINT.PPWD = passwdSintProf;

	    CommonMMLChecker.createDocumentBuilder();
	}





	// procesa todas las solicitudes a este servlet

	public void doGet(HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException
	{

			// se averigua el idioma (y se asigna a lang) según la URI invocada
		  String cLang="es";
		  if (request.getRequestURI().endsWith(P2MChecker.ENGLISH_URI))
			  cLang = "en";

	    // si el parámetro p=si, es el profesor el que está ejecutando, de lo contrario es un alumno
	    // lo reflejamos en la variable CommonEAChecker.esProfesor

	    esProfesor = 0;

	    String profesor = request.getParameter("p");
	    if (profesor != null) {
    			if (profesor.equals("si")) esProfesor = 1;
	    }


		// inicializamos un par de variables comunes

	    CommonMMLChecker.server_port = request.getServerName()+":"+request.getServerPort();
	    CommonMMLChecker.servicioProf = "http://"+CommonMMLChecker.server_port+CommonMMLChecker.PROF_CONTEXT+CommonMMLChecker.SERVICE_NAME;

	    response.setCharacterEncoding("utf-8");

	    // en 'screenP' se recibe la pantalla que se está solicitando

	    String screenP;
	    screenP = request.getParameter("screenP");

	    if (screenP == null) screenP="0"; // si screenP no existe, se está pidiendo la pantalla inicial (igual a screenP=0)

	    switch (screenP) {
			case "0":		// screenP=0, se está pidiendo la pantalla inicial del checker
				this.doGetHome(request, response, cLang);
				break;

			case "4":		// screenP=4, se está pidiendo un fichero de resultados de una corrección
				CommonMMLChecker.doGetRequestResultFile(request, response, cLang);
				break;



			// CONSULTA 1: Reparto de una película de un año

			case "11":  	// screenP=11, se está pidiendo comprobar todas las llamadas del profesor
				Query1.doGetC1CheckSintprofCalls(request, response, cLang);
				break;

			case "12":    // screenP=12, se está pidiendo la pantalla para ordenar corregir un único servicio
				Query1.doGetC1CorrectOneForm(request, response, cLang, esProfesor);
				break;
			case "121":	  // screenP=121, se pide el informe de la corrección de un servicio
				Query1.doGetC1CorrectOneReport(request, response, cLang, esProfesor);
				break;

			// los siguientes 2 bloques hacen lo mismo de distinta forma

			// este bloque gestiona la corrección de todos los servicios, devolviendo una respuesta al terminar
			case "13":    // screenP=13, se está pidiendo la pantalla para ordenar corregir todos los servicios informando al final
				Query1.doGetC1CorrectAllForm(request, response, cLang);
				break;
			case "131":		// screenP=131, se pide el informe de la corrección de todos los servicios informando al final
				Query1.doGetC1CorrectAllReport(request, response, cLang);
				break;

			// este bloque gestiona la corrección de todos los servicios, devolviendo una respuesta cada vez que se corrige uno
			// utiliza la página jSP "/InformeResultadosCorreccionC1.jsp" que está en webapps
			case "14":    // screenP=14, se está pidiendo la pantalla para ordenar corregir todos los servicios informando uno a uno
				Query1.doGetC1CorrectAllForm2(request, response, cLang);
				break;
			case "141":		// screenP=141, se pide el informe de la corrección de todos los servicios informando uno a uno
				Query1.doGetC1CorrectAllReport2(request, response, cLang);
				break;
			case "142":		// screenP=142, se envía el informe de la corrección de todos los servicios uno a uno
				Query1.doGetC1CorrectAllReport2Run(request, response, cLang);
				break;



			// CONSULTA 2: movies de un cast en un lang 

			case "21":  	// screenP=21, se está pidiendo comprobar todas las llamadas del profesor
				Query2.doGetC2CheckSintprofCalls(request, response, cLang);
				break;

			case "22":     // screenP=22, se está pidiendo la pantalla para ordenar corregir un único servicio
				Query2.doGetC2CorrectOneForm(request,response,cLang,esProfesor);
				break;
			case "221":	   // screenP=221, se pide el informe de la corrección de un servicio
				Query2.doGetC2CorrectOneReport(request,response,cLang,esProfesor);
				break;

			// los siguientes 2 bloques hacen lo mismo de distinta forma

			// este bloque gestiona la corrección de todos los servicios, devolviendo una respuesta al terminar
			case "23":    // screenP=23, se está pidiendo la pantalla para ordenar corregir todos los servicios informando al final
				Query2.doGetC2CorrectAllForm(request,response,cLang);
				break;
			case "231":	  // screenP=231, se pide el informe de la corrección de todos los servicios informando al final
				Query2.doGetC2CorrectAllReport(request,response,cLang);
				break;

			// este bloque gestiona la corrección de todos los servicios, devolviendo una respuesta cada vez que se corrige uno
			// utiliza la página jSP "/InformeResultadosCorreccionC2.jsp" que está en webapps
			case "24":    // screenP=24, se está pidiendo la pantalla para ordenar corregir todos los servicios informando uno a uno
				Query2.doGetC2CorrectAllForm2(request,response,cLang);
				break;
			case "241":		// screenP=241, se pide el informe de la corrección de todos los servicios informando uno a uno
				Query2.doGetC2CorrectAllReport2(request,response,cLang);
				break;
			case "242":		// screenP=242, se envía el informe de la corrección de todos los servicios uno a uno
				Query2.doGetC2CorrectAllReport2Run(request,response,cLang);
				break;

	    }

	}



	// pantalla inicial para seleccionar acción

	public void doGetHome (HttpServletRequest request, HttpServletResponse response, String cLang)
				throws IOException
	{

		PrintWriter out = response.getWriter();

		out.println("<html>");
		CommonMMLChecker.printHead(out, cLang);
		CommonMMLChecker.printBodyHeader(out, cLang);

		
		
/*		
		out.println("<h2>"+MsMMLCh.getMsg(MsMMLCh.CMML05,cLang)+"</h2>");  // CONSULTA 1 (enero)...

		out.println("<form>");
		out.println("<input type='hidden' name='screenP' value='12'>");
		if (esProfesor == 1)
			   out.println("<input type='hidden' name='p' value='si'>");
		out.println("<input class='menu' type='submit'  value='"+MsCC.getMsg(MsCC.CC02,cLang)+"'>");  // CC02=corregir un servicio
		out.println("</form>");

		// estas opciones sólo se le muestran al profesor, por tanto no es necesario traducirlas

		if (esProfesor == 1) {
			out.println("<form>");
			out.println("<input type='hidden' name='screenP' value='13'>");
			out.println("<input type='hidden' name='p' value='si'>");
			out.println("<input class='menu'  type='submit'  value='Corregir todos los servicios'>");
			out.println("</form>");

			out.println("<form>");
			out.println("<input type='hidden' name='screenP' value='14'>");
			out.println("<input type='hidden' name='p' value='si'>");
			out.println("<input class='menu'  type='submit'  value='Corregir todos los servicios (uno a uno)'>");
			out.println("</form>");

			out.println("<form>");
			out.println("<input type='hidden' name='screenP' value='11'>");
			out.println("<input type='hidden' name='p' value='si'>");
			out.println("<input class='menu'  type='submit'  value='Comprobar las llamadas a sintprof'>");
			out.println("</form>");
		}
*/
		

/* 		out.println("<br><br><hr>");  */   // solo poner en FC
 

		out.println("<h2>"+MsMMLCh.getMsg(MsMMLCh.CMML06, cLang)+"</h2>");  // CONSULTA 2 (junio)...

		out.println("<form>");
		out.println("<input type='hidden' name='screenP' value='22'>");
		if (esProfesor == 1)
			out.println("<input type='hidden' name='p' value='si'>");
		out.println("<input class='menu' type='submit'  value='"+MsCC.getMsg(MsCC.CC02,cLang)+"'>");  // CC02=corregir un servicio
		out.println("</form>");

		// estas opciones sólo se le muestran al profesor, por tanto no es necesario traducirlas

		if (esProfesor == 1) {
			out.println("<form>");
			out.println("<input type='hidden' name='screenP' value='23'>");
			out.println("<input type='hidden' name='p' value='si'>");
			out.println("<input class='menu'  type='submit'  value='Corregir todos los servicios'>");
			out.println("</form>");

			out.println("<form>");
			out.println("<input type='hidden' name='screenP' value='24'>");
			out.println("<input type='hidden' name='p' value='si'>");
			out.println("<input class='menu'  type='submit'  value='Corregir todos los servicios (uno a uno)'>");
			out.println("</form>");

			out.println("<form>");
			out.println("<input type='hidden' name='screenP' value='21'>");
			out.println("<input type='hidden' name='p' value='si'>");
			out.println("<input class='menu' type='submit'  value='Comprobar las llamadas a sintprof'>");
			out.println("</form>");
		}


		CommonSINT.printFoot(out, MsMMLCh.CREATED);
		out.println("</body></html>");
	}

}
