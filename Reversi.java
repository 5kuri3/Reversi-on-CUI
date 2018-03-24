import java.util.concurrent.TimeUnit;

public class Reversi extends SimpleGame {
    public static final long TIME_LIMIT_MILLIS = TimeUnit.MINUTES.toMillis(20);
    
    // typeに応じてプレイヤーを作って返す．オーバーライドすればプレイヤーの種類を増やせる
    @Override
    protected Player createPlayer(String type, String name) {
        if ("input".equals(type)) {
            return createDefaultInput(name);
        }
        else {
            return createDefaultAI(name);
        }
    }
    
    @Override
    protected Player createDefaultInput(String name) {
        return new InputPlayer(name, System.in, System.out);
    }
    
    @Override
    protected Player createDefaultAI(String name) {
        return new TimeLimitedPlayer(new GeneralPlayer(name), TIME_LIMIT_MILLIS, System.out);
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
