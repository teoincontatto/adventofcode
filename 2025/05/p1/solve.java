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
    final var ids = input.stream()
        .filter(line -> line.matches("^[^-]+$"))
        .map(line -> new BigInteger(line))
        .toList();
    var output = ids.stream()
      .reduce(
        0,
        (result, id) -> {
          if (freshRanges.stream()
              .anyMatch(range -> {
                boolean match = range[0].compareTo(id) <= 0 && range[1].compareTo(id) >= 0;
                System.out.println(range[0] + " <= " + id + " && " + range[1] + " >= " + id + " = " + match);
                return match;
              })) {
            return result + 1;
          }
          return result;
        },
        (u, v) -> v);
    System.out.println(output);
  }

}
