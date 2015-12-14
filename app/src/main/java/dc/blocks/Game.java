package dc.blocks;

import android.os.Handler;

class Game {

    private final static int startSpeed = 1000, minSpeed = 200;
    static Field field;
    static Figure figure;
    private final Handler handler = new Handler();
    private int speed;
    private int score;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            synchronized (this) {
                figureDown();
            }
            handler.postDelayed(runnable, speed);
        }
    };

    public Game(int rows, int cols) {
        field = new Field(rows, cols);
        newGame();
        runnable.run();
    }

    public int getScore() {
        return score;
    }

    void pause() {
        handler.removeCallbacks(runnable);
    }

    void resume() {
        runnable.run();
    }

    void newGame() {
        field.clear();
        newFigure();
        score = 0;
        speed = startSpeed;
    }

    void newFigure() {
        figure = FigureFactory.createFigure(field.getCols());
    }

    void figureDown() {
        synchronized (field.matrix) {
            if (!figure.down()) {
                newFigure();
                if (!figure.isPositionAvailable()) newGame();
                score += (int) Math.pow(field.removeFullRows(), 2);
            }
        }
        int newSpeed = startSpeed - field.getRowsDeleted() * 5;
        if (newSpeed >= minSpeed) speed = newSpeed;
    }
}