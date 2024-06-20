import org.junit.jupiter.api.*;

import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage6.impl.DocumentPersistenceManager;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class documentPersistenceManagerTest {

    File path = new File("/Users/yosefginsberg/Downloads/test/");
    URI textUri = URI.create("https://www.baeldung.com/test/text");
    URI binaryUri = URI.create("https://www.baeldung.com/test/binary");
    public String testText = "Hello, this is a document";
    byte[] binaryData = {00000001};
    String key = "owner";
    String value = "Judah";
    DocumentImpl textDoc;
    DocumentImpl binaryDoc;
    DocumentPersistenceManager manager;
    

    @BeforeEach
    public void beforeEach() {
        manager = new DocumentPersistenceManager(path);
        textDoc = new DocumentImpl(textUri, testText, null);
        binaryDoc = new DocumentImpl(binaryUri, binaryData);
        textDoc.setMetadataValue(key, value);
        binaryDoc.setMetadataValue(key, value);
    }


    /*@Test
    public void cleanURIFileTest() throws URISyntaxException{
        String[] randomURLS = {
            "http://www.example.com/?agreement=ball&badge=authority",
            "http://www.example.net/approval.php?bone=bear",
            "http://example.com/",
            "http://www.example.com/",
            "https://www.example.com/",
            "https://www.example.com/book/boat",
            "https://example.com/account/beds",
            "http://attack.example.com/aftermath/agreement.aspx?battle=bat",
            "http://example.com/",
            "http://brother.example.com/basketball/aunt.html",
        };
        for(int i = 0; i < randomURLS.length; i++){
            URI uri = new URI(randomURLS[i]);
            File cleanFile = manager.cleanURIFile(uri);
            System.out.println(cleanFile.getAbsolutePath());
        }
    }*/

    @Test
    public void serializeTest() throws IOException{
        manager.serialize(textUri, textDoc);
        manager.serialize(binaryUri, binaryDoc);
    }

    @Test
    public void deserializeTest() throws IOException{
        manager.serialize(textUri, textDoc);
        manager.serialize(binaryUri, binaryDoc);
        DocumentImpl textRevived = (DocumentImpl) manager.deserialize(textUri); 
        DocumentImpl binaryRevived = (DocumentImpl) manager.deserialize(binaryUri); 
        assertEquals(textRevived.getDocumentTxt(), textDoc.getDocumentTxt());
        assertEquals(binaryRevived.getDocumentBinaryData()[0], binaryDoc.getDocumentBinaryData()[0]);
        assertEquals(textRevived.getMetadataValue("owner"), textDoc.getMetadataValue("owner"));
        assertEquals(binaryRevived.getMetadataValue("owner"), binaryDoc.getMetadataValue("owner"));
        for(String word : textRevived.getWordMap().keySet()){
            assertEquals(textRevived.wordCount(word), textDoc.wordCount(word));
        }
    }

    @Test
    public void deleteTest() throws IOException{
        manager.serialize(textUri, textDoc);
        manager.serialize(binaryUri, binaryDoc);
        assertTrue(manager.delete(textUri));
        assertTrue(manager.delete(binaryUri));
    }



    
}

