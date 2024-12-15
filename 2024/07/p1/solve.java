import java.nio.file.*;
import java.math.*;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

public class solve {

  public static void main(String[] args) throws Exception {
    var input = Files.lines(Paths.get("input"))
      .toList();
    var equations = input.stream()
        .map(line -> Arrays.asList(line.split(":? "))
            .stream()
            .map(BigInteger::new)
            .toList())
        .toList();
    BigInteger resultSum = BigInteger.ZERO;
    for (var equation : equations) {
      final int size = equation.size();
      final BigInteger result = equation.get(0);
      final long iterations = 1l << (size - 2);
      System.out.println("Iterations " + iterations);
      for (long iteration = 0; iteration < iterations; iteration++) {
        final StringBuilder builder = new StringBuilder();
        builder.append(result).append(" = ");
        builder.append(equation.get(1));
        List<Value> stack = new ArrayList<>();
        stack.add(new ScalarValue(equation.get(1)));
        for (int index = 2; index < size; index++) {
          if ((iteration >> (index - 2)) % 2 == 0) {
            builder.append(" + ");
            stack.add(new PartialSum(equation.get(index)));
          } else {
            builder.append(" * ");
            stack.add(new PartialMul(equation.get(index)));
          }
          builder.append(equation.get(index));
        }
        BigInteger calculated = stack.get(0).value;
        System.out.print(calculated);
        stack.remove(0);
        while (!stack.isEmpty()) {
          var value = stack.get(0);
          stack.remove(0);
          if (value instanceof PartialSum) {
            calculated = calculated.add(value.value);
            System.out.println(" + " + value.value + " = " + calculated);
            System.out.print(calculated);
          } else {
            calculated = calculated.multiply(value.value);
            System.out.println(" * " + value.value + " = " + calculated);
            System.out.print(calculated);
          }
        }
        System.out.println();
        builder.append(" == ").append(calculated).append("? ").append(calculated == result);
        System.out.println(builder.toString());
        if (calculated.equals(result)) {
          resultSum = resultSum.add(result);
          break;
        }
      }
    }
    System.out.println(resultSum);
  }

  static abstract class Value {
    final BigInteger value;

    Value(BigInteger value) {
      this.value = value;
    }

    BigInteger value() {
      return value;
    }

    abstract Value result(BigInteger value);
  }

  static class ScalarValue extends Value {
    ScalarValue(BigInteger value) {
      super(value);
    }

    Value result(BigInteger value) {
      return new ScalarValue(value);
    }
  }

  static class PartialSum extends Value {
    PartialSum(BigInteger value) {
      super(value);
    }

    Value result(BigInteger value) {
      return new PartialSum(value);
    }
  }

  static class PartialMul extends Value {
    PartialMul(BigInteger value) {
      super(value);
    }

    Value result(BigInteger value) {
      return new PartialMul(value);
    }
  }
}
