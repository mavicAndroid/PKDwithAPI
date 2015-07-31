package com.example.aleksey.pkdwithapi;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

/**
 * Created by Aleksey on 19.06.15.
 */
public class PhoneInput extends MainActivity {


    //константы получаемые с маин активити
    String ID, Name;
    DialogFragment dlg1, dlg2;
    String phone = "+7";
    CustomKeyboard mCustomKeyboard;
    private Handler mHandler;
    long timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //свойства окна для киоск мода
        mDecorView = getWindow().getDecorView();

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        setContentView(R.layout.phone_input);

        //получаем id и имя очереди из маин активити
        Intent intent = getIntent();
        ID = intent.getStringExtra("id");
        Name = intent.getStringExtra("name");

        //TextView text = (TextView)findViewById(R.id.textView);
        //text.append(" ");
        //rfcnjvyfz rkfdbfnehf
        mCustomKeyboard= new CustomKeyboard(this, R.id.keyboardview, R.xml.keyboard );
        //Button button = (Button)findViewById(R.id.OKButon);
        //final EditText phtxt = (EditText)findViewById(R.id.edittext);
        BlockedSelectionEditText phtxt = (BlockedSelectionEditText)findViewById(R.id.edittext);
        //проверка поля ввода, если Б 10 символов кнопка ввода неактивна
        phtxt.addTextChangedListener(mTextWatcher);
        phtxt.setClickable(false);
        phtxt.setTextIsSelectable(false);
        ///
        /*phtxt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                phtxt.setSelection(phtxt.getText().length());
                Toast.makeText(PhoneInput.this, "EitText Touch", Toast.LENGTH_LONG).show();
                return false;
            }
        });*/
        //боремся с копировать вставить



        phtxt.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        mCustomKeyboard.registerEditText(R.id.edittext);
    //
        //EnterListener();
        dlg1 = new dialog();
        dlg2 = new errordialog();
        //isempty();
        /*View rootV = (View)findViewById(R.id.root);
        rootV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                //gesture detector to detect swipe.
                Toast.makeText(PhoneInput.this, "Touch", Toast.LENGTH_SHORT).show();
                return false;//always return true to consume event
            }
        });*/
        timer = System.currentTimeMillis();
        mHandler = new Handler();
        startRepeatingTask();
    }

    Runnable mStatusCheker = new Runnable() {
        @Override
        public void run() {
            if ((System.currentTimeMillis() - timer) >= 10000) {
                //Toast.makeText(PhoneInput.this, " " + (System.currentTimeMillis() - timer), Toast.LENGTH_SHORT).show();
                onCancelClick();
                stopRepeatingTask();
            } else {
                mHandler.postDelayed(mStatusCheker, 11000);
            }
        }
    };
    void startRepeatingTask(){
        mStatusCheker.run();
    }
    void stopRepeatingTask(){
        mHandler.removeCallbacks(mStatusCheker);
    }
    /*
    public void EnterListener(){
        BlockedSelectionEditText phtxt = (BlockedSelectionEditText)findViewById(R.id.edittext);
        phtxt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Toast.makeText(PhoneInput.this, "KEY CODE", Toast.LENGTH_SHORT).show();
                    onOKClick();
                    return true;
                }
                return false;
            }
        } );
    }*/

    //проверка на полноту ввода телефона, заменяем 89 вначале на 9
private TextWatcher mTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        BlockedSelectionEditText phtxt = (BlockedSelectionEditText) findViewById(R.id.edittext);
        if (s.toString().equals("89")) {
            phtxt.setText("9");
            phtxt.setSelection(1);
        }
        if ((s.length()==2)&&(s.charAt(0)=='7')) {
            String m = s.toString();
            //phtxt.setText(s, 0, 1);
            phtxt.setText(m.substring(1));
            phtxt.setSelection(1);
            //Toast.makeText(PhoneInput.this, "Text "+s.charAt(1),Toast.LENGTH_LONG).show();
        }
        if (s.toString().equals("88")) {
            phtxt.setText("8");
            phtxt.setSelection(1);
        }
        timer = System.currentTimeMillis();
        if (s.length() >= 1) {
            stopRepeatingTask();

            startRepeatingTask();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().length() == 10){
            onOKClick();
        }
    //isempty();
    }
};
    //void isempty(){
        //Button button = (Button)findViewById(R.id.OKButon);
    //    BlockedSelectionEditText phtxt = (BlockedSelectionEditText)findViewById(R.id.edittext);
        //if(phtxt.getText().length()<10){button.setClickable(false);}else{button.setClickable(true);}
    //}

        ///по вводу делаем запрос на сервер
    public void onOKClick() {

        //Button button = (Button)findViewById(R.id.OKButon);
        //отклбчаем возможность двойного нажатия подтверждения
        //button.setClickable(false);
        URL url = null;
        try {
            url = new URL("http://frontend.queue.petrocrypt.local:80/api/organizations/"+orgid+"/departments/"+depid+"/queue/isMobilePhoneNotBinded");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        BlockedSelectionEditText phtxt = (BlockedSelectionEditText)findViewById(R.id.edittext);
        phone ="+7"+ phtxt.getText();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("organizationsId", orgid));
        nameValuePairs.add(new BasicNameValuePair("departmentsId", depid));
        nameValuePairs.add(new BasicNameValuePair("mobilePhone", phone));

        HttpClient client = new DefaultHttpClient();

        HttpPost post = new HttpPost(url.toString());

        try {
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = client.execute(post);

            int status = response.getStatusLine().getStatusCode();

            String responseBody = EntityUtils.toString(response.getEntity());

            JSONObject jsonObject = new JSONObject(responseBody);


            //if (jsonObject.getString("result") != "true") {
            //    Toast.makeText(this, "Номер есть в очереди", Toast.LENGTH_LONG).show();
            //} else {
            InQueue(ID);
            //}
            Log.e(responseBody, "resp");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //запрос для записи телефона в очередь
    private void InQueue(String ID) {

        URL url = null;
        try {
            url = new URL("http://frontend.queue.petrocrypt.local:80/api/organizations/"+orgid+"/departments/"+depid+"/queue");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("organizationsId", orgid));
        nameValuePairs.add(new BasicNameValuePair("departmentsId", depid));
        nameValuePairs.add(new BasicNameValuePair("mobilePhone", phone));
        nameValuePairs.add(new BasicNameValuePair("queueId", ID));

        HttpClient client = new DefaultHttpClient();

        HttpPost post = new HttpPost(url.toString());

        try {
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = client.execute(post);

            int status = response.getStatusLine().getStatusCode();

            String responseBody = EntityUtils.toString(response.getEntity());

            JSONObject jsonObject = new JSONObject(responseBody);


            //Toast toast = null;
            //View toastView = toast.getView();
            if (jsonObject.getString("result") == "true") {
                //Toast.makeText(this, jsonObject.getString("code"), Toast.LENGTH_LONG).show();

                Bundle args = new Bundle();
                args.putString("code", jsonObject.getString("code"));
                args.putString("line", Name);
                dlg1.setArguments(args);
                dlg1.show(getFragmentManager(), "dlg1");

                //finish();
            }else {
                if (jsonObject.getString("reason").contains("validation.phone")){
                    Bundle args = new Bundle();
                    args.putString("ErrorText", "validation");
                    dlg2.setArguments(args);
                    dlg2.show(getFragmentManager(), "dlg2");
                        }

                else{
                    Bundle args = new Bundle();
                    args.putString("ErrorText", "notinqueue");
                    dlg2.setArguments(args);
                    dlg2.show(getFragmentManager(), "dlg2");
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //finish();
    }
    //при нажатии кнопки отмены закрываем активности и переходим к главной
    public void onCancelClick()
    {
        finish();
    }
    //киоск мод
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus) {
            // Close every kind of system dialog
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                try
                {
                    String command;
                    command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib am startservice -n com.android.systemui/.SystemUIService";
                    Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
                    proc.waitFor();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                Intent intent = new Intent(PhoneInput.this, Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                try
                {
                    String command;
                    command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib am startservice -n com.android.systemui/.SystemUIService";
                    Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
                    proc.waitFor();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                Intent intent1 = new Intent(PhoneInput.this, Activity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    View.OnLongClickListener mOnLOngClickListener = new View.OnLongClickListener(){
        @Override
        public boolean onLongClick(View v){
            return true;
        }
    };
}



