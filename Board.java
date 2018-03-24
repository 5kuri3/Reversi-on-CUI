import java.util.List;
import java.io.PrintStream;

// ゲーム盤を表すインターフェース
interface Board extends Cloneable {
    public static final int M_UNDO = -1;
    public static final int M_VOID = -2;
    Board clone();
    Player.ID nextTurn();       // 次の手番のプレイヤー
    boolean isEndOfGame();      // ゲームが終了したならtrue
    Player.ID winner();         // 勝者のプレイヤーID．引き分けならNONE．
                                //  (ゲーム終了していなければ未定義)．
    List<Integer> legalMoves(); // 打てる手のリスト
    int keyToMove(String key);  // キー入力を手に変換する
    Object boardState();        // 盤の状態を返す(内容はゲームに依存する)
    void put(int m);            // 手を打つ
    void unput();               // putの効果を取り消す．何段階でも取り消せる．
    int unputCapacity();        // unput可能な回数
    void print(PrintStream out);// 盤の状態，可能な手をプリントする．
    String explain(int m);      // 打った手の解説．リバーシでは座標
    Object drawPiece(Player.ID id); // コマの描画
}
