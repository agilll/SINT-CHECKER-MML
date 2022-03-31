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

package docencia.sint.MML;

//objeto Film, un elemento de una Filmografía, se usa en el resultado final

public class Film implements Comparable<Film> {
	String titulo;
	String oscar;
	
	public Film (String t, String o)  {
		titulo = t;
		oscar = o;
	}
	
	public String getTitulo () {
		return titulo;
	}
	
	public String getOscar () {
		return oscar;
	}
	
	
	// orden natural (primero sin oscar, luego Principal, y luego Secundario, en cada bloque alfabéticamente)
	
	public int compareTo(Film segundoFilm) {

		if (this.oscar.compareTo(segundoFilm.oscar) < 0) return -1;
		else
			if (this.oscar.compareTo(segundoFilm.oscar) > 0)  return 1;
			else 
				return  this.titulo.compareTo(segundoFilm.titulo);
			
			
	}
}



