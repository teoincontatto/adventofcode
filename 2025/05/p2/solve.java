import java.nio.file.*;
import java.math.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;
import java.util.function.*;

public class solve {

  public static void main(String[] args) throws Exception {
    final var input = Files.lines(Paths.get("input"))
      .toList();
    final var freshRanges = input.stream()
        .filter(line -> line.contains("-"))
        .map(line -> line.split("-"))
        .map(range -> new BigInteger[] { new BigInteger(range[0]), new BigInteger(range[1]) })
        .toList();
    final var reducedFreshRanges = freshRanges.stream()
        .reduce(
          new ArrayList<BigInteger[]>(),
          (result, range) -> {
            boolean nested = false;
            ArrayList<BigInteger[]> overlapping = new ArrayList<>();
            for (var foundRange : result) {
              if (inRange(range[0], foundRange) && inRange(range[1], foundRange)) {
                System.out.println("Using " + Arrays.asList(foundRange) + " for " + Arrays.asList(range));
                nested = true;
                break;
              } else if (inRange(range[0], foundRange)) {
                overlapping.add(foundRange);
              } else if (inRange(range[1], foundRange)) {
                overlapping.add(foundRange);
              }
            }
            if (!nested) {
              if (overlapping.isEmpty()) {
                System.out.println("New " + Arrays.asList(range));
                result.add(range);
              } else {
                if (overlapping.size() > 2) {
                  System.err.println("Found more than 2 overlapping for: " + Arrays.asList(range));
                  overlapping.stream().map(Arrays::asList).forEach(System.err::println);
                  System.exit(1);
                }
                if (overlapping.size() == 1) {
                  if (inRange(range[0], overlapping.get(0))) {
                    overlapping.get(0)[1] = range[1];
                  } else {
                    overlapping.get(0)[0] = range[0];
                  }
                  System.out.println("Change " + Arrays.asList(overlapping.get(0)));
                } else {
                  if (overlapping.get(0)[0].compareTo(overlapping.get(1)[0]) < 0) {
                    overlapping.get(0)[1] = overlapping.get(1)[1];
                  } else {
                    overlapping.get(0)[0] = overlapping.get(1)[0];
                  }
                  result.remove(overlapping.get(1));
                  System.out.println("Change " + Arrays.asList(overlapping.get(0)));
                  System.out.println("Removed " + Arrays.asList(overlapping.get(1)));
                }
              }
            }
            return result;
          },
          (u, v) -> v);
    reducedFreshRanges.stream().map(Arrays::asList).forEach(System.out::println);
    for (var freshRange : freshRanges) {
      var foundReducedFreshRanges = reducedFreshRanges.stream()
          .filter(reducedFreshRange -> inRange(freshRange[0], reducedFreshRange)
            && inRange(freshRange[1], reducedFreshRange))
          .toList();
      if (foundReducedFreshRanges.size() == 0) {
        System.err.println("Can not find original range: " + Arrays.asList(freshRange));
        System.exit(1);
      }
      if (foundReducedFreshRanges.size() > 1) {
        System.err.println("Found multiple matches for original range: " + Arrays.asList(freshRange));
        foundReducedFreshRanges.stream().map(Arrays::asList).forEach(System.err::println);
        System.exit(1);
      }
    }
    var output = reducedFreshRanges.stream()
      .reduce(
        BigInteger.ZERO,
        (result, reducedFreshRange) -> result.add(reducedFreshRange[1].subtract(reducedFreshRange[0]).add(BigInteger.ONE)),
        (u, v) -> v);
    System.out.println(output);
  }

  private static boolean inRange(BigInteger value, BigInteger[] range) {
    return range[0].compareTo(value) <= 0 && range[1].compareTo(value) >= 0;
  }
}
