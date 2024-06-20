import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Set;

import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;

public class documentImplTest {

    URI uri = URI.create("https://www.baeldung.com/articles");
    URI uri2 = URI.create("https://www.baeldung.com/articles2");
    public String text = "Hello, this is a document";
    byte[] binaryData = {00000001};
    DocumentImpl testDoc;

    String[] badStrings = {null, "", "    "};
    URI badURI = null;
    byte[] badByte = null;

    @BeforeEach
    public void beforeEach() {
        testDoc = new DocumentImpl(uri, text, null);
    }

    @Test
    public void testDocumentImpl(){
        assertEquals(uri, testDoc.getKey());
        assertEquals(text, testDoc.getDocumentTxt());
        assertNull(testDoc.getDocumentBinaryData());
        //assertNull(testDoc.getMetadata());
        for(String bad : badStrings){
            assertThrows(IllegalArgumentException.class, () ->
            testDoc = new DocumentImpl(uri, bad, null));
        }
        assertThrows(IllegalArgumentException.class, () ->
        testDoc = new DocumentImpl(badURI, text, null));
        testDoc = new DocumentImpl(uri, binaryData);
        assertEquals(uri, testDoc.getKey());
        assertArrayEquals(binaryData, testDoc.getDocumentBinaryData());
        assertNull(testDoc.getDocumentTxt());
        //assertNull(testDoc.getMetadata());
        assertThrows(IllegalArgumentException.class, () ->
        testDoc = new DocumentImpl(uri, badByte));
        assertThrows(IllegalArgumentException.class, () ->
        testDoc = new DocumentImpl(badURI, binaryData));
    }

    @Test
    public void testEquals(){
        assertTrue(testDoc.equals(testDoc));
        DocumentImpl testDoc2 = new DocumentImpl(uri, "Hello, this is a document!", null);
        assertFalse(testDoc.equals(testDoc2));
        testDoc = new DocumentImpl(uri, binaryData);
        assertTrue(testDoc.equals(testDoc));
        byte[] binaryData2 = {00000011};
        testDoc2 = new DocumentImpl(uri, binaryData2);
        assertFalse(testDoc.equals(testDoc2));
        assertFalse(testDoc.equals("hello"));
    }

    @Test
    public void testSetMetaDataValue(){
        String key = "owner";
        String value = "Judah";
        assertNull(testDoc.setMetadataValue(key, value));
        String value2 = "Diament";
        assertEquals(testDoc.setMetadataValue(key, value2), value);
        for(String bad : badStrings){
            assertThrows(IllegalArgumentException.class, () ->
            testDoc.setMetadataValue(bad, value));
        }
    }

    @Test
    public void testGetMetaDataValue(){
        String key = "owner";
        String value = "Judah";
        testDoc.setMetadataValue(key, value);
        assertEquals(testDoc.getMetadataValue(key), value);
        for(String bad : badStrings){
            assertThrows(IllegalArgumentException.class, () ->
            testDoc.getMetadataValue(bad));
        }
    }

    @Test
    public void testGetMetaData(){
        assertNotNull(testDoc.getMetadata());
        HashMap<String, String> shallow = new HashMap<String, String>();
        String key = "owner";
        String value = "Judah";
        for(int i = 1; i <= 100; i++){
            assertNull(testDoc.setMetadataValue(key + i, value + i));
            shallow.put(key + i, value + i);
        }
        for(int i = 1; i <= 100; i++){
            assertEquals(testDoc.getMetadata().get(key + i), shallow.get(key + i));
        }
    }

    @Test
    public void testGetBinaryData(){
        assertNull(testDoc.getDocumentBinaryData());
        testDoc = new DocumentImpl(uri, binaryData);
        assertNotNull(testDoc.getDocumentBinaryData());
    }

    @Test
    public void getDocumentTxt(){
        testDoc = new DocumentImpl(uri, text, null);
        assertEquals(testDoc.getDocumentTxt(), text);
        testDoc = new DocumentImpl(uri, binaryData);
        assertNull(testDoc.getDocumentTxt());
    }

    @Test
    public void testWordCount(){
        String gettysburg = "Four score and seven years ago our fathers brought forth on this continent, a new nation, conceived in Liberty, and dedicated to the proposition that all men are created equal. Now we are engaged in a great civil war, testing whether that nation, or any nation so conceived and so dedicated, can long endure. We are met on a great battle-field of that war. We have come to dedicate a portion of that field, as a final resting place for those who here gave their lives that that nation might live. It is altogether fitting and proper that we should do this. But, in a larger sense, we can not dedicate -- we can not consecrate -- we can not hallow -- this ground. The brave men, living and dead, who struggled here, have consecrated it, far above our poor power to add or detract. The world will little note, nor long remember what we say here, but it can never forget what they did here. It is for us the living, rather, to be dedicated here to the unfinished work which they who fought here have thus far so nobly advanced. It is rather for us to be here dedicated to the great task remaining before us -- that from these honored dead we take increased devotion to that cause for which they gave the last full measure of devotion -- that we here highly resolve that these dead shall not have died in vain -- that this nation, under God, shall have a new birth of freedom -- and that government of the people, by the people, for the people, shall not perish from the earth.";
        testDoc = new DocumentImpl(uri, gettysburg, null);
        Set<String> words = testDoc.getWords();
        for(String s : words){
            //System.out.println(s + ": " + testDoc.wordCount(s));
        }
    }

    @Test
    public void testGetWords(){
        String[] testTexts = {
            " h  ",
            "Hello, this is a document!",
            "12345 67890 ",
            "  G,;;;45454 h'''they're  ",
            " ,h4 ,6/ ,5,5,5, 32 3!2 3",
            "Four score and seven years ago our fathers brought forth on this continent, a new nation, conceived in Liberty, and dedicated to the proposition that all men are created equal. Now we are engaged in a great civil war, testing whether that nation, or any nation so conceived and so dedicated, can long endure. We are met on a great battle-field of that war. We have come to dedicate a portion of that field, as a final resting place for those who here gave their lives that that nation might live. It is altogether fitting and proper that we should do this. But, in a larger sense, we can not dedicate -- we can not consecrate -- we can not hallow -- this ground. The brave men, living and dead, who struggled here, have consecrated it, far above our poor power to add or detract. The world will little note, nor long remember what we say here, but it can never forget what they did here. It is for us the living, rather, to be dedicated here to the unfinished work which they who fought here have thus far so nobly advanced. It is rather for us to be here dedicated to the great task remaining before us -- that from these honored dead we take increased devotion to that cause for which they gave the last full measure of devotion -- that we here highly resolve that these dead shall not have died in vain -- that this nation, under God, shall have a new birth of freedom -- and that government of the people, by the people, for the people, shall not perish from the earth."
        };
        int len = testTexts.length;
        for(int i = 0; i < len; i++){
            testDoc = new DocumentImpl(uri, testTexts[i], null);
            Set<String> words = testDoc.getWords();
            for(String s : words){
                //System.out.println(i + ": " + s);
            }
        }
    }

    @Test
    public void getLastUseTimeTest(){
        //System.out.println(testDoc.getLastUseTime());
    }

    @Test
    public void setLastUseTimeTest(){
        /*System.out.println(testDoc.getLastUseTime());
        testDoc.setLastUseTime(System.nanoTime());
        System.out.println(testDoc.getLastUseTime());
        testDoc.setLastUseTime(System.nanoTime());
        System.out.println(testDoc.getLastUseTime());*/
    }

    @Test
    public void compareToTest() {
        testDoc = new DocumentImpl(uri, text, null);
        DocumentImpl testDoc2 = new DocumentImpl(uri2, text, null);
        assertEquals(testDoc.compareTo(testDoc2), -1);
        testDoc.setLastUseTime(System.nanoTime());
        assertEquals(testDoc.compareTo(testDoc2), 1);
    }


}
