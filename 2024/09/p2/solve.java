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
      int size = Integer.parseInt("" + map.charAt(position));
      final Sector sector;
      if (position % 2 == 0) {
        sector = new File(index, size);
        index++;
      } else {
        sector = new Space(size);
      }
      for (int repeat = 0; repeat < size; repeat++) {
        disk.add(sector);
      }
    }
    //printDisk(disk);
    final int diskLength = disk.size();
    System.out.println("Disk length: " + diskLength);
    index--;
    while (index > 0) {
      System.out.println("Searching index " + index);
      for (int right = 0; right < diskLength; right++) {
        if (disk.get(right) instanceof File fileFound
            && fileFound.index == index) {
          System.out.println("File " + fileFound.index + " (" + fileFound.size + ") found at position " + right);
          for (int left = 0; left < diskLength; left++) {
            if (disk.get(left) instanceof Space spaceFound
                && fileFound.size <= spaceFound.size
                && right > left) {
              System.out.println("Space (" + spaceFound.size + ") found at position " + left);
              Space newSpace = new Space(spaceFound.size - fileFound.size);
              for (int position = right; position < right + fileFound.size; position++) {
                disk.set(position, newSpace);
              }
              int position = left;
              for (; position < left + fileFound.size; position++) {
                disk.set(position, fileFound);
              }
              if (position < left + spaceFound.size) {
                Space leftSpace = new Space(left + spaceFound.size - position);
                for (; position < left + spaceFound.size; position++) {
                  disk.set(position, leftSpace);
                }
              }
              break;
            }
          }
          break;
        }
      }
      index--;
      //printDisk(disk);
    }
    //printDisk(disk);
    long checksum = 0;
    for (int position = 0; position < diskLength; position++) {
      if (disk.get(position) instanceof File) {
        checksum += File.class.cast(disk.get(position)).index * position;
      }
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
    final int size;

    Space(int size) {
      this.size = size;
    }

    public String toString() {
      return ".";
    }
  }

  static class File extends Sector {
    final int index;
    final int size;

    File(int index, int size) {
      this.index = index;
      this.size = size;
    }

    public String toString() {
      return "" + index;
    }
  }
}
