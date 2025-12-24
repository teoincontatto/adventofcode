import java.nio.file.*;
import java.math.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;
import java.util.function.*;

public class solve {

  public static void main(String[] args) throws Exception {
    final var input = Files.lines(Paths.get("input"))
      .map(String::trim)
      .map(line -> line.split(" +"))
      .toList();
    final var values = input.stream().limit(input.size() - 1).map(value -> Arrays.asList(value).stream().map(BigInteger::new).toList()).toList();
    final var ops = input.stream().skip(input.size() - 1).findFirst().get();
    final int height = values.size();
    final int length = input.getFirst().length;
    BigInteger sum = BigInteger.ZERO;
    for (int x = 0; x < length; x++) {
      BigInteger result;
      if (ops[x].equals("+")) {
        result = BigInteger.ZERO;
      } else {
        result = BigInteger.ONE;
      }
      System.out.println(result);
      for (int y = 0; y < height; y++) {
        System.out.print(result + " " + ops[x] + " " + values.get(y).get(x) + " = ");
        if (ops[x].equals("+")) {
          result = result.add(values.get(y).get(x));
        } else {
          result = result.multiply(values.get(y).get(x));
        }
        System.out.println(result);
      }
      final int xx = x;
      System.out.println(values.stream().map(value -> "" + value.get(xx)).collect(Collectors.joining(" " + ops[x] + " ")) + " = " + result);
      sum = sum.add(result);
    }
    System.out.println(sum);
  }
}
