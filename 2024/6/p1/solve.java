import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

public class solve {

  public static void main(String[] args) throws Exception {
    var input = Files.lines(Paths.get("input"))
      .toList();
    int length = input.get(0).length();
    int height = input.size();
    int x = 0;
    int y = 0;
    int dx = 0;
    int dy = -1;
    System.out.println("length:" + length + " height:" + height);
    found:
    for (; y < height; y++) {
      x = 0;
      for (; x < length; x++) {
        if (input.get(y).charAt(x) == '^') {
          break found;
        }
      }
    }
    if (x == length && y == height) {
      System.err.println("Guard not found!");
      System.exit(1);
    }
    System.out.println("Guard found at x:" + x + " y:" + y);

    Set<Integer> positions = new HashSet<>(length * height);
    while (true) {
      positions.add(x + y * length);
      //showLab(input, height, length, x, y, dx, dy);
      if (x == 0 || x == length - 1
        || y == 0 || y == height - 1) {
        break;
      }
      if (input.get(y + dy).charAt(x + dx) == '#') {
        if (dy == -1) {
          dy = 0;
          dx = 1;
        } else if (dx == 1) {
          dy = 1;
          dx = 0;
        } else if (dy == 1) {
          dy = 0;
          dx = -1;
        } else {
          dy = -1;
          dx = 0;
        }
      }
      x += dx;
      y += dy;
    }
    System.out.println(positions.size());
  }

  static final String ANSI_RESET = "\u001B[0m";
  static final String ANSI_RED = "\u001B[31m";
  
  static void showLab(List<String> input, int height, int length, int x, int y, int dx, int dy) {
    final char guard;
    if (dy == 1) {
      guard = '_';
    } else if (dx == 1) {
      guard = '>';
    } else if (dy == -1) {
      guard = '^';
    } else {
      guard = '<';
    }
    System.out.println();
    IntStream.range(0, height)
        .mapToObj(yy -> {
          var line = input.get(yy).replace("^", ".");
          if (y == yy) {
            return line.substring(0, x) + ANSI_RED + guard + ANSI_RESET + line.substring(x + 1, length);
          }
          return line;
        })
        .forEach(System.out::println);
    System.out.println();
    try {
      Thread.sleep(250);
    } catch (Exception ex) {
    }
  }
}
