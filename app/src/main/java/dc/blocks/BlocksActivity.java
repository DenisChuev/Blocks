package dc.blocks;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

public class BlocksActivity extends AppCompatActivity implements OnTouchListener {

    private final Handler handler = new Handler();
    private int action; //0-not move    1-move left     2-move right    3-move figureDown    4-rotate
    private int numMoves, numMadeMoves;
    private boolean running = true;
    private BlocksView view;
    private MenuItem play_pause;
    private Animation animation;
    private Toolbar toolbar;
    private SharedPreferences pref;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                int curScore = view.game.getScore();
                if (curScore > pref.getInt("record", 0)) {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("record", curScore);
                    editor.apply();
                }
                int record = pref.getInt("record", 0);
                toolbar.setTitle(getString(R.string.score) + " " + view.game.getScore());
                toolbar.setSubtitle(getString(R.string.record) + " " + record);
                if (action != 0) {
                    if (action == 1) Game.figure.left();
                    else if (action == 2) Game.figure.right();
                    else if (action == 3) view.game.figureDown();
//                    else if (action == 4) view.game.rotate();
                    numMadeMoves++;
                }
            } catch (NullPointerException ignored) {
            }
            handler.postDelayed(runnable, 75);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        animation = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        setSupportActionBar(toolbar);
        ImageButton left = (ImageButton) findViewById(R.id.left_button);
        ImageButton right = (ImageButton) findViewById(R.id.right_button);
        ImageButton down = (ImageButton) findViewById(R.id.down_button);
        ImageButton rotate = (ImageButton) findViewById(R.id.rotate_button);
        left.setOnTouchListener(this);
        right.setOnTouchListener(this);
        down.setOnTouchListener(this);
        rotate.setOnTouchListener(this);
        view = (BlocksView) findViewById(R.id.blocks_view);
        runnable.run();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        play_pause = menu.findItem(R.id.play_pause);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                pause();
                view.game.newGame();
                resume();
                break;
            case R.id.play_pause:
                if (running) pause();
                else resume();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, getString(R.string.exit), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    private void pause() {
        action = 0;
        running = false;
        play_pause.setIcon(R.drawable.ic_action_play);
        play_pause.setTitle(getString(R.string.play));
        view.game.pause();
        handler.removeCallbacks(runnable);
    }

    private void resume() {
        running = true;
        play_pause.setIcon(R.drawable.ic_action_pause);
        play_pause.setTitle(getString(R.string.pause));
        view.game.resume();
        runnable.run();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (running && event.getY() >= 0) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (v.getId() == R.id.left_button) action = 1;
                    else if (v.getId() == R.id.right_button) action = 2;
                    else if (v.getId() == R.id.down_button) action = 3;
//                    else if (v.getId() == R.id.rotate_button) action = 4;
                    numMoves++;
                    break;
                case MotionEvent.ACTION_UP:
                    v.startAnimation(animation);
                    if (numMoves > numMadeMoves) {
                        if (v.getId() == R.id.left_button) Game.figure.left();
                        else if (v.getId() == R.id.right_button) Game.figure.right();
                        else if (v.getId() == R.id.down_button) view.game.figureDown();
                        else if (v.getId() == R.id.rotate_button) Game.figure.rotate();
                    }
                    action = numMadeMoves = numMoves = 0;
                    break;
            }
        } else action = 0;
        return super.onTouchEvent(event);
    }
}