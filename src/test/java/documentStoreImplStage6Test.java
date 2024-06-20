import org.junit.jupiter.api.*;

import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.DocumentStore;
import edu.yu.cs.com1320.project.stage6.DocumentStore.DocumentFormat;
import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage6.impl.DocumentPersistenceManager;
import edu.yu.cs.com1320.project.stage6.impl.DocumentStoreImpl;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;


public class documentStoreImplStage6Test {
    
    File path = new File("/Users/yosefginsberg/Downloads/test");
    File file = new File("/Users/yosefginsberg/Documents/School/2024/Spring 2024/Data Structures/GitHub/Ginsberg_Joseph_800691019/DataStructures/project/stage6/src/main/resources/test.txt");
    File file2 = new File("/Users/yosefginsberg/Documents/School/2024/Spring 2024/Data Structures/GitHub/Ginsberg_Joseph_800691019/DataStructures/project/stage6/src/main/resources/test2.txt");
    URI textUri = URI.create("https://www.baeldung.com/text");
    URI textUri2 = URI.create("https://www.baeldung.com/text2");
    URI binaryUri = URI.create("https://www.baeldung.com/binary");
    String txt1 = "Four score and seven years ago our fathers brought forth on this continent, a new nation, conceived in Liberty, and dedicated to the proposition that all men are created equal. Now we are engaged in a great civil war, testing whether that nation, or any nation so conceived and so dedicated, can long endure. We are met on a great battle-field of that war. We have come to dedicate a portion of that field, as a final resting place for those who here gave their lives that that nation might live. It is altogether fitting and proper that we should do this. But, in a larger sense, we can not dedicate -- we can not consecrate -- we can not hallow -- this ground. The brave men, living and dead, who struggled here, have consecrated it, far above our poor power to add or detract. The world will little note, nor long remember what we say here, but it can never forget what they did here. It is for us the living, rather, to be dedicated here to the unfinished work which they who fought here have thus far so nobly advanced. It is rather for us to be here dedicated to the great task remaining before us -- that from these honored dead we take increased devotion to that cause for which they gave the last full measure of devotion -- that we here highly resolve that these dead shall not have died in vain -- that this nation, under God, shall have a new birth of freedom -- and that government of the people, by the people, for the people, shall not perish from the earth.";
    String txt2 = "In a hole in the ground there lived a hobbit. Not a nasty, dirty, wet hole, filled with the ends of worms and an oozy smell, nor yet a dry, bare, sandy hole with nothing in it to sit down on or to eat: it was a hobbit-hole, and that means comfort. It had a perfectly round door like a porthole, painted green, with a shiny yellow brass knob in the exact middle. The door opened on to a tube-shaped hall like a tunnel: a very comfortable tunnel without smoke, with panelled walls, and floors tiled and carpeted, provided with polished chairs, and lots and lots of pegs for hats and coats—the hobbit was fond of visitors. The tunnel wound on and on, going fairly but not quite straight into the side of the hill—The Hill, as all the people for many miles round called it—and many little round doors opened out of it, first on one side and then on another. No going upstairs for the hobbit: bedrooms, bathrooms, cellars, pantries (lots of these), wardrobes (he had whole rooms devoted to clothes), kitchens, dining-rooms, all were on the same floor, and indeed on the same passage. The best rooms were all on the left-hand side (going in), for these were the only ones to have windows, deep-set round windows looking over his garden, and meadows beyond, sloping down to the river. This hobbit was a very well-to-do hobbit, and his name was Baggins. The Bagginses had lived in the neighbourhood of The Hill for time out of mind, and people considered them very respectable, not only because most of them were rich, but also because they never had any adventures or did anything unexpected: you could tell what a Baggins would say on any question without the bother of asking him. This is a story of how a Baggins had an adventure, and found himself doing and saying things altogether unexpected. He may have lost the neighbours’ respect, but he gained—well, you will see whether he gained anything in the end.";
    public String testText = "Hello, this is a document";
    byte[] binaryData = {00000001};
    String key = "owner";
    String value = "Judah";
    DocumentFormat TXT = DocumentStore.DocumentFormat.TXT;
    DocumentFormat BINARY = DocumentFormat.BINARY;
    InputStream NullInput = null;
    DocumentImpl textDoc;
    DocumentImpl binaryDoc;
    DocumentPersistenceManager manager;
    DocumentStoreImpl testStore;
    DocumentImpl textDocument;
    DocumentImpl binaryDocument;

    @BeforeEach
    public void beforeEach() {
       testStore = new DocumentStoreImpl(path);
       textDocument = new DocumentImpl(textUri, txt1, null);
       binaryDocument = new DocumentImpl(binaryUri, binaryData);
    }

    @Test
    public void testPutExceptions() throws IOException{
        assertThrows(IllegalArgumentException.class, () ->
            testStore.put(NullInput, null, TXT));
        assertThrows(IllegalArgumentException.class, () ->
            testStore.put(NullInput, textUri, null));
        assertThrows(IllegalArgumentException.class, () ->
            testStore.put(NullInput, null, BINARY));
        assertThrows(IllegalArgumentException.class, () ->
            testStore.put(NullInput, binaryUri, null));
    }


    @Test
    public void testPut() throws IOException{
        FileInputStream input = new FileInputStream(file);
        assertEquals(testStore.put(input, textUri, TXT), 0);
        input = new FileInputStream(file);
        assertEquals(testStore.put(input, textUri, TXT), textDocument.hashCode());
        assertEquals(testStore.put(NullInput, textUri, TXT), textDocument.hashCode());
        assertEquals(testStore.put(NullInput, textUri, TXT), 0);
    }

    @Test
    public void testSetMetaDataExceptions() throws IOException{
        assertThrows(IllegalArgumentException.class, () ->
            testStore.setMetadata(null, key, value));
        assertThrows(IllegalArgumentException.class, () ->
            testStore.setMetadata(textUri, null, value));
        assertThrows(IllegalArgumentException.class, () ->
            testStore.setMetadata(textUri, "  ", value));
        assertThrows(IllegalArgumentException.class, () ->
            testStore.setMetadata(textUri, key, value));
    }

    @Test
    public void testSetMetaData() throws IOException{
        FileInputStream input = new FileInputStream(file);
        testStore.put(input, textUri, TXT);
        assertNull(testStore.setMetadata(textUri, key, value));
        assertEquals(testStore.setMetadata(textUri, key, value), value);
        assertEquals(testStore.setMetadata(textUri, key, value + "1"), value);
    }

    @Test
    public void testGetMetaDataExceptions() throws IOException{
        assertThrows(IllegalArgumentException.class, () ->
            testStore.getMetadata(null, key));
        assertThrows(IllegalArgumentException.class, () ->
            testStore.getMetadata(textUri, null));
        assertThrows(IllegalArgumentException.class, () ->
            testStore.getMetadata(textUri, "  "));
        assertThrows(IllegalArgumentException.class, () ->
            testStore.getMetadata(textUri, key));
    }

    @Test
    public void testGetMetaData() throws IOException{
        FileInputStream input = new FileInputStream(file);
        testStore.put(input, textUri, TXT);
        assertNull(testStore.getMetadata(textUri, key));
        testStore.setMetadata(textUri, key, value);
        assertEquals(testStore.getMetadata(textUri, key), value);
        assertEquals(testStore.setMetadata(textUri, key, value + "1"), value);
        assertEquals(testStore.getMetadata(textUri, key), value + "1");
    }

    @Test
    public void testGet() throws IOException{
        FileInputStream input = new FileInputStream(file);
        testStore.put(input, textUri, TXT);
        assertEquals(testStore.get(textUri), textDocument);
    }

     @Test
    public void testSearch() throws IOException{
        FileInputStream input = new FileInputStream(file);
        assertEquals(testStore.put(input, textUri, TXT), 0);
        input = new FileInputStream(file2);
        assertEquals(testStore.put(input, textUri2, TXT), 0);
        String[] toCount = {"the","of","and","a","to","in","is","you","that","it","he","was","for","on","are","as","with","his","they","I","at","be","this","have","from","or","one","had","by","word","but","not","what","all","were","we","when","your","can","said","there","use","an","each","which","she","do","how","their","if","will","up","other","about","out","many","then","them","these","so","some","her","would","make","like","him","into","time","has","look","two","more","write","go","see","number","no","way","could","people","my","than","first","water","been","call","who","oil","its","now","find","long","down","day","did","get","come","made","may","part"};
        for(int i = 0; i < toCount.length; i++){
            List<Document> wordList = testStore.search(toCount[i]);
            for(Document d : wordList){
                //System.out.println(toCount[i] + " | " + d.getKey() + " | " + d.wordCount(toCount[i]));
            }
            //System.out.println();
        }
        assertEquals(testStore.search("").size(), 0);
        assertEquals(testStore.search(" ").size(), 0);
    }

    @Test
    public void testlaterSetMaxDocumentCount() throws IOException{
        int size = 8;
        for(int i = 1; i <= 100; i++){
            URI loopUri = URI.create(textUri + "" + i);
            FileInputStream input = new FileInputStream(file);
            assertEquals(testStore.put(input, loopUri, TXT), 0);
        }
        int[] randoms = {3, 57, 59, 99, 1, 22, 88, 50};
        for(int i = 0; i < 8; i++){
            URI loopUri = URI.create(textUri + "" + randoms[i]);
            testStore.setMetadata(loopUri, "key", "value");
        }
        testStore.setMaxDocumentCount(size);
    }

    
    @Test
    public void testSetMaxDocumentCount() throws IOException{
        int size = 8;
        testStore.setMaxDocumentCount(size);
        for(int i = 1; i <= 100; i++){
            URI loopUri = URI.create(textUri + "" + i);
            FileInputStream input = new FileInputStream(file);
            assertEquals(testStore.put(input, loopUri, TXT), 0);
        }
        for(int i = 56; i < 60; i++){
            URI loopUri = URI.create(textUri + "" + i);
            testStore.setMetadata(loopUri, "key", "value");
        }
    }


}
