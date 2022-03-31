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


// consulta 1
// filmografía de un actor (S3), en una película (S2), de un año (S1)

package docencia.sint.MML;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Set;
import java.util.Collections;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import docencia.sint.Common.CommonSINT;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;


   // MÉTODOS PARA LA PRIMERA CONSULTA
   
public class P2MC1 {

    // F11: método que imprime o devuelve la lista de años

    public static void doGetF11Anios (HttpServletRequest request, HttpServletResponse response) throws IOException {

	    	ArrayList<String> anios = P2MC1.getAniosF11();   // se pide la lista de anios
	
	    	if (anios.size() == 0) {
	    		CommonSINT.doBadRequest("no hay años", request, response);
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
		    	out.println("<h3>Consulta 1</h3>");
	    		out.println("<h3>Selecciona un año:</h3>");
	
	    		out.println("<form>");
			out.println("<input type='hidden' name='p' value='"+CommonSINT.PASSWD+"'>");  
	    		out.println("<input type='hidden' name='pfase' value='12'>");  // de aquí se pasa a la fase 12
	
	    		for (int x=0; x < anios.size(); x++) {
	    			out.println("<input type='radio' name='panio' value='"+anios.get(x)+"' checked> "+(x+1)+".- "+anios.get(x)+"<BR>"); 
	    		}
			
	    		out.println("<p><input class='enviar'  type='submit' value='Enviar'>");
	    		out.println("<input class='back' type='submit' value='Atrás' onClick='document.forms[0].pfase.value=\"01\"'>");  // Atrás vuelve al inicio
	    		out.println("</form>");
	    		
	    		CommonSINT.printFoot(out, CommonMML.CREATED);
	    		out.println("</body></html>");
	    	}
	    	else {
	    		out.println("<?xml version='1.0' encoding='utf-8'?>");
	    		out.println("<anios>");
	
	    		for (int x=0; x < anios.size(); x++) 
	    			out.println("<anio>"+anios.get(x).trim()+"</anio>");
	
	    		out.println("</anios>");
	    	}
    }


    // método auxiliar del anterior, que calcula la lista de años

    private static ArrayList<String> getAniosF11 () {	
	    	if (CommonMML.real == 0)
	    		return new ArrayList<String>(Arrays.asList("2011", "2012","2013","2014"));
	    	else {
	    		ArrayList<String> listaAnios = new ArrayList<String>();
	    		
	    		// convertimos las claves del hashmap en una lista
	    		
	    		Set<String> setAnios = CommonMML.mapDocs.keySet();
	    		listaAnios.addAll(setAnios);
	    		
	    		Collections.sort(listaAnios);  // se ordenan alfabéticamente, que es equivalente a cronológicamente
	    		return listaAnios;
	    	}
    }






    // F12: método que imprime o devuelve la lista de peliculas de un año

    public static void doGetF12Peliculas (HttpServletRequest request, HttpServletResponse response) throws IOException {

	    	ArrayList<Pelicula> peliculas;
	
	    	String panio = request.getParameter("panio");
	    	if (panio == null) {
	    		CommonSINT.doBadRequest("no param:panio", request, response);
	    		return;
	    	}
	
	    	peliculas = P2MC1.getPeliculasF12(panio);  // se pide la lista de peliculas del año seleccionado
	
	    	if (peliculas == null) {
	    		CommonSINT.doBadRequest("el año "+panio+" no existe", request, response);
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
			out.println("<h3>Consulta 1:");
	    		out.println("Año="+panio+"</h3>");
	
	    		out.println("<h3>Selecciona una película:</h3>");
	
	    		out.println("<form>");
	    		out.println("<input type='hidden' name='p' value='"+CommonSINT.PASSWD+"'>");  
	    		out.println("<input type='hidden' name='panio' value='"+panio+"'>");
	    		out.println("<input type='hidden' name='pfase' value='13'>");  // de aquí se pasa a la fase 13
	
	    		for (int x=0; x < peliculas.size(); x++) 
	    			out.println("<input type='radio' name='ppelicula' value='"+peliculas.get(x).getTitulo()+"' checked> "+(x+1)+".- "+
	    					     peliculas.get(x).getTitulo()+" ("+peliculas.get(x).getDuracion()+" min.) ("+peliculas.get(x).getIdiomas()+")<BR>"); 
	
	    		out.println("<p><input class='enviar' type='submit' value='Enviar'>");
	    		out.println("<input class='back' type='submit' value='Atrás' onClick='document.forms[0].pfase.value=\"11\"'>");  // Atrás vuelve a la fase 11
	    		out.println("<input class='home' type='submit' value='Inicio' onClick='document.forms[0].pfase.value=\"01\"'>");
	    		out.println("</form>");
	
	    		CommonSINT.printFoot(out, CommonMML.CREATED);
	    		out.println("</body></html>");
	    	}
	    	else {
	    		out.println("<?xml version='1.0' encoding='utf-8'?>");
	
	    		out.println("<peliculas>");
	
	    		for (int x=0; x < peliculas.size(); x++) 
	    			out.println("<pelicula duracion='"+peliculas.get(x).getDuracion()+"' langs='"+peliculas.get(x).getIdiomas()+"'>"+peliculas.get(x).getTitulo()+"</pelicula>");
	
	    		out.println("</peliculas>");
	    	}
    }


    // método auxiliar del anterior, que calcula la lista de peliculas de un año dado
    // no rellena los óscares, no le hace falta

    private static ArrayList<Pelicula> getPeliculasF12 (String anio) {
	    	if (CommonMML.real == 0)
	    		return new ArrayList<Pelicula>(Arrays.asList(new Pelicula("P1",100,"es"), new Pelicula("P2",110,"es"), new Pelicula ("P3",120,"es")));
	    	else {	    
	    		Document doc = CommonMML.mapDocs.get(anio);
	    		if (doc == null) return null;  // no existe ese año
	
	    		ArrayList<Pelicula> listaPeliculas = new ArrayList<Pelicula>();
	
	    		NodeList nlPeliculas = doc.getElementsByTagName("Pelicula");  // pedimos el NodeList con todas las peliculas
	
	    		for (int y=0; y < nlPeliculas.getLength(); y++) {
	    			Element elemPelicula = (Element)nlPeliculas.item(y);  // estudiamos una pelicula
	
	    			Element elemPais = (Element)elemPelicula.getParentNode();  // pedimos el País al que pertenece
	    			String langPais = elemPais.getAttribute("lang");
	
	    			NodeList nlTitulo = elemPelicula.getElementsByTagName("Titulo");  // obtenemos el titulo de la pelicula
	    			Element elemTitulo = (Element)nlTitulo.item(0);
	    			String titulo = elemTitulo.getTextContent().trim();
	    			
	    			NodeList nlDuracion = elemPelicula.getElementsByTagName("Duracion");  // obtenemos la duracion de la pelicula
	    			Element elemDuracion = (Element)nlDuracion.item(0);
	    			String duracion = elemDuracion.getTextContent().trim();
	    			
	    			String langsPeli = elemPelicula.getAttribute("langs");
	    			String langs;
	    			
	    			if (langsPeli.equals("")) langs = langPais;
	    			else langs = langsPeli;
	    			
	    			listaPeliculas.add(new Pelicula(titulo, Integer.parseInt(duracion), langs));  // creamos y añadimos la pelicula (sin los oscares, no hacen falta)
	    		}
	
	    		Collections.sort(listaPeliculas);  // orden por defecto, por duración de la peli, a igual duración orden alfabético inverso
	
	    		return listaPeliculas;
	    	}
    }







    // F13: método que imprime o devuelve la lista de actores de una película de un año

    public static void doGetF13Actores (HttpServletRequest request, HttpServletResponse response) throws IOException {

	    	String panio = request.getParameter("panio");
	
	    	if (panio == null) {
	    		CommonSINT.doBadRequest("no param:panio", request, response);
	    		return;
	    	}
	
	    	String ppelicula = request.getParameter("ppelicula");
	
	    	if (ppelicula == null) {
	    		CommonSINT.doBadRequest("no param:ppelicula", request, response);
	    		return;
	    	}
	
	    	
	    	ArrayList<Actor> actores = P2MC1.getActoresF13(panio, ppelicula);   // pedimos la lista de actores de una película de un año
	
	    	if (actores == null) {
	    		CommonSINT.doBadRequest("el 'año' ("+panio+") o la 'película' ("+ppelicula+") no existen", request, response);
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
			out.println("<h3>Consulta 1: ");
	    		out.println("Año="+panio+", Película="+ppelicula+"</h3>");
	
	    		out.println("<h3>Selecciona un actor:</h3>");
	
	    		out.println("<form>");
	    		out.println("<input type='hidden' name='p' value='"+CommonSINT.PASSWD+"'>");  
	    		out.println("<input type='hidden' name='ppelicula' value='"+ppelicula+"'>");
	    		out.println("<input type='hidden' name='panio' value='"+panio+"'>");
	    		out.println("<input type='hidden' name='pfase' value='14'>");  // de aquí se pasa a la fase 14
	
	    		for (int x=0; x < actores.size(); x++) {
	    			out.println("<input type='radio' name='pact' value='"+actores.get(x).getNombre()+"' checked> "+(x+1)+".- "+
	    					      actores.get(x).getNombre()+" ("+actores.get(x).getCiudad()+")<BR>"); 
	    		}
	
	    		out.println("<p><input class='enviar' type='submit' value='Enviar'>");
	    		out.println("<input class='back' type='submit' value='Atrás' onClick='document.forms[0].pfase.value=\"12\"'>");  // Atrás vuelve a la fase 12
	    		out.println("<input class='home' type='submit' value='Inicio' onClick='document.forms[0].pfase.value=\"01\"'>");
	    		out.println("</form>");
	    		
	    		CommonSINT.printFoot(out, CommonMML.CREATED);
	    		out.println("</body></html>");
	    	}
	    	else {
	    		out.println("<?xml version='1.0' encoding='utf-8'?>");
	    		out.println("<reparto>");
	
	    		for (int x=0; x < actores.size(); x++) 
	    			out.println("<act ciudad='"+actores.get(x).getCiudad()+"'>"+actores.get(x).getNombre()+"</act>");
	
	    		out.println("</reparto>");
	    	}
    }



    // método auxiliar del anterior, que calcula la lista de actores de una película+año
    // si devuelve null es que no se encontró la película o el año

    private static ArrayList<Actor> getActoresF13 (String anio, String pelicula) {
	    	if (CommonMML.real == 0)
	    		return new ArrayList<Actor>(Arrays.asList(new Actor("Harrison Ford","Chicago, EEUU", false), new Actor("Jordi Mollá", "Madrid, España", false),
	    				new Actor("Roberto Benini", "Roma, Italia", false)));
	    	else {
	    		Document doc = CommonMML.mapDocs.get(anio);
	    		if (doc == null) {
	    			CommonMML.logMML("No hay doc para el año "+anio);
	    			return null;  // no existe ese año
	    		}
	
	    		ArrayList<Actor> listaActores = new ArrayList<Actor>();  // lista de estructuras de actores a devolver
	
	        	String xpathTarget = ".//Reparto[../Titulo[text() = '"+pelicula+"']]";
	        NodeList nlActores=null;
	    		
	    		try {  // obtenemos los actores de la pelicula cuyo nombre sea el seleccionado
	    	        nlActores = (NodeList)CommonMML.xpath.evaluate(xpathTarget, doc, XPathConstants.NODESET);
			}
			catch (XPathExpressionException e) {CommonMML.logMML(e.toString()); return null;}
	    		
	    		if (nlActores.getLength() == 0) {
	    			CommonMML.logMML("No hay actores para el año "+anio);
	    			return null;  // no hay actores para ese año????
	    		}
	
			for (int t=0; t < nlActores.getLength(); t++) {
				Element elemActor = (Element)nlActores.item(t);  // procesamos cada actor
	
				NodeList nlNombres = elemActor.getElementsByTagName("Nombre");  // averiguamos el nombre del actor
				Element elemNombre = (Element)nlNombres.item(0);
				String nombre = elemNombre.getTextContent().trim();
	
				String ciudad = CommonSINT.getTextContent(elemActor);  // conseguimos la ciudad del actor, si existe
	
				Actor actorStruct = new Actor(nombre, ciudad);  
	
				// comprobamos si este actor ya figura incluido en la lista (por si acaso)

				if (!actorStruct.isContainedInList(listaActores)) 
					listaActores.add(actorStruct);
				
			}
	    		
	    		// si la lista de actores sigue vacía es que no hemos encontrado la película
	
	    		if (listaActores.size() == 0) return null; 
	
	    		Collections.sort(listaActores);  // ordenamos la lista alfabéticamente
	    		return listaActores;
	    	}
    }









    // F14: método que imprime la filmografía de un actor (S3) de una película (S2) de un año (S1)

    public static void doGetF14Film (HttpServletRequest request, HttpServletResponse response) throws IOException {
		
	    	String panio = request.getParameter("panio");
	
	    	if (panio == null) {
	    		CommonSINT.doBadRequest("no param:panio", request, response);
	    		return;
	    	}
	
	    	String ppelicula = request.getParameter("ppelicula");
	
	    	if (ppelicula == null) {
	    		CommonSINT.doBadRequest("no param:ppelicula", request, response);
	    		return;
	    	}
	
	    	String pact = request.getParameter("pact");
	
	    	if (pact == null) {
	    		CommonSINT.doBadRequest("no param:pact", request, response);
	    		return;
	    	}
	
	    	Filmografia filmografia = P2MC1.getFilmografiaF14(panio, ppelicula, pact);
	
	    	if (filmografia == null) {
	    		CommonSINT.doBadRequest("el 'anio' ("+panio+"), o la 'pelicula' ("+ppelicula+"), o el 'act' ("+pact+") no existen", request, response);
	    		return;
	    	}

		Film film;
		
	    	response.setCharacterEncoding("utf-8");
	    	PrintWriter out = response.getWriter();
	
	    	String auto = request.getParameter("auto");
	
	    	if (auto == null) {
			out.println("<html>");
	    		CommonMML.printHead(out);
	    		out.println("<body>");
	  
	    		out.println("<h2>"+CommonMML.MSGINICIAL+"</h2>");
			out.println("<h3>Consulta 1: ");
	    		out.println("Año="+panio+", Película="+ppelicula+", Act="+pact+"</h3>");
	
	    		out.println("<h3>El personaje es: "+filmografia.getPersonaje()+"</h3>");
	    		
	    		out.println("<h3>Esta es su filmografía:</h3>");
	
	    		out.println("<ul>");
	
	    		for (int x=0; x < filmografia.getFilms().size(); x++) {
	    			film = filmografia.getFilms().get(x);
	    			if (film.getOscar().equals("")) 
	    				out.println(" <li>"+(x+1)+".- "+"<b>Título</b>="+film.getTitulo()+"<BR>"); 
	    			else 
	    				out.println(" <li>"+(x+1)+".- "+"<b>Título</b>="+film.getTitulo()+", <b>Óscar</b>="+film.getOscar()+"<BR>"); 
	    		}
	
	    		out.println("</ul>");
	
	    		out.println("<form>");
	    		out.println("<input type='hidden' name='p' value='"+CommonSINT.PASSWD+"'>");  
	    		out.println("<input type='hidden' name='ppelicula' value='"+ppelicula+"'>");
	    		out.println("<input type='hidden' name='panio' value='"+panio+"'>");
	    		out.println("<input type='hidden' name='pfase' value='0'>");
	
	    		out.println("<input class='back' type='submit' value='Atrás' onClick='document.forms[0].pfase.value=\"13\"'>");  // Atrás vuelve a la fase 13
	    		out.println("<input class='home' type='submit' value='Inicio' onClick='document.forms[0].pfase.value=\"01\"'>");
			
			   		// out.println("<input type='submit' value='Next' onClick='document.forms[0].pfase.value=\"15\"'>");
					
					
	    		out.println("</form>");
	    		
	    		CommonSINT.printFoot(out, CommonMML.CREATED);
	    		out.println("</body></html>");
	    	}
	    	else {
	    		out.println("<?xml version='1.0' encoding='utf-8'?>");
	    		out.println("<filmografia nombre='"+pact+"' personaje='"+filmografia.getPersonaje()+"'>");
	
	    		for (int x=0; x < filmografia.getFilms().size(); x++) {
	    			film = filmografia.getFilms().get(x);
	    			if (film.getOscar().equals("")) out.println("<film>"+film.getTitulo()+"</film>");
	    			else out.println("<film oscar='"+film.getOscar()+"'>"+film.getTitulo()+"</film>");
	    		}
	
	    		out.println("</filmografia>"); 
	    	}
    }


    		
    // método auxiliar del anterior, que calcula la filmografía de un actor+pelicula+año
    // si devuelve null es que no se encontró el año o la película o el actor

    private static Filmografia getFilmografiaF14 (String anio, String pelicula, String act) {
	    	if (CommonMML.real == 0)
	    		return new Filmografia("Gary Cooper", "asterix", new ArrayList<Film>(Arrays.asList(new Film("F1", "Principal"), new Film("F2",""), new Film ("F3","Secundario"))));
	    	else {
    		
	    		// primero conseguimos el personaje que interpretó en la pelicula seleccionada
	       	Document doc = CommonMML.mapDocs.get(anio);
	       	
	    		if (doc == null) {
	    			CommonMML.logMML("No hay doc para el año "+anio);
	    			return null;  // no existe ese año
	    		}
	
	    		NodeList nlActores=null;
	    		Element elemPersonaje;
	    		String personaje;
	    		String xpathTarget= ".//Personaje[(../Nombre[text() = '"+act+"']) and (../../Titulo[text() = '"+pelicula+"']) ]";  
	    		
	    		try {  // obtenemos los personajes de la pelicula seleccionada cuyo actor sea el seleccionado (solo debe haber uno)
	      	    	nlActores = (NodeList)CommonMML.xpath.evaluate(xpathTarget, doc, XPathConstants.NODESET);
		    }
		    catch (XPathExpressionException ex) {CommonMML.logMML("Excepción: "+ex.toString()+" con la expresión: "+xpathTarget); return null;}
	    		
       		if (nlActores.getLength() == 0) {
       			CommonMML.logMML("No hay Personaje para el año "+anio+" y la expresión:"+xpathTarget);
       			return null;  // no existe ese año
	    		}
	       		
	
			elemPersonaje = (Element)nlActores.item(0);  // procesamos el primero, sólo debe haber uno
		    personaje = elemPersonaje.getTextContent().trim();
				
	    		
	    		// y ahora vamos con toda su filmografía
	    			
	       	ArrayList<Film> listaFilms = new ArrayList<Film>();
	       		
	       	// iteramos sobre todos los docs
	       		
	    		Collection<Document> collectionDocs = CommonMML.mapDocs.values();
	    		Iterator<Document> iter = collectionDocs.iterator();
    		
	    		while (iter.hasNext()) {   // se busca en todos los años
    	
	    			doc = iter.next();
	
		    		NodeList nlPeliculas=null, nlTitulos, nlOscares=null, nlNombres;
		    		Element elemPelicula, elemTitulo, elemOscar, elemActor, elemNombre;
		    		String titulo, categoria, nombreActor, oscar;
	    		
	      	    xpathTarget = ".//Pelicula[Reparto[Nombre = '"+act+"']]";  
	      	    
		    		try {  // obtenemos las peliculas con el actor seleccionado
		      	    		nlPeliculas = (NodeList)CommonMML.xpath.evaluate(xpathTarget, doc, XPathConstants.NODESET);
			    }
			    catch (XPathExpressionException ex) {CommonMML.logMML("Excepción: "+ex.toString()+" con la expresión: "+xpathTarget); return null;}
		
		    		// procesamos todas las películas
		    		
		    		for (int t=0; t < nlPeliculas.getLength(); t++) {
		
		    			elemPelicula = (Element)nlPeliculas.item(t);  // estudiamos una película
					
				    nlTitulos = elemPelicula.getElementsByTagName("Titulo");
				    elemTitulo = (Element)nlTitulos.item(0);
				    titulo = elemTitulo.getTextContent().trim();
					    
				    xpathTarget = ".//Oscar";  
				    
			    		try {  // obtenemos los óscares de la pelicula en curso
			      	    	nlOscares = (NodeList)CommonMML.xpath.evaluate(xpathTarget, elemPelicula, XPathConstants.NODESET);
				    }
				    catch (XPathExpressionException ex) {CommonMML.logMML("Excepción: "+ex.toString()+" con la expresión: "+xpathTarget); return null;}
			       		
			    		oscar = "";
			    		
			    		for (int z=0; z < nlOscares.getLength(); z++) {
			    			elemOscar = (Element)nlOscares.item(z);
			    			categoria = elemOscar.getTextContent().trim();
			    			
						elemActor = (Element)elemOscar.getParentNode();  // el padre, Reparto, nos permite acceder a su nombre
						
					    nlNombres = elemActor.getElementsByTagName("Nombre");
					    elemNombre = (Element)nlNombres.item(0);
					    nombreActor = elemNombre.getTextContent().trim();
					    
					    if (nombreActor.equals(act)) oscar = categoria;		    
			    		}
					    				
					listaFilms.add(new Film(titulo, oscar));
	    		}	    		
    		}

    		
    		// si no hemos encontrado la pelicula o el actor, devolvemos null

    		if (listaFilms.size() == 0 ) return null;

    		Collections.sort(listaFilms);  // orden OSCAR, primero las de oscar principal, luego secundario, luego resto, en cada bloque orden alfabético
    		return new Filmografia(act, personaje, listaFilms);
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

    		out.println("<input type='hidden' name='pfase' value='0'><br>");

    		out.println("<input class='home' type='submit' value='Inicio' onClick='document.forms[0].pfase.value=\"0\"'>");
    		out.println("</form>");
    		
    		CommonMML.printFoot(out);
    		out.println("</body></html>");
		
		
     }
    	*/	
  
}