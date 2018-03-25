import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class StandardReversiScore implements Function<ReversiBoard, Integer> {
    private static final List<Pair<List<Boolean>, Integer> > CORNERS_SCORE = Collections.unmodifiableList(Arrays.asList(
            // 角，角の隣，角の隣の隣 の順番で, 自石が置かれていれば true, o.w. false.
            // 角が取れていれば100点, 角を取れずにその隣に自石が置かれていると-50点　
            Pair.of(Arrays.asList(true, true, true), 100),
            Pair.of(Arrays.asList(true, true, false), 100),
            Pair.of(Arrays.asList(true, false, true), 100),
            Pair.of(Arrays.asList(true, false, false), 100),
            Pair.of(Arrays.asList(false, false, true), 10),
            Pair.of(Arrays.asList(false, false, false), 0),
            Pair.of(Arrays.asList(false, true, true), -50),
            Pair.of(Arrays.asList(false, true, false), -50)
            ));

    private final int k;

    public StandardReversiScore(int k) {
        this.k = k;
    }

    @Override
    public Integer apply(ReversiBoard board) {
        final int iMin = board.getHeightMin();
        final int iMax = board.getHeightMax();
        final int jMin = board.getWidthMin();
        final int jMax = board.getWidthMax();
        final Player.ID selfPlayer = board.nextTurn();
        int selfMovableCount = 0;
        for(int i = iMin; i <= iMax; ++i) {
            for(int j = jMin; j <= jMax; ++j) {
                if(board.isAvailable(selfPlayer, j, i)) ++selfMovableCount;
            }
        }
        final Player.ID enemyPlayer = board.opposite();
        int enemyMovableCount = 0;
        for(int i = iMin; i <= iMax; ++i) {
            for(int j = jMin; j <= jMax; ++j) {
                if(board.isAvailable(enemyPlayer, j, i)) ++enemyMovableCount;
            }
        }
        int ansCornerScore;
        if(jMax - jMin >= 2 && iMax - iMin >= 2) {
            int [] cornerScores = new int[2];
            Player.ID[] players = new Player.ID[] { selfPlayer, enemyPlayer };
            for(int i = 0; i < 2; ++i) {
                Player.ID player = players[i];
                Boolean[][] corners = new Boolean[][]{
                        bits(board, player, Pair.of(jMin + 0, iMin + 0), Pair.of(jMin + 1, iMin + 0), Pair.of(jMin + 2, iMin + 0)),
                        bits(board, player, Pair.of(jMin + 0, iMin + 0), Pair.of(jMin + 0, iMin + 1), Pair.of(jMin + 0, iMin + 2)),
                        bits(board, player, Pair.of(jMin + 0, iMin + 0), Pair.of(jMin + 1, iMin + 1), Pair.of(jMin + 2, iMin + 2)),
                        bits(board, player, Pair.of(jMax - 0, iMin + 0), Pair.of(jMax - 1, iMin + 0), Pair.of(jMax - 2, iMin + 0)),
                        bits(board, player, Pair.of(jMax - 0, iMin + 0), Pair.of(jMax - 0, iMin + 1), Pair.of(jMax - 0, iMin + 2)),
                        bits(board, player, Pair.of(jMax - 0, iMin + 0), Pair.of(jMax - 1, iMin + 1), Pair.of(jMax - 2, iMin + 2)),
                        bits(board, player, Pair.of(jMin + 0, iMax - 0), Pair.of(jMin + 1, iMax - 0), Pair.of(jMin + 2, iMax - 0)),
                        bits(board, player, Pair.of(jMin + 0, iMax - 0), Pair.of(jMin + 0, iMax - 1), Pair.of(jMin + 0, iMax - 2)),
                        bits(board, player, Pair.of(jMin + 0, iMax - 0), Pair.of(jMin + 1, iMax - 1), Pair.of(jMin + 2, iMax - 2)),
                        bits(board, player, Pair.of(jMax - 0, iMax - 0), Pair.of(jMax - 1, iMax - 0), Pair.of(jMax - 2, iMax - 0)),
                        bits(board, player, Pair.of(jMax - 0, iMax - 0), Pair.of(jMax - 0, iMax - 1), Pair.of(jMax - 0, iMax - 2)),
                        bits(board, player, Pair.of(jMax - 0, iMax - 0), Pair.of(jMax - 1, iMax - 1), Pair.of(jMax - 2, iMax - 2))
                };
                int sum = 0;
                for (Boolean[] corner : corners) {
                    sum += cornerScore(corner);
                }
                cornerScores[i] = sum;
            }
            ansCornerScore = cornerScores[0] - cornerScores[1];
        }
        else {
            System.err.println("WARNING: Corner score was not calculated");
            ansCornerScore = 0;
        }
        return (selfMovableCount - enemyMovableCount) + k * ansCornerScore;
    }

    private Boolean[] bits(ReversiBoard reversi, Player.ID player, Pair<Integer, Integer>... positions) {
        ReversiBoard.State playerState = reversi.playerState(player);
        Boolean[] bits = new Boolean[positions.length];
        for(int i = 0; i < positions.length; ++i) {
            Pair<Integer, Integer> position = positions[i];
            ReversiBoard.State state = reversi.get(position.getKey(), position.getValue());
            if(playerState.equals(state)) bits[i] = true;
            else bits[i] = false;
        }
        return bits;
    }

    private int cornerScore(Boolean[] corner) {
        List<Boolean> cornerList = Arrays.asList(corner);
        for(Pair<List<Boolean>, Integer> cornerScore : CORNERS_SCORE) {
            if(cornerScore.getKey().equals(cornerList)) {
                return cornerScore.getValue();
            }
        }
        System.err.println("WARNING: Unexpected pattern of corner");
        return 0;
    }
}
