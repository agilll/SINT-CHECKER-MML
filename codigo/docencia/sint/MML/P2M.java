/****************************************************************
 *    SERVICIOS DE INTERNET
 *    EE TELECOMUNICACIÓN
 *    UNIVERSIDAD DE VIGO
 *
 *    Práctica MML
 *
 *    Autor: Alberto Gil Solla
 *    Curso : 2021-2022
 ****************************************************************/

// puede trabajar con datos reales o inventados (parámetro 'real=no' en web.xml)
// se puede especificar en el web.xml el directorio base de los documentos (parámetro dirBase),
// el directorio base de los Schemas (dirRulesBase), y el fichero inicial (urlInicial)

package docencia.sint.MML2021;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Collections;

import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import docencia.sint.Common.CommonSINT;
import docencia.sint.Common.MsCP;
import docencia.sint.Common.ErrorHandlerSINT;
import docencia.sint.Common.WrongFile;

import javax.xml.xpath.XPathConstants;



public class P2M extends HttpServlet {

    private static final long serialVersionUID = 1L;

		// todas estas son variables de clase, compartidas por todos los usuarios

    final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    final String MML_SCHEMA = "/_rules/mml2021.xsd";

    // los valores por defecto, si no hay parámetros de configuración
    String dirBaseDefault = "http://localhost:7000/sintprof/ficheros_prueba/21-22_MML/";
    String urlInicialDefault = "mml2001.xml";

    // los parámetros de configuración o los valores por defecto
    String dirBase;
    String urlInicial;



    // El init se ejecuta al cargar el servlet la primera vez

    public void init (ServletConfig servletConfig) throws ServletException {

      CommonMML.initLoggerMML(P2M.class);
      CommonMML.logMML("\nInit..."+servletConfig.getServletName());

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

      CommonMML.logMML("\nLeyendo ficheros...");
      if (CommonMML.real==1) this.buscarFicheros(dirBase, urlInicial, servletConfig);

      Collections.sort(CommonMML.listWarnings);
      Collections.sort(CommonMML.listErrores);
      Collections.sort(CommonMML.listErroresfatales);
    }




    public void doGet(HttpServletRequest request, HttpServletResponse response)
    		throws IOException, ServletException
    {
      // previamente se ha comprobado la passwd con un filtro
			String language;

			String servletPath = request.getServletPath();  // es el servletPath, que será /P2M o /P2Me
			if (servletPath.equals("/P2Me")) language = "en";
			else language = "es";

			String auto = request.getParameter("auto");  // auto = "true" o "false" (default)
			
			
      String fe = request.getParameter("fe");    // fe = "ajax" o "html" (default)

      if (auto == null)  auto = "false";
			else
				if (!auto.equals("true")  &&  !auto.equals("false")) {
					// CP09 = "el parámetro %s tiene un valor incorrecto (%s)"
					CommonSINT.doBadRequest(String.format(MsCP.getMsg(MsCP.CP09, language), "auto", auto), request, response);
					return;
				}

      if (fe == null)  fe = "html";
			else
				if (!fe.equals("html")  &&  !fe.equals("ajax")) {
					// CP09 = "el parámetro %s tiene un valor incorrecto (%s)"
					CommonSINT.doBadRequest(String.format(MsCP.getMsg(MsCP.CP09, language), "fe", fe), request, response);
					return;
				}


	    String fase = request.getParameter(CommonSINT.PFASE);
	    if (fase == null) fase = "01";

	    CommonMML.logMML("Solicitud fase "+fase);

      response.setCharacterEncoding("utf-8");

	    switch (fase) {
				case "01":
					this.doGetHome(request,response, language, auto, fe);
					break;
				case "02":
					this.doGetErrors(request,response, language, auto, fe);
					break;

					// consulta 1, resparto de una película de un año

				case "11": // se pide el listado de años
					P2MC1.doGetF11Years(request, response, language, auto, fe);
					break;
				case "12": // se pide las películas de un año
					P2MC1.doGetF12Movies(request, response, language, auto, fe);
					break;
				case "13": // se pide el reparto de una película de un año
					P2MC1.doGetF13Cast(request, response, language, auto, fe);
					break;

					// consulta 2, filmografía en un idioma de un protagonista

				case "21":  // se pide el listado de idiomas
					P2MC2.doGetF21Langs(request, response, language, auto, fe);
					break;
				case "22":  // se pide los protagonistas que tienen películas en un idioma
					P2MC2.doGetF22Cast(request, response, language, auto, fe);
					break;
				case "23":  // se pide la filmografía de un protagonista en un idioma
					P2MC2.doGetF23Movies(request, response, language, auto, fe);
					break;

				default:
					// CP09 = "el parámetro %s tiene un valor incorrecto (%s)"
					CommonSINT.doBadRequest(String.format(MsCP.getMsg(MsCP.CP09, language), CommonSINT.PFASE, fase), request, response);
					break;
	}
}







    // la pantalla inicial

    public void doGetHome (HttpServletRequest request, HttpServletResponse response, String language, String auto, String fe)
    		throws IOException
    {
      if (auto.equals("true")) {
          response.setContentType("text/xml");
          PrintWriter out = response.getWriter();
          P2MFE.printHomeXML(out);
      }
      else {
					response.setContentType("text/html");
					PrintWriter out = response.getWriter();
					P2MFE.printHomeHTML(language, fe, out);
			}
    }


    // método que imprime o devuelve la lista de errores

    public void doGetErrors (HttpServletRequest request, HttpServletResponse response, String language, String auto, String fe)
    		throws IOException
    {
      if (auto.equals("true")) {
          response.setContentType("text/xml");
          PrintWriter out = response.getWriter();
          P2MFE.printErrorsXML(out);
      }
      else {
          response.setContentType("text/html");
          PrintWriter out = response.getWriter();
          P2MFE.printErrorsHTML(language, fe, out);
      }
    }







    // MÉTODOS AUXILIARES

    // Zona de búsqueda de ficheros, llamado la primera vez que se invoca el doGet

    public void buscarFicheros (String urlBase, String fich, ServletConfig conf)
	         throws UnavailableException  {

        Document doc, doc2;
    		DocumentBuilderFactory dbf;
    		DocumentBuilder db;
    		ErrorHandlerSINT errorHandler;

        CommonMML.logMML("\nBuscando ficheros..."+fich);

        String url = urlBase+fich;
        if (!CommonMML.listaFicherosProcesados.contains(url))
						CommonMML.listaFicherosProcesados.add(url);  // damos este fichero por procesado

    		dbf = DocumentBuilderFactory.newInstance();
    		dbf.setValidating(true);
    		dbf.setNamespaceAware(true);
    		dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);

    		ServletContext servCont = conf.getServletContext();
    		String pathSchema = servCont.getRealPath(MML_SCHEMA);
    		File fileSchema = new File(pathSchema);
    		dbf.setAttribute(JAXP_SCHEMA_SOURCE, fileSchema);
                // dbf.setAttribute(JAXP_SCHEMA_SOURCE, "http://localhost:7000/sintprof/_rules/mml2001.xsd");  // también funciona

	/* otra forma
	 *
	 FICHERO_SCHEMA = "/mml2001.xsd";
SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
StreamSource streamSource = new StreamSource(this.getServletContext().getResourceAsStream(FICHERO_SCHEMA));
Schema schema = sf.newSchema(streamSource);
dbf.setSchema(schema);

	 */


    		try {
    			db = dbf.newDocumentBuilder();
    		}
    		catch  (ParserConfigurationException e) {
    			throw new UnavailableException("Error creando el analizador de ficheros "+MsMML.XML_LANGUAGE+": "+e);
    		}

    		errorHandler = new ErrorHandlerSINT();
    		db.setErrorHandler(errorHandler);

      	// parsear el fichero solicitado

      	errorHandler.clear();  // resetear el ErrorHandler para borrar lo anterior

      	try {
      		doc = db.parse(url);
      	}
      	catch (SAXException ex) {
      		CommonMML.listErroresfatales.add(new WrongFile(url, ex.toString()));
      		return;
      	}
      	catch (IOException ex) {
      		CommonMML.listErroresfatales.add(new WrongFile(url, ex.toString()));
      		return;
      	}

      	// ver si saltó el ErrorHandler

      	if (errorHandler.hasWarnings()) {
        		CommonMML.listWarnings.add(new WrongFile(url, errorHandler.getWarnings()));
      	}

      	if (errorHandler.hasErrors()) {
      		CommonMML.listErrores.add(new WrongFile(url, errorHandler.getErrors()));
      		return;  // si hubo un error se termina
      	}

      	if (errorHandler.hasFatalerrors()) {
         		CommonMML.listErroresfatales.add(new WrongFile(url, errorHandler.getFatalerrors()));
      		return;  // si hubo un fatalerror se termina
      	}

				CommonMML.logMML(fich+" parseado");
      	// Vamos a procesar esta titulación para ver si contiene enlaces a otros ficheros

      	String year;

      	// averiguar el año del fichero que acabamos de leer
      	// la excepción no debería producirse, pero...
      	try {
      		NodeList nlYears = (NodeList)CommonMML.xpath.evaluate("/Movies/Year", doc, XPathConstants.NODESET);
      		Element elemYear = (Element)nlYears.item(0);
      		year = elemYear.getTextContent().trim();
      		if (year.equals("")) throw new Exception("Nombre vacío");
      	}
      	catch (Exception ex) {
         		CommonMML.listErrores.add(new WrongFile(url, "Problema leyendo 'Year' ("+ex+")"));
      		return;  // si se produce cualquier tipo de excepción, hay un error y se termina
      	}

        doc2 = CommonMML.mapDocs.get(year);

        // si no lo tenemos, lo añadimos. si ya lo tenemos (imposible), no lo añadimos y volvemos

        if (doc2 == null)  CommonMML.mapDocs.put(year,doc);  // almacenar el Document del año leído
        else {
					CommonMML.logMML(fich+" ya fue incluido");
					return;
		}

    	// buscar recursivamente los nuevos ficheros que hay en el que acabamos de leer

    	// conseguir la lista de enlaces MML

    	NodeList nlMMLs = doc.getElementsByTagName("MML");

    	for (int x=0; x < nlMMLs.getLength(); x++) {

      		 // procesar cada uno de los encontrados

      		 Element elemMML = (Element) nlMMLs.item(x);

      		 String newURL = elemMML.getTextContent();  // CUIDADO

  			 if (newURL.equals("")) continue;

  			 String laBase, elFichero;

			 if (newURL.startsWith("http://"))   {  // si es absoluta la dividimos entre la base y el fichero
				    laBase = newURL.substring(0,newURL.lastIndexOf('/')+1);
				    elFichero = newURL.substring(newURL.lastIndexOf('/')+1);
			 }
			 else {
				    laBase = urlBase;
				    elFichero = newURL;
			 }

			 // si ya hemos leído este fichero en el pasado lo saltamos
			 if (CommonMML.listaFicherosProcesados.contains(laBase+elFichero)) continue;

           // analizamos el nuevo fichero
           this.buscarFicheros(laBase, elFichero, conf);
        }
    }
}
