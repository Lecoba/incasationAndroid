package com.example.user.incasation.http;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.user.incasation.ListActivity;
import com.example.user.incasation.LoginActivity;
import com.example.user.incasation.MainActivity;
import com.example.user.incasation.NavigationActivity;
import com.example.user.incasation.R;
import com.example.user.incasation.adapter.TransactionAdapter;
import com.example.user.incasation.domain.Transaction;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@SuppressWarnings(value = "all")
public class OkHttpHandler extends AsyncTask {

    private Object domainObject;
    private Context context;
    private String[] loginParams;
    private OkHttpClient client = createOkHttpClient();
    private static final Gson gson = new Gson();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public OkHttpHandler(Context context, Object domainObject) {
        this.domainObject = domainObject;
        this.context = context;
    }

    public OkHttpHandler(Context context, String[] loginParams) {
        this.loginParams = loginParams;
        this.context = context;
    }

    public OkHttpHandler(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Object[] params) {
        String url = String.valueOf(params[0]);
        String response = null;

        try {
            if (context.getClass() == MainActivity.class) {
                response = domainObject != null ? doPostTransaction(url) : doLoadRecources(url);
            } else if (context.getClass() == ListActivity.class) {
                response = doGetTransactionList(url);
            } else if (context.getClass() == LoginActivity.class) {
                response = doPostAuth(url);
            }
        } catch (IOException e) {
            //todo add server connection issue messages
        }

        return response;
    }


    private String doPostAuth(String url) throws IOException {
        String stringResponse = null;
        RequestBody body = new FormBody.Builder()
                .add("login", loginParams[0])
                .add("pass", loginParams[1])
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        stringResponse = response.body().string();

        JsonElement element = gson.fromJson(stringResponse, JsonElement.class);
        String isValidLogin = ((JsonObject) element).get("auth").getAsString();

        return isValidLogin;
    }

    private String doPostTransaction(String url) throws IOException {
        String stringResponse = null;
        String json = convertObjectToJson(domainObject);
        if (url != null && json != null) {
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Username", getUserNameFromPrefs())
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            stringResponse = response.body().string();
            if (stringResponse.equals("Account is Expired")) {
                return null;
            }
        }
        return stringResponse;
    }

    private String doGetTransactionList(String url) throws IOException {
        String stringResponse = null;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Username", getUserNameFromPrefs())
                .get()
                .build();


        Response response = client.newCall(request).execute();
        stringResponse = response.body().string();

        if (stringResponse.equals("Account is Expired")) {
            return null;
        }

        return stringResponse;
    }

    private String doLoadRecources(String url) throws IOException {
        String stringResponse = null;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Username", getUserNameFromPrefs())
                .get()
                .build();


        Response response = client.newCall(request).execute();
        stringResponse = response.body().string();

        if (stringResponse.equals("Account is Expired")) {
            return null;
        }

        return stringResponse;
    }

    private String convertObjectToJson(Object domainObject) {
        return gson.toJson(domainObject);
    }

    @Override
    protected void onPostExecute(Object result) {
        View rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);

        if (context.getClass() == ListActivity.class) {
            ProgressBar progressBar = rootView.findViewById(R.id.progressBar);
            RecyclerView recycleView = rootView.findViewById(R.id.recycleView);

            if (result != null) {
                fillListActivityAfterLoad(result, recycleView);
                hideProgressBar(progressBar);
            } else {
                showServerNotResponsibleAlertDialog();
                hideProgressBar(progressBar);
            }

        } else if (context.getClass() == MainActivity.class) {
            ProgressBar progressBar = rootView.findViewById(R.id.progressBar);
            hideProgressBar(progressBar);
            String alertDialogMessage = "";
            if(domainObject == null) {
                addResourcesToPrefs(result);
                alertDialogMessage = context.getResources().getString(R.string.successResponseLoadResources);
            } else{
                alertDialogMessage = context.getResources().getString(R.string.successResponse);
            }

            if (result != null) {
                showSucecessOperationAlertDialog(alertDialogMessage);
            } else {
                showServerNotResponsibleAlertDialog();
            }
        } else if (context.getClass() == LoginActivity.class) {
            final ProgressBar mProgressView = rootView.findViewById(R.id.login_progress);
            final View mLoginFormView = rootView.findViewById(R.id.login_pass_form);

            boolean isServerResponsible = result != null;
            if (isServerResponsible) {
                boolean isValidLogin = Boolean.valueOf(result.toString());

                if (isValidLogin) {
                    storeUserNameInPrefs();
                    startNavigationActivity();
                    closeActivity();
                } else {
                    Toast toast = Toast.makeText(context, "Ձեր մուտքագրած տվյալները սխալ են", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 300);
                    toast.show();
                    hideLoginProgressBar(mProgressView, mLoginFormView);
                }
            } else {
                hideLoginProgressBar(mProgressView, mLoginFormView);
                showServerNotResponsibleAlertDialog();
            }
        }
    }

    private void fillListActivityAfterLoad(Object result, RecyclerView recyclerView) {
        List<Transaction> transactionList = null;
        if (result != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Transaction>>() {
            }.getType();

            transactionList = gson.fromJson(result.toString(), type);

            RecyclerView.Adapter adapter = new TransactionAdapter(transactionList, context);
            recyclerView.setAdapter(adapter);
        }
    }

    private void addResourcesToPrefs(Object result) {
        SharedPreferences preferences = context.getSharedPreferences("Assets", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.putString("bank", result.toString());
        prefsEditor.apply();
    }

    private String getUserNameFromPrefs() {
        SharedPreferences preferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        return preferences.getString("username", "");
    }

    private void hideLoginProgressBar(final ProgressBar mProgressView, final View mLoginFormView) {
        int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(View.VISIBLE);
            }
        });

        hideProgressBar(mProgressView);
        mProgressView.animate().setDuration(shortAnimTime).alpha(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                hideProgressBar(mProgressView);
                ;
            }
        });
    }

    private void hideProgressBar(ProgressBar progressBar) {
        progressBar.setVisibility(View.GONE);
    }

    private void showServerNotResponsibleAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.failResponse)
                .setTitle("ՀԱՅԻՆԿԱՍԱՑԻԱ")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Շարունակել", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();
    }

    private void showSucecessOperationAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle("ՀԱՅԻՆԿԱՍԱՑԻԱ")
                .setIcon(android.R.drawable.ic_dialog_dialer)
                .setPositiveButton("Շարունակել", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        closeActivity();
                    }
                })
                .show();
    }

    private void closeActivity() {
        ((Activity) context).finish();
    }

    private OkHttpClient createOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    private void storeUserNameInPrefs() {
        SharedPreferences sharedPref = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", loginParams[0]);
        editor.apply();
    }

    private void startNavigationActivity() {
        Intent intent = new Intent(context, NavigationActivity.class);
        context.startActivity(intent);
    }
}
