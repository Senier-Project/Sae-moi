package com.three_eung.saemoi;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;

/**
 * Created by CH on 2018-02-21.
 */

public class InputDialog extends DialogFragment implements View.OnClickListener {
    private int mYear=0, mMonth=0, mDay=0;
    private String pickedDate;
    private Button dateButton;
    private DatePickerDialog datePickerDialog;
    private Spinner inoutSpinner;
    private EditText valueText;
    private DialogListener mDialogListener;
    private InfoListener mListener;

    public static InputDialog newInstance(InfoListener mListener) {
        InputDialog inputDialog = new InputDialog();
        inputDialog.mListener = mListener;

        return inputDialog;
    }

    public interface InfoListener {
        void onDataInputComplete(EventInfo eventInfo);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View mView = inflater.inflate(R.layout.fragment_inout, null);

        Bundle data = getArguments();
        mYear = data.getInt("year");
        mMonth = data.getInt("month");
        mDay = data.getInt("day");

        Calendar c = Calendar.getInstance();
        if(mYear == 0)
            mYear = c.get(Calendar.YEAR);
        if(mMonth == 0)
            mMonth = c.get(Calendar.MONTH);
        if(mDay == 0)
            mDay = c.get(Calendar.DATE);

        DatePickerDialog.OnDateSetListener mListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mYear = year;
                mMonth = month;
                mDay = day;
                setDate();
            }
        };

        inoutSpinner = (Spinner)mView.findViewById(R.id.inoutSpinner);
        dateButton = (Button)mView.findViewById(R.id.dateButton);
        valueText = (EditText)mView.findViewById(R.id.valueEt);

        setDate();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.inout_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inoutSpinner.setAdapter(adapter);

        datePickerDialog = new DatePickerDialog(this.getContext(), mListener, mYear, mMonth, mDay);
        dateButton.setOnClickListener(this);

        builder.setView(mView)
                .setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setValue();
                    }
                }).setNegativeButton("취소", null);

        Dialog dialog = builder.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    private void setDate() {
        StringBuffer buffer = new StringBuffer();

        buffer.append(mYear);
        buffer.append("/");
        buffer.append(mMonth+1);
        buffer.append("/");
        buffer.append(mDay);

        pickedDate = buffer.toString();

        dateButton.setText(pickedDate);
    }

    private void setValue() {
        String inout = null;

        switch(inoutSpinner.getSelectedItem().toString()) {
            case "수입":
                inout = "income";
                break;
            case "지출":
                inout = "outcome";
                break;
        }

        StringBuffer buffer = new StringBuffer();
        buffer.append(mYear);
        buffer.append(mMonth+1);
        String yearmonth = buffer.toString();
        EventInfo eventInfo = new EventInfo(inout, yearmonth, String.valueOf(mDay), Integer.parseInt(valueText.getText().toString()));
        mListener.onDataInputComplete(eventInfo);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.dateButton:
                datePickerDialog.show();
        }
    }

    public interface DialogListener {
        void onDialogClick(Bundle arg);
    }

    public void setDialogListener(DialogListener mDialogListener) {
        this.mDialogListener = mDialogListener;
    }
}
