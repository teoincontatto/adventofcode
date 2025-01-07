import java.nio.file.*;
import java.math.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;
import java.util.function.*;

public class resolve {

  static final Map<Long, List<List<Long>>> stoneCache = Collections.synchronizedMap(new HashMap<>(Map.of(0L, new ArrayList<>(List.of(List.of(1L))))));
  static final int cacheMaxBlinks = 25;

  public static void main(String[] args) throws Exception {
    //loadCache();
    final var input = Files.lines(Paths.get("input"))
      .toList();
    final var initStones = Arrays.asList(input.getFirst().split(" ")).stream().map(Long::parseLong).toList();
    List<List<Long>> stones = new ArrayList<>();
    List<Integer> blinks = new ArrayList<>();
    stones.add(initStones);
    blinks.add(Optional.of(args).filter(a -> a.length > 0).map(a -> Integer.parseInt(a[0])).orElse(25));
    final int maxThreads = 1 << 0;
    final int stoneBatchSize = 1 << 0;
    BigInteger[] count = new BigInteger[] { BigInteger.ZERO };
    int iterations = 0;
    long start = System.currentTimeMillis();
    while (!stones.isEmpty()) {
      for (var e : stoneCache.entrySet()) {
	  while (e.getValue().size() < cacheMaxBlinks
	      && e.getValue().getLast().stream().allMatch(stoneCache::containsKey)) {
	    //System.out.println("expanding: " + e.getValue().getLast());
	    //System.out.println("... to: " + e.getValue().getLast().stream().map(stoneCache::get).flatMap(List::stream).flatMap(List::stream).toList());
	    e.getValue().add(e.getValue().getLast().stream().map(stoneCache::get).map(List::getFirst).flatMap(List::stream).toList());
	  }
	};
      final int threads = Math.min(maxThreads, stones.size());
      //System.out.println("stones: " + IntStream.range(0, threads).mapToObj(thread -> stones.get(thread).size() + " (" + blinks.get(thread) + ")").collect(Collectors.joining(" ")) + " levels " + stones.size() + " " + count[0] + " pending blinks range: " + blinks.stream().mapToInt(b -> b).min().orElse(0) + "-" + blinks.stream().mapToInt(b -> b).max().orElse(0) + " remaining: " + stones.stream().mapToLong(List::size).sum() + " threads: " + threads);
      final var newStones = new ArrayList<List<Long>>(IntStream.range(0, threads).mapToObj(thread -> new ArrayList<Long>(stones.get(thread).size() << 1)).toList());
      final var newBlinks = IntStream.range(0, threads).mapToObj(thread -> new ArrayList<Integer>(1)).toList();
      final var realBatchSize = IntStream.range(0, threads).mapToObj(thread -> new ArrayList<Integer>(1)).toList();
      if (threads == 1) {
        blink(stones.get(0), newStones, 0, realBatchSize.get(0), blinks.get(0), newBlinks.get(0), Math.min(stoneBatchSize - 1, stones.get(0).size() - 1));
      } else {
      IntStream.range(0, threads).mapToObj(thread -> CompletableFuture.runAsync(() -> {
        blink(stones.get(thread), newStones, thread, realBatchSize.get(thread), blinks.get(thread), newBlinks.get(thread), Math.min(stoneBatchSize - 1, stones.get(thread).size() - 1));
      }))
          .toList()
          .forEach(CompletableFuture::join);
      }
      if (!stones.isEmpty() && (System.currentTimeMillis() - start) > 1000) {
        start = System.currentTimeMillis();
        System.out.println("[" + iterations + "] " + stones.get(0).size() + " -> " + newStones.get(0).size() + " (" + blinks.getFirst() + " -> " + newBlinks.get(0).getFirst() + ") levels " + stones.size() + " " + count[0] + " " + blinks.stream().mapToInt(b -> b).min().orElse(0) + "-" + blinks.stream().mapToInt(b -> b).max().orElse(0) + " " + stones.stream().mapToLong(List::size).sum() + " " + stoneCache.size());
      }
      for (int thread = threads - 1; thread >= 0; thread--) {
      if (stones.get(thread).size() > realBatchSize.get(thread).getLast()) {
	stones.add(Math.min(stones.size(), threads), stones.get(thread).subList(realBatchSize.get(thread).getLast(), stones.get(thread).size()));
	blinks.add(Math.min(blinks.size(), threads), blinks.get(thread));
      }
      if (newBlinks.get(thread).getFirst() == 0) {
        count[0] = count[0].add(BigInteger.valueOf((long) newStones.get(thread).size()));
        stones.remove(thread);
	blinks.remove(thread);
      } else {
        stones.set(thread, newStones.get(thread));
        blinks.set(thread, newBlinks.get(thread).getFirst());
      }
      };
      stones.stream().map(s -> s.stream().map(Object::toString).collect(Collectors.joining(" "))).forEach(System.out::println);
      iterations++;
    }
    System.out.println("iterations: " + iterations);
    System.out.println(count[0]);
    //storeCache();
  }

  static void blink(
      List<Long> stones,
      List<List<Long>> newStones,
      int pos,
      List<Integer> realBatchSize,
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
	//if (blink > 1) System.out.println("stone " + stone + " found in cache: " + cachedStones.get(blink - 1));
	if (blink > 6 && newStones.get(pos).isEmpty()) {
          newStones.set(pos, cachedStones.get(blink - 1));
          index++;
          break;
	}
        newStones.get(pos).addAll(cachedStones.get(blink- 1));
	continue;
      }
      if (blink != 1) {
	//System.out.println("stone " + stone + " not found in cache");
	if (!newStones.get(pos).isEmpty()) {
	  index++;
	  break;
	}
	continue blink;
      }
      String stoneText = stone.toString();
      if (stoneText.length() % 2 == 0) {
	newStones.get(pos).add(Long.parseLong(stoneText.substring(0, stoneText.length() / 2)));
	String right = stoneText.substring(stoneText.length() / 2);
	newStones.get(pos).add(Long.parseLong(right));
	stoneCache.put(stone, new ArrayList<>(List.of(List.copyOf(newStones.get(pos).subList(newStones.get(pos).size() - 2, newStones.get(pos).size())))));
	continue;
      }
      newStones.get(pos).add(stone * 2024L);
      stoneCache.put(stone, new ArrayList<>(List.of(List.copyOf(newStones.get(pos).subList(newStones.get(pos).size() - 1, newStones.get(pos).size())))));
  }
  realBatchSize.add(index);
  newBlinks.add(blinks - blink);
  return;
  }
  }
  
  static void storeCache() throws Exception {
    try (var writer = Files.newBufferedWriter(Paths.get("cache.tmp"))) {
      stoneCache.entrySet().stream().map(e -> e.getKey() + " " + e.getValue().stream().map(l -> l.stream().map(Object::toString).collect(Collectors.joining(","))).collect(Collectors.joining(" "))).forEach(v -> { try { writer.append(v); } catch (Exception ex) { throw new RuntimeException(ex); } });
    }
  }

  static void loadCache() throws Exception {
    try (var reader = Files.newBufferedReader(Paths.get("cache.tmp"))) {
      reader.lines().map(l -> Arrays.asList(l.split(" ")).stream().map(ll -> Arrays.asList(ll.split(",")).stream().map(Long::parseLong).toList()).toList()).forEach(l -> stoneCache.put(l.getFirst().getFirst(), new ArrayList<>(l.stream().skip(1).toList())));
    }
  }

}
