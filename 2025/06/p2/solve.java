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
    final var lastLine = input.stream()
        .skip(input.size() - 1)
        .findFirst()
        .get();
    final var valueLengths = lastLine.chars()
        .mapToObj(c -> Character.valueOf((char) c))
        .reduce(
          new ArrayList<Integer[]>(),
          (result, c) -> {
            final int last = Math.max(0, result.size() - 1);
            if (c != ' ') {
              if (!result.isEmpty()) {
                result.add(new Integer[] { result.get(last)[1] + 1, result.get(last)[1] + 1 });
                result.get(last)[1]--;
              } else {
                result.add(new Integer[] { 0, 0 });
              }
            } else {
              result.get(last)[1]++;
            }
            System.out.println("result: " + result.stream().map(Arrays::asList).toList() + " c: '" + c + "'");
            return result;
          },
          (u, v) -> v
        );
    final var originalValues = input.stream()
        .limit(input.size() - 1)
        .toList();
    final var values = valueLengths
        .stream()
        .map(range -> IntStream.range(range[0], range[1] + 1)
            .mapToObj(x -> IntStream.range(0, originalValues.size())
                .mapToObj(y -> "" + originalValues.get(y).charAt(x))
                .collect(Collectors.joining())
                .trim())
            .map(n -> {
              System.out.println("range: " + Arrays.asList(range) + " n: '" + n + "'");
              return n;
            })
            .map(BigInteger::new)
            .toList())
        .map(n -> {
          System.out.println("n: " + n);
          return n;
        })
        .toList();
    final var ops = input.stream()
        .skip(input.size() - 1)
        .map(String::trim)
        .map(line -> line.split(" +"))
        .findFirst()
        .get();
    final int length = valueLengths.size();
    BigInteger sum = BigInteger.ZERO;
    for (int x = 0; x < length; x++) {
      BigInteger result;
      if (ops[x].equals("+")) {
        result = BigInteger.ZERO;
      } else {
        result = BigInteger.ONE;
      }
      System.out.println(result);
      for (int y = 0; y < values.get(x).size(); y++) {
        System.out.print(result + " " + ops[x] + " " + values.get(x).get(y) + " = ");
        if (ops[x].equals("+")) {
          result = result.add(values.get(x).get(y));
        } else {
          result = result.multiply(values.get(x).get(y));
        }
        System.out.println(result);
      }
      sum = sum.add(result);
    }
    System.out.println(sum);
  }
}
