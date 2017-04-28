import java.io.File;


public class Parser {
    public static void main(String[] args) {

        try {
            PackageScanner scanner = new PackageScanner(args[0]);
            // System.out.println(scanner.loaded());
            String s;
            System.out.println(scanner);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
}