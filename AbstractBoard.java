import java.util.*;

// Boardに必要な機能のうち，よく使うものをまとめたクラス
public abstract class AbstractBoard implements Board {
    Player.ID nextTurn;
    Deque<Integer> history;     // 打った手をプッシュして記録する
    public AbstractBoard() {
        history = new LinkedList<Integer>();
        nextTurn = Player.ID.P1;
    }
    void flipTurn() { nextTurn = opposite(); } // 手番を入れ替える
    Player.ID opposite() {                     // 「今の手番のプレイヤー」の相手のプレイヤー
        return nextTurn == Player.ID.P1 ? Player.ID.P2 : Player.ID.P1;
    }
    public Player.ID nextTurn() { return nextTurn; } // 今の手番のプレイヤー
}
