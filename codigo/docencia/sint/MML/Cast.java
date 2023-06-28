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
import java.util.Comparator;

// objeto Cast, que almacena los datos de un miembro del reparto
// se usa también en el checker

public class Cast implements Comparable<Cast> {

	String name, id, role, contact;

	public Cast (String n, String i, String r, String c)  {
		name = n;
		id = i;
		role = r;
		contact = c;
	}

	public String getName () {
		return name;
	}

	public String getID () {
		return id;
	}

	public String getRole () {
		return role;
	}

	public String getContact () {
		return contact;
	}




	// para ver si este actor ya está contenido en la lista que se le pasa

	// public boolean isContainedInList (ArrayList<Cast> listaCast) {
	//
	// 	String i;
	//
	// 	for (int x=0; x < listaCast.size(); x++) {
	// 		i = listaCast.get(x).getID();
	// 		if (i.equals(this.getID())) return true;
	// 	}
	//
	// 	return false;
	// }


// Necesario para igualdad en HashSet, pero no para un ArrayList

	// @Override
	// public int hashCode () {
	// 	return 0;
	// }

// para ver si un Cast ya está en un ArrayList
@Override
public boolean equals (Object o) {
	Cast co = (Cast)o;

	if (this.id.equals(co.id)) return true;
	else return false;
}

	// orden: orden alfabético

	public int compareTo(Cast secondCast) {
		String r1 = this.role;
		String r2 = secondCast.role;

		if (r1.equals(r2) ) return (this.id.compareTo(secondCast.id));

		if (r1.equals("Supporting")) return -1; // Supporting y otro

		if (r2.equals("Supporting")) return 1;  // otro y Supporting

		if (r1.equals("Main")) return -1;	  // Main, Extra
		return 1;  // Extra, Main
	}

	// orden CON (primero telefonos, luego email, t en cada bloque por orden alfabético)

	static final Comparator<Cast> CON =
			new Comparator<Cast>() {
				public int compare(Cast p1, Cast p2) {
					if ( (!p1.contact.contains("@")) && ( p2.contact.contains("@"))) return -1;
					if ( ( p1.contact.contains("@")) && (!p2.contact.contains("@"))) return 1;
					return p1.name.compareTo(p2.name);
				}
			};
}
