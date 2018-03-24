
// Boardに必要な機能のうち，よく使うものをまとめたクラス
public abstract class AbstractBoard implements Board {
    protected Player.ID nextTurn;
    
    public AbstractBoard() {
        nextTurn = Player.ID.P1;
    }
    
    protected AbstractBoard(Player.ID firstTurn) {
        this.nextTurn = firstTurn;
    }
    
    @Override
    public abstract AbstractBoard clone();
    
    void flipTurn() {                       // 手番を入れ替える
        nextTurn = opposite();
    }
    
    Player.ID opposite() {                  // 「今の手番のプレイヤー」の相手のプレイヤー
        return nextTurn == Player.ID.P1 ? Player.ID.P2 : Player.ID.P1;
    }
    
    public Player.ID nextTurn() {           // 今の手番のプレイヤー
        return nextTurn;
    }
}
