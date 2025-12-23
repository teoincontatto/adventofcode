import java.nio.file.*;
import java.math.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;
import java.util.function.*;

public class solve {

  public static void main(String[] args) throws Exception {
    List<String> input = Files.lines(Paths.get("input"))
      .toList();
    final int height = input.size();
    final int length = input.getFirst().length();
    int totalForklifts = 0;
    while (true) {
      int forklifts = 0;
      List<String> newInput = new ArrayList<>(input);
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < length; x++) {
          if (input.get(y).charAt(x) == '@') {
            int paperRolls = 0;
            for (int yy = Math.max(0, y - 1); yy <= y + 1 && yy < height; yy++) {
              for (int xx = Math.max(0, x - 1); xx <= x + 1 && xx < length; xx++) {
                if ((xx != x || yy != y) && input.get(yy).charAt(xx) == '@') {
                  paperRolls++;
                }
              }
            }
            if (paperRolls < 4) {
              forklifts++;
              System.out.print('x');
              newInput.set(y, newInput.get(y).substring(0, x) + 'x' + newInput.get(y).substring(x + 1, length));
            } else {
              System.out.print(input.get(y).charAt(x));
            }
          } else {
            System.out.print(input.get(y).charAt(x));
          }
        }
        System.out.println();
      }
      if (forklifts <= 0) {
        break;
      }
      System.out.println(forklifts);
      totalForklifts += forklifts;
      input = newInput;
    }
    System.out.println(totalForklifts);
  }
}
