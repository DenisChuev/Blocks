package dc.blocks;

import android.graphics.Point;
import android.os.Handler;

import java.util.Random;

import static java.lang.System.arraycopy;

public class Game {

    private final static int startSpeed = 1000, minSpeed = 200;
    final int[][] field;
    Handler handler = new Handler();
    private int rows, cols;
    private int score, rowsDeleted;
    private boolean tmp_grid[][];
    private boolean figure[][];
    private Point position;
    private boolean first = true;
    private int type, size;
    private int speed = startSpeed;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            synchronized (this) {
                moveDown();
            }
            handler.postDelayed(runnable, speed);
        }
    };

    public Game(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        field = new int[rows][cols];
        position = new Point(0, 0);
        newGame();
        runnable.run();
    }

    void pause() {
        handler.removeCallbacks(runnable);
    }

    void resume() {
        runnable.run();
    }

    void newGame() {
        clearField();
        newShape();
        score = 0;
        rowsDeleted = 0;
        speed = startSpeed;
    }

    private void newShape() {
        first = true;
        type = Math.abs(new Random(System.currentTimeMillis()).nextInt()) % 7 + 1;
//        type = (int) (Math.random() * 7) + 1;
        size = Shapes.VALUES[type - 1].length;
        figure = new boolean[size][size];
        tmp_grid = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            arraycopy(Shapes.VALUES[type - 1][i], 0, figure[i], 0, size);
        }
        setPosition(cols / 2 - size / 2, type == 1 ? -1 : 0);
    }

    private void cut() {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (figure[i][j] && position.y + i >= 0)
                    field[position.y + i][position.x + j] = 0;
    }

    private void paste() {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (figure[i][j] && position.y + i >= 0)
                    field[position.y + i][position.x + j] = type;
    }

    private boolean canPaste() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int to_x = j + position.x;
                int to_y = i + position.y;
                if (figure[i][j]) {
                    if (0 > to_x || to_x >= cols || to_y >= rows)
                        return false;
                    if (to_y >= 0 && field[to_y][to_x] != 0)
                        return false;
                }
            }
        }
        return true;
    }

    private boolean canStepDown() {
        cut();
        position.y++;
        boolean OK = canPaste();
        position.y--;
        paste();
        return OK;
    }

    void moveLeft() {
        synchronized (field) {
            cut();
            position.x--;
            if (!canPaste())
                position.x++;
            paste();
        }
    }

    void moveRight() {
        synchronized (field) {
            cut();
            position.x++;
            if (!canPaste())
                position.x--;
            paste();
        }
    }

    void rotateRight() {
        synchronized (field) {
            cut();
            rotate();
            if (!canPaste())
                rotateBack();
            paste();
        }
    }

    void moveDown() {
        synchronized (field) {
            if (!first)
                if (canStepDown()) {
                    cut();
                    position.y++;
                    paste();
                } else {
                    newShape();
                    if (!canPaste()) newGame();
                    removeFullRows();
                }
            else {
                paste();
                first = false;
            }
        }
    }

    private void clearField() {
        synchronized (field) {
            score = 0;
            for (int i = 0; i < rows; i++)
                for (int j = 0; j < cols; j++)
                    field[i][j] = 0;
        }
    }

    private void setPosition(int x, int y) {
        position.x = x;
        position.y = y;
    }

    public int getScore() {
        return score;
    }

    private void rotate() {
        if (position.y < 0) return;
        for (int i = 0; i < size; i++)
            arraycopy(figure[i], 0, tmp_grid[i], 0, size);
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                figure[j][i] = tmp_grid[size - 1 - i][j];
    }

    private void rotateBack() {
        for (int i = 0; i < size; i++)
            arraycopy(tmp_grid[i], 0, figure[i], 0, size);
    }

    private boolean rowIsFull(int row) {
        for (int i = 0; i < cols; i++)
            if (field[row][i] == 0)
                return false;
        return true;
    }

    private int countFullRows() {
        int n_full_rows = 0;
        for (int i = 0; i < rows; i++)
            if (rowIsFull(i)) n_full_rows++;
        return n_full_rows;
    }

    private void removeRow(int row) {
        for (int j = 0; j < cols; j++)
            field[row][j] = 0;
        for (int i = row; i > 0; i--)
            arraycopy(field[i - 1], 0, field[i], 0, cols);
    }

    private void removeFullRows() {
        synchronized (field) {
            int n_full = countFullRows();
            rowsDeleted += n_full;
            score += (int) Math.pow(n_full, 2);
            if (n_full == 0) return;
            for (int i = 0; i < rows; i++)
                while (rowIsFull(i)) {
                    removeRow(i);
                    removeRow(0);
                }
            int newSpeed = startSpeed - rowsDeleted * 5;
            if (newSpeed >= minSpeed) speed = newSpeed;
        }
    }
}