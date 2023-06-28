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

// en esta clase van variables y métodos usados por toda la aplicación

package docencia.sint.MML2021.checker;

import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.http.HttpServletResponse;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.SAXException;

import docencia.sint.Common.MsCC;
import docencia.sint.Common.CheckerFailure;
import docencia.sint.Common.CommonSINT;
import docencia.sint.Common.SearchInFile;
import docencia.sint.Common.ErrorHandlerSINT;
import docencia.sint.Common.ExcepcionChecker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


class CommonMMLChecker {

	public static ServletContext servletContextSintProf;

	public static int esProfesor=0;

	public static final String SERVICE_NAME = "/P2M";
	public static final String PROF_CONTEXT  = "/sintprof";

	public static String server_port;
	public static String servicioProf;

	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

	static final String SCHEMA_MMLAUTO = "_rules/mml2021Auto.xsd";
	static final String CSS_FILE = "css/mmlChecker.css";

	public static DocumentBuilderFactory dbf;
	public static DocumentBuilder db;
	public static ErrorHandlerSINT errorHandler;

	
	private static Logger logger = null;  // el objeto Logger

	// para inicializar el objeto Logger, que imprimirá en el fichero "sintprof.log", que se crea donde arranca el tomcat
	// ese nombre se especifica en el fichero de configuración log4j2.xml
	// log4j2.xml se busca en el classpath, yo lo tengo en "classes"

	public static void initLoggerMMLChecker (Class<P2MChecker> c) {
		logger = LogManager.getLogger(c);
	}

	 // para imprimir con el Logger en el fichero "sintprof.log"

	 public static void logMMLChecker (String msg) {
		logger.info("## MML Checker ## "+msg);
	 }



	public static void printHead(PrintWriter out, String cLang)
	{
		out.println("<head><meta charset='utf-8'/>");
		out.println("<title>"+MsMMLCh.getMsg(MsMMLCh.CMML00, cLang)+"</title>");  // 0 = "Corrector de MML"
		out.println("<link rel='stylesheet'  type='text/css' href='"+CommonMMLChecker.CSS_FILE+"'/></head>");
	}


	public static void printBodyHeader(PrintWriter out, String cLang)
	{
		out.println("<body>");
		out.println("<div id='asignatura'>"+MsCC.getMsg(MsCC.CC00, cLang)+"</div><br>");  // CC00 = Servicios de Internet
		out.println("<div id='grado'>"+MsCC.getMsg(MsCC.CC01, cLang)+"</div><br>");  // CC01 = EE Telecomunicación
		// 3 = "Comprobación de servicios sobre MML"        "4" = CURSO xx-yy
		out.println("<div id='servicio'>"+MsMMLCh.getMsg(MsMMLCh.CMML03, cLang)+"<br>"+MsMMLCh.getMsg(MsMMLCh.CMML04, cLang)+"<hr></div>");
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
	// no devuelve nada si son iguales
	// levanta una excepcion ExcepcionChecker en caso contrario

	public static void comparaErrores (String usuario, String servicioAluP, String passwdAlu, String cLang)
			throws ExcepcionChecker
	{
		CheckerFailure cf;
		String currentMethod = Thread.currentThread().getStackTrace()[1].getMethodName();

		// pedimos la lista de errores de sintprof

		Element pErrores;
		try {
			pErrores = requestErrores("sintprof", CommonMMLChecker.servicioProf, CommonSINT.PPWD, cLang);
		}
		catch (ExcepcionChecker ex) {
			cf = ex.getCheckerFailure();
			cf.addMotivo(currentMethod+": ExcepcionChecker: "+MsCC.getMsg(MsCC.CC44, cLang)+"sintprof");  // CC44 = Solicitando el informe de errores de
			throw new ExcepcionChecker(cf);
		}


		// pedimos la lista de errores del sintX

		Element xErrores;
		try {
			xErrores = requestErrores(usuario, servicioAluP, passwdAlu, cLang);
		}
		catch (ExcepcionChecker ex) {
			cf = ex.getCheckerFailure();
			cf.addMotivo(currentMethod+": ExcepcionChecker: "+MsCC.getMsg(MsCC.CC44, cLang)+usuario); // CC44 = Solicitando el informe de errores de
			throw new ExcepcionChecker(cf);
		}


		ArrayList<String> pList = new ArrayList<String>();  // para crear la lista de ficheros del profesor
		ArrayList<String> xList = new ArrayList<String>();  // para crear la lista de ficheros del alumno



		// vamos a comparar los ficheros que dan WARNING

		NodeList pnlWarnings = pErrores.getElementsByTagName("warning");
		NodeList xnlWarnings = xErrores.getElementsByTagName("warning");

		// extraemos los warnings del profesor

		for (int x=0; x < pnlWarnings.getLength(); x++) {
			Element pelemWarning = (Element)pnlWarnings.item(x);     // cogemos uno de los warnings

			String pnombreWarningFile = CommonSINT.getTextContentOfChild (pelemWarning, "file");  //cogemos el contenido de su elemento file

			// vamos a comparar sólo el nombre, no el path completo

			int pos = pnombreWarningFile.lastIndexOf('/');
			if (pos != -1) pnombreWarningFile = pnombreWarningFile.substring(pos+1);

			pList.add(pnombreWarningFile);
		}

		// extraemos los warnings del alumno

		for (int x=0; x < xnlWarnings.getLength(); x++) {
			Element xelemWarning = (Element)xnlWarnings.item(x);    // cogemos uno de los warnings

			String xnombreWarningFile = CommonSINT.getTextContentOfChild (xelemWarning, "file");  //cogemos el contenido de su elemento file

			int pos = xnombreWarningFile.lastIndexOf('/');
			if (pos != -1) xnombreWarningFile = xnombreWarningFile.substring(pos+1);

			xList.add(xnombreWarningFile);
		}

		// comprobamos que las listas sean de igual tamaño

		if (pList.size() != xList.size()) {
			cf = new CheckerFailure("", "", currentMethod+": "+String.format(MsCC.getMsg(MsCC.CC45, cLang), pList.size(), xList.size())); // CC45 = Deberían haberse recibido x warnings, pero se reciben y
			throw new ExcepcionChecker(cf);
		}

		// comprobamos que todos los ficheros del alumno están en la lista del profesor

		for (int x=0; x < xList.size(); x++)
			if (!pList.contains(xList.get(x))) {
				cf = new CheckerFailure("", "", currentMethod+": "+String.format(MsCC.getMsg(MsCC.CC46, cLang), x, xList.get(x)));  // CC46 = el warning x no es correcto. Fichero f
				throw new ExcepcionChecker(cf);
			}



		// vamos a comparar los ficheros que dan ERROR

		pList.clear();
		xList.clear();
		NodeList pnlErrors = pErrores.getElementsByTagName("error");
		NodeList xnlErrors = xErrores.getElementsByTagName("error");

		// extraemos los errors del profesor

		for (int x=0; x < pnlErrors.getLength(); x++) {
			Element pelemError = (Element)pnlErrors.item(x);   // cogemos uno de los errors

			String pnombreErrorFile = CommonSINT.getTextContentOfChild (pelemError, "file");  //cogemos el contenido de su elemento file

			int pos = pnombreErrorFile.lastIndexOf('/');
			if (pos != -1) pnombreErrorFile = pnombreErrorFile.substring(pos+1);

			pList.add(pnombreErrorFile);
		}

		// extraemos los errors del alumno

		for (int x=0; x < xnlErrors.getLength(); x++) {
			Element xelemError = (Element)xnlErrors.item(x);  // cogemos uno de los errors

			String xnombreErrorFile = CommonSINT.getTextContentOfChild (xelemError, "file");  //cogemos el contenido de su elemento file

			int pos = xnombreErrorFile.lastIndexOf('/');
			if (pos != -1) xnombreErrorFile = xnombreErrorFile.substring(pos+1);

			xList.add(xnombreErrorFile);
		}

		// comprobamos que las listas sean de igual tamaño

		String errorFalta = "";

		if (xList.size() < pList.size()) {

		    for (int x=0; x < pList.size(); x++)
		    	if (!xList.contains(pList.get(x)))
		    		errorFalta = pList.get(x);

			cf = new CheckerFailure("", "", currentMethod+": "+String.format(MsCC.getMsg(MsCC.CC47, cLang), errorFalta)); // CC47 = Falta un 'error': x
			throw new ExcepcionChecker(cf);
		}

		String errorSobra = "";

		if (pList.size() < xList.size()) {

		    for (int x=0; x < xList.size(); x++)
		    	if (!pList.contains(xList.get(x)))
		    		errorSobra = xList.get(x);

			cf = new CheckerFailure("", "", currentMethod+": "+String.format(MsCC.getMsg(MsCC.CC48, cLang), errorSobra)); // CC48 = Se recibe un 'error' incorrecto: x
			throw new ExcepcionChecker(cf);
		}

		// comprobamos que todos los ficheros del alumno están en la lista del profesor

		for (int x=0; x < xList.size(); x++)
			if (!pList.contains(xList.get(x))) {
				cf = new CheckerFailure("", "", currentMethod+": "+String.format(MsCC.getMsg(MsCC.CC48, cLang), xList.get(x))); // CC48 = Se recibe un 'error' incorrecto: x
				throw new ExcepcionChecker(cf);
			}

		

		// vamos a comparar los ficheros que dan FATAL ERROR

		pList.clear();
		xList.clear();
		NodeList pnlFatalErrors = pErrores.getElementsByTagName("fatalerror");
		NodeList xnlFatalErrors = xErrores.getElementsByTagName("fatalerror");

		// extraemos los fatalerrors del profesor

		for (int x=0; x < pnlFatalErrors.getLength(); x++) {
			Element pelemFatalError = (Element)pnlFatalErrors.item(x);   // cogemos uno de los fatalerrors

			String pnombreFatalErrorFile = CommonSINT.getTextContentOfChild (pelemFatalError, "file");  //cogemos el contenido de su elemento file

			int pos = pnombreFatalErrorFile.lastIndexOf('/');
			if (pos != -1) pnombreFatalErrorFile = pnombreFatalErrorFile.substring(pos+1);

			pList.add(pnombreFatalErrorFile);
		}

		// extraemos los fatalerrors del alumno

		for (int x=0; x < xnlFatalErrors.getLength(); x++) {
			Element xelemFatalError = (Element)xnlFatalErrors.item(x);  // cogemos uno de los fatalerrors

			String xnombreFatalErrorFile = CommonSINT.getTextContentOfChild (xelemFatalError, "file");  //cogemos el contenido de su elemento file

			int pos = xnombreFatalErrorFile.lastIndexOf('/');
			if (pos != -1) xnombreFatalErrorFile = xnombreFatalErrorFile.substring(pos+1);

			xList.add(xnombreFatalErrorFile);
		}

		// comprobamos que las listas sean de igual tamaño

		if (pList.size() != xList.size()) {

			cf = new CheckerFailure("", "", currentMethod+": "+String.format(MsCC.getMsg(MsCC.CC49, cLang), pList.size(), xList.size())); // CC49 = Deberían haberse recibido x fatal errors, pero se reciben y
			throw new ExcepcionChecker(cf);
		}

		// comprobamos que todos los ficheros del alumno están en la lista del profesor

		for (int x=0; x < xList.size(); x++)
			if (!pList.contains(xList.get(x))) {
				cf = new CheckerFailure("", "", currentMethod+": "+String.format(MsCC.getMsg(MsCC.CC50, cLang), x, xList.get(x)));  // CC46 = el fatal error x no es correcto. File: y
				throw new ExcepcionChecker(cf);
			}

		return;  // todo fue bien, los errores son los mismos
	}




	// pide y devuelve la lista de errores detectados de un usuario

	 public static Element requestErrores (String usuario, String url, String passwd, String lang)
			 		throws ExcepcionChecker
	 {
		Document doc;
		String qs, call;
		CheckerFailure cf;
		String currentMethod = Thread.currentThread().getStackTrace()[1].getMethodName();

		qs = "?"+CommonSINT.PFASE+"=02&auto=true&p="+passwd;
		call = url+qs;

		errorHandler.clear();

		try {
			doc = db.parse(call);
		}
		catch (SAXException ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			CommonMMLChecker.logCall(call);

			cf = new CheckerFailure(call, "", ex.toString().replace("<", "&lt;"));
			cf.addMotivo(currentMethod+": SAXException: "+MsCC.getMsg(MsCC.CC51,lang)); // CC51 = Error al solicitar/parsear la lista de ficheros erróneos"
			throw new ExcepcionChecker(cf);
		}
		catch (Exception ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			CommonMMLChecker.logCall(call);

			cf = new CheckerFailure(call, "", ex.toString());
			cf.addMotivo(currentMethod+": Exception: "+MsCC.getMsg(MsCC.CC51,lang)); // CC51 = Error al solicitar/parsear la lista de ficheros erróneos"
			throw new ExcepcionChecker(cf);
		}


		if (errorHandler.hasErrors()) {
			CommonMMLChecker.logCall(call);

			ArrayList<String> errors = errorHandler.getErrors();
			String msg="";

			for (int x=0; x < errors.size(); x++)
				if (x == (errors.size()-1)) msg += "++++"+errors.get(x);
				else msg += "++++"+errors.get(x)+"<br>\n";


			cf = new CheckerFailure(call, "", msg);
			cf.addMotivo(currentMethod+": "+String.format(MsCC.getMsg(MsCC.CC52,lang), "<errors>"));  // CC52 Resultado inválido, <errors> al parsear la lista de ficheros erróneos
			throw new ExcepcionChecker(cf);
		}

		if (errorHandler.hasFatalerrors()) {
			CommonMMLChecker.logCall(call);

			ArrayList<String> fatalerrors = errorHandler.getFatalerrors();
			String msg="";

			for (int x=0; x < fatalerrors.size(); x++)
				if (x == (fatalerrors.size()-1)) msg += "++++"+fatalerrors.get(x);
				else msg += "++++"+fatalerrors.get(x)+"<br>\n";


			cf = new CheckerFailure(call, "", msg);
			cf.addMotivo(currentMethod+": "+String.format(MsCC.getMsg(MsCC.CC52,lang), "<fatalerrors>"));  // CC52 Resultado inválido, <fatalerrors> al parsear la lista de ficheros erróneos
			throw new ExcepcionChecker(cf);
		}


		if (doc == null) {
			CommonMMLChecker.logCall(call);

			cf = new CheckerFailure(call, "", currentMethod+": "+MsCC.getMsg(MsCC.CC53,lang));    // CC53 = El parser devuelve 'null' al parsear la lista de ficheros erróneos
			throw new ExcepcionChecker(cf);
		}

		Element e = doc.getDocumentElement();
		String tagName = e.getTagName();

		if (tagName.equals("wrongRequest")) {
			String reason = e.getTextContent();

			cf = new CheckerFailure(call, "", currentMethod+": "+MsCC.getMsg(MsCC.CC54,lang)+reason); // CC54 = Al solicitar la lista de ficheros erróneos se recibe <wrongRequest>reason
			throw new ExcepcionChecker(cf);
		}

		if (!tagName.equals("wrongDocs")) {
			CommonMMLChecker.logMMLChecker("Elemento resultado = "+tagName);

			cf = new CheckerFailure(call, "", currentMethod+": "+MsCC.getMsg(MsCC.CC55,lang)+"(<"+tagName+">)"); // CC55 = Al solicitar la lista de ficheros erróneos se recibe una respuesta incorrecta:
			throw new ExcepcionChecker(cf);
		}

		NodeList nlErrores = doc.getElementsByTagName("wrongDocs");
		Element elemErrores = (Element)nlErrores.item(0);

		return elemErrores;
	}





	// método para comprobar si un servicio está operativo, pidiendo el estado
	// no devuelve nada si está operativo,
	// levanta una excepción  ExcepcionChecker si falla algo

	public static void doOneCheckUpStatus (HttpServletRequest request, String user, String passwd, String cLang)
	throws ExcepcionChecker
	{
		CheckerFailure cf;
		Document doc;
		Element e;
		String qs;
		String currentMethod = Thread.currentThread().getStackTrace()[1].getMethodName();

		ServletContext scPropio, scUser;
		scPropio = request.getServletContext();  // contexto del que ejecuta el checker (sintprof)
		scUser = scPropio.getContext("/"+user);  // contexto del User que vamos a comprobar

		CommonMMLChecker.logMMLChecker("Vamos a comprobar el estado de "+user);

		if (scUser == null)  {
			CommonMMLChecker.logMMLChecker("01_NOCONTEXT: null "+user);
			throw new ExcepcionChecker(new CheckerFailure("", "01_NOCONTEXT", currentMethod+": No existe el contexto "+user));
		}

		String cpUser = scUser.getContextPath();        // context path del User que vamos a comprobar

		if (cpUser.equals("")) {
			CommonMMLChecker.logMMLChecker("01_NOCONTEXT:"+cpUser+"**"+user);
			throw new ExcepcionChecker(new CheckerFailure("", "01_NOCONTEXT", currentMethod+": No existe el contexto "+user));
		}


		// vamos a pedir el estado sin passwd, para comprobar que responde con error

		String url = "http://"+server_port+cpUser+CommonMMLChecker.SERVICE_NAME;
		String call;

		errorHandler.clear();

		qs = "?auto=true";
		call = url+qs;

		try {
			doc = db.parse(call);  // petición del estado sin passwd
		}
		catch (ConnectException ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			cf = new CheckerFailure(call, "00_DOWN", ex.toString());
			cf.addMotivo(currentMethod+": (ConnectException): "+MsCC.getMsg(MsCC.CC23, cLang)); // CC23 = el servidor no responde
			throw new ExcepcionChecker(cf);    // No debería pasar, ya que es el mismo servidor que está ejecutando el checker
		}
		catch (FileNotFoundException ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			cf = new CheckerFailure(call, "02_FILENOTFOUND", ex.toString());
			cf.addMotivo(currentMethod+": (FileNotFoundException):"+MsCC.getMsg(MsCC.CC24, cLang)); // CC24 = el servlet no está declarado
			throw new ExcepcionChecker(cf);   // el servlet no está declarado en el web.xml de ese usuario
		}
		catch (com.sun.org.apache.xerces.internal.impl.io.MalformedByteSequenceException ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			cf = new CheckerFailure(call, "03_ENCODING", ex.toString());
			cf.addMotivo(currentMethod+": (MalformedByteSequenceException): "+MsCC.getMsg(MsCC.CC25, cLang));  // CC25 = La codificación de caracteres recibida es incorrecta (no UTF-8)
			throw new ExcepcionChecker(cf);
		}
		catch (IOException ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			cf = new CheckerFailure(call, "04_IOEXCEPTION", ex.toString());
			cf.addMotivo(currentMethod+": (IOException): "+MsCC.getMsg(MsCC.CC26, cLang)); // CC26 = No se ha encontrado la clase del servlet
			throw new ExcepcionChecker(cf);
		}
		catch (SAXException ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			CommonMMLChecker.logCall(call);

			cf = new CheckerFailure(call, "05_BF", ex.toString());
			cf.addMotivo(currentMethod+": (SAXException): "+MsCC.getMsg(MsCC.CC27, cLang));  // CC27 = La respuesta al pedir el estado sin passwd está mal construida
			throw new ExcepcionChecker(cf);
		}
		catch (Exception ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			CommonMMLChecker.logCall(call);

			cf = new CheckerFailure(call,"07_ERRORUNKNOWN", ex.toString());
			cf.addMotivo(currentMethod+": (Exception): "+MsCC.getMsg(MsCC.CC28, cLang));  // CC28 = Error desconocido al realizar la solicitud de estado sin passwd
			throw new ExcepcionChecker(cf);
		}

		if (errorHandler.hasErrors()) {
			CommonMMLChecker.logCall(call);

			ArrayList<String> errors = errorHandler.getErrors();
			String msg="";

			for (int x=0; x < errors.size(); x++)
				if (x == (errors.size()-1)) msg += "++++"+errors.get(x);
				else msg += "++++"+errors.get(x)+"<br>\n";

			CommonMMLChecker.logMMLChecker(msg);

			cf = new CheckerFailure(call,"06_INVALID", msg);
			cf.addMotivo(currentMethod+": "+MsCC.getMsg(MsCC.CC29, cLang)+"errors");  // CC29 = La respuesta al pedir el estado sin passwd es inválida, tiene
			throw new ExcepcionChecker(cf);
		}

		if (errorHandler.hasFatalerrors()) {
			CommonMMLChecker.logCall(call);

			ArrayList<String> fatalerrors = errorHandler.getFatalerrors();
			String msg="";

			for (int x=0; x < fatalerrors.size(); x++)
				if (x == (fatalerrors.size()-1)) msg += "++++"+fatalerrors.get(x);
				else msg += "++++"+fatalerrors.get(x)+"<br>\n";

			CommonMMLChecker.logMMLChecker(msg);
			cf = new CheckerFailure(call,"06_INVALID", msg);
			cf.addMotivo(currentMethod+": "+MsCC.getMsg(MsCC.CC29, cLang)+"fatal errors");  // CC29 = La respuesta al pedir el estado sin passwd es inválida, tiene
			throw new ExcepcionChecker(cf);
		}

		e = doc.getDocumentElement();
		String tagName = e.getTagName();

		if (tagName.equals("service"))
			throw new ExcepcionChecker(new CheckerFailure(call, "08_OKNOPASSWD", currentMethod+": "+MsCC.getMsg(MsCC.CC30, cLang)));   // CC30 = No ha requerido passwd

		if (tagName.equals("wrongRequest")) {
			String reason = e.getTextContent().trim().toLowerCase();
			if (!reason.equals("no passwd"))
				throw new ExcepcionChecker(new CheckerFailure(call, "10_BADANSWER", currentMethod+": "+MsCC.getMsg(MsCC.CC31, cLang)+reason));  // CC31 = Responde con &lt;wrongRequest> pero no por 'no passwd', sino por:
		}
		else throw new ExcepcionChecker(new CheckerFailure(call, "10_BADANSWER", currentMethod+": "+MsCC.getMsg(MsCC.CC32, cLang)+"&lt;"+tagName+">"));   // CC32 = No ha contestado con &lt;wrongRequest> al no enviar passwd, sino con


		// si ha llegado hasta aquí es que respondió correctamente con <wrongRequest>no passwd</wrongRequest>


		// vamos ahora a pedir el estado incluyendo la passwd

		errorHandler.clear();

		qs = "?auto=true&p="+passwd;
		call = url+qs;

		try {
			doc = db.parse(call);  // petición del estado
		}
		catch (ConnectException ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			cf = new CheckerFailure(call, "00_DOWN", ex.toString());
			cf.addMotivo(currentMethod+": (ConnectException): "+MsCC.getMsg(MsCC.CC23, cLang)); // CC23 = el servidor no responde
			throw new ExcepcionChecker(cf);    // No debería pasar, ya que es el mismo servidor que está ejecutando el checker
		}
		catch (FileNotFoundException ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			cf = new CheckerFailure(call, "02_FILENOTFOUND", ex.toString());
			cf.addMotivo(currentMethod+": (FileNotFoundException):"+MsCC.getMsg(MsCC.CC24, cLang)); // CC24 = el servlet no está declarado
			throw new ExcepcionChecker(cf);   // el servlet no está declarado en el web.xml de ese usuario
		}
		catch (com.sun.org.apache.xerces.internal.impl.io.MalformedByteSequenceException ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			cf = new CheckerFailure(call, "03_ENCODING", ex.toString());
			cf.addMotivo(currentMethod+": (MalformedByteSequenceException): "+MsCC.getMsg(MsCC.CC25, cLang));  // CC25 = La codificación de caracteres recibida es incorrecta (no UTF-8)
			throw new ExcepcionChecker(cf);
		}
		catch (IOException ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			cf = new CheckerFailure(call, "04_IOEXCEPTION", ex.toString());
			cf.addMotivo(currentMethod+": (IOException): "+MsCC.getMsg(MsCC.CC26, cLang)); // CC26 = No se ha encontrado la clase del servlet
			throw new ExcepcionChecker(cf);
		}
		catch (SAXException ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			CommonMMLChecker.logCall(call);

			cf = new CheckerFailure(call, "05_BF", ex.toString());
			cf.addMotivo(currentMethod+": (SAXException): "+MsCC.getMsg(MsCC.CC33, cLang));  // CC33 = La respuesta al pedir el estado con passwd está mal construida
			throw new ExcepcionChecker(cf);
		}
		catch (Exception ex) {
			CommonMMLChecker.logMMLChecker(ex.toString());
			CommonMMLChecker.logCall(call);

			cf = new CheckerFailure(call,"07_ERRORUNKNOWN", ex.toString());
			cf.addMotivo(currentMethod+": (Exception): "+MsCC.getMsg(MsCC.CC34, cLang));  // CC34 = Error desconocido al realizar la solicitud de estado con passwd
			throw new ExcepcionChecker(cf);
		}


		if (errorHandler.hasErrors()) {
			CommonMMLChecker.logCall(call);

			ArrayList<String> errors = errorHandler.getErrors();
			String msg="";

			for (int x=0; x < errors.size(); x++)
				if (x == (errors.size()-1)) msg += "++++"+errors.get(x);
				else msg += "++++"+errors.get(x)+"<br>\n";

			CommonMMLChecker.logMMLChecker(msg);
			cf = new CheckerFailure(call,"06_INVALID", msg);
			cf.addMotivo(currentMethod+": "+MsCC.getMsg(MsCC.CC35, cLang)+"errors");  // CC35 = La respuesta al pedir el estado con passwd es inválida, tiene
			throw new ExcepcionChecker(cf);
		}

		if (errorHandler.hasFatalerrors()) {
			CommonMMLChecker.logCall(call);

			ArrayList<String> fatalerrors = errorHandler.getFatalerrors();
			String msg="";

			for (int x=0; x < fatalerrors.size(); x++)
				if (x == (fatalerrors.size()-1)) msg += "++++"+fatalerrors.get(x);
				else msg += "++++"+fatalerrors.get(x)+"<br>\n";

			CommonMMLChecker.logMMLChecker(msg);
			cf = new CheckerFailure(call,"06_INVALID", msg);
			cf.addMotivo(currentMethod+": "+MsCC.getMsg(MsCC.CC35, cLang)+"fatal errors");  // CC35 = La respuesta al pedir el estado con passwd es inválida, tiene
			throw new ExcepcionChecker(cf);
		}


		e = doc.getDocumentElement();
		tagName = e.getTagName();

		if (tagName.equals("wrongRequest")) {
			String reason = e.getTextContent().trim();
			if (reason.equals("bad passwd")) {
				throw new ExcepcionChecker(new CheckerFailure(call, "09_BADPASSWD", currentMethod+": "+MsCC.getMsg(MsCC.CC36, cLang))); // CC36 = el servicio dice que la passwd enviada es incorrecta"
			}
			else throw new ExcepcionChecker(new CheckerFailure(call, "10_BADANSWER", currentMethod+": "+MsCC.getMsg(MsCC.CC37, cLang)+reason)); //  CC37 = al pedir el estado, el servicio contesta con &lt;wrongRequest> con razón desconocida
		}

		if (!tagName.equals("service"))
			throw new ExcepcionChecker(new CheckerFailure(call, "10_BADANSWER", currentMethod+": "+MsCC.getMsg(MsCC.CC38, cLang)+"(&lt;"+tagName+">)"));   //  al pedir el estado, el servicio contesta con un tag incorrecto

		CommonMMLChecker.logMMLChecker(currentMethod+": "+user+" OK");
		return;
	}






	// Envío del fichero de un resultado negativo
	// el nombre del fichero se recibe en el parámetro 'file'
	// es sólo para el profesor, no es necesario traducirlo

	public static void doGetRequestResultFile(HttpServletRequest request, HttpServletResponse response, String cLang)
						throws IOException, ServletException
	{
		File file;
		BufferedReader br;
		String linea;

		PrintWriter out = response.getWriter();

		out.println("<html>");
		CommonMMLChecker.printHead(out, cLang);
		CommonMMLChecker.printBodyHeader(out, cLang);

		String resultFile = request.getParameter("file");
		if (resultFile == null) {
			out.println("<h4>Error: no se ha recibido el parámetro con el nombre del fichero solicitado</h4>");
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

		CommonSINT.printFoot(out, MsMMLCh.CREATED);

		out.println("</body></html>");
	}



	// para obtener la passwd de un alumno que está en el server.xml

	public static String getAluPasswd (String alu) throws ExcepcionChecker
	{
		ServletContext scAlu;

		scAlu = CommonMMLChecker.servletContextSintProf.getContext("/"+alu);

		if (scAlu == null) {
			CheckerFailure cf = new CheckerFailure("", "NOCONTEXT", "");
			throw new ExcepcionChecker(cf);
		}

		String cpUser = scAlu.getContextPath();

		if (cpUser.equals("")) {
			CheckerFailure cf = new CheckerFailure("", "NOCONTEXT", "");
			throw new ExcepcionChecker(cf);
		}

        String passwdAlu = scAlu.getInitParameter("passwd");
        if (passwdAlu == null) {
			    CheckerFailure cf = new CheckerFailure("", "NOPASSWD", "");
			    throw new ExcepcionChecker(cf);
				}

        if (passwdAlu.equals("")) {
					CheckerFailure cf = new CheckerFailure("", "NOPASSWD", "");
			    throw new ExcepcionChecker(cf);
				}

        return passwdAlu;
	}


	// para comprobar que un alumno tiene sus ficheros en regla

	public static void doOneCheckUpFiles (String alu_num,  String consulta, String cLang)
			throws ExcepcionChecker
	{
		String currentMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
	  String SINT_HOME = "/home/eetlabs.local/sint/sint";
	  String aluPublic = SINT_HOME+alu_num+"/public_html/";
	  String aluClasses = aluPublic+"webapps/WEB-INF/classes/";
	  String f ;
	  File fd;
		int veces, veces_call1, veces_call2, veces_call3;
		String call1, call2, call3;
		int veces_Builder=0, veces_public=0;

		if (consulta.equals("1")) {
			call1 = "getQ1Years";
			call2 = "getQ1Movies";
			call3 = "getQ1Cast";
		}
		else {
			call1 = "getQ2Langs";
			call2 = "getQ2Cast";
			call3 = "getQ2Movies";
		}

		SearchInFile sif = new SearchInFile();

		// comprobamos que mml.xsd esté en public_html/p2
	  f = aluPublic+"p2/mml.xsd";
	  fd = new File(f);
	  if (!fd.isFile())
				// CC15 = "No existe o no se puede acceder al fichero "
				throw new ExcepcionChecker(new CheckerFailure("", "12_FILES", currentMethod+": "+MsCC.getMsg(MsCC.CC15, cLang)+f));

		// comprobamos que FrontEnd.java esté en public_html/p2
			f = aluPublic+"p2/FrontEnd.java";
			fd = new File(f);
			if (!fd.isFile())
				// CC15 = "No existe o no se puede acceder al fichero "
				throw new ExcepcionChecker(new CheckerFailure("", "12_FILES", currentMethod+": "+MsCC.getMsg(MsCC.CC15, cLang)+f));

		// revisamos el fichero public_html/p2/SintXP2.java, que debe estar en public_html/p2
		f = aluPublic+"p2/Sint"+alu_num+"P2.java";
		fd = new File(f);
		if (!fd.isFile())
				throw new ExcepcionChecker(new CheckerFailure("", "12_FILES", currentMethod+": "+MsCC.getMsg(MsCC.CC15, cLang)+f));  // CC15 = "No existe o no se puede acceder al fichero "


		// comprobamos que DataModel.java esté en public_html/p2
		f = aluPublic+"p2/DataModel.java";
		fd = new File(f);
		if (!fd.isFile())
				// CC15 = "No existe o no se puede acceder al fichero "
				throw new ExcepcionChecker(new CheckerFailure("", "12_FILES", currentMethod+": "+MsCC.getMsg(MsCC.CC15, cLang)+f));



		// comprobamos que DataModel.java contiene las llamadas getQ*
		try {
			veces = sif.isInFile(call1, fd);
		}
		catch (Exception ex) {
			// CC17 = "problema leyendo el fichero "
			throw new ExcepcionChecker(new CheckerFailure("", "12_FILES", currentMethod+": "+call1+" "+MsCC.getMsg(MsCC.CC17, cLang)+f));
		}

		if (veces != 1)
			// CC20 = "debe aparecer una vez, y sólo una, en el fichero "
			throw new ExcepcionChecker(new CheckerFailure("", "12_FILES", currentMethod+": "+call1+" "+MsCC.getMsg(MsCC.CC20, cLang)+f));


		try {
			veces = sif.isInFile(call2, fd);
		}
		catch (Exception ex) {
			// CC17 = "problema leyendo el fichero "
			throw new ExcepcionChecker(new CheckerFailure("", "12_FILES", currentMethod+": "+call2+" "+MsCC.getMsg(MsCC.CC17, cLang)+f));
		}

		if (veces != 1)
			// CC20 = "debe aparecer una vez, y sólo una, en el fichero "
			throw new ExcepcionChecker(new CheckerFailure("", "12_FILES", currentMethod+": "+call2+" "+MsCC.getMsg(MsCC.CC20, cLang)+f));


		try {
			veces = sif.isInFile(call3, fd);
		}
		catch (Exception ex) {
			// CC17 = "problema leyendo el fichero "
			throw new ExcepcionChecker(new CheckerFailure("", "12_FILES", currentMethod+": "+call3+" "+MsCC.getMsg(MsCC.CC17, cLang)+f));
		}

		if (veces != 1)
			// CC20 = "debe aparecer una vez, y sólo una, en el fichero "
			throw new ExcepcionChecker(new CheckerFailure("", "12_FILES", currentMethod+": "+call3+" "+MsCC.getMsg(MsCC.CC20, cLang)+f));



		// revisamos todos los ficheros java de public_html/p2
		// contamos las veces de getQ*, que deben apaecer 2 veces cada una
		// contamos las veces de newDocumentBuilder, que sólo debe aparecer 1 vez
		// buscamos "public_html", que no debe aparecer


		File carpeta = new File(aluPublic+"p2");
		veces_Builder = veces_public = veces_call1 = veces_call2 = veces_call3 = 0;

		for (File ficheroEntrada : carpeta.listFiles()) {
			if (ficheroEntrada.isFile()) {
					String fileName = ficheroEntrada.getName();

					if (fileName.endsWith(".java")) {
							try {
								 veces_Builder += sif.isInFile("newDocumentBuilder", ficheroEntrada);
								 veces_public += sif.isInFile("public_html", ficheroEntrada);
								 veces_call1 += sif.isInFile(call1, ficheroEntrada);
								 veces_call2 += sif.isInFile(call2, ficheroEntrada);
								 veces_call3 += sif.isInFile(call3, ficheroEntrada);
							}
							catch (Exception ex) {
								// CC17 = "problema leyendo el fichero "
								throw new ExcepcionChecker(new CheckerFailure("", "12_FILES", currentMethod+": getQ* "+MsCC.getMsg(MsCC.CC17, cLang)+fileName));
							}
					}
				}
    }

		if (veces_Builder != 1)
				// CC21 = "debe aparecer una vez, y sólo una, en el código, pero aparece "
			 	throw new ExcepcionChecker(new CheckerFailure("", "12_FILES", currentMethod+": newDocumentBuilder "+MsCC.getMsg(MsCC.CC21, cLang)+veces_Builder));

		if (veces_public > 0)
				// CC21 = "Encontrada la cadena prohibida '%s' en el código"
				throw new ExcepcionChecker(new CheckerFailure("", "12_FILES", currentMethod+": public_html "+String.format(MsCC.getMsg(MsCC.CC16, cLang), "public_html")));

		if (veces_call1 != 2)
			 // CC22 = "debe aparecer dos veces, y sólo dos, en el código, pero aparece "
			 throw new ExcepcionChecker(new CheckerFailure("", "12_FILES", currentMethod+": "+call1+" "+MsCC.getMsg(MsCC.CC22, cLang)+veces_call1));

		if (veces_call2 != 2)
			 // CC22 = "debe aparecer dos veces, y sólo dos, en el código, pero aparece "
			 throw new ExcepcionChecker(new CheckerFailure("", "12_FILES", currentMethod+": "+call2+" "+MsCC.getMsg(MsCC.CC22, cLang)+veces_call2));

		if (veces_call3 != 2)
			 // CC22 = "debe aparecer dos veces, y sólo dos, en el código, pero aparece "
			 throw new ExcepcionChecker(new CheckerFailure("", "12_FILES", currentMethod+": "+call3+" "+String.format(MsCC.getMsg(MsCC.CC22, cLang), call3)+veces_call3));




		// revisamos el fichero  webapps/p2/mml.xsd
	  f = aluPublic+"webapps/p2/mml.xsd";
	  fd = new File(f);
	  if (!fd.isFile())
				throw new ExcepcionChecker(new CheckerFailure("", "12_FILES", currentMethod+": "+MsCC.getMsg(MsCC.CC15, cLang)+f));  // CC15 = "No existe o no se puede acceder al fichero "

		try {
			veces = sif.isInFile("mixed", fd);
		}
		catch (Exception ex) {
			// CC17 = "problema leyendo el fichero "
			throw new ExcepcionChecker(new CheckerFailure("", "12_FILES", currentMethod+": mixed "+MsCC.getMsg(MsCC.CC17, cLang)+f));
		}

		if (veces != 1)
				// CC20 = "debe aparecer sólo una vez en el fichero "
				throw new ExcepcionChecker(new CheckerFailure("", "12_FILES", currentMethod+": mixed "+MsCC.getMsg(MsCC.CC20, cLang)+f));



		// comprobamos que SintXP2.class esté dentro de classes/p2
	  f = aluClasses+"p2/Sint"+alu_num+"P2.class";
	  fd = new File(f);
	  if (!fd.isFile())
				throw new ExcepcionChecker(new CheckerFailure("", "12_FILES", currentMethod+": "+MsCC.getMsg(MsCC.CC15, cLang)+f));  // CC15 = "No existe o no se puede acceder al fichero "

		// comprobamos que SintXP2.class no esté dentro de classes
	  f = aluClasses+"Sint"+alu_num+"P2.class";
	  fd = new File(f);
	  if (fd.isFile())
				throw new ExcepcionChecker(new CheckerFailure("", "12_FILES", currentMethod+": "+MsCC.getMsg(MsCC.CC19, cLang)+f));  // CC19 = "Fichero prohibido en esa ubicación: "

	  return;
	}


	// para hacer una llamada a un servicio y hacer log con el resultado

	public static void logCall (String call)
	{
		try {
		     String urlContents = CommonSINT.getURLContents(call);
		     CommonMMLChecker.logMMLChecker(urlContents);
		}
		catch (ExcepcionChecker ex) {
			String motivos = ex.getCheckerFailure().toString();
			CommonMMLChecker.logMMLChecker(motivos);
		}
	}


	// OJO SOLO HAY 2 PARAMETROS

	// pide la lista de elementos resultado sin algún parámetro (recibe los nombres y valores de los parámetros)
	// comprueba que recibe del usuario las correspondientes notificaciones de error
	// levanta ExcepcionChecker si algo va mal

	public static void checkLackParam (String url, String passwd, String VPF, String NP1, String VP1, String NP2,  String VP2, String cLang)
						throws ExcepcionChecker
	{
		String currentMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
		CheckerFailure cf;
		Document doc;
		Element el;
		String tagName, reason, call, qs, qs1, qs2;

		try {
            VP1 = URLEncoder.encode(VP1, "utf-8");
         }
    catch (UnsupportedEncodingException ex) {
        cf = new CheckerFailure(url, "", currentMethod+": "+MsCC.getMsg(MsCC.CC58, cLang)+VP1);  // CC58 = "utf-8 no soportado para parametro"
		    throw new ExcepcionChecker(cf);
    }

		try {
            VP2 = URLEncoder.encode(VP2, "utf-8");
         }
    catch (UnsupportedEncodingException ex) {
        cf = new CheckerFailure(url, "", currentMethod+": "+MsCC.getMsg(MsCC.CC58, cLang)+VP2);  // CC58 = "utf-8 no soportado para parametro"
		    throw new ExcepcionChecker(cf);
    }

		qs = "?auto=true&"+CommonSINT.PFASE+"="+VPF+"&p="+passwd;     // falta NP1, NP2
		qs1 = qs+"&"+NP2+"="+VP2;     // falta NP1
		qs2 = qs+"&"+NP1+"="+VP1;   // falta NP2


		// probando qs1, donde falta NP1

		call = url+qs1;

		CommonMMLChecker.errorHandler.clear();

		try {
			doc = CommonMMLChecker.db.parse(call);
		}
		catch (SAXException e) {
			CommonMMLChecker.logCall(call);

			cf = new CheckerFailure(call, "", e.toString());
			cf.addMotivo(currentMethod+": SAXException: "+MsCC.getMsg(MsCC.CC59, cLang)+"'"+NP1+"'"); // CC59 = al solicitar y parsear la lista de resultados sin el parámetro obligatorio x
			throw new ExcepcionChecker(cf);
		}
		catch (Exception e) {
			CommonMMLChecker.logCall(call);

			cf = new CheckerFailure(call, "", e.toString());
			cf.addMotivo(currentMethod+": Exception: "+MsCC.getMsg(MsCC.CC59, cLang)+"'"+NP1+"'");  // CC59 = al solicitar y parsear la lista de resultados sin el parámetro obligatorio x
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
			cf.addMotivo(currentMethod+": "+String.format(MsCC.getMsg(MsCC.CC60, cLang), "errors")+"'"+NP1+"'");  // CC60 = Respuesta inválida (tiene %s) al solicitar y parsear la lista de resultados sin el parámetro obligatorio x
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
			cf.addMotivo(currentMethod+": "+String.format(MsCC.getMsg(MsCC.CC60, cLang), "fatal errors")+"'"+NP1+"'");  // CC60 = Respuesta inválida (tiene %s) al solicitar y parsear la lista de resultados sin el parámetro obligatorio x
			throw new ExcepcionChecker(cf);
		}

		if (doc == null) {
			cf = new CheckerFailure(call, "", currentMethod+": "+MsCC.getMsg(MsCC.CC61,cLang)+"'"+NP1+"'");  // CC61 = Se recibe 'null' al solicitar la lista de resultados sin el parámetro obligatorio x
			throw new ExcepcionChecker(cf);
		}

		el = doc.getDocumentElement();
		tagName = el.getTagName();
		if (tagName.equals("wrongRequest")) {
			reason = el.getTextContent().trim();

			if (!reason.equals("no param:"+NP1)) {
				cf = new CheckerFailure(call, "", currentMethod+": "+String.format(MsCC.getMsg(MsCC.CC62, cLang), NP1)+"'"+reason+"'"); // CC62 = "Responde con &lt;wrongRequest> pero no por 'no param:%s', sino por: " reason
				throw new ExcepcionChecker(cf);
			}
		}
		else {
			cf = new CheckerFailure(call, "", currentMethod+": "+MsCC.getMsg(MsCC.CC63, cLang)+"'"+NP1+"'"); // CC63 = "No se recibe &lt;wrongRequest> al solicitar la lista de resultados sin el parámetro obligatorio "
			throw new ExcepcionChecker(cf);
		}



		// probando qs2, donde falta NP2

		call = url+qs2;

		CommonMMLChecker.errorHandler.clear();

		try {
			doc = CommonMMLChecker.db.parse(call);
		}
		catch (SAXException e) {
			CommonMMLChecker.logCall(call);

			cf = new CheckerFailure(call, "", e.toString());
			cf.addMotivo(currentMethod+": SAXException: "+MsCC.getMsg(MsCC.CC59, cLang)+"'"+NP2+"'"); // CC59 = al solicitar y parsear la lista de resultados sin el parámetro obligatorio x
			throw new ExcepcionChecker(cf);
		}
		catch (Exception e) {
			CommonMMLChecker.logCall(call);

			cf = new CheckerFailure(call, "", e.toString());
			cf.addMotivo(currentMethod+": Exception: "+MsCC.getMsg(MsCC.CC59, cLang)+"'"+NP2+"'"); // CC59 = al solicitar y parsear la lista de resultados sin el parámetro obligatorio x
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
			cf.addMotivo(currentMethod+": "+String.format(MsCC.getMsg(MsCC.CC60, cLang), "errors")+"'"+NP2+"'");  // CC60 = Respuesta inválida (tiene %s) al solicitar y parsear la lista de resultados sin el parámetro obligatorio x
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
			cf.addMotivo(currentMethod+": "+String.format(MsCC.getMsg(MsCC.CC60, cLang), "fatal errors")+"'"+NP2+"'");  // CC60 = Respuesta inválida (tiene %s) al solicitar y parsear la lista de resultados sin el parámetro obligatorio x
			throw new ExcepcionChecker(cf);
		}

		if (doc == null) {
			cf = new CheckerFailure(call, "", currentMethod+": "+MsCC.getMsg(MsCC.CC61, cLang)+"'"+NP2+"'");  // CC61 = Se recibe 'null' al solicitar la lista de resultados sin el parámetro obligatorio
			throw new ExcepcionChecker(cf);
		}

		el = doc.getDocumentElement();
		tagName = el.getTagName();
		if (tagName.equals("wrongRequest")) {
			reason = el.getTextContent().trim();
			if (!reason.equals("no param:"+NP2)) {
				cf = new CheckerFailure(call, "", currentMethod+": "+String.format(MsCC.getMsg(MsCC.CC62, cLang), NP2)+"'"+reason+"'"); // CC62 = "Responde con &lt;wrongRequest> pero no por 'no param:%s', sino por: " reason
				throw new ExcepcionChecker(cf);
			}
		}
		else {
			cf = new CheckerFailure(call, "", currentMethod+": "+MsCC.getMsg(MsCC.CC63, cLang)+"'"+NP2+"'"); // CC63 = "No se recibe &lt;wrongRequest> al solicitar la lista de resultados sin el parámetro obligatorio "
			throw new ExcepcionChecker(cf);
		}


		// todo bien
		return;
	}



}
