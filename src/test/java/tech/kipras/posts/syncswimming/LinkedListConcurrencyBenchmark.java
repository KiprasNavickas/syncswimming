package tech.kipras.posts.syncswimming;

import org.openjdk.jmh.annotations.*;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Fork(value = 3, warmups = 2)
@BenchmarkMode(Mode.SingleShotTime)
public class LinkedListConcurrencyBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {

        @Param({ "10000", "100000", "1000000" })
        protected int count;

        protected LinkedList<Integer> list;

        @Setup
        public void setup() {
            list = new LinkedList<>();
        }
    }

    @State(Scope.Benchmark)
    public static class SyncBenchmarkState extends BenchmarkState {

        private List<Callable<Object>> tasks;

        @Setup
        public void setup() {
            super.setup();
            tasks = IntStream.range(0, count)
                .mapToObj(number -> (Callable<Object>) () -> {
                    list.add(number);
                    return null;
                })
                .collect(Collectors.toList());
        }

    }

    @Benchmark
    public int synchronizedProcessing(SyncBenchmarkState state) throws InterruptedException {
        var threadPool = Executors.newFixedThreadPool(8);
        threadPool.invokeAll(state.tasks);
        threadPool.shutdownNow();

        return state.list.size();
    }

    @Benchmark
    public int parallelProcessing(BenchmarkState state) {
        LinkedList<Integer> list = IntStream.range(0, state.count)
            .parallel()
            .collect(
                LinkedList::new,
                LinkedList::add,
                LinkedList::addAll
            );

        return list.size();
    }

    @Benchmark
    public int nonConcurrentProcessing(BenchmarkState state) {
        LinkedList<Integer> list = IntStream.range(0, state.count)
            .collect(
                LinkedList::new,
                LinkedList::add,
                LinkedList::addAll
            );

        return list.size();
    }

}
