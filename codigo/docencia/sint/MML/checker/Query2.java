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

// Implementación de la comprobación de la consulta 1 (reparto de una película de un año)

package docencia.sint.MML2021.checker;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

import docencia.sint.Common.CheckerFailure;
import docencia.sint.Common.CommonSINT;
import docencia.sint.Common.ExcepcionChecker;
import docencia.sint.Common.SintRandom;
import docencia.sint.Common.BeanResultados;
import docencia.sint.Common.MsCP;
import docencia.sint.Common.MsCC;
import docencia.sint.MML2021.MsMML;
import docencia.sint.MML2021.Movie;
import docencia.sint.MML2021.Cast;


public class Query2 {

	// nombres de los parámetros de esta consulta

	static final String NAMEP1 = "plang";
	static final String NAMEP2 = "pid";


	// COMPROBACIÓN DE LAS LLAMADAS A SINTPROF
	// es sólo para el profesor, no es necesario traducir
	// cLang es el idioma del checker

	public static void doGetC2CheckSintprofCalls(HttpServletRequest request, HttpServletResponse response, String cLang)
						throws IOException, ServletException
	{
		PrintWriter out = response.getWriter();

		CheckerFailure cf;
		int esProfesor = 1;  // sólo el profesor debería llegar aquí, podemos poner esto a 1

		out.println("<html>");
		CommonMMLChecker.printHead(out, cLang);
		CommonMMLChecker.printBodyHeader(out, cLang);

		out.println("<h2>Consulta 2</h2>");
		out.println("<h3>Comprobar las llamadas al servicio de sintprof</h3>");

		// doOneCheckUpStatus: hace una petición de estado al servicio del profesor para ver si está operativo

		try {
			CommonMMLChecker.doOneCheckUpStatus(request, "sintprof", CommonSINT.PPWD, cLang);
		}
		catch (ExcepcionChecker e) {
			cf = e.getCheckerFailure();
			out.println("<h4 style='color: red'>doGetC2CheckSintprofCalls: error al preguntar por el estado de sintprof: <br>");
			out.println(cf.toHTMLString());
			out.println("</h4>");
			CommonSINT.printEndPageChecker(out,  "0", esProfesor, MsMMLCh.CREATED, cLang);
			return;
		}

		out.println("<h4>Check Status OK</h4>");




		// empezamos por pedir los errores

		Element wrongDocs;

		try {
			wrongDocs = CommonMMLChecker.requestErrores("sintprof", CommonMMLChecker.servicioProf, CommonSINT.PPWD, cLang);
		}
		catch (ExcepcionChecker e) {
			cf = e.getCheckerFailure();
			out.println("<h4 style='color: red'>doGetC2CheckSintprofCalls: ExcepcionChecker pidiendo los errores: <br>");
			out.println(cf.toHTMLString());
			out.println("</h4>");
			CommonSINT.printEndPageChecker(out,  "0", esProfesor, MsMMLCh.CREATED, cLang);
			return;
		}
		catch (Exception ex) {
			out.println("<h4 style='color: red'>doGetC2CheckSintprofCalls: Exception pidiendo los errores: "+ex.toString()+"</h4>");
			CommonSINT.printEndPageChecker(out,  "0", esProfesor, MsMMLCh.CREATED, cLang);
			return;
		}

		NodeList nlWarnings = wrongDocs.getElementsByTagName("warning");
		NodeList nlErrors = wrongDocs.getElementsByTagName("error");
		NodeList nlFatalErrors = wrongDocs.getElementsByTagName("fatalerror");

		out.println("<h4>Errores OK: "+nlWarnings.getLength()+", "+nlErrors.getLength()+", "+nlFatalErrors.getLength()+"</h4>");


		// y ahora todas y cada una de las consultas

		// pedimos la lista de langs de sintprof

		String qs = "?auto=true&"+CommonSINT.PFASE+"=21&p="+CommonSINT.PPWD;
		String call = CommonMMLChecker.servicioProf+qs;

		ArrayList<String> pLangs;
		try {
			pLangs = Query2.requestLangs(call, cLang);
			out.println("<h4>Langs OK: "+pLangs.size()+"</h4>");
		}
		catch (ExcepcionChecker e) {
			cf = e.getCheckerFailure();
			out.println("<h4 style='color: red'>doGetC2CheckSintprofCalls: ExcepcionChecker pidiendo los langs a sintprof: <br>");
			out.println(cf.toHTMLString());
			out.println("</h4>");
			CommonSINT.printEndPageChecker(out,  "0", esProfesor, MsMMLCh.CREATED, cLang);
			return;
		}



		// vamos con la segunda fase, los casts de cada lang
		// el bucle X recorre todos los years


		String langActual;

		for (int x=0; x < pLangs.size(); x++) {
			String indent ="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

			langActual = pLangs.get(x);

			// pedimos los cast de ese lang de sintprof
			// aplicamos un URLencode por si el valor tiene caracteres no ASCII
			// no es el caso de langs

			try {
			    qs = "?auto=true&"+CommonSINT.PFASE+"=22&"+NAMEP1+"="+URLEncoder.encode(langActual, "utf-8")+"&p="+CommonSINT.PPWD;
				call = CommonMMLChecker.servicioProf+qs;
			}
			catch (UnsupportedEncodingException ex) {
				out.println("<h4 style='color: red'>doGetC2CheckSintprofCalls: UnsupportedEncodingException pidiendo los Cast: UTF-8 no soportado</h4>");
				CommonSINT.printEndPageChecker(out,  "0", esProfesor, MsMMLCh.CREATED, cLang);
				return;
			}

			ArrayList<Cast> pCasts;
			try {
				pCasts = requestCastsLang(call, cLang);
				out.println("<h4>"+indent+langActual+": "+pCasts.size()+"  OK</h4>");
			}
			catch (ExcepcionChecker e) {
				cf = e.getCheckerFailure();
				out.println("<h4 style='color: red'>doGetC2CheckSintprofCalls: ExcepcionChecker pidiendo los Cast de "+langActual+" a sintprof: <br>");
				out.println(cf.toHTMLString());
				out.println("</h4>");
				CommonSINT.printEndPageChecker(out,  "0", esProfesor, MsMMLCh.CREATED, cLang);
				return;
			}




	        // vamos con la tercera fase, las movies de un cast en un lang
	        // el bucle Y recorre todos los Cast

	        Cast castActual;

	        for (int y=0; y < pCasts.size(); y++) {

	        	indent = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

		        castActual = pCasts.get(y);

		    	// pedimos las movies de ese cast de ese lang de sintprof
				// aplicamos un URLencode por si el valor tiene caracteres no ASCII
				// no es el caso de lang
				// puede ser el caso de Cast

				try {
		    		qs = "?auto=true&"+CommonSINT.PFASE+"=23&"+NAMEP1+"="+URLEncoder.encode(langActual, "utf-8")+"&"+NAMEP2+"="+URLEncoder.encode(castActual.getID(), "utf-8")+"&p="+CommonSINT.PPWD;
					call = CommonMMLChecker.servicioProf+qs;
				}
				catch (UnsupportedEncodingException ex) {
					out.println("<h4 style='color: red'>doGetC2CheckSintprofCalls: UnsupportedEncodingException pidiendo las movies a sintprof: UTF-8 no soportado</h4>");
					CommonSINT.printEndPageChecker(out,  "0", esProfesor, MsMMLCh.CREATED, cLang);
					return;
				}

		    	ArrayList<Movie> pMovies;
		    	try {
		    		pMovies = requestMoviesCastLang(call, cLang);
		    		out.println("<h4>"+indent+langActual+"+"+castActual.getID()+": "+pMovies.size()+"  OK</h4>");
					Movie movieActual;
					for (int z=0; z < pMovies.size(); z++) {
						movieActual = pMovies.get(z);
						out.println("<h4>"+indent+langActual+"+"+castActual.getID()+"+"+movieActual.getTitle()+":   OK</h4>");
					}
		    	}
		    	catch (ExcepcionChecker e) {
					cf = e.getCheckerFailure();
		    		out.println("<h4 style='color: red'>doGetC2CheckSintprofCalls: ExcepcionChecker pidiendo las movies a sintprof: <br>");
					out.println(cf.toHTMLString());
		    		out.println("</h4>");
		    		CommonSINT.printEndPageChecker(out,  "0", esProfesor, MsMMLCh.CREATED, cLang);
		    		return;
		        }


	        } // for y

	    } // for x


		out.println("<h4>sintprof: Todo OK</h4>");

		CommonSINT.printEndPageChecker(out,  "0", esProfesor, MsMMLCh.CREATED, cLang);
	}








	// COMPROBACIÓN DEL SERVICIO DE UN ÚNICO ESTUDIANTE

	// pantalla para ordenar comprobar un estudiante (se pide su número de login)
	// debería unificarse en uno sólo con el de la consulta 1, son casi iguales

	public static void doGetC2CorrectOneForm (HttpServletRequest request, HttpServletResponse response, String cLang, int esProfesor)
						throws IOException
	{
		PrintWriter out = response.getWriter();

		out.println("<html>");
		CommonMMLChecker.printHead(out, cLang);

		out.println("<script>");

		out.println("function stopEnter (event) {");
		out.println("var x = event.which;");
		out.println("if (x === 13) {event.preventDefault();}"); // 13 es el ENTER
		out.println("}");

		out.println("function hideservice () {");
		out.println("var serviceAluElem = document.getElementById('serviceAluInput');");
		out.println("serviceAluElem.style.visibility='hidden';");
		out.println("var sendButton = document.getElementById('sendButton');");
		out.println("sendButton.disabled=true;");
		out.println("}");

		out.println("function showservice () {");
		out.println("var inputSintElement = document.getElementById('inputSint');");
		out.println("if ( ! inputSintElement.validity.valid ) return;");
		out.println("var inputSint = inputSintElement.value;");
		out.println("var sendButton = document.getElementById('sendButton');");
		out.println("sendButton.disabled=false;");

		out.println("var inputServiceElem = document.getElementById('serviceAluInput');");

		out.println("inputServiceElem.value = 'http://"+CommonMMLChecker.server_port+"/sint'+inputSint+'"+CommonMMLChecker.SERVICE_NAME+"';");
		out.println("inputServiceElem.style.visibility='visible';");
		out.println("}");
		out.println("</script>");

		CommonMMLChecker.printBodyHeader(out, cLang);

		out.println("<h2>"+MsCC.getMsg(MsCC.CC03,cLang)+" 1</h2>");   // CC03=Consulta
		out.println("<h3>"+MsCC.getMsg(MsCC.CC04,cLang)+"</h3>");   // CC04 = corrigiendo un servicio

		out.println("<form>");
		out.println("<input type='hidden' name='screenP' value='221'>");  // conduce a doGetC2CorrectOneReport

		out.println(MsCC.getMsg(MsCC.CC05,cLang)); // CC05 = "Introduzca el número de la cuenta SINT a comprobar: "
		out.println("<input id='inputSint' type='text' name='alumnoP' size='3' onfocus='hideservice();' onblur='showservice();' onkeypress='stopEnter(event);' pattern='[1-9]([0-9]{1,2})?' required> <br>");

		out.println(MsCC.getMsg(MsCC.CC06,cLang)); // CC06 = "URL del servicio del alumno: "
		out.println("<input style='visibility: hidden' id='serviceAluInput' type='text' name='servicioAluP' value='' size='40'><br>");

		if (esProfesor == 0) {
			// CC07 = "Passwd del servicio (10 letras o números): "
			out.println("<p>"+MsCC.getMsg(MsCC.CC07,cLang)+" <input id='passwdAlu' type='text' name='passwdAlu'  pattern='[A-Za-z0-9]{10}?' required> <br><br>");
		}
		else {
			out.println("<p><input type='hidden' name='p' value='si'>");
		}

		out.println("<p><input class='enviar' id='sendButton' disabled='true' type='submit' value='"+MsCP.getMsg(MsCP.CPC00,cLang)+"'>");  //CPC00=Enviar
		out.println("</form>");

		out.println("<form>");
		if (esProfesor == 1)
			out.println("<p><input type='hidden' name='p' value='si'>");
		out.println("<p><input class='home'  type='submit' value='"+MsCP.getMsg(MsCP.CPC01,cLang)+"'>");  //CPC01=Inicio
		out.println("</form>");

		CommonSINT.printFoot(out, MsMMLCh.CREATED);

		out.println("</body></html>");
	}






	// pantalla para informar de la corrección de un sintX (se recibe en 'alumnoP' su número de login X)
	// también recibe en servicioAlu el URL del servicio del alumno

	public static void doGetC2CorrectOneReport(HttpServletRequest request, HttpServletResponse response, String cLang, int esProfesor)
						throws IOException, ServletException
	{
		String currentMethod = Thread.currentThread().getStackTrace()[1].getMethodName();

		PrintWriter out = response.getWriter();

		out.println("<html>");
		CommonMMLChecker.printHead(out, cLang);
		CommonMMLChecker.printBodyHeader(out, cLang);

		out.println("<h2>"+MsCC.getMsg(MsCC.CC03,cLang)+" 2</h2>");  // CC03=Consulta
		out.println("<h3>"+MsCC.getMsg(MsCC.CC04,cLang)+"</h3>");  // CC04 = corrigiendo un servicio

		// leemos los datos del estudiante

		String alumnoP = request.getParameter("alumnoP");
		String servicioAluP = request.getParameter("servicioAluP");

		if ((alumnoP == null) || (servicioAluP == null)) {
			out.println("<h4 style='color: red'>"+currentMethod+": "+MsCC.getMsg(MsCC.CC40, cLang)+"</h4>");  // CC40 = falta uno de los parámetros (alumnoP, servicioAluP)
			CommonSINT.printEndPageChecker(out,  "22", esProfesor, MsMMLCh.CREATED, cLang);
			return;
		}

		String usuario="sint"+alumnoP;
		String passwdAlu, passwdRcvd;



		try {
			passwdAlu = CommonMMLChecker.getAluPasswd(usuario);
		}
		catch (ExcepcionChecker ex) {
			String codigo = ex.getCheckerFailure().getCodigo();
			if (codigo.equals("NOCONTEXT"))
				out.println("<h4 style='color: red'>"+currentMethod+": ExcepcionChecker: "+MsCC.getMsg(MsCC.CC39,cLang)+usuario+"</h4>"); // CC39 = Todavía no se ha creado el contexto del usuario, o este fue eliminado tras un error
			else
				out.println("<h4 style='color: red'>"+currentMethod+": ExcepcionChecker: "+MsCC.getMsg(MsCC.CC41,cLang)+usuario+"</h4>"); // CC41 = "Imposible recuperar la passwd del contexto de "
			CommonSINT.printEndPageChecker(out,  "22", esProfesor, MsMMLCh.CREATED, cLang);
			return;
		}

		if (esProfesor == 0) {  // lo está probando un alumno, es obligatorio que haya introducido su passwd
			passwdRcvd = request.getParameter("passwdAlu");   // leemos la passwd del alumno del parámetro

			if (passwdRcvd == null) {
				out.println("<h4 style='color: red'>"+currentMethod+": "+MsCC.getMsg(MsCC.CC42, cLang)+usuario+"</h4>");  // CC42 = No se ha recibido la passwd de
				CommonSINT.printEndPageChecker(out,  "22", esProfesor, MsMMLCh.CREATED, cLang);
				return;
			}

			if (!passwdAlu.equals(passwdRcvd)) {
				// CC10 = "La passwd proporcionada no coincide con la almacenada en el sistema para "
				out.println("<h4 style='color: red'>"+currentMethod+": "+MsCC.getMsg(MsCC.CC10,cLang)+usuario+"</h4>");
				CommonSINT.printEndPageChecker(out,  "22", esProfesor, MsMMLCh.CREATED, cLang);
				return;
			}

		}

		out.println("<h3>"+MsCC.getMsg(MsCC.CC08,cLang)+usuario+" ("+servicioAluP+")</h3>");  // CC08 = comprobando el servicio del usuario X


		// doOneCheckUpStatus: hace una petición de estado al servicio del profesor para ver si está operativo

		try {
			CommonMMLChecker.doOneCheckUpStatus(request, "sintprof", CommonSINT.PPWD, cLang);
		}
		catch (ExcepcionChecker e) {
			CheckerFailure cf = e.getCheckerFailure();
			out.println("<h4 style='color: red'>"+currentMethod+": ExcepcionChecker: "+MsCC.getMsg(MsCC.CC43,cLang)+"<br>"); // CC43 = Error al preguntar por el estado de la práctica del profesor
			out.println(cf.toHTMLString());
			out.println("</h4>");
			CommonSINT.printEndPageChecker(out,  "22", esProfesor, MsMMLCh.CREATED, cLang);
			return;
		}


		try {
			Query2.correctC2OneStudent(request, usuario, alumnoP, servicioAluP, passwdAlu, cLang);
		}
		catch (ExcepcionChecker e) {
			CheckerFailure cf = e.getCheckerFailure();
			out.println("<h4 style='color: red'>"+currentMethod+": "+MsCC.getMsg(MsCC.CC11,cLang)+usuario+" <br>"); // CC11 = "Resultado incorrecto comprobando el servicio de "
			out.println(cf.toHTMLString());
			out.println("</h4>");
			CommonSINT.printEndPageChecker(out,  "22", esProfesor, MsMMLCh.CREATED, cLang);
			return;
		}

		out.println("<h3>"+MsCC.getMsg(MsCC.CC09, cLang)+"OK</h3>"); // CC09 = "Resultado: "



		// terminamos con botones Atrás e Inicio

		CommonSINT.printEndPageChecker(out,  "22", esProfesor, MsMMLCh.CREATED, cLang);
	}




	// método que corrige la consulta 2 de un estudiante

    private static void correctC2OneStudent (HttpServletRequest request,String usuario, String aluNum, String servicioAluP, String passwdAlu, String cLang)
    			throws ExcepcionChecker
	{
    	CheckerFailure cf;
		String currentMethod = Thread.currentThread().getStackTrace()[1].getMethodName();

		// para la consulta directa final, vamos a escoger lang y cast al azar y a guardarlos en esas variables

		SintRandom.init(); // inicializamos el generador de números aleatorios
		int posrandom;

		String dqLang="";
		String dqCast="";

		// empezamos por comprobar los ficheros

		try {
			CommonMMLChecker.doOneCheckUpFiles(aluNum, "2", cLang);  // 2 es el número de la consulta
		}
		catch (ExcepcionChecker e) {
			cf = e.getCheckerFailure();
			cf.addMotivo(currentMethod+": ExcepcionChecker: "+MsCC.getMsg(MsCC.CC12, cLang));  // CC12 = "Error en los ficheros fuente"
			throw new ExcepcionChecker(cf);
		}


        // ahora comprobamos que el servicio está operativo

		try {
			CommonMMLChecker.doOneCheckUpStatus(request, usuario, passwdAlu, cLang);
		}
		catch (ExcepcionChecker e) {
			cf = e.getCheckerFailure();
			cf.addMotivo(currentMethod+": ExcepcionChecker: "+MsCC.getMsg(MsCC.CC13, cLang));  // CC13 = "Error al comprobar si el servicio del estudiante está operativo"
			throw new ExcepcionChecker(cf);
		}





		// ahora comprobamos los errores

		try {
			CommonMMLChecker.comparaErrores(usuario, servicioAluP, passwdAlu, cLang);
		}
		catch (ExcepcionChecker e) {
			cf = e.getCheckerFailure();
			cf.setCodigo("20_DIFS");
			cf.addMotivo(currentMethod+": ExcepcionChecker: "+MsCC.getMsg(MsCC.CC14,cLang));  // CC14 = "Diferencias en la lista de errores"
			throw new ExcepcionChecker(cf);
		}



		// y ahora todas y cada una de las consultas

		// pedimos la lista de years de sintprof

		String qs = "?auto=true&"+CommonSINT.PFASE+"=21&p="+CommonSINT.PPWD;
		String call = CommonMMLChecker.servicioProf+qs;

		ArrayList<String> pLangs;
		try {
			pLangs = Query2.requestLangs(call, cLang);
		}
		catch (ExcepcionChecker e) {
			cf = e.getCheckerFailure();
			cf.setCodigo("20_DIFS");
			cf.addMotivo(currentMethod+": ExcepcionChecker: "+MsMMLCh.getMsg(MsMMLCh.CMML21, cLang)+"langs (sintprof)"); // error solicitando lista de
			throw new ExcepcionChecker(cf);
		}


		// pedimos la lista de langs del sintX

		qs = "?auto=true&"+CommonSINT.PFASE+"=21&p="+passwdAlu;
		call = servicioAluP+qs;

		ArrayList<String> xLangs;
		try {
			xLangs = Query2.requestLangs(call, cLang);
		}
		catch (ExcepcionChecker e) {
			cf = e.getCheckerFailure();
			cf.setCodigo("20_DIFS");
			cf.addMotivo(currentMethod+": ExcepcionChecker: "+MsMMLCh.getMsg(MsMMLCh.CMML21, cLang)+"langs ("+usuario+")");  // error solicitando lista de
			throw new ExcepcionChecker(cf);
		}


		// comparamos las listas de sintprof y sintX

		try {
		     Query2.comparaLangs(usuario, pLangs, xLangs, cLang);
		}
		catch (ExcepcionChecker e) {
			cf = e.getCheckerFailure();
			cf.setUrl(call);
			cf.addMotivo(currentMethod+": ExcepcionChecker: "+MsMMLCh.getMsg(MsMMLCh.CMML22, cLang)+"langs");  // diferencias en la lista de
			throw new ExcepcionChecker(cf);
		}



		// las listas de langs son iguales
		// elegimos un lang al azar para la consulta directa final

		posrandom = SintRandom.getRandomNumber(0, pLangs.size()-1);
		dqLang = pLangs.get(posrandom);




		// vamos con la segunda fase, los casts de cada lang
		// el bucle X recorre todos los langs


		String langActual;

		for (int x=0; x < pLangs.size(); x++) {

			langActual = pLangs.get(x);

			// pedimos los casts de ese lang de sintprof
			// aplicamos URLencode por si hay caracteres no ASCII
			// no es el caso de lang

			try {
				qs = "?auto=true&"+CommonSINT.PFASE+"=22&"+NAMEP1+"="+URLEncoder.encode(langActual, "utf-8")+"&p="+CommonSINT.PPWD;
				call = CommonMMLChecker.servicioProf+qs;
			}
			catch (UnsupportedEncodingException ex) {
				CommonMMLChecker.logCall(call);

				cf = new CheckerFailure(call, "20_DIFS", ex.toString());
				cf.addMotivo(currentMethod+": UnsupportedEncodingException: "+MsMMLCh.getMsg(MsMMLCh.CMML23, cLang)+"casts (sintprof)");  // error creando solicitud lista de
				throw new ExcepcionChecker(cf);
			}

			ArrayList<Cast> pCasts;
			try {
				pCasts = requestCastsLang(call, cLang);
			}
			catch (ExcepcionChecker e) {
				cf = e.getCheckerFailure();
				cf.setCodigo("20_DIFS");
				cf.addMotivo(currentMethod+": ExcepcionChecker: "+MsMMLCh.getMsg(MsMMLCh.CMML21, cLang)+"casts - "+langActual+" (sintprof)"); // error solicitando lista de
				throw new ExcepcionChecker(cf);
			}


			// pedimos los casts de ese lang del sintX
			// aplicamos URLencode por si hay caracteres no ASCII
			// no es el caso de lang

			try {
				qs = "?auto=true&"+CommonSINT.PFASE+"=22&"+NAMEP1+"="+URLEncoder.encode(langActual, "utf-8")+"&p="+passwdAlu;
				call = servicioAluP+qs;
			 }
			 catch (UnsupportedEncodingException ex) {
				 CommonMMLChecker.logCall(call);

 				 cf = new CheckerFailure(call, "20_DIFS", ex.toString());
 				 cf.addMotivo(currentMethod+": UnsupportedEncodingException: "+MsMMLCh.getMsg(MsMMLCh.CMML23, cLang)+"casts ("+usuario+")"); // error creando solicitud lista de
 				 throw new ExcepcionChecker(cf);
			 }

			ArrayList<Cast> xCasts;
			try {
				xCasts = requestCastsLang(call, cLang);
			}
			catch (ExcepcionChecker e) {
				cf = e.getCheckerFailure();
				cf.setCodigo("20_DIFS");
				cf.addMotivo(currentMethod+": ExcepcionChecker: "+MsMMLCh.getMsg(MsMMLCh.CMML21, cLang)+"casts - "+langActual+" ("+usuario+")");  // error solicitando lista de
				throw new ExcepcionChecker(cf);
			}


			// comparamos las listas de sintprof y sintX

			try {
				Query2.comparaCasts(usuario, langActual, pCasts, xCasts, cLang);
			}
			catch (ExcepcionChecker e) {
				cf = e.getCheckerFailure();
				cf.setUrl(call);
				cf.addMotivo(currentMethod+": ExcepcionChecker: "+MsMMLCh.getMsg(MsMMLCh.CMML22, cLang)+"casts ("+langActual+")"); // diferencias en la lista de
				throw new ExcepcionChecker(cf);
			}



			// las listas de casts para este lang son iguales
		    // si este lang es el de la consulta directa final, elegimos un cast al azar para la consulta directa final


	        if (langActual.equals(dqLang)) {
	            posrandom = SintRandom.getRandomNumber(0, pCasts.size()-1);
	            Cast pCas = pCasts.get(posrandom);
	          	dqCast = pCas.getID();
	        }



	        // vamos con la tercera fase, las movies de un cast
	        // el bucle Y recorre todos los cast

	        Cast castActual;

	        for (int y=0; y < pCasts.size(); y++) {

	        	castActual = pCasts.get(y);

	    		// pedimos las movies de ess cast de ese lang de sintprof
				// aplicamos URLencode por si hay caracteres no ASCII
				// no es el caso de lang
				// no es el caso de cast (pid)

				try {
	    		    qs = "?auto=true&"+CommonSINT.PFASE+"=23&"+NAMEP1+"="+URLEncoder.encode(langActual, "utf-8")+"&"+NAMEP2+"="+URLEncoder.encode(castActual.getID(), "utf-8")+"&p="+CommonSINT.PPWD;
	    		    call = CommonMMLChecker.servicioProf+qs;
				}
				catch (UnsupportedEncodingException ex) {
					CommonMMLChecker.logCall(call);

	  				cf = new CheckerFailure(call, "20_DIFS", ex.toString());
	  				cf.addMotivo(currentMethod+": UnsupportedEncodingException: "+MsMMLCh.getMsg(MsMMLCh.CMML23, cLang)+"movies (sintprof)"); // error creando solicitud lista de
	  				throw new ExcepcionChecker(cf);
				}

	    		ArrayList<Movie> pMovies;
	    		try {
	    			pMovies = requestMoviesCastLang(call, cLang);
	    		}
	    		catch (ExcepcionChecker e) {
	    			cf = e.getCheckerFailure();
					cf.setCodigo("20_DIFS");
					cf.addMotivo(currentMethod+": ExcepcionChecker: "+MsMMLCh.getMsg(MsMMLCh.CMML21, cLang)+"movies (sintprof)");  // error solicitando lista de
					throw new ExcepcionChecker(cf);
	    		}


	    		// pedimos las movies de ese cast de ese lang del sintX
				// aplicamos URLencode por si hay caracteres no ASCII
				// no es el caso de lang
				// no es el caso de cast (pid)

				try {
					qs = "?auto=true&"+CommonSINT.PFASE+"=23&"+NAMEP1+"="+URLEncoder.encode(langActual, "utf-8")+"&"+NAMEP2+"="+URLEncoder.encode(castActual.getID(), "utf-8")+"&p="+passwdAlu;
					call = servicioAluP+qs;
				}
 				catch (UnsupportedEncodingException ex) {
					CommonMMLChecker.logCall(call);

					cf = new CheckerFailure(call, "20_DIFS", ex.toString());
					cf.addMotivo(currentMethod+": UnsupportedEncodingException: "+MsMMLCh.getMsg(MsMMLCh.CMML23, cLang)+"movies ("+usuario+")"); // error creando solicitud lista de
					throw new ExcepcionChecker(cf);
 				}

	    		ArrayList<Movie> xMovies;
	    		try {
	    			xMovies = requestMoviesCastLang(call, cLang);
	    		}
	    		catch (ExcepcionChecker e) {
	    			cf = e.getCheckerFailure();
					cf.setCodigo("20_DIFS");
					cf.addMotivo(currentMethod+": ExcepcionChecker: "+MsMMLCh.getMsg(MsMMLCh.CMML21, cLang)+"movies ("+usuario+")"); // error solicitando lista de
					throw new ExcepcionChecker(cf);
	    		}


	    		// comparamos las listas de sintprof y sintX

	    		try {
	    			Query2.comparaMovies(usuario, langActual, castActual.getID(), pMovies, xMovies, cLang);
				}
				catch (ExcepcionChecker e) {
					cf = e.getCheckerFailure();
					cf.setUrl(call);
					cf.addMotivo(currentMethod+": ExcepcionChecker: "+MsMMLCh.getMsg(MsMMLCh.CMML22, cLang)+"movies ("+langActual+", "+castActual.getID()+")"); // diferencias en la lista de
					throw new ExcepcionChecker(cf);
				}


	        } // for y

	    } // for x


		// finalmente la consulta directa

		try {
			Query2.checkDirectQueryC2(CommonMMLChecker.servicioProf, usuario, servicioAluP, dqLang, dqCast, passwdAlu, cLang);
		}
		catch (ExcepcionChecker e) {
			cf = e.getCheckerFailure();
			cf.setCodigo("20_DIFS");
			cf.addMotivo(currentMethod+": ExcepcionChecker: "+MsMMLCh.getMsg(MsMMLCh.CMML24, cLang));  // resultado erróneo de la consuta directa
			throw new ExcepcionChecker(cf);
		}

		// todas las consultas coincidieron

	    return;
	}









	// comprueba que las consultas directas son iguales

	private static void checkDirectQueryC2(String servicioProf, String usuario, String servicioAluP, String lang, String idCast, String passwdAlu, String cLang)
		throws ExcepcionChecker
	{
		String currentMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
		CheckerFailure cf;
		ArrayList<Movie> pMovies, xMovies;
		String qs="", call="";

		// primero comprobamos que responde con el error apropiado si falta algún parámetro

  		try {
  			CommonMMLChecker.checkLackParam(servicioAluP, passwdAlu, "23", NAMEP1, lang, NAMEP2, idCast, cLang);
		}
		catch (ExcepcionChecker e) {
			cf = e.getCheckerFailure();
			cf.setCodigo("20_DIFS");
			cf.addMotivo(currentMethod+": ExcepcionChecker: "+MsCC.getMsg(MsCC.CC64, cLang));  // CC64 = "No responde correctamente si falta algún parámetro obligatorio"
			throw new ExcepcionChecker(cf);
		}


 		// ahora comprobamos que los resultados son correctos

		// pedimos la consulta a sintprof
		// aplicamos URLencode por si hay caracteres no ASCII
		// no es el caso de lang
		// no es el caso de idCast

		try {
		    qs = "?auto=true&"+CommonSINT.PFASE+"=23&"+NAMEP1+"="+URLEncoder.encode(lang, "utf-8")+"&"+NAMEP2+"="+URLEncoder.encode(idCast, "utf-8")+"&p="+CommonSINT.PPWD;
			call = CommonMMLChecker.servicioProf+qs;
		}
		catch (UnsupportedEncodingException ex) {
			CommonMMLChecker.logCall(call);

			cf = new CheckerFailure(call, "20_DIFS", ex.toString());
			cf.addMotivo(currentMethod+": UnsupportedEncodingException: "+MsMMLCh.getMsg(MsMMLCh.CMML23, cLang)+"movies (sintprof)"); // error creando solicitud lista de
			throw new ExcepcionChecker(cf);
		}

		try {
   			pMovies = Query2.requestMoviesCastLang(call, cLang);
		}
		catch (ExcepcionChecker e) {
			cf = e.getCheckerFailure();
			cf.setCodigo("20_DIFS");
			cf.addMotivo(currentMethod+": ExcepcionChecker: "+MsMMLCh.getMsg(MsMMLCh.CMML26, cLang)+" (sintprof)");  // error pidiendo consulta directa
			throw new ExcepcionChecker(cf);
		}

		// pedimos la consulta al sintX
		// aplicamos URLencode por si hay caracteres no ASCII
		// no es el caso de year
		// no es el caso de idCast

		try {
		    qs = "?auto=true&"+CommonSINT.PFASE+"=23&"+NAMEP1+"="+URLEncoder.encode(lang, "utf-8")+"&"+NAMEP2+"="+URLEncoder.encode(idCast, "utf-8")+"&p="+passwdAlu;
			call = servicioAluP+qs;
		}
		catch (UnsupportedEncodingException ex) {
			CommonMMLChecker.logCall(call);

			cf = new CheckerFailure(call, "20_DIFS", ex.toString());
			cf.addMotivo(currentMethod+": UnsupportedEncodingException:"+MsMMLCh.getMsg(MsMMLCh.CMML23, cLang)+" movies ("+usuario+")"); // // error creando solicitud lista de
			throw new ExcepcionChecker(cf);
		}

		try {
  			xMovies = Query2.requestMoviesCastLang(call, cLang);
		}
		catch (ExcepcionChecker e) {
			cf = e.getCheckerFailure();
			cf.setCodigo("20_DIFS");
			cf.addMotivo(currentMethod+": ExcepcionChecker: "+MsMMLCh.getMsg(MsMMLCh.CMML26, cLang)+" ("+usuario+")"); // error pidiendo consulta directa
			throw new ExcepcionChecker(cf);
		}


		// comparamos las listas de movies resultado de sintprof y sintX

		try {
			Query2.comparaMovies(usuario, lang, idCast, pMovies, xMovies, cLang);
		}
		catch (ExcepcionChecker e) {
			cf = e.getCheckerFailure();
			cf.setUrl(call);
			cf.addMotivo(currentMethod+": ExcepcionChecker: "+MsMMLCh.getMsg(MsMMLCh.CMML22, cLang)+" movies");  // error Diferencias en la lista de
			throw new ExcepcionChecker(cf);
		}


		// todo coincidió

		return;
	}












	// COMPROBACIÓN DEL SERVICIO DE TODOS LOS ESTUDIANTES

	// pantalla para comprobar todos los estudiantes, se pide el número de cuentas a comprobar (corregir su práctica)

	public static void doGetC2CorrectAllForm (HttpServletRequest request, HttpServletResponse response, String cLang) throws IOException
	{
		PrintWriter out = response.getWriter();
		int esProfesor = 1;

		out.println("<html>");
		CommonMMLChecker.printHead(out, cLang);
		CommonMMLChecker.printBodyHeader(out, cLang);

		out.println("<h2>Consulta 1</h2>");
		out.println("<h3>Corrección de todos los servicios</h3>");

		out.println("<form>");
		out.println("<input type='hidden' name='screenP' value='231'>");  // CAMBIAR

		out.println("Introduzca el número de cuentas SINT a corregir: ");
		out.println("<input id='inputNumCuentas' type='text' name='numCuentasP' size='3' pattern='[1-9]([0-9]{1,2})?' required>");

		if (esProfesor == 1)
			out.println("<p><input type='hidden' name='p' value='si'>");

		out.println("<input class='enviar' type='submit' value='"+MsCP.getMsg(MsCP.CPC00, cLang)+"' >");  //CPC00=Enviar
		out.println("</form>");

		out.println("<form>");
		if (esProfesor == 1)
			out.println("<p><input type='hidden' name='p' value='si'>");
		out.println("<p><input class='home' type='submit' value='"+MsCP.getMsg(MsCP.CPC01, cLang)+"'>");  //CPC01=Inicio
		out.println("</form>");

		CommonSINT.printFoot(out, MsMMLCh.CREATED);

		out.println("</body></html>");
	}




	// pantalla para corregir a todos los estudiantes
	// presenta en pantalla diversas listas según el resultado de cada alumno
	// se crea un fichero con el resultado de cada corrección (webapps/CORRECCIONES/sintX/fecha-corrección)
	// se devuelven enlaces a esos ficheros

	public static void doGetC2CorrectAllReport(HttpServletRequest request, HttpServletResponse response, String cLang)
						throws IOException, ServletException
	{
		PrintWriter out = response.getWriter();
		int esProfesor = 1;

		out.println("<html>");
		CommonMMLChecker.printHead(out, cLang);
		CommonMMLChecker.printBodyHeader(out, cLang);

		out.println("<h2>Consulta 2</h2>");

		// si no se recibe el parámetro con el número de cuentas no se hace nada

		String numCuentasP = request.getParameter("numCuentasP");
		if (numCuentasP == null) {
			out.println("<h4>Error: no se ha recibido el número de cuentas</h4>");
			CommonSINT.printEndPageChecker(out,  "23", esProfesor, MsMMLCh.CREATED, cLang);
			return;
		}

		int numCuentas=0;

		try {
			numCuentas = Integer.parseInt(numCuentasP);
		}
		catch (NumberFormatException e) {
			out.println("<h4>Error: el número de cuentas recibido no es un número válido</h4>");
			CommonSINT.printEndPageChecker(out,  "23", esProfesor, MsMMLCh.CREATED, cLang);
			return;
		}

		if (numCuentas < 1) {
			out.println("<h4>Error: el número de cuentas recibido es menor que uno</h4>");
			CommonSINT.printEndPageChecker(out,  "23", esProfesor, MsMMLCh.CREATED, cLang);
			return;
		}





		// todos los parámetros están bien


		out.println("<h3>Corrección de todos los servicios ("+numCuentas+")</h3>");


		// listas para almacenar en qué caso está cada alumno

		ArrayList<Integer> usersOK = new ArrayList<Integer>();	   // corrección OK

		ArrayList<Integer> usersE1NoContext= new ArrayList<Integer>();       // contexto no existe

		ArrayList<Integer> usersE2FileNotFound= new ArrayList<Integer>();    // servlet no declarado
		ArrayList<Integer> usersE3Encoding = new ArrayList<Integer>();       // respuesta mala codificación
		ArrayList<Integer> usersE4IOException= new ArrayList<Integer>();    // falta clase servlet o este produce una excepción
		ArrayList<Integer> usersE5Bf = new ArrayList<Integer>();            // respuesta mal formada
		ArrayList<Integer> usersE6Invalid = new ArrayList<Integer>();       // respuesta inválida
		ArrayList<Integer> usersE7Error = new ArrayList<Integer>();         // error desconocido
		ArrayList<Integer> usersE8OkNoPasswd = new ArrayList<Integer>();    // responde bien sin necesidad de passwd
		ArrayList<Integer> usersE9BadPasswd = new ArrayList<Integer>();    // la passwd es incorrecta
		ArrayList<Integer> usersE10BadAnswer = new ArrayList<Integer>();    // la respuesta no es la esperada
		ArrayList<Integer> usersE11NoPasswd = new ArrayList<Integer>();    // el usuario no tiene passwd
		ArrayList<Integer> usersE12Files = new ArrayList<Integer>();    // el usuario no tiene passwd

		ArrayList<Integer> usersE20Diff = new ArrayList<Integer>();	   // las peticiones del alumno tienen diferencias respecto a las del profesor

		String servicioAlu;

		// lista para almacenar el nombre del fichero de cada cuenta

		ArrayList<String> usersCompareResultFile = new ArrayList<String>();

		// variables para crear y escribir los ficheros
		File  folder, fileUser;
		BufferedWriter bw;
		Date fecha;

		// si no existe, se crea el directorio de las CORRECCIONES

		String correccionesPath = CommonMMLChecker.servletContextSintProf.getRealPath("/")+"CORRECCIONES";

		folder = new File(correccionesPath);
		if (!folder.exists())
			folder.mkdir();

		// vamos a por las cuentas, de una en una

		String sintUser;

		bucle:
		for (int x=1; x <= numCuentas; x++) {

			sintUser="sint"+x;

			// si no existe, se crea el directorio del alumno

			folder = new File(correccionesPath+"/"+sintUser);
			if (!folder.exists())
				folder.mkdir();

			// se crea el fichero donde se almacenará esta corrección

			fecha = new Date();
			String nombreFicheroCorreccion = correccionesPath+"/"+sintUser+"/"+fecha.toString();
			fileUser = new File(nombreFicheroCorreccion);
			usersCompareResultFile.add(nombreFicheroCorreccion);
			bw = new BufferedWriter(new FileWriter(fileUser));


			// Comienza la comprobación del alumno


			// leemos la passwd del alumno

		  String passwdAlu;

			try {
				passwdAlu = CommonMMLChecker.getAluPasswd(sintUser);
			}
			catch (ExcepcionChecker ex) {
				String codigo = ex.getCheckerFailure().getCodigo();
				if (codigo.equals("NOCONTEXT")) {
					bw.write("No hay contexto");
					usersE1NoContext.add(x);
				}
				else {
					bw.write("No hay passwd");
					usersE11NoPasswd.add(x);
				}
				bw.close();
				continue bucle;
			}


			servicioAlu = "http://"+CommonMMLChecker.server_port+"/"+sintUser+CommonMMLChecker.SERVICE_NAME;

			try {
				Query2.correctC2OneStudent(request, sintUser, Integer.toString(x), servicioAlu, passwdAlu, cLang);
				bw.write("OK");
				bw.close();
				usersOK.add(x);
			}
			catch (ExcepcionChecker e) {
				CheckerFailure cf = e.getCheckerFailure();

			    switch (cf.getCodigo()) {

				case "01_NOCONTEXT":   // el contexto no está declarado o no existe su directorio
					bw.write(cf.toString());
					bw.close();
					usersE1NoContext.add(x);
					continue bucle;
				case "02_FILENOTFOUND":   // el servlet no está declarado.
					bw.write(cf.toString());
					bw.close();
					usersE2FileNotFound.add(x);
					continue bucle;
				case "03_ENCODING":   // la secuencia de bytes recibida UTF-8 está malformada
					bw.write(cf.toString());
					bw.close();
					usersE3Encoding.add(x);
					continue bucle;
				case "04_IOEXCEPTION":    // la clase del servlet no está o produjo una excepción
					bw.write(cf.toString());
					bw.close();
					usersE4IOException.add(x);
					continue bucle;
				case "05_BF":   // la respuesta no es well-formed
					bw.write(cf.toString());
					bw.close();
					usersE5Bf.add(x);
					continue bucle;
				case "06_INVALID":   // la respuesta es inválida
					bw.write(cf.toString());
					bw.close();
					usersE6Invalid.add(x);
					continue bucle;
				case "07_ERRORUNKNOWN":   // error desconocido
					bw.write(cf.toString());
					bw.close();
					usersE7Error.add(x);
					continue bucle;
				case "08_OKNOPASSWD":   // responde bien incluso sin passwd
					bw.write(cf.toString());
					bw.close();
					usersE8OkNoPasswd.add(x);
					continue bucle;
				case "09_BADPASSWD":   // la passwd es incorrecta
					bw.write(cf.toString());
					bw.close();
					usersE9BadPasswd.add(x);
					continue bucle;
				case "10_BADANSWER":   // la respuesta es inesperada
					bw.write(cf.toString());
					bw.close();
					usersE10BadAnswer.add(x);
					continue bucle;
			    case "12_FILES":
					bw.write(cf.toString());
					bw.close();
					usersE12Files.add(x);
					continue bucle;
				case "20_DIFS":
					bw.write(cf.toString());
					bw.close();
					usersE20Diff.add(x);
					continue bucle;
				default:      // error desconocido
					bw.write("Respuesta desconocida de la corrección:\n"+cf.toString());
					bw.close();
					usersE7Error.add(x);
					continue bucle;
			   } // switch
			}  // catch
		} // for

		// Breve resumen de los resultados por pantalla, con enlaces a los ficheros

		int numAlu;
		String fileAlu;

		if (usersOK.size() >0) {
			out.print("<h4 style='color: green'>Servicios OK ("+usersOK.size()+"): ");
			for (int x=0; x < usersOK.size(); x++) {
				numAlu = usersOK.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}

		if (usersE12Files.size() >0) {
			out.print("<h4 style='color: red'>Servicios con errores en los ficheros ("+usersE12Files.size()+"): ");
			for (int x=0; x < usersE12Files.size(); x++) {
				numAlu = usersE12Files.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}

		if (usersE20Diff.size() >0) {
			out.print("<h4 style='color: red'>Servicios con diferencias respecto a los resultados esperados ("+usersE20Diff.size()+"): ");
			for (int x=0; x < usersE20Diff.size(); x++) {
				numAlu = usersE20Diff.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}

		if (usersE10BadAnswer.size() >0) {
			out.print("<h4 style='color: red'>Servicios que responden de forma inesperada ("+usersE10BadAnswer.size()+"): ");
			for (int x=0; x < usersE10BadAnswer.size(); x++) {
				numAlu = usersE10BadAnswer.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}

		if (usersE9BadPasswd.size() >0) {
			out.print("<h4 style='color: red'>Servicios que no reconocen la passwd ("+usersE9BadPasswd.size()+"): ");
			for (int x=0; x < usersE9BadPasswd.size(); x++) {
				numAlu = usersE9BadPasswd.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}

		if (usersE8OkNoPasswd.size() >0) {
			out.print("<h4 style='color: red'>Servicios que responden bien sin necesidad de passwd ("+usersE8OkNoPasswd.size()+"): ");
			for (int x=0; x < usersE8OkNoPasswd.size(); x++) {
				numAlu = usersE8OkNoPasswd.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}

		if (usersE6Invalid.size() >0) {
			out.print("<h4 style='color: red'>Servicios con respuesta inválida a la solicitud de estado ("+usersE6Invalid.size()+"): ");
			for (int x=0; x < usersE6Invalid.size(); x++) {
				numAlu = usersE6Invalid.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}

		if (usersE5Bf.size() >0) {
			out.print("<h4 style='color: red'>Servicios con respuesta mal formada a la solicitud de estado ("+usersE5Bf.size()+"): ");
			for (int x=0; x < usersE5Bf.size(); x++) {
				numAlu = usersE5Bf.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}

		if (usersE4IOException.size() >0) {
			out.print("<h4 style='color: red'>Servicios donde falta la clase del servlet o éste produjo una excepción ("+usersE4IOException.size()+"): ");
			for (int x=0; x < usersE4IOException.size(); x++) {
				numAlu = usersE4IOException.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}

		if (usersE3Encoding.size() >0) {
			out.print("<h4 style='color: red'>Servicios que responden con una codificación incorrecta a la solicitud de estado ("+usersE3Encoding.size()+"): ");
			for (int x=0; x < usersE3Encoding.size(); x++) {
				numAlu = usersE3Encoding.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}

		if (usersE2FileNotFound.size() >0) {
			out.print("<h4 style='color: red'>Servicios sin el servlet declarado ("+usersE2FileNotFound.size()+"): ");
			for (int x=0; x < usersE2FileNotFound.size(); x++) {
				numAlu = usersE2FileNotFound.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}

		if (usersE11NoPasswd.size() >0) {
			out.print("<h4 style='color: red'>Servicios que no tienen passwd ("+usersE11NoPasswd.size()+"): ");
			for (int x=0; x < usersE11NoPasswd.size(); x++) {
				numAlu = usersE11NoPasswd.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}

		if (usersE1NoContext.size() >0) {
			out.print("<h4 style='color: red'>Servicios sin contexto ("+usersE1NoContext.size()+"): ");
			for (int x=0; x < usersE1NoContext.size(); x++) {
				numAlu = usersE1NoContext.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}

		if (usersE7Error.size() >0) {
			out.print("<h4 style='color: red'>Servicios con algún error desconocido ("+usersE7Error.size()+"): ");
			for (int x=0; x < usersE7Error.size(); x++) {
				numAlu = usersE7Error.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}

		CommonSINT.printEndPageChecker(out,  "23", esProfesor, MsMMLCh.CREATED, cLang);
	}




    // para corregir todos los servicios uno a uno
	public static void doGetC2CorrectAllForm2 (HttpServletRequest request, HttpServletResponse response, String cLang) throws IOException
	{
		PrintWriter out = response.getWriter();
		int esProfesor = 1;

		out.println("<html>");
		CommonMMLChecker.printHead(out, cLang);
		CommonMMLChecker.printBodyHeader(out, cLang);

		out.println("<h2>Consulta 2</h2>");
		out.println("<h3>Corrección de todos los servicios uno a uno</h3>");

		out.println("<form>");
		out.println("<input type='hidden' name='screenP' value='241'>"); // CAMBIAR

		out.println("Introduzca el número de cuentas SINT a corregir: ");
		out.println("<input id='inputNumCuentas' type='text' name='numCuentasP' size='3' pattern='[1-9]([0-9]{1,2})?' required>");

		if (esProfesor == 1)
			out.println("<p><input type='hidden' name='p' value='si'>");

		out.println("<input class='enviar' type='submit' value='"+MsCP.getMsg(MsCP.CPC00, cLang)+"' >");  //CPC00=Enviar
		out.println("</form>");

		out.println("<form>");
		if (esProfesor == 1)
			out.println("<p><input type='hidden' name='p' value='si'>");
		out.println("<p><input class='home' type='submit' value='"+MsCP.getMsg(MsCP.CPC01, cLang)+"'>");  //CPC01=Inicio
		out.println("</form>");

		CommonSINT.printFoot(out, MsMMLCh.CREATED);

		out.println("</body></html>");
	}




	public static void doGetC2CorrectAllReport2(HttpServletRequest request, HttpServletResponse response, String cLang)
						throws IOException, ServletException
	{
		PrintWriter out;
		int esProfesor = 1;

		// si no se recibe el parámetro con el número de cuentas no se hace nada

		String numCuentasP = request.getParameter("numCuentasP");

		if (numCuentasP == null) {
		   out = response.getWriter();
		   out.println("<html>");
		   CommonMMLChecker.printHead(out, cLang);
		   CommonMMLChecker.printBodyHeader(out, cLang);

		   out.println("<h4>Error: no se ha recibido el número de cuentas</h4>");
		   CommonSINT.printEndPageChecker(out,  "24", esProfesor, MsMMLCh.CREATED, cLang);
		   return;
		}

		int numCuentas=0;

		try {
		   numCuentas = Integer.parseInt(numCuentasP);
		}
		catch (NumberFormatException e) {
		   out = response.getWriter();
		   out.println("<html>");
		   CommonMMLChecker.printHead(out, cLang);
		   CommonMMLChecker.printBodyHeader(out, cLang);

		   out.println("<h4>Error: el número de cuentas recibido no es un número válido</h4>");
		   CommonSINT.printEndPageChecker(out,  "24", esProfesor, MsMMLCh.CREATED, cLang);
		   return;
		}


		if (numCuentas < 1) {
		   out = response.getWriter();
		   out.println("<html>");
		   CommonMMLChecker.printHead(out, cLang);
		   CommonMMLChecker.printBodyHeader(out, cLang);

		   out.println("<h4>Error: el número de cuentas recibido es menor que uno</h4>");
		   CommonSINT.printEndPageChecker(out,  "24", esProfesor, MsMMLCh.CREATED, cLang);
		   return;
		}


		BeanResultados miBean = new BeanResultados();
		miBean.setCssFile(CommonMMLChecker.CSS_FILE);
		miBean.setTitle(MsMMLCh.getMsg(MsMMLCh.CMML00, cLang));
		miBean.setLang(MsMML.XML_LANGUAGE);
		miBean.setCurso(MsMML.CURSO);
		miBean.setNumCuentas(numCuentas);
		miBean.setEsProfesor(esProfesor);
		miBean.setCreated(MsMMLCh.CREATED);

		request.setAttribute("db", miBean);

		try {
		    // ServletContext sc = getServletContext();

			// transfiere el control a una página JSP con SSE, para ser notificada de cada corrección terminada
		    RequestDispatcher dispatcher = CommonMMLChecker.servletContextSintProf.getRequestDispatcher("/InformeResultadosCorreccionC2.jsp");
		    dispatcher.forward(request, response);
		}
		catch (Exception s) {
		   out = response.getWriter();
		   out.println("<html>");
		   CommonMMLChecker.printHead(out, cLang);
		   CommonMMLChecker.printBodyHeader(out, cLang);

		   out.println("<h4>Error: Exception"+s.toString()+"</h4");
		   CommonSINT.printEndPageChecker(out,  "24", esProfesor, MsMMLCh.CREATED, cLang);
		   return;
		}


	}



	public static void doGetC2CorrectAllReport2Run(HttpServletRequest request, HttpServletResponse response, String cLang)
						throws IOException, ServletException
	{

		  // notifica a la página JSP que se ha terminado cada corrección

	    response.setContentType("text/event-stream");
	    PrintWriter out = response.getWriter();

	    out.write("retry: -1\n");

	    String numCuentasP = request.getParameter("numCuentasP");
	    if (numCuentasP == null) {
		out.write("data: error no hay numCuentasP\n\n");
		out.close();
		return;
	    }

	    int numCuentas=0;

	    try {
		 numCuentas = Integer.parseInt(numCuentasP);
	    }
	    catch (NumberFormatException e) {
		out.write("data: error numCuentasP no es entero\n\n");
		out.close();
		return;
	    }

	   if (numCuentas < 1) {
		out.write("data: error numCuentasP es menor que uno\n\n");
		out.close();
		return;
	   }


	    String servicioAlu, sintUser, passwdAlu;

	    bucle:
	    for (int x=1; x <= numCuentas; x++) {
		out.flush();
		sintUser="sint"+x;

		try {
		    passwdAlu = CommonMMLChecker.getAluPasswd(sintUser);
		}
		catch (ExcepcionChecker ex) {
			String codigo = ex.getCheckerFailure().getCodigo();
		    if (codigo.equals("NOCONTEXT")) {
		    	out.write("data: "+x+",NOCONTEXT\n\n");   // usersE1NoContext.add(x);
		    }
		    else {
		        out.write("data: "+x+",NOPASSWD\n\n"); //  usersE11NoPasswd.add(x);
		    }
		    continue bucle;
		}


		servicioAlu = "http://"+CommonMMLChecker.server_port+"/"+sintUser+CommonMMLChecker.SERVICE_NAME;

		try {
		    Query2.correctC2OneStudent(request, sintUser, Integer.toString(x), servicioAlu, passwdAlu, cLang);
		    out.write("data: "+x+",OK\n\n"); //   usersOK.add(x);
		}
		catch (ExcepcionChecker e) {
		    CheckerFailure cf = e.getCheckerFailure();

	            switch (cf.getCodigo()) {

			case "01_NOCONTEXT":   // el contexto no está declarado o no existe su directorio
			    out.write("data: "+x+",NOCONTEXT\n\n");  //  usersE1NoContext.add(x);
			    continue bucle;
			case "02_FILENOTFOUND":   // el servlet no está declarado.
			    out.write("data: "+x+",FILENOTFOUND\n\n");   //  usersE2FileNotFound.add(x);
			    continue bucle;
			case "03_ENCODING":   // la secuencia de bytes recibida UTF-8 está malformada
			    out.write("data: "+x+",ENCODING\n\n");    // usersE3Encoding.add(x);
			    continue bucle;
			case "04_IOEXCEPTION":    // la clase del servlet no está o produjo una excepción
			    out.write("data: "+x+",IOEXCEPTION\n\n");     // usersE4IOException.add(x);
			    continue bucle;
			case "05_BF":   // la respuesta no es well-formed
			    out.write("data: "+x+",BF\n\n");       // usersE5Bf.add(x);
			    continue bucle;
			case "06_INVALID":   // la respuesta es inválida
			    out.write("data: "+x+",INVALID\n\n");     // usersE6Invalid.add(x);
			    continue bucle;
			case "07_ERRORUNKNOWN":   // error desconocido
			    out.write("data: "+x+",ERRORUNKNOWN\n\n");    //  usersE7Error.add(x);
			    continue bucle;
			case "08_OKNOPASSWD":   // responde bien incluso sin passwd
			    out.write("data: "+x+",OKNOPASSWD\n\n");     //   usersE8OkNoPasswd.add(x);
			    continue bucle;
			case "09_BADPASSWD":   // la passwd es incorrecta
			    out.write("data: "+x+",BADPASSWD\n\n");    //   usersE9BadPasswd.add(x);
			    continue bucle;
			case "10_BADANSWER":   // la respuesta es inesperada
			    out.write("data: "+x+",BADANSWER\n\n");     //  usersE10BadAnswer.add(x);
			    continue bucle;
			case "12_FILES":
			    out.write("data: "+x+",FILES\n\n");    // usersE12Files.add(x);
			    continue bucle;
			case "20_DIFS":
			    out.write("data: "+x+",DIFS\n\n");   // usersE20Diff.add(x);
			    continue bucle;
			default:      // error desconocido
			    out.write("data: "+x+",??\n\n");   // usersE7Error.add(x);
			    continue bucle;
		    } // switch
		 }  // catch
	      } // for

	      out.write("data: -1,FIN\n\n");
	      out.close();

	}











	// Métodos auxiliares para la correción de un alumno de la consulta 2


	// pide y devuelve la lista de langs de un usuario
	// levanta ExcepcionChecker si algo va mal

	private static ArrayList<String> requestLangs (String call, String cLang)
									throws ExcepcionChecker
	{
		String currentMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
		CheckerFailure cf;
		Document doc;
		ArrayList<String> listaLangs = new ArrayList<String>();

		CommonMMLChecker.errorHandler.clear();

		try {
			doc = CommonMMLChecker.db.parse(call);
		}
		catch (SAXException ex) {
			CommonMMLChecker.logCall(call);

			cf = new CheckerFailure(call, "", ex.toString());
			cf.addMotivo(currentMethod+": SAXException: "+MsMMLCh.getMsg(MsMMLCh.CMML27, cLang)+"langs"); // solicitando/parseando la lista de
			throw new ExcepcionChecker(cf);
		}
		catch (Exception ex) {
			CommonMMLChecker.logCall(call);

			cf = new CheckerFailure(call, "", ex.toString());
			cf.addMotivo(currentMethod+": Exception: "+MsMMLCh.getMsg(MsMMLCh.CMML27, cLang)+"langs"); // solicitando/parseando la lista de
			throw new ExcepcionChecker(cf);
		}


		if (CommonMMLChecker.errorHandler.hasErrors()) {
			CommonMMLChecker.logCall(call);

			ArrayList<String> errors = CommonMMLChecker.errorHandler.getErrors();
			String msg="";

			for (int x=0; x < errors.size(); x++)
				if (x == (errors.size()-1)) msg += "++++"+errors.get(x);
				else msg += "++++"+errors.get(x)+"<br>\n";


			cf = new CheckerFailure(call, "", msg);
			cf.addMotivo(currentMethod+": "+String.format(MsMMLCh.getMsg(MsMMLCh.CMML28, cLang), "errors")+"langs"); // resultado inválido, 'errors' en la lista de
			throw new ExcepcionChecker(cf);
		}

		if (CommonMMLChecker.errorHandler.hasFatalerrors()) {
			CommonMMLChecker.logCall(call);

			ArrayList<String> fatalerrors = CommonMMLChecker.errorHandler.getFatalerrors();
			String msg="";

			for (int x=0; x < fatalerrors.size(); x++)
				if (x == (fatalerrors.size()-1)) msg += "++++"+fatalerrors.get(x);
				else msg += "++++"+fatalerrors.get(x)+"<br>\n";


			cf = new CheckerFailure(call, "", msg);
			cf.addMotivo(currentMethod+": "+String.format(MsMMLCh.getMsg(MsMMLCh.CMML28, cLang), "fatal errors")+"langs"); // resultado inválido, 'fatal errors' en la lista de
			throw new ExcepcionChecker(cf);
		}

		if (doc == null) {
			cf = new CheckerFailure(call, "", currentMethod+": "+MsMMLCh.getMsg(MsMMLCh.CMML30, cLang)+"langs");   // el parser devuelve 'null' al parsear la lista de
			throw new ExcepcionChecker(cf);
		}


		NodeList nlLangs = doc.getElementsByTagName("langs");

		if (nlLangs.getLength() != 1) {
		    	cf = new CheckerFailure(call, "", currentMethod+": "+MsMMLCh.getMsg(MsMMLCh.CMML34, cLang));  // No se recibe '&lt;langs>' al solicitar y parsear la lista de langs
			throw new ExcepcionChecker(cf);
		}

		nlLangs = doc.getElementsByTagName("lang");

		// procesamos todos los lang

		for (int x=0; x < nlLangs.getLength(); x++) {
			Element elemLang = (Element)nlLangs.item(x);
			String strLang = elemLang.getTextContent().trim();

			listaLangs.add(strLang);
		}

		return listaLangs;
	}


	// para comparar el resultado de la F21: listas de langs
	// no devuelve nada si son iguales
	// levanta ExcepcionChecker si hay diferencias

	private static void comparaLangs (String usuario, ArrayList<String> pLangs, ArrayList<String> xLangs, String cLang)
			throws ExcepcionChecker
	{
		CheckerFailure cf;
		String currentMethod = Thread.currentThread().getStackTrace()[1].getMethodName();

		if (pLangs.size() != xLangs.size()) {
			cf = new CheckerFailure("", "20_DIFS", currentMethod+": "+MsCC.getMsg(MsCC.CC56, cLang)+pLangs.size()+" langs, "+MsCC.getMsg(MsCC.CC57, cLang)+xLangs.size()); // CC56...CC57 = debería devolver... pero ddevuelve
			throw new ExcepcionChecker(cf);
		}


		for (int x=0; x < pLangs.size(); x++)
			if (!xLangs.get(x).equals(pLangs.get(x))) {
				cf = new CheckerFailure("", "20_DIFS", currentMethod+": "+String.format(MsMMLCh.getMsg(MsMMLCh.CMML35, cLang), xLangs.get(x), x, pLangs.get(x))); // diferencia lang: valor recibido , posicion, valor esperado
				throw new ExcepcionChecker(cf);
			}

		return;
	}





	// pide y devuelve la lista de casts de un lang
	// levanta ExcepcionChecker si algo va mal

	private static ArrayList<Cast> requestCastsLang (String call, String cLang)
					throws ExcepcionChecker
	{
		String currentMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
		CheckerFailure cf;
		Document doc;
		ArrayList<Cast> listaCasts = new ArrayList<Cast>();

		CommonMMLChecker.errorHandler.clear();

		try {
			doc = CommonMMLChecker.db.parse(call);
		}
		catch (SAXException ex) {
			CommonMMLChecker.logCall(call);

			cf = new CheckerFailure(call, "", ex.toString());
			cf.addMotivo(currentMethod+": SAXException: "+MsMMLCh.getMsg(MsMMLCh.CMML27,cLang)+"casts"); // solicitando/parseando la lista de
			throw new ExcepcionChecker(cf);
		}
		catch (Exception ex) {
			CommonMMLChecker.logCall(call);

			cf = new CheckerFailure(call, "", ex.toString());
			cf.addMotivo(currentMethod+": Exception: "+MsMMLCh.getMsg(MsMMLCh.CMML27,cLang)+"casts"); // solicitando/parseando la lista de
			throw new ExcepcionChecker(cf);
		}

		if (CommonMMLChecker.errorHandler.hasErrors()) {
			CommonMMLChecker.logCall(call);

			ArrayList<String> errors = CommonMMLChecker.errorHandler.getErrors();
			String msg="";

			for (int x=0; x < errors.size(); x++)
				if (x == (errors.size()-1)) msg += "++++"+errors.get(x);
				else msg += "++++"+errors.get(x)+"<br>\n";

			cf = new CheckerFailure(call, "", msg);
			cf.addMotivo(currentMethod+": "+String.format(MsMMLCh.getMsg(MsMMLCh.CMML28, cLang), "errors")+"casts"); // resultado inválido, errors en la lista de
			throw new ExcepcionChecker(cf);
		}

		if (CommonMMLChecker.errorHandler.hasFatalerrors()) {
			CommonMMLChecker.logCall(call);

			ArrayList<String> fatalerrors = CommonMMLChecker.errorHandler.getFatalerrors();
			String msg="";

			for (int x=0; x < fatalerrors.size(); x++)
				if (x == (fatalerrors.size()-1)) msg += "++++"+fatalerrors.get(x);
				else msg += "++++"+fatalerrors.get(x)+"<br>\n";

			cf = new CheckerFailure(call, "", msg);
			cf.addMotivo(currentMethod+": "+String.format(MsMMLCh.getMsg(MsMMLCh.CMML28, cLang), "fatalerrors")+"casts"); // resultado inválido, fatal errors en la lista de
			throw new ExcepcionChecker(cf);
		}


		if (doc == null) {
			cf = new CheckerFailure(call, "", currentMethod+": "+MsMMLCh.getMsg(MsMMLCh.CMML30, cLang)+"casts");   // el parser devuelve 'null' al parsear la lista de
			throw new ExcepcionChecker(cf);
		}


		NodeList nlCasts = doc.getElementsByTagName("thecast");

		if (nlCasts.getLength() != 1) {
		    	cf = new CheckerFailure(call, "", currentMethod+": "+MsMMLCh.getMsg(MsMMLCh.CMML33, cLang));  // No se recibe '&lt;thecast>' al solicitar y parsear la lista de casts
			throw new ExcepcionChecker(cf);
		}

		nlCasts = doc.getElementsByTagName("cast");

		// procesamos todos los casts

		for (int x=0; x < nlCasts.getLength(); x++) {
			Element elemCast = (Element)nlCasts.item(x);
			String name = elemCast.getTextContent().trim();

			String id = elemCast.getAttribute("id");
			String contact = elemCast.getAttribute("contact");

			listaCasts.add(new Cast(name, id, "", contact));
		}

		return listaCasts;
	}

	
	

	// para comparar el resultado de la F22: listas de casts
	// no devuelve nada si son iguales
	// levanta ExcepcionChecker si hay diferencias

	private static void comparaCasts (String usuario, String langActual, ArrayList<Cast> pCasts, ArrayList<Cast> xCasts, String cLang)
					throws ExcepcionChecker
	{
			CheckerFailure cf;
			String currentMethod = Thread.currentThread().getStackTrace()[1].getMethodName();

		    if (pCasts.size() != xCasts.size()) {
				  cf = new CheckerFailure("", "20_DIFS", currentMethod+": CASTS ("+usuario+"+"+langActual+"): "+MsCC.getMsg(MsCC.CC56, cLang)+pCasts.size()+" casts, "+MsCC.getMsg(MsCC.CC57, cLang)+xCasts.size()); // CC56...CC57 = debería devolver...pero devuelve
				  throw new ExcepcionChecker(cf);
		    }

			String pName, xName, pId, xId, pContact, xContact;
				
		    for (int z=0; z < pCasts.size(); z++) {
					pName = pCasts.get(z).getName();
					xName = xCasts.get(z).getName();

		    	    if (!xName.equals(pName)) {
		    			    cf = new CheckerFailure("", "20_DIFS", currentMethod+": CASTS ('"+usuario+"', '"+langActual+"'): "+String.format(MsMMLCh.getMsg(MsMMLCh.CMML40, cLang), pName, z, xName)); // "Se esperaba el cast '%s' en la posición %d y se recibió '%s'"
						    throw new ExcepcionChecker(cf);
		    	    }

					pId = pCasts.get(z).getID();
					xId = xCasts.get(z).getID();

		    	    if (!xId.equals(pId)) {
						    cf = new CheckerFailure("", "20_DIFS", currentMethod+": CASTS ('"+usuario+"', '"+langActual+"'): "+String.format(MsMMLCh.getMsg(MsMMLCh.CMML41, cLang), pId, z, xId)); // "Se esperaba el ID '%s' en la posición %d y se recibió '%s'"
		    			    throw new ExcepcionChecker(cf);
		    	    }

					pContact = pCasts.get(z).getContact();
					xContact = xCasts.get(z).getContact();

		    	    if (!xContact.equals(pContact)) {
		    			     cf = new CheckerFailure("", "20_DIFS", currentMethod+": CASTS ('"+usuario+"', '"+langActual+"'): "+String.format(MsMMLCh.getMsg(MsMMLCh.CMML46, cLang), pContact, z, xContact)); // "Se esperaba el contact '%s' en la posición %d y se recibió '%s'"
	                 throw new ExcepcionChecker(cf);
							}
		    }

		    return;
	}

		
		
		
		





	// pide y devuelve la lista de movies de un cast de un lang
	// levanta ExcepcionChecker si algo va mal

	private static ArrayList<Movie> requestMoviesCastLang (String call, String cLang)
									throws ExcepcionChecker
	{
		String currentMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
		CheckerFailure cf;
		Document doc;
		ArrayList<Movie> listaMovies = new ArrayList<Movie>();

		CommonMMLChecker.errorHandler.clear();

		try {
			doc = CommonMMLChecker.db.parse(call);
		}
		catch (SAXException e) {
			CommonMMLChecker.logCall(call);

			cf = new CheckerFailure(call, "", e.toString());
			cf.addMotivo(currentMethod+": SAXException: "+MsMMLCh.getMsg(MsMMLCh.CMML27, cLang)+"movies"); // solicitando/parseando la lista de
			throw new ExcepcionChecker(cf);
		}
		catch (Exception e) {
			CommonMMLChecker.logCall(call);

			cf = new CheckerFailure(call, "", e.toString());
			cf.addMotivo(currentMethod+": Exception: "+MsMMLCh.getMsg(MsMMLCh.CMML27, cLang)+"movies"); // solicitando/parseando la lista de
			throw new ExcepcionChecker(cf);
		}

		if (CommonMMLChecker.errorHandler.hasErrors()) {
			CommonMMLChecker.logCall(call);

			ArrayList<String> errors = CommonMMLChecker.errorHandler.getErrors();
			String msg="";

			for (int x=0; x < errors.size(); x++)
				if (x == (errors.size()-1)) msg += "++++"+errors.get(x);
				else msg += "++++"+errors.get(x)+"<br>\n";

			cf = new CheckerFailure(call, "", msg);
			cf.addMotivo(currentMethod+": "+String.format(MsMMLCh.getMsg(MsMMLCh.CMML28, cLang), "errors")+"movies"); // resultado inválido, <errors> en la lista de
			throw new ExcepcionChecker(cf);
		}

		if (CommonMMLChecker.errorHandler.hasFatalerrors()) {
			CommonMMLChecker.logCall(call);

			ArrayList<String> fatalerrors = CommonMMLChecker.errorHandler.getFatalerrors();
			String msg="";

			for (int x=0; x < fatalerrors.size(); x++)
				if (x == (fatalerrors.size()-1)) msg += "++++"+fatalerrors.get(x);
				else msg += "++++"+fatalerrors.get(x)+"<br>\n";

			cf = new CheckerFailure(call, "", msg);
			cf.addMotivo(currentMethod+": "+String.format(MsMMLCh.getMsg(MsMMLCh.CMML28, cLang), "fatalerrors")+"movies"); // resultado inválido, fatalerrors en la lista de
			throw new ExcepcionChecker(cf);
		}


		if (doc == null) {
			cf = new CheckerFailure(call, "", currentMethod+": "+MsMMLCh.getMsg(MsMMLCh.CMML30, cLang)+"movies");   // el parser devuelve 'null' al parsear la lista de
			throw new ExcepcionChecker(cf);
		}

		NodeList nlMovies = doc.getElementsByTagName("movies");

		if (nlMovies.getLength() != 1) {
		    	cf = new CheckerFailure(call, "", currentMethod+": "+MsMMLCh.getMsg(MsMMLCh.CMML32, cLang));  // No se recibe '&lt;movies>' al solicitar y parsear la lista de movies
			throw new ExcepcionChecker(cf);
		}

		nlMovies = doc.getElementsByTagName("movie");

		// procesamos todas las movies

		for (int x=0; x < nlMovies.getLength(); x++) {
			Element elemMovie = (Element)nlMovies.item(x);

			String title = elemMovie.getTextContent().trim();

			String year = elemMovie.getAttribute("year");
			String sinopsis = elemMovie.getAttribute("synopsis");
			
			String genres = elemMovie.getAttribute("genres");
			ArrayList<String> listaGenres = new ArrayList<String>(Arrays.asList(genres.split(",")));

			listaMovies.add(new Movie(title, year, listaGenres, sinopsis, ""));
		}

		return listaMovies;
	}

	
	
	

	// para comparar el resultado de la F23: listas de movies
	// no devuelve nada si son iguales
	// levanta ExcepcionChecker si hay diferencias

	private static void comparaMovies (String usuario, String langActual, String idCastActual, ArrayList<Movie> pMovies, ArrayList<Movie> xMovies, String cLang)
				throws ExcepcionChecker
	{
		CheckerFailure cf;
		String currentMethod = Thread.currentThread().getStackTrace()[1].getMethodName();

	    if (pMovies.size() != xMovies.size()) {
			  cf = new CheckerFailure("", "20_DIFS", currentMethod+": CASTS ("+usuario+"+"+langActual+"+"+idCastActual+"): "+MsCC.getMsg(MsCC.CC56, cLang)+pMovies.size()+" movies, "+MsCC.getMsg(MsCC.CC57, cLang)+xMovies.size()); // CC56...CC57 = debería devolver...pero devuelve
			  throw new ExcepcionChecker(cf);
	    }


		String pTitle, xTitle, pSynopsis, xSynopsis, pGenres, xGenres;

	    for (int y=0; y < pMovies.size(); y++) {
			pTitle = pMovies.get(y).getTitle();
			xTitle = xMovies.get(y).getTitle();

			if (!xTitle.equals(pTitle)) {
				cf = new CheckerFailure("", "20_DIFS", currentMethod+": MOVIES ('"+usuario+"', '"+langActual+"', '"+idCastActual+"'): "+String.format(MsMMLCh.getMsg(MsMMLCh.CMML37, cLang), pTitle, y, xTitle)); // "Se esperaba la movie '%s' en la posición %d y se recibió '%s'"
				throw new ExcepcionChecker(cf);
			}
			
			pSynopsis = pMovies.get(y).getSinopsis();
			xSynopsis = xMovies.get(y).getSinopsis();

			if (!xSynopsis.equals(pSynopsis)) {
				cf = new CheckerFailure("", "20_DIFS", currentMethod+": MOVIES ('"+usuario+"', '"+langActual+"', '"+idCastActual+"'): "+String.format(MsMMLCh.getMsg(MsMMLCh.CMML38, cLang), pSynopsis, y, xSynopsis)); // "Se esperaba la sinopsis '%s' en la posición %d y se recibió '%s'"
				throw new ExcepcionChecker(cf);
			}

			pGenres = pMovies.get(y).getGenres();
			xGenres = xMovies.get(y).getGenres();
			
			String[] pGenresList = pGenres.split(",");
			String[] xGenresList = xGenres.split(",");
			
			Arrays.sort(pGenresList);
			Arrays.sort(xGenresList);

			if (!Arrays.equals(pGenresList, xGenresList)) {
				cf = new CheckerFailure("", "20_DIFS", currentMethod+": MOVIES ('"+usuario+"', '"+langActual+"', '"+idCastActual+"'): "+String.format(MsMMLCh.getMsg(MsMMLCh.CMML45, cLang), pGenres, y, xGenres)); // "Se esperaban los géneros '%s' en la posición %d y se recibió '%s'"
				throw new ExcepcionChecker(cf);
			}
					
	    }

	    return;
	}
	


}
