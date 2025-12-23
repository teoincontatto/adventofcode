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
      .reduce(
        0,
        (result, bank) -> {
          var charsWithIndex = bank.chars()
              .mapToObj(c -> c)
              .reduce(
                new ArrayList<Integer[]>(bank.length()),
                (r, c) -> {
                  r.add(new Integer[] { c, r.size() });
                  return r;
                },
                (u, v) -> v);
          var firstMaxJolts = charsWithIndex
              .stream()
              .limit(bank.length() - 1)
              .sorted(Comparator.comparing(solve::extractChar).reversed())
              .findFirst()
              .get();
          String maxJolts = List.of(
              firstMaxJolts,
              charsWithIndex
              .stream()
              .skip(firstMaxJolts[1] + 1)
              .sorted(Comparator.comparing(solve::extractChar).reversed())
              .findFirst()
              .get())
              .stream()
              .map(c -> String.valueOf(Character.valueOf((char) c[0].intValue())))
              .collect(Collectors.joining());
          System.out.println("bank: " + bank + " charsWithIndex: " + charsWithIndex.stream().map(Arrays::asList).toList() + " maxJolts: " + maxJolts);
          return result + Integer.parseInt(maxJolts);
        },
        (u, v) -> v);
    System.out.println(output);
  }

  private static Integer extractChar(Integer[] charWithIndex) {
    return charWithIndex[0];
  }
}
