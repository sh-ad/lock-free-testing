package org.LockFreeSet;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class SetImpl<T extends Comparable<T>> implements Set<T> {
    private class Node<N extends Comparable<N>> {
        private AtomicMarkableReference<Node<N>> next;
        private N value;
        private Node() {
            next = new AtomicMarkableReference<>(null, false);
            value = null;
        }
    }
    private AtomicMarkableReference<Node<T>> head = new AtomicMarkableReference<>(null, false);

    @Override
    public boolean add(T value) {
        Node<T> newNode = new Node<>();
        newNode.value = value;
        while(true) {
            tryRemoveGarbage(head);
            AtomicMarkableReference<Node<T>> currentPtr = head;
            Node<T> current = currentPtr.getReference();
            while (current != null) {
                if(current.value.equals(value)) {
                    if (!current.next.isMarked()) {
                        return false;
                    }
                }
                currentPtr = current.next;
                tryRemoveGarbage(currentPtr);
                current = currentPtr.getReference();
            }
            if(currentPtr.compareAndSet(null, newNode, false, false)) {
                return true;
            }
        }
    }

    @Override
    public boolean remove(T value) {
        while(true) {
            tryRemoveGarbage(head);
            AtomicMarkableReference<Node<T>> currentPtr = head;
            Node<T> current = currentPtr.getReference();
            while (current != null) {
                if(current.value.equals(value)) {
                    if (!current.next.isMarked()) {
                        Node<T> nextNode = current.next.getReference();
                        if(current.next.compareAndSet(nextNode, nextNode, false, true)) {
                            tryRemoveGarbage(currentPtr);
                            return true;
                        }
                        break;
                    }
                }
                currentPtr = current.next;
                tryRemoveGarbage(currentPtr);
                current = currentPtr.getReference();
            }
            if(current == null)
                return false;
        }
    }

    @Override
    public boolean contains(T value) {
        tryRemoveGarbage(head);
        AtomicMarkableReference<Node<T>> currentPtr = head;
        Node<T> current = currentPtr.getReference();
        while (current != null) {
            if(current.value.equals(value)) {
                if (!current.next.isMarked()) {
                    return true;
                }
            }
            currentPtr = current.next;
            tryRemoveGarbage(currentPtr);
            current = currentPtr.getReference();
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        while(true) {
            while(tryRemoveGarbage(head));
            Node<T> current = head.getReference();
            if (current == null)
                return true;
            if(current.next.isMarked())
                continue;
            return false;
        }
    }

    private List<T> getSnapshot() {
        List<Node<T>> nodeSnapshot = new LinkedList<>();
        while(true) {
            Node<T> current = head.getReference();
            while (current != null) {
                if (!current.next.isMarked()) {
                    nodeSnapshot.add(current);
                }
                current = current.next.getReference();
            }
            Iterator<Node<T>> snapshotIterator = nodeSnapshot.iterator();
            current = head.getReference();
            while (current != null) {
                if (!current.next.isMarked()) {
                    try {
                        Node<T> currentSnapshotNode = snapshotIterator.next();
                        if (currentSnapshotNode != current)
                            break;
                    }
                    catch (NoSuchElementException e) {
                        break;
                    }
                }
                current = current.next.getReference();
            }
            if(current != null || snapshotIterator.hasNext()) {
                nodeSnapshot.clear();
                continue;
            }
            break;
        }
        List<T> snapshot = new LinkedList<>();
        for(Node<T> node : nodeSnapshot) {
            snapshot.add(node.value);
        }
        snapshot.sort((o1, o2) -> o1.compareTo(o2));
        return snapshot;
    }

    @Override
    public Iterator<T> iterator() {
        return getSnapshot().iterator();
    }

    private boolean tryRemoveGarbage(AtomicMarkableReference<Node<T>> currentPtr) {
        if(currentPtr == null)
            return false;
        Node<T> current = currentPtr.getReference();
        if(current == null || !current.next.isMarked())
            return false;
        Node<T> next = current.next.getReference();
        return currentPtr.compareAndSet(current, next, false, false);
    }
}
