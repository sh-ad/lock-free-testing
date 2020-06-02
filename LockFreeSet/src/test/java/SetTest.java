import org.LockFreeSet.Set;
import org.LockFreeSet.SetImpl;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class SetTest {
    @Test
    public void simpleOperations() {
        Set<Integer> mySet = new SetImpl<>();
        assertTrue(mySet.isEmpty());
        assertTrue(mySet.add(10));
        assertFalse(mySet.add(10));
        assertFalse(mySet.isEmpty());
        assertTrue(mySet.remove(10));
        assertTrue(mySet.isEmpty());
        assertTrue(mySet.add(20));
        assertTrue(mySet.add(30));
        assertFalse(mySet.add(20));
        assertFalse(mySet.remove(10));
        assertFalse(mySet.contains(10));
        assertTrue(mySet.contains(20));
        assertTrue(mySet.contains(30));
        assertTrue(mySet.add(10));
        assertTrue(mySet.contains(10));
        assertTrue(mySet.add(15));
        assertTrue(mySet.contains(20));
        assertTrue(mySet.contains(30));
        assertTrue(mySet.contains(15));
        assertFalse(mySet.isEmpty());
    }
    @Test
    public void iteratorTest() {
        Set<Integer> mySet = new SetImpl<>();
        Integer a  = 10;
        Integer b  = 20;
        Integer c  = 30;
        assertTrue(mySet.add(a));
        assertTrue(mySet.add(c));
        assertTrue(mySet.add(b));
        Iterator<Integer> iterator = mySet.iterator();
        assertNotNull(iterator);
        assertTrue(mySet.remove(c));
        assertTrue(iterator.hasNext());
        assertEquals(a, iterator.next());
        assertEquals(b, iterator.next()); //sorted
        assertEquals(c, iterator.next()); //still exists in snapshot
        assertFalse(iterator.hasNext());
        Iterator<Integer> secondIterator = mySet.iterator();
        assertTrue(secondIterator.hasNext());
        assertEquals(a, secondIterator.next());
        assertEquals(b, secondIterator.next());
        assertFalse(secondIterator.hasNext());
    }
}
