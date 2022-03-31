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


// Implementación de la comprobación de la consulta 2 (películas de un actor S2 en un idioma S1 de un país S3)

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

import docencia.sint.MML.Actor;
import docencia.sint.MML.CommonMML;
import docencia.sint.MML.Pais;
import docencia.sint.MML.Pelicula;
import docencia.sint.Common.CommonSINT;
import docencia.sint.Common.ExcepcionSINT;
import docencia.sint.Common.SintRandom;



public class Query2 {
		
	// nombres de los parámetros de esta consulta
	
	static final String PFASE = "pfase";
	static final String PLANG = "plang";	
    static final String PACTOR = "pact";
    static final String PPAIS = "ppais";


	
	// COMPROBACIÓN DE LAS LLAMADAS A SINTPROF
	
	public static void doGetC2CheckSintprofCalls(HttpServletRequest request, PrintWriter out)
						throws IOException, ServletException
	{	
		int esProfesor = 1;  // sólo el profesor debería llegar aquí
		
		out.println("<html>");
		CommonMMLChecker.printHead(out);
		CommonMMLChecker.printBodyHeader(out);
		
		out.println("<h2>Consulta 2</h2>");
		out.println("<h3>Comprobando las llamadas al servicio de sintprof</h3>");
		
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
			out.println("<h4 style='color: red'>sintprof (ExcepcionSINT pidiendo Errores): "+ex.toString()+"</h4>");
			CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonMML.CREATED);
			return;
        }
		catch (Exception ex) { 
			out.println("<h4 style='color: red'>sintprof (Exception pidiendo Errores): "+ex.toString()+"</h4>");
			CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonMML.CREATED);
			return;
        }
		
		
		// y ahora todas y cada una de las consultas
		
		// pedimos la lista de idiomas de sintprof
	 
		ArrayList<String> pLangs;
		try {
			pLangs = Query2.requestLangs("sintprof", CommonMMLChecker.servicioProf, CommonSINT.PASSWD);
			out.println("<h4>Idiomas OK: "+pLangs.size()+"</h4>");
		}
		catch (ExcepcionSINT ex) { 
			out.println("<h4 style='color: red'>sintprof  (ExcepcionSINT pidiendo idiomas): "+ex.toString()+"</h4>");
			CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonMML.CREATED);
			return;
        }
	    
		// vamos con la segunda fase, los actores con películas en cada idioma
		// el bucle X recorre todos los idiomas
		
		
		String langActual;
	     
		for (int x=0; x < pLangs.size(); x++) {
			String indent ="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
	    	
			langActual = pLangs.get(x);
			
			// pedimos los actores de ese idioma de sintprof
	   	 
			ArrayList<Actor> pActores;
			try {
				pActores = Query2.requestActoresLang("sintprof", CommonMMLChecker.servicioProf, langActual, CommonSINT.PASSWD);
				out.println("<h4>"+indent+langActual+": "+pActores.size()+"  OK</h4>");
			}
			catch (Exception e) { 
				out.println("<h4 style='color: red'>sintprof (Actores): "+e.toString()+"</h4>");
				CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonMML.CREATED);
				return;
	        }
	      
	        
	        // vamos con la tercera fase, los paises de un actor
	        // el bucle Y recorre todas las películas
	        
	        Actor actorActual;
	        
	        for (int y=0; y < pActores.size(); y++) {
	        	
		        	indent = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		        	
		        	actorActual = pActores.get(y);
		        	
		    		// pedimos los paises de ese actor de ese idioma de sintprof
		       	 
		    		ArrayList<Pais> pPaises;
		    		try {
		    			pPaises = Query2.requestPaisesActor("sintprof", CommonMMLChecker.servicioProf, langActual, actorActual.getNombre(), CommonSINT.PASSWD);
					out.println("<h4>"+indent+langActual+"+"+actorActual.getNombre()+": "+pPaises.size()+"  OK</h4>");
		    		}
		    		catch (ExcepcionSINT e) { 
		    			out.println("<h4 style='color: red'>sintprof (ExcepcionSINT pidiendo paises): "+e.toString()+"</h4>");
		    			CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonMML.CREATED);
		    			return;
		        }
		    	            
		            
		            
	            // vamos con la cuarta fase, las películas de un actor en un idioma de un pais
	            // el bucle Z recorre todos los paises
	            
            		Pais paisActual;
            	
	            for (int z=0; z < pPaises.size(); z++) {
			        	indent = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			        	
			        	paisActual = pPaises.get(z);
		            			
		        		// pedimos las peliculas de ese actor de sintprof
		           	 
			    		ArrayList<Pelicula> pPeliculas;
			    		
		        		try {
		        			pPeliculas = Query2.requestPeliculasPais("sintprof", CommonMMLChecker.servicioProf, langActual, actorActual.getNombre(), paisActual.getNombre(), CommonSINT.PASSWD);
						out.println("<h4>"+indent+langActual+"+"+actorActual.getNombre()+"+"+paisActual.getNombre()+": "+pPeliculas.size()+"  OK</h4>");
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
	// debería unificarse en uno sólo con el de la consulta 1, son casi iguales

	public static void doGetC2CorrectOneForm (HttpServletRequest request, PrintWriter out, int esProfesor)
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
		
		out.println("<h2>Consulta 2</h2>");                               // consulta 2
		out.println("<h3>Corrección de un único servicio</h3>");
		
		out.println("<form>");
		out.println("<input type='hidden' name='screenP' value='221'>");  // conduce a doGetC2CorrectOneReport

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

	public static void doGetC2CorrectOneReport(HttpServletRequest request, PrintWriter out, int esProfesor)
						throws IOException, ServletException
	{	
		out.println("<html>");
		CommonMMLChecker.printHead(out);
		CommonMMLChecker.printBodyHeader(out);
		
		out.println("<h2>Consulta 2</h2>");
		out.println("<h3>Corrección de un único servicio</h3>");
		
		// leemos los datos del estudiante
		
		String alumnoP = request.getParameter("alumnoP");
		String servicioAluP = request.getParameter("servicioAluP");
		
		if ((alumnoP == null) || (servicioAluP == null)) {
			out.println("<h4 style='color: red'>Falta uno de los parámetros</h4>");  // si falta algún parámetro no se hace nada
			CommonSINT.printEndPageChecker(out,  "22", esProfesor, CommonMML.CREATED);
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
			CommonSINT.printEndPageChecker(out,  "22", esProfesor, CommonMML.CREATED);
			return; 
		}
		
		if (esProfesor == 0) {  // lo está probando un alumno, es obligatorio que haya introducido su passwd
			passwdRcvd = request.getParameter("passwdAlu");   // leemos la passwd del alumno del parámetro
			
			if (passwdRcvd == null) {
				out.println("<h4 style='color: red'>No se ha recibido la passwd de "+usuario+"</h4>"); 
				CommonSINT.printEndPageChecker(out,  "22", esProfesor, CommonMML.CREATED);
				return; 
			}
			
			if (!passwdAlu.equals(passwdRcvd)) {
				out.println("<h4 style='color: red'>La passwd proporcionada no coincide con la almacenada en el sistema para "+usuario+"</h4>"); 
				CommonSINT.printEndPageChecker(out,  "22", esProfesor, CommonMML.CREATED);
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
			CommonSINT.printEndPageChecker(out,  "22", esProfesor, CommonMML.CREATED);
			return;
		}
		
		oneCheckStatus = CommonMMLChecker.doOneCheckUpStatus(request, usuario, passwdAlu);  
		statusCode = oneCheckStatus.substring(0,oneCheckStatus.indexOf(','));
		statusPhrase = oneCheckStatus.substring (oneCheckStatus.indexOf(',')+1);

		if (!statusCode.equals("OK")) {
			out.println("<h4 style='color: red'>"+usuario+" (error al preguntar por el estado): "+statusPhrase+"</h4>");
			CommonSINT.printEndPageChecker(out,  "22", esProfesor, CommonMML.CREATED);
			return;
		}
		
		out.println("<h3>El servicio está en estado operativo </h3>");

		
		// todas las comprobaciones preliminares están bien, vamos a pedir las consultas

		CommonMMLChecker.logMMLChecker("Comprobaciones preliminares OK");
		
		String resultado = Query2.correctC2OneStudent(usuario, servicioAluP, passwdAlu);
		
		// el resultado será 'OK' o una descripción de la diferencia encontrada (termina al encontrar una diferencia)
		
		out.println("<h3>Resultado: "+resultado+"</h3>");
	
		// terminamos con botones Atrás e Inicio

		CommonSINT.printEndPageChecker(out,  "22", esProfesor, CommonMML.CREATED);
	}
	
	
	
	
	// método que corrige la consulta 2 de un estudiante

	private static String correctC2OneStudent (String usuario, String servicioAluP, String passwdAlu) 
	{
		String resultComp;
		
		// para la consulta directa final, vamos a escoger lang, actor y país al azar y a guardarlas en esas variables
		
		SintRandom.init(); // inicializamos el generador de números aleatorios
		int posrandom;
		
		String dqLang="";
		String dqActor="";
		String dqPais="";
		
		
		// empezamos por comprobar los errores
		
		String resultErrores = CommonMMLChecker.comparaErrores(usuario, servicioAluP, passwdAlu);
		
		if (!resultErrores.equals("OK")) 
			return resultErrores;

		
		
		// y ahora todas y cada una de las consultas
		
		// pedimos la lista de langs de sintprof
	 
		ArrayList<String> pLangs;
		try {
			pLangs = Query2.requestLangs("sintprof", CommonMMLChecker.servicioProf, CommonSINT.PASSWD);
		}
		catch (Exception ex) { return "<br>Excepción solicitando la lista de idiomas a sintprof:<br> "+ex.toString(); }
	
	
		// pedimos la lista de langs del sintX
		
		ArrayList<String> xLangs;
		try {
			xLangs = Query2.requestLangs(usuario, servicioAluP, passwdAlu);
		}
		catch (Exception ex) { return "<br>Excepción solicitando la lista de idiomas a "+usuario+":<br> "+ ex.toString(); }
	
		
		// comparamos las listas de sintprof y sintX
		
		resultComp = Query2.comparaLangs(usuario, pLangs, xLangs);
		
		if (!resultComp.equals("OK")) 
			return resultComp;
		
		CommonMMLChecker.logMMLChecker("Listas de langs iguales");
	    
		// las listas de idiomas son iguales
		// elegimos un idioma al azar para la consulta directa final
	    
		posrandom = SintRandom.getRandomNumber(0, pLangs.size()-1);
		dqLang = pLangs.get(posrandom);
	    
		
		
	    
		// vamos con la segunda fase, los actores de cada idioma
		// el bucle X recorre todos los idiomas
		
		
		String langActual;
	     
		for (int x=0; x < pLangs.size(); x++) {
	    	
			langActual = pLangs.get(x);
			
			// pedimos los actores de ese idioma de sintprof
	   	 
			ArrayList<Actor> pActores;
			try {
				pActores = Query2.requestActoresLang("sintprof", CommonMMLChecker.servicioProf, langActual, CommonSINT.PASSWD);
			}
			catch (Exception ex) { return "<br>Excepción solicitando la lista de actores a sintprof:<br> "+ex.toString(); }
		
			
			// pedimos los actores de ese idioma del sintX
			
			ArrayList<Actor> xActores;
			try {
				xActores = Query2.requestActoresLang(usuario, servicioAluP, langActual, passwdAlu);
			}
			catch (Exception ex) { return "<br>Excepción solicitando la lista de actores a "+usuario+":<br> "+ex.toString(); }			
			
			
			// comparamos las listas de sintprof y sintX
			
			resultComp = Query2.comparaActores(usuario, langActual, pActores, xActores);
			
			if (!resultComp.equals("OK")) 
				return resultComp;
	       	        
			CommonMMLChecker.logMMLChecker("Listas de actores iguales");
			
	        // las listas de actores para este idioma son iguales
		    // si este idioma es el de la consulta directa final, elegimos un actor al azar para la consulta directa final
	        
	        
	        if (langActual.equals(dqLang)) {
	            posrandom = SintRandom.getRandomNumber(0, pActores.size()-1);
	            Actor pAct = pActores.get(posrandom);
	          	dqActor = pAct.getNombre();
	        }
	        
	        
	        
	        // vamos con la tercera fase, los países de un actor
	        // el bucle Y recorre todos los actores
	        
	        Actor actActual;
	        
	        for (int y=0; y < pActores.size(); y++) {
	        	
		        	actActual = pActores.get(y);
		        	
		    		// pedimos los países de ese actor de ese idioma de sintprof
		       	 
		    		ArrayList<Pais> pPaises;
		    		try {
		    			pPaises = Query2.requestPaisesActor("sintprof", CommonMMLChecker.servicioProf, langActual, actActual.getNombre(), CommonSINT.PASSWD);
		    		}
		    		catch (ExcepcionSINT ex) { return "<br>ExcepcionSINT solicitando la lista de países a sintprof:<br> "+ex.toString(); }
		    		
		    		
		    		// pedimos los países de ese actor de ese idioma del sintX
		    		
		    		ArrayList<Pais> xPaises;
		    		try {
		    			xPaises = Query2.requestPaisesActor(usuario, servicioAluP, langActual, actActual.getNombre(), passwdAlu);
		    		}
		    		catch (ExcepcionSINT ex) { return "<br>ExcepcionSINT solicitando la lista de países a "+usuario+":<br> "+ex.toString(); }
		    		
		    		
		    		// comparamos las listas de sintprof y sintX
		    		
				resultComp = Query2.comparaPaises(usuario, langActual, actActual.getNombre(), pPaises, xPaises);
				
				if (!resultComp.equals("OK")) 
					return resultComp;
	            
	            
				CommonMMLChecker.logMMLChecker("Listas de paises iguales");
				
	            // las listas de países de este actor son iguales
	            // si este idioma+actor es el de la consulta directa final, elegimos un pais al azar para la consulta directa final
	            
	            if ( (langActual.equals(dqLang)) && (actActual.getNombre().equals(dqActor))) {
		            posrandom = SintRandom.getRandomNumber(0, pPaises.size()-1);
		        	    dqPais = pPaises.get(posrandom).getNombre();
		        }
		            
		            
	            // vamos con la cuarta fase, la lista de películas de un país de un actor de un idioma
	            // el bucle Z recorre todos los actores
	            
	            	Pais paisActual;
	            	
	            for (int z=0; z < pPaises.size(); z++) {
	            	
		            	paisActual = pPaises.get(z);
		            			
		        		// pedimos la lista de películas de ese pais de sintprof
		           	 
		        		ArrayList<Pelicula> pPeliculas;
		        		
		        		try {
		        			pPeliculas = Query2.requestPeliculasPais("sintprof", CommonMMLChecker.servicioProf, langActual, actActual.getNombre(), paisActual.getNombre(), CommonSINT.PASSWD);
		        		}
		        		catch (ExcepcionSINT ex) { return "<br>ExcepcionSINT solicitando la lista de películas a sintprof:<br> "+ex.toString(); }
		        		
		        		
		        		// pedimos la lista de películas de ese país del sintX
		        		
		        		ArrayList<Pelicula> xPeliculas;
		        		
		        		try {
		        			xPeliculas = Query2.requestPeliculasPais(usuario, servicioAluP, langActual, actActual.getNombre(), paisActual.getNombre(), passwdAlu);
		        		}
		        		catch (ExcepcionSINT ex) { return "<br>ExcepcionSINT solicitando la lista de películas a: "+usuario+"<br> "+ex.toString(); }
		        			
		        		
		        		// comparamos las listas de sintprof y sintX
		        		
					resultComp = Query2.comparaPeliculas(usuario, langActual, actActual.getNombre(), paisActual.getNombre(), pPeliculas, xPeliculas);
					
					if (!resultComp.equals("OK")) 
						return resultComp;	
					
					CommonMMLChecker.logMMLChecker("Listas de películas iguales");
	            
	            } // for z
	            
	        } // for y
	         
	    } // for x
	    
	    
		// finalmente la consulta directa
		
		String resultadoDQ = Query2.checkDirectQueryC2(CommonMMLChecker.servicioProf, usuario, servicioAluP, dqLang, dqActor, dqPais, passwdAlu);
		
		if (!resultadoDQ.equals("OK")) 
			return "Resultados erróneos en la consulta directa: "+resultadoDQ; 
		
		// si todas las consultas coincidieron, devuelve 'OK'
	    return "OK";
	}
	
	
	

	
	

	
	
	// comprueba que las consultas directas son iguales   
	
	private static String checkDirectQueryC2(String servicioProf, String usuario, String servicioAluP, String lang, String actor, String pais, String passwdAlu) 
	{	
		ArrayList<Pelicula> pPeliculas, xPeliculas;

		// primero comprobamos que responde con el error apropiado si falta algún parámetro
		
  		try {
  			Query2.checkLackParam(usuario, servicioAluP, lang, actor, pais, passwdAlu);
		}
		catch (ExcepcionSINT ex) { return "<br>ExcepcionSINT solicitando la consulta directa a: "+usuario+"<br> "+ex.toString(); }
  	
  		
  		// ahora comprobamos que los resultados son correctos
  		
   		try {
   			pPeliculas = Query2.requestPeliculasPais("sintprof", CommonMMLChecker.servicioProf, lang, actor, pais, CommonSINT.PASSWD);
		}
		catch (ExcepcionSINT ex) { return "<br>ExcepcionSINT solicitando la consulta directa a sintprof:<br> "+ex.toString(); }
   		
  		try {
  			xPeliculas = Query2.requestPeliculasPais(usuario, servicioAluP, lang, actor, pais, passwdAlu);
		}
		catch (ExcepcionSINT ex) { return "<br>ExcepcionSINT solicitando la consulta directa a: "+usuario+"<br> "+ex.toString(); }
   		
   		
		// comparamos las filmografías de sintprof y sintX
		
		String resultComp = Query2.comparaPeliculas(usuario, lang, actor, pais, pPeliculas, xPeliculas);
		
		if (!resultComp.equals("OK")) 
			return "checkDirectQueryC2: "+resultComp;	    
  	
	
		// si todo coincidió, devuelve 'OK'
		
		return "OK";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	// COMPROBACIÓN DEL SERVICIO DE TODOS LOS ESTUDIANTES

	// pantalla para comprobar todos los estudiantes, se pide el número de cuentas a comprobar (corregir su práctica)

	public static void doGetC2CorrectAllForm (HttpServletRequest request, PrintWriter out)
	{
		int esProfesor = 1;
		
		out.println("<html>");
		CommonMMLChecker.printHead(out);
		CommonMMLChecker.printBodyHeader(out);
		
		out.println("<h2>Consulta 2</h2>");
		out.println("<h3>Corrección de todos los servicios</h3>");

		out.println("<form>");
		out.println("<input type='hidden' name='screenP' value='231'>");

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
		
	public static void doGetC2CorrectAllReport(HttpServletRequest request, PrintWriter out)
						throws IOException, ServletException
	{
		int esProfesor = 1;
		
		out.println("<html>");
		CommonMMLChecker.printHead(out);
		CommonMMLChecker.printBodyHeader(out);
		
		out.println("<h2>Consulta 2</h2>");

		// si no se recibe el parámetro con el número de cuentas no se hace nada

		String numCuentasP = request.getParameter("numCuentasP");
		if (numCuentasP == null) {
			out.println("<h4>Error: no se ha recibido el número de cuentas</h4>");
			CommonSINT.printEndPageChecker(out,  "23", esProfesor, CommonMML.CREATED);
			return;
		}

		int numCuentas=0;

		try {
			numCuentas = Integer.parseInt(numCuentasP);
		}
		catch (NumberFormatException e) {
			out.println("<h4>Error: el número de cuentas recibido no es un número válido</h4>");
			CommonSINT.printEndPageChecker(out,  "23", esProfesor, CommonMML.CREATED);
			return;
		}

		if (numCuentas < 1) {  
			out.println("<h4>Error: el número de cuentas recibido es menor que uno</h4>");
			CommonSINT.printEndPageChecker(out,  "23", esProfesor, CommonMML.CREATED);
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
			CommonSINT.printEndPageChecker(out,  "23", esProfesor, CommonMML.CREATED);
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
			
			String resultado = Query2.correctC2OneStudent(sintUser, servicioAlu, passwdAlu);

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

		CommonSINT.printEndPageChecker(out,  "23", esProfesor, CommonMML.CREATED);
	}

	
	

	
	// Métodos auxiliares para la correción de un alumno de la consulta 2
	
	
	// pide y devuelve la lista de idiomas de un usuario
	// levanta excepciones si algo va mal
	
	private static ArrayList<String> requestLangs (String usuario, String url, String passwd) 
									throws ExcepcionSINT  
	{
		Document doc;
		String call, qs;
		ArrayList<String> listaLangs = new ArrayList<String>();
		
		qs = "?auto=si&"+PFASE+"=21&p="+passwd;
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
			
			throw new ExcepcionSINT("<br>SAXException al solicitar y parsear la lista de idiomas: <br>"+usuario+" --> "+ex) ;
		}
		catch (Exception ex) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			throw new ExcepcionSINT("<br>Exception al solicitar y parsear la lista de idiomas: <br>"+usuario+" --> "+ex);
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
			
			throw new ExcepcionSINT("La lista de idiomas es inválida, tiene errors: "+usuario+" --> "+msg);
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
			
			throw new ExcepcionSINT("La lista de idiomas es inválida, tiene fatal errors: "+usuario+" --> "+msg); 
		}
		
		if (doc == null) {
			throw new ExcepcionSINT("Se recibe 'null' al solicitar y parsear la lista de idiomas: "+usuario);
		}
		

		NodeList nlLangs = doc.getElementsByTagName("lang");

		// procesamos todos los idiomas

		for (int x=0; x < nlLangs.getLength(); x++) {
			Element elemLang = (Element)nlLangs.item(x);
			String lang = elemLang.getTextContent().trim();
			
			listaLangs.add(lang);
		}
		
		return listaLangs;
	}
	
	
	// para comparar el resultado de la F21: listas de idiomas
	
	private static String comparaLangs (String usuario, ArrayList<String> pLangs, ArrayList<String> xLangs) 
	{	
		if (pLangs.size() != xLangs.size()) 
			return usuario+": Debería devolver "+pLangs.size()+" idiomas, pero devuelve "+xLangs.size(); 
			
		for (int x=0; x < pLangs.size(); x++) 
			if (!xLangs.get(x).equals(pLangs.get(x))) 
			return usuario+": El idioma número "+x+" debería ser '"+pLangs.get(x)+"', pero es '"+xLangs.get(x)+"'"; 
		
		return "OK";
	}
	
	
	

	
	// pide y devuelve la lista de actores con películas en un idioma
	// levanta excepciones si algo va mal
	
	private static ArrayList<Actor> requestActoresLang (String usuario, String url, String lang, String passwd) 
									throws ExcepcionSINT  
	{
		Document doc;
		String call, qs;
		ArrayList<Actor> listaActores = new ArrayList<Actor>();
		
		qs = "?auto=si&"+PFASE+"=22&"+PLANG+"="+lang+"&p="+passwd;
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
			
			throw new ExcepcionSINT("<br>SAXException al solicitar y parsear la lista de actores:<br> "+usuario+"+"+lang+" --> "+ex) ;
		}
		catch (Exception ex) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			throw new ExcepcionSINT("<br>Exception al solicitar y parsear la lista de actores:<br> "+usuario+"+"+lang+" --> "+ex);
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
			
			throw new ExcepcionSINT("La lista de actores es inválida, tiene errors: "+usuario+"+"+lang+" --> "+msg);
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
			
			throw new ExcepcionSINT("La lista de actores es inválida, tiene fatal errors: "+usuario+"+"+lang+" --> "+msg); 
		}
		
		
		if (doc == null) {
			throw new ExcepcionSINT("Se recibe 'null' al solicitar y parsear la lista de actores:"+usuario+"+"+lang);
		}
		

		NodeList nlActores = doc.getElementsByTagName("ac");

		// procesamos todos los actores

		for (int x=0; x < nlActores.getLength(); x++) {
			Element elemActor = (Element)nlActores.item(x);
			String nombre = elemActor.getTextContent().trim();
			
			String ciudad = elemActor.getAttribute("ciudad");
			String oscar = elemActor.getAttribute("oscar");
			
			listaActores.add(new Actor(nombre,ciudad, Boolean.valueOf(oscar)));
		}
		
		return listaActores;
	}
	
	
	// para comparar el resultado de la F22: listas de actores
	
	private static String comparaActores (String usuario, String langActual, ArrayList<Actor> pActores, ArrayList<Actor> xActores) 
	{
		String pNombreAct, xNombreAct, pCiudadAct, xCiudadAct;
		Boolean pOscarAct, xOscarAct;
		
		if (pActores.size() != xActores.size()) 
			return usuario+"+"+langActual+": debería devolver "+pActores.size()+" actores, pero devuelve "+xActores.size()+"</h4>"; 
	
		for (int y=0; y < pActores.size(); y++) {
			
			pNombreAct = pActores.get(y).getNombre();
			xNombreAct = xActores.get(y).getNombre();
			
			if (!xNombreAct.equals(pNombreAct)) 
				return usuario+"+"+langActual+": el actor número "+y+" debería ser '<pre>"+pNombreAct+"</pre>', pero es '<pre>"+xNombreAct+"</pre>'"; 
			
			pCiudadAct = pActores.get(y).getCiudad();
			xCiudadAct = pActores.get(y).getCiudad();
			
		   	if (!pCiudadAct.equals(xCiudadAct)) 
				return usuario+"+"+langActual+": el actor número "+y+" debería tener ciudad '<pre>"+pCiudadAct+"</pre>', pero tiene '<pre>"+xCiudadAct+"</pre>'"; 
				
		   	pOscarAct = pActores.get(y).hasOscar();
		   	xOscarAct = pActores.get(y).hasOscar();
			
		   	if (pOscarAct != xOscarAct) 
					return usuario+"+"+langActual+": el actor número "+y+" debería tener óscar '"+pOscarAct+"', pero tiene '"+xOscarAct+"'"; 
		}
		
		return "OK";
	}
    
	
	
	
	
	// pide y devuelve la lista de países con películas de un actor en un idioma
	// levanta excepciones si algo va mal
	
	private static ArrayList<Pais> requestPaisesActor (String usuario, String url, String lang, String act, String passwd) 
									throws ExcepcionSINT  
	{
		Document doc;
		String call, qs;
		ArrayList<Pais> listaPaises = new ArrayList<Pais>();
		
		try {
		   qs = "?auto=si&"+PFASE+"=23&"+PLANG+"="+lang+"&"+PACTOR+"="+URLEncoder.encode(act, "utf-8")+"&p="+passwd;
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
			
			throw new ExcepcionSINT("<br>SAXException al solicitar y parsear la lista de países:<br> "+usuario+"+"+lang+"+"+act+" --> "+ex) ;
		}
		catch (Exception ex) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			throw new ExcepcionSINT("<br>Exception al solicitar y parsear la lista de países:<br> "+usuario+"+"+lang+"+"+act+" --> "+ex);
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
			
			throw new ExcepcionSINT("La lista de países es inválida, tiene errors: "+usuario+"+"+lang+"+"+act+" --> "+msg);
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
			
			throw new ExcepcionSINT("La lista de países es inválida, tiene fatal errors: "+usuario+"+"+lang+"+"+act+" --> "+msg); 
		}
		
		
		if (doc == null) {
			throw new ExcepcionSINT("Se recibe 'null' al solicitar y parsear la lista de países: "+usuario+"+"+lang+"+"+act);
		}
		

		NodeList nlPaises = doc.getElementsByTagName("pais");

		// procesamos todos los países

		for (int x=0; x < nlPaises.getLength(); x++) {
			Element elemPais = (Element)nlPaises.item(x);
			String nombre = elemPais.getTextContent().trim();
			String langPais = elemPais.getAttribute("lang");
			String numPais = elemPais.getAttribute("num");
			
			listaPaises.add(new Pais(nombre,langPais,Integer.valueOf(numPais)));
		}
		
		return listaPaises;
	}
	
	
	
	// para comparar el resultado de la F23: listas de países
	
	private static String comparaPaises (String usuario, String langActual, String actActual, ArrayList<Pais> pPaises, ArrayList<Pais> xPaises) 
	{	
	    if (pPaises.size() != xPaises.size()) 
		return usuario+"+"+langActual+"+"+actActual+": debería devolver '"+pPaises.size()+"' países, pero devuelve '"+xPaises.size()+"'"; 
	
	    
	    for (int z=0; z < pPaises.size(); z++) {
	    	    if (!xPaises.get(z).getNombre().equals(pPaises.get(z).getNombre()))
			    return usuario+"+"+langActual+"+"+actActual+": el país número "+z+" debería ser '<pre>"+pPaises.get(z).getNombre()+"</pre>', pero es '<pre>"+xPaises.get(z).getNombre()+"</pre>'"; 
	    	
	    	    if (!xPaises.get(z).getLang().equals(pPaises.get(z).getLang()))
			    return usuario+"+"+langActual+"+"+actActual+": el idioma del país número "+z+" debería ser '<pre>"+pPaises.get(z).getLang()+"</pre>', pero es '<pre>"+xPaises.get(z).getLang()+"</pre>'"; 
	    	    
	    	    if (xPaises.get(z).getNumPeliculas() != pPaises.get(z).getNumPeliculas())
			    return usuario+"+"+langActual+"+"+actActual+": el número de películas del país número "+z+" debería ser '<pre>"+pPaises.get(z).getNumPeliculas()+"</pre>', pero es '<pre>"+xPaises.get(z).getNumPeliculas()+"</pre>'"; 
	    }
	    
	    return "OK";  
	}
	
	
	
	
	// pide y devuelve la lista de películas de un pais de un actor de un idioma
	// levanta excepciones si algo va mal
	
	private static ArrayList<Pelicula> requestPeliculasPais  (String usuario, String url, String lang, String act, String pais, String passwd) 
										throws ExcepcionSINT  
	{
		Document doc;
		String call, qs;
		
		ArrayList<Pelicula> listaPeliculas = new ArrayList<Pelicula>();
		
		try {
			qs = "?auto=si&"+PFASE+"=24&"+PLANG+"="+lang+"&"+PACTOR+"="+URLEncoder.encode(act,"utf-8")+"&"+PPAIS+"="+URLEncoder.encode(pais, "utf-8")+"&p="+passwd;
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
			
			throw new ExcepcionSINT("<br>SAXException al solicitar y parsear las películas:<br> "+usuario+"+"+lang+"+"+act+"+"+pais+" --> "+ex) ;
		}
		catch (Exception ex) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			throw new ExcepcionSINT("<br>Exception al solicitar y parsear las películas:<br> "+usuario+"+"+lang+"+"+act+"+"+pais+" --> "+ex) ;
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
			
			throw new ExcepcionSINT("La lista de películas es inválida, tiene errors: "+usuario+"+"+lang+"+"+act+"+"+pais+" --> "+msg);
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
			
			throw new ExcepcionSINT("La lista d epelículas es inválida, tiene fatal errors: "+usuario+"+"+lang+"+"+act+"+"+pais+" --> "+msg); 
		}
	
		
		if (doc == null) {
			throw new ExcepcionSINT("Se recibe 'null' al solicitar y parsear la lista de películas: "+usuario+"+"+lang+"+"+act+"+"+pais);
		}
		
		
		NodeList nlTitulos = doc.getElementsByTagName("titulo");

		// procesamos todas las películas

		for (int x=0; x < nlTitulos.getLength(); x++) {
			Element elemTitulo = (Element)nlTitulos.item(x);
			String titulo = elemTitulo.getTextContent().trim();
			String ip = elemTitulo.getAttribute("ip");
				
			listaPeliculas.add(new Pelicula(titulo, ip));
		}
		
		return listaPeliculas;
	}
	
	
	// para comparar el resultado de la F24: películas
	
	private static String comparaPeliculas (String usuario, String langActual, String actActual, String paisActual, ArrayList<Pelicula> pPeliculas, ArrayList<Pelicula> xPeliculas) 
	{		
	    if (pPeliculas.size() != xPeliculas.size()) 
			return usuario+"+"+langActual+"+"+actActual+"+"+paisActual+": debería devolver '"+pPeliculas.size()+"' films, pero devuelve '"+xPeliculas.size()+"'"; 
	    		
	        Pelicula pPel, xPel;
	        
	        for (int t=0; t < pPeliculas.size(); t++) {
	        		pPel = pPeliculas.get(t);
		        	xPel = xPeliculas.get(t);
		        	
		        	if (!xPel.getTitulo().equals(pPel.getTitulo())) 
		    			return usuario+"+"+langActual+"+"+actActual+"+"+paisActual+": la película número "+(t+1)+" debería ser '<pre>"+pPel.getTitulo()+"</pre>', pero es '<pre>"+xPel.getTitulo()+"</pre>'"; 
		        	
		        	if (!xPel.getIP().equals(pPel.getIP())) 
		    			return usuario+"+"+langActual+"+"+actActual+"+"+paisActual+": la película número "+(t+1)+" debería tener ip '<pre>"+pPel.getIP()+"</pre>', pero tiene '<pre>"+xPel.getIP()+"</pre>'"; 
	        	
	        }  // for t
	
	    return "OK";
	}
	
		
	// pide  la lista de películas de un pais de un actor de un idioma, pero dejando de enviar algún parámetro
	// comprueba que recibe del usuario las correspondientes notificaciones de error
	// levanta excepciones si algo va mal
	
	private static void checkLackParam (String usuario, String url, String lang, String act, String pais, String passwd) 
						throws ExcepcionSINT  
	{
		Document doc;
		Element e;
		String tagName, reason, call, qs, qs1, qs2, qs3;
		
		try {
			qs = "?auto=si&"+PFASE+"=24&p="+passwd;
			qs1 = qs+"&"+PACTOR+"="+URLEncoder.encode(act,"utf-8")+"&"+PPAIS+"="+URLEncoder.encode(pais, "utf-8");  	// falta PLANG
			qs2 = qs+"&"+PLANG+"="+lang+"&"+PPAIS+"="+URLEncoder.encode(pais, "utf-8");								// falta PACTOR
			qs3 = qs+"&"+PLANG+"="+lang+"&"+PACTOR+"="+URLEncoder.encode(act,"utf-8");								// falta PPAIS
		}
		catch (UnsupportedEncodingException ex) {throw new ExcepcionSINT("utf-8 no soportado");}
		
		
		// probando qs1, donde falta PLANG
	
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
			
			throw new ExcepcionSINT("<br>SAXException al solicitar y parsear las películas sin '"+PLANG+"':<br> "+usuario+" --> "+ex) ;
		}
		catch (Exception ex) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			throw new ExcepcionSINT("<br>Exception al solicitar y parsear las películas sin '"+PLANG+"':<br> "+usuario+" --> "+ex) ;
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
			
			throw new ExcepcionSINT("La respuesta (al solicitar y parsear las películas sin '"+PLANG+"') es inválida, tiene errors: "+usuario+" --> "+msg);
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
			
			throw new ExcepcionSINT("La respuesta (al solicitar y parsear las películas sin '"+PLANG+"') es inválida, tiene fatal errors: "+usuario+" --> "+msg); 
		}
			
		if (doc == null) {
			throw new ExcepcionSINT("Se recibe 'null' al solicitar las películas sin '"+PLANG+"': "+usuario);
		}
		
		e = doc.getDocumentElement();
		tagName = e.getTagName();
		if (tagName.equals("wrongRequest")) {
			reason = e.getTextContent();
			if (!reason.equals("no param:"+PLANG))
			throw new ExcepcionSINT("Se recibe <wrongRequest> pero sin 'no param:"+PLANG+"' al solicitar las películas sin '"+PLANG+"': "+usuario+"+"+reason);
		}
		else throw new ExcepcionSINT("No se recibe 'wrongRequest' al solicitar las películas sin '"+PLANG+"': "+usuario);
		

		
		// probando qs2, donde falta PACTOR
		
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
			
			throw new ExcepcionSINT("<br>SAXException al solicitar y parsear las películas sin '"+PACTOR+"':<br> "+usuario+" --> "+ex) ;
		}
		catch (Exception ex) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			throw new ExcepcionSINT("<br>Exception al solicitar y parsear las películas sin '"+PACTOR+"':<br> "+usuario+" --> "+ex) ;
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
			
			throw new ExcepcionSINT("La respuesta (al solicitar y parsear las películas sin '"+PACTOR+"') es inválida, tiene errors: "+usuario+" --> "+msg);
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
			
			throw new ExcepcionSINT("La respuesta (al solicitar y parsear las películas sin '"+PACTOR+"') es inválida, tiene fatal errors: "+usuario+" --> "+msg); 
		}
			
		if (doc == null) {
			throw new ExcepcionSINT("Se recibe 'null' al solicitar las películas sin '"+PACTOR+"': "+usuario);
		}
		
		e = doc.getDocumentElement();
		tagName = e.getTagName();
		if (tagName.equals("wrongRequest")) {
			reason = e.getTextContent();
			if (!reason.equals("no param:"+PACTOR))
			throw new ExcepcionSINT("Se recibe <wrongRequest> pero sin 'no param:"+PACTOR+"' al solicitar las películas sin '"+PACTOR+"': "+usuario+"+"+reason);
		}
		else throw new ExcepcionSINT("No se recibe 'wrongRequest' al solicitar las películas sin '"+PACTOR+"': "+usuario);
		
		
	// probando qs3, donde falta PPAIS
		
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
			
			throw new ExcepcionSINT("<br>SAXException al solicitar y parsear las películas sin '"+PPAIS+"':<br> "+usuario+" --> "+ex) ;
		}
		catch (Exception ex) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			throw new ExcepcionSINT("<br>Exception al solicitar y parsear las películas sin '"+PPAIS+"':<br> "+usuario+" --> "+ex) ;
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
			
			throw new ExcepcionSINT("La respuesta (al solicitar y parsear las películas sin '"+PPAIS+"') es inválida, tiene errors: "+usuario+" --> "+msg);
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
			
			throw new ExcepcionSINT("La respuesta (al solicitar y parsear las películas sin '"+PPAIS+"') es inválida, tiene fatal errors: "+usuario+" --> "+msg); 
		}
			
		if (doc == null) {
			throw new ExcepcionSINT("Se recibe 'null' al solicitar las películas sin '"+PPAIS+"': "+usuario);
		}
		
		e = doc.getDocumentElement();
		tagName = e.getTagName();
		if (tagName.equals("wrongRequest")) {
			reason = e.getTextContent();
			if (!reason.equals("no param:"+PPAIS))
			throw new ExcepcionSINT("Se recibe <wrongRequest> pero sin 'no param:"+PPAIS+"' al solicitar las películas sin '"+PPAIS+"': "+usuario+"+"+reason);
		}
		else throw new ExcepcionSINT("No se recibe 'wrongRequest' al solicitar las películas sin '"+PPAIS+"': "+usuario);
		
		// todo bien
		return;
	}

}
