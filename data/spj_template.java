import java.io.File;
import java.util.Scanner;

public class spj {  // class name must be 'spj', do NOT change.
    public static void main(String args[]) {
        try {
            Scanner in = new Scanner(new File(args[0]));
            Scanner out = new Scanner(new File(args[1]));
            Scanner user = new Scanner(new File(args[2]));
            int ret = 0;  // 0-AC, 1-WA, 2-PE, -1-JE
            while (in.hasNext()) {
                int a = in.nextInt(), b = in.nextInt();
                int c = user.nextInt();
                if (a + b != c) {
                    System.out.printf("%d + %d != %d\n", a, b, c);
                    ret = 1;
                    break;
                }
            }
            System.exit(ret);
        } catch (Exception e) {
            System.exit(-1);
        }
    }
}