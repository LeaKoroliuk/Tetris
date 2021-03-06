package com.koroliuk.tetris;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Tetris extends Application {

	private Stage primaryStage;
	public static final int MOVE = 25;
	public static final int SIZE = 25;
	public static int XMAX = SIZE * 12;
	public static int YMAX = SIZE * 24;
	public static int[][] MESH = new int[XMAX / SIZE][YMAX / SIZE];
	private static Pane group = new Pane();
	private static Form object;
	private static Scene scene = new Scene(group, XMAX + 200, YMAX);
	public static int score = 0;
	private static int top = 0;
	private volatile boolean game = true;
	private static Form nextObj = Controller.makeRect();
	private TextField nameOfPlayer = new TextField("Enter Your Name");

	private Media media;
	private MediaPlayer player;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		primaryStage.setResizable(false);
		primaryStage.setTitle("T E T R I S");
//		primaryStage.setScene(gameScene());
		primaryStage.setScene(mainScene());
		startMusic();
		primaryStage.show();
	}

	private Scene mainScene() {
		GridPane root = new GridPane();
		HBox btnBox = new HBox();
		btnBox.setAlignment(Pos.BOTTOM_CENTER);
		Button playBtn = new Button("P L A Y");
		playBtn.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event arg0) {
				primaryStage.setScene(gameScene());
				primaryStage.show();
			}
		});
		nameOfPlayer.setId("nameOfPlayer");
		nameOfPlayer.setPrefHeight(15);
		nameOfPlayer.setPrefWidth(150);
		nameOfPlayer.setCenterShape(true);
		btnBox.getChildren().addAll(playBtn);
		root.setVgap(3);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(150, 5, 5, 5));
		root.add(nameOfPlayer, 3, 0);
		root.add(btnBox, 3, 5);
		Scene sc = new Scene(root, XMAX + 200, YMAX);
		sc.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		return sc;
	}

	private Scene gameScene() {
		for (int[] a : MESH) {
			Arrays.fill(a, 0);
		}
		Text name = new Text("Player: ");
		name.setStyle("-fx-font: 20 arial;");
		name.setY(50);
		name.setX(XMAX + 5);
		name.setFill(Color.GREEN);

		Line line = new Line(XMAX, 0, XMAX, YMAX);
		Text scoretext = new Text("Score: ");
		scoretext.setStyle("-fx-font: 20 arial;");
		scoretext.setY(100);
		scoretext.setX(XMAX + 5);

		group.getChildren().addAll(scoretext, line, name);

		Form a = nextObj;
		group.getChildren().addAll(a.a, a.b, a.c, a.d);
		moveOnKeyPress(a);
		object = a;
		nextObj = Controller.makeRect();

		Timer fall = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				Platform.runLater(new Runnable() {
					public void run() {
						if (object.a.getY() == 0 || object.b.getY() == 0 || object.c.getY() == 0
								|| object.d.getY() == 0) {
							top++;
						} else {
							top = 0;
						}
						if (top == 2) {
							// GAME OVER
							Text over = new Text("GAME OVER");
							over.setFill(Color.RED);
							over.setStyle("-fx-font: 80 arial;");
							over.setY(250);
							over.setX(10);
							group.getChildren().add(over);
							game = false;
						}
						// Exit
						if (top == 15) {
							System.exit(0);
						}

						if (game) {
							moveDown(object);
							String tmp = nameOfPlayer.getText();
							if (tmp.equals("Enter Your Name")) {
								tmp = "Incognito";
							}
							scoretext.setText("Score: " + Integer.toString(score));
							name.setText("Player: " + tmp);
						}
					}
				});
			}
		};
		fall.schedule(task, 0, 300);
		return scene;
	}

	private void moveOnKeyPress(Form form) {
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
				case RIGHT:
					Controller.moveRight(form);
					break;
				case DOWN:
					moveDown(form);
					score++;
					break;
				case LEFT:
					Controller.moveLeft(form);
					break;
				case UP:
					moveTurn(form);
					break;
				}
			}
		});
	}

	private void moveTurn(Form form) {
		int f = form.form;
		Rectangle a = form.a;
		Rectangle b = form.b;
		Rectangle c = form.c;
		Rectangle d = form.d;
		switch (form.getName()) {
		case "j":
			if (f == 1 && cB(a, 1, -1) && cB(c, -1, -1) && cB(d, -2, -2)) {
				moveRight(form.a);
				moveDown(form.a);
				moveDown(form.c);
				moveLeft(form.c);
				moveDown(form.d);
				moveDown(form.d);
				moveLeft(form.d);
				moveLeft(form.d);
				form.changeForm();
				break;
			}
			if (f == 2 && cB(a, -1, -1) && cB(c, -1, 1) && cB(d, -2, 2)) {
				moveDown(form.a);
				moveLeft(form.a);
				moveLeft(form.c);
				moveUp(form.c);
				moveLeft(form.d);
				moveLeft(form.d);
				moveUp(form.d);
				moveUp(form.d);
				form.changeForm();
				break;
			}
			if (f == 3 && cB(a, -1, 1) && cB(c, 1, 1) && cB(d, 2, 2)) {
				moveLeft(form.a);
				moveUp(form.a);
				moveUp(form.c);
				moveRight(form.c);
				moveUp(form.d);
				moveUp(form.d);
				moveRight(form.d);
				moveRight(form.d);
				form.changeForm();
				break;
			}
			if (f == 4 && cB(a, 1, 1) && cB(c, 1, -1) && cB(d, 2, -2)) {
				moveUp(form.a);
				moveRight(form.a);
				moveRight(form.c);
				moveDown(form.c);
				moveRight(form.d);
				moveRight(form.d);
				moveDown(form.d);
				moveDown(form.d);
				form.changeForm();
				break;
			}
			break;
		case "l":
			if (f == 1 && cB(a, 1, -1) && cB(c, 1, 1) && cB(b, 2, 2)) {
				moveRight(form.a);
				moveDown(form.a);
				moveUp(form.c);
				moveRight(form.c);
				moveUp(form.b);
				moveUp(form.b);
				moveRight(form.b);
				moveRight(form.b);
				form.changeForm();
				break;
			}
			if (f == 2 && cB(a, -1, -1) && cB(b, 2, -2) && cB(c, 1, -1)) {
				moveDown(form.a);
				moveLeft(form.a);
				moveRight(form.b);
				moveRight(form.b);
				moveDown(form.b);
				moveDown(form.b);
				moveRight(form.c);
				moveDown(form.c);
				form.changeForm();
				break;
			}
			if (f == 3 && cB(a, -1, 1) && cB(c, -1, -1) && cB(b, -2, -2)) {
				moveLeft(form.a);
				moveUp(form.a);
				moveDown(form.c);
				moveLeft(form.c);
				moveDown(form.b);
				moveDown(form.b);
				moveLeft(form.b);
				moveLeft(form.b);
				form.changeForm();
				break;
			}
			if (f == 4 && cB(a, 1, 1) && cB(b, -2, 2) && cB(c, -1, 1)) {
				moveUp(form.a);
				moveRight(form.a);
				moveLeft(form.b);
				moveLeft(form.b);
				moveUp(form.b);
				moveUp(form.b);
				moveLeft(form.c);
				moveUp(form.c);
				form.changeForm();
				break;
			}
			break;
		case "o":
			break;
		case "s":
			if (f == 1 && cB(a, -1, -1) && cB(c, -1, 1) && cB(d, 0, 2)) {
				moveDown(form.a);
				moveLeft(form.a);
				moveLeft(form.c);
				moveUp(form.c);
				moveUp(form.d);
				moveUp(form.d);
				form.changeForm();
				break;
			}
			if (f == 2 && cB(a, 1, 1) && cB(c, 1, -1) && cB(d, 0, -2)) {
				moveUp(form.a);
				moveRight(form.a);
				moveRight(form.c);
				moveDown(form.c);
				moveDown(form.d);
				moveDown(form.d);
				form.changeForm();
				break;
			}
			if (f == 3 && cB(a, -1, -1) && cB(c, -1, 1) && cB(d, 0, 2)) {
				moveDown(form.a);
				moveLeft(form.a);
				moveLeft(form.c);
				moveUp(form.c);
				moveUp(form.d);
				moveUp(form.d);
				form.changeForm();
				break;
			}
			if (f == 4 && cB(a, 1, 1) && cB(c, 1, -1) && cB(d, 0, -2)) {
				moveUp(form.a);
				moveRight(form.a);
				moveRight(form.c);
				moveDown(form.c);
				moveDown(form.d);
				moveDown(form.d);
				form.changeForm();
				break;
			}
			break;
		case "t":
			if (f == 1 && cB(a, 1, 1) && cB(d, -1, -1) && cB(c, -1, 1)) {
				moveUp(form.a);
				moveRight(form.a);
				moveDown(form.d);
				moveLeft(form.d);
				moveLeft(form.c);
				moveUp(form.c);
				form.changeForm();
				break;
			}
			if (f == 2 && cB(a, 1, -1) && cB(d, -1, 1) && cB(c, 1, 1)) {
				moveRight(form.a);
				moveDown(form.a);
				moveLeft(form.d);
				moveUp(form.d);
				moveUp(form.c);
				moveRight(form.c);
				form.changeForm();
				break;
			}
			if (f == 3 && cB(a, -1, -1) && cB(d, 1, 1) && cB(c, 1, -1)) {
				moveDown(form.a);
				moveLeft(form.a);
				moveUp(form.d);
				moveRight(form.d);
				moveRight(form.c);
				moveDown(form.c);
				form.changeForm();
				break;
			}
			if (f == 4 && cB(a, -1, 1) && cB(d, 1, -1) && cB(c, -1, -1)) {
				moveLeft(form.a);
				moveUp(form.a);
				moveRight(form.d);
				moveDown(form.d);
				moveDown(form.c);
				moveLeft(form.c);
				form.changeForm();
				break;
			}
			break;
		case "z":
			if (f == 1 && cB(b, 1, 1) && cB(c, -1, 1) && cB(d, -2, 0)) {
				moveUp(form.b);
				moveRight(form.b);
				moveLeft(form.c);
				moveUp(form.c);
				moveLeft(form.d);
				moveLeft(form.d);
				form.changeForm();
				break;
			}
			if (f == 2 && cB(b, -1, -1) && cB(c, 1, -1) && cB(d, 2, 0)) {
				moveDown(form.b);
				moveLeft(form.b);
				moveRight(form.c);
				moveDown(form.c);
				moveRight(form.d);
				moveRight(form.d);
				form.changeForm();
				break;
			}
			if (f == 3 && cB(b, 1, 1) && cB(c, -1, 1) && cB(d, -2, 0)) {
				moveUp(form.b);
				moveRight(form.b);
				moveLeft(form.c);
				moveUp(form.c);
				moveLeft(form.d);
				moveLeft(form.d);
				form.changeForm();
				break;
			}
			if (f == 4 && cB(b, -1, -1) && cB(c, 1, -1) && cB(d, 2, 0)) {
				moveDown(form.b);
				moveLeft(form.b);
				moveRight(form.c);
				moveDown(form.c);
				moveRight(form.d);
				moveRight(form.d);
				form.changeForm();
				break;
			}
			break;
		case "i":
			if (f == 1 && cB(a, 2, 2) && cB(b, 1, 1) && cB(d, -1, -1)) {
				moveUp(form.a);
				moveUp(form.a);
				moveRight(form.a);
				moveRight(form.a);
				moveUp(form.b);
				moveRight(form.b);
				moveDown(form.d);
				moveLeft(form.d);
				form.changeForm();
				break;
			}
			if (f == 2 && cB(a, -2, -2) && cB(b, -1, -1) && cB(d, 1, 1)) {
				moveDown(form.a);
				moveDown(form.a);
				moveLeft(form.a);
				moveLeft(form.a);
				moveDown(form.b);
				moveLeft(form.b);
				moveUp(form.d);
				moveRight(form.d);
				form.changeForm();
				break;
			}
			if (f == 3 && cB(a, 2, 2) && cB(b, 1, 1) && cB(d, -1, -1)) {
				moveUp(form.a);
				moveUp(form.a);
				moveRight(form.a);
				moveRight(form.a);
				moveUp(form.b);
				moveRight(form.b);
				moveDown(form.d);
				moveLeft(form.d);
				form.changeForm();
				break;
			}
			if (f == 4 && cB(a, -2, -2) && cB(b, -1, -1) && cB(d, 1, 1)) {
				moveDown(form.a);
				moveDown(form.a);
				moveLeft(form.a);
				moveLeft(form.a);
				moveDown(form.b);
				moveLeft(form.b);
				moveUp(form.d);
				moveRight(form.d);
				form.changeForm();
				break;
			}
			break;
		}
	}

	private void removeRows(Pane pane) {
		List<Node> rects = new ArrayList<Node>();
		List<Integer> lines = new ArrayList<Integer>();
		List<Node> newrects = new ArrayList<Node>();
		int full = 0;
		for (int i = 0; i < MESH[0].length; i++) {
			for (int j = 0; j < MESH.length; j++) {
				if (MESH[j][i] == 1)
					full++;
			}
			if (full == MESH.length)
				lines.add(i + lines.size());
			full = 0;
		}
		if (lines.size() > 0)
			do {
				for (Node node : pane.getChildren()) {
					if (node instanceof Rectangle)
						rects.add(node);
				}
				score += 50;

				for (Node node : rects) {
					Rectangle a = (Rectangle) node;
					if (a.getY() == lines.get(0) * SIZE) {
						MESH[(int) a.getX() / SIZE][(int) a.getY() / SIZE] = 0;
						pane.getChildren().remove(node);
					} else
						newrects.add(node);
				}

				for (Node node : newrects) {
					Rectangle a = (Rectangle) node;
					if (a.getY() < lines.get(0) * SIZE) {
						MESH[(int) a.getX() / SIZE][(int) a.getY() / SIZE] = 0;
						a.setY(a.getY() + SIZE);
					}
				}
				lines.remove(0);
				rects.clear();
				newrects.clear();
				for (Node node : pane.getChildren()) {
					if (node instanceof Rectangle)
						rects.add(node);
				}
				for (Node node : rects) {
					Rectangle a = (Rectangle) node;
					try {
						MESH[(int) a.getX() / SIZE][(int) a.getY() / SIZE] = 1;
					} catch (ArrayIndexOutOfBoundsException e) {
					}
				}
				rects.clear();
			} while (lines.size() > 0);
	}

	private void moveDown(Rectangle rect) {
		if (rect.getY() + MOVE < YMAX)
			rect.setY(rect.getY() + MOVE);

	}

	private void moveRight(Rectangle rect) {
		if (rect.getX() + MOVE <= XMAX - SIZE)
			rect.setX(rect.getX() + MOVE);
	}

	private void moveLeft(Rectangle rect) {
		if (rect.getX() - MOVE >= 0)
			rect.setX(rect.getX() - MOVE);
	}

	private void moveUp(Rectangle rect) {
		if (rect.getY() - MOVE > 0)
			rect.setY(rect.getY() - MOVE);
	}

	private void moveDown(Form form) {
		if (form.a.getY() == YMAX - SIZE || form.b.getY() == YMAX - SIZE || form.c.getY() == YMAX - SIZE
				|| form.d.getY() == YMAX - SIZE || moveA(form) || moveB(form) || moveC(form) || moveD(form)) {
			MESH[(int) form.a.getX() / SIZE][(int) form.a.getY() / SIZE] = 1;
			MESH[(int) form.b.getX() / SIZE][(int) form.b.getY() / SIZE] = 1;
			MESH[(int) form.c.getX() / SIZE][(int) form.c.getY() / SIZE] = 1;
			MESH[(int) form.d.getX() / SIZE][(int) form.d.getY() / SIZE] = 1;
			removeRows(group);

			Form a = nextObj;
			nextObj = Controller.makeRect();
			object = a;
			group.getChildren().addAll(a.a, a.b, a.c, a.d);
			moveOnKeyPress(a);
		}

		if (form.a.getY() + MOVE < YMAX && form.b.getY() + MOVE < YMAX && form.c.getY() + MOVE < YMAX
				&& form.d.getY() + MOVE < YMAX) {
			int movea = MESH[(int) form.a.getX() / SIZE][((int) form.a.getY() / SIZE) + 1];
			int moveb = MESH[(int) form.b.getX() / SIZE][((int) form.b.getY() / SIZE) + 1];
			int movec = MESH[(int) form.c.getX() / SIZE][((int) form.c.getY() / SIZE) + 1];
			int moved = MESH[(int) form.d.getX() / SIZE][((int) form.d.getY() / SIZE) + 1];
			if (movea == 0 && movea == moveb && moveb == movec && movec == moved) {
				form.a.setY(form.a.getY() + MOVE);
				form.b.setY(form.b.getY() + MOVE);
				form.c.setY(form.c.getY() + MOVE);
				form.d.setY(form.d.getY() + MOVE);
			}
		}
	}

	private boolean moveA(Form form) {
		return (MESH[(int) form.a.getX() / SIZE][((int) form.a.getY() / SIZE) + 1] == 1);
	}

	private boolean moveB(Form form) {
		return (MESH[(int) form.b.getX() / SIZE][((int) form.b.getY() / SIZE) + 1] == 1);
	}

	private boolean moveC(Form form) {
		return (MESH[(int) form.c.getX() / SIZE][((int) form.c.getY() / SIZE) + 1] == 1);
	}

	private boolean moveD(Form form) {
		return (MESH[(int) form.d.getX() / SIZE][((int) form.d.getY() / SIZE) + 1] == 1);
	}

	private boolean cB(Rectangle rect, int x, int y) {
		boolean xb = false;
		boolean yb = false;
		if (x >= 0)
			xb = rect.getX() + x * MOVE <= XMAX - SIZE;
		if (x < 0)
			xb = rect.getX() + x * MOVE >= 0;
		if (y >= 0)
			yb = rect.getY() - y * MOVE > 0;
		if (y < 0)
			yb = rect.getY() + y * MOVE < YMAX;
		return xb && yb && MESH[((int) rect.getX() / SIZE) + x][((int) rect.getY() / SIZE) - y] == 0;
	}

	private void startMusic() {
		media = new Media(this.getClass().getResource("/music/tetrisMusic.mp3").toString());
		player = new MediaPlayer(media);
		player.play();
	}
}