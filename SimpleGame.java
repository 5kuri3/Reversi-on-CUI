import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class SimpleGame {
    public static final String NAME_P1 = "プレイヤー１";
    public static final String NAME_P2 = "プレイヤー２";
    
    private Player p1;
    private Player p2;
    
    public SimpleGame(String[] args) {
        parseArgs(args);
    }
    
    protected abstract Player createPlayer(String type, String name);
    
    protected abstract String explainPlayerType();

    protected Collection<String> effectiveCommandArguments() {
        return null;
    }
    
    private void parseArgs(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            if("--p1".equals(arg)) {
                ++i;
                if(i < args.length) {
                    this.p1 = createPlayer(args[i], NAME_P1);
                }
                else {
                    System.err.println("警告: オプション '--p1' は引数が必要です");
                } 
            }
            else if("--p2".equals(arg)) {
                ++i;
                if(i < args.length) {
                    this.p2 = createPlayer(args[i], NAME_P2);
                }
                else {
                    System.err.println("警告: オプション '--p2' は引数が必要です");
                } 
            }
            else {
                Collection<String> cmdArgs = effectiveCommandArguments();
                if(cmdArgs == null || !cmdArgs.contains(arg)) {
                    System.err.println(String.format("認識されないオプション '%s' は無視されます", arg));
                }
            }
        }
    }
    
    // Boardと2つのプレイヤーを使う，ゲームのメインプログラム
    public void play(Board board) {
        if(p1 == null || p2 == null) {
            if(p1 == null) {
                System.err.println("エラー: --p1 <player-type> オプションでプレイヤータイプを設定する必要があります");
            }
            if(p2 == null) {
                System.err.println("エラー: --p2 <player-type> オプションでプレイヤータイプを設定する必要があります");
            }
            System.err.println(explainPlayerType());
            System.exit(-2);
        }
        System.out.println();
        System.out.println(String.format("%s 先攻: %s (%s)", board.drawPiece(Player.ID.P1).toString(), p1.getName(), p1.getTitle()));
        System.out.println(String.format("%s 後攻: %s (%s)", board.drawPiece(Player.ID.P2).toString(), p2.getName(), p2.getTitle()));
        System.out.println();
        System.out.println("＊　＊　＊　ゲ　ー　ム　ス　タ　ー　ト　＊　＊　＊");
        System.out.println();
        while (! board.isEndOfGame()) {
            board.print(System.out);
            Player.ID turn = board.nextTurn();
            Player nextPlayer = (turn == Player.ID.P1 ? p1 : p2);
            System.out.println(String.format("%s (%s) のターンです", nextPlayer.getName(), nextPlayer.getTitle()));
            int m = nextPlayer.move(board);
            System.out.println();
            if(m == Board.M_UNDO) {
                if(board.unputCapacity() >= 2) {
                    System.out.print(nextPlayer.getName() + " は一手戻しました");
                    board.unput();
                    board.unput();
                }
                else {
                    System.out.print("エラー: Undo に失敗しました．このターンからやり直します");
                }
            }
            else {
                System.out.print(nextPlayer.getName() + " は " + board.explain(m) + " を選びました");
                try {
                    board.put(m);
                }
                catch(IllegalArgumentException e) {
                    System.out.print("エラー: 不可能な場所に置こうとしました．このターンからやり直します");
                }
            }
            if(nextPlayer instanceof TimeLimitedPlayer) {
                TimeLimitedPlayer timeLimitedPlayer = (TimeLimitedPlayer)nextPlayer;
                System.out.println(String.format(" [残り時間: %s]", Times.format(timeLimitedPlayer.getTimeLeftMillis())));
            }
            else {
                System.out.println();
            }
        }
        board.print(System.out);
        System.out.println("ゲーム終了");
        Player.ID winner = board.winner();
        if (winner != Player.ID.NONE) {
            Player winPlayer = (winner == Player.ID.P1 ? p1 : p2);
            System.out.println(winPlayer.getName() + " (" + winPlayer.getTitle() + ") の勝ちです．");
        } else {
            System.out.println("引き分け");
        }
    }
}
