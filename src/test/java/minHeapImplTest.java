import org.junit.jupiter.api.*;

import edu.yu.cs.com1320.project.impl.MinHeapImpl;

import static org.junit.jupiter.api.Assertions.*;

public class minHeapImplTest {
    
    public interface stringIn extends Comparable<stringIn>{

        public String getString();

        public void setString(String s);

        public long getLastUseTime();

        public void setLastUseTime(long timeInNanoseconds);

    }

    public class stringOb implements stringIn{

        String str;
        long lastUseTime;

        public stringOb(String s){
            this.str = s;
            lastUseTime = System.nanoTime();
        }

        public String getString(){
            return this.str;
        }

        public void setString(String s){
            this.str = s;
        }

        public long getLastUseTime(){
            return this.lastUseTime;
        }
    
        public void setLastUseTime(long timeInNanoseconds){
            this.lastUseTime = timeInNanoseconds;
        }

        public int compareTo(stringIn s) {
            if(this.lastUseTime > s.getLastUseTime()){
                return 1;
            }
            else if(this.getLastUseTime() < s.getLastUseTime()){
                return -1;
            }
            else{
                return 0;
            }
        }

    }

    MinHeapImpl<stringIn> heap;

    @BeforeEach
    public void beforeEach() {
        heap = new MinHeapImpl<stringIn>();
    }

    @Test
    public void insertTest(){
        String base = "";
        for(int i = 0; i < 100; i++){
            stringOb s = new stringOb(base + i);
            heap.insert(s);
        }
        for(int i = 0; i < 100; i++){
            assertEquals(base + i, heap.remove().getString());
            //System.out.println(i + " | " + heap.remove().getString());
        }
    }

    @Test
    public void reHeapifyTest(){
        stringOb outside = new stringOb("");
        long nanotime = 0;
        long nanotime2 = 0;
        String base = "";
        for(int i = 0; i < 100; i++){
            stringOb s = new stringOb(base + i);
            heap.insert(s);
            if(i == 0){
                outside = s;
            }
            if(i == 55){
                nanotime = System.nanoTime();
            }
            if(i == 0){
                nanotime2 = System.nanoTime();
            }
        }
        //outside.setString("q");
        outside.setLastUseTime(nanotime);
        heap.reHeapify(outside);
        outside.setLastUseTime(nanotime2);
        heap.reHeapify(outside);
        for(int i = 0; i < 100; i++){
            assertEquals(base + i, heap.remove().getString());
            //System.out.println(i + " | " + heap.remove().getString());
        }


    }

}
