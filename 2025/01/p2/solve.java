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
		new int[] { 50, 0 },
		(result, n) -> {
      int previous = result[0]; 
      int current = previous + n;
      int clicks = result[1];
      if (n >= 0) {
        clicks += current / 100;
        current = current % 100;
      } else {
        if (current == 0) {
          clicks += 1;
        } else if (current < 0) {
          if (previous == 0) {
            clicks += - current / 100;
          } else {
            clicks += - current / 100 + 1;
          }
          current = (((- current / 100) + 1) * 100 + current) % 100;
        }
      }
      // System.out.println(
        // "result: [" + result[0] + ", " + result[1] + "]"
        //   + " rotation: " + n
        //   + " previous: " + previous
        //   + " current: " + current
        //   + " clicks: " + clicks);
      result[0] = current;
      result[1] = clicks;
		  return result;
		},
		(u, v) -> v);
    System.out.println(output[1]);
  }

}
