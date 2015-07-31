package com.example.aleksey.pkdwithapi;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;



public class MainActivity extends Activity {

    public String resultJson = null;
    //id организации и подразделения
    public final String orgid = "2";
    public final String depid = "3";
    View mDecorView;
    //для запроса queues
    JSONArray queues = null;
    ArrayList<HashMap<String, String>> queueList;
    public  static final String TAG_QUEUES = "queues";
    public  static final String TAG_ID = "id";
    public static  final String TAG_NAME = "name";
    public static final String TAG_IMAGEURL = "imageUrl";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //параментры отображения для киоск мода
        mDecorView = getWindow().getDecorView();

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);


        //android.provider.Settings.System.putInt(getBaseContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
        //this.getWindow().getAttributes().screenBrightness = 100/ 100.0f;
        /*WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 100/ 100.0f;
        getWindow().setAttributes(lp);*/

        setContentView(R.layout.activity_main);
        getActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //делаем запрос на сервер, чтобы узнать количество кнопок
        //для тестовой пкд
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //alert dialog если возвращается не 200 response code
        AlertDialog.Builder errbuilder = new AlertDialog.Builder(MainActivity.this);
        errbuilder.setTitle("Произошла ошибка");

        int r= GetQueuesList();
        if (r != 200) {//errbuilder.setMessage("response code =" + r); AlertDialog alert = errbuilder.create(); alert.show();};
            finish();
        }

        //парсим ответ с сервера
        queueList = new ArrayList<HashMap<String, String>>();


        try {
            JSONObject jsonObject = new JSONObject(resultJson);

            queues = jsonObject.getJSONArray(TAG_QUEUES);

            String name;
            for (int i = 0; i <= queues.length(); i++) {
                JSONObject q = queues.getJSONObject(i);

                String id = q.getString(TAG_ID);
                name = q.getString(TAG_NAME);
                String imageUrl = q.getString(TAG_IMAGEURL);

                HashMap<String, String> queue = new HashMap<String, String>();

                queue.put(TAG_ID, id);
                queue.put(TAG_NAME, name);
                queue.put(TAG_IMAGEURL, imageUrl);

                queueList.add(queue);
            }
        }catch (JSONException e)
        {e.printStackTrace();}


        try {
            createButtons(queueList.size());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//HIDE TOOLBAR
        try{
            //REQUIRES ROOT
            Build.VERSION_CODES vc = new Build.VERSION_CODES();
            Build.VERSION vr = new Build.VERSION();
            String ProcID = "79"; //HONEYCOMB AND OLDER

            //v.RELEASE  //4.0.3
            if(vr.SDK_INT >= vc.ICE_CREAM_SANDWICH){
                ProcID = "42"; //ICS AND NEWER
            }

            //REQUIRES ROOT
            Process proc = Runtime.getRuntime().exec(new String[]{"su","-c","service call activity "+ ProcID +" s16 com.android.systemui"}); //WAS 79
            //Process proc = Runtime.getRuntime().exec(new String[]{"am","startservice","-n","com.android.systemui/.SystemUIService"});
            proc.waitFor();

        }catch(Exception ex){
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    //создаем кнопки
    private void createButtons(int Size) throws IOException {
        int mrow=0;
        int mcol=0;
        int buttonID = 0;
        if (Size == 3){mrow = 0; mcol = 2;} else{mrow=1;mcol=1;}
        //создаем таблицу под кнопки в зависимости от их кол-ва
        TableLayout table = (TableLayout) findViewById(R.id.tableForButtons);
        int y=0;
        for (int row = 0; row <= mrow; row++)
        {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    1.0f));
            table.addView(tableRow);
            //добавляем кнопки
            for(int col = 0; col <= mcol; col++)
            {

                ImageButton Button = new ImageButton(this);

                Button.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT,
                        1.0f));

                /////////new block
                //пдгружаем изображения для кнопок, и применяем их
                URL url = new URL("http://admin.queue.petrocrypt.local/queueLogos/"+queueList.get(y).get(TAG_IMAGEURL));

                y++;
                InputStream is = url.openConnection().getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);

                if (Size == 3){bitmap = Bitmap.createScaledBitmap(bitmap, 260, 260, false);}
                    else {bitmap = Bitmap.createScaledBitmap(bitmap, 250, 250, false);}


                Button.setImageBitmap(bitmap);
                Button.setBackgroundColor(12);
                //imageButton.setPadding

                //обработка нажатий кнопок
                final int finalButtonID = buttonID;
                Button.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        ButtonClicked(queueList.get(finalButtonID).get(TAG_ID).toString(), queueList.get(finalButtonID).get(TAG_NAME).toString());
                    }

                });
                buttonID ++;
                tableRow.addView(Button);
            }
        }
    }
    //при нажатии кнопки переходим к новому экрану активности ввода телефона
    private void ButtonClicked(String id, String name) {
        Intent myIntent = new Intent(MainActivity.this, PhoneInput.class);
        myIntent.putExtra("id", id);
        myIntent.putExtra("name", name);
        MainActivity.this.startActivity(myIntent);
    }

    //get запрос на кол-во кнопок, название, и ссылки картинок
    public int GetQueuesList()
    {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        int resp = 0;
        try {
            URL url = new URL("http://frontend.queue.petrocrypt.local:80/api/organizations/"+orgid+"/departments/"+depid);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //
            resp = urlConnection.getResponseCode();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            resultJson = buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("mytag", "my message", e);
        }
        urlConnection.disconnect();
        return resp;
    }

    ///////киоск мод, на клавиши громкости закрывает окно и открывает настройки планшета
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

                Intent intent = new Intent(MainActivity.this, Activity.class);
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

                Intent intent1 = new Intent(MainActivity.this, Activity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}