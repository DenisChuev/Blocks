package dc.blocks;

class Figure {

    private final int[][] matrix;
    private int x;
    private int y;

    public Figure(int x, int y, int[][] matrix) {
        this.x = x;
        this.y = y;
        this.matrix = matrix;
    }

    boolean isPositionAvailable() {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] != 0) {
                    int col = j + x;
                    int row = i + y;
                    if (col < 0 || col >= Game.field.getCols() || row >= Game.field.getRows())
                        return false;
                    if (row >= 0 && Game.field.matrix[row][col] != 0)
                        return false;
                }
            }
        }
        return true;
    }

    private void cut() {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] != 0 && y + i >= 0) {
                    Game.field.matrix[y + i][x + j] = 0;
                }
            }
        }
    }

    private void paste() {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] != 0 && y + i >= 0) {
                    Game.field.matrix[y + i][x + j] = matrix[i][j];
                }
            }
        }
    }

    private boolean canStepDown() {
        cut();
        y++;
        boolean res = isPositionAvailable();
        y--;
        paste();
        return res;
    }


    void left() {
        synchronized (Game.field.matrix) {
            cut();
            x--;
            if (!isPositionAvailable())
                x++;
            paste();
        }
    }

    void right() {
        synchronized (Game.field.matrix) {
            cut();
            x++;
            if (!isPositionAvailable())
                x--;
            paste();
        }
    }

    boolean down() {
        synchronized (Game.field.matrix) {
            if (canStepDown()) {
                cut();
                y++;
                paste();
            } else return false;
        }
        return true;
    }

    void rotate() {
        if (y < 0) return;
        synchronized (Game.field.matrix) {
            cut();
            rotateRight();
            if (!isPositionAvailable())
                rotateLeft();
            paste();
        }
    }

    private void rotateRight() {
        int n = matrix.length, b;
        for (int i = 0; i < n / 2; i++) {
            for (int j = i; j < n - i - 1; j++) {
                b = matrix[i][j];
                matrix[i][j] = matrix[n - j - 1][i];
                matrix[n - j - 1][i] = matrix[n - i - 1][n - j - 1];
                matrix[n - i - 1][n - j - 1] = matrix[j][n - i - 1];
                matrix[j][n - i - 1] = b;
            }
        }
    }

    private void rotateLeft() {
        int n = matrix.length, b;
        for (int i = 0; i < n / 2; i++) {
            for (int j = i; j < n - i - 1; j++) {
                b = matrix[i][j];
                matrix[i][j] = matrix[j][n - i - 1];
                matrix[j][n - i - 1] = matrix[n - i - 1][n - j - 1];
                matrix[n - i - 1][n - j - 1] = matrix[n - j - 1][i];
                matrix[n - j - 1][i] = b;
            }
        }
    }
}