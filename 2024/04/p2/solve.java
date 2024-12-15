import java.nio.file.*;
import java.util.stream.*;
import java.util.regex.*;

public class solve {

  public static void main(String[] args) throws Exception {
    var input = Files.lines(Paths.get("input"))
      .toList();
    var inputChars = input.stream()
      .map(line -> line.chars().toArray())
      .toList();
    int lines = input.size();
    int columns = input.get(0).length();
    int count = 0;
    for (int m = 0; m < lines; m++) {
      if (m >= lines - 2) {
        continue;
      }
      for (int n = 0; n < columns; n++) {
        if (n >= columns - 2) {
          continue;
        }
        if (((inputChars.get(m)[n] == 'M'
          && inputChars.get(m + 1)[n + 1] == 'A'
          && inputChars.get(m + 2)[n + 2] == 'S')
          || (inputChars.get(m)[n] == 'S'
          && inputChars.get(m + 1)[n + 1] == 'A'
          && inputChars.get(m + 2)[n + 2] == 'M'))
          && ((inputChars.get(m)[n + 2] == 'M'
          && inputChars.get(m + 1)[n + 1] == 'A'
          && inputChars.get(m + 2)[n] == 'S')
          || (inputChars.get(m)[n + 2] == 'S'
          && inputChars.get(m + 1)[n + 1] == 'A'
          && inputChars.get(m + 2)[n] == 'M'))) {
          count++;
        }
      }
    }
    System.out.println(count);
  }


}
