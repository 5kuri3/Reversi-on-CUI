import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;

public class ReversiBoard extends AbstractBoard implements Board {

	public enum State {
		EMPTY(" "),
		BLACK("○"),
		WHITE("●");

		String str;

		State(String str) {
			this.str = str;
		}

		@Override
		public String toString() {
			return str;
		}
	}

	private static final int DEFAULTWIDTH = 8;
	private static final int DEFAULTHEIGHT = 8;

	private int width;
	private int height;
	private List<State> state;
	//現在実際の棋譜上で実現しうる最大の選択肢が30なので30個用意しておく．
  private String[] pos = {"A","B","C","D","E","F","G","H","I","J","K","L","M",
                          "N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
													"a","b","c","d"};

	private Deque<StateHistory> history;

	public ReversiBoard() {
		this(DEFAULTWIDTH, DEFAULTHEIGHT);
	}

	public ReversiBoard(int width, int height) {
		this.width = width;
		this.height = height;
		state = new ArrayList<>();
		for (int i = 0; i < (width + 2) * (height + 2); i++) {
			state.add(State.EMPTY);
		}
		history = new LinkedList<>();

		// TODO:
		state.set(convert(width / 2, height / 2), State.BLACK);
		state.set(convert(width / 2 + 1, height / 2 + 1), State.BLACK);
		state.set(convert(width / 2 + 1, height / 2), State.WHITE);
		state.set(convert(width / 2, height / 2 + 1), State.WHITE);
	}


	@Override
	public boolean isEndOfGame() {
		return !canPut(Player.ID.P1) && !canPut(Player.ID.P2);
	}

	@Override
	public Player.ID winner() {
		int p1 = count(Player.ID.P1);
		int p2 = count(Player.ID.P2);

		if (p1 > p2){
			return Player.ID.P1;
		} else if (p1 < p2){
			return Player.ID.P2;
		} else {
			return Player.ID.NONE;
		}
		//return p1 > p2 ? Player.ID.P1 : Player.ID.P2;
	}

	@Override
	public List<Integer> legalMoves() {
    int count = 0;
		List<Integer> legals = new ArrayList<>();
		for (int i = 1; i <= height; i++) {
			for (int j = 1; j <= width; j++) {
				if (isAvailable(j, i)) {
					legals.add(convert(j, i));
				}
			}
		}
		return legals;
	}

	@Override
	public Object boardState() {
		return state;
	}

	@Override
	public void put(int m) {
		int x = convertToX(m);
		int y = convertToY(m);

		if (isAvailable(x, y)) {
			int[] diff = getDiff();
			State myState = playerState(nextTurn());

			history.push(createHistory());

			for (int i = 0; i < 8; i++) {
				if (isAvailable(nextTurn(), x, y, diff[i])) {
					for (int j = m + diff[i]; contains(j) && state.get(j) != myState; j += diff[i]) {
						state.set(j, myState);
					}
				}
			}

			state.set(m, myState);

			if (canPut(opposite())) {
				flipTurn();
			}

		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void unput() {
		StateHistory history = this.history.pop();
		nextTurn = history.id;
		state = history.state;
	}

	@Override
	public void print(PrintStream out) {
    int count = 0;
    int[] memory = new int[DEFAULTWIDTH*DEFAULTHEIGHT - 4];
    Arrays.fill(memory,0);
    out.println("---------------------------------");
		for (int i = 1; i <= height; i++) {
			out.print("|");
			for (int j = 1; j <= width; j++) {

        if (isAvailable(j,i)){
          out.print(" " + pos[count] + " ");
          memory[count] = convert(j,i);
          count++;
        } else{
							if (state.get(convert(j, i)) == State.EMPTY){
								out.print("\u001b[00;42m" + "   " + "\u001b[00m");
							} else {
							/*if (state.get(convert(j,i)) == State.BLACK){
								out.print("\u001b[00;42m" + " " +
								  "\u001b[00;42m" + "\u001b[00;30m" + state.get(convert(j, i)) + " " + "\u001b[00m");
							}
							else*/ out.print("\u001b[00;44m" + " " + state.get(convert(j, i)) + " " + "\u001b[00m");
							}

				      //out.print(state.get(convert(j, i)));
        }
        out.print("|");

			}
			out.println();
      out.println("---------------------------------");
		}
    out.println();
		if (!canPut(Player.ID.P1) && !canPut(Player.ID.P2)){
			out.println("黒：" + count(Player.ID.P1) + "個，白：" + count(Player.ID.P2) + "個");
		}
    for (int i = 0; i < count; i++){
      //System.out.println(nextTurn());
      //if (nextTurn() == Player.ID.P1){
        out.println(pos[i] +  " : " + memory[i] + "を選択");
      //}
    }
	}



	private boolean isAvailable(int x, int y) {
		return isAvailable(nextTurn(), x, y);
	}

	private boolean isAvailable(Player.ID id, int x, int y) {
		int[] diff = getDiff();
		boolean flag = false;

		for (int i = 0; i < 8 && !flag; i++) {
			flag = isAvailable(id, x, y, diff[i]);
		}

		return flag;
	}

	private boolean isAvailable(Player.ID id, int x, int y, int diff) {
		State myState = playerState(id);

		if (state.get(convert(x, y)) != State.EMPTY
				|| !contains(convert(x, y) + diff)
				|| state.get(convert(x, y) + diff) == myState
				|| state.get(convert(x, y) + diff) == State.EMPTY) {
			return false;
		}

		for (int i = convert(x, y) + diff; contains(i); i += diff) {
			if (state.get(i) == myState) {
				return true;
			} else if (state.get(i) == State.EMPTY){
        break;
      }
		}

		return false;
	}

	private int count() {
		return count(nextTurn());
	}

	private int count(Player.ID id) {
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

	private State playerState(Player.ID id) {
		return id == Player.ID.P1 ? State.BLACK : State.WHITE;
	}

	private boolean canPut(Player.ID id) {
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
	 * @param x 1 <= x <= width
	 * @param y 1 <= y <= height
	 * @return
	 */
	private int convert(int x, int y) {
		return (width + 2) * y + x + 1;
	}

	/**
	 * Returns true if p is in board.
	 * @param p
	 * @return
	 */
	private boolean contains(int p) {
		for (int i = 1; i <= height; i++) {
			for (int j = 1; j <= width; j++) {
				if (convert(j, i) == p) {
					return true;
				}
			}
		}

		return false;
	}

	private int convertToX(int p) {
		for (int i = 1; i <= height; i++) {
			for (int j = 1; j <= width; j++) {
				if (convert(j, i) == p) {
					return j;
				}
			}
		}

		throw new IllegalArgumentException();
	}

	private int convertToY(int p) {
		for (int i = 1; i <= height; i++) {
			for (int j = 1; j <= width; j++) {
				if (convert(j, i) == p) {
					return i;
				}
			}
		}

		throw new IllegalArgumentException();
	}

	private int[] getDiff() {
		return new int[]{
				- (width + 2) - 1,	- (width + 2), - (width + 2) + 1,
				-1,									1,
				(width + 2) - 1, (width + 2), (width + 2) + 1
		};
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
