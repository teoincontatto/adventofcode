import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

public class solve {

  public static void main(String[] args) throws Exception {
    var input = Files.lines(Paths.get("input"))
      .toList();
    var instructions = input.stream()
        .filter(line -> line.contains("|"))
        .map(line -> line.split("\\|"))
        .collect(Collectors.groupingBy(
          instruction -> instruction[1]))
        .entrySet()
        .stream()
        .collect(Collectors.toMap(
          instructionGroup -> Integer.parseInt(instructionGroup.getKey()),
          instructionGroup -> instructionGroup.getValue()
              .stream()
              .map(instruction -> Integer.parseInt(instruction[0]))
              .toList()));
    System.err.println("Instructions " + instructions
        .entrySet()
        .stream()
        .map(Map.Entry::getValue)
        .flatMap(List::stream)
        .count());
    var prints = input.stream()
        .filter(line -> line.contains(","))
        .map(line -> Arrays.asList(line.split(","))
            .stream()
            .map(n -> Integer.parseInt(n))
            .toList())
        .toList();
    System.err.println("Prints " + prints.size());
    int count = 0;
    for (var print : prints) {
      int pages = print.size();
      if (pages % 2 != 1) {
        System.err.println("**Page " + print + " has even pages!**");
      }
      boolean correct = true;
      print:
      for (int page = 0; page < pages - 1; page++) {
        for (int otherPage = page + 1; otherPage < pages; otherPage++) {
          if (instructions.containsKey(print.get(page))
              && instructions.get(print.get(page)).contains(print.get(otherPage))) {
            correct = false;
            break print;
          }
        }
      }
      if (!correct) {
        var orderedPrint = print
            .stream()
            .sorted(Comparator.comparing(
              Function.identity(),
              (o1, o2) -> instructions.containsKey(o1)
                && instructions.get(o1).contains(o2) ? -1 : 0))
            .toList();
        count += orderedPrint.get(pages / 2);
      }
    }
    System.out.println(count);
  }


}
