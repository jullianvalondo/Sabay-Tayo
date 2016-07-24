package com.entry.globelabs.hacktayo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.entry.globelabs.hacktayo.Utils.DateUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    Calendar defaultDate = Calendar.getInstance();

    @Bind(R.id.spinner_book_from)
    Spinner mBookFromSpinner;

    @Bind(R.id.spinner_book_to)
    Spinner mBookToSpinner;

    @Bind(R.id.edittext_book_date)
    EditText mDateEditText;

    @Bind(R.id.edittext_book_time)
    EditText mTimeEditText;

    @Bind(R.id.edittext_book_pax)
    EditText mPaxEditText;

    @Bind(R.id.edittext_book_notes)
    EditText mNotesEditText;

    @Bind(R.id.button_book)
    Button mBook;

    SmsManager smsManager = SmsManager.getDefault();
    String destinationNum = "21581666";

    DatePickerDialog dpd = DatePickerDialog.newInstance(this,
            defaultDate.get(Calendar.YEAR),
            defaultDate.get(Calendar.MONTH),
            defaultDate.get(Calendar.DAY_OF_MONTH));

    TimePickerDialog tpd = TimePickerDialog.newInstance(this,
            defaultDate.get(Calendar.HOUR_OF_DAY),
            defaultDate.get(Calendar.MINUTE), true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getFragmentManager().popBackStack();
        configureBookDateEditText();
        configureBookTimeEditText();
    }

    private void configureBookDateEditText() {
        mDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

//                    try {
                        dpd.show(getFragmentManager(), this.getClass().getSimpleName() + "date");
//                    } catch (IllegalStateException e) {
//                        getFragmentManager().popBackStack();
//                        //dpd.show(getFragmentManager(), this.getClass().getSimpleName());
//                    }
                }
            }
        });
    }

    @OnClick(R.id.button_book)
    public void onBookButtonClicked() {
        String message = mBookFromSpinner.getSelectedItem().toString() + "/" + mBookToSpinner.getSelectedItem().toString() + "/"
                + DateUtils.getStringFormatCustom(Calendar.getInstance().getTime(), "yyyy-MM-dd") + "/" + mTimeEditText.getText().toString() + "/"
                + mPaxEditText.getText().toString() + "/" + mNotesEditText.getText().toString();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        smsManager.sendTextMessage(destinationNum, null, message, null, null);
    }

    private void configureBookTimeEditText() {

        mTimeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                    try {
                        tpd.show(getFragmentManager(), this.getClass().getSimpleName() + "time");
//                    } catch (IllegalStateException e) {
//                        getFragmentManager().popBackStack();
//                        //tpd.show(getFragmentManager(), this.getClass().getSimpleName());
//                    }
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        String dateStr = "" + (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
        mDateEditText.setText(dateStr);
        defaultDate.set(year, monthOfYear, dayOfMonth);
        mDateEditText.clearFocus();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        defaultDate.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_WEEK, hourOfDay, minute, second);
        String time = hourOfDay + ":" + minute;
        mTimeEditText.setText(time);
        mTimeEditText.clearFocus();
    }
}
