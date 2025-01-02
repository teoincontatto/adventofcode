import java.nio.file.*;
import java.math.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;
import java.util.function.*;

public class resolve {

  static final Map<Long, List<List<Long>>> stoneCache = Collections.synchronizedMap(new HashMap<>(Map.of(0L, new ArrayList<>(List.of(List.of(1L))))));
  static final int cacheMaxBlinks = 15;

  public static void main(String[] args) throws Exception {
    /* Total number of processors or cores available to the JVM */
    System.out.println("Available processors (cores): " + 
    Runtime.getRuntime().availableProcessors());

    /* Total amount of free memory available to the JVM */
    System.out.println("Free memory (bytes): " + 
    Runtime.getRuntime().freeMemory());

    /* This will return Long.MAX_VALUE if there is no preset limit */
    long maxMemory = Runtime.getRuntime().maxMemory();
    /* Maximum amount of memory the JVM will attempt to use */
    System.out.println("Maximum memory (bytes): " + 
    (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));

    /* Total memory currently in use by the JVM */
    System.out.println("Total memory (bytes): " + 
    Runtime.getRuntime().totalMemory());
    final var input = Files.lines(Paths.get("input"))
      .toList();
    final var initStones = Arrays.asList(input.getFirst().split(" ")).stream().map(Long::parseLong).toList();
    List<List<Long>> stones = new ArrayList<>();
    List<Integer> blinks = new ArrayList<>();
    stones.add(initStones);
    blinks.add(Optional.of(args).filter(a -> a.length > 0).map(a -> Integer.parseInt(a[0])).orElse(75));
    final int maxThreads = 1 << 4;
    final int stoneBatchSize = 1 << 18;
    BigInteger[] count = new BigInteger[] { BigInteger.ZERO };
    int iterations = 0;
    while (!stones.isEmpty()) {
      stoneCache.entrySet()
          .forEach(e -> {
            while (e.getValue().size() < cacheMaxBlinks
                && e.getValue().getLast().stream().allMatch(stoneCache::containsKey)) {
              //System.out.println("expanding: " + e.getValue().getLast());
              //System.out.println("... to: " + e.getValue().getLast().stream().map(stoneCache::get).flatMap(List::stream).flatMap(List::stream).toList());
              e.getValue().add(e.getValue().getLast().stream()
                  .map(stoneCache::get)
                  .map(List::getFirst)
                  .flatMap(List::stream).toList());
            }
          });
      final int threads = Math.min(maxThreads, stones.size());
      //System.out.println("stones: " + IntStream.range(0, threads).mapToObj(thread -> stones.get(thread).size() + " (" + blinks.get(thread) + ")").collect(Collectors.joining(" ")) + " levels " + stones.size() + " " + count[0] + " pending blinks range: " + blinks.stream().mapToInt(b -> b).min().orElse(0) + "-" + blinks.stream().mapToInt(b -> b).max().orElse(0) + " remaining: " + stones.stream().mapToLong(List::size).sum() + " threads: " + threads);
      final var newStones = IntStream.range(0, threads).mapToObj(thread -> new ArrayList<Long>(stones.get(thread).size() << 1)).toList();
      final var newBlinks = IntStream.range(0, threads).mapToObj(thread -> new ArrayList<Integer>(1)).toList();
      final var doneBatchSize = IntStream.range(0, threads).mapToObj(thread -> new ArrayList<Integer>(1)).toList();
      IntStream.range(0, threads)
          .mapToObj(thread -> CompletableFuture.runAsync(() -> {
            blink(
              stones.get(thread),
              doneBatchSize.get(thread),
              newStones.get(thread),
              blinks.get(thread),
              newBlinks.get(thread),
              Math.min(stoneBatchSize - 1, stones.get(thread).size() - 1));
          }))
          .toList()
          .forEach(CompletableFuture::join);
      if (!stones.isEmpty()) {
        System.out.println("[" + iterations + "] "
          + "stoneBatch: " + Math.min(stoneBatchSize, stones.get(0).size()) + " -> " + newStones.get(0).size()
          + " (" + blinks.get(0) + " -> " + newBlinks.get(0).getFirst() + ")"
          + " levels: " + stones.size()
          + " count: " + count[0]
          + " blinks: " + blinks.stream().mapToInt(b -> b).min().orElse(0) + "-" + blinks.stream().mapToInt(b -> b).max().orElse(0)
          + " pending: " + stones.stream().mapToLong(List::size).sum() + " "
          + " cache: " + stoneCache.values().stream().mapToLong(List::size).sum());
      }
      IntStream.range(0, threads)
          .forEach(thread -> {
            thread = threads - thread - 1;
            //System.out.println("thread " + thread + " new stones: " + newStones.get(thread).stream().map(Object::toString).collect(Collectors.joining(" ")) + " (" + newStones.get(thread).size() + "-" + newBlinks.get(thread).getFirst() + ")"); 
            if (stones.get(thread).size() > doneBatchSize.get(thread).getFirst()) {
              stones.add(Math.min(stones.size(), threads), stones.get(thread).subList(doneBatchSize.get(thread).getFirst(), stones.get(thread).size()));
              blinks.add(Math.min(blinks.size(), threads), blinks.get(thread));
            }
            //System.out.println("thread: " + thread + " blinks: " + blinks.get(thread) + " newBlinks: " + newBlinks.get(thread).getFirst());
            if (newBlinks.get(thread).getFirst() == 0) {
              count[0] = count[0].add(BigInteger.valueOf((long) newStones.get(thread).size()));
              stones.remove(thread);
              blinks.remove(thread);
            } else {
              stones.set(thread, newStones.get(thread));
              blinks.set(thread, newBlinks.get(thread).getFirst());
            }
          });
      iterations++;
    }
    System.out.println("iterations: " + iterations);
    System.out.println(count[0]);
  }

  static void blink(
      List<Long> stones,
      List<Integer> doneBatchSize,
      List<Long> newStones,
      int blinks,
      List<Integer> newBlinks,
      int endIndex) {
    blink:
    for (int blink = Math.min(Math.max(1, blinks), cacheMaxBlinks); blink >= 1; blink--) {
      //System.out.println("try " + blink + " blinks");
      int index = 0;
      for (; index <= endIndex; index++) {
        var stone = stones.get(index);
        var cachedStones = stoneCache.get(stone);
        if (cachedStones != null && blink <= cachedStones.size()) {
          //System.out.println("stone " + stone + " found in cache: " + cachedStones.get(blink - 1));
          newStones.addAll(cachedStones.get(blink - 1));
          continue;
        }
        if (blink != 1) {
          if (!newStones.isEmpty()) {
            index++;
            break;
          }
          //System.out.println("stone " + stone + " not found in cache");
          continue blink;
        }
        String stoneText = stone.toString();
        if (stoneText.length() % 2 == 0) {
          newStones.add(Long.parseLong(stoneText.substring(0, stoneText.length() / 2)));
          String right = stoneText.substring(stoneText.length() / 2);
          newStones.add(Long.parseLong(right));
          var cachedStone = new ArrayList<>();
          cachedStone.add(newStones.subList(newStones.size() - 2, newStones.size()));
          stoneCache.put(stone, cachedStone);
          continue;
        }
        newStones.add(stone * 2024L);
        var cachedStone = new ArrayList<>();
        cachedStone.add(newStones.subList(newStones.size() - 1, newStones.size()));
        stoneCache.put(stone, cachedStone);
      }
      doneBatchSize.add(index);
      newBlinks.add(blinks - blink);
      return;
    }
  }
}
