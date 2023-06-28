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

package docencia.sint.MML2021;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

// objeto Movie que puede almacenar Title, lista de Genre, sinopsis, langs
// se usa en la consulta 12, la lista de movies de un año
// se usa también en el checker

public class Movie implements Comparable<Movie> {

	String title;
	String year;
	ArrayList<String> genres;
	String sinopsis;
	String langs;

	public Movie (String t, String y, ArrayList<String> g, String s, String l)  {
		title = t;
		year = y;
		genres = g;
		sinopsis = s;
		langs = l;
	}

	public String getYear () {
		return year;
	}

	public String getTitle () {
		return title;
	}

	public String getGenres () {
		String listString = String.join(",", genres);
		return listString;
	}

	public String getSinopsis () {
		return sinopsis;
	}

	public String getLangs () {
		return langs;
	}


	// orden principal: por duración ascendente, si igual duración en orden alfabéticamente inverso

	public int compareTo(Movie secondMovie) {
		if (this.genres.size() < secondMovie.genres.size()) return 1;
		else
			if (this.genres.size() > secondMovie.genres.size()) return -1;
			else
				return this.title.compareTo(secondMovie.title);
	}

	// orden 

	static final Comparator<Movie> SIN =
			new Comparator<Movie>() {
				public int compare(Movie p1, Movie p2) {
					if (p1.sinopsis.length() < p2.sinopsis.length()) return -1;
					if (p1.sinopsis.length() > p2.sinopsis.length()) return 1;
					return p2.title.compareTo(p1.title);
				}
			};
			
	static final Comparator<Movie> EX = // examen
			new Comparator<Movie>() {
				public int compare(Movie p1, Movie p2) {
					String l1 = p1.getLangs(); 
					String[] al1 = l1.split(" "); 
					String l2 = p2.getLangs(); 
					String[] al2 = l2.split(" "); 
					
					if (al1.length < al2.length) {
						return -1;
					}
					if (al1.length > al2.length) {
						return 1;
					}
					
					return p1.sinopsis.compareTo(p2.sinopsis);
				}
			};


}
