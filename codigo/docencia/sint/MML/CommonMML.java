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


// cosas comunes para varias clases

package docencia.sint.MML;

import java.io.PrintWriter;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class CommonMML {

    public static final String CREATED = "2017";
    public static final String CURSO = "2017-2018";
    static final String MSGTITLE = "Servicio Movies";
    static final String MSGINICIAL = "Servicio de consulta de películas";

    static HashMap<String,Document> mapDocs = new HashMap<String,Document>();   // el hashmap de documentos
    
    static XPathFactory xpathFactory = XPathFactory.newInstance();
    static XPath xpath = xpathFactory.newXPath();
    
    static int real=1; // para indicar si los resultados son reales (real=1) o inventados (real=0)
    
	private static Logger logger = null;  // el objeto Logger
	
	// para inicializar el objeto Logger
	
	public static void initLoggerMML (Class c) {
		logger = LogManager.getLogger(c);
	}
	
	 // para imprimir con el Logger en el sintprof.log
	 
	 public static void logMML (String msg) {
		logger.info("## MML ## "+msg);
	 }
	 
    
    // para imprimir la cabecera de cada respuesta HTML
    
	public static void printHead(PrintWriter out) {
		out.println("<head><meta charset='utf-8'/>");
		out.println("<title>"+CommonMML.MSGTITLE+"</title>");
		out.println("<link rel='stylesheet'  type='text/css' href='css/mml.css'/></head>");
	}
    
    
	 
	 // comprueba si un actor/actriz tiene algún óscar
	 
	 public static Boolean hasActorOscar (String nombreActor) {
		Document doc;
		NodeList nlOscares=null;
    		
    		String targetAct = "/Movies/Pais/Pelicula/Reparto[Nombre='"+nombreActor+"']/Oscar";  // óscares de ese actor
    		
		Collection<Document> collectionDocs = CommonMML.mapDocs.values();
		Iterator<Document> iter = collectionDocs.iterator();
		
		while (iter.hasNext()) {   // iteramos sobre todos los años
	
			doc = iter.next();
	
	    		try {  // obtenemos los óscares de ese actor este año
	    			nlOscares = (NodeList)CommonMML.xpath.evaluate(targetAct, doc, XPathConstants.NODESET);
	    		}
			catch (XPathExpressionException ex) {CommonMML.logMML(ex.toString()); return false;}
			catch (Exception ex) {CommonMML.logMML(ex.toString()); return false;}
	    		
	    		if (nlOscares.getLength() > 0) return true;  // si este año tuvo algún óscar, ya devolvemos true
		}
		
   	    return false;
	 }
	 
	 
	 // calcula el número de pelis producidas en un país
	 
	 public static int getNumPeliculasInPais (String pais) {
			Document doc;
			NodeList nlPeliculas=null;
			int numPeliculas = 0;
	    		
	    		String targetAct = "/Movies/Pais[@pais='"+pais+"']/Pelicula";  // películas producidas en ese país
	    		
			Collection<Document> collectionDocs = CommonMML.mapDocs.values();
			Iterator<Document> iter = collectionDocs.iterator();
			
			while (iter.hasNext()) {   // iteramos sobre todos los años
		
				doc = iter.next();
		
		    		try {  // obtenemos las películas producidas en ese país en este año
		    			nlPeliculas = (NodeList)CommonMML.xpath.evaluate(targetAct, doc, XPathConstants.NODESET);
		    		}
				catch (XPathExpressionException ex) {CommonMML.logMML(ex.toString()); return 0;}
				catch (Exception ex) {CommonMML.logMML(ex.toString()); return 0;}
		    		
		    		numPeliculas += nlPeliculas.getLength();  // le sumamos las de este año
			}
			
	   	    return numPeliculas;
		 }
    
}



