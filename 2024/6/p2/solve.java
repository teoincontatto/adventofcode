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

    int X = x;
    int Y = y;
    Set<Integer> positions = new HashSet<>(length * height);
    positions.add(x + y * length);
    while (true) {
      //showLab(input, height, length, x, y, dx, dy, -1, -1, null);
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
        continue;
      }
      x += dx;
      y += dy;
      positions.add(x + y * length);
    }
    showLab(input, height, length, x, y, dx, dy, -1, -1, positions);
    Set<Integer> obstaclePositions = new HashSet<>();
    for (int position : positions) {
      if (position == X + Y * length) {
        continue;
      }
      x = X;
      y = Y;
      dx = 0;
      dy = -1;
      Set<Integer> positionAndDirections = new HashSet<>(length * height * 3 * 3);
      while (true) {
        //showLab(input, height, length, x, y, dx, dy, position % length, position / length, null);
        if (x == 0 || x == length - 1
            || y == 0 || y == height - 1) {
          break;
        }
        if (input.get(y + dy).charAt(x + dx) == '#'
            || x + dx + (y + dy) * length == position) {
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
          continue;
        }
        x += dx;
        y += dy;
        int positionAndDirection = x * 9 + y * length * 9 + (dx + 1) + (dy + 1) * 3;
        if (positionAndDirections.contains(positionAndDirection)) {
          obstaclePositions.add(position);
          break;
        }
        positionAndDirections.add(positionAndDirection);
      }
    }
    System.out.println(obstaclePositions.size());
  }

  record PositionAndDirection(int x, int y, int dx, int dy) {}

  static final String ANSI_RESET = "\u001B[0m";
  static final String ANSI_GREEN = "\u001B[32m";
  static final String ANSI_RED = "\u001B[31m";

  static void showLab(List<String> input, int height, int length, int x, int y, int dx, int dy, int obstacleX, int obstacleY, Set<Integer> positions) {
    final char guard;
    if (dy == 1) {
      guard = 'v';
    } else if (dx == 1) {
      guard = '>';
    } else if (dy == -1) {
      guard = '^';
    } else {
      guard = '<';
    }
    System.out.println();
    IntStream.range(0, height)
        .mapToObj(yy -> new Line(yy, input.get(yy).replace("^", ".")))
        .map(yy -> {
          if (y == yy.y) {
            return new Line(yy.y, yy.line.substring(0, x)
                + guard
                + yy.line.substring(x + 1, length));
          }
          return yy;
        })
        .map(yy -> {
          if (obstacleY == yy.y) {
            return new Line(yy.y, yy.line.substring(0, obstacleX)
                + 'O'
                + yy.line.substring(obstacleX + 1, length));
          }
          return yy;
        })
        .map(yy -> {
          if (positions != null) {
            var lineChars = new ArrayList<>(yy.line.codePoints().mapToObj(c -> c).toList());
            positions.stream()
                .filter(p -> p / length == yy.y)
                .forEach(p -> lineChars.set(p % length, (int) 'X'));
            return new Line(yy.y, lineChars.stream().mapToInt(c -> c)
                .collect(
                  StringBuilder::new,
                  StringBuilder::appendCodePoint,
                  StringBuilder::append)
                .toString());
          }
          return yy;
        })
        .map(Line::line)
        .forEach(System.out::println);
    System.out.println();
    try {
      Thread.sleep(50);
    } catch (Exception ex) {
      return;
    }
  }

  record Line(int y, String line) {}
}
