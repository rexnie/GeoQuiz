package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_IS_CHEATER = "is_cheater";
    private static final int REQUEST_CODE_CHEAT = 0;
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mPrevButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;
    private ImageButton mNextImageButton;
    private ImageButton mPrevImageButton;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };
    private int mCurrentIndex = 0;
    private int NUM_QUESTIONS = mQuestionBank.length;
    private boolean mIsCheater[] = new boolean[NUM_QUESTIONS];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        if (savedInstanceState == null) {
            Log.d(TAG, "onCreate:savedInstanceState is null");
        } else {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIsCheater = savedInstanceState.getBooleanArray(KEY_IS_CHEATER);
            Log.d(TAG, "onCreate:savedInstanceState is not null,mCurrentIndex=" + mCurrentIndex
                    + ",mIsCheater=" + mIsCheater);
        }
        setContentView(R.layout.activity_quiz);

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);

        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);
        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);
        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mNextImageButton = (ImageButton) findViewById(R.id.next_image_button);
        mPrevImageButton = (ImageButton) findViewById(R.id.prev_image_button);

        mQuestionTextView.setOnClickListener(this);
        mTrueButton.setOnClickListener(this);
        mFalseButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
        mPrevButton.setOnClickListener(this);
        mCheatButton.setOnClickListener(this);
        mNextImageButton.setOnClickListener(this);
        mPrevImageButton.setOnClickListener(this);
        updateQuestion();
        initButtonState();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult,requestCode=" + requestCode + ",resultCode=" + resultCode + ",intent=" + data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater[mCurrentIndex] = CheatActivity.wasAnswerShown(data);
            Log.d(TAG, "mIsCheater=" + mIsCheater[mCurrentIndex]);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG, "onSaveInstanceState is called");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBooleanArray(KEY_IS_CHEATER, mIsCheater);
    }

    private void updateQuestion() {
        mQuestionTextView.setText(mQuestionBank[mCurrentIndex]
                .getTextResId());
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;

        if (mIsCheater[mCurrentIndex]) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }

        Toast.makeText(QuizActivity.this, messageResId,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.true_button:
                checkAnswer(true);
                break;
            case R.id.false_button:
                checkAnswer(false);
                break;
            case R.id.next_button:
            case R.id.question_text_view:
            case R.id.next_image_button:
                updateCurrentIndex(true);
                resetIsCheaterCurrentIndex();
                updateQuestion();
                break;
            case R.id.prev_button:
            case R.id.prev_image_button:
                updateCurrentIndex(false);
                resetIsCheaterCurrentIndex();
                updateQuestion();
                break;
            case R.id.cheat_button:
                Intent i = CheatActivity.newIntent(QuizActivity.this,
                        mQuestionBank[mCurrentIndex].isAnswerTrue());
                startActivityForResult(i, REQUEST_CODE_CHEAT);
                break;
            default:
                Log.e(TAG, "unknown view ID");
                break;
        }

    }

    private void updateCurrentIndex(boolean isNext) {
        if (isNext == true) {
            //Goto next question
            if (mCurrentIndex < mQuestionBank.length - 1) {
                mCurrentIndex++;
                setButtonEnabled(false, true);
                if (mCurrentIndex == mQuestionBank.length - 1) {
                    setButtonEnabled(true, false);
                }
            }
        } else {
            //Goto previous question
            if (mCurrentIndex > 0) {
                mCurrentIndex--;
                setButtonEnabled(true, true);
                if (mCurrentIndex == 0) {
                    setButtonEnabled(false, false);
                }
            }
        }
    }

    private void resetIsCheaterCurrentIndex() {
        if (mIsCheater[mCurrentIndex] == false) {
            mIsCheater[mCurrentIndex] = false;
        }
    }

    private void initButtonState() {
        if (mCurrentIndex == 0) {
            setButtonEnabled(false, false);
        } else if (mCurrentIndex == mQuestionBank.length - 1) {
            setButtonEnabled(true, false);
        }
    }

    private void setButtonEnabled(boolean next, boolean enabled) {
        if (next) {
            mNextButton.setEnabled(enabled);
            mNextImageButton.setEnabled(enabled);
        } else {
            mPrevButton.setEnabled(enabled);
            mPrevImageButton.setEnabled(enabled);
        }
    }
}
