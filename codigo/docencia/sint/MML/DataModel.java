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

// consulta 1
// alumnos de una asignatura en una titulación

package docencia.sint.MML2021;

import java.util.HashSet;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;
import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;

import docencia.sint.Common.CommonSINT;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;


// MÉTODOS PARA CALCULAR LOS RESULTADOS

public class DataModel {

		// MÉTODOS PARA LA CONSULTA 1

    // método auxiliar que calcula la lista de titulaciones

    public static ArrayList<String> getQ1Years () {
	    	if (CommonMML.real == 0)
	    		return new ArrayList<String>(Arrays.asList("2001", "2002", "2003", "2004"));
	    	else {
	    		ArrayList<String> listaYears = new ArrayList<String>();

	    		// convertimos las claves del hashmap en una lista

	    		Set<String> setYears = CommonMML.mapDocs.keySet();
	    		listaYears.addAll(setYears);

	    		Collections.sort(listaYears, Collections.reverseOrder());  // se ordenan alfabéticamente
	    		return listaYears;
	    	}
    }



    // método auxiliar que calcula la lista de películas de un año

    public static ArrayList<Movie> getQ1Movies (String pyear) {
	    if (CommonMML.real == 0)
	    	return new ArrayList<Movie>(Arrays.asList(new Movie("T1","",new ArrayList<String>(), "s1", "es"), new Movie("T2","",new ArrayList<String>(), "s2", "es"), new Movie ("T3","",new ArrayList<String>(), "s3", "es")));
	    else {
	    		Document doc = CommonMML.mapDocs.get(pyear);
	    		if (doc == null) return null;  // no existe ese año

	    		ArrayList<Movie> listaMovies = new ArrayList<Movie>();

	    		NodeList nlMovies = doc.getElementsByTagName("Movie");  // pedimos el NodeList con todas las peliculas

	    		Element elemMovie, elemTitle, elemGenre;
	    		String title, langs, genre, sinopsis;

	    		// vamos a recopilar la información de todas las películas

					for (int y=0; y < nlMovies.getLength(); y++) {
						elemMovie = (Element)nlMovies.item(y);  // estudiamos una pelicula

						NodeList nlTitulos = elemMovie.getElementsByTagName("Title");  // obtenemos el titulo de la pelicula
						elemTitle = (Element)nlTitulos.item(0);
						title = elemTitle.getTextContent().trim();

						NodeList nlGenres = elemMovie.getElementsByTagName("Genre");  // obtenemos los géneros de la pelicula
						ArrayList<String> listaGeneros = new ArrayList<String>();
						for (int z=0; z < nlGenres.getLength(); z++) {
							elemGenre = (Element)nlGenres.item(z);
							genre = elemGenre.getTextContent().trim();
							listaGeneros.add(genre);
						}

						sinopsis = CommonSINT.getTextFromMixedContent(elemMovie);

						langs = elemMovie.getAttribute("langs");

						listaMovies.add(new Movie(title, "", listaGeneros, sinopsis, langs));  // creamos y añadimos la pelicula
					}

	    		Collections.sort(listaMovies);  // ordenamos las películas

	    		return listaMovies;
	    }
    }


    // método auxiliar que calcula el reparto de una película de un año

    public static ArrayList<Cast> getQ1Cast (String pyear, String pmovie) {
	    	if (CommonMML.real == 0)
	    		return new ArrayList<Cast>(Arrays.asList(new Cast("Pepe","abc123", "Main", "555555555"),
	    				new Cast("Ana","mno456", "Supporting", "555555555"), new Cast("Juan","xyz789", "Main", "555555555")));
	    	else {

	    		Document doc = CommonMML.mapDocs.get(pyear);
	    		if (doc == null) return null;  // no existe ese año

	    		ArrayList<Cast> listaCast = new ArrayList<Cast>();  // lista de reparto a devolver

	        String xpathTarget =   "/Movies/Movie[Title='"+pmovie+"']/Cast";    // los miembros del reparto buscados
	        NodeList nlCast=null;

	    		try {  // obtenemos los miembros
	    			nlCast = (NodeList)CommonMML.xpath.evaluate(xpathTarget, doc, XPathConstants.NODESET);
	    		}
	    		catch (XPathExpressionException e) {CommonMML.logMML(e.toString()); return null;}

	    		if (nlCast.getLength() == 0)
              		return listaCast;

	    		Element elemCast;
	    		String nombre, id, papel, contacto;

	    		for (int x=0; x < nlCast.getLength(); x++) {
	    			elemCast = (Element) nlCast.item(x);

	    			nombre = CommonSINT.getTextContentOfChild(elemCast, "Name");

						id = elemCast.getAttribute("id");

						papel = CommonSINT.getTextContentOfChild(elemCast, "Role");

						contacto = CommonSINT.getTextContentOfChild(elemCast, "Phone");
            if (contacto == null)
              	contacto = CommonSINT.getTextContentOfChild(elemCast, "Email");

	    			listaCast.add(new Cast(nombre, id, papel, contacto));
	    		}

    		Collections.sort(listaCast);  // ordenamos la lista
    		return listaCast;
    	}
    }






		// MÉTODOS PARA LA CONSULTA 2

		// método auxiliar que calcula la lista de idiomas

		public static ArrayList<String> getQ2Langs () {
				if (CommonMML.real == 0)
					return new ArrayList<String>(Arrays.asList("es", "en", "de", "fr"));
				else {
					ArrayList<String> listaLangs = new ArrayList<String>();

					String targetLangs = "/Movies/Movie/@langs";    // langs en los atributos langs de las películas
					Document doc;
					NodeList nlLangs=null;
					Attr attrLangs;
					String listaIdiomas;

					Collection<Document> collectionDocs = CommonMML.mapDocs.values();
					Iterator<Document> iter = collectionDocs.iterator();

					while (iter.hasNext()) {   // iteramos sobre todos los docs

						doc = iter.next();

						try {  // obtenemos los atributos langs
							nlLangs = (NodeList)CommonMML.xpath.evaluate(targetLangs, doc, XPathConstants.NODESET);
						}
						catch (XPathExpressionException ex) {CommonMML.logMML(ex.toString()); return null;}

						for (int z=0; z < nlLangs.getLength(); z++) {
							attrLangs = (Attr)nlLangs.item(z);   // estudiamos cada atributo langs
							listaIdiomas = attrLangs.getValue();
							String[] idiomas = listaIdiomas.split(" "); // pasamos la cadena de este langs a un array de idiomas

							for (int i = 0; i < idiomas.length; i++){
								if (!listaLangs.contains(idiomas[i])) listaLangs.add(idiomas[i]); // miramos cada idioma y, si no está en la lista, lo añadimos
							}
						}
					}

					Collections.sort(listaLangs);  // alfabéticamente
					return listaLangs;
				}
		}




		// 	método auxiliar que calcula la lista de protagonistas con películas en un idioma

			public static ArrayList<Cast> getQ2Cast (String plang, String userLanguage) {

        HashSet<Cast> hs = new HashSet<Cast>();

					if (CommonMML.real == 0)
						return new ArrayList<Cast>(Arrays.asList(new Cast("Pepe","12345678A", "role", "666666666"),
								new Cast("Ana","87654321B", "role", "777777777"), new Cast("Juan","J2233445", "role", "888888888")));
					else {
						ArrayList<Cast> listaCasts = new ArrayList<Cast>();  // lista de Cast a devolver
						String targetCast = "/Movies/Movie[contains(@langs,'"+plang+"')]/Cast"; // Cast de películas en el idioma seleccionado

						Collection<Document> collectionDocs = CommonMML.mapDocs.values();
						Iterator<Document> iter = collectionDocs.iterator();
						Document doc;
						NodeList nlCasts=null, nlNames, nlContacts, nlRoles;
						Element elemCast, elemName, elemContact, elemRole;
						String name, contact, id, role;
						Cast unCast;

						while (iter.hasNext()) {   // iteramos sobre todos los docs

							doc = iter.next();

							try {  // obtenemos los Cast de las películas con el lang
								nlCasts = (NodeList)CommonMML.xpath.evaluate(targetCast, doc, XPathConstants.NODESET);
							}
							catch (XPathExpressionException ex) {CommonMML.logMML(ex.toString()); return null;}

							for (int y=0; y < nlCasts.getLength(); y++) {
								elemCast = (Element)nlCasts.item(y);   // estudiamos cada cast
								id = elemCast.getAttribute("id");

								nlNames = (NodeList)elemCast.getElementsByTagName("Name");
								elemName = (Element)nlNames.item(0);
								name = elemName.getTextContent();

								nlContacts = (NodeList)elemCast.getElementsByTagName("Phone");
								if (nlContacts.getLength() > 0) {
									elemContact = (Element)nlContacts.item(0);
									contact = elemContact.getTextContent();
								}
								else {
									nlContacts = (NodeList)elemCast.getElementsByTagName("Email");
									elemContact = (Element)nlContacts.item(0);
									contact = elemContact.getTextContent();
								}
								
								nlRoles = (NodeList)elemCast.getElementsByTagName("Role");
								elemRole = (Element)nlRoles.item(0);
								role = elemRole.getTextContent();
								
								if (userLanguage.equals("en"))
									if (!role.equals("Main")) continue;

								unCast = new Cast(name, id, role, contact);

                // if (hs.contains(unCast))   System.out.println("\n### HashSet ya lo contiene: "+name);
                // else {
                //   System.out.println("\n### HashSet no lo contiene: "+name);
                //   hs.add(unCast);
                // }

                // esto funciona porque he sobreescrito el equals
                if (!listaCasts.contains(unCast))
                  listaCasts.add(unCast);  // si no está, lo añadimos

                // if (listaCasts.contains(unCast))
                //   System.out.println("\n### ya lo contiene: "+name);
                // else
                //   System.out.println("\n### no lo contiene: "+name);
                //
								// if (!unCast.isContainedInList(listaCasts)) {
                //   System.out.println("*** no lo contiene: "+name);
                //   listaCasts.add(unCast);  // si no está, lo añadimos
                // }
                // else
                //   System.out.println("*** ya lo contiene: "+name);


							} // fin de Casts
						} // fin de docs

						Collections.sort(listaCasts, Cast.CON);
						return listaCasts;
					}
			}




		// método auxiliar que calcula la filmografía de un Cast en un idioma

		public static ArrayList<Movie> getQ2Movies (String plang, String pid) {
			if (CommonMML.real == 0)
				return new ArrayList<Movie>(Arrays.asList(new Movie("A1","",new ArrayList<String>(), "sinopsis", "idiomas"),
																									new Movie("A2","",new ArrayList<String>(), "sinopsis", "idiomas"),
																									new Movie ("A3","",new ArrayList<String>(), "sinopsis", "idiomas")));
			else {
					ArrayList<Movie> listaMovies = new ArrayList<Movie>();

					String targetMovie = "/Movies/Movie[(contains(@langs,'"+plang+"')) and (Cast[@id='"+pid+"'])]";

					Collection<Document> collectionDocs = CommonMML.mapDocs.values();
					Iterator<Document> iter = collectionDocs.iterator();

					Document doc;
					NodeList nlMovies=null, nlGenres;
					Element elemMovies, elemMovie, elemGenre;
					String title, genre, sinopsis, year, langs;
					ArrayList<String> listaGeneros;

					while (iter.hasNext()) {   // iteramos sobre todos los docs

						doc = iter.next();

						try {  // obtenemos las películas con el lang
							nlMovies = (NodeList)CommonMML.xpath.evaluate(targetMovie, doc, XPathConstants.NODESET);
						}
						catch (XPathExpressionException ex) {CommonMML.logMML(ex.toString()); return null;}

						for (int z=0; z < nlMovies.getLength(); z++) {
							elemMovie = (Element)nlMovies.item(z);   // estudiamos cada movie

							title =  CommonSINT.getTextContentOfChild(elemMovie, "Title");

							listaGeneros = new ArrayList<String>();
							nlGenres = elemMovie.getElementsByTagName("Genre");  // obtenemos los géneros de la pelicula
							for (int y=0; y < nlGenres.getLength(); y++) {
								elemGenre = (Element)nlGenres.item(y);
								genre = elemGenre.getTextContent().trim();
								listaGeneros.add(genre);
							}

							langs = elemMovie.getAttribute("langs");
							
							sinopsis = CommonSINT.getTextFromMixedContent(elemMovie);

							elemMovies = (Element)elemMovie.getParentNode();
							year = CommonSINT.getTextContentOfChild(elemMovies, "Year");

							listaMovies.add(new Movie(title, year, listaGeneros, sinopsis, langs));
						}
					}


					// Collections.sort(listaMovies, Movie.SIN);  // ordenamos por tamaño sinopsis, y si es igual, por alfabético de título
					Collections.sort(listaMovies, Movie.EX);  // examen ordenamos por número de idiomas, y si es igual, por alfabético de sinopsis
					return listaMovies;
			}
		}

}
