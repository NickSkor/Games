package game_2048;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Controller extends KeyAdapter {
	private Model model;
	private View view;
	private static int WINNING_TILE = 2048;

	public Controller(Model model) {
		this.model = model;
		view = new View(this);
	}

	public Tile[][] getGameTiles() {
		return model.getGameTiles();
	}

	public View getView() {
		return view;
	}

	public int getScore() {
		return model.getScore();
	}

	public void resetGame() {
		model.setScore(0);
		view.isGameLost = false;
		view.isGameWon = false;
		model.resetGameTiles();
	}

	@Override
	public void keyPressed(KeyEvent keyEvent) {
		if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE)
			resetGame();

		if (keyEvent.getKeyCode() == KeyEvent.VK_Z)
			model.rollback();

		if (!model.canMove())
			view.isGameLost = true;

		if (!view.isGameLost && !view.isGameWon)
			switch (keyEvent.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					model.left();
					break;
				case KeyEvent.VK_RIGHT:
					model.right();
					break;
				case KeyEvent.VK_UP:
					model.up();
					break;
				case KeyEvent.VK_DOWN:
					model.down();
					break;
				case KeyEvent.VK_R:
					model.randomMove();
					break;
				case KeyEvent.VK_A:
					model.autoMove();
					break;
			}
		if (model.maxTile==WINNING_TILE)
			view.isGameWon=true;

		view.repaint();
	}

}
