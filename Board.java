import java.util.List;
import java.io.PrintStream;

// ゲーム盤を表すインターフェース
interface Board {
    Player.ID nextTurn();       // 次の手番のプレイヤー
    boolean isEndOfGame();      // ゲームが終了したならtrue
    Player.ID winner();         // 勝者のプレイヤーID．引き分けならNONE．
                                //  (ゲーム終了していなければ未定義)．
    List<Integer> legalMoves(); // 打てる手のリスト
    Object boardState();        // 盤の状態を返す(内容はゲームに依存する)
    void put(int m);            // 手を打つ
    void unput();               // putの効果を取り消す．何段階でも取り消せる．
    void print(PrintStream out); // 盤の状態，可能な手をプリントする．
}
