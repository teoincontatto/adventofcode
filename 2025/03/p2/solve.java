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
        0L,
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
          String maxJolts = IntStream.range(0, 12)
              .mapToObj(digit -> digit)
              .reduce(
                new Result("", -1),
                (r, digit) -> {
                  System.out.println("result: " + r + " limit: " + (bank.length() - (11 - digit)) + " skip: " + (r.prevIndex + 1));
                  var charWithIndex = charsWithIndex
                      .stream()
                      .limit(bank.length() - (11 - digit))
                      .skip(r.prevIndex + 1)
                      .sorted(Comparator.comparing(solve::extractChar).reversed())
                      .findFirst()
                      .get();
                  return new Result(r.maxJolts + String.valueOf(Character.valueOf((char) charWithIndex[0].intValue())), charWithIndex[1]);
                },
                (u, v) -> v)
                .maxJolts;
          System.out.println("bank: " + bank + " charsWithIndex: " + charsWithIndex.stream().map(Arrays::asList).toList() + " maxJolts: " + maxJolts);
          return result + Long.parseLong(maxJolts);
        },
        (u, v) -> v);
    System.out.println(output);
  }

  private static Integer extractChar(Integer[] charWithIndex) {
    return charWithIndex[0];
  }

  private record Result(String maxJolts, int prevIndex) {}

}
