/****************************************************************
 *    SERVICIOS DE INTERNET
 *    EE TELECOMUNICACIÓN
 *    UNIVERSIDAD DE VIGO
 *
 *    Checker de la práctica MML
 *
 *    Autor: Alberto Gil Solla
 *    Curso : 2021-2022
 ****************************************************************/

// etiquetas de mensajes en distintos idiomas para varias clases

package docencia.sint.MML2021.checker;

import java.util.HashMap;
import docencia.sint.MML2021.MsMML;

public class MsMMLCh {

		public static final String CREATED = "2021";

		static HashMap<Integer, String[]> mapMsgs = new HashMap<Integer, String[]>();   // el hashmap de mensajes

		public static final int CMML00=0, CMML03=3, CMML04=4, CMML05=5, CMML06=6;
		public static final int CMML21=21, CMML22=22, CMML23=23, CMML24=24, CMML26=26, CMML27=27, CMML28=28;
		public static final int CMML30=30, CMML31=31, CMML32=32, CMML33=33, CMML34=34, CMML35=35, CMML36=36, CMML37=37, CMML38=38, CMML39=39;
		public static final int CMML40=40, CMML41=41, CMML42=42, CMML45=45, CMML46=46;

		static {
			mapMsgs.put(CMML00, new String[] {"Corrector de "+MsMML.XML_LANGUAGE, MsMML.XML_LANGUAGE+" checker"});
			mapMsgs.put(CMML03, new String[] {"Comprobación de servicios sobre "+MsMML.XML_LANGUAGE, "Checker of services about "+MsMML.XML_LANGUAGE});
			mapMsgs.put(CMML04, new String[] {"CURSO "+MsMML.CURSO, "COURSE "+MsMML.CURSO});

			mapMsgs.put(CMML05, new String[] {"CONSULTA 1 (enero): Reparto de una película de un año ", "QUERY 1 (January): Cast of a movie of a year"});
			mapMsgs.put(CMML06, new String[] {"CONSULTA 2 (junio): Películas de un miembro del reparto en un idioma ", "QUERY 2 (June): Movies of a cast in a language"});

			mapMsgs.put(CMML21, new String[] {"Error solicitando la lista de ", "Error requesting list of "});
			mapMsgs.put(CMML22, new String[] {"Diferencias en la lista de ", "Differences in list of "});
			mapMsgs.put(CMML23, new String[] {"Creando la solicitud de la lista de ", "Creating request for list of "});
			mapMsgs.put(CMML24, new String[] {"Resultado erróneo en la consulta directa", "Wrong result in the direct query"});

			mapMsgs.put(CMML26, new String[] {"Al pedir la consulta directa ", "Requesting direct query "});
			mapMsgs.put(CMML27, new String[] {"Al solicitar/parsear la lista de ", "Requesting/parsing the list of "});
			mapMsgs.put(CMML28, new String[] {"Resultado inválido, '%s' al parsear la lista de ", "Invalid result, '%s' parsing the list of "});

			mapMsgs.put(CMML30, new String[] {"El parser devuelve 'null' al parsear la lista de ", "Parser returns 'null' parsing the list of "});
			mapMsgs.put(CMML31, new String[] {"No se recibe '&lt;years>' al solicitar y parsear la lista de years", "'&lt;years>' is not received when requesting the list of years"});
			mapMsgs.put(CMML32, new String[] {"No se recibe '&lt;movies>' al solicitar y parsear la lista de movies", "'&lt;movies>' is not received when requesting the list of movies"});
			mapMsgs.put(CMML33, new String[] {"No se recibe '&lt;thecast>' al solicitar y parsear la lista del reparto", "'&lt;thecast>' is not received when requesting the list of cast",});
			mapMsgs.put(CMML34, new String[] {"No se recibe '&lt;langs>' al solicitar y parsear la lista de langs", "'&lt;langs>' is not received when requesting the list of langs"});
			
			mapMsgs.put(CMML35, new String[] {"Diferencia en la lista de langs: se recibe '%s' en la posición %d, pero se esperaba '%s'", "Difference in list of langs: received '%s' in position %d, but it was expected '%s'"});
			mapMsgs.put(CMML36, new String[] {"Diferencia en la lista de years: se recibe '%s' en la posición %d, pero se esperaba '%s'", "Difference in list of years: received '%s' in position %d, but it was expected '%s'"});

			mapMsgs.put(CMML37, new String[] {"Se esperaba la movie '%s' en la posición %d y se recibió '%s'", "Movie '%s' was expected in position %d and it was received '%s'"});
			mapMsgs.put(CMML38, new String[] {"Se esperaba la sinopsis '%s' en la posición %d y se recibió '%s'", "Synopsis '%s' was expected in position %d and it was received '%s'"});
			mapMsgs.put(CMML39, new String[] {"Se esperaban los idiomas '%s' en la posición %d y se recibió '%s'", "Langs '%s' were expected in position %d and it was received '%s'"});
			mapMsgs.put(CMML45, new String[] {"Se esperaban los géneros '%s' en la posición %d y se recibió '%s'", "Genres '%s' were expected in position %d and it was received '%s'"});

			mapMsgs.put(CMML40, new String[] {"Se esperaba el cast '%s' en la posición %d y se recibió '%s'", "Cast '%s' was expected in position %d and it was received '%s'"});
			mapMsgs.put(CMML41, new String[] {"Se esperaba el Id '%s' en la posición %d y se recibió '%s'", "Id '%s' was expected in position %d and it was received '%s'"});
			mapMsgs.put(CMML42, new String[] {"Se esperaba el Role '%s' en la posición %d y se recibió '%s'", "Role '%s' was expected in position %d and it was received '%s'"});
			mapMsgs.put(CMML46, new String[] {"Se esperaba el Contact '%s' en la posición %d y se recibió '%s'", "Contact '%s' was expected in position %d and it was received '%s'"});
		}


		 // obtiene un mensaje (id) dependiendo del idioma (lang = "es" o "en")
 		static String getMsg(int id, String lang) {
 			 String value[] = mapMsgs.get(id);
			 if (value == null) return "ERROR MsMMLCh.getMsg key "+Integer.toString(id);
 			 if (lang.equals("en"))  return value[1];
 			 else return value[0];
 		}


}
