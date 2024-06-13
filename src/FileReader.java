import java.io.File;
import java.nio.file.Files;

public class FileReader {
    private static File file;
    private static String fileContent;
    private static int currPos = 0;

    public static int getCurrPos() {
        return currPos;
    }

    public static void setCurrPos(int pos) {
        currPos = pos;
    }

    public FileReader(String fn) {
        file = new File("src/" + fn);
        try {
            fileContent = Files.readString(file.toPath());
        } catch (Exception e) {
            Error(e);
        }
    }

    public static char getNext() {
        char op;

        op = fileContent.charAt(currPos);

        if (currPos < fileContent.length() - 1) {
            currPos++;
        } else {
            isEOF();
        }

        return op;
    }

    public static void isEOF() {
    }

    public static void Error(Exception e) {
        System.out.println("Error found here: " + e.getMessage());
    }

}