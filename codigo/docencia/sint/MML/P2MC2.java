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


// consulta 2
// películas en un idioma (S1), de un actor (S2), no producidas en un país (S3)

package docencia.sint.MML;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import docencia.sint.Common.CommonSINT;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;


   // MÉTODOS PARA LA CONSULTA 2
   
public class P2MC2 {

    // F21: método que imprime o devuelve la lista de idiomas
	 
    public static void doGetF21Langs (HttpServletRequest request, HttpServletResponse response) throws IOException {

	    ArrayList<String> langs = P2MC2.getLangsF21();   // se pide la lista de idiomas
	
	    if (langs == null) {
	    		CommonSINT.doBadRequest("problema obteniendo los idiomas", request, response);
	    		return;
	    	}
	      	
	    	if (langs.size() == 0) {
	    		CommonSINT.doBadRequest("no hay idiomas", request, response);
	    		return;
	    	}
	
	    	response.setCharacterEncoding("utf-8");
	    	PrintWriter out = response.getWriter();
	
	    	String auto = request.getParameter("auto");
	
	    	if (auto == null) {
	    		out.println("<html>");
	    		CommonMML.printHead(out);
	    		out.println("<body>");
                        
	    		out.println("<h2>"+CommonMML.MSGINICIAL+"</h2>");
                        out.println("<h3>Consulta 2</h3>");
	
	    		out.println("<h3>Selecciona un idioma:</h3>");
	
	    		out.println("<form>");
			out.println("<input type='hidden' name='p' value='"+CommonSINT.PASSWD+"'>");  
	    		out.println("<input type='hidden' name='pfase' value='22'>");  // de aquí se pasa a la fase 22
	
	    		for (int x=0; x < langs.size(); x++) 
	    			out.println("<input type='radio' name='plang' value='"+langs.get(x)+"' checked> "+(x+1)+".- "+langs.get(x)+"<BR>"); 
			
	    		out.println("<p><input class='enviar' type='submit' value='Enviar'>");
	    		out.println("<input class='back' type='submit' value='Atrás' onClick='document.forms[0].pfase.value=\"01\"'>");  // Atrás vuelve al inicio
	    		out.println("</form>");
	    		
	    		CommonSINT.printFoot(out, CommonMML.CREATED);
	    		out.println("</body></html>");
	    	}
	    	else {
	    		out.println("<?xml version='1.0' encoding='utf-8'?>");
	    		out.println("<langs>");
	
	    		for (int x=0; x < langs.size(); x++) 
	    			out.println("<lang>"+langs.get(x).trim()+"</lang>");
	
	    		out.println("</langs>");
	    	}
    }


    // método auxiliar del anterior, que devuelve la lista de idiomas

    private static ArrayList<String> getLangsF21 () {	
	    	if (CommonMML.real == 0)
	    		return new ArrayList<String>(Arrays.asList("us", "uk","es","de"));
	    	else {
	    		ArrayList<String> listaLangs = new ArrayList<String>();
	    		
	        String targetLang = "//Movies/Pais/@lang";               // langs por defecto
	        String targetLangs = "//Movies/Pais/Pelicula/@langs";    // langs en los atributos langs de las pelis
			Document doc;
			NodeList nlLangs=null;  	
			Attr attrLang, attrLangs;
			String idioma, listaIdiomas;
	       
			Collection<Document> collectionDocs = CommonMML.mapDocs.values();
			Iterator<Document> iter = collectionDocs.iterator();
			
			while (iter.hasNext()) {   // iteramos sobre todos los años
		
				doc = iter.next();
		
		    		try {  // obtenemos los atributos lang
		    			nlLangs = (NodeList)CommonMML.xpath.evaluate(targetLang, doc, XPathConstants.NODESET);
		    		}
				catch (XPathExpressionException ex) {CommonMML.logMML(ex.toString()); return null;}
		    		
				for (int z=0; z < nlLangs.getLength(); z++) {
					attrLang = (Attr)nlLangs.item(z);   // estudiamos cada lang
					idioma = attrLang.getValue();
					if (!listaLangs.contains(idioma)) listaLangs.add(idioma);
				}
				
				
		    		try {  // obtenemos los atributos langs
		    			nlLangs = (NodeList)CommonMML.xpath.evaluate(targetLangs, doc, XPathConstants.NODESET);
		    		}
				catch (XPathExpressionException ex) {CommonMML.logMML(ex.toString()); return null;}
		    		
				for (int z=0; z < nlLangs.getLength(); z++) {
					attrLangs = (Attr)nlLangs.item(z);   // estudiamos cada lang
					listaIdiomas = attrLangs.getValue();
					String[] idiomas = listaIdiomas.split(" "); 
					
					for (int i = 0; i < idiomas.length; i++){
						if (!listaLangs.contains(idiomas[i])) listaLangs.add(idiomas[i]);
					}
				}
								
			}
	    		
	    		Collections.sort(listaLangs, Collections.reverseOrder());  // alfabéticamente en orden inverso
	    		return listaLangs;
	    	}
    }






    // F22: método que imprime o devuelve la lista de actores que han hecho peliculas disponibles en un determinado idioma (parámetro lang)

    public static void doGetF22Acts (HttpServletRequest request, HttpServletResponse response) throws IOException {

    	ArrayList<Actor> actores;

    	String plang = request.getParameter("plang");
    	if (plang == null) {
    		CommonSINT.doBadRequest("no param:plang", request, response);
    		return;
    	}

    	actores = P2MC2.getActoresF22(plang);  // se pide la lista de peliculas del año seleccionado

    	if (actores == null) {
    		CommonSINT.doBadRequest("problema leyendo los actores con el idioma "+plang, request, response);
    		return;
    	}

    	response.setCharacterEncoding("utf-8");
    	PrintWriter out = response.getWriter();

    	String auto = request.getParameter("auto");

    	if (auto == null) {
                out.println("<html>");
    		CommonMML.printHead(out);
    		out.println("<body>");
 
    		out.println("<h2>"+CommonMML.MSGINICIAL+"</h2>");
                out.println("<h3>Consulta 2: ");
    		out.println("Idioma="+plang+"</h3>");

    		out.println("<h3>Selecciona un Actor/Actriz:</h3>");

    		out.println("<form>");
    		out.println("<input type='hidden' name='p' value='"+CommonSINT.PASSWD+"'>");  
    		out.println("<input type='hidden' name='plang' value='"+plang+"'>");
    		out.println("<input type='hidden' name='pfase' value='23'>");  // de aquí se pasa a la fase 23

    		String tieneOscar;
    		for (int x=0; x < actores.size(); x++) {
    			if (actores.get(x).hasOscar()) tieneOscar="con óscar";
    			else tieneOscar = "sin óscar";
    			
    			out.println("<input type='radio' name='pact' value='"+actores.get(x).getNombre()+"' checked> "+(x+1)+".- "+
    					     actores.get(x).getNombre()+"  ("+actores.get(x).getCiudad()+")  -- "+tieneOscar+"<BR>"); 
    		}

    		out.println("<p><input class='enviar' type='submit' value='Enviar'>");
    		out.println("<input class='back' type='submit' value='Atrás' onClick='document.forms[0].pfase.value=\"21\"'>");  // Atrás vuelve a la fase 21
    		out.println("<input class='home' type='submit' value='Inicio' onClick='document.forms[0].pfase.value=\"01\"'>");
    		out.println("</form>");

    		CommonSINT.printFoot(out, CommonMML.CREATED);
    		out.println("</body></html>");
    	}
    	else {
    		out.println("<?xml version='1.0' encoding='utf-8'?>");

    		out.println("<acts>");

    		for (int x=0; x < actores.size(); x++) 
    			out.println("<ac ciudad='"+actores.get(x).getCiudad()+"' oscar='"+actores.get(x).hasOscar()+"'>"+actores.get(x).getNombre()+"</ac>");

    		out.println("</acts>");
    	}
    }


    // método auxiliar del anterior, que calcula la lista de actores de peliculas en un determinado idioma

    public static ArrayList<Actor> getActoresF22 (String lang) {
    		if (CommonMML.real == 0)
    			return new ArrayList<Actor>(Arrays.asList(new Actor("Harrison Ford","Chicago, EEUU", true), new Actor("Jordi Mollá", "Madrid, España", true),
    					new Actor("Roberto Benini", "Roma, Italia", false)));
    		else {	
    			Document doc;
    			NodeList nlActs=null;
    			Element elemAct;
    			Actor act;
    			String nombreAct, ciudadAct;
    			Boolean oscarAct;
        		ArrayList<Actor> listaActores = new ArrayList<Actor>();
        		
        		String targetAct1 = "/Movies/Pais[@lang='"+lang+"']/Pelicula[not(@langs)]/Reparto";  // actores en peliculas sin @langs de un país con lang por defecto 
        		String targetAct2 = "/Movies/Pais/Pelicula[contains(@langs,'"+lang+"')]/Reparto";    // actores en peliculas con lang entre sus langs
        		
			Collection<Document> collectionDocs = CommonMML.mapDocs.values();
			Iterator<Document> iter = collectionDocs.iterator();
			
			while (iter.hasNext()) {   // iteramos sobre todos los años
		
				doc = iter.next();
		
		    		try {  // obtenemos los actores que nos interesan
		    			nlActs = (NodeList)CommonMML.xpath.evaluate(targetAct1+" | "+targetAct2, doc, XPathConstants.NODESET);
		    		}
				catch (XPathExpressionException ex) {CommonMML.logMML(ex.toString()); return null;}
				catch (Exception ex) {CommonMML.logMML(ex.toString()); return null;}
		    		
				for (int z=0; z < nlActs.getLength(); z++) {
					elemAct  = (Element)nlActs.item(z);   // estudiamos cada actor
					nombreAct = CommonSINT.getTextContentOfChild(elemAct, "Nombre");
					ciudadAct = CommonSINT.getTextContent(elemAct);
					if (CommonMML.hasActorOscar(nombreAct)) oscarAct=true;
					else oscarAct = false;
					
					act = new Actor(nombreAct,ciudadAct, oscarAct);
					if (!act.isContainedInList(listaActores)) 
						listaActores.add(act);
				}
			}
			
    			Collections.sort(listaActores, Actor.OSCAR);  // alfabéticamente en orden inverso
	   	    return listaActores;
    		}
	    		
    	
    }







    // F23: método que imprime o devuelve la lista de paises que han producido peliculas en un idioma en las que trabaje un actor

    public static void doGetF23Paises (HttpServletRequest request, HttpServletResponse response) throws IOException {

	    	String plang = request.getParameter("plang");
	
	    	if (plang == null) {
	    		CommonSINT.doBadRequest("no param:plang", request, response);
	    		return;
	    	}
	
	    	String pact = request.getParameter("pact");
	
	    	if (pact == null) {
	    		CommonSINT.doBadRequest("no param:pact", request, response);
	    		return;
	    	}
	
	    	
	    	ArrayList<Pais> paises = P2MC2.getPaisesF23(plang, pact);   // pedimos la lista de actores de una película en un idioma
	
	    	if (paises == null) {
	    		CommonSINT.doBadRequest("problema obteniendo los países con el idioma ("+plang+") y el actor/actriz ("+pact+")", request, response);
	    		return;
	    	}
	
	    	response.setCharacterEncoding("utf-8");
	    	PrintWriter out = response.getWriter();
	
	    	String auto = request.getParameter("auto");

	    	if (auto == null) {
	       	        out.println("<html>");
	    		CommonMML.printHead(out);
	    		out.println("<body>");
	   
	    		out.println("<h2>"+CommonMML.MSGINICIAL+"</h2>");
                        out.println("<h3>Consulta 2: ");
	    		out.println("Idioma="+plang+", Actor/Actriz="+pact+"</h3>");
	
	    		out.println("<h3>Selecciona un país:</h3>");
	
	    		out.println("<form>");
	    		out.println("<input type='hidden' name='p' value='"+CommonSINT.PASSWD+"'>");  
	    		out.println("<input type='hidden' name='plang' value='"+plang+"'>");
	    		out.println("<input type='hidden' name='pact' value='"+pact+"'>");
	    		out.println("<input type='hidden' name='pfase' value='24'>");  // de aquí se pasa a la fase 24
	
	    		for (int x=0; x < paises.size(); x++) {
	    			out.println("<input type='radio' name='ppais' value='"+paises.get(x).getNombre()+"' checked> "+(x+1)+".- "+
	    					     paises.get(x).getNombre()+"  ("+paises.get(x).getNumPeliculas()+" películas)  -- idioma por defecto='"+paises.get(x).getLang()+"'<BR>"); 
	    		}
	
	    		out.println("<p><input class='enviar' type='submit' value='Enviar'>");
	    		out.println("<input class='back' type='submit' value='Atrás' onClick='document.forms[0].pfase.value=\"22\"'>");  // Atrás vuelve a la fase 22
	    		out.println("<input class='home' type='submit' value='Inicio' onClick='document.forms[0].pfase.value=\"01\"'>");
	    		out.println("</form>");
	    		
	    		CommonSINT.printFoot(out, CommonMML.CREATED);
	    		out.println("</body></html>");
	    	}
	    	else {
	    		out.println("<?xml version='1.0' encoding='utf-8'?>");
	    		out.println("<paises>");
	
	    		for (int x=0; x < paises.size(); x++) 
	    			out.println("<pais lang='"+paises.get(x).getLang()+"'  num='"+paises.get(x).getNumPeliculas()+"'>"+paises.get(x).getNombre()+"</pais>");
	
	    		out.println("</paises>");
	    	}
    }



    // método auxiliar del anterior, que obtiene la lista de países de un idioma+actor

    public static ArrayList<Pais> getPaisesF23 (String lang, String act) {
	    	if (CommonMML.real == 0)
	    		return new ArrayList<Pais>(Arrays.asList(new Pais("España","es", 4), new Pais("EEUU", "en",3), new Pais("UK", "en",3)));
	    	else {
	    		Document doc;
    			NodeList nlPaises=null;
    			Element elemPais;
    			String nombrePais, langDefault;
    			int numPeliculas;
    			Pais pais;
        		ArrayList<Pais> listaPaises = new ArrayList<Pais>();
        		
        		String targetAct1 = "/Movies/Pais[(@lang='"+lang+"') and (Pelicula[not(@langs)]/Reparto/Nombre = '"+act+"')]";  // países con lang por defecto y que tienen pelis sin langs en las que trabaja el actor
        		String targetAct2 = "/Movies/Pais[Pelicula[contains(@langs,'"+lang+"')]/Reparto/Nombre = '"+act+"']";   // países con peliculas (que tienen lang entre los langs ) en las que trabaja el actor
        		
			Collection<Document> collectionDocs = CommonMML.mapDocs.values();
			Iterator<Document> iter = collectionDocs.iterator();
			
			while (iter.hasNext()) {   // iteramos sobre todos los años
		
				doc = iter.next();
		
		    		try {  // obtenemos los países que nos interesan
		    			nlPaises = (NodeList)CommonMML.xpath.evaluate(targetAct1+" | "+targetAct2, doc, XPathConstants.NODESET);
		    		}
				catch (XPathExpressionException ex) {CommonMML.logMML(ex.toString()); return null;}
				catch (Exception ex) {CommonMML.logMML(ex.toString()); return null;}
		    		
				for (int z=0; z < nlPaises.getLength(); z++) {
					elemPais  = (Element)nlPaises.item(z);   // estudiamos cada país
					nombrePais = elemPais.getAttribute("pais");
					langDefault = elemPais.getAttribute("lang");
					numPeliculas = CommonMML.getNumPeliculasInPais(nombrePais);
							
					pais = new Pais(nombrePais,langDefault, numPeliculas);
					if (!pais.isContainedInList(listaPaises))
						listaPaises.add(pais);
				}
			}
			
    			Collections.sort(listaPaises);  // alfabéticamente en orden inverso
	   	    return listaPaises;
	    		
	    	}
    }









    // F24: método que imprime las peliculas de un actor en un idioma no producidas en un pais

    public static void doGetF24Pelis (HttpServletRequest request, HttpServletResponse response) throws IOException {
		
	    	String plang = request.getParameter("plang");
	
	    	if (plang == null) {
	    		CommonSINT.doBadRequest("no param:plang", request, response);
	    		return;
	    	}
	
	    	String pact = request.getParameter("pact");
	
	    	if (pact == null) {
	    		CommonSINT.doBadRequest("no param:pact", request, response);
	    		return;
	    	}
	
	    	String ppais = request.getParameter("ppais");
	
	    	if (ppais == null) {
	    		CommonSINT.doBadRequest("no param:ppais", request, response);
	    		return;
	    	}
	
	    	ArrayList<Pelicula> peliculas = P2MC2.getPeliculasF24(plang, pact, ppais);
	
	    	if (peliculas == null) {
	    		CommonSINT.doBadRequest("problema obteniendo las películas con el idioma ("+plang+"), el actor/actriz ("+pact+"), y el pais ("+ppais+")", request, response);
	    		return;
	    	}
		
	    	response.setCharacterEncoding("utf-8");
	    	PrintWriter out = response.getWriter();
	
	    	String auto = request.getParameter("auto");

	    	if (auto == null) {
	       	        out.println("<html>");
	    		CommonMML.printHead(out);
	    		out.println("<body>");
	  
	    		out.println("<h2>"+CommonMML.MSGINICIAL+"</h2>");
                        out.println("<h3>Consulta 2: ");
	    		out.println("Idioma="+plang+", Actor/Actriz="+pact+", Pais="+ppais+"</h3>");
	    		
	    		out.println("<h3>Estas son sus películas:</h3>");
	
	    		out.println("<ul>");
	
	    		for (int x=0; x < peliculas.size(); x++) 
	    			out.println(" <li>"+(x+1)+".- "+"<b>Película</b>="+peliculas.get(x).getTitulo()+", <b>IP</b>="+peliculas.get(x).getIP()+"<BR>"); 
	    		
	
	    		out.println("</ul>");
	
	    		out.println("<form>");
	    		out.println("<input type='hidden' name='p' value='"+CommonSINT.PASSWD+"'>");  
	    		out.println("<input type='hidden' name='plang' value='"+plang+"'>");
	    		out.println("<input type='hidden' name='pact' value='"+pact+"'>");
	    		out.println("<input type='hidden' name='pfase' value='01'>");
	
	    		out.println("<input class='back' type='submit' value='Atrás' onClick='document.forms[0].pfase.value=\"23\"'>");  // Atrás vuelve a la fase 23
	    		out.println("<input class='home' type='submit' value='Inicio' onClick='document.forms[0].pfase.value=\"01\"'>");
			
			  // out.println("<input type='submit' value='Next' onClick='document.forms[0].pfase.value=\"25\"'>");
					
					
	    		out.println("</form>");
	    		
	    		CommonSINT.printFoot(out, CommonMML.CREATED);
	    		out.println("</body></html>");
	    	}
	    	else {
	    		out.println("<?xml version='1.0' encoding='utf-8'?>");
	    		out.println("<titulos>");
	
	    		for (int x=0; x < peliculas.size(); x++) 
	    			out.println("<titulo ip='"+peliculas.get(x).getIP()+"'>"+peliculas.get(x).getTitulo()+"</titulo>");
	
	    		out.println("</titulos>"); 
	    	}
    }


    		
    // método auxiliar del anterior, que calcula las peliculas de un actor+lang producidas en un país

    public static  ArrayList<Pelicula> getPeliculasF24 (String lang, String act, String pais) {
	    	if (CommonMML.real == 0)
	    		return new ArrayList<Pelicula>(Arrays.asList(new Pelicula("Titanic","tit001"), new Pelicula("Torremolinos 73", "tor002"), new Pelicula("Superman", "sup003")));
	    	else {   		
	       	Document doc;
    			NodeList nlPeliculas=null;
    			Element elemPelicula;
    			Pelicula pelicula;
    			String titulo, ip;
        		ArrayList<Pelicula> listaPeliculas = new ArrayList<Pelicula>();
        		
        		String targetAct1 = "/Movies/Pais[(@lang = '"+lang+"') and (@pais = '"+pais+"')]/Pelicula[not(@langs) and (Reparto/Nombre = '"+act+"')]";  // películas de pais con lang por defecto, con pelis en las que trabaja act
        		String targetAct2 = "/Movies/Pais[@pais = '"+pais+"']/Pelicula[contains(@langs,'"+lang+"') and (Reparto/Nombre = '"+act+"')]";    // películas de pais, con pelis en las que trabaja act, con lang entre langs
        		
			Collection<Document> collectionDocs = CommonMML.mapDocs.values();
			Iterator<Document> iter = collectionDocs.iterator();
			
			while (iter.hasNext()) {   // iteramos sobre todos loso años
		
				doc = iter.next();
		
		    		try {  // obtenemos las películas que nos interesan
		    			nlPeliculas = (NodeList)CommonMML.xpath.evaluate(targetAct1+" | "+targetAct2, doc, XPathConstants.NODESET);
		    		}
				catch (XPathExpressionException ex) {CommonMML.logMML(ex.toString());}
				catch (Exception ex) {CommonMML.logMML(ex.toString());}
		    		
				for (int z=0; z < nlPeliculas.getLength(); z++) {
					elemPelicula  = (Element)nlPeliculas.item(z);   // estudiamos cada pelicula
					ip = elemPelicula.getAttribute("ip");
					titulo = CommonSINT.getTextContentOfChild(elemPelicula, "Titulo");
							
					pelicula = new Pelicula(titulo,ip);
					if (!pelicula.isContainedInList(listaPeliculas))
						listaPeliculas.add(pelicula);
				}
			}
			
  			Collections.sort(listaPeliculas, Pelicula.IP);  // alfabéticamente en orden inverso
	   	    return listaPeliculas;
	    	}
    }
    
    
    
    /* para el examen
     * 
     public static void doGetF15 (HttpServletRequest request, HttpServletResponse response) throws IOException {
	
	 		
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
	
		out.println("<html><head><meta charset='utf-8'/><title>"+Common.MSGTITLE+"</title></head><body>");
    		out.println("<h2>"+Common.MSGINICIAL+"</h2>");
    		
    		out.println("<h3>Los datos son:</h3>");



    		out.println("<form>");
    		out.println("<input type='hidden' name='p' value='"+Common.PASSWD+"'>");  

    		out.println("<input type='hidden' name='fase' value='0'><br>");

    		out.println("<input  class='home' type='submit' value='Inicio' onClick='document.forms[0].fase.value=\"0\"'>");
    		out.println("</form>");
    		
    		CommonMML.printFoot(out);
    		out.println("</body></html>");
		
		
     }
    	*/	
  
}