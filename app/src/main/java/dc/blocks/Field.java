package dc.blocks;

class Field {

    final int[][] matrix;
    private final int rows;
    private final int cols;
    private int rowsDeleted;

    public Field(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        matrix = new int[rows][cols];
    }

    public int getRowsDeleted() {
        return rowsDeleted;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    void clear() {
        synchronized (matrix) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    matrix[i][j] = 0;
                }
            }
        }
        rowsDeleted = 0;
    }

    int removeFullRows() {
        int fullRows = countFullRows();
        rowsDeleted += fullRows;
        synchronized (matrix) {
            if (fullRows != 0) {
                for (int i = 0; i < rows; i++) {
                    while (rowIsFull(i)) {
                        removeRow(i);
                    }
                }
            }
        }
        return fullRows;
    }

    private boolean rowIsFull(int row) {
        for (int i = 0; i < cols; i++) {
            if (matrix[row][i] == 0)
                return false;
        }
        return true;
    }

    private int countFullRows() {
        int count = 0;
        for (int i = 0; i < rows; i++) {
            if (rowIsFull(i))
                count++;
        }
        return count;
    }

    private void removeRow(int row) {
        for (int i = row; i > 0; i--) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = matrix[i - 1][j];
                matrix[i - 1][j] = 0;
            }
        }
    }
}