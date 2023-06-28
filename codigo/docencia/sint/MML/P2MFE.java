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
import docencia.sint.Common.WrongFile;


public class P2MFE  {

    // la pantalla inicial

    // XML
    public static void printHomeXML (PrintWriter out)
    {
    		out.println("<?xml version='1.0' encoding='utf-8'?>");
    		out.println("<service>");
    		out.println("<status>OK</status>");
    		out.println("</service>");
    }

    // HTML y AJAX
    public static void printHomeHTML (String language, String fe, PrintWriter out)
    {
      out.println("<html>");
      CommonMML.printHead(language, out);
      out.println("<body>");

			// 001 = "Servicio de consulta de películas"
      out.println("<h2>"+MsMML.getMsg(MsMML.MML001, language)+"</h2>");

			// este link permite cambiar entre HTML y AJAX
			// CP00 = "Bienvenido a este servicio"
      if (fe.equals("html"))
        out.println("<h2><a href='?fe=ajax&p="+CommonSINT.PPWD+"'>"+MsCP.getMsg(MsCP.CP00, language)+"</a></h2>");
      else
        out.println("<h2><a href='?p="+CommonSINT.PPWD+"'>"+MsCP.getMsg(MsCP.CP00, language)+"</a></h2>");


			// CP03 = "Selecciona una consulta:"
      out.println("<h3>"+MsCP.getMsg(MsCP.CP03, language)+"</h3>");

      out.println("<ul>");

      // CP01 = "Ver los ficheros erróneos"
      if (fe.equals("ajax")) {
			  out.println("<li><u onclick='mngErrors();'><span id='linkErrors'>"+MsCP.getMsg(MsCP.CP01, language)+"</span></u>");
        // out.println("<u onclick='mngErrors();'><span id='linkErrors'></span></u>");
			}
      else {
        out.println("<li><a href='?"+CommonSINT.PFASE+"=02&p="+CommonSINT.PPWD+"'>"+MsCP.getMsg(MsCP.CP01, language)+"</a>");
				// out.println("<li><a href='?"+CommonSINT.PFASE+"=02&p="+CommonSINT.PPWD+"'></a>");  // no se muestra el mensaje de ver errores
			}

			// 101 = "Consulta 1: reparto de una película de un año"
			// 201 = "Consulta 2: filmografía de un protagonista en un idioma"
      if (fe.equals("ajax")) {
          out.println("<li><u onclick='sendRequest(\"11\", \"\");'>"+MsMML.getMsg(MsMML.MML101, language)+"</u>");
          out.println("<li><u onclick='sendRequest(\"21\", \"\");'>"+MsMML.getMsg(MsMML.MML201, language)+"</u>");
      }
      else {
          out.println("<li><a href='?p="+CommonSINT.PPWD+"&"+CommonSINT.PFASE+"=11'>"+MsMML.getMsg(MsMML.MML101, language)+"</a>");
          out.println("<li><a href='?p="+CommonSINT.PPWD+"&"+CommonSINT.PFASE+"=21'>"+MsMML.getMsg(MsMML.MML201, language)+"</a>");
      }
      out.println("</ul>");

      out.println("<div id='bloqueErrores'></div>");
      out.println("<div id='bloqueFase11'></div>");
      out.println("<div id='bloqueFase12'></div>");
      out.println("<div id='bloqueFase13'></div>");
      out.println("<div id='bloqueFase21'></div>");
      out.println("<div id='bloqueFase22'></div>");
      out.println("<div id='bloqueFase23'></div>");

      CommonSINT.printFoot(out, MsMML.CURSO);

      if (fe.equals("ajax")) {
          out.println("<script>");
					out.println("console.log(\"Cargando código\")");
          out.println("errors=false; ");
          out.println("var xhttp = new XMLHttpRequest();  ");
          out.println("bloqueErrores = document.getElementById('bloqueErrores') ");
          out.println("linkErrors = document.getElementById('linkErrors') ");
          out.println("bloque11 = document.getElementById('bloqueFase11') ");
          out.println("bloque12 = document.getElementById('bloqueFase12') ");
          out.println("bloque13 = document.getElementById('bloqueFase13') ");
          out.println("bloque21 = document.getElementById('bloqueFase21') ");
          out.println("bloque22 = document.getElementById('bloqueFase22') ");
          out.println("bloque23 = document.getElementById('bloqueFase23') ");

          out.println("function hideErrors() { ");
          out.println("       bloqueErrores.style.display = 'none';   ");
					// CP01 = "Ver los ficheros erróneos"
          // out.println("       linkErrors.innerHTML = '"+MsCP.getMsg(MsCP.CP01, language)+"';  ");
          out.println("       errors=false; ");
          out.println("}  ");

          out.println("function mngErrors() { ");
          out.println("   if (errors == true) { ");
          out.println("       hideErrors();  ");
          out.println("       return;  ");
          out.println("   }  ");
          out.println("   var url = '?p="+CommonSINT.PPWD+"&fe=ajax&"+CommonSINT.PFASE+"=02';");
          out.println("   xhttp.onreadystatechange = function() { ");
          out.println("            if (xhttp.readyState == 4 && xhttp.status == 200)   {");
          out.println("                bloqueErrores.innerHTML = xhttp.responseText;   ");
          out.println("                bloqueErrores.style.display = 'block';   ");
					// CP02 = "Ocultar los ficheros erróneos"
          out.println("                linkErrors.innerHTML = '"+MsCP.getMsg(MsCP.CP02, language)+"';  ");
          out.println("                errors = true;    ");
          out.println("            } ");
          out.println("   };  ");
          out.println("   xhttp.open('GET', url, true);  ");
          out.println("   xhttp.send();  ");
          out.println("}  ");

          out.println("function hideBlocks(blocks) { ");
          out.println("    for (b of blocks) { ");
          out.println("        b.style.display = 'none';  ");
          out.println("    }  ");
          out.println("}  ");

          out.println("function sendRequest(fase, params) { ");
          out.println("   hideErrors();  ");

          out.println("   var url = '?p="+CommonSINT.PPWD+"&fe=ajax&"+CommonSINT.PFASE+"='+fase+params;");
          out.println("   xhttp.onreadystatechange = function() { ");
          out.println("            if (xhttp.readyState == 4 && xhttp.status == 200)   {");
          out.println("                if (fase == '11') { ");
          out.println("                     bloque = bloque11;  ");
          out.println("                     hideBlocks([bloque12, bloque13, bloque21, bloque22, bloque23]);   ");
          out.println("                }  ");
          out.println("                if (fase == '12') { ");
          out.println("                     bloque = bloque12;  ");
          out.println("                     hideBlocks([bloque13]);   ");
          out.println("                }  ");
          out.println("                if (fase == '13') bloque = bloque13;   ");
          out.println("                if (fase == '21')  { ");
          out.println("                     bloque = bloque21;  ");
          out.println("                     hideBlocks([bloque22, bloque23, bloque11, bloque12, bloque13]);   ");
          out.println("                }  ");
          out.println("                if (fase == '22')  { ");
          out.println("                     bloque = bloque22;  ");
          out.println("                     hideBlocks([bloque23]);   ");
          out.println("                }  ");
          out.println("                if (fase == '23') bloque = bloque23;  ");
          out.println("                bloque.innerHTML = xhttp.responseText;   ");
          out.println("                bloque.style.display = 'block';   ");
          out.println("            } ");
          out.println("   };  ");
          out.println("   xhttp.open('GET', url, true);  ");
          out.println("   xhttp.send();  ");
          out.println("}  ");
          out.println("</script>");
      }

      out.println("</body></html>");
    }



    // los errores

    // XML
    public static void printErrorsXML (PrintWriter out)
    {
        out.println("<?xml version='1.0' encoding='utf-8'?>");
        out.println("<wrongDocs>");

        out.println("<warnings>");
        for (int x=0; x < CommonMML.listWarnings.size(); x++) {
        	WrongFile wf = CommonMML.listWarnings.get(x);
          out.println("<warning>");
          out.println("<file>"+wf.getFile()+"</file>");
          out.println("<cause>");
          ArrayList<String> warningsL = wf.getCausas();

          for (int y=0; y < warningsL.size(); y++) {
            out.println(warningsL.get(y));
          }
          out.println("</cause>");
          out.println("</warning>");
        }
        out.println("</warnings>");

        out.println("<errors> ");
        for (int x=0; x < CommonMML.listErrores.size(); x++) {
        	WrongFile wf = CommonMML.listErrores.get(x);
          out.println("<error> ");
          out.println("<file>"+wf.getFile()+"</file>");
          out.println("<cause>");
          ArrayList<String> errorsL = wf.getCausas();

          for (int y=0; y < errorsL.size(); y++) {
            out.println(errorsL.get(y));
          }

          out.println("</cause>");
          out.println("</error>");
        }
        out.println("</errors>");

        out.println("<fatalerrors>");
        for (int x=0; x < CommonMML.listErroresfatales.size(); x++) {
        	WrongFile wf = CommonMML.listErroresfatales.get(x);
          out.println("<fatalerror>");
          out.println("<file>"+wf.getFile()+"</file>");
          out.println("<cause>");
          ArrayList<String> fatalerroresL = wf.getCausas();

          for (int y=0; y < fatalerroresL.size(); y++) {
            out.println(fatalerroresL.get(y).replace("<","&lt;"));
          }
          out.println("</cause>");
          out.println("</fatalerror>");
        }
        out.println("</fatalerrors>");

        out.println("</wrongDocs>");
    }


    // HTML y AJAX
    public static void printErrorsHTML (String language, String fe, PrintWriter out)
    {
        if (fe.equals("html")) {
            out.println("<html>");
    		    CommonMML.printHead(language, out);
    		    out.println("<body>");
						// 001 = "Servicio de consulta de películas"
						out.println("<h2>"+MsMML.getMsg(MsMML.MML001, language)+"</h2>");
        }

				// CP04 = "Ficheros con warnings: "
    		out.println("<h3>"+MsCP.getMsg(MsCP.CP04, language)+CommonMML.listWarnings.size()+"</h3>");
    		if (CommonMML.listWarnings.size() > 0) {
    			out.println("<ul>");

    			for (int x=0; x < CommonMML.listWarnings.size(); x++) {
      			WrongFile wf = CommonMML.listWarnings.get(x);
    				out.println("<li> "+wf.getFile()+":<BR>");

    				// out.println("<ul>");
    				// ArrayList<String> warningsL = wf.getCausas();
            //
  	  			// for (int y=0; y < warningsL.size(); y++) {
  	  			// 	out.println("<li> "+warningsL.get(y)+"<BR>");
  	  			// }
        		// out.println("</ul>");
    			}

    			out.println("</ul>");
    		}

				// CP05 = "Ficheros con errores: "
       	out.println("<h3>"+MsCP.getMsg(MsCP.CP05, language)+CommonMML.listErrores.size()+"</h3>");
    		if (CommonMML.listErrores.size() > 0) {
    			out.println("<ul>");

    			for (int x=0; x < CommonMML.listErrores.size(); x++) {
      				WrongFile wf = CommonMML.listErrores.get(x);
    					out.println("<li> "+wf.getFile()+":<BR>");

    	  			// out.println("<ul>");
    				  // ArrayList<String> erroresL = wf.getCausas();
              //
  	  			  // for (int y=0; y < erroresL.size(); y++) {
  	  				//        out.println("<li> "+erroresL.get(y)+"<BR>");
  	  			  // }
        		  // out.println("</ul>");
    			}

    			out.println("</ul>");
    		}

				// CP06 = "Ficheros con errores fatales: "
      	out.println("<h3>"+MsCP.getMsg(MsCP.CP06, language)+CommonMML.listErroresfatales.size()+"</h3>");
    		if (CommonMML.listErroresfatales.size() > 0) {
    			out.println("<ul>");

    			for (int x=0; x < CommonMML.listErroresfatales.size(); x++) {
    				WrongFile wf = CommonMML.listErroresfatales.get(x);
    				out.println("<li> "+wf.getFile()+":<BR>");

   	  			// out.println("<ul>");
    				// ArrayList<String> fatalerroresL = wf.getCausas();
            //
  	  			// for (int y=0; y < fatalerroresL.size(); y++) {
  	  			// 	out.println("<li> "+fatalerroresL.get(y).replace("<","&lt;")+"<BR>");
  	  			// }
        		// out.println("</ul>");
    			}

    			out.println("</ul>");
    		}


        if (fe.equals("html")) {
    		    out.println("<form>");
    		    out.println("<input type='hidden' name='p' value='"+CommonSINT.PPWD+"'>");
    		    out.println("<input class='back' type='submit' value='"+MsCP.getMsg(MsCP.CPC02, language)+"'>"); //CPC02 = "Atrás"
    		    out.println("</form>");

    		    CommonSINT.printFoot(out, MsMML.CURSO);
    		    out.println("</body></html>");
        }
    	}

}
