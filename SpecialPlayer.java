import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class SpecialPlayer implements Player {
    private static final String TITLE = "Special AI";
    private static final long STANDARD_TIME_LIMIT_MILLIS = 30300;
    private static final int STANDARD_P = 100;
    private static final int STANDARD_Q = 3;
    private static final int STANDARD_END_GAME_COUNT = 12;
    private static final int INF = Integer.MAX_VALUE / 2;
    private static final boolean DEBUG_MODE = false;
    private final String name;
    private final Function<ReversiBoard, Integer> score;
    private final long timeLimitMillis;
    private final int endGameCount;

    public static SpecialPlayer standardScore(String name) {
        return new SpecialPlayer(name, new StandardReversiScore(STANDARD_P, STANDARD_Q), STANDARD_TIME_LIMIT_MILLIS, STANDARD_END_GAME_COUNT);
    }

    private static class TimeLimitException extends Exception { }

    public SpecialPlayer(String name, Function<ReversiBoard, Integer> score, long timeLimitMillis, int endGameCount) {
        this.name = name;
        this.score = score;
        this.timeLimitMillis = timeLimitMillis;
        this.endGameCount = endGameCount;
    }

    @Override
    public int move(Board board) {
        if(DEBUG_MODE) {
            System.err.println(String.format("\u001b[00;33m[DEBUG] 警告: %s: デバッグモード有効\u001b[00m", getClass().getSimpleName()));
        }
        ReversiBoard reversiBoard;
        if(board instanceof ReversiBoard) {
            reversiBoard = (ReversiBoard)board;
        }
        else {
            throw new IllegalArgumentException("board is not an instance of ReversiBoard");
        }
        reversiBoard = reversiBoard.clone();
        if(isEndGame(reversiBoard)) {
            return endGameMove(reversiBoard);
        }
        else {
            return usualMove(reversiBoard);
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

    private boolean isEndGame(ReversiBoard board) {
        int boardSize = board.getBoardSize();
        int pieceCount = board.countAll();
        return boardSize - pieceCount <= endGameCount;
    }

    private int usualMove(ReversiBoard board) {
        Integer[] legalMoves = board.legalMoves().toArray(new Integer[0]);
        Integer[] scores = new Integer[legalMoves.length];
        Arrays.fill(scores, 0);
        long timeLimitAt = System.currentTimeMillis() + timeLimitMillis;
        int depth = 0;
        try {
            for (depth = 3; ; ++depth) {
                Integer[] tmp = new Integer[legalMoves.length];
                Arrays.fill(tmp, 0);
                for (int i = 0; i < legalMoves.length; ++i) {
                    int m = legalMoves[i];
                    board.put(m, true);
                    int score = abSearch(board, depth - 1, -INF, INF, timeLimitAt);
                    board.unput();
                    tmp[i] = score;
                }
                scores = tmp;
            }
        }
        catch(TimeLimitException e) {
        }
        if(DEBUG_MODE) {
            System.err.println("[DEBUG] Depth: " + depth);
        }
        int minScore = scores[0];
        int optMove = legalMoves[0];
        for(int i = 0; i < scores.length; ++i) {
            int score = scores[i];
            if(DEBUG_MODE) {
                System.err.println(String.format("[DEBUG] %s: %d", board.explain(legalMoves[i]), score));
            }
            if(minScore > score) {
                minScore = score;
                optMove = legalMoves[i];
            }
        }
        return optMove;
    }

    private int endGameMove(ReversiBoard board) {
        Integer[] legalMoves = board.legalMoves().toArray(new Integer[0]);
        Integer[] scores = new Integer[legalMoves.length];
        for (int i = 0; i < legalMoves.length; ++i) {
            int m = legalMoves[i];
            board.put(m, true);
            int score = abFullSearch(board, 0, -INF, INF);
            board.unput();
            scores[i] = score;
        }
        int minScore = scores[0];
        int optMove = legalMoves[0];
        for(int i = 0; i < scores.length; ++i) {
            int score = scores[i];
            if(DEBUG_MODE) {
                System.err.println(String.format("[DEBUG] %s: %d", board.explain(legalMoves[i]), score));
            }
            if(minScore > score) {
                minScore = score;
                optMove = legalMoves[i];
            }
        }
        return optMove;
    }

    private int abSearch(ReversiBoard board, int depth, int a, int b, long timeLimitAt) throws TimeLimitException {
        if(timeLimitAt < System.currentTimeMillis()) {
            throw new TimeLimitException();
        }
        if(depth <= 0) {
            return score.apply(board);
        }
        List<Integer> legalMoves = board.legalMoves();
        if(legalMoves.isEmpty()) {
            board.put(Board.M_VOID, true);
            int ans = -abSearch(board, depth - 1, -b, -a, timeLimitAt);
            board.unput();
            return ans;
        }
        for(int m : legalMoves) {
            board.put(m, true);
            int ans = -abSearch(board, depth - 1, -b, -a, timeLimitAt);
            board.unput();
            a = Integer.max(a, ans);
            if(a >= b) return a;
        }
        return a;
    }

    private int abFullSearch(ReversiBoard board, int pass, int a, int b) {
        final Player.ID self = board.nextTurn();
        final Player.ID enemy = board.opposite();
        final int boardSize = board.getBoardSize();
        final int allPiece = board.countAll();
        final int selfPiece = board.count(self);
        final int enemyPiece = board.count(enemy);
        final int score = selfPiece - enemyPiece;
        if(boardSize == allPiece) {
            return score;
        }
        List<Integer> legalMoves = board.legalMoves();
        if(legalMoves.isEmpty()) {
            if(pass > 0) {
                return score;
            }
            else {
                board.put(Board.M_VOID, true);
                int ans = -abFullSearch(board, pass + 1, -b, -a);
                board.unput();
                return ans;
            }
        }
        for(int m : legalMoves) {
            board.put(m, true);
            int ans = -abFullSearch(board, pass, -b, -a);
            board.unput();
            a = Integer.max(a, ans);
            if(a >= b) return a;
        }
        return a;
    }
}
