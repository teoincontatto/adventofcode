import java.nio.file.*;
import java.util.stream.*;
import java.util.regex.*;

public class solve {

  public static void main(String[] args) throws Exception {
    var xmasPattern = Pattern.compile("XMAS");
    var samxPattern = Pattern.compile("SAMX");
    var input = Files.lines(Paths.get("input"))
      .toList();
    var inputChars = input.stream()
      .map(line -> line.chars().toArray())
      .toList();
    int lines = input.size();
    int columns = input.get(0).length();
    int count = 0;
    for (int m = 0; m < lines; m++) {
      count += xmasPattern.matcher(input.get(m)).results().count();
      count += samxPattern.matcher(input.get(m)).results().count();
      if (m >= lines - 3) {
        continue;
      }
      for (int n = 0; n < columns; n++) {
        if (inputChars.get(m)[n] == 'X'
          && inputChars.get(m + 1)[n] == 'M'
          && inputChars.get(m + 2)[n] == 'A'
          && inputChars.get(m + 3)[n] == 'S') {
          count++;
        }
        if (inputChars.get(m)[n] == 'S'
          && inputChars.get(m + 1)[n] == 'A'
          && inputChars.get(m + 2)[n] == 'M'
          && inputChars.get(m + 3)[n] == 'X') {
          count++;
        }
        if (n >= columns - 3) {
          continue;
        }
        if (inputChars.get(m)[n] == 'X'
          && inputChars.get(m + 1)[n + 1] == 'M'
          && inputChars.get(m + 2)[n + 2] == 'A'
          && inputChars.get(m + 3)[n + 3] == 'S') {
          count++;
        }
        if (inputChars.get(m)[n] == 'S'
          && inputChars.get(m + 1)[n + 1] == 'A'
          && inputChars.get(m + 2)[n + 2] == 'M'
          && inputChars.get(m + 3)[n + 3] == 'X') {
          count++;
        }
        if (inputChars.get(m)[n + 3] == 'X'
          && inputChars.get(m + 1)[n + 2] == 'M'
          && inputChars.get(m + 2)[n + 1] == 'A'
          && inputChars.get(m + 3)[n] == 'S') {
          count++;
        }
        if (inputChars.get(m)[n + 3] == 'S'
          && inputChars.get(m + 1)[n + 2] == 'A'
          && inputChars.get(m + 2)[n + 1] == 'M'
          && inputChars.get(m + 3)[n] == 'X') {
          count++;
        }
      }
    }
    System.out.println(count);
  }


}
