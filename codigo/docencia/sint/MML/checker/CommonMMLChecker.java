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


// variables y métodos comunes a toda la aplicación


package docencia.sint.MML.checker;

import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.UnavailableException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NodeList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.SAXException;

import docencia.sint.Common.CommonSINT;
import docencia.sint.Common.ErrorHandlerSINT;
import docencia.sint.Common.ExcepcionSINT;
import docencia.sint.MML.CommonMML;


class CommonMMLChecker {

	public static ServletContext servletContextSintProf;
	
	public static int esProfesor=0;
	
	public static final String MSG_TITLE = "Checker de MML"; 
	public static final String CHECKER_NAME  = "MML Checker";
	public static final String SERVICE_NAME = "/P2M";
	public static final String PROF_CONTEXT  = "/sintprof";
	
	public static String server_port;
	public static String servicioProf;
	
	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
	
	static final String SCHEMA_MMLAUTO = "_rules/mmlAuto.xsd";

	public static DocumentBuilderFactory dbf;
	public static DocumentBuilder db;
	public static ErrorHandlerSINT errorHandler;

	
	private static Logger logger = null;  // el objeto Logger
	
	// para inicializar el objeto Logger
	
	public static void initLoggerMMLChecker (Class c) {
		logger = LogManager.getLogger(c);
	}
	
	 // para imprimir con el Logger en el sintprof.log
	 
	 public static void logMMLChecker (String msg) {
		logger.info("## IML Checker ## "+msg);
	 }
	 
	
	public static void printHead(PrintWriter out) 
	{
		out.println("<head><meta charset='utf-8'/>");
		out.println("<title>"+CommonMMLChecker.MSG_TITLE+"</title>");
		out.println("<link rel='stylesheet'  type='text/css' href='css/mmlChecker.css'/></head>");
	}
	
	
	public static void printBodyHeader(PrintWriter out) 
	{
		out.println("<body>");
		out.println("<div id='asignatura'>Servicios de Internet </div><br>");
		out.println("<div id='grado'>EE Telecomunicación (Universidad de Vigo) </div><br>");
		out.println("<div id='servicio'>Comprobación de servicios sobre MML <br> Curso "+CommonMML.CURSO+"<hr></div>");

	}

	
	
	// para crear el DocumentBuilder, se invoca desde el init del servlet
	
	public static void createDocumentBuilder() throws  UnavailableException 
	{	
	    dbf = DocumentBuilderFactory.newInstance();
	    dbf.setValidating(true);
	    dbf.setNamespaceAware(true);
	    dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
	    
	    String pathSchema = CommonMMLChecker.servletContextSintProf.getRealPath(SCHEMA_MMLAUTO);
	    File fileSchema = new File(pathSchema);
		
	    dbf.setAttribute(JAXP_SCHEMA_SOURCE, fileSchema);    // se valida con el schema del modo auto

	    try {
		    db = dbf.newDocumentBuilder();
	    }
	    catch  (ParserConfigurationException e) {
		    throw new UnavailableException("Error creando el analizador de respuestas del modo auto: "+e);
	    }

	    errorHandler = new ErrorHandlerSINT();
	    db.setErrorHandler(CommonMMLChecker.errorHandler);
	}
	
	
	
	
	// para comprobar las listas de errores de profesor y alumno (sólo se compara los nombres de los ficheros, no la explicación del error)
	// devuelve OK si son iguales, o un mensaje de error en caso contrario
	
	public static String comparaErrores (String usuario, String servicioAluP, String passwdAlu) 
	{		
		// pedimos la lista de errores de sintprof
		
		Element pErrores;
		try {
			pErrores = requestErrores("sintprof", CommonMMLChecker.servicioProf, CommonSINT.PASSWD);
		}
		catch (ExcepcionSINT ex) { return "<br>ExcepcionSINT solicitando el informe de errores de sintprof:<br>"+ex.toString(); }
	
	
		// pedimos la lista de errores del sintX
		
		Element xErrores;
		try {
			xErrores = requestErrores(usuario, servicioAluP, passwdAlu);
		}
		catch (ExcepcionSINT ex) { return "<br>ExcepcionSINT solicitando el informe de errores:<br>"+ex.toString(); }
		
		
		ArrayList<String> pList = new ArrayList<String>();  // para crear la lista de ficheros del profesor
		ArrayList<String> xList = new ArrayList<String>();  // para crear la lista de ficheros del alumno
		
		
		// vamos a comparar los ficheros que dan warning
		
		NodeList pnlWarnings = pErrores.getElementsByTagName("warning");
		NodeList xnlWarnings = xErrores.getElementsByTagName("warning");

		// extraemos los warnings del profesor
		
		for (int x=0; x < pnlWarnings.getLength(); x++) {
			Element pelemWarning = (Element)pnlWarnings.item(x);

			// cambiar las siguientes tres líneas y casos sucesivos por   
			// String pnombreWarningFile = getFirstStringElementByTagName(pelemWarning, "file")
			
			NodeList pnlWarningFiles = pelemWarning.getElementsByTagName("file");
			Element pelemWarningFile = (Element)pnlWarningFiles.item(0);
			String pnombreWarningFile = pelemWarningFile.getTextContent().trim();
			
			// vamos a comparar sólo el nombre, no el path completo
			
			int pos = pnombreWarningFile.lastIndexOf('/');
			if (pos != -1) pnombreWarningFile = pnombreWarningFile.substring(pos+1);
			
			pList.add(pnombreWarningFile);
		}
		
		// extraemos los warnings del alumno
		
		for (int x=0; x < xnlWarnings.getLength(); x++) {
			Element xelemWarning = (Element)xnlWarnings.item(x);

			NodeList xnlWarningFiles = xelemWarning.getElementsByTagName("file");
			Element xelemWarningFile = (Element)xnlWarningFiles.item(0);
			String xnombreWarningFile = xelemWarningFile.getTextContent().trim();
			
			int pos = xnombreWarningFile.lastIndexOf('/');
			if (pos != -1) xnombreWarningFile = xnombreWarningFile.substring(pos+1);
			
			xList.add(xnombreWarningFile);
		}

		// comprobamos que las listas sean de igual tamaño
		
		if (pList.size() != xList.size())
			return "La lista de warnings tiene "+xList.size()+" elementos, pero debería tener "+pList.size();
		
		// comprobamos que todos los ficheros del alumno están en la lista del profesor
		
		for (int x=0; x < xList.size(); x++) 
			if (!pList.contains(xList.get(x)))
				return "El warning número "+x+" ("+xList.get(x)+") no es correcto";
		
		
		
		// vamos a comparar los ficheros que dan error
		
		pList.clear();
		xList.clear();
		NodeList pnlErrors = pErrores.getElementsByTagName("error");
		NodeList xnlErrors = xErrores.getElementsByTagName("error");

		// extraemos los errors del profesor
		
		for (int x=0; x < pnlErrors.getLength(); x++) {
			Element pelemError = (Element)pnlErrors.item(x);

			NodeList pnlErrorFiles = pelemError.getElementsByTagName("file");
			Element pelemErrorFile = (Element)pnlErrorFiles.item(0);
			String pnombreErrorFile = pelemErrorFile.getTextContent().trim();
			
			int pos = pnombreErrorFile.lastIndexOf('/');
			if (pos != -1) pnombreErrorFile = pnombreErrorFile.substring(pos+1);
			
			pList.add(pnombreErrorFile);
		}
		
		// extraemos los errors del alumno
		
		for (int x=0; x < xnlErrors.getLength(); x++) {
			Element xelemError = (Element)xnlErrors.item(x);

			NodeList xnlErrorFiles = xelemError.getElementsByTagName("file");
			Element xelemErrorFile = (Element)xnlErrorFiles.item(0);
			String xnombreErrorFile = xelemErrorFile.getTextContent().trim();
			
			int pos = xnombreErrorFile.lastIndexOf('/');
			if (pos != -1) xnombreErrorFile = xnombreErrorFile.substring(pos+1);
			
			xList.add(xnombreErrorFile);
		}

		// comprobamos que las listas sean de igual tamaño
		
		if (pList.size() != xList.size())
			return "La lista de errors tiene "+xList.size()+" elementos, pero debería tener "+pList.size();
		
		// comprobamos que todos los ficheros del alumno están en la lista del profesor
		
		for (int x=0; x < xList.size(); x++) 
			if (!pList.contains(xList.get(x)))
				return "El error número "+x+" ("+xList.get(x)+") no es correcto";
		
		
		// vamos a comparar los ficheros que dan fatalerror
		
		pList.clear();
		xList.clear();
		NodeList pnlFatalErrors = pErrores.getElementsByTagName("fatalerror");
		NodeList xnlFatalErrors = xErrores.getElementsByTagName("fatalerror");

		// extraemos los fatalerrors del profesor
		
		for (int x=0; x < pnlFatalErrors.getLength(); x++) {
			Element pelemFatalError = (Element)pnlFatalErrors.item(x);

			NodeList pnlFatalErrorFiles = pelemFatalError.getElementsByTagName("file");
			Element pelemFatalErrorFile = (Element)pnlFatalErrorFiles.item(0);
			String pnombreFatalErrorFile = pelemFatalErrorFile.getTextContent().trim();
			
			int pos = pnombreFatalErrorFile.lastIndexOf('/');
			if (pos != -1) pnombreFatalErrorFile = pnombreFatalErrorFile.substring(pos+1);
			
			pList.add(pnombreFatalErrorFile);
		}
		
		// extraemos los fatalerrors del alumno
		
		for (int x=0; x < xnlFatalErrors.getLength(); x++) {
			Element xelemFatalError = (Element)xnlFatalErrors.item(x);

			NodeList xnlFatalErrorFiles = xelemFatalError.getElementsByTagName("file");
			Element xelemFatalErrorFile = (Element)xnlFatalErrorFiles.item(0);
			String xnombreFatalErrorFile = xelemFatalErrorFile.getTextContent().trim();
			
			int pos = xnombreFatalErrorFile.lastIndexOf('/');
			if (pos != -1) xnombreFatalErrorFile = xnombreFatalErrorFile.substring(pos+1);
			
			xList.add(xnombreFatalErrorFile);
		}

		// comprobamos que las listas sean de igual tamaño
		
		if (pList.size() != xList.size())
			return "La lista de fatal errors tiene "+xList.size()+" elementos, pero debería tener "+pList.size();
		
		// comprobamos que todos los ficheros del alumno están en la lista del profesor
		
		for (int x=0; x < xList.size(); x++) 
			if (!pList.contains(xList.get(x)))
				return "El fatal error número "+x+" ("+xList.get(x)+") no es correcto";
		
			
		
		return "OK";  // todo fue igual
	}
	
	
	
	
	// pide y devuelve la lista de errores detectados de un usuario
	
	 public static Element requestErrores (String usuario, String url, String passwd) 
			 		throws ExcepcionSINT  
	 {
		Document doc;
		String qs, call;
		
		qs = "?pfase=02&auto=si&p="+passwd;
		call = url+qs;
		
		errorHandler.clear();

		try {
			doc = db.parse(call);
		}
		catch (SAXException ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}

			throw new ExcepcionSINT("<br>SAXException al solicitar y parsear la lista de errores: <br>"+usuario+" --> "+ex) ;
		}
		catch (Exception ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			throw new ExcepcionSINT("<br>Exception al solicitar y parsear la lista de errores:<br> "+usuario+" --> "+ex);
		}

		if (errorHandler.hasErrors()) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			ArrayList<String> errors = errorHandler.getErrors();
			String msg="";
			
			for (int x=0; x < errors.size(); x++) {
				msg += "++++"+errors.get(x);
			}
			
			throw new ExcepcionSINT("<br>La respuesta con la lista de errores es inválida, se generan 'errors' al parsearla: "+usuario+" --> "+msg);
		}
		
		if (errorHandler.hasFatalerrors()) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			ArrayList<String> fatalerrors = errorHandler.getFatalerrors();
			String msg="";
			
			for (int x=0; x < fatalerrors.size(); x++) {
				msg += "++++"+fatalerrors.get(x);
			}
			
			throw new ExcepcionSINT("<br>La respuesta con la lista de errores es inválida, se generan 'fatal errors' al parsearla: "+usuario+" --> "+msg);
		}
		
		
		if (doc == null) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			throw new ExcepcionSINT("<br>Se recibe 'null' al solicitar y parsear la lista de errores: "+usuario);
		}
		
		Element e = doc.getDocumentElement();
		String tagName = e.getTagName();
		if (tagName.equals("wrongRequest")) {
			String reason = e.getTextContent();
			throw new ExcepcionSINT("<br>Se recibe <wrongRequest> al solicitar la lista de errores: "+reason);
		}
		
		if (!tagName.equals("errores")) {
			CommonMMLChecker.logMMLChecker("Elemento resultado = "+tagName);
			throw new ExcepcionSINT("<br>Llamada: "+call+"<br>Tipo de respuesta incorrecta (&lt;"+tagName+">) al solicitar la lista de errores");
		}

		NodeList nlErrores = doc.getElementsByTagName("errores");
		Element elemErrores = (Element)nlErrores.item(0);
		
		return elemErrores;
	}
	
	
	
	
	
	// método para comprobar si un servicio está operativo, pidiendo el estado
	// devuelve OK si está operativo, un código de error si falla algo

	public static String doOneCheckUpStatus (HttpServletRequest request, String user, String passwd)   
	{
		Document doc;
		Element e;
		String qs;
		
		ServletContext scPropio, scUser;
		scPropio = request.getServletContext();  // contexto del que ejecuta el checker (sintprof)
		scUser = scPropio.getContext("/"+user);  // contexto del User que vamos a comprobar

		CommonMMLChecker.logMMLChecker("Vamos a comprobar el estado de "+user);

		if (scUser == null)  {
			CommonMMLChecker.logMMLChecker("01_NOCONTEXT: null "+user);
			return "01_NOCONTEXT,No existe el contexto "+user;
		}

		String cpUser = scUser.getContextPath();        // context path del User que vamos a comprobar

		if (cpUser.equals("")) {
			CommonMMLChecker.logMMLChecker("01_NOCONTEXT:"+cpUser+"**"+user);
			return "01_NOCONTEXT,No existe el contexto "+user;
		}


		// vamos a pedir el estado sin passwd, para comprobar que responde con error
		
		String url = "http://"+server_port+cpUser+CommonMMLChecker.SERVICE_NAME;
		String call;
		
		errorHandler.clear();

		qs = "?auto=si";
		call = url+qs;
		
		try {
			doc = db.parse(call);  // petición del estado sin passwd
		}
		catch (ConnectException ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			return "00_DOWN,(ConnectException) El servidor no responde:<br> "+ex;    // No debería pasar, ya que es el mismo servidor que está ejecutando el checker
		}
		catch (FileNotFoundException ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			return "02_FILENOTFOUND,(FileNotFoundException) Ese servlet no está declarado:<br> "+ex;    // el servlet no está declarado en el web.xml
		}
		catch (com.sun.org.apache.xerces.internal.impl.io.MalformedByteSequenceException ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			return "03_ENCODING,(MalformedByteSequenceException) La codificación de caracteres recibida es incorrecta (no UTF-8):<br> "+ex;
		}
		catch (IOException ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			return "04_IOEXCEPTION,(IOException) No se ha encontrado la clase del servlet o ésta devolvió una excepción al pedir el estado sin passwd: <br>"+ex;
		}
		catch (SAXException ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			return "05_BF,(SAXException) La respuesta al pedir el estado sin passwd ("+call+") está mal construida: <br>"+ex;
		}
		catch (Exception ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			return "07_ERRORUNKNOWN,(Exception) Error desconocido al realizar la solicitud ("+call+") <br> "+ex; 
		}
		
		if (errorHandler.hasErrors()) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			ArrayList<String> errors = errorHandler.getErrors();
			String msg="";
			
			for (int x=0; x < errors.size(); x++) {
				msg += "++++"+errors.get(x);
			}
			
			CommonMMLChecker.logMMLChecker(msg);
			return "06_INVALID,La respuesta al pedir el estado sin passwd ("+call+") es inválida, tiene errors"+" --> "+msg;   
		}
		
		if (errorHandler.hasFatalerrors()) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			ArrayList<String> fatalerrors = errorHandler.getFatalerrors();
			String msg="";
			
			for (int x=0; x < fatalerrors.size(); x++) {
				msg += "++++"+fatalerrors.get(x);
			}
			
			CommonMMLChecker.logMMLChecker(msg);
			return "06_INVALID,La respuesta al pedir el estado sin passwd ("+call+") es inválida, tiene fatal errors"+" --> "+msg;   
		}
		
		
		e = doc.getDocumentElement();
		String tagName = e.getTagName();
		
		if (tagName.equals("service")) 
			return "08_OKNOPASSWD,No ha requerido passwd";   
		
		if (tagName.equals("wrongRequest")) {
			String reason = e.getTextContent().trim().toLowerCase();
			if (!reason.equals("no passwd"))
				return "10_BADANSWER,Responde con &lt;wrongRequest> pero no por 'no passwd', sino por: "+reason;   
		}
		else return "10_BADANSWER,No ha contestado con &lt;wrongRequest> al no enviar passwd, sino con &lt;"+tagName+">";   
		
		
		// vamos ahora a pedir el estado incluyendo la passwd
		
		errorHandler.clear();

		qs = "?auto=si&p="+passwd;
		call = url+qs;
		
		try {
			doc = db.parse(call);  // petición del estado
		}
		catch (ConnectException ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			return "00_DOWN,(ConnectException) El servidor no responde "+ex;    // No debería pasar, ya que es el mismo servidor que está ejecutando el checker
		}
		catch (FileNotFoundException ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			return "02_FILENOTFOUND,(FileNotFoundException) Ese servlet no está declarado: <br>"+ex;    // el servlet no está declarado en el web.xml
		}
		catch (com.sun.org.apache.xerces.internal.impl.io.MalformedByteSequenceException ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			return "03_ENCODING,(MalformedByteSequenceException) La codificación de caracteres recibida es incorrecta (no UTF-8):<br> "+ex;   // la secuencia de bytes recibida UTF-8 está malformada
		}
		catch (IOException ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			return "04_IOEXCEPTION,(IOException) No se ha encontrado la clase del servlet o ésta devolvió una excepción al pedir el estado: <br>"+ex; 
		}
		catch (SAXException ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			return "05_BF,(SAXException) La respuesta al pedir el estado ("+call+") está mal construida: <br>"+ex;   
		}
		catch (Exception ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			return "07_ERRORUNKNOWN,(Exception) Error desconocido al realizar la solicitud ("+call+"):<br> "+ex;   
		}

		
		if (errorHandler.hasErrors()) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			ArrayList<String> errors = errorHandler.getErrors();
			String msg="";
			
			for (int x=0; x < errors.size(); x++) {
				msg += "++++"+errors.get(x);
			}
			
			CommonMMLChecker.logMMLChecker(msg);
			return "06_INVALID,La respuesta al pedir el estado con passwd ("+call+") es inválida, tiene errors"+" --> "+msg;   
		}
		
		if (errorHandler.hasFatalerrors()) {
			try {
			     String urlContents = CommonSINT.getURLContents(call);
			     CommonMMLChecker.logMMLChecker(urlContents);
			}
			catch (ExcepcionSINT es) {CommonMMLChecker.logMMLChecker(es.toString());}
			
			ArrayList<String> fatalerrors = errorHandler.getFatalerrors();
			String msg="";
			
			for (int x=0; x < fatalerrors.size(); x++) {
				msg += "++++"+fatalerrors.get(x);
			}
			
			CommonMMLChecker.logMMLChecker(msg);
			return "06_INVALID,La respuesta al pedir el estado con passwd ("+call+") es inválida, tiene fatal errors"+" --> "+msg;   
		}
		
		
		e = doc.getDocumentElement();
		tagName = e.getTagName();
		
		if (tagName.equals("service")) 
			return "OK,Servicio operativo";
	
		if (tagName.equals("wrongRequest")) {
			String reason = e.getTextContent().trim();
			if (reason.equals("bad passwd")) {
				return "09_BADPASSWD,La passwd enviada es incorrecta";
			}
			else return "10_BADANSWER,Se ha contestado con &lt;wrongRequest> con razón desconocida ("+reason+") al pedir el estado ("+call+"): <br>";   
		}
		
		return "10_BADANSWER,Se ha contestado con un tag inadecuado (&lt;"+tagName+">) al pedir el estado ("+call+")";   
	}
		
		
		
		
		
		
	// Envío del fichero de un resultado negativo
	// el nombre del fichero se recibe en el parámetro 'file'

	public static void doGetRequestResultFile(HttpServletRequest request, PrintWriter out)
						throws IOException, ServletException
	{
		File file;
		BufferedReader br;
		String linea;

		out.println("<html>");
		CommonMMLChecker.printHead(out);
		CommonMMLChecker.printBodyHeader(out);

		String resultFile = request.getParameter("file");
		if (resultFile == null) {
			out.println("<h4>Error: no se ha recibido el fichero solicitado</h4>");
		}
		else {
			file = new File(resultFile);
			br = new BufferedReader(new FileReader(file));

			while ((linea = br.readLine()) != null) {
				out.println(linea+"<BR>");
			}

			br.close();
		}

		out.println("<form>");
		out.println("<p><input type='hidden' name='p' value='si'>");	
		out.println("<input class='home' type='submit' value='Inicio'>");
		out.println("</form>");
		
		CommonSINT.printFoot(out, CommonMML.CREATED);
		
		out.println("</body></html>");
	}
		
		
	// para obtener la passwd de un alumno que está en el server.xml
	
	public static String getAluPasswd (String alu) throws ExcepcionSINT 
	{	
		ServletContext scAlu;
	
		scAlu = CommonMMLChecker.servletContextSintProf.getContext("/"+alu);  

		if (scAlu == null)  
			throw new ExcepcionSINT("NOCONTEXT");

		String cpUser = scAlu.getContextPath();   

		if (cpUser.equals("")) 
			throw new ExcepcionSINT("NOCONTEXT");
		
        String passwdAlu = scAlu.getInitParameter("passwd");
        if (passwdAlu == null)
        	throw new ExcepcionSINT("NOPASSWD");
        
        if (passwdAlu.equals(""))
        	throw new ExcepcionSINT("NOPASSWD");
        
        return passwdAlu;
	}
		
	
		

}
