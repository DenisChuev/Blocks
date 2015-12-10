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
    Game game;
    private int width, height, sizeCell, rows, cols;
    private float indent_h, indent_w;
    private boolean isUpdatedSizes = false;
    private Paint paint;
    private int background, frame;
    private DrawThread thread;

    public BlocksView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        background = ContextCompat.getColor(context, R.color.background);
        frame = ContextCompat.getColor(context, R.color.frame);
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
        drawShape(canvas);
    }

    void updateSizes() {
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

    void drawBackground(Canvas canvas) {
        paint.setColor(frame);
        canvas.drawRect(0, 0, width, indent_h, paint);
        canvas.drawRect(width - indent_w, 0, width, height, paint);
        canvas.drawRect(width, height - indent_h, 0, height, paint);
        canvas.drawRect(0, height, indent_w, 0, paint);
        paint.setColor(background);
        canvas.drawRect(indent_w, indent_h, width - indent_w, height - indent_h, paint);
    }

    void drawShape(Canvas canvas) {
        synchronized (game.field) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    paint.setColor(Color.parseColor(Shapes.COLORS[game.field[i][j]]));
                    float x1 = indent_w + j * sizeCell + 1;
                    float y1 = indent_h + i * sizeCell + 1;
                    float x2 = indent_w + (j + 1) * sizeCell - 1;
                    float y2 = indent_h + (i + 1) * sizeCell - 1;
                    canvas.drawRect(x1, y1, x2, y2, paint);
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