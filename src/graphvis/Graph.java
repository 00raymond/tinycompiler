package graphvis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//graphviz java api needed
public class Graph {
    public static void createGraph(String dotSource) {

        String dirPath = "src/graphvis/output";
        String filePath = dirPath + "/dot.txt";

        File directory = new File(dirPath);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Directory created: " + dirPath);
            } else {
                System.out.println("Failed to create directory: " + dirPath);
                return;
            }
        }

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(dotSource);
            System.out.println("Dot source written to file: " + filePath);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e);
        }

    }
}
