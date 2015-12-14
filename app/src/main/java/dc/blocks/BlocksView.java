package dc.blocks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class BlocksView extends SurfaceView implements SurfaceHolder.Callback {

    private final static int CELLS = 30;
    private final Paint paint;
    private final int background;
    private final int frame;
    private final String[] figureColors;
    Game game;
    private int width, height, sizeCell, rows, cols;
    private float indent_h, indent_w;
    private boolean isUpdatedSizes = false;
    private DrawThread thread;


    public BlocksView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        background = ContextCompat.getColor(context, R.color.background);
        frame = ContextCompat.getColor(context, R.color.frame);
        figureColors = context.getResources().getStringArray(R.array.figure_colors);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (!isUpdatedSizes) updateSizes();
        thread = new DrawThread();
        thread.running = true;
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        thread.running = false;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawField(canvas);
    }

    private void updateSizes() {
        width = getWidth();
        height = getHeight();
        sizeCell = (width + height) / CELLS;
        rows = height / sizeCell;
        cols = width / sizeCell;
        indent_h = (height - rows * sizeCell) / 2;
        indent_w = (width - cols * sizeCell) / 2;
        game = new Game(rows, cols);
        isUpdatedSizes = true;
    }

    private void drawBackground(Canvas canvas) {
        paint.setColor(frame);
        canvas.drawRect(0, 0, width, indent_h, paint);
        canvas.drawRect(width - indent_w, 0, width, height, paint);
        canvas.drawRect(width, height - indent_h, 0, height, paint);
        canvas.drawRect(0, height, indent_w, 0, paint);
        paint.setColor(background);
        canvas.drawRect(indent_w, indent_h, width - indent_w, height - indent_h, paint);
    }

    private void drawField(Canvas canvas) {
        synchronized (Game.field.matrix) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (Game.field.matrix[i][j] != 0) {
                        paint.setColor(Color.parseColor(figureColors[Game.field.matrix[i][j] - 1]));
                        float x1 = indent_w + j * sizeCell + 1;
                        float y1 = indent_h + i * sizeCell + 1;
                        float x2 = indent_w + (j + 1) * sizeCell - 1;
                        float y2 = indent_h + (i + 1) * sizeCell - 1;
                        canvas.drawRect(x1, y1, x2, y2, paint);
                    }
                }
            }
        }
    }

    private class DrawThread extends Thread {

        private boolean running = false;

        @SuppressLint("WrongCall")
        @Override
        public void run() {
            while (running) {
                Canvas canvas = null;
                try {
                    if ((canvas = getHolder().lockCanvas(null)) == null) continue;
                    synchronized (getHolder()) {
                        onDraw(canvas);
                    }

                } finally {
                    if (canvas != null) getHolder().unlockCanvasAndPost(canvas);
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}