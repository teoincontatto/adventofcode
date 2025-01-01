import java.nio.file.*;
import java.math.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;
import java.util.function.*;

public class resolve {

  static Map<Long, BigInteger> cache = new HasMap<>();
  static Map<Long, List<Long>> stoneCache = new HasMap<>();

  public static void main(String[] args) throws Exception {
    final var input = Files.lines(Paths.get("input"))
      .toList();
    final var initStones = Arrays.asList(input.getFirst().split(" ")).stream().map(Long::parseLong).toList();
    List<List<Long>> stones = new ArrayList<>();
    List<Integer> blinks = new ArrayList<>();
    stones.add(initStones);
    blinks.add(Optional.of(args).filter(a -> a.length > 0).map(a -> Integer.parseInt(a[0])).orElse(25));
    final int maxThreads = 1 << 3;
    final int stoneBatchSize = 1 << 16;
    final int superblinks = 3;
    BigInteger[] count = new BigInteger[] { BigInteger.ZERO };
    int iterations = 0;
    for (int superblink = 0; superblink < superblinks; superblink++) {
    var nextStones = new ArrayList<List<Long>>();
    for (Long stone : stones.stream().flatMap(List::stream).toList()) {
    if (cache.containsKey(stone)) {
      count[0] = count[0].add(cache.get(stone));
      nextStones.add(stoneCache.get(stone));
      continue;
    }
    stones = List.of(List.of(stone));
    while (!stones.isEmpty()) {
      iterations++;
      final int threads = Math.min(maxThreads, stones.size());
      //System.out.println("stones: " + IntStream.range(0, threads).mapToObj(thread -> stones.get(thread).size() + " (" + blinks.get(thread) + ")").collect(Collectors.joining(" ")) + " levels " + stones.size() + " " + count[0] + " pending blinks range: " + blinks.stream().mapToInt(b -> b).min().orElse(0) + "-" + blinks.stream().mapToInt(b -> b).max().orElse(0) + " remaining: " + stones.stream().mapToLong(List::size).sum() + " threads: " + threads);
      final var newStones = IntStream.range(0, threads).mapToObj(thread -> new ArrayList<Long>(stones.get(thread).size() << 1)).toList();
      IntStream.range(0, threads).mapToObj(thread -> CompletableFuture.runAsync(() -> {
        if (stones.get(thread).size() > stoneBatchSize) {
          blink(stones, newStones.get(thread), thread, thread, 0, stoneBatchSize - 1);
        } else {
          blink(stones, newStones.get(thread), thread, thread, 0, stones.get(thread).size() - 1);
        }
      }))
          .toList()
          .forEach(CompletableFuture::join);
      IntStream.range(0, threads).forEach(thread -> {
      thread = threads - thread - 1;
      //System.out.println("thread " + thread + " new stones: " + newStones.get(thread).stream().collect(Collectors.joining(" ")) + " (" + newStones.get(thread).size() + ")"); 
      if (stones.get(thread).size() > stoneBatchSize) {
	stones.add(Math.min(stones.size(), threads), stones.get(thread).subList(stoneBatchSize, stones.get(thread).size()));
	blinks.add(Math.min(blinks.size(), threads), blinks.get(thread));
      }
      if (blinks.get(thread).intValue() == 1) {
        count[0] = count[0].add(BigInteger.valueOf((long) newStones.get(thread).size()));
	nextStones.add(newStones.get(thread));
        stones.remove(thread);
	blinks.remove(thread);
      } else {
        stones.set(thread, newStones.get(thread));
        blinks.set(thread, blinks.get(thread) - 1);
      }
      });
      if (!stones.isEmpty() && iterations % 50 == 0) {
      System.out.println("[" + iterations + "] " + stones.get(0).size() + " (" + blinks.get(0) + ") levels " + stones.size() + " " + count[0] + " " + blinks.stream().mapToInt(b -> b).min().orElse(0) + "-" + blinks.stream().mapToInt(b -> b).max().orElse(0) + " " + stones.stream().mapToLong(List::size).sum());
      }
    }
    stones = List.of(nextStones.stream().flatMap(List::stream).toList());
    }
    }
    }
    System.out.println("iterations: " + iterations);
    System.out.println(count[0]);
  }

  static BigInteger N2024 = new BigInteger("2024");

  static void blink(
      List<List<Long>> stones,
      List<Long> newStones,
      int firstLevelStart,
      int lastLevelEnd,
      int firstIndexStart,
      int lastIndexEnd) {
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
      if (stone.equals(0L)) {
	newStones.add(1L);
	continue;
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
	continue;
      }
      newStones.add(stone * 2024L);
    }
  }
}
}
