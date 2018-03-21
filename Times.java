
public abstract class Times {

    public static String format(long millis) {
        long min = millis / (60 * 1000);
        long sec = (millis % (60 * 1000)) / 1000;
        long ms = millis % 1000;
        return String.format("%02d:%02d.%03d", min, sec, ms);
    }
}
