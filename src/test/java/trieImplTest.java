import org.junit.jupiter.api.*;

import edu.yu.cs.com1320.project.impl.TrieImpl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class trieImplTest {

    TrieImpl<String> trie;
    
    @BeforeEach
    public void beforeEach() {
       trie = new TrieImpl<String>();
    }

    @Test
    public void testPut(){
        assertEquals(trie.get("hello").size(), 0);
        trie.put("hello", "hellovalue");
        assertTrue(trie.get("hello").contains("hellovalue"));
        trie.put("hello", "hellovalue2");
        assertEquals(trie.get("hello").size(), 2);
        assertTrue(trie.get("hello").contains("hellovalue"));
        assertTrue(trie.get("hello").contains("hellovalue2"));
        trie.put("heathen", "heanthenvalue");
        assertTrue(trie.get("heathen").contains("heanthenvalue"));
        trie.put("hefty", "heftyvalue");
        assertTrue(trie.get("hefty").contains("heftyvalue"));
        trie.put("Hello", "Hellovalue");
        assertTrue(trie.get("Hello").contains("Hellovalue"));
        assertFalse(trie.get("hello").contains("Hellovalue"));
        assertFalse(trie.get("Hello").contains("hellovalue"));
    }

    @Test
    public void testDelete(){
        trie.put("hello", "hellovalue");
        assertThrows(IllegalArgumentException.class, () ->
        trie.delete(null, "hellovalue"));
        assertThrows(IllegalArgumentException.class, () ->
        trie.delete("hellovalue", null));
        trie.put("hello", "hellovalue2");
        trie.put("Hello", "Hellovalue");
        trie.put("heathen", "heanthenvalue");
        trie.put("hefty", "heftyvalue");
        assertEquals(trie.delete("hello", "hellovalue"), "hellovalue");
        assertFalse(trie.get("hello").contains("hellovalue"));
        assertTrue(trie.get("hello").contains("hellovalue2"));
        assertTrue(trie.get("Hello").contains("Hellovalue"));
        assertEquals(trie.get("hello").size(), 1);
        assertNull(trie.delete("hello", "hellovalue"));
        assertEquals(trie.delete("Hello", "Hellovalue"), "Hellovalue");
    }

    @Test
    public void testDeleteAll(){
        assertThrows(IllegalArgumentException.class, () ->
        trie.deleteAll(null));
        trie.put("hello", "hellovalue");
        trie.put("hello", "hellovalue2");
        trie.put("Hello", "Hellovalue");
        Set<String> deleteSet = trie.deleteAll("hello");
        assertFalse(trie.get("hello").contains("hellovalue"));
        assertFalse(trie.get("hello").contains("hellovalue2"));
        assertEquals(trie.get("hello").size(), 0);
        assertTrue(deleteSet.contains("hellovalue"));
        assertTrue(deleteSet.contains("hellovalue2"));
        assertTrue(trie.get("Hello").contains("Hellovalue"));
    }

    @Test
    public void testDeleteAllWithPrefix(){
        assertThrows(IllegalArgumentException.class, () ->
        trie.deleteAllWithPrefix(null));
        trie.put("cow", "cowvalue");
        trie.put("then", "thenvalue");
        trie.put("hither", "hithervalue");
        trie.put("h", "hvalue");
        trie.put("he", "hevalue");
        trie.put("hello", "hellovalue");
        trie.put("hello", "hellovalue2");
        trie.put("heathen", "heanthenvalue");
        trie.put("hefty", "heftyvalue");
        trie.put("H", "Hvalue");
        trie.put("He", "Hevalue");
        trie.put("HE", "HEvalue");
        trie.put("Hello", "Hellovalue");
        Set<String> deleteSet = trie.deleteAllWithPrefix("he");
        assertFalse(trie.get("hello").contains("hellovalue"));
        assertFalse(trie.get("hello").contains("hellovalue2"));
        assertFalse(trie.get("heathen").contains("heanthenvalue"));
        assertFalse(trie.get("hefty").contains("heftyvalue"));
        assertFalse(trie.get("he").contains("hevalue"));
        assertEquals(trie.get("hello").size(), 0);
        assertEquals(trie.get("heathen").size(), 0);
        assertEquals(trie.get("hefty").size(), 0);
        assertEquals(trie.get("he").size(), 0);
        assertTrue(deleteSet.contains("hellovalue"));
        assertTrue(deleteSet.contains("hellovalue2"));
        assertTrue(deleteSet.contains("heanthenvalue"));
        assertTrue(deleteSet.contains("heftyvalue"));
        assertTrue(deleteSet.contains("hevalue"));
        assertTrue(trie.get("cow").contains("cowvalue"));
        assertTrue(trie.get("then").contains("thenvalue"));
        assertTrue(trie.get("hither").contains("hithervalue"));
        assertTrue(trie.get("h").contains("hvalue"));
        assertTrue(trie.get("H").contains("Hvalue"));
        assertTrue(trie.get("He").contains("Hevalue"));
        assertTrue(trie.get("HE").contains("HEvalue"));
        assertTrue(trie.get("Hello").contains("Hellovalue"));
    }

    private class stringComparator implements Comparator<String> {

        @Override
        public int compare(String s1, String s2){
            return s1.compareTo(s2);
        }

    }

    @Test
    public void testGetSorted(){
        String key = "hello";
        String[] values = {"c", "a", "f", "b"};
        int len = values.length;
        for(int i = 0; i < len; i++){
            trie.put(key, values[i]);
        }
        List<String> sorted = trie.getSorted("hello", new stringComparator());
        assertTrue(len == sorted.size());
        for(int i = 0; i < len; i++){
            assertTrue(sorted.contains(values[i]));
            //System.out.println(sorted.get(i));
        } 
        assertThrows(IllegalArgumentException.class, () ->
        trie.getSorted("hello", null));
        assertThrows(IllegalArgumentException.class, () ->
        trie.getSorted(null, new stringComparator()));
    }

    @Test
    public void testGetAllWithPrefixSorted(){
        String[] keys = {"he", "hello", "hello", "hello", "heathen", "hefty", "he", "her", "heft", "heaven", "help", "her", "hermitian", "heel", "hercules"};
        String[] values = {"z", "c", "I", "a", "f", "b", "k", "q", "A", "C", "B", "Z", "z", "i", "c"};
        int len = values.length;
        assertTrue(keys.length == len);
        for(int i = 0; i < len; i++){
            trie.put(keys[i], values[i]);
        }
        trie.put("h", "M");
        trie.put("hi", "N");
        List<String> sorted = trie.getAllWithPrefixSorted("he", new stringComparator());
        //System.out.println(len);
        //System.out.println(sorted.size());
        for(int i = 0; i < len; i++){
            assertTrue(sorted.contains(values[i]));
            /*if(i < sorted.size()){
                System.out.println(sorted.get(i));
            }*/
        } 
        assertFalse(sorted.contains("M"));
        assertFalse(sorted.contains("N"));
        assertTrue(trie.get("h").contains("M"));
        assertTrue(trie.get("hi").contains("N"));
        assertThrows(IllegalArgumentException.class, () ->
        trie.getSorted("hello", null));
        assertThrows(IllegalArgumentException.class, () ->
        trie.getSorted(null, new stringComparator()));
    }


}
