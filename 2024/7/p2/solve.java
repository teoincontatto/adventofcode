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
      final BigInteger iterations = BigInteger.valueOf(3).pow(size - 2);
      System.out.println("Iterations " + iterations);
      for (BigInteger iteration = BigInteger.ZERO; iteration.compareTo(iterations) < 0; iteration = iteration.add(BigInteger.ONE)) {
        final StringBuilder builder = new StringBuilder();
        builder.append(result).append(" = ");
        builder.append(equation.get(1));
        List<Value> stack = new ArrayList<>();
        stack.add(new ScalarValue(equation.get(1)));
        for (int index = 2; index < size; index++) {
          if (iteration.divide(BigInteger.valueOf(3).pow(index - 2)).mod(BigInteger.valueOf(3)).equals(BigInteger.ZERO)) {
            builder.append(" + ");
            stack.add(new PartialSum(equation.get(index)));
          } else if (iteration.divide(BigInteger.valueOf(3).pow(index - 2)).mod(BigInteger.valueOf(3)).equals(BigInteger.ONE)) {
            builder.append(" * ");
            stack.add(new PartialMul(equation.get(index)));
          } else {
            builder.append(" || ");
            stack.add(new PartialConcat(equation.get(index)));
          }
          builder.append(equation.get(index));
        }
        // System.out.println(iteration);
        BigInteger calculated = stack.get(0).value;
        // System.out.print(calculated);
        stack.remove(0);
        while (!stack.isEmpty()) {
          var value = stack.get(0);
          stack.remove(0);
          if (value instanceof PartialSum) {
            calculated = calculated.add(value.value);
            // System.out.println(" + " + value.value + " = " + calculated);
            // System.out.print(calculated);
          } else if (value instanceof PartialMul) {
            calculated = calculated.multiply(value.value);
            // System.out.println(" * " + value.value + " = " + calculated);
            // System.out.print(calculated);
          } else {
            calculated = new BigInteger(calculated.toString() + value.value.toString());
            // System.out.println(" || " + value.value + " = " + calculated);
            // System.out.print(calculated);
          }
        }
        // System.out.println();
        builder.append(" == ").append(calculated).append("? ").append(calculated.equals(result));
        // System.out.println(builder.toString());
        if (calculated.equals(result)) {
          System.out.println(builder.toString());
          resultSum = resultSum.add(result);
          break;
        }
        // System.out.println(builder.toString());
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

  static class PartialConcat extends Value {
    PartialConcat(BigInteger value) {
      super(value);
    }

    Value result(BigInteger value) {
      return new PartialConcat(value);
    }
  }
}
