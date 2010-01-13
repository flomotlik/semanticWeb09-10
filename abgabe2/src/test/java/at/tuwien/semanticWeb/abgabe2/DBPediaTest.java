package at.tuwien.semanticWeb.abgabe2;

import java.io.IOException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class DBPediaTest {
    @Test
    public void testDBPedia() throws IOException, SAXException {
        DBPedia dbPedia = new DBPedia();
        String theAbstract = dbPedia.getAbstract("Ski_jumping");
        System.out.println(theAbstract);
    }
}
