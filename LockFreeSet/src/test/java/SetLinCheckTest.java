import org.LockFreeSet.Set;
import org.LockFreeSet.SetImpl;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.paramgen.IntGen;
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTest;
import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

@Param(name = "key", gen = IntGen.class, conf = "1:5")
@StressCTest
public class SetLinCheckTest {
    private Set<Integer> set = new SetImpl<>();

    @Operation
    public boolean add(@Param(name = "key") int key) {
        return set.add(key);
    }

    @Operation
    public boolean remove(@Param(name = "key") int key) {
        return set.remove(key);
    }

    @Operation
    public boolean contains(@Param(name = "key") int key) {
        return set.contains(key);
    }

    @Operation
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @Operation
    public Integer iteratorNext() {
        Iterator iterator = set.iterator();
        try {
            return (Integer)iterator.next();
        }
        catch(NoSuchElementException e) {
            return null;
        }
    }

    @Operation
    public boolean hasNext() {
        Iterator iterator = set.iterator();
        return iterator.hasNext();
    }

    @Test
    public void runTest() {
        LinChecker.check(SetLinCheckTest.class);
    }
}


