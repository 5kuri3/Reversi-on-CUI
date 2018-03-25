import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;

public class ReversiBoard extends AbstractBoard implements Board {

    public enum State {
        EMPTY(" ", " "), BLACK("●", "○"), WHITE("○", "●");
        private final String str;
        private final String istr;
        State(String str, String istr) {
            this.str = str;
            this.istr = istr;
        }
        public String piece() {
            return str;
        }
        public String inversePiece() {
            return istr;
        }
        public String piece(boolean inverse) {
            if(inverse) return inversePiece();
            else return piece();
        }
    }
    
    // 現在実際の棋譜上で実現しうる最大の選択肢が30なので30個用意しておく．
    public static final String[] KEYS = new String[] {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4",
            "5", "6", "7", "8", "9", "0"
    };
    public static final String UNKNOWN_KEY = "#";
    public static final String VOID_KEY = "\\";
    private static final int DEFAULTWIDTH = 8;
    private static final int DEFAULTHEIGHT = 8;

    private final int width;
    private final int height;
    private final boolean inverse;
    private List<State> state;
    private Deque<StateHistory> history;
    private int lastPutPos = M_VOID;

    public ReversiBoard() {
        this(false);
    }
    
    public ReversiBoard(boolean inverse) {
        this(DEFAULTWIDTH, DEFAULTHEIGHT, inverse);
    }

    public ReversiBoard(int width, int height, boolean inverse) {
        this.width = width;
        this.height = height;
        this.inverse = inverse;
        state = new ArrayList<>();
        for (int i = 0; i < (width + 2) * (height + 2); i++) {
            state.add(State.EMPTY);
        }
        history = new LinkedList<>();

        // TODO:
        state.set(convert(width / 2, height / 2), State.WHITE);
        state.set(convert(width / 2 + 1, height / 2 + 1), State.WHITE);
        state.set(convert(width / 2 + 1, height / 2), State.BLACK);
        state.set(convert(width / 2, height / 2 + 1), State.BLACK);
    }
    
    private ReversiBoard(Player.ID nextTurn, int width, int height, boolean inverse, List<State> state, Deque<StateHistory> history) {
        super(nextTurn);
        this.width = width;
        this.height = height;
        this.inverse = inverse;
        this.state = state;
        this.history = history;
    }
    
    @Override
    public ReversiBoard clone() {
        return new ReversiBoard(this.nextTurn, this.width, this.height, this.inverse, new ArrayList<>(this.state), new LinkedList<>(this.history));
    }

    @Override
    public boolean isEndOfGame() {
        return !canPut(Player.ID.P1) && !canPut(Player.ID.P2);
    }

    @Override
    public Player.ID winner() {
        int p1 = count(Player.ID.P1);
        int p2 = count(Player.ID.P2);

        if (p1 > p2) {
            return Player.ID.P1;
        }
        else if (p1 < p2) {
            return Player.ID.P2;
        }
        else {
            return Player.ID.NONE;
        }
        // return p1 > p2 ? Player.ID.P1 : Player.ID.P2;
    }

    @Override
    public List<Integer> legalMoves() {
        List<Integer> legalMoves = new ArrayList<>();
        for (int i = 1; i <= height; i++) {
            for (int j = 1; j <= width; j++) {
                if (isAvailable(j, i)) {
                    int m = convert(j, i);
                    legalMoves.add(m);
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public int keyToMove(String key) {
        List<Integer> legalMoves = legalMoves();
        int index = -1;
        for(int i = 0; i < KEYS.length; ++i) {
            if(KEYS[i].equalsIgnoreCase(key)) {
                index = i;
                break;
            }
        }
        if(0 <= index && index < legalMoves.size()) {
            int m = legalMoves.get(index);
            return m;
        }
        else {
            return M_VOID;
        }
    }
    
    public String moveToKey(int m) {
        List<Integer> legalMoves = legalMoves();
        int index = legalMoves.indexOf(m);
        if(index < 0) {
            return VOID_KEY;
        }
        else if(KEYS.length <= index) {
            return UNKNOWN_KEY;
        }
        else {
            return KEYS[index];
        }
    }

    @Override
    public Object boardState() {
        return state;
    }

    @Override
    public void put(int m) {
        put(m, false);
    }

    public void put(int m, boolean forceFlipPlayer) {
        history.push(createHistory());
        if(m != M_VOID) {
            int x = convertToX(m);
            int y = convertToY(m);
            if (isAvailable(x, y)) {
                int[] diff = getDiff();
                State myState = playerState(nextTurn());
                for (int i = 0; i < 8; i++) {
                    if (isAvailable(nextTurn(), x, y, diff[i])) {
                        for (int j = m + diff[i]; contains(j) && state.get(j) != myState; j += diff[i]) {
                            state.set(j, myState);
                        }
                    }
                }
                state.set(m, myState);
                this.lastPutPos = m;
            } else {
                throw new IllegalArgumentException();
            }
        }
        if (forceFlipPlayer || canPut(opposite())) {
            flipTurn();
        }
    }

    @Override
    public void unput() {
        StateHistory history = this.history.pop();
        nextTurn = history.id;
        state = history.state;
    }
    
    @Override
    public int unputCapacity() {
        return history.size();
    }

    @Override
    public void print(PrintStream out) {
        List<Integer> legalMoves = legalMoves();
        out.println("--a---b---c---d---e---f---g---h--");
        for (int i = 1; i <= height; i++) {
            out.print(Integer.valueOf(i).toString());
            for (int j = 1; j <= width; j++) {
                final int m = convert(j, i);
                if (legalMoves.contains(m)) {
                    String key = moveToKey(m);
                    out.print(" " + key + " ");
                }
                else {
                    if (state.get(convert(j, i)) == State.EMPTY) {
                        out.print("\u001b[00;42m" + "   " + "\u001b[00m");
                    }
                    else {
                        /*
                         * if (state.get(convert(j,i)) == State.BLACK){ out.print("\u001b[00;42m" + " "
                         * + "\u001b[00;42m" + "\u001b[00;30m" + state.get(convert(j, i)) + " " +
                         * "\u001b[00m"); } else
                         */
                        if (m == lastPutPos) {
                            out.print("\u001b[00;45m" + " " + state.get(m).piece(inverse) + " " + "\u001b[00m");
                        } else {
                            out.print("\u001b[00;44m" + " " + state.get(m).piece(inverse) + " " + "\u001b[00m");
                        }
                    }
                    // out.print(state.get(convert(j, i)));
                }
                out.print("|");
            }
            out.println();
            if(i != height) {
                out.println("---------------------------------");
            }
            else {
                out.println(String.format("--------------------------------- %s%d %s%d", drawPiece(Player.ID.P1), count(Player.ID.P1), drawPiece(Player.ID.P2), count(Player.ID.P2)));
            }
        }
        out.println();
        if (!canPut(Player.ID.P1) && !canPut(Player.ID.P2)) {
            out.println("黒：" + count(Player.ID.P1) + "個，白：" + count(Player.ID.P2) + "個");
        }
        for (int m : legalMoves) {
            // System.out.println(nextTurn());
            // if (nextTurn() == Player.ID.P1){
            String key = moveToKey(m);
            out.println(String.format("%s : %s [%d] を選択", key, explain(m), m));
            // }
        }
    }
    
    @Override
    public String explain(int m) {
        int x = convertToX(m);
        int y = convertToY(m);
        return String.format("(%c, %d)", 'a' + (x-1), y);
    }
    
    @Override
    public Object drawPiece(Player.ID id) {
        if(Player.ID.P1.equals(id)) return State.BLACK.piece(inverse);
        else if(Player.ID.P2.equals(id)) return State.WHITE.piece(inverse);
        else return null;
    }

    int getWidthMin() {
        return 1;
    }

    int getWidthMax() {
        return width;
    }

    int getHeightMin() {
        return 1;
    }

    int getHeightMax() {
        return height;
    }

    int getBoardSize() {
        return width * height;
    }

    State get(int x, int y) {
        return get(convert(x, y));
    }

    State get(int m) {
        return state.get(m);
    }

    boolean isAvailable(int x, int y) {
        return isAvailable(nextTurn(), x, y);
    }

    boolean isAvailable(Player.ID id, int x, int y) {
        int[] diff = getDiff();
        boolean flag = false;

        for (int i = 0; i < 8 && !flag; i++) {
            flag = isAvailable(id, x, y, diff[i]);
        }

        return flag;
    }

    boolean isAvailable(Player.ID id, int x, int y, int diff) {
        State myState = playerState(id);

        if (state.get(convert(x, y)) != State.EMPTY || !contains(convert(x, y) + diff)
                || state.get(convert(x, y) + diff) == myState || state.get(convert(x, y) + diff) == State.EMPTY) {
            return false;
        }

        for (int i = convert(x, y) + diff; contains(i); i += diff) {
            if (state.get(i) == myState) {
                return true;
            }
            else if (state.get(i) == State.EMPTY) {
                break;
            }
        }

        return false;
    }

    int count() {
        return count(nextTurn());
    }

    int count(Player.ID id) {
        State myState = playerState(id);

        int count = 0;
        for (int i = 1; i <= height; i++) {
            for (int j = 1; j <= width; j++) {
                if (state.get(convert(j, i)) == myState) {
                    count++;
                }
            }
        }

        return count;
    }

    int countAll() {
        return count(Player.ID.P1) + count(Player.ID.P2);
    }

    State playerState(Player.ID id) {
        return id == Player.ID.P1 ? State.BLACK : State.WHITE;
    }

    boolean canPut(Player.ID id) {
        for (int i = 1; i <= height; i++) {
            for (int j = 1; j <= width; j++) {
                if (isAvailable(id, j, i)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Convert points.
     * 
     * @param x
     *            1 <= x <= width
     * @param y
     *            1 <= y <= height
     * @return
     */
    int convert(int x, int y) {
        return (width + 2) * y + x + 1;
    }

    /**
     * Returns true if p is in board.
     * 
     * @param p
     * @return
     */
    boolean contains(int p) {
        for (int i = 1; i <= height; i++) {
            for (int j = 1; j <= width; j++) {
                if (convert(j, i) == p) {
                    return true;
                }
            }
        }

        return false;
    }

    int convertToX(int p) {
        for (int i = 1; i <= height; i++) {
            for (int j = 1; j <= width; j++) {
                if (convert(j, i) == p) {
                    return j;
                }
            }
        }

        throw new IllegalArgumentException();
    }

    int convertToY(int p) {
        for (int i = 1; i <= height; i++) {
            for (int j = 1; j <= width; j++) {
                if (convert(j, i) == p) {
                    return i;
                }
            }
        }

        throw new IllegalArgumentException();
    }

    int[] getDiff() {
        return new int[] { -(width + 2) - 1, -(width + 2), -(width + 2) + 1, -1, 1, (width + 2) - 1, (width + 2),
                (width + 2) + 1 };
    }

    private StateHistory createHistory() {
        StateHistory history = new StateHistory();
        history.id = nextTurn();
        history.state = new ArrayList<>();
        for (int i = 0; i < state.size(); i++) {
            history.state.add(state.get(i));
        }
        return history;
    }

    private static class StateHistory {
        Player.ID id;
        List<State> state;
    }
}
