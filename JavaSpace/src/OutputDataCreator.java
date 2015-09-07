import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class OutputDataCreator {

    public static void writeData(List<Group> groups, String file) throws FileNotFoundException,
            UnsupportedEncodingException {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        
        html.append("\t<head>\n");
        html.append("\t<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n");
        html.append("\t\t<title>Задание 3</title>\n");
        html.append("\t</head>\n");
        
        html.append("\t<body>\n");

        for (Group grp : groups) {
            html.append("\t\t<table>\n");

            for (WordWrapper wrd : grp.getElements()) {
                html.append("\t\t\t<tr>\n\t\t\t\t<td>");
                html.append(wrd.getOriginalWord());
                html.append("</td>\n\t\t\t</tr>\n");
            }

            html.append("\t\t</table>\n");
        }

        html.append("\t</body>\n");
        html.append("</html>\n");

        PrintWriter pw = new PrintWriter(file, "UTF-8");
        pw.write(html.toString());
        pw.close();
    }

}
