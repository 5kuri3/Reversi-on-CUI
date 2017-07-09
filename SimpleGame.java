import java.util.*;

public class SimpleGame {
    // typeに応じてプレイヤーを作って返す．オーバーライドすればプレイヤーの種類を増やせる
    Player makePlayer(String type, String name) {
        if (type.equals("general")) {
            return new GeneralPlayer(name);
        } else {
            return new SimplePlayer(name);
        }
    }
    Map<String,String> map;
    public SimpleGame() {
        map = new HashMap<String,String>();
        map.put("--p1", "simple");
        map.put("--p2", "general");
    }
    void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i += 2) {
            map.put(args[i], args[i + 1]);
        }
    }
    public SimpleGame(String[] args) {
        this();                 // 引数なしのコンストラクタを呼びだす
        parseArgs(args);
    }
    // Boardと2つのプレイヤーを使う，ゲームのメインプログラム
    void play(Board board) {
        Player p1 = makePlayer(map.get("--p1"), "プレイヤー1");
        Player p2 = makePlayer(map.get("--p2"), "プレイヤー2");
        while (! board.isEndOfGame()) {
            board.print(System.out);
            Player.ID turn = board.nextTurn();
            Player nextPlayer = (turn == Player.ID.P1 ? p1 : p2);
            System.out.println(nextPlayer.getName() + "の手番");
            int m = nextPlayer.move(board);
            System.out.println();
            System.out.println(nextPlayer.getName() + "は" + m + "を選びました");
            board.put(m);
        }
        board.print(System.out);
        System.out.println("ゲーム終了");
        Player.ID winner = board.winner();
        if (winner != Player.ID.NONE) {
            System.out.println((winner == Player.ID.P1 ? p1 : p2).getName() + "の勝ち．");
        } else {
            System.out.println("引き分け");
        }
    }
}
