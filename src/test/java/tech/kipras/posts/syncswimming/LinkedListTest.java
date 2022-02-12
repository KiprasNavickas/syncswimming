package tech.kipras.posts.syncswimming;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LinkedListTest {

    @Test
    void emptyInitially() {
        var list = new LinkedList<Integer>();
        assertEquals(0, list.size());
    }

    @Test
    void addsAndGets() {
        var list = new LinkedList<String>();
        list.add("Hello");

        assertEquals(1, list.size());
        assertEquals("Hello", list.get(0));

        list.add("World!");

        assertEquals(2, list.size());
        assertEquals("World!", list.get(1));
    }

    @Test
    void stringifies() {
        var list = new LinkedList<String>();

        assertEquals("[]", list.toString());

        list.add("A");
        assertEquals("[A]", list.toString());

        list.add("B");
        assertEquals("[A, B]", list.toString());

        list.add("C");
        assertEquals("[A, B, C]", list.toString());

        list.add("D");
        assertEquals("[A, B, C, D]", list.toString());
    }

    @Test
    void addsAll() {
        var list1 = new LinkedList<String>();
        list1.add("A");
        list1.add("B");
        list1.add("C");

        assertEquals(3, list1.size());
        assertEquals("A", list1.get(0));
        assertEquals("B", list1.get(1));
        assertEquals("C", list1.get(2));

        var list2 = new LinkedList<String>();
        list2.add("D");
        list2.add("E");
        list2.add("F");

        assertEquals(3, list2.size());
        assertEquals("D", list2.get(0));
        assertEquals("E", list2.get(1));
        assertEquals("F", list2.get(2));

        list1.addAll(list2);

        assertEquals(6, list1.size());
        assertEquals("A", list1.get(0));
        assertEquals("B", list1.get(1));
        assertEquals("C", list1.get(2));
        assertEquals("D", list1.get(3));
        assertEquals("E", list1.get(4));
        assertEquals("F", list1.get(5));

        assertEquals(3, list2.size());
        assertEquals("D", list2.get(0));
        assertEquals("E", list2.get(1));
        assertEquals("F", list2.get(2));
    }

    @Test
    void noDataRaces() throws InterruptedException {
        var list = new LinkedList<String>();
        var tasks = List.of(
            call(() -> list.add("A")),
            call(() -> list.add("B")),
            call(() -> list.add("C")),
            call(() -> list.add("D")),
            call(() -> list.add("E")),
            call(() -> list.add("F")),
            call(() -> list.add("G")),
            call(() -> list.add("H"))
        );

        var threadPool = Executors.newFixedThreadPool(8);
        threadPool.invokeAll(tasks);
        threadPool.shutdownNow();

        assertEquals(8, list.size());

        assertTrue(list.contains("A"));
        assertTrue(list.contains("B"));
        assertTrue(list.contains("C"));
        assertTrue(list.contains("D"));
        assertTrue(list.contains("E"));
        assertTrue(list.contains("F"));
        assertTrue(list.contains("G"));
        assertTrue(list.contains("H"));
    }

    private Callable<Object> call(Runnable runnable) {
        return () -> {
            runnable.run();
            return null;
        };
    }
}

