package io.ipolyzos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.stream.Stream;

public class DataSourceUtils {
    public static Stream<String> loadDataFile(String fileName) throws IOException {
        return Files.lines(
                Paths.get(System.getProperty("user.dir") +  fileName)
        ).skip(1);
    }

    public static Event lineAsEvent(String line) {
        String[] tokens = line.split(",");
        Timestamp timestamp = Timestamp.valueOf(tokens[0]);
        return new Event(timestamp.getTime(), tokens[1], tokens[2], tokens[3], tokens[4], tokens[5], Double.parseDouble(tokens[6]), tokens[7], tokens[8]);
    }
}
