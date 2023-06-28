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

// cosas comunes para varias clases

package docencia.sint.MML2021;

import java.io.PrintWriter;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import docencia.sint.Common.WrongFile;

public class CommonMML {

    public static final String MMLCSS = "css/mml2021.css";

		static ArrayList<String> listaFicherosProcesados = new ArrayList<String>();
    static ArrayList<WrongFile> listWarnings = new ArrayList<WrongFile>();
    static ArrayList<WrongFile> listErrores= new ArrayList<WrongFile>();
    static ArrayList<WrongFile> listErroresfatales = new ArrayList<WrongFile>();

    static HashMap<String,Document> mapDocs = new HashMap<String,Document>();   // el hashmap de documentos

    static XPathFactory xpathFactory = XPathFactory.newInstance();
    static XPath xpath = xpathFactory.newXPath();

    static int real=1; // para indicar si los resultados son reales (real=1) o inventados (real=0)

		private static Logger logger = null;  // el objeto Logger

		// para inicializar el objeto Logger

		public static void initLoggerMML (Class<P2M> c) {
			logger = LogManager.getLogger(c);
		}

		// para imprimir con el Logger en el sintprof.log

		public static void logMML (String msg) {
			logger.info("## MML ## "+msg);
		}



		// para imprimir la cabecera de cada respuesta HTML

		public static void printHead(String language, PrintWriter out) {
			out.println("<head><meta charset='utf-8'/>");
			// 001 = "Servicio de consulta de películas"
			out.println("<title>"+MsMML.getMsg(MsMML.MML001, language)+"</title>");
			out.println("<link rel='stylesheet'  type='text/css'  href='"+CommonMML.MMLCSS+"'/></head>");
		}

}
