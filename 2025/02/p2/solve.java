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
    var output = input.stream()
      .flatMap(line -> Arrays.asList(line.split(",")).stream())
      .map(range -> range.split("-"))
      .map(range -> {
        if (!String.valueOf(Long.parseLong(range[0])).equals(range[0])
          || !String.valueOf(Long.parseLong(range[1])).equals(range[1])) {
          System.err.println("Malformed range: " + range);
          System.exit(1);
        }
        return range;
      })
      .map(range -> new long[] { Long.parseLong(range[0]), Long.parseLong(range[1]) })
      .flatMap(range -> LongStream.rangeClosed(range[0], range[1]).mapToObj(i -> i))
      .reduce(
        BigInteger.ZERO,
        (result, id) -> {
          String idString = String.valueOf(id);
          if (IntStream.range(1, idString.length() / 2 + 1)
              .filter(i -> idString.length() % i == 0)
              .mapToObj(i -> idString.substring(0, i))
              .anyMatch(subIdString -> IntStream.range(0, idString.length() / subIdString.length())
                .mapToObj(i -> subIdString)
                .collect(Collectors.joining())
                .equals(idString))) {
            System.out.println("Invalid: " + id + " Total: " + result.add(BigInteger.valueOf(id)));
            return result.add(BigInteger.valueOf(id));
          }
          return result;
        },
        (u, v) -> v);
    System.out.println(output);
  }

}
