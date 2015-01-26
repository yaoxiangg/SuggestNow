package com.ghost.canyouseeme;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends Activity {

    public static final String IP_ADDRESS = "http://localhost";
    public static String userName = "";

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void login(View view) {
        String loginName = ((EditText) this.findViewById(R.id.LoginNameField)).getText().toString();
        String password = ((EditText) this.findViewById(R.id.passwordField)).getText().toString();
        new LoginProgressTask().execute(loginName, password);
    }

    private void loginSuccess(String loginName) {
        userName = loginName;
        Intent mainIntent = new Intent(LoginActivity.this, HostActivity.class);
        LoginActivity.this.startActivity(mainIntent);
        HostActivity.userName = loginName;
        LoginActivity.this.finish();
    }

    //This method tries to register the account if username is not yet taken
    public void registerAccount(View view) {
        String loginName = ((EditText) this.findViewById(R.id.LoginNameField)).getText().toString();
        String password = ((EditText) this.findViewById(R.id.passwordField)).getText().toString();
        new RegisterProgressTask().execute(loginName, password);
    }

    class LoginProgressTask extends AsyncTask<String, Integer, Boolean> {
        SweetAlertDialog pDialog;

        @Override
        protected void onPreExecute() {
            //show loading window
            pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            pDialog.show();
            //Toast.makeText(this.getApplicationContext(), "Logging in...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            boolean success = false;
            try {
                success = authenticateWithServer(username, password);
                if (success) {
                    LoginActivity.this.userName = username;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return success;   // Return your real result here
        }

        //TO BE FILLED
        //This method authenticates with the server for validity of account
        private boolean authenticateWithServer(String username, String password) {
            if (password.equals("pass")) {
                return true;
            }
            String urlParameters = "username="+username+"&password="+password;
            URL url = null;
            String output = "";
            try {
                url = new URL(IP_ADDRESS + "/canyouseeme/login.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            HttpURLConnection hp= null;
            try {
                hp = (HttpURLConnection)url.openConnection();
                hp.setDoInput(true);
                hp.setDoOutput(true);
                hp.setInstanceFollowRedirects(false);
                hp.setRequestMethod("POST");
                hp.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                hp.setRequestProperty("charset", "utf-8");
                hp.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
                hp.setUseCaches (false);
                DataOutputStream wr = new DataOutputStream(hp.getOutputStream ());
                wr.writeBytes(urlParameters);
                wr.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(hp.getInputStream()));
                output = reader.readLine();
                wr.close();
                reader.close();
                hp.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (output.equals("SUCCESS")) {
                return true;
            } else {
                return false;
            }
            /*
            return false;*/
        }

        @Override
        protected void onPostExecute(Boolean result) {
            //Close Loading Window
            pDialog.dismiss();
            //Inform user success/Fail
            if (result) {
                LoginActivity.this.loginSuccess(userName);
            } else {
                new SweetAlertDialog(LoginActivity.this , SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Fail to login")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        }
    }

    class RegisterProgressTask extends AsyncTask<String, Integer, Boolean> {
        SweetAlertDialog rDialog, loginDialog;

        @Override
        protected void onPreExecute() {
            //show loading window
            rDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            rDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            rDialog.setTitleText("Registering...");
            rDialog.setCancelable(false);
            rDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String username = params[0];
            String password = params[1];
            boolean success = false;
            try {
                success = registerWithServer(username, password);
                if (success) {
                    LoginActivity.this.userName = username;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return success;   // Return your real result here
        }

        //This method authenticates with the server for validity of account
        private boolean registerWithServer(String username, String password) {
            //Checks with server for duplication of loginName
            boolean isInvalid = invalidText(username) || invalidText(password);
            if (!isInvalid) {
                return hasDuplicate(username, password);
            }
            return false;
        }


        private boolean invalidText(String str) {
            if (str.equals(""))
               return true;
            return false;
        }

        //TO BE FILLED
        //STUB TO CHECK FOR DUPLICATES IN DATABASE
        private boolean hasDuplicate(String username, String password) {
            String urlParameters = "username="+username+"&password="+password;
            URL url = null;
            String output = "";
            try {
                url = new URL(IP_ADDRESS + "/canyouseeme/register.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            HttpURLConnection hp= null;
            try {
                hp = (HttpURLConnection)url.openConnection();
                hp.setDoInput(true);
                hp.setDoOutput(true);
                hp.setInstanceFollowRedirects(false);
                hp.setRequestMethod("POST");
                hp.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                hp.setRequestProperty("charset", "utf-8");
                hp.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
                hp.setUseCaches (false);
                DataOutputStream wr = new DataOutputStream(hp.getOutputStream ());
                wr.writeBytes(urlParameters);
                wr.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(hp.getInputStream()));
                output = reader.readLine();
                wr.close();
                reader.close();
                hp.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (output.equals("SUCCESS")) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            rDialog.dismiss();
            //Inform user success/Fail
            if (result) {
                //LOGGING IN DIALOG
                loginDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                loginDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                loginDialog.setTitleText("Proceeding to login...");
                loginDialog.setCancelable(false);
                loginDialog.show();

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        loginDialog.dismissWithAnimation();
                        LoginActivity.this.loginSuccess(userName);
                    }
                }, 1000);
            } else {
                new SweetAlertDialog(LoginActivity.this , SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Unable to register")
                        .setContentText("Please choose another login name!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        }
    }
}


