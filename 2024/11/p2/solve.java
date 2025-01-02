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
    final var input = Files.lines(Paths.get("input"))
      .toList();
    final var initStones = Arrays.asList(input.getFirst().split(" ")).stream().map(Long::parseLong).toList();
    List<List<Long>> stones = new ArrayList<>();
    List<Integer> blinks = new ArrayList<>();
    stones.add(initStones);
    blinks.add(Optional.of(args).filter(a -> a.length > 0).map(a -> Integer.parseInt(a[0])).orElse(25));
    final int maxThreads = 1 << 0;
    final int stoneBatchSize = 1 << 16;
    BigInteger[] count = new BigInteger[] { BigInteger.ZERO };
    int iterations = 0;
    while (!stones.isEmpty()) {
      stoneCache.entrySet()
	.forEach(e -> {
	  while (e.getValue().size() < cacheMaxBlinks
	      && e.getValue().getLast().stream().allMatch(stoneCache::containsKey)) {
	    //System.out.println("expanding: " + e.getValue().getLast());
	    //System.out.println("... to: " + e.getValue().getLast().stream().map(stoneCache::get).flatMap(List::stream).flatMap(List::stream).toList());
	    e.getValue().add(e.getValue().getLast().stream().map(stoneCache::get).map(List::getFirst).flatMap(List::stream).toList());
	  }
	});
      final int threads = Math.min(maxThreads, stones.size());
      //System.out.println("stones: " + IntStream.range(0, threads).mapToObj(thread -> stones.get(thread).size() + " (" + blinks.get(thread) + ")").collect(Collectors.joining(" ")) + " levels " + stones.size() + " " + count[0] + " pending blinks range: " + blinks.stream().mapToInt(b -> b).min().orElse(0) + "-" + blinks.stream().mapToInt(b -> b).max().orElse(0) + " remaining: " + stones.stream().mapToLong(List::size).sum() + " threads: " + threads);
      final var newStones = IntStream.range(0, threads).mapToObj(thread -> new ArrayList<Long>(stones.get(thread).size() << 1)).toList();
      final var newBlinks = IntStream.range(0, threads).mapToObj(thread -> new ArrayList<Integer>(1)).toList();
      IntStream.range(0, threads).mapToObj(thread -> CompletableFuture.runAsync(() -> {
        blink(stones, newStones.get(thread), blinks.get(thread), newBlinks.get(thread), thread, thread, 0, Math.min(stoneBatchSize - 1, stones.get(thread).size() - 1));
      }))
          .toList()
          .forEach(CompletableFuture::join);
      IntStream.range(0, threads).forEach(thread -> {
      thread = threads - thread - 1;
      //System.out.println("thread " + thread + " new stones: " + newStones.get(thread).stream().map(Object::toString).collect(Collectors.joining(" ")) + " (" + newStones.get(thread).size() + "-" + newBlinks.get(thread).getFirst() + ")"); 
      if (stones.get(thread).size() > stoneBatchSize) {
	stones.add(Math.min(stones.size(), threads), stones.get(thread).subList(stoneBatchSize, stones.get(thread).size()));
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
      if (!stones.isEmpty()) {
        System.out.println("[" + iterations + "] " + newStones.get(0).size() + " (" + newBlinks.get(0).getFirst() + ") levels " + stones.size() + " " + count[0] + " " + blinks.stream().mapToInt(b -> b).min().orElse(0) + "-" + blinks.stream().mapToInt(b -> b).max().orElse(0) + " " + stones.stream().mapToLong(List::size).sum() + " " + stoneCache.size());
      }
      iterations++;
    }
    System.out.println("iterations: " + iterations);
    System.out.println(count[0]);
  }

  static BigInteger N2024 = new BigInteger("2024");

  static void blink(
      List<List<Long>> stones,
      List<Long> finalNewStones,
      int blinks,
      List<Integer> newBlinks,
      int firstLevelStart,
      int lastLevelEnd,
      int firstIndexStart,
      int lastIndexEnd) {
    blink:
    for (int blink = Math.min(Math.max(1, blinks), cacheMaxBlinks); blink >= 1; blink--) {
    //System.out.println("try " + blink + " blinks");
    var newStones = new ArrayList<Long>(stones.get(firstLevelStart).size() << 1);
    for (int level = firstLevelStart; level <= lastLevelEnd; level++) {
    final int startIndex;
    final int endIndex;
    if (level == firstLevelStart) {
      startIndex = firstIndexStart;
    } else {
      startIndex = 0;
    }
    if (level == lastLevelEnd) {
      endIndex = lastIndexEnd;
    } else {
      endIndex = stones.get(level).size() - 1;
    }
    for (int index = startIndex; index <= endIndex; index++) {
      var stone = stones.get(level).get(index);
      var cachedStones = stoneCache.get(stone);
      if (cachedStones != null && blink <= cachedStones.size()) {
	//if (blink > 1) System.out.println("stone " + stone + " found in cache: " + cachedStones.get(blink - 1));
        newStones.addAll(cachedStones.get(blink - 1));
	continue;
      }
      if (blink != 1) {
	//System.out.println("stone " + stone + " not found in cache");
	continue blink;
      }
      String stoneText = stone.toString();
      if (stoneText.length() % 2 == 0) {
	newStones.add(Long.parseLong(stoneText.substring(0, stoneText.length() / 2)));
	String right = stoneText.substring(stoneText.length() / 2);
	//int leftZeros = (int) right.chars().takeWhile(c -> c == '0').count();
	//if (leftZeros < right.length()) {
	//  newStones.add(right.substring(leftZeros));
	//} else {
	//  newStones.add("0");
	//}
	newStones.add(Long.parseLong(right));
	stoneCache.put(stone, new ArrayList<>(List.of(List.copyOf(newStones.subList(newStones.size() - 2, newStones.size())))));
	continue;
      }
      newStones.add(stone * 2024L);
      stoneCache.put(stone, new ArrayList<>(List.of(List.copyOf(newStones.subList(newStones.size() - 1, newStones.size())))));
    }
  }
  finalNewStones.addAll(newStones);
  newBlinks.add(blinks - blink);
  return;
  }
  }
}
