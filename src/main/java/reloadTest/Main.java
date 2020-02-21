package reloadTest;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

    public static void main(String[] args) throws IOException {
        AtomicReference<Integer> atom = new AtomicReference(32);
        atom.compareAndSet(32, 64);
        System.out.println(atom);
    }
}
