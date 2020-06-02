package org.LockFreeSet;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.I_Result;
import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.FORBIDDEN;

public class SetStressTest {
    @JCStressTest
    @Outcome(id = "0", expect = FORBIDDEN,  desc = "Addition error.")
    @Outcome(id = "1", expect = FORBIDDEN,  desc = "Remove error.")
    @Outcome(id = "2", expect = FORBIDDEN,  desc = "Values exists after removing.")
    @Outcome(id = "3", expect = ACCEPTABLE, desc = "Correct addition and removing.")
    @State
    public static class AddAndRemoveTest {
        private Set<Integer> set = new SetImpl<>();

        private int addAndRemove(int first, int second) {
            set.add(first);
            set.add(second);
            if(set.contains(first) && set.contains(second))
                if(set.remove(first) && set.remove(second))
                    if(!set.contains(first) && !set.contains(second))
                        return 3;
                    else
                        return 2;
                else
                    return 1;
            else
                return 0;
        }

        @Actor
        public void actor1(I_Result r) {
            r.r1 = addAndRemove(1, 3);
        }

        @Actor
        public void actor2(I_Result r) {
            r.r1 = addAndRemove(2, 4);
        }

        @Actor
        public void actor3(I_Result r) {
            r.r1 = addAndRemove(0, 5);
        }
    }
}


