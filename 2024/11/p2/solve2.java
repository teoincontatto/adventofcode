import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class solve2 {
    public static void main(String[] args) {
        HashMap<Long, Long> stones = new HashMap<Long, Long>();
        try (BufferedReader bin = new BufferedReader(new FileReader("input"))) {
            String s = bin.readLine();
            String[] tokens = s.split(" ");
            for (String string : tokens) {
                stones.put(Long.parseLong(string), stones.getOrDefault(Long.parseLong(string), Long.valueOf(0)) + 1);
            }
        } catch (Exception e) {
            System.err.println(e);
        }

        for (int k = 0; k < 75; k++) {
            HashMap<Long, Long> new_stones = new HashMap<Long, Long>();
            for (Map.Entry<Long, Long> entry : stones.entrySet()) {
                Long stone = entry.getKey();
                Long n = entry.getValue();
                if (stone == 0)
                    new_stones.put(Long.valueOf(1), new_stones.getOrDefault(n, Long.valueOf(0)) + 1);
                else if (Long.toString(stone).length() % 2 == 0) {
                    new_stones.put(Long.parseLong(Long.toString(stone).substring(0, Long.toString(stone).length() / 2)),
                            new_stones.getOrDefault(Long
                                    .parseLong(Long.toString(stone).substring(0, Long.toString(stone).length() / 2)),
                                    Long.valueOf(0))
                                    + n);
                    new_stones.put(Long.parseLong(Long.toString(stone).substring(Long.toString(stone).length() / 2)),
                            new_stones.getOrDefault(
                                    Long.parseLong(Long.toString(stone).substring(Long.toString(stone).length() / 2)),
                                    Long.valueOf(0)) + n);
                } else
                    new_stones.put(stone * 2024, n);
            }
            stones = new_stones;
        }
        Long stone_count = Long.valueOf(0);
        for (Long value : stones.values()) {
            stone_count += value;
        }
        System.out.println(stone_count);
    }
}