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
	    .map(line -> (line.charAt(0) == 'R' ? 1 : -1) * Integer.parseInt(line.substring(1)))
	    .reduce(
		new Integer[] { 50, 0 },
		(result, n) -> {
		  result[0] = (result[0] + n) % 100;
		  if (result[0] == 0) {
		    result[1]++;
		  }
		  return result;
		},
		(u, v) -> v);
    System.out.println(output[1]);
  }
}
