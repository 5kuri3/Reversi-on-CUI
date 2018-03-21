import java.io.PrintStream;
import java.util.List;
import java.util.Random;

public class TimeLimitedPlayer implements Player {

    private static class PlayerRunner implements Runnable {
        private final Player player;
        private final Board board;
        private int move = Board.M_VOID;
        public PlayerRunner(Player player, Board board) {
            this.player = player;
            this.board = board;
        }
        @Override public void run() {
            int m = player.move(board);
            this.move = m;
        }
        public int getResult() {
            return move;
        }
    }
    
    private final Player player;
    private final PrintStream out;
    private final Random random = new Random();
    private long timeLeftMillis;
    
    public TimeLimitedPlayer(Player player, long timeLimitMillis, PrintStream out) {
        this.player = player;
        this.timeLeftMillis = timeLimitMillis;
        this.out = out;
    }
    
    @Override
    public int move(Board board) {
        long timeLeftMillis = getTimeLeftMillis();
        if(timeLeftMillis > 0) {
            PlayerRunner runner = new PlayerRunner(player, board.clone());
            Thread thread = new Thread(runner);
            thread.start();
            long start = System.currentTimeMillis();
            try {
                thread.join(getTimeLeftMillis());
            }
            catch(InterruptedException e) {
                throw new RuntimeException(e);
            }
            long end = System.currentTimeMillis();
            reduceTimeLeft(start, end);
            if(!thread.isAlive()) {
                int m = runner.getResult();
                if(m != Board.M_VOID) {
                    return m;
                }
            }
        }
        out.println(String.format("\u001b[00;33m警告: タイムアウトにより，%s は打つ手をランダムに選択します\u001b[00m", getName()));
        return chooseRandom(board.legalMoves());
    }
    
    @Override
    public String getName() {
        return player.getName();
    }
    
    @Override
    public String getTitle() {
        return String.format("%s [%s]", player.getTitle(), Times.format(getTimeLeftMillis()));
    }
    
    public long getTimeLeftMillis() {
        return timeLeftMillis;
    }
    
    private void reduceTimeLeft(long millis) {
        long timeLeftMillis = this.timeLeftMillis - millis;
        if(timeLeftMillis >= 0) this.timeLeftMillis = timeLeftMillis;
        else this.timeLeftMillis = 0L;
    }
    
    private void reduceTimeLeft(long start, long end) {
        reduceTimeLeft(end - start);
    }
    
    private int chooseRandom(List<Integer> list) {
        int i = random.nextInt(list.size());
        return list.get(i);
    }
}
