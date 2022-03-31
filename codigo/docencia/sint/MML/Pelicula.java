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

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Comparator;

// objeto Pelicula que puede almacenar título, duración, idiomas y óscares de su reparto
// se usa en la consulta 12, la lista de películas de un año
// se usa también en el checker

public class Pelicula implements Comparable<Pelicula> {

	String titulo;
	String ip;
	int duracion;
	String idiomas;
	ArrayList<Oscar> oscares;

	// varios constructores, según la necesidad
	
	public Pelicula (String t, String i)  {
		titulo = t;
		ip = i;
	}
	
	public Pelicula (String t, int d, String i)  {
		titulo = t;
		duracion = d;
		idiomas = i;
	}
	
	public Pelicula (String t, int d, String i, ArrayList<Oscar> o)  {
		titulo = t;
		duracion = d;
		idiomas = i;
		oscares = o;
	}

	
	
	public String getTitulo () {
		return titulo;
	}

	public String getIP () {
		return ip;
	}
	
	public int getDuracion () {
		return duracion;
	}

	public String getIdiomas () {
		return idiomas;
	}
	
	public ArrayList<Oscar> getOscares () {
		return oscares;
	}
	
	// devuelve la categoría del óscar que ganó el miembro de su reparto que se le pasa como parámetro
	// o "" si ese miembro no ganó un óscar por esa peli
	
	public String getOscar (String n) {
		String categoria = "";
		
		Iterator<Oscar> i = oscares.iterator();
		Oscar o;
		
		while (i.hasNext()) {
			o = i.next();
			if (o.getNombre().equals(n)) return o.getCategoria();
		}
		
		return categoria;
	}


	// para ver si esta Pelicula ya está contenida en la lista que se le pasa
	
	public boolean isContainedInList (ArrayList<Pelicula> listaPeliculas) {

		String peli;

		for (int x=0; x < listaPeliculas.size(); x++) {
			peli = listaPeliculas.get(x).getTitulo();
			if (peli.equals(this.getTitulo())) return true;
		}

		return false;
	}

	
	// orden principal: por duración ascendente, si igual duración en orden alfabéticamente inverso
	
	public int compareTo(Pelicula segundaPelicula) {
		if (this.duracion < segundaPelicula.duracion) return -1;
		else
			if (this.duracion > segundaPelicula.duracion) return 1;
			else
				return -1 * this.titulo.compareTo(segundaPelicula.titulo);
	}
	

	

	// orden IP (alfabéticamente por ip)
	
	static final Comparator<Pelicula> IP = 
			new Comparator<Pelicula>() {
		public int compare(Pelicula p1, Pelicula p2) {
			return p1.ip.compareTo(p2.ip);
		}
	};
	
	

}


