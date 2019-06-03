package game_2048;

import java.util.*;

public class Model {
	private static final int FIELD_WIDTH = 4;
	private Tile[][] gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
	protected int score = 0;
	protected int maxTile = 0;

	private Stack previousStates = new Stack();
	private Stack previousScores = new Stack();
	private boolean isSaveNeeded = true;

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getMaxTile() {
		return maxTile;
	}

	public void setMaxTile(int maxTile) {
		this.maxTile = maxTile;
	}

	public Tile[][] getGameTiles() {
		return gameTiles;
	}

	private void saveState(Tile[][] arg) {
		Tile[][] array = new Tile[FIELD_WIDTH][FIELD_WIDTH];

		for (int i = 0; i < gameTiles.length; i++) {
			for (int j = 0; j < gameTiles[i].length; j++) {
				array[i][j] = new Tile();
			}
		}


		for (int i = 0; i < gameTiles.length; i++) {
			for (int j = 0; j < gameTiles[i].length; j++) {
				array[i][j].value = gameTiles[i][j].value;
			}
		}

		previousStates.push(array);
		previousScores.push(score);
		isSaveNeeded = false;
	}

	public void rollback() {
		if (!previousStates.empty() && !previousScores.empty()) {
			gameTiles = (Tile[][]) previousStates.pop();
			setScore((int) previousScores.pop());
		}
	}

	public Model() {
		resetGameTiles();
	}

	private List<Tile> getEmptyTiles() {
		List<Tile> list = new ArrayList<>();

		for (int i = 0; i < gameTiles.length; i++) {
			for (int j = 0; j < gameTiles[i].length; j++) {
				if (gameTiles[i][j].isEmpty())
					list.add(gameTiles[i][j]);
			}
		}

		return list;
	}

	private void addTile() {
		List<Tile> list = getEmptyTiles();
		if (!list.isEmpty()) {
			Tile tile = list.get((int) (Math.random() * (list.size())));
			tile.value = Math.random() < 0.9 ? 2 : 4;
		}
	}

	protected void resetGameTiles() {
		setScore(0);
		setMaxTile(0);
		for (int i = 0; i < gameTiles.length; i++) {
			for (int j = 0; j < gameTiles[i].length; j++) {
				gameTiles[i][j] = new Tile();
			}
		}
		addTile();
		addTile();

	}

	private boolean compressTiles(Tile[] tiles) {
		int count = 0;
		boolean result = false;
		for (int i = 0; i < tiles.length - 1; i++) {
			if (tiles[i].value == 0 && tiles[i].value < tiles[i + 1].value) {
				count++;
				result = true;
			}
		}

		while (count > 0) {
			for (int i = 0; i < tiles.length - 1; i++) {
				if (tiles[i].value == 0 && tiles[i].value < tiles[i + 1].value) {
					tiles[i].value = tiles[i].value + tiles[i + 1].value;
					tiles[i + 1].value = tiles[i].value - tiles[i + 1].value;
					tiles[i].value = tiles[i].value - tiles[i + 1].value;
				}
			}

			count = 0;
			for (int i = 0; i < tiles.length - 1; i++) {
				if (tiles[i].value == 0 && tiles[i].value < tiles[i + 1].value)
					count++;
			}
		}
		return result;
	}


	private boolean mergeTiles(Tile[] tiles) {
		boolean result = false;
		for (int i = 0; i < tiles.length - 1; i++) {
			if (tiles[i].value != 0 && tiles[i].value == tiles[i + 1].value) {
				tiles[i].value = 2 * tiles[i].value;
				setScore(getScore()+tiles[i].value);
				tiles[i + 1].value = 0;
				result = true;
			}
		}
		compressTiles(tiles);

		int max = 0;
		for (int i = 0; i < tiles.length - 1; i++)
			if (tiles[i].value > max)
				max = tiles[i].value;
		if (max > getMaxTile())
			setMaxTile(max);

		return result;
	}

	public boolean canMove() {
		if (!getEmptyTiles().isEmpty()) return true;

		for (int i = 0; i < gameTiles.length; i++) {
			for (int j = 0; j < gameTiles[i].length; j++) {
				if (i - 1 >= 0 && gameTiles[i][j].value == gameTiles[i - 1][j].value)
					return true;
				if (j - 1 >= 0 && gameTiles[i][j].value == gameTiles[i][j - 1].value)
					return true;
				if ((i + 1 <= FIELD_WIDTH - 1) && gameTiles[i][j].value == gameTiles[i + 1][j].value)
					return true;
				if ((j + 1 <= FIELD_WIDTH - 1) && gameTiles[i][j].value == gameTiles[i][j + 1].value)
					return true;
			}
		}

		return false;
	}

	public void left() {
		boolean compress = false;
		boolean merge = false;

		if (isSaveNeeded)
			saveState(gameTiles);

		for (int i = 0; i < gameTiles.length; i++) {
			compress = compressTiles(gameTiles[i]);
			merge = mergeTiles(gameTiles[i]);
		}
		if (compress || merge)
			addTile();

		isSaveNeeded = true;
	}

	public void right() {
		saveState(gameTiles);
		rotateClockwise();
		rotateClockwise();
		left();
		rotateClockwise();
		rotateClockwise();
	}

	public void randomMove() {
		int n = (int) (Math.random() * 4);
		switch (n) {
			case 0:
				left();
				break;
			case 1:
				right();
				break;
			case 2:
				up();
				break;
			case 3:
				down();
				break;
		}
	}

	public boolean hasBoardChanged() {
		Tile[][] arr = (Tile[][]) previousStates.peek();

		for (int i = 0; i < gameTiles.length; i++) {
			for (int j = 0; j < gameTiles[i].length; j++)
				if (arr[i][j].value != gameTiles[i][j].value)
					return true;
		}

		return false;
	}

	public MoveEfficiency getMoveEfficiency(Move move) {
		MoveEfficiency moveEfficiency;
		move.move();

		if (hasBoardChanged())
			moveEfficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);
		else
			moveEfficiency = new MoveEfficiency(-1, 0, move);
		rollback();

		return moveEfficiency;

	}

	public void autoMove(){
		PriorityQueue<MoveEfficiency> queue = new PriorityQueue<>(4, Collections.reverseOrder());

		queue.add(getMoveEfficiency(this::left));
		queue.add(getMoveEfficiency(this::right));
		queue.add(getMoveEfficiency(this::up));
		queue.add(getMoveEfficiency(this::down));

		queue.poll().getMove().move();
	}

	public void up() {
		saveState(gameTiles);
		rotateClockwise();
		rotateClockwise();
		rotateClockwise();
		left();
		rotateClockwise();
	}

	public void down() {
		saveState(gameTiles);
		rotateClockwise();
		left();
		rotateClockwise();
		rotateClockwise();
		rotateClockwise();
	}

	private void rotateClockwise() {
		Tile[][] rezult = new Tile[FIELD_WIDTH][FIELD_WIDTH];
		for (int i = 0; i < gameTiles.length; i++) {
			for (int j = 0; j < gameTiles[i].length; j++) {
				rezult[j][FIELD_WIDTH - i - 1] = gameTiles[i][j];
			}
		}
		gameTiles = rezult;
	}

}
