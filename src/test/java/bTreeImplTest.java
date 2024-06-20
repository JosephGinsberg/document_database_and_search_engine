import org.junit.jupiter.api.*;

import edu.yu.cs.com1320.project.impl.BTreeImpl;
import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage6.impl.DocumentPersistenceManager;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class bTreeImplTest {

    BTreeImpl<URI, Document> tree;
    File path = new File("/Users/yosefginsberg/Downloads/experiment");
    URI textUri = URI.create("https://www.baeldung.com/text");
    URI binaryUri = URI.create("https://www.baeldung.com/binary");
    public String testText = "Hello, this is a document";
    byte[] binaryData = {00000001};
    String key = "owner";
    String value = "Judah";
    DocumentImpl textDoc;
    DocumentImpl binaryDoc;
    DocumentPersistenceManager manager;
    DocumentImpl[] docs = new DocumentImpl[50];

    @BeforeEach
    public void beforeEach() throws URISyntaxException {
        tree = new BTreeImpl<URI, Document>();
        manager = new DocumentPersistenceManager(path);
        tree.setPersistenceManager(manager);
        for(int i = 0; i < 50; i++){
            if(i < 25){
                docs[i] = new DocumentImpl(new URI(textUri.toString() + i), testText + i, null);
            }
            if(i >= 25){
                docs[i] = new DocumentImpl(new URI(textUri.toString() + i), binaryData);
            }
            docs[i].setMetadataValue(key, value);
        }
    }

    @Test
    public void testMoveToDisk() throws URISyntaxException, IOException{
        for(int i = 0; i < 50; i++){
            tree.put(new URI(textUri.toString() + i), docs[i]);
        }
        tree.moveToDisk(new URI(textUri.toString() + 4));
        assertEquals(tree.get(new URI(textUri.toString() + 4)), docs[4]);
        assertEquals(tree.get(new URI(textUri.toString() + 4)), docs[4]);
        assertEquals(tree.put(new URI(textUri.toString() + 4), null), docs[4]);
        tree.moveToDisk(new URI(textUri.toString() + 9));
        assertEquals(tree.put(new URI(textUri.toString() + 9), null), docs[9]);
        assertEquals(tree.put(new URI(textUri.toString() + 9), null), null);
    }

    @Test
    public void testPutandGet() throws URISyntaxException{
        for(int i = 0; i < 50; i++){
            tree.put(new URI(textUri.toString() + i), docs[i]);
        }
        assertNull(tree.get(new URI(textUri.toString() + 50)));
        for(int i = 0; i < 50; i++){
            assertEquals(tree.get(new URI(textUri.toString() + i)), docs[i]);
        }
        assertEquals(tree.put(new URI(textUri.toString() + 9), null), docs[9]);
        assertEquals(tree.put(new URI(textUri.toString() + 9), null), null);
        assertEquals(tree.put(new URI(textUri.toString() + 8), docs[9]), docs[8]);
    }
    
}
