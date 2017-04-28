import java.io.File;

public class Parser {
    public static void main(String[] args) {

        try {
            PackageScanner scanner = new PackageScanner(args[0]); // give file name as first arg
            System.out.println(scanner); // pipe to file if for best results
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
}
