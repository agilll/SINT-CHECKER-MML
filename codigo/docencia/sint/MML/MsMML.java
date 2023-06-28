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

// etiquetas de mensajes para varias clases

package docencia.sint.MML2021;

import java.util.HashMap;

public class MsMML {

		public static final String XML_LANGUAGE = "MML";
		public static final String CURSO = "2021-2022";

		static HashMap<Integer, String[]> mapMsgs = new HashMap<Integer, String[]>();   // el hashmap de mensajes

		public static final int MML001=1;
		public static final int MML101=101, MML102=102, MML103=103, MML104=104, MML105=105, MML106=106, MML107=107, MML108=108, MML109=109;
		public static final int MML110=110, MML111=111, MML112=112, MML113=113, MML114=114, MML115=115, MML116=116, MML117=117, MML118=118;
		public static final int MML201=201, MML202=202, MML203=203, MML204=204, MML205=205, MML206=206, MML207=207, MML208=208, MML209=209;
		public static final int MML210=210, MML211=211, MML212=212, MML213=213, MML214=214, MML215=215, MML216=216, MML217=217;

		static {
			mapMsgs.put(MML001, new String[] {"Servicio de consulta de películas", "Movie Information service"});

			mapMsgs.put(MML101, new String[] {"Consulta 1: reparto de una película de un año", "Query 1: cast of a movie of a year"});
			mapMsgs.put(MML102, new String[] {"Consulta 1: Fase 1", "Query 1: Phase 1"});
			mapMsgs.put(MML103, new String[] {"Consulta 1: Fase 2 (Año = %s)", "Query 1: Phase 2 (Year = %s)"});
			mapMsgs.put(MML104, new String[] {"Consulta 1: Fase 3  (Año = %s, Película = %s)", "Query 1: Phase 3 (Year = %s, Movie = %s)"});
			mapMsgs.put(MML105, new String[] {"No hay años", "No available years"});
			mapMsgs.put(MML106, new String[] {"El año %s no existe", "Year %s not found"});
			mapMsgs.put(MML107, new String[] {"El año %s o la película %s no existe", "Year %s or movie %s not found"});
			mapMsgs.put(MML108, new String[] {"Selecciona un año:", "Please, select a year:"});
			mapMsgs.put(MML109, new String[] {"Selecciona una película:", "Please, select a movie:"});
			mapMsgs.put(MML110, new String[] {"No hay películas en ese año", "No movies in this year"});
			mapMsgs.put(MML111, new String[] {"Película", "Movie"});
			mapMsgs.put(MML112, new String[] {"Idiomas", "Languages"});
			mapMsgs.put(MML113, new String[] {"Generos", "Genres"});
			mapMsgs.put(MML114, new String[] {"No hay reparto en la película %s del año %s", "No cast in movie %s of year %s"});
			mapMsgs.put(MML115, new String[] {"Nombre", "Name"});
			mapMsgs.put(MML116, new String[] {"Papel", "Role"});
			mapMsgs.put(MML117, new String[] {"Sinopsis", "Synopsis"});
			mapMsgs.put(MML118, new String[] {"Contacto", "Contact"});

			mapMsgs.put(MML201, new String[] {"Consulta 2: filmografía de un protagonista en un idioma", "Query 2: movies of a cast in a language"});
			mapMsgs.put(MML202, new String[] {"Consulta 2: Fase 1", "Query 2: Phase 1"});
			mapMsgs.put(MML203, new String[] {"Consulta 2: Fase 2 (Idioma = %s)", "Query 2: Phase 2 (Lang = %s)"});
			mapMsgs.put(MML204, new String[] {"Consulta 2: Fase 3  (Idioma = %s, Protagonista = %s)", "Query 2: Phase 3 (Lang = %s, Cast = %s)"});
			mapMsgs.put(MML205, new String[] {"No hay idiomas", "No available langs"});
			mapMsgs.put(MML206, new String[] {"El idioma %s no existe", "Lang %s not found"});
			mapMsgs.put(MML207, new String[] {"El idioma %s o el protagonista %s no existe", "Lang %s or cast %s not found"});
			mapMsgs.put(MML208, new String[] {"Selecciona un idioma:", "Select a language:"});
			mapMsgs.put(MML209, new String[] {"Selecciona un protagonista:", "Select a cast:"});
			mapMsgs.put(MML210, new String[] {"No hay protagonistas con películas en ese idioma", "No cast with movies in this language"});
			mapMsgs.put(MML211, new String[] {"ID", "ID"});
			mapMsgs.put(MML212, new String[] {"Contacto", "Contact"});
			mapMsgs.put(MML213, new String[] {"No hay películas del protagonista %s en el idioma %s", "No movies for cast %s in language %s"});
			mapMsgs.put(MML214, new String[] {"Película", "Movie"});
			mapMsgs.put(MML215, new String[] {"Géneros", "Genres"});
			mapMsgs.put(MML216, new String[] {"Sinopsis", "Synopsis"});
			mapMsgs.put(MML217, new String[] {"Idiomas", "Languages"});
		}


		// obtiene un mensaje (id) dependiendo del idioma (lang = "es" o "en")
		static String getMsg(int id, String lang) {
			 String value[] = mapMsgs.get(id);
			 if (value == null) return "ERROR MsMML.getMsg key "+Integer.toString(id);
			 if (lang.equals("en"))  return value[1];
			 else return value[0];
		}

}
