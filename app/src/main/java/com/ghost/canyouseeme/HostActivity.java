package com.ghost.canyouseeme;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

public class HostActivity extends Activity {

    public static String userName = "";
    public static String roomJoined = "";
    private PopupWindow hostPopup;
    private Dialog hostDialog;
    private Dialog joinDialog;
    private PopupWindow joinPopup;
    private View layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
    }

    public void hostGame(View view) {
        //DISPLAY HOSTPOPUP
        launchHostPopup();
        //initiateHostPopupWindow();
        //Intent mainIntent = new Intent(HostActivity.this, MenuActivity.class);
        //HostActivity.this.startActivity(mainIntent);
    }

    private void launchHostPopup() {
        hostDialog = new Dialog(HostActivity.this);
        hostDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        hostDialog.setContentView(R.layout.host_popup);
        WindowManager.LayoutParams lp = hostDialog.getWindow().getAttributes();
        lp.dimAmount=0.3f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
        hostDialog.getWindow().setAttributes(lp);
        hostDialog.show();
        Button declineButton = (Button) hostDialog.findViewById(R.id.btn_close_host_popup);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hostDialog.dismiss();
            }
        });

        Button okayButton = (Button) hostDialog.findViewById(R.id.btn_create_room);
        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard((EditText) hostDialog.findViewById(R.id.createPassCode));
                createRoom(v);
            }
        });
    }

    private void launchJoinPopup() {
        joinDialog = new Dialog(HostActivity.this);
        joinDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        joinDialog.setContentView(R.layout.join_popup);
        WindowManager.LayoutParams lp = joinDialog.getWindow().getAttributes();
        lp.dimAmount=0.3f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
        joinDialog.getWindow().setAttributes(lp);
        joinDialog.show();
        Button declineButton = (Button) joinDialog.findViewById(R.id.btn_close_join_popup);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinDialog.dismiss();
            }
        });

        Button okayButton = (Button) joinDialog.findViewById(R.id.btn_join_room);
        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard((EditText) joinDialog.findViewById(R.id.joinPassCode));
                joinRoom(v);
            }
        });
    }

    public void joinHost(View view) {
        //DISPLAY JOINPOPUP
        launchJoinPopup();
    }

    private void closeKeyboard(EditText et) {
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    //
    public void createRoom(View view) {
        //START ASYNC TASK TO DO ALL THIS
        String roomName = ((EditText) hostDialog.findViewById(R.id.createRoomName)).getText().toString();
        String roomPass = ((EditText) hostDialog.findViewById(R.id.createPassCode)).getText().toString();
        //SELECT * FROM ROOM - if recrod < 1
        if (roomPass.equals("pass")) {
            //insert room to database, set host as player, update player room.
            //GOTO WAITING ROOM
            roomJoined = roomName;
            hostDialog.dismiss();
        } else {
            //if greater than 0, throw error.
            Toast.makeText(this.getApplicationContext(), "Unable to create room", Toast.LENGTH_SHORT).show();
        }
    }

    public void joinRoom(View view) {
        //START ASYNC TASK TO DO ALL THIS
        String roomName = ((EditText) joinDialog.findViewById(R.id.joinRoomName)).getText().toString();
        String roomPass = ((EditText) joinDialog.findViewById(R.id.joinPassCode)).getText().toString();
        //SELECT * FROM ROOM - if recrod > 1,
        if (roomPass.equals("pass")) {
            //add player to room. update player room.
            //GOTO WAITING ROOM
            roomJoined = roomName;
            Intent mainIntent = new Intent(HostActivity.this, MenuActivity.class);
            HostActivity.this.startActivity(mainIntent);
            joinDialog.dismiss();
        } else {
            Toast.makeText(this.getApplicationContext(), "Invalid Room Name / Passcode", Toast.LENGTH_SHORT).show();
        }
    }

//OBSOLETE
    private void initiateHostPopupWindow() {
        try {
            LayoutInflater inflater = (LayoutInflater) HostActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.host_popup, (ViewGroup) findViewById(R.id.popup_host));
            hostPopup = new PopupWindow(layout, 600, 870, true);
            hostPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

            Button btnClosePopup = (Button) layout.findViewById(R.id.btn_close_host_popup);
            btnClosePopup.setOnClickListener(cancel_button_host_click_listener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener cancel_button_host_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            hostPopup.dismiss();
        }
    };

    private void initiateJoinPopupWindow() {
        try {
            LayoutInflater inflater = (LayoutInflater) HostActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.join_popup, (ViewGroup) findViewById(R.id.popup_join));
            joinPopup = new PopupWindow(layout, 600, 870, true);
            joinPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

            Button btnClosePopup = (Button) layout.findViewById(R.id.btn_close_join_popup);
            btnClosePopup.setOnClickListener(cancel_button_join_click_listener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener cancel_button_join_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            joinPopup.dismiss();
        }
    };
//UNTIL HERE

}
