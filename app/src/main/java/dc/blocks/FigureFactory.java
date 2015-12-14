package dc.blocks;

import java.util.Random;

class FigureFactory {

    private final static int[][][] FIGURES = {

            //I
            {
                    {0, 0, 0, 0},
                    {1, 1, 1, 1},
                    {0, 0, 0, 0},
                    {0, 0, 0, 0},
            },

            //J
            {
                    {0, 0, 2},
                    {2, 2, 2},
                    {0, 0, 0},
            },

            //L
            {
                    {3, 0, 0},
                    {3, 3, 3},
                    {0, 0, 0},
            },

            //S
            {
                    {4, 4, 0},
                    {0, 4, 4},
                    {0, 0, 0},
            },

            //Z
            {
                    {0, 5, 5},
                    {5, 5, 0},
                    {0, 0, 0},
            },

            //T
            {
                    {0, 6, 0},
                    {6, 6, 6},
                    {0, 0, 0},
            },

            //O
            {
                    {7, 7},
                    {7, 7},
            },
    };

    static Figure createFigure(int width) {
        int index = Math.abs(new Random(System.currentTimeMillis()).nextInt()) % 7;
        Random r = new Random();
        int size = FIGURES[index].length;
        int[][] figure = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(FIGURES[index][i], 0, figure[i], 0, size);
        }
        int x = (width / 2 - FIGURES[index].length / 2);
        int y = FIGURES[index].length == 4 ? -2 : -1;
        return new Figure(x, y, figure);
    }
}