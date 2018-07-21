package com.example.user.incasation.utils;

import android.util.Log;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IncasationUtils {

    public static final String MANDATORY = "Դաշտը պարտադիր է լրացման համար";
    public static final String DROPDOWN_VALID = "Դաշտը ընտրել ներկայացված ցուցակից";

    public static final String BANK = "bank";
    public static final String BANK_BRANCH = "bankBranch";
    public static final String CURRENCY = "currency";

    public static final Set<String> EMPTY_SET = new HashSet<>();

    public static boolean isFieldContentEmpty(TextView textView) {
        if (convertInputToString(textView).isEmpty()) {
            textView.setError(MANDATORY);
            return true;
        }
        return false;
    }

    public static boolean isDropdownFieldValid(List dropdownList, TextView textView) {
        String inputText = convertInputToString(textView);
        if (!inputText.isEmpty() && !dropdownList.contains(inputText)) {
            textView.setError(DROPDOWN_VALID);
            return false;
        }
        return true;
    }

    public static Date convertInputToDate(TextView textView) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); // Make sure user insert date into edittext in this format.
        try {
            String inputDate = (textView.getText().toString());
            Date dateObject = formatter.parse(inputDate);
            return dateObject;
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i("E11111111111", e.toString());
        }
        return null;
    }

    public static String convertInputToString(TextView textView) {
        return textView.getText().toString();
    }

    public static List createListFromArray(Object[] array) {
        return new ArrayList<>(Arrays.asList(array));
    }
}
