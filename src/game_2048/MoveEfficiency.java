package game_2048;

public class MoveEfficiency implements Comparable<MoveEfficiency> {
	private int numberOfEmptyTiles;
	private int score;
	private Move move;

	public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
		this.numberOfEmptyTiles = numberOfEmptyTiles;
		this.score = score;
		this.move = move;
	}

	public Move getMove() {
		return move;
	}

	@Override
	public int compareTo(MoveEfficiency o) {
		if (this.numberOfEmptyTiles != o.numberOfEmptyTiles)
			return new Integer(this.numberOfEmptyTiles).compareTo(o.numberOfEmptyTiles);
		else if (this.score!=o.score)
			return new Integer(this.score).compareTo(o.score);
		else
			return 0;
	}
}
