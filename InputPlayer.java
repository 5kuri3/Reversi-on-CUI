import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Scanner;

public class InputPlayer implements Player {
    private static final String TITLE = "操作プレイヤー";
    private static final String INPUT_QUIT = "QUIT";
    private static final String INPUT_DISPLAY = "?";
    private static final String INPUT_UNDO = "undo";
    private final String name;
    private final Scanner sc;
    private final PrintStream out;

    public InputPlayer(String name, InputStream in, PrintStream out) {
        this.name = name;
        this.sc = new Scanner(in);
        this.out = out;
    }

    @Override
    public int move(Board board) {
        while(true) {
            System.out.print(String.format("%s's hand? (%s:終了, %s:再表示, %s:一手戻る) >", getName(), INPUT_QUIT, INPUT_DISPLAY, INPUT_UNDO));
            String cmd = sc.nextLine();
            if(INPUT_QUIT.equals(cmd)) {
                System.exit(0);
            }
            else if(INPUT_DISPLAY.equalsIgnoreCase(cmd)) {
                board.print(out);
                continue;
            }
            else if(INPUT_UNDO.equalsIgnoreCase(cmd)) {
                if(board.unputCapacity() < 2) {
                    System.out.println("戻れません");
                    continue;
                }
                else {
                    return Board.M_UNDO;
                }
            }
            else {
                int m = board.keyToMove(cmd);
                if(m == Board.M_VOID) {
                    System.out.println("無効な入力");
                    continue;
                }
                else {
                    return m;
                }
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }
}
