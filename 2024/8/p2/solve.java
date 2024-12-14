import java.nio.file.*;
import java.math.*;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

public class solve {

  public static void main(String[] args) throws Exception {
    final var input = Files.lines(Paths.get("input"))
      .toList();
    final int height = input.size();
    final int length = input.getFirst().length();
    System.out.println("length: " + length + " height: " + height);
    Set<Integer> totalAntinodes = new HashSet<>();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < length; x++) {
        char antenna = input.get(y).charAt(x);
        if (antenna == '.') {
          continue;
        }
        System.out.println("Searching pair for " + antenna + " (" + y + "," + x + ")");
        for (int yy = y; yy < height; yy++) {
          for (int xx = 0; xx < length; xx++) {
            if (y == yy && x >= xx) {
              continue;
            }
            char antennaPair = input.get(yy).charAt(xx);
            if (antenna != antennaPair) {
              continue;
            }
            System.out.println("Searching antinodes for " + antenna + " (" + y + "," + x + ") and " + antennaPair + " (" + yy + "," + xx + ")");
            {
            System.out.println("Adding antinodes (" + y + "," + x + ")");
            int position = x + y * length;
            totalAntinodes.add(position);
            }
            {
            System.out.println("Adding antinodes (" + yy + "," + xx + ")");
            int position = xx + yy * length;
            totalAntinodes.add(position);
            }
            final int xa, ya, xb, yb;
            if (x >= xx) {
              xa = x;
              ya = y;
              xb = xx;
              yb = yy;
            } else {
              xa = xx;
              ya = yy;
              xb = x;
              yb = y;
            }

            int x1 = 2 * xa - xb;
            int y1 = 2 * ya - yb;
            while (x1 >= 0
                && y1 >= 0
                && x1 < length
                && y1 < height) {
              System.out.println("Adding antinodes (" + y1 + "," + x1 + ")");
              int position = x1 + y1 * length;
              totalAntinodes.add(position);
              x1 -= xb - xa;
              y1 -= yb - ya;
            }
            System.out.println("Skipping antinodes (" + y1 + "," + x1 + ")");
            int x2 = 2 * xb - xa;
            int y2 = 2 * yb - ya;
            while (x2 >= 0
                && y2 >= 0
                && x2 < length
                && y2 < height) {
              System.out.println("Adding antinodes (" + y2 + "," + x2 + ")");
              int position = x2 + y2 * length;
              totalAntinodes.add(position);
              x2 += xb - xa;
              y2 += yb - ya;
            }
            System.out.println("Skipping antinodes (" + y2 + "," + x2 + ")");
          }
        }
      }
    }
    System.out.println(IntStream.range(0, height)
        .mapToObj(y -> totalAntinodes.stream()
            .filter(position -> position / length == y)
            .map(position -> position % length)
            .reduce(
              input.get(y),
              (line, x) -> input.get(y).charAt(x) == '.' ? line.substring(0, x) + '#' + line.substring(x + 1, length) : line.substring(0, x) + '@' + line.substring(x + 1, length),
              (u, v) -> v))
        .collect(Collectors.joining("\n")));
    System.out.println(totalAntinodes.size());
  }

}
