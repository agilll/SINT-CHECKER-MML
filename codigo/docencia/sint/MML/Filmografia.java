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

import java.util.ArrayList;

//objeto Filmografia que almacena  la filmografía d eun actor
//se usa en la consulta 14, el resultado final

public class Filmografia {
	String nombre, personaje;
	ArrayList<Film> films;
	
	public Filmografia (String n, String p, ArrayList<Film> f)  {
		nombre = n;
		personaje = p;
		films = f;
	}
	
	public String getNombre () {
		return nombre;
	}
	
	public String getPersonaje () {
		return personaje;
	}
	
	public ArrayList<Film> getFilms () {
		return films;
	}
}



