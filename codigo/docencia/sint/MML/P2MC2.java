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

// consulta 1: reparto de una película de un año

package docencia.sint.MML2021;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import docencia.sint.Common.CommonSINT;
import docencia.sint.Common.MsCP;


   // MÉTODOS PARA LA SEGUNDA CONSULTA

public class P2MC2 {

    // F21: método que gestiona ls solicitud de la lista de idiomas

    public static void doGetF21Langs (HttpServletRequest request, HttpServletResponse response,
		                                    String language, String auto, String fe) throws IOException {

	    	ArrayList<String> langsList = DataModel.getQ2Langs();   // se pide la lista de idiomas

	    	if (langsList.size() == 0) {
					// 205 = "No hay idiomas"
	    		CommonSINT.doBadRequest(MsMML.getMsg(MsMML.MML205, language), request, response);
	    		return;
	    	}

        if (auto.equals("true")) {
            response.setContentType("text/xml");
            PrintWriter out = response.getWriter();
            P2MC2FE.printF21XML(out, langsList);
        }
        else {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            P2MC2FE.printF21HTML(out, fe, langsList, language);
	    	}
    }




    // F22: método que imprime o devuelve la lista de protagonistas que tienen películas en un idioma

    public static void doGetF22Cast (HttpServletRequest request, HttpServletResponse response,
		                                     String language, String auto, String fe) throws IOException {

	    ArrayList<Cast> castList;

	    String plang = request.getParameter("plang");
	    if (plang == null) {
					// CP08 = "No param:"
	    	CommonSINT.doBadRequest(MsCP.getMsg(MsCP.CP08, language)+"plang", request, response);
	    	return;
	    }

	    castList = DataModel.getQ2Cast(plang, language);  // se pide la lista de protagonistas con películas en el idioma seleccionado
	    if (castList == null) {
					// 206 = "El idioma %s no existe"
	    		CommonSINT.doBadRequest(String.format(MsMML.getMsg(MsMML.MML206, language), plang), request, response);
	    		return;
	    }

      if (auto.equals("true")) {
          response.setContentType("text/xml");
          PrintWriter out = response.getWriter();
          P2MC2FE.printF22XML(out, castList);
      }
      else {
          response.setContentType("text/html");
          PrintWriter out = response.getWriter();
          P2MC2FE.printF22HTML(out, fe, plang, castList, language);
      }

    }





    // F23: método que imprime o devuelve la filmografía de un protagonista en un idioma

    public static void doGetF23Movies (HttpServletRequest request, HttpServletResponse response,
		                                     String language, String auto, String fe) throws IOException
    {
    		ArrayList<Movie> moviesList;

	    	String plang = request.getParameter("plang");
	    	if (plang == null) {
					// CP08 = "No param:"
	    		CommonSINT.doBadRequest(MsCP.getMsg(MsCP.CP08, language)+"plang", request, response);
	    		return;
	    	}

	    	String pid = request.getParameter("pid");
	    	if (pid == null) {
					// CP08 = "No param:"
	    		CommonSINT.doBadRequest(MsCP.getMsg(MsCP.CP08, language)+"pid", request, response);
	    		return;
	    	}


	    	moviesList = DataModel.getQ2Movies(plang, pid);   // pedimos la filmografía de un protagonista en un idioma
	    	if (moviesList == null) {
					// 207 = "El idioma %s o el protagonista %s no existe: "
	    		CommonSINT.doBadRequest(String.format(MsMML.getMsg(MsMML.MML207, language), plang, pid), request, response);
	    		return;
	    	}

        if (auto.equals("true")) {
            response.setContentType("text/xml");
            PrintWriter out = response.getWriter();
            P2MC2FE.printF23XML(out, moviesList);
        }
        else {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            P2MC2FE.printF23HTML(out, fe, plang, pid, moviesList, language);
        }
    }

}
