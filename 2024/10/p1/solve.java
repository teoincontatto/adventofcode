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
    List<Step> trails = new ArrayList<>();
    for (char level = '0'; level <= '9'; level++) {
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < length; x++) {
          if (input.get(y).charAt(x) == level) {
            if (level == '0') {
              //System.out.println("Added level 0 (" + x + "," + y + ")");
              trails.add(new Step(level, x, y));
            } else {
              final int sx = x;
              final int sy = y;
              final char plevel = (char) (level - 1);
              final char slevel = level;
              //System.out.println("Search trail with level " + plevel + " next to level " + slevel + " (" + sx + "," + sy + ")");
              trails.stream()
                  .flatMap(step -> step.streamStepsForLevel(plevel))
                  .filter(step -> {
                    return ((step.x == sx + 1 || step.x == sx - 1) && step.y == sy)
                      || ((step.y == sy + 1 || step.y == sy - 1) && step.x == sx);
                  })
                  .forEach(step -> {
                    //System.out.println("Adding next to step with level " + step.level + " (" + step.x + "," + step.y + ")");
                    step.next.add(new Step(slevel, sx, sy));
                  });
            }
          }
        }
      }
    }

    long scoreSum = 0;
    for (Step trail :  trails) {
      long score = trail.streamStepsForLevel('9').collect(Collectors.groupingBy(step -> step.x + step.y * length)).size();
      scoreSum += score;
      /*
      if (score > 0) {
        trail.streamSubTrails('9')
            .forEach(subTrail -> {
              for (int y = 0; y < height; y++) {
                for (int x = 0; x < length; x++) {
                  System.out.print(subTrail.hasPosition(x, y) ? input.get(y).charAt(x) : '.');
                }
                System.out.println();
              }
              System.out.println();
            });
      }
      */
    }
    System.out.println(scoreSum);
  }

  static class Step {
    final List<Step> next = new ArrayList<>();
    final char level;
    final int x;
    final int y;

    Step(char level, int x, int y) {
      this.level = level;
      this.x = x;
      this.y = y;
    }

    Step(char level, int x, int y, Step next) {
      this.level = level;
      this.x = x;
      this.y = y;
      this.next.add(next);
    }

    Stream<Step> streamStepsForLevel(char targetLevel) {
      if (targetLevel == this.level) {
        return Stream.of(this);
      }
      return next.stream().flatMap(step -> step.streamStepsForLevel(targetLevel));
    }

    Stream<Step> streamSubTrails(char targetLevel) {
      if (targetLevel == this.level) {
        return Stream.of(new Step(this.level, this.x, this.y));
      }
      return next.stream()
          .flatMap(step -> step.streamSubTrails(targetLevel))
          .map(step -> new Step(this.level, this.x, this.y, step));
    }

    boolean hasPosition(int x, int y) {
      if (this.x == x && this.y == y) {
        return true;
      }
      return next.stream().anyMatch(step -> step.hasPosition(x, y));
    }
  }

}
