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

// objeto Actor, que almacena su nombre y su ciudad natal
// se usa también en el checker

public class Pais implements Comparable<Pais> {

	String nombre, langDefault;
	int numPeliculas;

	public Pais (String n, String l, int np)  {
		nombre = n;
		langDefault = l;
		numPeliculas = np;
	}
	
	
	public String getNombre () {
		return nombre;
	}

	public String getLang () {
		return langDefault;
	}
	
	public int getNumPeliculas () {
		return numPeliculas;
	}

	
	// para ver si este actor ya está contenido en la lista que se le pasa
	
	public boolean isContainedInList (ArrayList<Pais> listaPaises) {

		String n;

		for (int x=0; x < listaPaises.size(); x++) {
			n = listaPaises.get(x).getNombre();
			if (n.equals(this.getNombre())) return true;
		}

		return false;
	}

	// orden: orden alfabético 
	
	public int compareTo(Pais segundoPais) {
		if (this.numPeliculas > segundoPais.numPeliculas)  return -1;
		else
			if (segundoPais.numPeliculas > this.numPeliculas)  return 1;
			else 
				return this.nombre.compareTo(segundoPais.nombre);
	}
	
}



