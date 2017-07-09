interface Player {
    enum ID {                   // プレイヤーを区別するための定数を定義する
        NONE,                   // 「どちらのプレイヤーでもない」ことを表す
        P1,                     // プレイヤー1を表す
        P2                      // プレイヤー2を表す
    }
    int move(Board board);      // Boardの可能な手からひとつ選ぶ
    String getName();           // プレイヤー名を返す
}
