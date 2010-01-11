package at.tuwien.semanticWeb.abgabe2;

import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

public class DBPedia {
    public String getAbstract(String concept) throws IOException, SAXException {
        String sparql = "http://dbpedia.org/sparql?query=SELECT%20DISTINCT%20?abstract"
            + "%20WHERE%20{{%20%3Chttp://dbpedia.org/resource/" + concept + "%3E%20%3Chttp://dbpedia.org/property/"
            + "abstract%3E%20?abstract%20.FILTER%20langMatches%28%20lang%28?abstract%29,%20%27en%27%29}}";
        String content = Helper.getURLContent(sparql);
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.addObjectCreate("table/tr/td", StringBuffer.class.getName());
        digester.addCallMethod("table/tr/td", "append", 1);
        digester.addCallParam("table/tr/td", 0);
        Object result = digester.parse(new StringReader(content));
        String toReturn = "";
        if (result != null) {
            toReturn = result.toString();
        }
        return toReturn;
    }
}
