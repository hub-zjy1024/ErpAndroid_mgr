package com.b1b.js.erpandroid_mgr.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.b1b.js.erpandroid_mgr.R;

import java.util.Calendar;

/**
 Created by 张建宇 on 2017/3/31. */

public class MyDateAndTimePickerDialog extends Dialog implements DialogInterface.OnClickListener, DatePicker.OnDateChangedListener {
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";

    private final DatePicker mDatePicker;

    private MyDateAndTimePickerDialog.OnDateSetListener mDateSetListener;

    /**
     Creates a new date picker dialog for the current date using the parent
     context's default date picker dialog theme.
     @param context the parent context
     */
    public MyDateAndTimePickerDialog(@NonNull Context context) {
        this(context, 0, null, Calendar.getInstance(), -1, -1, -1);
    }

    /**
     Creates a new date picker dialog for the current date.
     @param context    the parent context
     @param themeResId the resource ID of the theme against which to inflate
     this dialog, or {@code 0} to use the parent
     {@code context}'s default alert dialog theme
     */
    public MyDateAndTimePickerDialog(@NonNull Context context, @StyleRes int themeResId) {
        this(context, themeResId, null, Calendar.getInstance(), -1, -1, -1);
    }

    /**
     Creates a new date picker dialog for the specified date using the parent
     context's default date picker dialog theme.
     @param context    the parent context
     @param listener   the listener to call when the user sets the date
     @param year       the initially selected year
     @param month      the initially selected month (0-11 for compatibility with
     {@link Calendar#MONTH})
     @param dayOfMonth the initially selected day of month (1-31, depending
     on month)
     */
    public MyDateAndTimePickerDialog(@NonNull Context context, @Nullable MyDateAndTimePickerDialog.OnDateSetListener listener,
                                     int year, int month, int dayOfMonth) {
        this(context, 0, listener, null, year, month, dayOfMonth);
    }

    /**
     Creates a new date picker dialog for the specified date.
     @param context     the parent context
     @param themeResId  the resource ID of the theme against which to inflate
     this dialog, or {@code 0} to use the parent
     {@code context}'s default alert dialog theme
     @param listener    the listener to call when the user sets the date
     @param year        the initially selected year
     @param monthOfYear the initially selected month of the year (0-11 for
     compatibility with {@link Calendar#MONTH})
     @param dayOfMonth  the initially selected day of month (1-31, depending
     on month)
     */
    public MyDateAndTimePickerDialog(@NonNull Context context, @StyleRes int themeResId,
                                     @Nullable MyDateAndTimePickerDialog.OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
        this(context, themeResId, listener, null, year, monthOfYear, dayOfMonth);
    }

    private MyDateAndTimePickerDialog(@NonNull Context context, @StyleRes int themeResId,
                                      @Nullable MyDateAndTimePickerDialog.OnDateSetListener listener, @Nullable Calendar calendar, int year,
                                      int monthOfYear, int dayOfMonth) {
        super(context, resolveDialogTheme(context, themeResId));

        final Context themeContext = getContext();
        final LayoutInflater inflater = LayoutInflater.from(themeContext);
        final View view = inflater.inflate(R.layout.date_and_time_layout, null);
        setContentView(view);
        Button btnOk = (Button) view.findViewById(R.id.date_time_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDateSetListener != null) {
                    mDatePicker.clearFocus();
                    mDateSetListener.onDateSet(mDatePicker, mDatePicker.getYear(),
                            mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
                }
                cancel();
            }
        });
        Button btnCancel = (Button) view.findViewById(R.id.date_time_cancle);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        if (calendar != null) {
            year = calendar.get(Calendar.YEAR);
            monthOfYear = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        }
        mDatePicker = (DatePicker) view.findViewById(R.id.date_time_datepicker);
        mDatePicker.init(year, monthOfYear, dayOfMonth, this);
        //        mDatePicker.setValidationCallback(mValidationCallback);
        mDateSetListener = listener;
    }

    static
    @StyleRes
    int resolveDialogTheme(@NonNull Context context, @StyleRes int themeResId) {
        if (themeResId == 0) {
            final TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.alertDialogTheme, outValue, true);
            return outValue.resourceId;
        } else {
            return themeResId;
        }
    }

    @Override
    public void onDateChanged(@NonNull DatePicker view, int year, int month, int dayOfMonth) {
        mDatePicker.init(year, month, dayOfMonth, this);
    }

    /**
     Sets the listener to call when the user sets the date.
     @param listener the listener to call when the user sets the date
     */
    public void setOnDateSetListener(@Nullable MyDateAndTimePickerDialog.OnDateSetListener listener) {
        mDateSetListener = listener;
    }


    /**
     Returns the {@link DatePicker} contained in this dialog.
     @return the date picker
     */
    @NonNull
    public DatePicker getDatePicker() {
        return mDatePicker;
    }

    /**
     Sets the current date.
     @param year       the year
     @param month      the month (0-11 for compatibility with
     {@link Calendar#MONTH})
     @param dayOfMonth the day of month (1-31, depending on month)
     */
    public void updateDate(int year, int month, int dayOfMonth) {
        mDatePicker.updateDate(year, month, dayOfMonth);
    }

    @Override
    public Bundle onSaveInstanceState() {
        final Bundle state = super.onSaveInstanceState();
        state.putInt(YEAR, mDatePicker.getYear());
        state.putInt(MONTH, mDatePicker.getMonth());
        state.putInt(DAY, mDatePicker.getDayOfMonth());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int year = savedInstanceState.getInt(YEAR);
        final int month = savedInstanceState.getInt(MONTH);
        final int day = savedInstanceState.getInt(DAY);
        mDatePicker.init(year, month, day, this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    //    private final ValidationCallback mValidationCallback = new ValidationCallback() {
    //        @Override
    //        public void onValidationChanged(boolean valid) {
    //            final Button positive = getButton(BUTTON_POSITIVE);
    //            if (positive != null) {
    //                positive.setEnabled(valid);
    //            }
    //        }
    //    };

    /**
     The listener used to indicate the user has finished selecting a date.
     */
    public interface OnDateSetListener {
        /**
         @param view       the picker associated with the dialog
         @param year       the selected year
         @param month      the selected month (0-11 for compatibility with
         {@link Calendar#MONTH})
         @param dayOfMonth th selected day of the month (1-31, depending on
         month)
         */
        void onDateSet(DatePicker view, int year, int month, int dayOfMonth);
    }
}
