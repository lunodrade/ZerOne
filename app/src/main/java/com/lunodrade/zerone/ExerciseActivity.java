package com.lunodrade.zerone;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.lunodrade.zerone.exercise.QuestionsAdapter;
import com.lunodrade.zerone.exercise.QuestionsFragment;
import com.lunodrade.zerone.exercise.StudiesFragment;
import com.lunodrade.zerone.exercise.ViewPagerAdapter;

import java.io.InputStream;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;

public class ExerciseActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Toolbar mTootlbar;

    private QuestionsFragment mQuestionsFragment;
    private Map<Integer, LinkedTreeMap> mQuestions;

    private int mPomodoroSecondsIdle = 60 * 1000;       // 5 * 1000 = 5 seconds
    public Chronometer mPomodoroChronometer;

    public String mBookCode = "";
    public String mBookTitle = "";
    public int mBookLevel = 0;
    public int mBookPoints = 0;
    public int mCorrectQuestions = 0;
    public int mWrongQuestions = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        mTootlbar = (Toolbar) findViewById(R.id.exercise_toolbar);
        setSupportActionBar(mTootlbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        checkExtras(extras);

        mViewPager = (ViewPager) findViewById(R.id.exercise_viewpager);
        setupViewPager();

        mTabLayout = (TabLayout) findViewById(R.id.exercise_tabs);
        mTabLayout.setupWithViewPager(mViewPager);



        /*
        new FancyShowCaseView.Builder(this)
                .focusOn(findViewById(R.id.exercise_question_life))
                .title("Vidas\n\nVocê começa a atividade com 3 vidas. A cada erro você perde " +
                        "uma vida. Se ficar sem vidas, você termina a atividade sem conclui-lá!")
                .titleSize(16, TypedValue.COMPLEX_UNIT_SP)
                .build()
                .show();
        */
        new FancyShowCaseView.Builder(this)
                .focusOn(mTabLayout)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .title("Praticar - Estudar\n\nVocê começa a atividade com 3 vidas. A cada erro você perde " +
                        "uma vida. Se ficar sem vidas, você termina a atividade sem conclui-lá!")
                .titleSize(16, TypedValue.COMPLEX_UNIT_SP)
                .build();


        mPomodoroChronometer = (Chronometer) findViewById(R.id.exercise_question_pomodoro);
        mPomodoroChronometer.setBase(SystemClock.elapsedRealtime());
        mPomodoroChronometer.start();
    }

    private int getRelativeLeft(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    private int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }

    private void checkExtras(Bundle extras) {
        Log.d("ExerciseActivity", "checkExtras: existe extras? " + (extras != null));
        if (extras != null) {
            Log.d("ExerciseActivity", "checkExtras: chamando leitura de extras = " + extras.getString("title"));

            mBookCode = extras.getString("code");
            mBookLevel = extras.getInt("level");
            mBookPoints = extras.getInt("points");
            mBookTitle = extras.getString("title");
            getSupportActionBar().setTitle(mBookTitle);

            Log.d("ExerciseActivity", "TexteExtras: " + mBookLevel + " | " + mBookPoints + " | " + mBookTitle);
        }
    }

    private void setupViewPager() {
        mQuestionsFragment = new QuestionsFragment();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mQuestionsFragment, "EXERCÍCIOS");
        adapter.addFragment(new StudiesFragment(), "ESTUDOS");
        mViewPager.setAdapter(adapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            public void onPageSelected(int position) {
                hideSoftKeyboard();
            }
        });
    }

    public void showInfo(String info) {
        Context contexto = getApplicationContext();
        String texto = info;
        int duracao = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(contexto, texto, duracao);
        toast.show();
    }

    public void hideSoftKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_exercise, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */


    @Override
    public void onBackPressed() {
        checkBackConfirm();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                checkBackConfirm();
                break;
        }
        return true;
    }

    private void checkBackConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Você tem certeza que quer sair?")
                .setCancelable(true)
                .setPositiveButton("Sair", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ExerciseActivity.this.finish();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        //super.onBackPressed();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                  Scroll maxBottom quando mostrar o teclado
    //
    //////////////////////////////////////////////////////////////////////////////////////////////*/

    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int heightDiff = rootLayout.getRootView().getHeight() - rootLayout.getHeight();
            int contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(ExerciseActivity.this);

            if(heightDiff <= contentViewTop){
                onHideKeyboard();

                Intent intent = new Intent("KeyboardWillHide");
                broadcastManager.sendBroadcast(intent);
            } else {
                int keyboardHeight = heightDiff - contentViewTop;
                onShowKeyboard(keyboardHeight);

                Intent intent = new Intent("KeyboardWillShow");
                intent.putExtra("KeyboardHeight", keyboardHeight);
                broadcastManager.sendBroadcast(intent);
            }
        }
    };

    private boolean keyboardListenersAttached = false;
    private ViewGroup rootLayout;

    protected void onShowKeyboard(int keyboardHeight) {
        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.questions_scrollview);
        nestedScrollView.fullScroll(View.FOCUS_DOWN);
    }
    protected void onHideKeyboard() { }

    public void attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return;
        }

        rootLayout = (ViewGroup) findViewById(R.id.questions_scrollview);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

        keyboardListenersAttached = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (keyboardListenersAttached) {
            rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //
    //
    //////////////////////////////////////////////////////////////////////////////////////////////*/

    public void updateQuestionNumber(String numberText) {
        TextView questionNumber = (TextView) findViewById(R.id.exercise_question_number);
        TextView questionLife = (TextView) findViewById(R.id.exercise_question_life);
        int lifeLeft = 3 - mWrongQuestions;

        questionNumber.setText(""+numberText);
        questionLife.setText(""+lifeLeft+"♥");
    }




    @Override
    public void onUserInteraction() {
        //Log.d("Pomodoro", "userTouched");
        countDownTimer.cancel();
        countDownTimer.start();

        super.onUserInteraction();
    }

    CountDownTimer countDownTimer = new CountDownTimer(mPomodoroSecondsIdle, 1000) {
        public void onTick(long millisUntilFinished) { }

        public void onFinish() {
            mPomodoroChronometer.stop();
            //mensagem que pomodoro parou
        }
    }.start();

    @Override
    protected void onPause() {
        mPomodoroChronometer.stop();
        super.onPause();
    }
}
