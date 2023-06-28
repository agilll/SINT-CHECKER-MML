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

import java.io.PrintWriter;
import java.util.ArrayList;

import docencia.sint.Common.CommonSINT;
import docencia.sint.Common.MsCP;


public class P2MC1FE  {

    // XML
    public static void printF11XML (PrintWriter out, ArrayList<String> years)
    {
      out.println("<?xml version='1.0' encoding='utf-8'?>");
      out.println("<years>");

      for (int x=0; x < years.size(); x++)
          out.println("<year>"+years.get(x).trim()+"</year>");

      out.println("</years>");
    }


    public static void printF12XML (PrintWriter out, ArrayList<Movie> movies)
    {
      Movie movie;

      out.println("<?xml version='1.0' encoding='utf-8'?>");
      out.println("<movies>");

      for (int x=0; x < movies.size(); x++) {
        movie = movies.get(x);
        out.println("<movie langs='"+movie.getLangs()+"' genres='"+movie.getGenres()+"' synopsis='"+movie.getSinopsis()+"' >"+movie.getTitle()+"</movie>");
      }

      out.println("</movies>");
    }


    public static void printF13XML (PrintWriter out, ArrayList<Cast> thecast)
    {
      Cast cast;

      out.println("<?xml version='1.0' encoding='utf-8'?>");
      out.println("<thecast>");

      for (int x=0; x < thecast.size(); x++) {
        cast = thecast.get(x);
        out.println("<cast id='"+cast.getID()+"'  role='"+cast.getRole()+"' contact='"+cast.getContact()+"' >"+cast.getName()+"</cast>");
      }

      out.println("</thecast>");
    }





    // HTML y AJAX
    public static void printF11HTML (PrintWriter out, String fe, ArrayList<String> years, String language)
    {
      if (fe.equals("html")) {
          out.println("<html>");
          CommonMML.printHead(language, out);
          out.println("<body>");
					// 001 = "Servicio de consulta de películas"
          out.println("<h2>"+MsMML.getMsg(MsMML.MML001, language)+"</h2>");
      }

			// 102 = "Consulta 1: Fase 1"
      out.println("<h3>"+MsMML.getMsg(MsMML.MML102, language)+"</h3>");
			// 108 = "Selecciona un año:"
      out.println("<h3>"+MsMML.getMsg(MsMML.MML108, language)+"</h3>");

      if (fe.equals("html")) {
          out.println("<ol>");

          for (int x=0; x < years.size(); x++)
            out.println("<li> <a href='?p="+CommonSINT.PPWD+"&"+CommonSINT.PFASE+"=12&pyear="+years.get(x)+"'>"+years.get(x)+"</a>");

          out.println("</ol>");
          out.println("<button class='home' onClick='window.location.href=\"?p="+CommonSINT.PPWD+"&"+CommonSINT.PFASE+"=01\"'>"+MsCP.getMsg(MsCP.CPC01, language)+"</button>");  //CPC01=Inicio

          CommonSINT.printFoot(out, MsMML.CURSO);
          out.println("</body></html>");
      }
      else { //ajax
        out.println("<ol>");

        for (int x=0; x < years.size(); x++) {
             String cad = "<li><u onclick='sendRequest(\"12\", \"&pyear="+years.get(x)+"\");'>"+years.get(x)+"</u>";
             out.println(cad);
        }

        out.println("</ol>");
      }
    }




    public static void printF12HTML (PrintWriter out, String fe, String pyear, ArrayList<Movie> movies, String language)
    {
        Movie movie;

        if (fe.equals("html")) {
            out.println("<html>");
            CommonMML.printHead(language, out);
            out.println("<body>");
						// 001 = "Servicio de consulta de películas"
	          out.println("<h2>"+MsMML.getMsg(MsMML.MML001, language)+"</h2>");
        }

				// 103 = "Consulta 1: Fase 2 (Año=%s)"
        out.println("<h3>"+String.format(MsMML.getMsg(MsMML.MML103, language), pyear)+"</h3>");

				// 110 ="No hay películas en el año"
        if (movies.size() == 0)
						out.println(MsMML.getMsg(MsMML.MML110, language)+pyear);
				// 109 = "Selecciona una película:"
				else
					out.println("<h3>"+MsMML.getMsg(MsMML.MML109, language)+"</h3>");

        if (fe.equals("html")) {
            out.println("<ol>");

            for (int x=0; x < movies.size(); x++) {
              movie = movies.get(x);
							// 111="Película"    112="Idiomas"     113="Géneros"   117="Sinopsis"
              out.println("<li><a href='?p="+CommonSINT.PPWD+"&"+CommonSINT.PFASE+"=13&pyear="+pyear+"&pmovie="+movie.getTitle()+"'> <b>"+MsMML.getMsg(MsMML.MML111, language)+"</b>='"+
                       movie.getTitle()+"'</a>"+
											 " ---  <b>"+MsMML.getMsg(MsMML.MML112, language)+"</b> = '"+movie.getLangs()+"'"+
											 " ---  <b>"+MsMML.getMsg(MsMML.MML113, language)+"</b> = '"+movie.getGenres()+"'"+
											 " ---  <b>"+MsMML.getMsg(MsMML.MML117, language)+"</b> = '"+movie.getSinopsis()+"'");

            }

            out.println("</ol>");
            out.println("<button class='home' onClick='window.location.href=\"?p="+CommonSINT.PPWD+"&"+CommonSINT.PFASE+"=01\"'>"+MsCP.getMsg(MsCP.CPC01, language)+"</button>");  //CPC01=Inicio
						out.println("<button class='back' onClick='window.location.href=\"?p="+CommonSINT.PPWD+"&"+CommonSINT.PFASE+"=11\"'>"+MsCP.getMsg(MsCP.CPC02, language)+"</button>");  //CPC02=Atrás vuelve a la fase 11

            CommonSINT.printFoot(out, MsMML.CURSO);
            out.println("</body></html>");
        }
        else {
              out.println("<ol>");

              for (int x=0; x < movies.size(); x++) {
                movie = movies.get(x);
								// 111="Película"    112="Idiomas"     113="Géneros"    117="Sinopsis"
                String cad = "<li><u onclick='sendRequest(\"13\", \"&pyear="+pyear+"&pmovie="+movie.getTitle()+"\");'> <b>"+MsMML.getMsg(MsMML.MML111, language)+"</b> ="+
								       movie.getTitle()+"</u>"+
											 " --- <b>"+MsMML.getMsg(MsMML.MML112, language)+"</b>="+movie.getLangs()+
											 " --- <b>"+MsMML.getMsg(MsMML.MML113, language)+"</b>="+movie.getGenres()+
											 " --- <b>"+MsMML.getMsg(MsMML.MML117, language)+"</b>="+movie.getSinopsis();
                out.println(cad);
              }

              out.println("</ol>");
        }
    }



    public static void printF13HTML (PrintWriter out, String fe, String pyear, String pmovie, ArrayList<Cast> thecast, String language)
    {
        Cast cast;

        if (fe.equals("html")) {
            out.println("<html>");
            CommonMML.printHead(language, out);
            out.println("<body>");
						// 001 = "Servicio de consulta de películas"
						out.println("<h2>"+MsMML.getMsg(MsMML.MML001, language)+"</h2>");
        }

				// 104 = "Consulta 1: Fase 3  (Año = %s, Película = %s)"
        out.println("<h3>"+String.format(MsMML.getMsg(MsMML.MML104, language), pyear, pmovie)+"</h3>");
        out.println("<h3>"+MsCP.getMsg(MsCP.CP07, language)+"</h3>"); // CP07 = "Este es el resultado: "

					// 114 = "No hay reparto en la película %s del año %s"
        if (thecast.size() == 0)
          out.println(String.format(MsMML.getMsg(MsMML.MML114, language), pmovie, pyear));

        if (fe.equals("html")) {
            out.println("<ol>");

            for (int x=0; x < thecast.size(); x++) {
              cast = thecast.get(x);
							// 115 = "Nombre"  116="Papel"    118="Contacto"
              out.println(" <li><b>"+MsMML.getMsg(MsMML.MML115, language)+"</b> = '"+cast.getName()+"'"+
													"  ---  <b>ID</b> = '"+cast.getID()+"'"+
                          "  ---  <b>"+MsMML.getMsg(MsMML.MML116, language)+"</b> = '"+cast.getRole()+"'"+
													"  ---  <b>"+MsMML.getMsg(MsMML.MML118, language)+"</b> = '"+cast.getContact()+"'");
            }

            out.println("</ol>");
            out.println("<button class='home' onClick='window.location.href=\"?p="+CommonSINT.PPWD+"&"+CommonSINT.PFASE+"=01\"'>"+MsCP.getMsg(MsCP.CPC01, language)+"</button>"); //CPC01=Inicio
						out.println("<button class='back' onClick='window.location.href=\"?p="+CommonSINT.PPWD+"&"+CommonSINT.PFASE+"=12&pyear="+pyear+"\"'>"+MsCP.getMsg(MsCP.CPC02, language)+"</button>");  //CPC02=Atrás vuelve a la fase 12

            CommonSINT.printFoot(out, MsMML.CURSO);
            out.println("</body></html>");
        }
        else {
            out.println("<ol>");

            for (int x=0; x < thecast.size(); x++) {
              cast = thecast.get(x);
							// 115 = "Nombre"  116="Papel"    118="Contacto"
              String cad = "<li><b>"+MsMML.getMsg(MsMML.MML115, language)+"</b>="+cast.getName()+
													 " --- <b>ID</b>="+cast.getID()+
                           " --- <b>"+MsMML.getMsg(MsMML.MML116, language)+"</b>="+cast.getRole()+
													 " --- <b>"+MsMML.getMsg(MsMML.MML118, language)+"</b>="+cast.getContact();
              out.println(cad);
            }

            out.println("</ol>");
        }
    }

}
