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


   // MÉTODOS PARA LA PRIMERA CONSULTA

public class P2MC1 {

    // F11: método que imprime o devuelve la lista de años

    public static void doGetF11Years (HttpServletRequest request, HttpServletResponse response,
		                                    String language, String auto, String fe) throws IOException {

	    	ArrayList<String> yearsList = DataModel.getQ1Years();   // se pide la lista de años

	    	if (yearsList.size() == 0) {
					// 105 = "No hay años"
	    		CommonSINT.doBadRequest(MsMML.getMsg(MsMML.MML105, language), request, response);
	    		return;
	    	}

        if (auto.equals("true")) {
            response.setContentType("text/xml");
            PrintWriter out = response.getWriter();
            P2MC1FE.printF11XML(out, yearsList);
        }
        else {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            P2MC1FE.printF11HTML(out, fe, yearsList, language);
	    	}
    }




    // F12: método que imprime o devuelve la lista de películas de un año

    public static void doGetF12Movies (HttpServletRequest request, HttpServletResponse response,
		                                     String language, String auto, String fe) throws IOException {

	    ArrayList<Movie> moviesList;

	    String pyear = request.getParameter("pyear");
	    if (pyear == null) {
					// CP08 = "No param:"
	    	CommonSINT.doBadRequest(MsCP.getMsg(MsCP.CP08, language)+"pyear", request, response);
	    	return;
	    }

	    moviesList = DataModel.getQ1Movies(pyear);  // se pide la lista de películas del año seleccionado
	    if (moviesList == null) {
					// 106 = "El año %s no existe"
	    		CommonSINT.doBadRequest(String.format(MsMML.getMsg(MsMML.MML106, language), pyear), request, response);
	    		return;
	    }

      if (auto.equals("true")) {
          response.setContentType("text/xml");
          PrintWriter out = response.getWriter();
          P2MC1FE.printF12XML(out, moviesList);
      }
      else {
          response.setContentType("text/html");
          PrintWriter out = response.getWriter();
          P2MC1FE.printF12HTML(out, fe, pyear, moviesList, language);
      }

    }





    // F13: método que imprime o devuelve el reparto de una película de un año

    public static void doGetF13Cast (HttpServletRequest request, HttpServletResponse response,
		                                     String language, String auto, String fe) throws IOException
    {
    		ArrayList<Cast> castList;

	    	String pyear = request.getParameter("pyear");
	    	if (pyear == null) {
					// CP08 = "No param:"
	    		CommonSINT.doBadRequest(MsCP.getMsg(MsCP.CP08, language)+"pyear", request, response);
	    		return;
	    	}

	    	String pmovie = request.getParameter("pmovie");
	    	if (pmovie == null) {
					// CP08 = "No param:"
	    		CommonSINT.doBadRequest(MsCP.getMsg(MsCP.CP08, language)+"pmovie", request, response);
	    		return;
	    	}


	    	castList = DataModel.getQ1Cast(pyear, pmovie);   // pedimos el reparto de una película de un año
	    	if (castList == null) {
					// 107 = "El año %s o la película %s no existe: "
	    		CommonSINT.doBadRequest(String.format(MsMML.getMsg(MsMML.MML107, language), pyear, pmovie), request, response);
	    		return;
	    	}

        if (auto.equals("true")) {
            response.setContentType("text/xml");
            PrintWriter out = response.getWriter();
            P2MC1FE.printF13XML(out, castList);
        }
        else {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            P2MC1FE.printF13HTML(out, fe, pyear, pmovie, castList, language);
        }
    }

}
