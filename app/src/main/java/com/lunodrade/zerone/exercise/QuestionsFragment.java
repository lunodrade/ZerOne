package com.lunodrade.zerone.exercise;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.lunodrade.zerone.ExerciseActivity;
import com.lunodrade.zerone.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;

import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.view.KeyEvent;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class QuestionsFragment extends Fragment {

    private ExerciseActivity mActivity;

    private Map<Integer, LinkedTreeMap> mQuestions;
    private ArrayList<Integer> mArrayIndexQuestions;
    private int mQuestionID;
    private int mCurrentIndexQuestions;
    private static final int MAXIMUM_INDEX_QUESTIONS = 4;


    @BindView(R.id.questions_number)
    TextView mQuestionNumber;

    @BindView(R.id.question_tyinput_block)
    View mTyInputBlock;
    @BindView(R.id.question_tyradio_block)
    View mTyRadioBlock;
    @BindView(R.id.question_tychip_block)
    View mTyChipBlock;
    @BindView(R.id.question_tycheck_block)
    View mTyCheckBlock;

    @BindView(R.id.question_tyinput_query)
    TextView mTyInputQuery;
    @BindView(R.id.question_tyradio_query)
    TextView mTyRadioQuery;
    @BindView(R.id.question_tychip_query)
    TextView mTyChipQuery;
    @BindView(R.id.question_tycheck_query)
    TextView mTyCheckQuery;

    @BindView(R.id.question_tyinput_confirm)
    Button mInputConfirmButton;
    @BindView(R.id.question_tyradio_confirm)
    Button mRadioConfirmButton;
    @BindView(R.id.question_tychip_confirm)
    Button mChipConfirmButton;
    @BindView(R.id.question_tycheck_confirm)
    Button mCheckConfirmButton;


    @BindView(R.id.question_tyinput_answer)
    TextInputEditText mTyInputAnswer;



    @BindView(R.id.question_tyradio_radiogroup)
    RadioGroup mTyRadioGroup;
    @BindViews({R.id.question_tyradio_optionA,
                R.id.question_tyradio_optionB,
                R.id.question_tyradio_optionC,
                R.id.question_tyradio_optionD })
    List<RadioButton> mTyRadioButton;


    @BindViews({R.id.questions_tycheck_boxA,
                R.id.questions_tycheck_boxB,
                R.id.questions_tycheck_boxC,
                R.id.questions_tycheck_boxD})
    List<CheckBox> mTyCheckBoxes;





    // Required empty public constructor
    public QuestionsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_questions, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        mActivity = (ExerciseActivity) getActivity();

        mCurrentIndexQuestions = 0;

        loadQuestions();
        callQuestions();
    }

    private void loadQuestions() {
        InputStream stream = getResources().openRawResource(R.raw.raw);  //TODO: aqui seleciona o book
        XmlToJson xmlToJson = new XmlToJson.Builder(stream, null)
                .forceList("/book/question/option")
                .forceList("/book/question/chip")
                .forceList("/book/question/answer")
                .build();
        Gson gson = new Gson();
        QuestionsAdapter p = gson.fromJson(xmlToJson.toString(), QuestionsAdapter.class);

        mQuestions = p.getQuestions();
        mArrayIndexQuestions = p.indexQuestions;
        Collections.shuffle(mArrayIndexQuestions);
        Log.d("ExerciseActivity", "initQuestions: GSON" + mQuestions.size() + " | " + mArrayIndexQuestions.size());
    }















    private void callQuestions() {
        if (mCurrentIndexQuestions < MAXIMUM_INDEX_QUESTIONS) {
            mQuestionID = mArrayIndexQuestions.get(mCurrentIndexQuestions);
            mCurrentIndexQuestions++;

            Log.d("ExerciseActivity", "callTestQuestion ID: " + mQuestionID + " → " + mQuestions.get(mQuestionID));

            mActivity.hideSoftKeyboard();

            updateView(mQuestions.get(mQuestionID));
        }
    }




    //TODO: ao trocar de aba, esconder o teclado




    public void updateView(LinkedTreeMap mSingleQuestion) {

        String type = mSingleQuestion.get("type").toString();
        String query = mSingleQuestion.get("query").toString().replace(" \\n ", "\n");

        String numberText = mCurrentIndexQuestions + " de " + MAXIMUM_INDEX_QUESTIONS;
        mQuestionNumber.setText(numberText);

        mTyInputBlock.setVisibility(View.GONE);
        mTyRadioBlock.setVisibility(View.GONE);
        mTyChipBlock.setVisibility(View.GONE);
        mTyCheckBlock.setVisibility(View.GONE);

        if (type.equals("input")) {
            mTyInputBlock.setVisibility(View.VISIBLE);
            mTyInputQuery.setText(query);

        } else if (type.equals("radio")) {
            mTyRadioBlock.setVisibility(View.VISIBLE);
            mTyRadioQuery.setText(query);
            ArrayList options = (ArrayList) mSingleQuestion.get("option");

            for (int i = 0; i < options.size(); i++) {
                LinkedTreeMap opt = (LinkedTreeMap) options.get(i);
                String text = opt.get("content").toString();
                mTyRadioButton.get(i).setText(text);
            }
            //TOD: permitir mais opções no xml... que daí aqui coloca gone para (lists.size - opt.size)


        } else if (type.equals("chip")) {
            mTyChipBlock.setVisibility(View.VISIBLE);
            query = query.replace("$chip", "____");
            mTyChipQuery.setText(query);

        } else if (type.equals("check")) {
            mTyCheckBlock.setVisibility(View.VISIBLE);
            mTyCheckQuery.setText(query);
        }
    }









    @OnEditorAction(R.id.question_tyinput_answer)
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_DONE){
            testar(new Button(getContext()));
            return true;
        }
        return false;
    }




    @OnClick(R.id.question_tyinput_confirm)
    public void testar(Button button) {
        Boolean result = false;
        String userAnswer = mTyInputAnswer.getText().toString().toLowerCase().trim();

        ArrayList answers = (ArrayList) mQuestions.get(mQuestionID).get("answer");
        for (int i=0; i<answers.size(); i++) {
            String realAnswer = ((LinkedTreeMap) answers.get(i)).get("content").toString()
                    .toLowerCase().trim();

            Log.d("QuestionsFragment", "testar USER: " + userAnswer + "| REAL: " + realAnswer);

            if (userAnswer.equals(realAnswer)) {
                result = true;
                break;
            }
        }

        if (result) {
            callQuestions();
        } else {
            mActivity.showInfo("Errou a resposta");
        }
    }

    @OnClick(R.id.question_tyradio_confirm)
    public void testar2(Button button) {
        int radioButtonID = mTyRadioGroup.getCheckedRadioButtonId();
        View radioButton = mTyRadioGroup.findViewById(radioButtonID);
        int idx = mTyRadioGroup.indexOfChild(radioButton);

        Log.d("QuestionsFragment", "testar: INDEX = " + idx);

        callQuestions();
    }

    @OnClick(R.id.question_tychip_confirm)
    public void testar3(Button button) {

        callQuestions();
    }

    @OnClick(R.id.question_tycheck_confirm)
    public void testar4(Button button) {

        callQuestions();
    }





    /*
    @BindViews({R.id.questions_tycheck_boxA,
            R.id.questions_tycheck_boxB,
            R.id.questions_tycheck_boxC,
            R.id.questions_tycheck_boxD})
    List<CheckBox> mTyCheckBoxes;
    */

    @OnCheckedChanged({R.id.questions_tycheck_boxA,
            R.id.questions_tycheck_boxB,
            R.id.questions_tycheck_boxC,
            R.id.questions_tycheck_boxD})
    public void onCheckboxChanged(CompoundButton button, boolean checked) {

    }




}
