
public class Reversi extends SimpleGame {
//
//	@Override
//	Player makePlayer(String type, String name) {
//        return new SimplePlayer(name);
//    }

	public static void main(String[] args) {
		new Reversi().play(new ReversiBoard(8, 8));
	}
}
