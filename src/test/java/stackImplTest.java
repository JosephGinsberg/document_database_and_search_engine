import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import edu.yu.cs.com1320.project.impl.StackImpl;

public class stackImplTest {

    StackImpl<String> stack;
    
    @BeforeEach
    public void beforeEach() {
        stack = new StackImpl<String>();
    }

    @Test
    public void testAll(){
        String tester = "hello";
        assertThrows(IllegalArgumentException.class, () ->
            stack.push(null));
        stack.push(tester);
        assertEquals(tester, stack.peek());
        assertEquals(1, stack.size());
        assertEquals(tester, stack.pop());
        assertEquals(0, stack.size());
        assertNull(stack.peek());
        assertNull(stack.pop());
        for(int i = 1; i <= 100; i++){
            stack.push(tester + i);
            assertEquals(i, stack.size());
        }
        assertEquals(tester + "100", stack.peek());
        assertEquals(tester + "100", stack.pop());
        assertEquals(99, stack.size());
    }



}
