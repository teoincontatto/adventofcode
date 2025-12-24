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
        .sorted(Comparator.comparing(range -> range[0]))
        .toList();
    final var reducedFreshRanges = freshRanges.stream()
        .reduce(
          new ArrayList<BigInteger[]>(),
          (result, range) -> {
            System.out.println("Adding " + Arrays.asList(range));
            boolean found = false;
            for (var foundRange : result.stream().toList()) {
              if (inRange(range[0], foundRange) && inRange(range[1], foundRange)) {
                System.out.println("Using " + Arrays.asList(foundRange) + " skipping " + Arrays.asList(range));
                found = true;
                break;
              } else if (inRange(range[0], foundRange)) {
                foundRange[1] = range[1];
                System.out.println("Changed " + Arrays.asList(foundRange) + " to accomodate end of " + Arrays.asList(range));
                found = true;
                range = foundRange;
              } else if (inRange(range[1], foundRange)) {
                foundRange[0] = range[0];
                System.out.println("Changed " + Arrays.asList(foundRange) + " to accomodate begin of " + Arrays.asList(range));
                if (found) {
                  result.remove(range);
                  System.out.println("Removed " + Arrays.asList(range));
                }
                found = true;
                range = foundRange;
              } else if (found) {
                break;
              }
            }
            if (!found) {
              System.out.println("Added " + Arrays.asList(range));
              result.add(range);
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
