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

// Clase Oscar, que almacena un óscar: nombre del miembro del reparto y categoría

public class Oscar {
	String nombre, categoria;
	
	public Oscar (String n, String c)  {
		nombre = n;
		categoria = c;
	}
	
	public String getNombre () {
		return nombre;
	}
	
	public String getCategoria () {
		return categoria;
	}
	
	
	
}