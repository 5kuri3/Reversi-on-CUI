import java.io.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class Reversi extends SimpleGame {
    public static final long TIME_LIMIT_MILLIS = TimeUnit.MINUTES.toMillis(20);
    
    // typeに応じてプレイヤーを作って返す．オーバーライドすればプレイヤーの種類を増やせる
    @Override
    protected Player createPlayer(String type, String name) {
        if ("input".equals(type)) {
            return new InputPlayer(name, System.in, System.out);
        }
        else if("general".equals(type)) {
            return new TimeLimitedPlayer(new GeneralPlayer(name), TIME_LIMIT_MILLIS, System.out);
        }
        else if("special".equals(type)) {
            return new TimeLimitedPlayer(SpecialPlayer.standardScore(name), TIME_LIMIT_MILLIS, System.out);
        }
        else if("special?".equals(type)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            int k1;
            int k2;
            int endGameCount;
            long timeLimitMs;
            try {
                k1 = ask(System.out, reader, name + "(special): K1?", SpecialPlayer.STANDARD_K1).intValue();
                k2 = ask(System.out, reader, name + "(special): K2?", SpecialPlayer.STANDARD_K2).intValue();
                endGameCount = ask(System.out, reader, name + "(special): EndGameCnt?", SpecialPlayer.STANDARD_END_GAME_COUNT).intValue();
                timeLimitMs =  ask(System.out, reader, name + "(special): TimeLim[ms]?", SpecialPlayer.STANDARD_TIME_LIMIT_MILLIS).longValue();
            }
            catch(IOException e) {
                e.printStackTrace();
                System.err.println("警告: Special AI にデフォルトのパラメータを与えます");
                return new TimeLimitedPlayer(SpecialPlayer.standardScore(name), TIME_LIMIT_MILLIS, System.out);
            }
            return new TimeLimitedPlayer(SpecialPlayer.standardScore(name, k1, k2, endGameCount, timeLimitMs), TIME_LIMIT_MILLIS, System.out);
        }
        else {
            System.err.println(String.format("警告: %s は不明なプレイヤータイプです", type));
            return null;
        }
    }

    @Override
    protected String explainPlayerType() {
        return "player-type:\n    input: 手入力\n    general: General AI\n    special: Special AI";
    }

    @Override
    protected Collection<String> effectiveCommandArguments() {
        return new HashSet<String>() {
            {
                add("inverse");
            }
        };
    }

    private Long ask(PrintStream writer, BufferedReader reader, String forWhat, long defaultValue) throws IOException {
        while(true) {
            writer.print("\u001b[00;32m" + String.format("%s (default: %d) >", forWhat, defaultValue) + "\u001b[00m");
            try {
                String s = reader.readLine();
                if(s.length() > 0) {
                    Long l = Long.parseLong(s);
                    return l;
                }
                else {
                    return defaultValue;
                }
            }
            catch(NumberFormatException e) {
                System.err.println("\u001b[00;33m警告: 整数値を入力してください．空文字リターンでデフォルト値です．やり直し\u001b[00m");
            }
        }

    }
    
    public Reversi(String[] args) {
        super(args);
    }

	public static void main(String[] args) {
        boolean inverse = false;
        for (String arg : args) {
            if ("inverse".equals(arg)) {
                inverse = true;
            }
        }
        new Reversi(args).play(new ReversiBoard(8, 8, inverse));
    }
}
