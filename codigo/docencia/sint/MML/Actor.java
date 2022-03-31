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
import java.util.Comparator;

// objeto Actor, que almacena su nombre y su ciudad natal
// se usa también en el checker

public class Actor implements Comparable<Actor> {

	String nombre, ciudad;
	Boolean algunOscar;

	public Actor (String n, String c)  {
		nombre = n;
		ciudad = c;
	}
	
	public Actor (String n, String c, Boolean  o)  {
		nombre = n;
		ciudad = c;
		algunOscar = o;
	}
	
	

	public String getNombre () {
		return nombre;
	}

	public String getCiudad () {
		return ciudad;
	}
	
	public Boolean hasOscar () {
		return algunOscar;
	}

	
	// para ver si este actor ya está contenido en la lista que se le pasa
	
	public boolean isContainedInList (ArrayList<Actor> listaActores) {

		String n;

		for (int x=0; x < listaActores.size(); x++) {
			n = listaActores.get(x).getNombre();
			if (n.equals(this.getNombre())) return true;
		}

		return false;
	}

	// orden: orden alfabético 
	
	public int compareTo(Actor segundoActor) {
		return (this.nombre.compareTo(segundoActor.nombre));
	}
	
	static final Comparator<Actor> OSCAR = 
			new Comparator<Actor>() {
		public int compare(Actor a1, Actor a2) {
			if (a1.algunOscar && !a2.algunOscar)  return -1;
			else
				if (a2.algunOscar && !a1.algunOscar)  return 1;
				else 
					return a1.nombre.compareTo(a2.nombre);
		}
	};
}



