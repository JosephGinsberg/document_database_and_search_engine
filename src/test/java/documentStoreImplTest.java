import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.yu.cs.com1320.project.stage6.*;
import edu.yu.cs.com1320.project.stage6.DocumentStore.*;
import edu.yu.cs.com1320.project.stage6.impl.*;

public class documentStoreImplTest {

    String uriBuilder = "https://www.baeldung.com/articles";
    URI uri = URI.create(uriBuilder);
    URI uri2 = URI.create(uriBuilder + "2");
    DocumentFormat TXT = DocumentStore.DocumentFormat.TXT;
    DocumentFormat BINARY = DocumentFormat.BINARY;
    InputStream NullInput = null;
    File file = new File("/Users/yosefginsberg/Documents/School/2024/Spring 2024/Data Structures/GitHub/Ginsberg_Joseph_800691019/DataStructures/project/stage6/src/main/resources/test.txt");
    File file2 = new File("/Users/yosefginsberg/Documents/School/2024/Spring 2024/Data Structures/GitHub/Ginsberg_Joseph_800691019/DataStructures/project/stage6/src/main/resources/test2.txt");
    String txt1 = "Four score and seven years ago our fathers brought forth on this continent, a new nation, conceived in Liberty, and dedicated to the proposition that all men are created equal. Now we are engaged in a great civil war, testing whether that nation, or any nation so conceived and so dedicated, can long endure. We are met on a great battle-field of that war. We have come to dedicate a portion of that field, as a final resting place for those who here gave their lives that that nation might live. It is altogether fitting and proper that we should do this. But, in a larger sense, we can not dedicate -- we can not consecrate -- we can not hallow -- this ground. The brave men, living and dead, who struggled here, have consecrated it, far above our poor power to add or detract. The world will little note, nor long remember what we say here, but it can never forget what they did here. It is for us the living, rather, to be dedicated here to the unfinished work which they who fought here have thus far so nobly advanced. It is rather for us to be here dedicated to the great task remaining before us -- that from these honored dead we take increased devotion to that cause for which they gave the last full measure of devotion -- that we here highly resolve that these dead shall not have died in vain -- that this nation, under God, shall have a new birth of freedom -- and that government of the people, by the people, for the people, shall not perish from the earth.";
    String txt2 = "In a hole in the ground there lived a hobbit. Not a nasty, dirty, wet hole, filled with the ends of worms and an oozy smell, nor yet a dry, bare, sandy hole with nothing in it to sit down on or to eat: it was a hobbit-hole, and that means comfort. It had a perfectly round door like a porthole, painted green, with a shiny yellow brass knob in the exact middle. The door opened on to a tube-shaped hall like a tunnel: a very comfortable tunnel without smoke, with panelled walls, and floors tiled and carpeted, provided with polished chairs, and lots and lots of pegs for hats and coats—the hobbit was fond of visitors. The tunnel wound on and on, going fairly but not quite straight into the side of the hill—The Hill, as all the people for many miles round called it—and many little round doors opened out of it, first on one side and then on another. No going upstairs for the hobbit: bedrooms, bathrooms, cellars, pantries (lots of these), wardrobes (he had whole rooms devoted to clothes), kitchens, dining-rooms, all were on the same floor, and indeed on the same passage. The best rooms were all on the left-hand side (going in), for these were the only ones to have windows, deep-set round windows looking over his garden, and meadows beyond, sloping down to the river. This hobbit was a very well-to-do hobbit, and his name was Baggins. The Bagginses had lived in the neighbourhood of The Hill for time out of mind, and people considered them very respectable, not only because most of them were rich, but also because they never had any adventures or did anything unexpected: you could tell what a Baggins would say on any question without the bother of asking him. This is a story of how a Baggins had an adventure, and found himself doing and saying things altogether unexpected. He may have lost the neighbours’ respect, but he gained—well, you will see whether he gained anything in the end.";
    DocumentImpl document = new DocumentImpl(uri, txt1, null);
    DocumentStoreImpl testStore;

    @BeforeEach
    public void beforeEach() {
       testStore = new DocumentStoreImpl();
    }

    @Test
    public void testPut() throws IOException{
        assertThrows(IllegalArgumentException.class, () ->
            testStore.put(NullInput, null, TXT));
        assertThrows(IllegalArgumentException.class, () ->
            testStore.put(NullInput, uri, null));
        assertEquals(testStore.put(NullInput, uri, TXT), 0);
        FileInputStream input = new FileInputStream(file);
        assertEquals(testStore.put(input, uri, TXT), 0);
        input = new FileInputStream(file);
        assertEquals(testStore.put(input, uri, TXT), document.hashCode());
        assertEquals(testStore.put(NullInput, uri, TXT), document.hashCode());
        assertEquals(testStore.put(NullInput, uri, TXT), 0);
    }

    @Test
    public void testSetMetaData() throws IOException{
        String key = "owner";
        String value = "Judah";
        assertThrows(IllegalArgumentException.class, () ->
            testStore.setMetadata(null, key, value));
        assertThrows(IllegalArgumentException.class, () ->
            testStore.setMetadata(uri, null, value));
        assertThrows(IllegalArgumentException.class, () ->
            testStore.setMetadata(uri, "  ", value));
        assertThrows(IllegalArgumentException.class, () ->
            testStore.setMetadata(uri, key, value));
        FileInputStream input = new FileInputStream(file);
        testStore.put(input, uri, TXT);
        assertNull(testStore.setMetadata(uri, key, value));
        assertEquals(testStore.setMetadata(uri, key, value), value);
    }

    @Test
    public void testGetMetaData() throws IOException{
        String key = "owner";
        String value = "Judah";
        assertThrows(IllegalArgumentException.class, () ->
            testStore.getMetadata(null, key));
        assertThrows(IllegalArgumentException.class, () ->
            testStore.getMetadata(uri, null));
        assertThrows(IllegalArgumentException.class, () ->
            testStore.getMetadata(uri, "  "));
        assertThrows(IllegalArgumentException.class, () ->
            testStore.getMetadata(uri, key));
        FileInputStream input = new FileInputStream(file);
        testStore.put(input, uri, TXT);
        assertNull(testStore.getMetadata(uri, key));
        testStore.setMetadata(uri, key, value);
        assertEquals(testStore.getMetadata(uri, key), value);
    }

    @Test
    public void testGet() throws IOException{
        FileInputStream input = new FileInputStream(file);
        testStore.put(input, uri, TXT);
        assertEquals(testStore.get(uri), document);
    }

    @Test
    public void testSearch() throws IOException{
        FileInputStream input = new FileInputStream(file);
        assertEquals(testStore.put(input, uri, TXT), 0);
        input = new FileInputStream(file2);
        assertEquals(testStore.put(input, uri2, TXT), 0);
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
    public void testSearchByPrefix() throws IOException{
        FileInputStream input = new FileInputStream(file);
        assertEquals(testStore.put(input, uri, TXT), 0);
        input = new FileInputStream(file2);
        assertEquals(testStore.put(input, uri2, TXT), 0);
        String[] toCount = {"a", "be", "de", "dis", "ex", "in", "mis", "non", "over", "pre", "re", "uni", "with"};
        for(int i = 0; i < toCount.length; i++){
            List<Document> wordList = testStore.searchByPrefix(toCount[i]);
            for(Document d : wordList){
                //System.out.println(toCount[i] + " | " + d.getKey());
            }
            //System.out.println();
        }
        assertEquals(testStore.searchByPrefix("").size(), 0);
        assertEquals(testStore.searchByPrefix(" ").size(), 0);
    }

    @Test
    public void testSearchByMetadata() throws IOException{
        FileInputStream input = new FileInputStream(file);
        assertEquals(testStore.put(input, uri, TXT), 0);
        input = new FileInputStream(file2);
        assertEquals(testStore.put(input, uri2, TXT), 0);
        String key = "key";
        String value = "value";
        Map<String, String> map = new HashMap<String, String>();
        assertEquals(testStore.searchByMetadata(map).size(), 0);
        for(int i = 0; i < 50; i++){
            assertNull(testStore.setMetadata(uri, key+i, value+i));
            assertNull(testStore.setMetadata(uri2, key+i, value+i));
            map.put(key+i, value+i);
        }
        List<Document> list = testStore.searchByMetadata(map);
        for(Document d : list){
            //System.out.println(list.size() + " " + d.getKey());
        }
    }

    @Test
    public void testSearchByKeywordAndMetadata() throws IOException{
        FileInputStream input = new FileInputStream(file);
        assertEquals(testStore.put(input, uri, TXT), 0);
        input = new FileInputStream(file2);
        assertEquals(testStore.put(input, uri2, TXT), 0);
        String key = "key";
        String value = "value";
        Map<String, String> map = new HashMap<String, String>();
        assertEquals(testStore.searchByMetadata(map).size(), 0);
        for(int i = 0; i < 50; i++){
            assertNull(testStore.setMetadata(uri, key+i, value+i));
            assertNull(testStore.setMetadata(uri2, key+i, value+i));
            map.put(key+i, value+i);
        }
        List<Document> list = testStore.searchByKeywordAndMetadata("Liberty", map);
        assertEquals(testStore.searchByKeywordAndMetadata("", map).size(), 0);
        assertEquals(testStore.searchByKeywordAndMetadata(" ", map).size(), 0);
        for(Document d : list){
            //System.out.println(d.getKey());
        }
    }
    
    @Test
    public void testSearchByPrefixAndMetadata() throws IOException{
        FileInputStream input = new FileInputStream(file);
        assertEquals(testStore.put(input, uri, TXT), 0);
        input = new FileInputStream(file2);
        assertEquals(testStore.put(input, uri2, TXT), 0);
        String key = "key";
        String value = "value";
        Map<String, String> map = new HashMap<String, String>();
        assertEquals(testStore.searchByMetadata(map).size(), 0);
        for(int i = 0; i < 50; i++){
            assertNull(testStore.setMetadata(uri, key+i, value+i));
            assertNull(testStore.setMetadata(uri2, key+i, value+i));
            map.put(key+i, value+i);
        }
        List<Document> list = testStore.searchByPrefixAndMetadata("co", map);
        assertEquals(testStore.searchByPrefixAndMetadata("", map).size(), 0);
        assertEquals(testStore.searchByPrefixAndMetadata(" ", map).size(), 0);
        for(Document d : list){
            //System.out.println(d.getKey());
        }
    }



    @Test
    public void testDelete() throws IOException{
        assertFalse(testStore.delete(uri));
        FileInputStream input = new FileInputStream(file);
        testStore.put(input, uri, TXT);
        assertTrue(testStore.delete(uri));
    }

    @Test
    public void testDeleteAll() throws IOException{
        FileInputStream input = new FileInputStream(file);
        assertEquals(testStore.put(input, uri, TXT), 0);
        input = new FileInputStream(file2);
        assertEquals(testStore.put(input, uri2, TXT), 0);
        Set<URI> uris = testStore.deleteAll("hobbithole");
        for(URI u : uris){
            //System.out.println(u);
        }
        //input = new FileInputStream(file);
        //assertEquals(testStore.put(input, uri2, TXT), 0);
        String[] toCount = {"the","of","and","a","to","in","is","you","that","it","he","was","for","on","are","as","with","his","they","I","at","be","this","have","from","or","one","had","by","word","but","not","what","all","were","we","when","your","can","said","there","use","an","each","which","she","do","how","their","if","will","up","other","about","out","many","then","them","these","so","some","her","would","make","like","him","into","time","has","look","two","more","write","go","see","number","no","way","could","people","my","than","first","water","been","call","who","oil","its","now","find","long","down","day","did","get","come","made","may","part"};
        for(int i = 0; i < toCount.length; i++){
            List<Document> wordList = testStore.search(toCount[i]);
            for(Document d : wordList){
                //System.out.println(toCount[i] + " | " + d.getKey() + " | " + d.wordCount(toCount[i]));
            }
            //System.out.println();
        }

        String[] toCount2 = {"a", "be", "de", "dis", "ex", "in", "mis", "non", "over", "pre", "re", "uni", "with"};
        for(int i = 0; i < toCount2.length; i++){
            List<Document> wordList = testStore.searchByPrefix(toCount2[i]);
            for(Document d : wordList){
                //System.out.println(toCount2[i] + " | " + d.getKey());
            }
            //System.out.println();
        }
    }

    @Test
    public void testDeleteAllWithPrefix() throws IOException{
        FileInputStream input = new FileInputStream(file);
        assertEquals(testStore.put(input, uri, TXT), 0);
        input = new FileInputStream(file2);
        assertEquals(testStore.put(input, uri2, TXT), 0);
        Set<URI> uris = testStore.deleteAllWithPrefix("prop");
        for(URI u : uris){
            //System.out.println(u);
        }
        //input = new FileInputStream(file);
        //assertEquals(testStore.put(input, uri2, TXT), 0);

        String[] toCount = {"the","of","and","a","to","in","is","you","that","it","he","was","for","on","are","as","with","his","they","I","at","be","this","have","from","or","one","had","by","word","but","not","what","all","were","we","when","your","can","said","there","use","an","each","which","she","do","how","their","if","will","up","other","about","out","many","then","them","these","so","some","her","would","make","like","him","into","time","has","look","two","more","write","go","see","number","no","way","could","people","my","than","first","water","been","call","who","oil","its","now","find","long","down","day","did","get","come","made","may","part"};
        for(int i = 0; i < toCount.length; i++){
            List<Document> wordList = testStore.search(toCount[i]);
            for(Document d : wordList){
                //System.out.println(toCount[i] + " | " + d.getKey() + " | " + d.wordCount(toCount[i]));
            }
            //System.out.println();
        }

        String[] toCount2 = {"a", "be", "de", "dis", "ex", "in", "mis", "non", "over", "pre", "re", "uni", "with"};
        for(int i = 0; i < toCount2.length; i++){
            List<Document> wordList = testStore.searchByPrefix(toCount2[i]);
            for(Document d : wordList){
                //System.out.println(toCount2[i] + " | " + d.getKey());
            }
            //System.out.println();
        }
    }


    @Test
    public void testUndoExceptions() throws IOException{
        assertThrows(IllegalStateException.class, () ->
        testStore.undo());
        assertThrows(IllegalStateException.class, () ->
        testStore.undo(uri));
        FileInputStream input = new FileInputStream(file);
        assertEquals(testStore.put(input, uri, TXT), 0);
        URI testUri1 = URI.create(uriBuilder + "1");
        assertThrows(IllegalStateException.class, () ->
        testStore.undo(testUri1));
        testStore.undo(uri);
        assertThrows(IllegalStateException.class, () ->
        testStore.undo(uri));
    }

    @Test
    public void testMetadataUndo() throws IOException{
        String key = "owner";
        String value = "Judah";
        FileInputStream input = new FileInputStream(file);
        testStore.put(input, uri, TXT);
        assertNull(testStore.setMetadata(uri, key, value));
        testStore.undo();
        assertNull(testStore.getMetadata(uri, key));
        assertNull(testStore.setMetadata(uri, key, value));
        testStore.undo(uri);
        assertNull(testStore.getMetadata(uri, key));
        for(int i = 1; i <= 100; i++){
            assertNull(testStore.setMetadata(uri, key + i, value + i));
        }
        assertEquals(testStore.getMetadata(uri, key + "100"), "Judah100"); 
        assertNull(testStore.getMetadata(uri, key));
        testStore.undo(uri);
        assertNull(testStore.getMetadata(uri, key + "100"));
        assertEquals(testStore.getMetadata(uri, key + "99"), "Judah99"); 
        for(int i = 1; i <= 97; i++){
            testStore.undo(uri);
        }
        assertEquals(testStore.getMetadata(uri, key + "2"), "Judah2"); 
        assertNull(testStore.getMetadata(uri, key));
        assertEquals(testStore.getMetadata(uri, key + "1"), "Judah1");
        for(int i = 3; i <= 100; i++){
            assertNull(testStore.getMetadata(uri, key + i));
        }
    }

    @Test
    public void testPutUndo() throws IOException{
        FileInputStream input = new FileInputStream(file);
        assertEquals(testStore.put(input, uri, TXT), 0);
        testStore.undo(uri);
        assertNull(testStore.get(uri));
        assertEquals(testStore.searchByPrefix("a").size(), 0);
        input = new FileInputStream(file);
        assertEquals(testStore.put(input, uri, TXT), 0);
        input = new FileInputStream(file2);
        assertEquals(testStore.put(input, uri, TXT), document.hashCode());
        assertNotEquals(testStore.get(uri), document);
        testStore.undo(uri);
        assertEquals(testStore.get(uri), document);
    }

    @Test
    public void testDeleteUndo() throws IOException{
        assertFalse(testStore.delete(uri));
        FileInputStream input = new FileInputStream(file);
        assertEquals(testStore.put(input, uri, TXT), 0);
        assertTrue(testStore.delete(uri));
        testStore.undo(uri);
        assertEquals(testStore.get(uri), document);
    }

    @Test
    public void testDeleteAllUndo() throws IOException{
        assertFalse(testStore.delete(uri));
        FileInputStream input = new FileInputStream(file);
        assertEquals(testStore.put(input, uri, TXT), 0);
        input = new FileInputStream(file2);
        assertEquals(testStore.put(input, uri2, TXT), 0);
        assertEquals(testStore.search("a").size(), 2);
        testStore.deleteAll("a");
        assertNull(testStore.get(uri));
        assertNull(testStore.get(uri2));
        assertEquals(testStore.search("a").size(), 0);
        testStore.undo(uri);
        assertEquals(testStore.search("a").size(), 1);
        assertEquals(testStore.get(uri), document);
        assertNull(testStore.get(uri2));
        testStore.undo(uri2);
        assertEquals(testStore.search("a").size(), 2);
        testStore.deleteAll("a");
        assertNull(testStore.get(uri));
        assertNull(testStore.get(uri2));
        testStore.undo();
        assertEquals(testStore.search("a").size(), 2);
        assertNotNull(testStore.get(uri));
        assertNotNull(testStore.get(uri2));
        testStore.undo();
        assertNull(testStore.get(uri2));
        testStore.undo();
        assertNull(testStore.get(uri));
        assertThrows(IllegalStateException.class, () ->
        testStore.undo(uri));
        assertThrows(IllegalStateException.class, () ->
        testStore.undo(uri2));
        assertThrows(IllegalStateException.class, () ->
        testStore.undo());
    }

    @Test
    public void testDeleteAllWithPrefixUndo() throws IOException{
        assertFalse(testStore.delete(uri));
        FileInputStream input = new FileInputStream(file);
        assertEquals(testStore.put(input, uri, TXT), 0);
        input = new FileInputStream(file2);
        assertEquals(testStore.put(input, uri2, TXT), 0);
        assertEquals(testStore.searchByPrefix("a").size(), 2);
        testStore.deleteAll("a");
        assertNull(testStore.get(uri));
        assertNull(testStore.get(uri2));
        assertEquals(testStore.searchByPrefix("a").size(), 0);
        testStore.undo(uri);
        assertEquals(testStore.searchByPrefix("a").size(), 1);
        assertEquals(testStore.get(uri), document);
        assertNull(testStore.get(uri2));
        testStore.undo(uri2);
        assertEquals(testStore.searchByPrefix("a").size(), 2);
        testStore.deleteAll("a");
        assertNull(testStore.get(uri));
        assertNull(testStore.get(uri2));
        testStore.undo();
        assertEquals(testStore.searchByPrefix("a").size(), 2);
        assertNotNull(testStore.get(uri));
        assertNotNull(testStore.get(uri2));
        testStore.undo();
        assertNull(testStore.get(uri2));
        testStore.undo();
        assertNull(testStore.get(uri));
        assertThrows(IllegalStateException.class, () ->
        testStore.undo(uri));
        assertThrows(IllegalStateException.class, () ->
        testStore.undo(uri2));
        assertThrows(IllegalStateException.class, () ->
        testStore.undo());
    }

    @Test
    public void testAllUndo() throws IOException{
        for(int i = 1; i <= 100; i++){
            URI loopUri = URI.create(uriBuilder + i);
            FileInputStream input = new FileInputStream(file);
            assertEquals(testStore.put(input, loopUri, TXT), 0);
        }
        testStore.undo();
        URI testUri100 = URI.create(uriBuilder + "100");
        assertNull(testStore.get(testUri100));
        URI testUri50 = URI.create(uriBuilder + "50");
        testStore.undo(testUri50);
        assertNull(testStore.get(testUri50));
        for(int i = 1; i <= 99; i++){
            if(i == 50){
                i++;
            }
            URI loopUri = URI.create(uriBuilder + i);
            assertNotNull(testStore.get(loopUri));
        }
        FileInputStream input = new FileInputStream(file);
        assertEquals(testStore.put(input, testUri100, TXT), 0);
        input = new FileInputStream(file);
        assertEquals(testStore.put(input, testUri50, TXT), 0);
        String key = "owner";
        String value = "Judah";
        for(int i = 1; i <= 100; i++){
            URI loopUri = URI.create(uriBuilder + i);
            assertNull(testStore.setMetadata(loopUri, key + i, value + i));
        }
        assertEquals(testStore.getMetadata(testUri50, key + "50"), "Judah50"); 
        testStore.undo(testUri50);
        assertNull(testStore.getMetadata(testUri50, key + "50"));
        URI testUri49 = URI.create(uriBuilder + "49");
        assertNotNull(testStore.get(testUri49));
        assertTrue(testStore.delete(testUri49));
        assertEquals(testStore.getMetadata(testUri100, key + "100"), "Judah100"); 
        testStore.undo(testUri100);
        assertNull(testStore.getMetadata(testUri50, key + "100"));
        testStore.undo(testUri50);
        assertNull(testStore.get(testUri50));
        testStore.undo(testUri49);
        assertNotNull(testStore.get(testUri49));
        assertEquals(testStore.getMetadata(testUri49, key + "49"), "Judah49"); 
    }

}