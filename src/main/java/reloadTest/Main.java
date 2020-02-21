package reloadTest;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

    public static void main(String[] args) throws IOException {
        AtomicReference<Integer> atom = new AtomicReference();
        atom.compareAndSet(32, 32);
        System.out.println("");
    }
}
