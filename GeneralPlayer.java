import java.util.*;

// モンテカルロ法を用い，Boardインターフェースを守っているゲーム盤なら何でも，ある程度
// 「よい」手を選ぶ，何でも屋ゲームプレイヤー
public class GeneralPlayer implements Player {
    String name;
    Random random;
    public GeneralPlayer(String name) {
        this.name = name;
        random = new Random();
    }
    public String getName() { return name; }
    public int move(Board board) {
        List<Integer> moves = board.legalMoves();
        int n = moves.size();
        Player.ID myID = board.nextTurn();
        long[] win = new long[n];
        long[] lost = new long[n];
        long start = System.nanoTime();
        while (System.nanoTime() - start < 1000000000L) { // 1秒間シミュレーションを繰り返す
            int i = 0;
            for (int m : moves) { // すべての可能な手について1回ずつシミュレーションする
                board.put(m);
                Player.ID winner = tryOne(board); // ゲーム終了までランダムな手を選ぶ
                if (winner == myID) {             // 自分の勝ちだった
                    win[i]++;
                } else if (winner != Player.ID.NONE) {
                    lost[i]++;  // 勝ちでも引き分けでもなければ負け
                }
                board.unput();
                i++;
            }
        }
        // (勝利数 - 敗北数) がもっとも大きい手を選ぶ
        int max = 0;
        for (int i = 0; i < n; i++) {
            if (win[max] - lost[max] < win[i] - lost[i]) {
                max = i;
            }
        }
        return moves.get(max);
    }
    private Player.ID tryOne(Board board) {
        if (board.isEndOfGame()) {
            return board.winner(); // 最終的な勝者を返す
        }
        List<Integer> m = board.legalMoves();
        board.put(m.get(random.nextInt(m.size()))); // ランダムな手を選ぶ
        Player.ID value = tryOne(board);            // 再帰的に次の局面へ
        board.unput();                              // 元の状態を復元
        return value;                               // このシミュレーションの結果の勝者
    }
}
