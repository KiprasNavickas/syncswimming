package tech.kipras.posts.syncswimming;

/**
 * Simple synchronized linked list implementation.
 *
 * Incomplete as you cannot insert at a specific index or remove elements,
 * but it gets the job done for what I need.
 */
public class LinkedList<T> {

    private final Object lock = new Object();

    private Node<T> first;
    private Node<T> last;
    private int size;

    public void add(T item) {
        synchronized (lock) {
            var newNode = new Node<>(item);

            if (first == null) {
                first = newNode;
                last = newNode;
                size++;
                return;
            }

            last.setNext(newNode);
            last = newNode;
            size++;
        }
    }

    public void addAll(LinkedList<T> other) {
        synchronized (lock) {
            last.setNext(other.first);
            last = other.last;
            size += other.size;
        }
    }

    public T get(int index) {
        synchronized (lock) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException();
            }

            var node = first;
            for (var i = 0; i < index; i++) {
                node = node.getNext();
            }

            return node.getItem();
        }
    }

    public int size() {
        synchronized (lock) {
            return size;
        }
    }

    public boolean contains(T item) {
        synchronized (lock) {
            for (var node = first; node != null; node = node.getNext()) {
                if (node.getItem().equals(item)) {
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public String toString() {
        synchronized (lock) {
            var buffer = new StringBuilder().append("[");

            if (first != null) {
                buffer.append(first.getItem().toString());

                for (var node = first.getNext(); node != null; node = node.getNext()) {
                    buffer.append(", ").append(node.getItem().toString());
                }
            }

            return buffer.append("]").toString();
        }
    }

    private static class Node<T> {
        private final T item;
        private Node<T> next;

        public Node(T item) {
            this.item = item;
        }

        public Node<T> getNext() {
            return next;
        }

        public void setNext(Node<T> next) {
            this.next = next;
        }

        public T getItem() {
            return item;
        }
    }
}
