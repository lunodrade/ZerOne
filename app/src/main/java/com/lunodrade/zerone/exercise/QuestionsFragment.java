package com.lunodrade.zerone.exercise;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.lunodrade.zerone.ExerciseActivity;
import com.lunodrade.zerone.MainActivity;
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
import butterknife.OnTextChanged;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;

import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;


public class QuestionsFragment extends Fragment {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                  Variáveis
    //
    //////////////////////////////////////////////////////////////////////////////////////////////*/
    private ExerciseActivity mActivity;

    private Map<Integer, LinkedTreeMap> mQuestions;
    private ArrayList<Integer> mArrayIndexQuestions;
    private int mQuestionID = -1;
    private int mCurrentIndexQuestions;
    private static final int MAXIMUM_INDEX_QUESTIONS = 4;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                  Bind do butter knife
    //
    //////////////////////////////////////////////////////////////////////////////////////////////*/

    //Número da questão

    @BindView(R.id.questions_number)
    TextView mQuestionNumber;

    //Blocos de UI

    @BindView(R.id.question_transition_block)
    View mTransitionBlock;

    @BindView(R.id.question_tyinput_block)
    View mTyInputBlock;

    @BindView(R.id.question_tyradio_block)
    View mTyRadioBlock;

    @BindView(R.id.question_tychip_block)
    View mTyChipBlock;

    @BindView(R.id.question_tycheck_block)
    View mTyCheckBlock;

    //Pergunta

    @BindView(R.id.question_tyinput_query)
    TextView mTyInputQuery;

    @BindView(R.id.question_tyradio_query)
    TextView mTyRadioQuery;

    @BindView(R.id.question_tychip_query)
    TextView mTyChipQuery;

    @BindView(R.id.question_tycheck_query)
    TextView mTyCheckQuery;

    //Botões de Confirm

    @BindView(R.id.question_tyinput_confirm)
    Button mInputConfirmButton;

    @BindView(R.id.question_tyradio_confirm)
    Button mRadioConfirmButton;

    @BindView(R.id.question_tychip_confirm)
    Button mChipConfirmButton;

    @BindView(R.id.question_tycheck_confirm)
    Button mCheckConfirmButton;

    //Respostas

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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                  Métodos da classe
    //
    //////////////////////////////////////////////////////////////////////////////////////////////*/

    // Required empty public constructor
    public QuestionsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_questions, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        mActivity = (ExerciseActivity) getActivity();
        mActivity.attachKeyboardListeners();

        mCurrentIndexQuestions = 0;

        loadQuestions();
        processQuestions();
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

    @OnEditorAction(R.id.question_tyinput_answer)
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        String text = v.getText().toString().trim();
        if(actionId == EditorInfo.IME_ACTION_DONE && (text.equals("") != true)){
            onConfirmTypeInput(new Button(getContext()));
            return true;
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                  Tratamento para cada tipo de questão, ao iniciar ela
    //
    //////////////////////////////////////////////////////////////////////////////////////////////*/

    private void processQuestions() {
        Log.d("QuestionsFragment", "processQuestions -- index atual: " + mCurrentIndexQuestions +
                                                    " -- index máximo: " + MAXIMUM_INDEX_QUESTIONS);

        //Após acertar a última questão
        if (mCurrentIndexQuestions >= MAXIMUM_INDEX_QUESTIONS) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            return;
        }

        //Limpar todos possíveis lixos visuais da questão anterior (se existir)
        if (mQuestionID >= 0) {
            Log.d("QuestionsFragment", "processQuestions: limpando rastros visuais das questões");

            mTransitionBlock.setVisibility(View.VISIBLE);
            //Por início, coloca-se todos blocos como escondidos...
            mTyInputBlock.setVisibility(View.GONE);
            mTyRadioBlock.setVisibility(View.GONE);
            mTyChipBlock.setVisibility(View.GONE);
            mTyCheckBlock.setVisibility(View.GONE);

            mTyInputAnswer.setText("");
            mTyRadioGroup.clearCheck();
            for (int i = 0; i < 4; i++) {
                mTyCheckBoxes.get(i).setChecked(false);
            }

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Processar qual é a proxima questão, e chamar o update visual dela
                    callNextQuestion();
                }
            }, 450);

        } else {
            //Processar qual é a proxima questão, e chamar o update visual dela
            if (mQuestionID < 0) {
                callNextQuestion();
            }
        }

    }

    private void callNextQuestion() {
        Log.d("QuestionsFragment", "callNextQuestion -- ID: " + mQuestionID + " → " + mQuestions.get(mQuestionID));
        mQuestionID = mArrayIndexQuestions.get(mCurrentIndexQuestions);
        mCurrentIndexQuestions++;

        mActivity.hideSoftKeyboard();

        updateView(mQuestions.get(mQuestionID));
    }

    public void updateView(LinkedTreeMap mSingleQuestion) {

        String type = mSingleQuestion.get("type").toString();
        String query = mSingleQuestion.get("query").toString().replace(" \\n ", "\n");

        String numberText = mCurrentIndexQuestions + " de " + MAXIMUM_INDEX_QUESTIONS;
        mQuestionNumber.setText(numberText);

        mTransitionBlock.setVisibility(View.GONE);

        //Trata de cada tipo individualmente, mostrando aquele bloco em específico
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

        } else if (type.equals("chip")) {
            mTyChipBlock.setVisibility(View.VISIBLE);
            query = query.replace("$chip", "____");
            mTyChipQuery.setText(query);

        } else if (type.equals("check")) {
            mTyCheckBlock.setVisibility(View.VISIBLE);
            mTyCheckQuery.setText(query);
            ArrayList options = (ArrayList) mSingleQuestion.get("option");
            for (int i = 0; i < options.size(); i++) {
                LinkedTreeMap opt = (LinkedTreeMap) options.get(i);
                String text = opt.get("content").toString();
                mTyCheckBoxes.get(i).setText(text);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                  Ação ao clicar no botão de confirmar    //TODO: mostrar comentário UI ao errar
    //
    //////////////////////////////////////////////////////////////////////////////////////////////*/

    // Perguntas do tipo Input

    @OnClick(R.id.question_tyinput_confirm)
    public void onConfirmTypeInput(Button button) {
        Boolean result = false;
        String userAnswer = mTyInputAnswer.getText().toString().toLowerCase().trim();

        ArrayList answers = (ArrayList) mQuestions.get(mQuestionID).get("answer");

        for (int i=0; i<answers.size(); i++) {
            String realAnswer = ((LinkedTreeMap) answers.get(i)).get("content").toString().toLowerCase().trim();

            Log.d("QuestionsFragment", "testar USER: " + userAnswer + "| REAL: " + realAnswer);
            if (userAnswer.equals(realAnswer)) {
                result = true;
                break;
            }
        }

        if (result) {
            processQuestions();
        } else {
            mActivity.showInfo("Errou a resposta");
        }
    }

    // Perguntas do tipo Radiobutton

    @OnClick(R.id.question_tyradio_confirm)
    public void onConfirmTypeRadiobutton(Button button) {
        Boolean result = false;

        int radioButtonID = mTyRadioGroup.getCheckedRadioButtonId();
        View radioButton = mTyRadioGroup.findViewById(radioButtonID);
        int idx = mTyRadioGroup.indexOfChild(radioButton);
        String userAnswer = ""+idx;

        ArrayList answers = (ArrayList) mQuestions.get(mQuestionID).get("answer");

        for (int i=0; i<answers.size(); i++) {
            String realAnswer = ((LinkedTreeMap) answers.get(i)).get("content").toString().toLowerCase().trim();

            Log.d("QuestionsFragment", "testar USER: " + userAnswer + "| REAL: " + realAnswer);
            if (userAnswer.equals(realAnswer)) {
                result = true;
                break;
            }
        }

        if (result) {
            processQuestions();
        } else {
            mActivity.showInfo("Errou a resposta");
        }
    }

    // Perguntas do tipo Chips //TODO fazer o confirmar do tipo chips

    @OnClick(R.id.question_tychip_confirm)
    public void onConfirmTypeChip(Button button) {

        processQuestions();
    }

    // Perguntas do tipo Checkbox

    @OnClick(R.id.question_tycheck_confirm)
    public void onConfirmTypeCheckbox(Button button) {
        Boolean result = false;

        List<String> list = new ArrayList<>();
        for (int i=0; i<mTyCheckBoxes.size(); i++) {
            CheckBox box = mTyCheckBoxes.get(i);
            if (box.isChecked()) {
                list.add(""+i);
            }
        }
        String userAnswer = TextUtils.join(",", list);

        ArrayList answers = (ArrayList) mQuestions.get(mQuestionID).get("answer");
        for (int i=0; i<answers.size(); i++) {
            String realAnswer = ((LinkedTreeMap) answers.get(i)).get("content").toString().toLowerCase().trim();

            Log.d("QuestionsFragment", "testar USER: " + userAnswer + "| REAL: " + realAnswer);
            if (userAnswer.equals(realAnswer)) {
                result = true;
                break;
            }
        }

        if (result) {
            processQuestions();
        } else {
            mActivity.showInfo("Errou a resposta");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                  Habilitar/Desabilitar botão de confirmar        TODO: falta do chip


    //TODO: reinicar a UI de cada tipo de questão ao passar por ela (acho que o butter tem essa função)
    //
    //////////////////////////////////////////////////////////////////////////////////////////////*/

    // Perguntas do tipo Input

    @OnTextChanged(R.id.question_tyinput_answer)
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean enabled = false;
        String text = mTyInputAnswer.getText().toString().trim();
        if (text.equals("") == false)
            enabled = true;
        mInputConfirmButton.setEnabled(enabled);
    }

    // Perguntas do tipo Radiobutton

    @OnCheckedChanged({R.id.question_tyradio_optionA, R.id.question_tyradio_optionB,
                       R.id.question_tyradio_optionC, R.id.question_tyradio_optionD})
    public void onRadiobuttonChanged(CompoundButton button, boolean checked) {
        mRadioConfirmButton.setEnabled(true);
    }

    // Perguntas do tipo Checkbox

    @OnCheckedChanged({R.id.questions_tycheck_boxA, R.id.questions_tycheck_boxB,
                       R.id.questions_tycheck_boxC, R.id.questions_tycheck_boxD})
    public void onCheckboxChanged(CompoundButton button, boolean checked) {
        boolean enabled = false;
        for (CheckBox box : mTyCheckBoxes) {
            if (box.isChecked() == true) {
                enabled = true;
                break;
            }
        }
        mCheckConfirmButton.setEnabled(enabled);
    }




}
