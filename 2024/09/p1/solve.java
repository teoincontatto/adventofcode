import java.nio.file.*;
import java.math.*;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

public class solve {

  public static void main(String[] args) throws Exception {
    final var input = Files.lines(Paths.get("input"))
      .toList();
    final String map = input.get(0);
    //System.out.println("Map: " + map);
    final int mapLength = map.length();
    System.out.println("Map length: " + mapLength);
    final List<Sector> disk = new ArrayList<>();
    int index = 0;
    for (int position = 0; position < mapLength; position++) {
      int times = Integer.parseInt("" + map.charAt(position));
      for (int repeat = 0; repeat < times; repeat++) {
        if (position % 2 == 0) {
          disk.add(new File(index));
        } else {
          disk.add(new Space());
        }
      }
      if (position % 2 == 0) {
        index++;
      }
    }
    //printDisk(disk);
    final int diskLength = disk.size();
    System.out.println("Disk length: " + diskLength);
    for (int left = 0, right = diskLength; left < right; left++) {
      if (disk.get(left) instanceof Space) {
        File file = null;
        while (left < right) {
          right--;
          if (disk.get(right) instanceof File fileFound) {
            file = fileFound;
            disk.remove(right);
            break;
          }
        }
        disk.set(left, file);
        //printDisk(disk);
      }
    }
    //printDisk(disk);
    long checksum = 0;
    for (int position = 0; disk.get(position) instanceof File && position < diskLength; position++) {
      checksum += File.class.cast(disk.get(position)).index * position;
    }
    System.out.println(checksum);
  }

  static void printDisk(List<Sector> disk) {
    System.out.println(disk.stream()
        .map(Object::toString)
        .collect(Collectors.joining()));
  }

  static abstract class Sector {
  }

  static class Space extends Sector {
    public String toString() {
      return ".";
    }
  }

  static class File extends Sector {
    final int index;

    File(int index) {
      this.index = index;
    }

    public String toString() {
      return "" + index;
    }
  }
}
