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


public class P2MC2FE  {

    // XML
    public static void printF21XML (PrintWriter out, ArrayList<String> langs)
    {
      out.println("<?xml version='1.0' encoding='utf-8'?>");
      out.println("<langs>");

      for (int x=0; x < langs.size(); x++)
          out.println("<lang>"+langs.get(x).trim()+"</lang>");

      out.println("</langs>");
    }


    public static void printF22XML (PrintWriter out, ArrayList<Cast> thecast)
    {
      Cast cast;

      out.println("<?xml version='1.0' encoding='utf-8'?>");
      out.println("<thecast>");

      for (int x=0; x < thecast.size(); x++) {
        cast = thecast.get(x);
        out.println("<cast id='"+cast.getID()+"' contact='"+cast.getContact()+"' >"+cast.getName()+"</cast>");
      }

      out.println("</thecast>");
    }


    public static void printF23XML (PrintWriter out, ArrayList<Movie> movies)
    {
      Movie movie;

      out.println("<?xml version='1.0' encoding='utf-8'?>");
      out.println("<movies>");

      for (int x=0; x < movies.size(); x++) {
        movie = movies.get(x);
        out.println("<movie year='"+movie.getYear()+"' genres='"+movie.getGenres()+"'  synopsis='"+movie.getSinopsis()+"' >"+movie.getTitle()+"</movie>");
      }

      out.println("</movies>");
    }





    // HTML y AJAX
    public static void printF21HTML (PrintWriter out, String fe, ArrayList<String> langs, String language)
    {
      if (fe.equals("html")) {
          out.println("<html>");
          CommonMML.printHead(language, out);
          out.println("<body>");
					// 001 = "Servicio de consulta de películas"
          out.println("<h2>"+MsMML.getMsg(MsMML.MML001, language)+"</h2>");
      }

			// 202 = "Consulta 2: Fase 1"
      out.println("<h3>"+MsMML.getMsg(MsMML.MML202, language)+"</h3>");
			// 208 = "Selecciona un idioma:"
      out.println("<h3>"+MsMML.getMsg(MsMML.MML208, language)+"</h3>");

      if (fe.equals("html")) {
          out.println("<ol>");

          for (int x=0; x < langs.size(); x++)
            out.println("<li> <a href='?p="+CommonSINT.PPWD+"&"+CommonSINT.PFASE+"=22&plang="+langs.get(x)+"'>"+langs.get(x)+"</a>");

          out.println("</ol>");
          out.println("<button class='home' onClick='window.location.href=\"?p="+CommonSINT.PPWD+"&"+CommonSINT.PFASE+"=01\"'>"+MsCP.getMsg(MsCP.CPC01, language)+"</button>");  //CPC01=Inicio

          CommonSINT.printFoot(out, MsMML.CURSO);
          out.println("</body></html>");
      }
      else { //ajax
        out.println("<ol>");

        for (int x=0; x < langs.size(); x++) {
             String cad = "<li><u onclick='sendRequest(\"22\", \"&plang="+langs.get(x)+"\");'>"+langs.get(x)+"</u>";
             out.println(cad);
        }

        out.println("</ol>");
      }
    }




    public static void printF22HTML (PrintWriter out, String fe, String plang, ArrayList<Cast> thecast, String language)
    {
        Cast cast;

        if (fe.equals("html")) {
            out.println("<html>");
            CommonMML.printHead(language, out);
            out.println("<body>");
						// 001 = "Servicio de consulta de películas"
	          out.println("<h2>"+MsMML.getMsg(MsMML.MML001, language)+"</h2>");
        }

				// 203 = "Consulta 2: Fase 2 (Idioma=%s)"
        out.println("<h3>"+String.format(MsMML.getMsg(MsMML.MML203, language), plang)+"</h3>");

				// 210 ="No hay protagonistas con películas en el idioma"
        if (thecast.size() == 0)
						out.println(MsMML.getMsg(MsMML.MML210, language)+plang);
				// 209 = "Selecciona un protagonista:"
				else
					out.println("<h3>"+MsMML.getMsg(MsMML.MML209, language)+"</h3>");

        if (fe.equals("html")) {
            out.println("<ol>");

            for (int x=0; x < thecast.size(); x++) {
              cast = thecast.get(x);
							// 211="ID"    212="Contacto"
              out.println("<li><a href='?p="+CommonSINT.PPWD+"&"+CommonSINT.PFASE+"=23&plang="+plang+"&pid="+cast.getID()+"'> "+cast.getName()+"</a>"+
											 " ---  <b>"+MsMML.getMsg(MsMML.MML211, language)+"</b> = '"+cast.getID()+"'"+
											 " ---  <b>"+MsMML.getMsg(MsMML.MML212, language)+"</b> = '"+cast.getContact()+"'");

            }

            out.println("</ol>");
            out.println("<button class='home' onClick='window.location.href=\"?p="+CommonSINT.PPWD+"&"+CommonSINT.PFASE+"=01\"'>"+MsCP.getMsg(MsCP.CPC01, language)+"</button>");  //CPC01=Inicio
						out.println("<button class='back' onClick='window.location.href=\"?p="+CommonSINT.PPWD+"&"+CommonSINT.PFASE+"=21\"'>"+MsCP.getMsg(MsCP.CPC02, language)+"</button>");  //CPC02=Atrás vuelve a la fase 21

            CommonSINT.printFoot(out, MsMML.CURSO);
            out.println("</body></html>");
        }
        else {
              out.println("<ol>");

              for (int x=0; x < thecast.size(); x++) {
                cast = thecast.get(x);
								// 211="ID"    212="Contacto"
                String cad = "<li><u onclick='sendRequest(\"23\", \"&plang="+plang+"&pid="+cast.getID()+"\");'>"+cast.getName()+"</u>"+
											 " --- <b>"+MsMML.getMsg(MsMML.MML211, language)+"</b>="+cast.getID()+
											 " --- <b>"+MsMML.getMsg(MsMML.MML212, language)+"</b>="+cast.getContact();
                out.println(cad);
              }

              out.println("</ol>");
        }
    }



    public static void printF23HTML (PrintWriter out, String fe, String plang, String pid, ArrayList<Movie> movies, String language)
    {
        Movie movie;

        if (fe.equals("html")) {
            out.println("<html>");
            CommonMML.printHead(language, out);
            out.println("<body>");
						// 001 = "Servicio de consulta de películas"
						out.println("<h2>"+MsMML.getMsg(MsMML.MML001, language)+"</h2>");
        }

				// 204 = "Consulta 2: Fase 3  (Idioma = %s, Protagonista = %s)"
        out.println("<h3>"+String.format(MsMML.getMsg(MsMML.MML204, language), plang, pid)+"</h3>");
        out.println("<h3>"+MsCP.getMsg(MsCP.CP07, language)+"</h3>"); // CP07 = "Este es el resultado: "

					// 213 = "No hay películas del protagonista %s en el idioma %s"
        if (movies.size() == 0)
          out.println(String.format(MsMML.getMsg(MsMML.MML213, language), pid, plang));

        if (fe.equals("html")) {
            out.println("<ol>");

            for (int x=0; x < movies.size(); x++) {
              movie = movies.get(x);
							// 214 = "Película"  215="Géneros"    216="Sinopsis"
              out.println(" <li><b>"+MsMML.getMsg(MsMML.MML214, language)+"</b> = '"+movie.getTitle()+" ("+movie.getYear()+")'"+
                          "  ---  <b>"+MsMML.getMsg(MsMML.MML215, language)+"</b> = '"+movie.getGenres()+"'"+
						  "  ---  <b>"+MsMML.getMsg(MsMML.MML216, language)+"</b> = '"+movie.getSinopsis()+"'"+
						  "  ---  <b>"+MsMML.getMsg(MsMML.MML217, language)+"</b> = '<span style='color: red'>"+movie.getLangs()+"</span>'"); // examen
            }

            out.println("</ol>");
            out.println("<button class='home' onClick='window.location.href=\"?p="+CommonSINT.PPWD+"&"+CommonSINT.PFASE+"=01\"'>"+MsCP.getMsg(MsCP.CPC01, language)+"</button>"); //CPC01=Inicio
						out.println("<button class='back' onClick='window.location.href=\"?p="+CommonSINT.PPWD+"&"+CommonSINT.PFASE+"=22&plang="+plang+"\"'>"+MsCP.getMsg(MsCP.CPC02, language)+"</button>");  //CPC02=Atrás vuelve a la fase 22

            CommonSINT.printFoot(out, MsMML.CURSO);
            out.println("</body></html>");
        }
        else {
            out.println("<ol>");

            for (int x=0; x < movies.size(); x++) {
              movie = movies.get(x);
							// 214 = "Película"  215="Géneros"    216="Sinopsis"
              String cad = "<li><b>"+MsMML.getMsg(MsMML.MML214, language)+"</b>="+movie.getTitle()+
                           " --- <b>"+MsMML.getMsg(MsMML.MML215, language)+"</b>="+movie.getGenres()+
													 " --- <b>"+MsMML.getMsg(MsMML.MML216, language)+"</b>="+movie.getSinopsis();
              out.println(cad);
            }

            out.println("</ol>");
        }
    }

}
