import java.util.Date;
import java.util.Stack;

/**
 * Created by Thomas VENNER on 20/08/2016.
 */
public class Utils {
    private static Stack<Long> timeStack = new Stack<>();

    public static void startChrono() {
        timeStack.push(new Date().getTime());
    }

    public static long endChrono() {
        if (!timeStack.empty()) {
            return new Date().getTime() - timeStack.pop();
        }
        return -1;
    }

    public static String stringRepeat(String s, int n) {
        n = Math.max(0, n);
        return new String(new char[n]).replace("\0", s);
    }
}
