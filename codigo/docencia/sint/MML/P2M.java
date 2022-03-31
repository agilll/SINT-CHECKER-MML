/****************************************************************
 *    SERVICIOS DE INTERNET
 *    EE TELECOMUNICACIÓN
 *    UNIVERSIDAD DE VIGO
 *
 *    Práctica MML
 *
 *    Autor: Alberto Gil Solla
 *    Curso : 2017-2018
 ****************************************************************/


// práctica 2 del lenguage MML con ficheros con Schema
// puede trabajar con datos reales o inventados (parámetro 'real=no' en web.xml)
// se puede especificar en el web.xml el directorio base  de los documentos (parámetro dirBase), el directorio base de los Schemas (dirRulesBase), y el fichero inicial (urlInicial)

package docencia.sint.MML;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import docencia.sint.Common.CommonSINT;
import docencia.sint.Common.ErrorHandlerSINT;

import javax.xml.xpath.XPathConstants;


public class P2M extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
	
	// todas estas son variables de clase, compartidas por todos los usuarios
    
    final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    final String MML_SCHEMA = "_rules/mml.xsd";
	    
    // los valores por defecto, si no hay parámetros de configuración
    String dirBaseDefault = "http://localhost:7000/sintprof/ficheros_prueba/17-18_MML/";
    String urlInicialDefault = "mml2001.xml";
    // String urlInicialDefault = "T1.xml";
    
    // los parámetros de configuración o los valores por defecto
    String dirBase;
    String urlInicial;
    
    ArrayList<String> listaFicherosWarnings = new ArrayList<String>();
    ArrayList<ArrayList<String>> listaWarnings= new ArrayList<ArrayList<String>>();
    ArrayList<String> listaFicherosErrores= new ArrayList<String>();
    ArrayList<ArrayList<String>> listaErrores= new ArrayList<ArrayList<String>>();
    ArrayList<String> listaFicherosErroresfatales = new ArrayList<String>();
    ArrayList<ArrayList<String>> listaErroresfatales= new ArrayList<ArrayList<String>>();
	
 
    // El init se ejecuta al cargar el servlet la primera vez
    
    public void init (ServletConfig servletConfig) throws ServletException {
	
        CommonMML.initLoggerMML(P2M.class);
        CommonMML.logMML("\nInit...");
        
	/*  para el examen
	 
	     DocumentBuilderFactory dbfe;    // los terminados en e son para el examen
    DocumentBuilder dbe;
 
    
	dbfe = DocumentBuilderFactory.newInstance();
	
    	try {
    		dbe = dbfe.newDocumentBuilder();
    	}
    	catch  (ParserConfigurationException e) {
    			throw new UnavailableException("Error creando el builder para el examen: "+e);
    	}
	
	Document doce;

    	try {
    		doce = dbe.parse("http://gssi.det.uvigo.es/users/agil/public_html/ex1.xml");
    	}
    	catch (Exception e) {
    		return;
    	}
  

	Element examen = doce.getDocumentElement();
	
	// esto será distinto para cada examen
	
	NodeList nlcalle = examen.getElementsByTagName("calle");
	Element calle = (Element)nlcalle.item(0);
	excalle = calle.getTextContent().trim();

	exnum = calle.getAttribute("numeros");
	
	NodeList nlhijos = examen.getChildNodes();
	
	for (int j=0; j < nlhijos.getLength(); j++) {
		Node e = (Node)nlhijos.item(j);
    		if (e.getNodeType() == org.w3c.dom.Node.TEXT_NODE) {
    			extit = e.getNodeValue().trim();
			if (!extit.equals(""))
				break;
		}

	*/
			
    	
    	
    	// si hay un parámetro "real=no" se trabajará con datos inventados (real=0)
    	// de lo contrario se leerán los ficheros reales

    	String datosReales = servletConfig.getInitParameter("real");

    	if (datosReales != null)
    		if (datosReales.equals("no")) CommonMML.real=0;

    	// si hay un parámetro "dirBase", se tomará como directorio base de los ficheros
    	// de lo contrario se cogerá el especificado por defecto

    	dirBase = servletConfig.getInitParameter("dirBase");
    	if (dirBase == null) dirBase = dirBaseDefault;

    	// si hay un parámetro "urlInicial", se tomará como fichero inicial
    	// de lo contrario se cogerá el especificado por defecto

    	urlInicial = servletConfig.getInitParameter("urlInicial");
    	if (urlInicial == null)  urlInicial = urlInicialDefault;
    	
       	if (CommonMML.real==1) this.buscarFicheros(dirBase, urlInicial, servletConfig);
    }
  
  
    	
	
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    		throws IOException, ServletException
    {
        // previamente se ha comprobado la passwd con un filtro
        
	    String pfase = request.getParameter("pfase");
	    if (pfase == null) pfase = "01";

		switch (pfase) {
		case "01":
			this.doGetHome(request,response);
			break;
		case "02":
			this.doGetErrors(request,response);
			break;
	
			// consulta 1, filmografía de un miembro del reparto
	
		case "11": // se pide el listado de años
			P2MC1.doGetF11Anios(request, response);
			break;
		case "12": // se pide las películas de un año	
			P2MC1.doGetF12Peliculas(request, response); 
			break;
		case "13": // se pide los actores de una pelicula de un año
			P2MC1.doGetF13Actores(request, response); 
			break;
		case "14":  // se pide la filmografía de un actor de una peli de un año
			P2MC1.doGetF14Film(request, response); 
			break;
	
			
			// consulta 2, materias en las que un alumno (que vive en una calle) ha sacado una nota
	
		case "21":  // se pide el listado de idiomas
			P2MC2.doGetF21Langs(request, response);
			break;
		case "22":  // se pide los actores que tienen peliculas en ese idioma
			P2MC2.doGetF22Acts(request, response);
			break;
		case "23":  // se piden los paises que producen peliculas en ese idioma con ese actor
			P2MC2.doGetF23Paises(request, response);
			break;
		case "24":   // se piden las películas de ese actor en ese idioma producidas en ese país
			P2MC2.doGetF24Pelis(request, response);
			break;
	      
			
		default:
			CommonSINT.doBadRequest("el parámetro 'pfase' tiene un valor incorrecto ("+pfase+")", request, response); 
			break;
	}
}


 




    // la pantalla inicial 

    public void doGetHome (HttpServletRequest request, HttpServletResponse response)
    		throws IOException
    {
    	response.setCharacterEncoding("utf-8");
    	PrintWriter out = response.getWriter();

    	String auto = request.getParameter("auto");

    	if (auto == null) {
    		out.println("<html>");
    		CommonMML.printHead(out);
    		out.println("<body>");
   
    		out.println("<h2>"+CommonMML.MSGINICIAL+"</h2>");
    		out.println("<h2>Bienvenido a este servicio</h2>");

    		out.println("<a href='?pfase=02&p="+CommonSINT.PASSWD+"'>Pulsa aquí para ver los ficheros erróneos</a>");

    		out.println("<h3>Selecciona una consulta:</h3>");

    		out.println("<form>");
		out.println("<input type='hidden' name='p' value='"+CommonSINT.PASSWD+"'>");  
    		out.println("<input type='radio' name='pfase' value='11' checked>Consulta 1: Filmografía de un miembro del reparto <br>");
    		out.println("<input type='radio' name='pfase' value='21' checked>Consulta 2: Películas en un idioma, de un actor/actriz, producidas en un país<br>");

    		out.println("<p><input class='enviar' type='submit' value='Enviar'>");
    		out.println("</form>");

    		CommonSINT.printFoot(out, CommonMML.CREATED);
    		out.println("</body></html>");
    	}
    	else {	    
    		out.println("<?xml version='1.0' encoding='utf-8'?>");
    		out.println("<service>");
    		out.println("<status>OK</status>");
    		out.println("</service>");
    	}
    }


    // método que imprime o devuelve la lista de errores

    public void doGetErrors (HttpServletRequest request, HttpServletResponse response)
    		throws IOException
    {
    	response.setCharacterEncoding("utf-8");
    	PrintWriter out = response.getWriter();

    	String auto = request.getParameter("auto");

    	if (auto == null) {
       	out.println("<html>");
    		CommonMML.printHead(out);
    		out.println("<body>");
 
    		out.println("<h2>"+CommonMML.MSGINICIAL+"</h2>");

    		out.println("<h3>Se han encontrado "+listaFicherosWarnings.size()+" ficheros con warnings:</h3>");
    		if (listaFicherosWarnings.size() > 0) {
    			out.println("<ul>");

    			for (int x=0; x < listaFicherosWarnings.size(); x++) {
    				out.println("<li> "+listaFicherosWarnings.get(x)+":<BR>");
 	  			out.println("<ul>");
    	  			
    				ArrayList<String> warningsL = listaWarnings.get(x);

    	  			for (int y=0; y < warningsL.size(); y++) {
    	  				out.println("<li> "+warningsL.get(y)+"<BR>");
    	  			}
    	  			
        			out.println("</ul>");
    			}

    			out.println("</ul>");
    		}

    		out.println("<h3>Se han encontrado "+listaFicherosErrores.size()+" ficheros con errores:</h3>");
    		if (listaFicherosErrores.size() > 0) {
    			out.println("<ul>");

    			for (int x=0; x < listaFicherosErrores.size(); x++) {
    				out.println("<li> "+listaFicherosErrores.get(x)+":<BR>");
    	  			out.println("<ul>");
    	  			
    				ArrayList<String> erroresL = listaErrores.get(x);

    	  			for (int y=0; y < erroresL.size(); y++) {
    	  				out.println("<li> "+erroresL.get(y)+"<BR>");
    	  			}
    	  			
        			out.println("</ul>");
    			}

    			out.println("</ul>");
    		}

    		out.println("<h3>Se han encontrado "+listaFicherosErroresfatales.size()+" ficheros con errores fatales:</h3>");
    		if (listaFicherosErroresfatales.size() > 0) {
    			out.println("<ul>");

    			for (int x=0; x < listaFicherosErroresfatales.size(); x++) {
    				out.println("<li> "+listaFicherosErroresfatales.get(x)+":<BR>");
   	  			out.println("<ul>");
	  			
    				ArrayList<String> fatalerroresL = listaErroresfatales.get(x);

    	  			for (int y=0; y < fatalerroresL.size(); y++) {
    	  				out.println("<li> "+fatalerroresL.get(y)+"<BR>");
    	  			}
    	  			
        			out.println("</ul>");
    			}

    			out.println("</ul>");
    		}

    		out.println("<form>");
		out.println("<input type='hidden' name='p' value='"+CommonSINT.PASSWD+"'>");  
    		out.println("<input class='back' type='submit' value='Atrás'>");
    		out.println("</form>");
    		
    		CommonSINT.printFoot(out, CommonMML.CREATED);
    		out.println("</body></html>");
    	}
    	else {
    		out.println("<?xml version='1.0' encoding='utf-8'?>");
    		out.println("<errores>");

    		out.println("<warnings>");
    		for (int x=0; x < listaFicherosWarnings.size(); x++) {
    			out.println("<warning>");
    			out.println("<file>"+listaFicherosWarnings.get(x)+"</file>");
    			out.println("<cause>");
    			ArrayList<String> warningsL = listaWarnings.get(x);

  			for (int y=0; y < warningsL.size(); y++) {
  				out.println(warningsL.get(y));
  			}
    		  	out.println("</cause>");
    			out.println("</warning>");
    		}
    		out.println("</warnings>");

    		out.println("<errors> ");	
    		for (int x=0; x < listaFicherosErrores.size(); x++) {
    			out.println("<error> ");
    			out.println("<file>"+listaFicherosErrores.get(x)+"</file>");
    			out.println("<cause>");
  			ArrayList<String> errorsL = listaErrores.get(x);

  			for (int y=0; y < errorsL.size(); y++) {
  				out.println(errorsL.get(y));
  			}
    	
    			out.println("</cause>");
    			out.println("</error>");
    		}
    		out.println("</errors>");

    		out.println("<fatalerrors>");	
    		for (int x=0; x < listaFicherosErroresfatales.size(); x++) {
    			out.println("<fatalerror>");
    			out.println("<file>"+listaFicherosErroresfatales.get(x)+"</file>");
    			out.println("<cause>");
   		    ArrayList<String> fatalerroresL = listaErroresfatales.get(x);

  			for (int y=0; y < fatalerroresL.size(); y++) {
  				out.println(fatalerroresL.get(y));
  			}
    			out.println("</cause>");
    			out.println("</fatalerror>");
    		}
    		out.println("</fatalerrors>");

    		out.println("</errores>");
    	}
    }








    // MÉTODOS AUXILIARES


    // Zona de búsqueda de ficheros, llamado la primera vez que se invoca el doGet

    public void buscarFicheros (String urlBase, String fich, ServletConfig conf)
	throws UnavailableException  {

	DocumentBuilderFactory dbf;
	DocumentBuilder db;
	ErrorHandlerSINT errorHandler;

	dbf = DocumentBuilderFactory.newInstance();
	dbf.setValidating(true);
	dbf.setNamespaceAware(true);
	dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
	
	ServletContext servCont = conf.getServletContext();
	String pathSchema = servCont.getRealPath(MML_SCHEMA);
	File fileSchema = new File(pathSchema);
	dbf.setAttribute(JAXP_SCHEMA_SOURCE, fileSchema);
	
	/* otra forma
	 * 
	 FICHERO_SCHEMA = "/eaml.xsd";
SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
StreamSource streamSource = new StreamSource(this.getServletContext().getResourceAsStream(FICHERO_SCHEMA));
Schema schema = sf.newSchema(streamSource);
dbf.setSchema(schema);
	 
	 */

	
	try {
		db = dbf.newDocumentBuilder();
	}
	catch  (ParserConfigurationException e) {
		throw new UnavailableException("Error creando el analizador de ficheros MML: "+e);
	}

	errorHandler = new ErrorHandlerSINT();
	db.setErrorHandler(errorHandler);
			
    	Document doc;
    	String url = urlBase+fich;

    	// parsear el fichero solicitado

    	errorHandler.clear();  // resetear el ErrorHandler para borrar lo anterior
    	
    	try {
    		doc = db.parse(url);
    	}
    	catch (SAXException ex) {
    		if (!listaFicherosErroresfatales.contains(url)) {
    			listaFicherosErroresfatales.add(url);
    			listaErroresfatales.add(new ArrayList<String>(Arrays.asList(ex.toString())));
    		}
    		return;
    	}
    	catch (IOException ex) {
    		if (!listaFicherosErroresfatales.contains(url)) {
    			listaFicherosErroresfatales.add(url);
    			listaErroresfatales.add(new ArrayList<String>(Arrays.asList(ex.toString())));
    		}    
    		return;
    	}

    	// ver si saltó el ErrorHandler

    	if (errorHandler.hasWarnings()) {
    		if (!listaFicherosWarnings.contains(url)) {
    			listaFicherosWarnings.add(url);
    			listaWarnings.add(errorHandler.getWarnings());
    		}
    	}

    	if (errorHandler.hasErrors()) {
    		if (!listaFicherosErrores.contains(url)) {
    			listaFicherosErrores.add(url); 			
    			listaErrores.add(errorHandler.getErrors());
    		}
    		return;  // si hubo un error se termina
    	}

    	if (errorHandler.hasFatalerrors()) {
    		if (!listaFicherosErroresfatales.contains(url)) {
    			listaFicherosErroresfatales.add(url);
    			listaErroresfatales.add(errorHandler.getFatalerrors());
    		}
    		return;  // si hubo un fatalerror se termina
    	}


    	// Vamos a procesar este año

    	String anio; 

    	// averiguar el año del fichero que acabamos de leer
    	// la excepción no debería producirse, pero...
    	try {
    		NodeList nlAnios = (NodeList)CommonMML.xpath.evaluate("/Movies/Anio", doc, XPathConstants.NODESET);
    		Element elemAnio = (Element)nlAnios.item(0);
    		anio = elemAnio.getTextContent().trim();
    		if (anio.equals("")) throw new Exception("Anio vacío");
    	}
    	catch (Exception ex) {
    		if (!listaFicherosErrores.contains(url)) {
    			listaFicherosErrores.add(url);
    			listaErrores.add(new ArrayList<String>(Arrays.asList("Problema leyendo 'Anio' ("+ex+")"  )));
    		}
    		return;  // si se produce cualquier tipo de excepción, hay un error y se termina
    	}

    	CommonMML.mapDocs.put(anio,doc);  // almacenar el Document del año leído


    	// buscar recursivamente los nuevos ficheros que hay en el que acabamos de leer

    	// conseguir la lista de otras películas

    	NodeList nlOtrapelicula = doc.getElementsByTagName("OtraPelicula");

    	for (int x=0; x < nlOtrapelicula.getLength(); x++) {

    		// procesar cada uno de los encontrados
    		
    		Element elemOtraPelicula = (Element)nlOtrapelicula.item(x);
    		
    		// averiguar el año al que corresponde
    		
    		String nuevoAnio = elemOtraPelicula.getAttribute("anio");
    		if (nuevoAnio.equals("")) continue;

    		// mirar si este año ya lo tenemos

    		Document doc2 = CommonMML.mapDocs.get(nuevoAnio);

    		// si no lo tenemos, aplicamos este método recursivamente sobre su fichero

    		if (doc2 == null) {
    			NodeList nlMML = elemOtraPelicula.getElementsByTagName("MML");		    
    			Element elMML = (Element)nlMML.item(0);
    			String nuevaUrl = elMML.getTextContent().trim();
    			if (nuevaUrl.equals("")) continue;

    			// se ve si la URL del año es absoluta o relativa

    			if (nuevaUrl.startsWith("http://"))   {  // si es absoluta la dividimos entre la base y el fichero
    				String laBase = nuevaUrl.substring(0,nuevaUrl.lastIndexOf('/')+1);
    				String elFichero = nuevaUrl.substring(nuevaUrl.lastIndexOf('/')+1);

    				this.buscarFicheros(laBase, elFichero, conf);
    			}
    			else
    				this.buscarFicheros(urlBase,nuevaUrl, conf);
    		}
    	}
    }
}