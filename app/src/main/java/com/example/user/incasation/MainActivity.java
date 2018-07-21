package com.example.user.incasation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.user.incasation.domain.Bank;
import com.example.user.incasation.domain.BankBranch;
import com.example.user.incasation.domain.Transaction;
import com.example.user.incasation.http.OkHttpHandler;
import com.example.user.incasation.utils.IncasationUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.user.incasation.utils.IncasationUtils.BANK;
import static com.example.user.incasation.utils.IncasationUtils.BANK_BRANCH;
import static com.example.user.incasation.utils.IncasationUtils.convertInputToString;
import static com.example.user.incasation.utils.IncasationUtils.createListFromArray;
import static com.example.user.incasation.utils.IncasationUtils.isDropdownFieldValid;
import static com.example.user.incasation.utils.IncasationUtils.isFieldContentEmpty;

public class MainActivity extends AppCompatActivity {

    private EditText user;
    private EditText amount;
    private EditText currency;
    private EditText destinationBank;
    private EditText destinationBankBranch;

    private ProgressBar progressBar;

    String[] banks;
    String[] bankBranches;
    String[] currencies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initActivityFields();
        initDropdownResources();
        fillBankAndCurrencyDropdownList();
        setLoggedUserName();
        addChangeListenerToBankField();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        progressBar.setVisibility(View.VISIBLE);
        OkHttpHandler okHttpHandler = new OkHttpHandler(this);
        okHttpHandler.execute(getString(R.string.apiGetResURL));
        return true;
    }

    public void sendData(View view) {
        if (hasAnyValidationErrors()) {
            return;
        }

        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.sendDataApproval)
                .setMessage(R.string.sendDataApprovalQuestion)
                .setIcon(android.R.drawable.ic_menu_save)
                .setPositiveButton("Այո", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        createAndPostTransaction();
                    }
                })
                .setNegativeButton("Ոչ", null)
                .show();
    }

    private void initActivityFields() {
        user = findViewById(R.id.user);
        amount = findViewById(R.id.amount);
        currency = findViewById(R.id.currency);
        destinationBank = findViewById(R.id.destinationBank);
        destinationBankBranch = findViewById(R.id.destinationBankBranch);

        progressBar = findViewById(R.id.progressBar);
    }

    private void initDropdownResources() {
        SharedPreferences preferences = getSharedPreferences("Assets", Context.MODE_PRIVATE);
        banks = getBankResourceFromPrefs(IncasationUtils.BANK, preferences, null);
//        bankBranches = getBankResourceFromPrefs(IncasationUtils.BANK_BRANCH, preferences);
        bankBranches = new String[0];
        currencies = getResources().getStringArray(R.array.currencyArray);
    }

    private String[] getBankResourceFromPrefs(String assetName, SharedPreferences preferences, String bankNameForBranch) {

        List<Bank> bankAndBranchList = getBankAndBranchListFromPrefs(preferences);
        BankBranch[] bankBranches;
        List<String> bankNames;
        List<String> bankBranchNames;

        switch (assetName) {
            case BANK:
                bankNames = new ArrayList<>();
                for (Bank bank : bankAndBranchList) {
                    bankNames.add(bank.getName());
                }
                return bankNames.toArray(new String[bankNames.size()]);
            case BANK_BRANCH:
                bankBranches = new BankBranch[0];
                bankBranchNames = new ArrayList<>();
                for (Bank bank : bankAndBranchList) {
                    if (bank.getName().equals(bankNameForBranch)) {
                        bankBranches = bank.getBankBranches();
                        break;
                    }
                }

                for (BankBranch bankBranch : bankBranches) {
                    bankBranchNames.add(bankBranch.getName());
                }
                return bankBranchNames.toArray(new String[bankBranchNames.size()]);
        }

        return null;
    }

    private List<Bank> getBankAndBranchListFromPrefs(SharedPreferences preferences) {
        String banksJson = preferences.getString(IncasationUtils.BANK, "");
        if (!banksJson.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Bank>>() {
            }.getType();

            return gson.fromJson(banksJson, type);
        }
        return new ArrayList<>();
    }

    private void fillBankAndCurrencyDropdownList() {
        //Create Array Adapter
        ArrayAdapter<String> bankAdapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, banks);
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, currencies);
        //Find TextView control
        AutoCompleteTextView bankDropDown = findViewById(R.id.destinationBank);
        AutoCompleteTextView currencyDropDown = findViewById(R.id.currency);
        //Set the number of characters the user must type before the drop down list is shown
        bankDropDown.setThreshold(1);
        currencyDropDown.setThreshold(1);
        //Set the adapter
        bankDropDown.setAdapter(bankAdapter);
        currencyDropDown.setAdapter(currencyAdapter);
    }

    private void fillBankBranchDropdownList() {
        ArrayAdapter<String> bankBranchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, bankBranches);
        AutoCompleteTextView bankBranchDropDown = findViewById(R.id.destinationBankBranch);
        bankBranchDropDown.setThreshold(1);
        bankBranchDropDown.setAdapter(bankBranchAdapter);
    }

    private void setLoggedUserName() {
        SharedPreferences preferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        String loggedUserName = preferences.getString("username", "!!!!!!!!");
        user.setText(loggedUserName);
        user.setFocusable(false);
    }

    private boolean hasAnyValidationErrors() {
        return isNotBankAndBranchesValid()
                | isFieldContentEmpty(user)
                | isFieldContentEmpty(amount)
                | isFieldContentEmpty(currency)
                | isFieldContentEmpty(destinationBank)
                | isFieldContentEmpty(destinationBankBranch);
    }

    private boolean isNotBankAndBranchesValid() {
        List bankList = createListFromArray(banks);
        List bankBranchList = createListFromArray(bankBranches);
        List currencyList = createListFromArray(currencies);

        return !isDropdownFieldValid(bankList, destinationBank) | !isDropdownFieldValid(bankBranchList, destinationBankBranch) | !isDropdownFieldValid(currencyList, currency);
    }

    private void createAndPostTransaction() {
        progressBar.setVisibility(View.VISIBLE);

        Transaction transaction = Transaction.createTransactionWithoutDateParams(
                convertInputToString(user),
                convertInputToString(amount),
                convertInputToString(currency),
                convertInputToString(destinationBank),
                convertInputToString(destinationBankBranch));

        OkHttpHandler okHttpHandler = new OkHttpHandler(this, transaction);
        okHttpHandler.execute(getString(R.string.apiPostTransactionURL));
    }

    private void addChangeListenerToBankField() {
        destinationBank.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    destinationBankBranch.setFocusable(true);
                } else {
                    destinationBankBranch.setFocusable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    SharedPreferences preferences = getSharedPreferences("Assets", Context.MODE_PRIVATE);
                    bankBranches = getBankResourceFromPrefs(IncasationUtils.BANK_BRANCH, preferences, editable.toString());
                    fillBankBranchDropdownList();
                }
            }
        });
    }

}
