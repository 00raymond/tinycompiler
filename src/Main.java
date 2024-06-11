// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        String fn;

        if (args.length == 0) {
            System.out.println("No file name provided. Defaulting to Test.txt");
            fn = "Test.txt";
        } else {
            fn = args[0];
        }

        new FileReader(fn);
        new SSA();

        if (!Tokenizer.Id2String(Tokenizer.getNext()).equals("main")) {
            System.out.println("Error: Program does not begin with main.");
        } else {
            try {
                Parser.computation();
            } catch (Exception e) {
                FileReader.Error(e);
            }
        }
    }
}