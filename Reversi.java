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
        else {
            System.err.println(String.format("警告: %s は不明なプレイヤータイプです", type));
            return null;
        }
    }

    @Override
    protected String explainPlayerType() {
        return "player-type:\n    input: 手入力\n    general: General AI\n    special: Special AI";
    }
    
    public Reversi(String[] args) {
        super(args);
    }

	public static void main(String[] args) {
	    boolean inverse = false;
	    for(String arg : args) {
	        if("inverse".equals(arg)) {
	            inverse = true;
	        }
	    }
		new Reversi(args).play(new ReversiBoard(8, 8, inverse));
	}
}
