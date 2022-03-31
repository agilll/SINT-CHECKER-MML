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


// Implementación de la comprobación de la consulta 1 (filmografía de un actor S3 en una película S2 de un año S1)

package docencia.sint.MML.checker;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.Date;

import docencia.sint.MML.Pelicula;
import docencia.sint.MML.Actor;
import docencia.sint.MML.CommonMML;
import docencia.sint.MML.Filmografia;
import docencia.sint.MML.Film;
import docencia.sint.Common.CommonSINT;
import docencia.sint.Common.ExcepcionSINT;
import docencia.sint.Common.SintRandom;



public class Query1 {
	
	// nombres de los parámetros de esta consulta
		
	static final String PFASE = "pfase";	
	static final String PANIO = "panio";
	static final String PPELI = "ppelicula";
	static final String PACTOR = "pact";
	
	// COMPROBACIÓN DE LAS LLAMADAS A SINTPROF
	
	public static void doGetC1CheckSintprofCalls(HttpServletRequest request, PrintWriter out)
						throws IOException, ServletException
	{
		int esProfesor = 1;  // sólo el profesor debería llegar aquí
		
		out.println("<html>");
		CommonMMLChecker.printHead(out);
		CommonMMLChecker.printBodyHeader(out);
		
		out.println("<h2>Consulta 1</h2>");
		out.println("<h3>Comprobar las llamadas al servicio de sintprof</h3>");
		
		String oneCheckStatus;  // para leer el Status de profesor 

		// doOneCheckUpStatus: hace una petición de estado al servicio del profesor para ver si está operativo

		oneCheckStatus = CommonMMLChecker.doOneCheckUpStatus(request, "sintprof", CommonSINT.PASSWD);

		String statusCode = oneCheckStatus.substring(0,oneCheckStatus.indexOf(','));
		String statusPhrase = oneCheckStatus.substring (oneCheckStatus.indexOf(',')+1);
		
		if (!statusCode.equals("OK")) {
			out.println("<h4 style='color: red'>sintprof (error status): "+statusPhrase+"</h4>");
			CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonMML.CREATED);
			return;
		}
		else {
			out.println("<h4>CheckStatus OK</h4>");
		}

		
		
		// empezamos por pedir los errores
		
		try {
			CommonMMLChecker.requestErrores("sintprof", CommonMMLChecker.servicioProf, CommonSINT.PASSWD);	
			out.println("<h4>Errores OK</h4>");
		}
		catch (ExcepcionSINT ex) { 
			out.println("<h4 style='color: red'>sintprof (ExcepcionSINT pidiendo errores): "+ex.toString()+"</h4>");
			CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonMML.CREATED);
			return;
        }
		catch (Exception ex) { 
			out.println("<h4 style='color: red'>sintprof (Exception pidiendo errores): "+ex.toString()+"</h4>");
			CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonMML.CREATED);
			return;
        }
		
		
		// y ahora todas y cada una de las consultas
		
		// pedimos la lista de años de sintprof
	 
		ArrayList<String> pAnios;
		try {
			pAnios = Query1.requestAnios("sintprof", CommonMMLChecker.servicioProf, CommonSINT.PASSWD);
			out.println("<h4>Años OK: "+pAnios.size()+"</h4>");
		}
		catch (ExcepcionSINT ex) { 
			out.println("<h4 style='color: red'>sintprof (ExcepcionSINT pidiendo años): "+ex.toString()+"</h4>");
			CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonMML.CREATED);
			return;
        }
	
	
	    
		// vamos con la segunda fase, las películas de cada año
		// el bucle X recorre todos los años
		
		
		String anioActual;
	     
		for (int x=0; x < pAnios.size(); x++) {
			String indent ="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
	    	
			anioActual = pAnios.get(x);
			
			// pedimos las películas de ese año de sintprof
	   	 
			ArrayList<Pelicula> pPeliculas;
			try {
				pPeliculas = requestPeliculasAnio("sintprof", CommonMMLChecker.servicioProf, anioActual, CommonSINT.PASSWD);
				out.println("<h4>"+indent+anioActual+": "+pPeliculas.size()+"  OK</h4>");
			}
			catch (Exception e) { 
				out.println("<h4 style='color: red'>sintprof (Películas): "+e.toString()+"</h4>");
				CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonMML.CREATED);
				return;
	        }
						        

	        
	        
	        // vamos con la tercera fase, los actores de una película
	        // el bucle Y recorre todas las películas
	        
	        Pelicula pelActual;
	        
	        for (int y=0; y < pPeliculas.size(); y++) {
	        	
		        	indent = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		        	
		        	pelActual = pPeliculas.get(y);
		        	
		    		// pedimos los actores de esa película de ese año de sintprof
		       	 
		    		ArrayList<Actor> pActores;
		    		try {
		    			pActores = requestActoresPelicula("sintprof", CommonMMLChecker.servicioProf, anioActual, pelActual.getTitulo(), CommonSINT.PASSWD);
					out.println("<h4>"+indent+anioActual+"+"+pelActual.getTitulo()+": "+pActores.size()+"  OK</h4>");
		    		}
		    		catch (ExcepcionSINT e) { 
		    			out.println("<h4 style='color: red'>sintprof (ExcepcionSINT pidiendo actores): "+e.toString()+"</h4>");
		    			CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonMML.CREATED);
		    			return;
		            }
		    	            	          
		            
		            
	            // vamos con la cuarta fase, la filmografía de un actor en una película de un año
	            // el bucle Z recorre todos los actores
		            
	            	Actor actActual;
	            	
		        for (int z=0; z < pActores.size(); z++) {
			        	indent = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			        	
			        	actActual = pActores.get(z);
		            			
		        		// pedimos la filmografía de ese actor de sintprof
		           	 
			    		Filmografia pFilm;
			    		
		        		try {
		        			pFilm = requestFilmografiaActor("sintprof", CommonMMLChecker.servicioProf, anioActual, pelActual.getTitulo(), actActual.getNombre(), CommonSINT.PASSWD);
						out.println("<h4>"+indent+anioActual+"+"+pelActual.getTitulo()+"+"+actActual.getNombre()+": "+pFilm.getFilms().size()+"  OK</h4>");
		        		}
		        		catch (ExcepcionSINT ex) { 
		        			out.println("<h4 style='color: red'>sintprof (ExcepcionSINT pidiendo filmografía): "+ex.toString()+"</h4>");
		        			CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonMML.CREATED);
		        			return;
		                }	        		        		
		            
		        } // for z
	            
	        } // for y
	         
	    } // for x
	    
		
		out.println("<h4>sintprof: Todo OK</h4>");
			
		CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonMML.CREATED);
	}
	
	

	
	
	
	
	
	// COMPROBACIÓN DEL SERVICIO DE UN ÚNICO ESTUDIANTE

	// pantalla para ordenar comprobar un estudiante (se pide su número de login)
	// debería unificarse en uno sólo con el de la consulta 2, son casi iguales

	public static void doGetC1CorrectOneForm (HttpServletRequest request, PrintWriter out, int esProfesor)
						throws IOException
	{
		out.println("<html>");
		CommonMMLChecker.printHead(out);
		
		out.println("<script>");

		out.println("function stopEnter (event) {");
		out.println("var x = event.which;");	
		out.println("if (x === 13) {event.preventDefault();}");
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
	
		CommonMMLChecker.printBodyHeader(out);
		
		out.println("<h2>Consulta 1</h2>");                               // consulta 1
		out.println("<h3>Corrección de un único servicio</h3>");
		
		out.println("<form>");
		out.println("<input type='hidden' name='screenP' value='121'>");  // conduce a doGetC1CorrectOneReport

		out.println("Introduzca el número de la cuenta SINT a comprobar: ");
		out.println("<input id='inputSint' type='text' name='alumnoP' size='3' onfocus='hideservice();' onblur='showservice();' onkeypress='stopEnter(event);' pattern='[1-9]([0-9]{1,2})?' required> <br>");

		out.println("URL del servicio del alumno:");
		out.println("<input style='visibility: hidden' id='serviceAluInput' type='text' name='servicioAluP' value='' size='40'><br>");
		
		if (esProfesor == 0) {
			out.println("<p>Passwd de la cuenta (10 letras o números) <input id='passwdAlu' type='text' name='passwdAlu'  pattern='[A-Za-z0-9]{10}?' required> <br><br>");
		}
		else {
			out.println("<p><input type='hidden' name='p' value='si'>");	
		}

		out.println("<p><input class='enviar' id='sendButton' disabled='true' type='submit' value='Enviar'>");
		out.println("</form>");

		out.println("<form>");
		if (esProfesor == 1) 
			out.println("<p><input type='hidden' name='p' value='si'>");	
		out.println("<p><input class='home'  type='submit' value='Inicio'>");
		out.println("</form>");
		
		CommonSINT.printFoot(out, CommonMML.CREATED);

		out.println("</body></html>");
	}



	
	
	
	// pantalla para informar de la corrección de un sintX (se recibe en 'alumnoP' su número de login X)
	// también recibe en servicioAlu el URL del servicio del alumno

	public static void doGetC1CorrectOneReport(HttpServletRequest request, PrintWriter out, int esProfesor)
						throws IOException, ServletException
	{	
		out.println("<html>");
		CommonMMLChecker.printHead(out);
		CommonMMLChecker.printBodyHeader(out);
		
		out.println("<h2>Consulta 1</h2>");
		out.println("<h3>Corrección de un único servicio</h3>");
		
		// leemos los datos del estudiante
		
		String alumnoP = request.getParameter("alumnoP");
		String servicioAluP = request.getParameter("servicioAluP");
		
		if ((alumnoP == null) || (servicioAluP == null)) {
			out.println("<h4 style='color: red'>Falta uno de los parámetros</h4>");  // si falta algún parámetro no se hace nada
			CommonSINT.printEndPageChecker(out,  "12", esProfesor, CommonMML.CREATED);
			return; 
		}
		
		
		String usuario="sint"+alumnoP;
		String passwdAlu, passwdRcvd;


		try {
			passwdAlu = CommonMMLChecker.getAluPasswd(usuario);
		}
		catch (ExcepcionSINT ex) {
			if (ex.getMessage().equals("NOCONTEXT"))
				out.println("<h4 style='color: red'>Todavía no se ha creado el contexto de "+usuario+"</h4>"); 
			else 
				out.println("<h4 style='color: red'>"+ex.getMessage()+": Imposible recuperar la passwd del contexto de "+usuario+"</h4>"); 
			CommonSINT.printEndPageChecker(out,  "12", esProfesor, CommonMML.CREATED);
			return; 
		}
		
		if (esProfesor == 0) {  // lo está probando un alumno, es obligatorio que haya introducido su passwd
			passwdRcvd = request.getParameter("passwdAlu");   // leemos la passwd del alumno del parámetro
			
			if (passwdRcvd == null) {
				out.println("<h4 style='color: red'>No se ha recibido la passwd de "+usuario+"</h4>"); 
				CommonSINT.printEndPageChecker(out,  "12", esProfesor, CommonMML.CREATED);
				return; 
			}
			
			if (!passwdAlu.equals(passwdRcvd)) {
				out.println("<h4 style='color: red'>La passwd proporcionada no coincide con la almacenada en el sistema para "+usuario+"</h4>"); 
				CommonSINT.printEndPageChecker(out,  "12", esProfesor, CommonMML.CREATED);
				return; 
			}
			
		}
		
		out.println("<h3>Comprobando el servicio del usuario "+usuario+" ("+servicioAluP+")</h3>");
		
		String oneCheckStatus;  // para leer el Status de profesor y alumno

		// doOneCheckUpStatus: hace una petición de estado al servicio del profesor y alumno para ver si están operativos

		oneCheckStatus = CommonMMLChecker.doOneCheckUpStatus(request, "sintprof", CommonSINT.PASSWD);
		String statusCode = oneCheckStatus.substring(0,oneCheckStatus.indexOf(','));
		String statusPhrase = oneCheckStatus.substring (oneCheckStatus.indexOf(',')+1);


		if (!statusCode.equals("OK")) {
			out.println("<h4 style='color: red'>sintprof (error al preguntar por el estado): "+statusPhrase+"</h4>");
			CommonSINT.printEndPageChecker(out,  "12", esProfesor, CommonMML.CREATED);
			return;
		}
		
		oneCheckStatus = CommonMMLChecker.doOneCheckUpStatus(request, usuario, passwdAlu);  
		statusCode = oneCheckStatus.substring(0,oneCheckStatus.indexOf(','));
		statusPhrase = oneCheckStatus.substring (oneCheckStatus.indexOf(',')+1);

		if (!statusCode.equals("OK")) {
			out.println("<h4 style='color: red'>"+usuario+" (error al preguntar por el estado): "+statusPhrase+"</h4>");
			CommonSINT.printEndPageChecker(out,  "12", esProfesor, CommonMML.CREATED);
			return;
		}
		
		out.println("<h3>El servicio está en estado operativo </h3>");

		// todas las comprobaciones preliminares están bien, vamos a pedir las consultas

		CommonMMLChecker.logMMLChecker("Comprobaciones preliminares OK");
		
		
		// el resultado será 'OK' o una descripción de la diferencia encontrada (termina al encontrar una diferencia)
		
		String resultado = Query1.correctC1OneStudent(usuario, servicioAluP, passwdAlu);
		
		out.println("<h3>Resultado: "+resultado+"</h3>");
	
		// terminamos con botones Atrás e Inicio

		CommonSINT.printEndPageChecker(out,  "12", esProfesor, CommonMML.CREATED);
	}
	
	
	
	
	// método que corrige la consulta 1 de un estudiante

	private static String correctC1OneStudent (String usuario, String servicioAluP, String passwdAlu) 
	{
		String resultComp;
		
		// para la consulta directa final, vamos a escoger anio, película y actor al azar y a guardarlas en esas variables
		
		SintRandom.init(); // inicializamos el generador de números aleatorios
		int posrandom;
		
		String dqAnio="";
		String dqPelicula="";
		String dqActor="";
		
		
		// empezamos por comprobar los errores
		
		String resultErrores = CommonMMLChecker.comparaErrores(usuario, servicioAluP, passwdAlu);
		
		if (!resultErrores.equals("OK")) 
			return resultErrores;

		
		
		
		// y ahora todas y cada una de las consultas
		
		// pedimos la lista de años de sintprof
	 
		ArrayList<String> pAnios;
		try {
			pAnios = Query1.requestAnios("sintprof", CommonMMLChecker.servicioProf, CommonSINT.PASSWD);
		}
		catch (Exception ex) { return "<br>Excepción solicitando la lista de años a sintprof:<br> "+ex.toString(); }
	
	
		// pedimos la lista de anios del sintX
		
		ArrayList<String> xAnios;
		try {
			xAnios = Query1.requestAnios(usuario, servicioAluP, passwdAlu);
		}
		catch (Exception ex) { return "<br>Excepción solicitando la lista de años a "+usuario+":<br> "+ ex.toString(); }
	
		
		// comparamos las listas de sintprof y sintX
		
		resultComp = Query1.comparaAnios(usuario, pAnios, xAnios);
		
		if (!resultComp.equals("OK")) 
			return resultComp;
		
		CommonMMLChecker.logMMLChecker("Listas de años iguales");
		
	    
		// las listas de años son iguales
		// elegimos un año al azar para la consulta directa final
	    
		posrandom = SintRandom.getRandomNumber(0, pAnios.size()-1);
		dqAnio = pAnios.get(posrandom);
	    
		
		
	    
		// vamos con la segunda fase, las películas de cada año
		// el bucle X recorre todos los años
		
		
		String anioActual;
	     
		for (int x=0; x < pAnios.size(); x++) {
	    	
			anioActual = pAnios.get(x);
			
			// pedimos las películas de ese año de sintprof
	   	 
			ArrayList<Pelicula> pPeliculas;
			try {
				pPeliculas = requestPeliculasAnio("sintprof", CommonMMLChecker.servicioProf, anioActual, CommonSINT.PASSWD);
			}
			catch (Exception ex) { return "<br>Excepción solicitando la lista de películas a sintprof:<br> "+ex.toString(); }
		
			
			// pedimos las películas de ese año del sintX
			
			ArrayList<Pelicula> xPeliculas;
			try {
				xPeliculas = requestPeliculasAnio(usuario, servicioAluP, anioActual, passwdAlu);
			}
			catch (Exception ex) { return "<br>Excepción solicitando la lista de películas a "+usuario+":<br> "+ex.toString(); }			
			
			
			// comparamos las listas de sintprof y sintX
			
			resultComp = Query1.comparaPeliculas(usuario, anioActual, pPeliculas, xPeliculas);
			
			if (!resultComp.equals("OK")) 
				return resultComp;
	       	       
			CommonMMLChecker.logMMLChecker("Año: "+anioActual+" ---> Listas de películas iguales");
	        
	        // las listas de películas para este año son iguales
		    // si este año es el de la consulta directa final, elegimos una película al azar para la consulta directa final
	        
	        
	        if (anioActual.equals(dqAnio)) {
	            posrandom = SintRandom.getRandomNumber(0, pPeliculas.size()-1);
	            Pelicula pPel = pPeliculas.get(posrandom);
	          	dqPelicula = pPel.getTitulo();
	        }
	        
	        
	        
	        // vamos con la tercera fase, los actores de una película
	        // el bucle Y recorre todas las películas
	        
	        Pelicula pelActual;
	        
	        for (int y=0; y < pPeliculas.size(); y++) {
	        	
		        	pelActual = pPeliculas.get(y);
		        	
		    		// pedimos los actores de esa película de ese año de sintprof
		       	 
		    		ArrayList<Actor> pActores;
		    		try {
		    			pActores = requestActoresPelicula("sintprof", CommonMMLChecker.servicioProf, anioActual, pelActual.getTitulo(), CommonSINT.PASSWD);
		    		}
		    		catch (ExcepcionSINT ex) { return "<br>ExcepcionSINT solicitando la lista de actores a sintprof:<br> "+ex.toString(); }
		    		
		    		
		    		// pedimos los actores de esa película de ese año del sintX
		    		
		    		ArrayList<Actor> xActores;
		    		try {
		    			xActores = requestActoresPelicula(usuario, servicioAluP, anioActual, pelActual.getTitulo(), passwdAlu);
		    		}
		    		catch (ExcepcionSINT ex) { return "<br>ExcepcionSINT solicitando la lista de actores a "+usuario+":<br> "+ex.toString(); }
		    		
		    		
		    		// comparamos las listas de sintprof y sintX
		    		
				resultComp = Query1.comparaActores(usuario, anioActual, pelActual.getTitulo(), pActores, xActores);
				
				if (!resultComp.equals("OK")) 
					return resultComp;
	            
				CommonMMLChecker.logMMLChecker("Listas de actores iguales");
				
	            
	            // las listas de actores de esta película son iguales
	            // si esta película es la de la consulta directa final, elegimos un actor al azar para la consulta directa final
	            
	            if ( (anioActual.equals(dqAnio)) && (pelActual.getTitulo().equals(dqPelicula))) {
		            posrandom = SintRandom.getRandomNumber(0, pActores.size()-1);
		        	    dqActor = pActores.get(posrandom).getNombre();
		        }
	            
	            
	            // vamos con la cuarta fase, la filmografía de un actor de una película de un año
	            // el bucle Z recorre todos los actores
		            
	            	Actor actorActual;
	            	
		        for (int z=0; z < pActores.size(); z++) {
	            	
		            	actorActual = pActores.get(z);
		            			
		        		// pedimos la filmografía de ese actor de sintprof
		           	 
		        		Filmografia pFilmografia;
		        		
		        		try {
		        			pFilmografia = requestFilmografiaActor("sintprof", CommonMMLChecker.servicioProf, anioActual, pelActual.getTitulo(), actorActual.getNombre(), CommonSINT.PASSWD);
		        		}
		        		catch (ExcepcionSINT ex) { return "<br>ExcepcionSINT solicitando la filmografía a sintprof:<br> "+ex.toString(); }
		        		
		        		
		        		// pedimos la filmografía de ese actor del sintX
		        		
		        		Filmografia xFilmografia;
		        		try {
		        			xFilmografia = requestFilmografiaActor(usuario, servicioAluP, anioActual, pelActual.getTitulo(), actorActual.getNombre(), passwdAlu);
		        		}
		        		catch (ExcepcionSINT ex) { return "<br>ExcepcionSINT solicitando la filmografía a "+usuario+":<br> "+ex.toString(); }
		        			
		        		
		        		// comparamos las filmografías de sintprof y sintX
		        		
					resultComp = Query1.comparaFilmografias(usuario, anioActual, pelActual.getTitulo(), actorActual.getNombre(), pFilmografia, xFilmografia);
					
					if (!resultComp.equals("OK")) 
						return resultComp;	    
					
					CommonMMLChecker.logMMLChecker("Filmografías iguales");
	            
	            } // for z
	            
	        } // for y
	         
	    } // for x
	    
	    
		// finalmente la consulta directa
		
		String resultadoDQ = checkDirectQueryC1(CommonMMLChecker.servicioProf, usuario, servicioAluP, dqAnio, dqPelicula, dqActor, passwdAlu);
		
		if (!resultadoDQ.equals("OK")) 
			return "Resultados erróneos en la consulta directa: "+resultadoDQ; 
		
		// si todas las consultas coincidieron, devuelve 'OK'
	    return "OK";
	}
	
	
	

	
	

	
	
	// comprueba que las consultas directas son iguales   
	
	private static String checkDirectQueryC1(String servicioProf, String usuario, String servicioAluP, String anio, String pelicula, String actor, String passwdAlu) 
	{
		Filmografia pFilmografia, xFilmografia;
			

		// primero comprobamos que responde con el error apropiado si falta algún parámetro
		
  		try {
  			Query1.checkLackParam(usuario, servicioAluP, anio, pelicula, actor, passwdAlu);
		}
		catch (ExcepcionSINT ex) { return "<br>ExcepcionSINT solicitando la consulta directa a: "+usuario+"<br> "+ex.toString(); }
  		
  		
 		// ahora comprobamos que los resultados son correctos
  		
   		try {
   			pFilmografia = Query1.requestFilmografiaActor("sintprof", CommonMMLChecker.servicioProf, anio, pelicula, actor, CommonSINT.PASSWD);
		}
		catch (ExcepcionSINT ex) { return "<br>ExcepcionSINT solicitando la consulta directa a sintprof:<br> "+ex.toString(); }
   		
  		try {
   			xFilmografia = Query1.requestFilmografiaActor(usuario, servicioAluP, anio, pelicula, actor, passwdAlu);
		}
		catch (ExcepcionSINT ex) { return "<br>ExcepcionSINT solicitando la consulta directa:<br> "+ex.toString(); }
   		
   		
		// comparamos las filmografías de sintprof y sintX
		
		String resultComp = Query1.comparaFilmografias(usuario, anio, pelicula, actor, pFilmografia, xFilmografia);
		
		if (!resultComp.equals("OK")) 
			return "checkDirectQueryC1: "+resultComp;	    
  	
	
		// si todo coincidió, devuelve 'OK'
		
		return "OK";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	// COMPROBACIÓN DEL SERVICIO DE TODOS LOS ESTUDIANTES

	// pantalla para comprobar todos los estudiantes, se pide el número de cuentas a comprobar (corregir su práctica)

	public static void doGetC1CorrectAllForm (HttpServletRequest request, PrintWriter out)
	{
		int esProfesor = 1;
		
		out.println("<html>");
		CommonMMLChecker.printHead(out);
		CommonMMLChecker.printBodyHeader(out);
		
		out.println("<h2>Consulta 1</h2>");
		out.println("<h3>Corrección de todos los servicios</h3>");

		out.println("<form>");
		out.println("<input type='hidden' name='screenP' value='131'>");

		out.println("Introduzca el número de cuentas SINT a corregir: ");
		out.println("<input id='inputNumCuentas' type='text' name='numCuentasP' size='3' pattern='[1-9]([0-9]{1,2})?' required>");

		if (esProfesor == 1) 
			out.println("<p><input type='hidden' name='p' value='si'>");	
		
		out.println("<input class='enviar' type='submit' value='Enviar' >");   
		out.println("</form>");

		out.println("<form>");
		if (esProfesor == 1) 
			out.println("<p><input type='hidden' name='p' value='si'>");	
		out.println("<p><input class='home' type='submit' value='Inicio'>");
		out.println("</form>");

		CommonSINT.printFoot(out, CommonMML.CREATED);
		
		out.println("</body></html>");
	}



	
	// pantalla para corregir a todos los estudiantes
	// presenta en pantalla diversas listas según el resultado de cada alumno
	// se crea un fichero con el resultado de cada corrección (webapps/CORRECCIONES/sintX/fecha-corrección)  
	// se devuelven enlaces a esos ficheros
		
	public static void doGetC1CorrectAllReport(HttpServletRequest request, PrintWriter out)
						throws IOException, ServletException
	{
		int esProfesor = 1;
		
		out.println("<html>");
		CommonMMLChecker.printHead(out);
		CommonMMLChecker.printBodyHeader(out);
		
		out.println("<h2>Consulta 1</h2>");

		// si no se recibe el parámetro con el número de cuentas no se hace nada

		String numCuentasP = request.getParameter("numCuentasP");
		if (numCuentasP == null) {
			out.println("<h4>Error: no se ha recibido el número de cuentas</h4>");
			CommonSINT.printEndPageChecker(out,  "13", esProfesor, CommonMML.CREATED);
			return;
		}

		int numCuentas=0;

		try {
			numCuentas = Integer.parseInt(numCuentasP);
		}
		catch (NumberFormatException e) {
			out.println("<h4>Error: el número de cuentas recibido no es un número válido</h4>");
			CommonSINT.printEndPageChecker(out,  "13", esProfesor, CommonMML.CREATED);
			return;
		}

		if (numCuentas < 1) {  
			out.println("<h4>Error: el número de cuentas recibido es menor que uno</h4>");
			CommonSINT.printEndPageChecker(out,  "13", esProfesor, CommonMML.CREATED);
			return;
		}

		
		
		
		// todos los parámetros están bien

		String oneCheckStatus;  // para leer el Status de profesor y alumnos


		// doOneCheckUpStatus: hace una petición de estado al servicio del profesor para ver si está operativo

		oneCheckStatus = CommonMMLChecker.doOneCheckUpStatus(request, "sintprof", CommonSINT.PASSWD);
		String statusCode = oneCheckStatus.substring(0,oneCheckStatus.indexOf(','));
		String statusPhrase = oneCheckStatus.substring (oneCheckStatus.indexOf(',')+1);

		if (!statusCode.equals("OK")) {
			out.println("<h4 style='color: red'>sintprof (error status): "+statusPhrase+"</h4>");
			CommonSINT.printEndPageChecker(out,  "13", esProfesor, CommonMML.CREATED);
			return;
		}


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
		
		ArrayList<Integer> usersDiff = new ArrayList<Integer>();	   // las peticiones del alumno tienen diferencias respecto a las del profesor

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
			catch (ExcepcionSINT ex) {
				if (ex.getMessage().equals("NOCONTEXT")) {
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
			
		    

			// primero comprobamos si el servicio está operativo

			bw.write("Comenzando la comprobación de "+sintUser+"... Comprobando si el servicio está operativo");
			bw.newLine();

			// doOneCheckUpStatus: hace una petición de estado al servicio de cada alumno

			oneCheckStatus = CommonMMLChecker.doOneCheckUpStatus(request, sintUser, passwdAlu);
			statusCode = oneCheckStatus.substring(0,oneCheckStatus.indexOf(','));
			statusPhrase = oneCheckStatus.substring (oneCheckStatus.indexOf(',')+1);

			switch (statusCode) {
				case "OK":    // el servicio está operativo                                      
					bw.write("El servicio está operativo");
					bw.newLine();
					break;
				case "01_NOCONTEXT":   // el contexto no está declarado o no existe su directorio
					bw.write(statusPhrase);
					bw.close();
					usersE1NoContext.add(x);
					continue bucle;
				case "02_FILENOTFOUND":   // el servlet no está declarado.
					bw.write(statusPhrase);
					bw.close();
					usersE2FileNotFound.add(x);
					continue bucle;
				case "03_ENCODING":   // la secuencia de bytes recibida UTF-8 está malformada
					bw.write(statusPhrase);
					bw.close();
					usersE3Encoding.add(x);
					continue bucle;
				case "04_IOEXCEPTION":    // la clase del servlet no está o produjo una excepción
					bw.write(statusPhrase);
					bw.close();
					usersE4IOException.add(x);
					continue bucle;
				case "05_BF":   // la respuesta no es well-formed
					bw.write(statusPhrase);
					bw.close();
					usersE5Bf.add(x);
					continue bucle;
				case "06_INVALID":   // la respuesta es inválida
					bw.write(statusPhrase);
					bw.close();
					usersE6Invalid.add(x);
					continue bucle;
				case "07_ERRORUNKNOWN":   // error desconocido
					bw.write(statusPhrase);
					bw.close();
					usersE7Error.add(x);
					continue bucle;
				case "08_OKNOPASSWD":   // responde bien incluso sin passwd
					bw.write(statusPhrase);
					bw.close();
					usersE8OkNoPasswd.add(x);
					continue bucle;
				case "09_BADPASSWD":   // la passwd es incorrecta
					bw.write(statusPhrase);
					bw.close();
					usersE9BadPasswd.add(x);
					continue bucle;
				case "10_BADANSWER":   // la respuesta es inesperada
					bw.write(statusPhrase);
					bw.close();
					usersE10BadAnswer.add(x);
					continue bucle;		
				default:      // error desconocido
					bw.write("Respuesta desconocida de doOneCheckUpStatus(): "+oneCheckStatus);
					bw.close();
					usersE7Error.add(x);
					continue bucle;
			}


			// el servicio del alumno está operativo, continuamos

			servicioAlu = "http://"+CommonMMLChecker.server_port+"/"+sintUser+CommonMMLChecker.SERVICE_NAME;
			
			String resultado = Query1.correctC1OneStudent(sintUser, servicioAlu, passwdAlu);

			bw.write("Comparando peticiones... ");
			bw.write(resultado);
			bw.newLine();
			bw.close();
			
			if (!resultado.equals("OK") )
				usersDiff.add(x);
			else 
				usersOK.add(x);
			
		}

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

		if (usersDiff.size() >0) { 	
			out.print("<h4 style='color: red'>Servicios con diferencias respecto a los resultados esperados ("+usersDiff.size()+"): ");
			for (int x=0; x < usersDiff.size(); x++) {
				numAlu = usersDiff.get(x);
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

		CommonSINT.printEndPageChecker(out,  "13", esProfesor, CommonMML.CREATED);
	}

	
	

	
	// Métodos auxiliares para la correción de un alumno de la consulta 1
	
	
	// pide y devuelve la lista de años de un usuario
	// levanta excepciones si algo va mal
	
	private static ArrayList<String> requestAnios (String usuario, String url, String passwd) 
									throws ExcepcionSINT  
	{
		Document doc;
		String call, qs;
		ArrayList<String> listaAnios = new ArrayList<String>();
		
		qs = "?auto=si&"+PFASE+"=11&p="+passwd;
		call = url+qs;
		
		CommonMMLChecker.errorHandler.clear();

		try {
			doc = CommonMMLChecker.db.parse(call);
		}
		catch (SAXException ex) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			throw new ExcepcionSINT("<br>SAXException al solicitar y parsear la lista de años: <br>"+usuario+" --> "+ex) ;
		}
		catch (Exception ex) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			throw new ExcepcionSINT("<br>Exception al solicitar y parsear la lista de años: <br>"+usuario+" --> "+ex);
		}

		
		if (CommonMMLChecker.errorHandler.hasErrors()) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			ArrayList<String> errors = CommonMMLChecker.errorHandler.getErrors();
			String msg="";
			
			for (int x=0; x < errors.size(); x++) {
				msg += "++++"+errors.get(x);
			}
			
			throw new ExcepcionSINT("La lista de años es inválida, tiene errors: "+usuario+" --> "+msg);
		}
		
		if (CommonMMLChecker.errorHandler.hasFatalerrors()) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			ArrayList<String> fatalerrors = CommonMMLChecker.errorHandler.getFatalerrors();
			String msg="";
			
			for (int x=0; x < fatalerrors.size(); x++) {
				msg += "++++"+fatalerrors.get(x);
			}
			
			throw new ExcepcionSINT("La lista de años es inválida, tiene fatal errors: "+usuario+" --> "+msg); 
		}
		
		if (doc == null) {
			throw new ExcepcionSINT("Se recibe 'null' al solicitar y parsear la lista de años: "+usuario);
		}
		

		NodeList nlAnios = doc.getElementsByTagName("anio");

		// procesamos todos los años

		for (int x=0; x < nlAnios.getLength(); x++) {
			Element elemAnio = (Element)nlAnios.item(x);
			String anio = elemAnio.getTextContent().trim();
			
			listaAnios.add(anio);
		}
		
		return listaAnios;
	}
	
	
	// para comparar el resultado de la F11: listas de años
	
	private static String comparaAnios (String usuario, ArrayList<String> pAnios, ArrayList<String> xAnios) 
	{	
		if (pAnios.size() != xAnios.size()) 
			return usuario+": Debería devolver "+pAnios.size()+" años, pero devuelve "+xAnios.size(); 
			
		for (int x=0; x < pAnios.size(); x++) 
			if (!xAnios.get(x).equals(pAnios.get(x))) 
			return usuario+": El año número "+x+" debería ser '"+pAnios.get(x)+"', pero es '"+xAnios.get(x)+"'"; 
		
		return "OK";
	}
	
	
	

	
	// pide y devuelve la lista de peliculas de un año
	// levanta excepciones si algo va mal
	
	private static ArrayList<Pelicula> requestPeliculasAnio (String usuario, String url, String anio, String passwd) 
					throws ExcepcionSINT  
	{
		Document doc;
		String call, qs;
		ArrayList<Pelicula> listaPeliculas = new ArrayList<Pelicula>();
		
		qs = "?auto=si&"+PFASE+"=12&"+PANIO+"="+anio+"&p="+passwd;
		call = url+qs;
		
		CommonMMLChecker.errorHandler.clear();

		try {
			doc = CommonMMLChecker.db.parse(call);   
		}
		catch (SAXException ex) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			throw new ExcepcionSINT("<br>SAXException al solicitar y parsear la lista de películas:<br> "+usuario+"+"+anio+" --> "+ex) ;
		}
		catch (Exception ex) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			throw new ExcepcionSINT("<br>Exception al solicitar y parsear la lista de películas:<br> "+usuario+"+"+anio+" --> "+ex);
		}

		if (CommonMMLChecker.errorHandler.hasErrors()) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			ArrayList<String> errors = CommonMMLChecker.errorHandler.getErrors();
			String msg="";
			
			for (int x=0; x < errors.size(); x++) {
				msg += "++++"+errors.get(x);
			}
			
			throw new ExcepcionSINT("La lista de películas es inválida, tiene errors: "+usuario+"+"+anio+" --> "+msg);
		}
		
		if (CommonMMLChecker.errorHandler.hasFatalerrors()) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			ArrayList<String> fatalerrors = CommonMMLChecker.errorHandler.getFatalerrors();
			String msg="";
			
			for (int x=0; x < fatalerrors.size(); x++) {
				msg += "++++"+fatalerrors.get(x);
			}
			
			throw new ExcepcionSINT("La lista de películas es inválida, tiene fatal errors: "+usuario+"+"+anio+" --> "+msg); 
		}
		
		
		if (doc == null) {
			throw new ExcepcionSINT("Se recibe 'null' al solicitar y parsear la lista de películas:"+usuario+"+"+anio);
		}
		

		NodeList nlPeliculass = doc.getElementsByTagName("pelicula");

		// procesamos todas las películas

		for (int x=0; x < nlPeliculass.getLength(); x++) {
			Element elemPelicula = (Element)nlPeliculass.item(x);
			String titulo = elemPelicula.getTextContent().trim();
			
			String duracion = elemPelicula.getAttribute("duracion");
			String langs = elemPelicula.getAttribute("langs");
			
			listaPeliculas.add(new Pelicula(titulo,Integer.valueOf(duracion), langs));
		}
		
		return listaPeliculas;
	}
	
	
	// para comparar el resultado de la F12: listas de películas
	
	private static String comparaPeliculas (String usuario, String anioActual, ArrayList<Pelicula> pPeliculas, ArrayList<Pelicula> xPeliculas) 
	{
		String pTituloPel, xTituloPel, pLangsPel, xLangsPel;
		int pDuracionPel, xDuracionPel;
		
		if (pPeliculas.size() != xPeliculas.size()) 
			return usuario+"+"+anioActual+": debería devolver "+pPeliculas.size()+" películas, pero devuelve "+xPeliculas.size()+"</h4>"; 
	
		for (int y=0; y < pPeliculas.size(); y++) {
			
			pTituloPel = pPeliculas.get(y).getTitulo();
			xTituloPel = xPeliculas.get(y).getTitulo();
			
			if (!xTituloPel.equals(pTituloPel)) 
				return usuario+"+"+anioActual+": la película número "+y+" debería ser '<pre>"+pTituloPel+"</pre>', pero es '<pre>"+xTituloPel+"</pre>'"; 
			
			pDuracionPel = pPeliculas.get(y).getDuracion();
			xDuracionPel = xPeliculas.get(y).getDuracion();
			
		   	if (pDuracionPel != xDuracionPel) 
				return usuario+"+"+anioActual+": la película número "+y+" debería durar '"+pDuracionPel+"', pero dura '"+xDuracionPel+"'"; 
			
		   	pLangsPel = pPeliculas.get(y).getIdiomas();
		   	xLangsPel = xPeliculas.get(y).getIdiomas();
			
		   	if (!pLangsPel.equals(xLangsPel)) 
				return usuario+"+"+anioActual+": la película número "+y+" debería tener idiomas '<pre>"+pLangsPel+"</pre>', pero tiene '<pre>"+xLangsPel+"</pre>'"; 
		}
		
		return "OK";
	}
    
	
	
	
	
	// pide y devuelve la lista de actores de una película de un año
	// levanta excepciones si algo va mal
	
	private static ArrayList<Actor> requestActoresPelicula (String usuario, String url, String anio, String pel, String passwd) 
									throws ExcepcionSINT  
	{
		Document doc;
		String call, qs;
		ArrayList<Actor> listaActores = new ArrayList<Actor>();
		
		try {
		   qs = "?auto=si&"+PFASE+"=13&"+PANIO+"="+anio+"&"+PPELI+"="+URLEncoder.encode(pel, "utf-8")+"&p="+passwd;
		}
		catch (UnsupportedEncodingException ex) {throw new ExcepcionSINT("utf-8 no soportado");}
			
		call = url+qs;
		
		CommonMMLChecker.errorHandler.clear();

		try {
			doc = CommonMMLChecker.db.parse(call);
		}
		catch (SAXException e) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			throw new ExcepcionSINT("<br>SAXException al solicitar y parsear la lista de actores:<br> "+usuario+"+"+anio+"+"+pel+" --> "+e) ;
		}
		catch (Exception e) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			throw new ExcepcionSINT("<br>Exception al solicitar y parsear la lista de actores:<br> "+usuario+"+"+anio+"+"+pel+" --> "+e);
		}

		if (CommonMMLChecker.errorHandler.hasErrors()) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			ArrayList<String> errors = CommonMMLChecker.errorHandler.getErrors();
			String msg="";
			
			for (int x=0; x < errors.size(); x++) {
				msg += "++++"+errors.get(x);
			}
			
			throw new ExcepcionSINT("La lista de actores es inválida, tiene errors: "+usuario+"+"+anio+"+"+pel+" --> "+msg);
		}
		
		if (CommonMMLChecker.errorHandler.hasFatalerrors()) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			ArrayList<String> fatalerrors = CommonMMLChecker.errorHandler.getFatalerrors();
			String msg="";
			
			for (int x=0; x < fatalerrors.size(); x++) {
				msg += "++++"+fatalerrors.get(x);
			}
			
			throw new ExcepcionSINT("La lista de actores es inválida, tiene fatal errors: "+usuario+"+"+anio+"+"+pel+" --> "+msg); 
		}
		
		
		if (doc == null) {
			throw new ExcepcionSINT("Se recibe 'null' al solicitar y parsear la lista de actores: "+usuario+"+"+anio+"+"+pel);
		}
		

		NodeList nlActores = doc.getElementsByTagName("act");

		// procesamos todos los actores

		for (int x=0; x < nlActores.getLength(); x++) {
			Element elemActor = (Element)nlActores.item(x);
			String nombre = elemActor.getTextContent().trim();
			String ciudad = elemActor.getAttribute("ciudad");
			
			listaActores.add(new Actor(nombre,ciudad));
		}
		
		return listaActores;
	}
	
	
	
	// para comparar el resultado de la F13: listas de actores
	
	private static String comparaActores (String usuario, String anioActual, String pelActual, ArrayList<Actor> pActores, ArrayList<Actor> xActores) 
	{	
	    if (pActores.size() != xActores.size()) 
		return usuario+"+"+anioActual+"+"+pelActual+": debería devolver '"+pActores.size()+"' actores, pero devuelve '"+xActores.size()+"'"; 
	
	    
	    for (int z=0; z < pActores.size(); z++) {
	    	    if (!xActores.get(z).getNombre().equals(pActores.get(z).getNombre()))
			    return usuario+"+"+anioActual+"+"+pelActual+": el actor número "+z+" debería ser '<pre>"+pActores.get(z).getNombre()+"</pre>', pero es '<pre>"+xActores.get(z).getNombre()+"</pre>'"; 
	    	
	    	    if (!xActores.get(z).getCiudad().equals(pActores.get(z).getCiudad()))
			    return usuario+"+"+anioActual+"+"+pelActual+": la ciudad del actor número "+z+" debería ser '<pre>"+pActores.get(z).getCiudad()+"</pre>', pero es '<pre>"+xActores.get(z).getCiudad()+"</pre>'"; 
	    }
	    
	    return "OK";  
	}
	
	
	
	
	// pide y devuelve la filmografia de un actor de una película de un año
	// levanta excepciones si algo va mal
	
	private static Filmografia requestFilmografiaActor (String usuario, String url, String anio, String pel, String act, String passwd) 
								throws ExcepcionSINT  
	{
		Document doc;
		String call, qs;
		
		ArrayList<Film> listaFilms = new ArrayList<Film>();
		
		try {
			qs = "?auto=si&"+PFASE+"=14&"+PANIO+"="+anio+"&"+PPELI+"="+URLEncoder.encode(pel,"utf-8")+"&"+PACTOR+"="+URLEncoder.encode(act, "utf-8")+"&p="+passwd;
		}
		catch (UnsupportedEncodingException ex) {throw new ExcepcionSINT("utf-8 no soportado");}
	
		call = url+qs;
		
		CommonMMLChecker.errorHandler.clear();

		try {
			doc = CommonMMLChecker.db.parse(call);
		}
		catch (SAXException ex) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			throw new ExcepcionSINT("<br>SAXException al solicitar y parsear la filmografía:<br> "+usuario+"+"+anio+"+"+pel+"+"+act+" --> "+ex) ;
		}
		catch (Exception ex) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			throw new ExcepcionSINT("<br>Exception al solicitar y parsear la filmografía:<br> "+usuario+"+"+anio+"+"+pel+"+"+act+" --> "+ex) ;
		}

		if (CommonMMLChecker.errorHandler.hasErrors()) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			ArrayList<String> errors = CommonMMLChecker.errorHandler.getErrors();
			String msg="";
			
			for (int x=0; x < errors.size(); x++) {
				msg += "++++"+errors.get(x);
			}
			
			throw new ExcepcionSINT("La filmografía es inválida, tiene errors: "+usuario+"+"+anio+"+"+pel+"+"+act+" --> "+msg);
		}
		
		if (CommonMMLChecker.errorHandler.hasFatalerrors()) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			ArrayList<String> fatalerrors = CommonMMLChecker.errorHandler.getFatalerrors();
			String msg="";
			
			for (int x=0; x < fatalerrors.size(); x++) {
				msg += "++++"+fatalerrors.get(x);
			}
			
			throw new ExcepcionSINT("La filmografía es inválida, tiene fatal errors: "+usuario+"+"+anio+"+"+pel+"+"+act+" --> "+msg); 
		}
	
		
		if (doc == null) {
			throw new ExcepcionSINT("Se recibe 'null' al solicitar y parsear la filmografía: "+usuario+"+"+anio+"+"+pel+"+"+act);
		}
		

		NodeList nlFilmografia = doc.getElementsByTagName("filmografia");
        if (nlFilmografia.getLength() != 1) throw new ExcepcionSINT(call+": No se recibe elemento 'filmografia'");
		
		Element elemFilmografia = (Element)nlFilmografia.item(0);
		String nombreActor = elemFilmografia.getAttribute("nombre");
		String personaje = elemFilmografia.getAttribute("personaje");
		
		NodeList nlFilms = elemFilmografia.getElementsByTagName("film");

		// procesamos todos los films

		for (int x=0; x < nlFilms.getLength(); x++) {
			Element elemFilm = (Element)nlFilms.item(x);
			String titulo = elemFilm.getTextContent().trim();
			String oscar = elemFilm.getAttribute("oscar");
				
			listaFilms.add(new Film(titulo, oscar));
		}
		
		return new Filmografia(nombreActor, personaje, listaFilms);
	}
	
	
	// para comparar el resultado de la F14: filmografía
	
	private static String comparaFilmografias (String usuario, String anioActual, String pelActual, String actorActual, Filmografia pFilmografia, Filmografia xFilmografia) 
	{
	    if (!pFilmografia.getNombre().equals(xFilmografia.getNombre()))
			return usuario+"+"+anioActual+"+"+pelActual+"+"+actorActual+": debería ser '<pre>"+pFilmografia.getNombre()+"</pre>', pero es '<pre>"+xFilmografia.getNombre()+"</pre>'"; 
			
	    if (!pFilmografia.getPersonaje().equals(xFilmografia.getPersonaje()))
			return usuario+"+"+anioActual+"+"+pelActual+"+"+actorActual+": debería ser el personaje '<pre>"+pFilmografia.getPersonaje()+"</pre>', pero es '<pre>"+xFilmografia.getPersonaje()+"</pre>'"; 
	        
	    ArrayList<Film> pFilms=pFilmografia.getFilms(), xFilms=xFilmografia.getFilms();
	        
	    // comparamos las listas de sintprof y sintX
			
	    if (pFilms.size() != xFilms.size()) 
			return usuario+"+"+anioActual+"+"+pelActual+"+"+actorActual+": debería devolver '"+pFilms.size()+"' films, pero devuelve '"+xFilms.size()+"'"; 
	    		
	        Film pFilm, xFilm;
	        
	        for (int t=0; t < pFilms.size(); t++) {
	        	pFilm = pFilms.get(t);
	        	xFilm = xFilms.get(t);
	        	
	        	if (!xFilm.getTitulo().equals(pFilm.getTitulo())) 
	    			return usuario+"+"+anioActual+"+"+pelActual+"+"+actorActual+": el film número "+t+" debería ser '<pre>"+pFilm.getTitulo()+"</pre>', pero es '<pre>"+xFilm.getTitulo()+"</pre>'"; 
	        	
	        	if (!xFilm.getOscar().equals(pFilm.getOscar())) 
	    			return usuario+"+"+anioActual+"+"+pelActual+"+"+actorActual+": el film número "+t+" debería tener óscar '<pre>"+pFilm.getOscar()+"</pre>', pero tiene '<pre>"+xFilm.getOscar()+"</pre>'"; 
	        	
	        }  // for t
	
	    return "OK";
	}
		
		
		
	// pide  la lista de filmografía de un actor de una película de un año
	// comprueba que recibe del usuario las correspondientes notificaciones de error
	// levanta excepciones si algo va mal
	
	private static void checkLackParam (String usuario, String url, String anio, String pel, String act, String passwd) 
						throws ExcepcionSINT  
	{
		Document doc;
		Element e;
		String tagName, reason, call, qs, qs1, qs2, qs3;
		
		try {
			qs = "?auto=si&"+PFASE+"=14&p="+passwd;
			qs1 = qs+"&"+PPELI+"="+URLEncoder.encode(pel,"utf-8")+"&"+PACTOR+"="+URLEncoder.encode(act, "utf-8");  // falta PPAIS
			qs2 = qs+"&"+PANIO+"="+anio+"&"+PACTOR+"="+URLEncoder.encode(act, "utf-8");                                // falta PPELI               
			qs3 = qs+"&"+PANIO+"="+anio+"&"+PPELI+"="+URLEncoder.encode(pel,"utf-8");                            // falta PACTOR
		}
		catch (UnsupportedEncodingException ex) {throw new ExcepcionSINT("utf-8 no soportado");}
		
		
		// probando qs1, donde falta PPAIS
	
		CommonMMLChecker.errorHandler.clear();
		call = url+qs1;

		try {
			doc = CommonMMLChecker.db.parse(call);
		}
		catch (SAXException ex) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			throw new ExcepcionSINT("<br>SAXException al solicitar y parsear la filmografía sin '"+PANIO+"':<br> "+usuario+" --> "+ex) ;
		}
		catch (Exception ex) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			throw new ExcepcionSINT("<br>Exception al solicitar y parsear la filmografía sin '"+PANIO+"':<br> "+usuario+" --> "+ex) ;
		}

		if (CommonMMLChecker.errorHandler.hasErrors()) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			ArrayList<String> errors = CommonMMLChecker.errorHandler.getErrors();
			String msg="";
			
			for (int x=0; x < errors.size(); x++) {
				msg += "++++"+errors.get(x);
			}
			
			throw new ExcepcionSINT("La respuesta (al solicitar y parsear la filmografía sin '"+PANIO+"') es inválida, tiene errors: "+usuario+" --> "+msg);
		}
		
		if (CommonMMLChecker.errorHandler.hasFatalerrors()) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			ArrayList<String> fatalerrors = CommonMMLChecker.errorHandler.getFatalerrors();
			String msg="";
			
			for (int x=0; x < fatalerrors.size(); x++) {
				msg += "++++"+fatalerrors.get(x);
			}
			
			throw new ExcepcionSINT("La respuesta (al solicitar y parsear la filmografía sin '"+PANIO+"') es inválida, tiene fatal errors: "+usuario+" --> "+msg); 
		}
			
		if (doc == null) {
			throw new ExcepcionSINT("Se recibe 'null' al solicitar la filmografía sin '"+PANIO+"': "+usuario);
		}
		
		e = doc.getDocumentElement();
		tagName = e.getTagName();
		if (tagName.equals("wrongRequest")) {
			reason = e.getTextContent();
			if (!reason.equals("no param:"+PANIO))
			throw new ExcepcionSINT("Se recibe <wrongRequest> pero sin 'no param:anio' al solicitar la filmografía sin '"+PANIO+"': "+usuario+"+"+reason);
		}
		else throw new ExcepcionSINT("No se recibe 'wrongRequest' al solicitar la filmografía sin '"+PANIO+"': "+usuario);
		

		
		// probando qs2, donde falta PPELI
		
		CommonMMLChecker.errorHandler.clear();

		call = url+qs2;
		
		try {
			doc = CommonMMLChecker.db.parse(call);
		}
		catch (SAXException ex) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			throw new ExcepcionSINT("<br>SAXException al solicitar y parsear la filmografía sin '"+PPELI+"':<br> "+usuario+" --> "+ex) ;
		}
		catch (Exception ex) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			throw new ExcepcionSINT("<br>Exception al solicitar y parsear la filmografía sin '"+PPELI+"':<br> "+usuario+" --> "+ex) ;
		}

		if (CommonMMLChecker.errorHandler.hasErrors()) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			ArrayList<String> errors = CommonMMLChecker.errorHandler.getErrors();
			String msg="";
			
			for (int x=0; x < errors.size(); x++) {
				msg += "++++"+errors.get(x);
			}
			
			throw new ExcepcionSINT("La respuesta (al solicitar y parsear la filmografía sin '"+PPELI+"') es inválida, tiene errors: "+usuario+" --> "+msg);
		}
		
		if (CommonMMLChecker.errorHandler.hasFatalerrors()) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			ArrayList<String> fatalerrors = CommonMMLChecker.errorHandler.getFatalerrors();
			String msg="";
			
			for (int x=0; x < fatalerrors.size(); x++) {
				msg += "++++"+fatalerrors.get(x);
			}
			
			throw new ExcepcionSINT("La respuesta (al solicitar y parsear la filmografía sin '"+PPELI+"') es inválida, tiene fatal errors: "+usuario+" --> "+msg); 
		}
			
		if (doc == null) {
			throw new ExcepcionSINT("Se recibe 'null' al solicitar la filmografía sin '"+PPELI+"': "+usuario);
		}
		
		e = doc.getDocumentElement();
		tagName = e.getTagName();
		if (tagName.equals("wrongRequest")) {
			reason = e.getTextContent();
			if (!reason.equals("no param:"+PPELI))
			throw new ExcepcionSINT("Se recibe <wrongRequest> pero sin 'no param:"+PPELI+"' al solicitar la filmografía sin '"+PPELI+"': "+usuario+"+"+reason);
		}
		else throw new ExcepcionSINT("No se recibe 'wrongRequest' al solicitar la filmografía sin '"+PPELI+"': "+usuario);
		
		
	// probando qs3, donde falta PACTOR
		
		CommonMMLChecker.errorHandler.clear();

		call = url+qs3;
		
		try {
			doc = CommonMMLChecker.db.parse(call);
		}
		catch (SAXException ex) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			throw new ExcepcionSINT("<br>SAXException al solicitar y parsear la filmografía sin '"+PACTOR+"':<br> "+usuario+" --> "+ex) ;
		}
		catch (Exception ex) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			throw new ExcepcionSINT("<br>Exception al solicitar y parsear la filmografía sin '"+PACTOR+"':<br> "+usuario+" --> "+ex) ;
		}

		if (CommonMMLChecker.errorHandler.hasErrors()) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			ArrayList<String> errors = CommonMMLChecker.errorHandler.getErrors();
			String msg="";
			
			for (int x=0; x < errors.size(); x++) {
				msg += "++++"+errors.get(x);
			}
			
			throw new ExcepcionSINT("La respuesta (al solicitar y parsear la filmografía sin '"+PACTOR+"') es inválida, tiene errors: "+usuario+" --> "+msg);
		}
		
		if (CommonMMLChecker.errorHandler.hasFatalerrors()) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			ArrayList<String> fatalerrors = CommonMMLChecker.errorHandler.getFatalerrors();
			String msg="";
			
			for (int x=0; x < fatalerrors.size(); x++) {
				msg += "++++"+fatalerrors.get(x);
			}
			
			throw new ExcepcionSINT("La respuesta (al solicitar y parsear la filmografía sin '"+PACTOR+"') es inválida, tiene fatal errors: "+usuario+" --> "+msg); 
		}
			
		if (doc == null) {
			throw new ExcepcionSINT("Se recibe 'null' al solicitar la filmografía sin '"+PACTOR+"': "+usuario);
		}
		
		e = doc.getDocumentElement();
		tagName = e.getTagName();
		if (tagName.equals("wrongRequest")) {
			reason = e.getTextContent();
			if (!reason.equals("no param:"+PACTOR))
			throw new ExcepcionSINT("Se recibe <wrongRequest> pero sin 'no param:"+PACTOR+"' al solicitar la filmografía sin '"+PACTOR+"': "+usuario+"+"+reason);
		}
		else throw new ExcepcionSINT("No se recibe 'wrongRequest' al solicitar la filmografía sin '"+PACTOR+"': "+usuario);
		
		// todo bien
		return;
	}

}
