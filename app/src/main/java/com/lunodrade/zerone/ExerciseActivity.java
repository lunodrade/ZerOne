package com.lunodrade.zerone;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.lunodrade.zerone.exercise.QuestionsAdapter;
import com.lunodrade.zerone.exercise.QuestionsFragment;
import com.lunodrade.zerone.exercise.StudiesFragment;
import com.lunodrade.zerone.exercise.ViewPagerAdapter;

import java.io.InputStream;
import java.util.Map;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class ExerciseActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Toolbar mTootlbar;

    private QuestionsFragment mQuestionsFragment;
    private Map<Integer, LinkedTreeMap> mQuestions;

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
    }

    private void checkExtras(Bundle extras) {
        Log.d("ExerciseActivity", "checkExtras: existe extras? " + (extras != null));
        if (extras != null) {
            Log.d("ExerciseActivity", "checkExtras: chamando leitura de extras = " + extras.getString("title"));
            
            String title = extras.getString("title");
            getSupportActionBar().setTitle(title);
        }
    }

    private void setupViewPager() {
        mQuestionsFragment = new QuestionsFragment();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mQuestionsFragment, "EXERCÍCIOS");
        adapter.addFragment(new StudiesFragment(), "ESTUDOS");
        mViewPager.setAdapter(adapter);
    }

    public void showInfo(String info) {
        Context contexto = getApplicationContext();
        String texto = info;
        int duracao = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(contexto, texto, duracao);
        toast.show();
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

}
