package com.example.aleksey.pkdwithapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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
    public final String orgid = "1";
    public final String depid = "1";
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
        if (r != 200)
            {errbuilder.setMessage("response code =" + r); AlertDialog alert = errbuilder.create(); alert.show();};


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


    }
    //создаем кнопки
    private void createButtons(int Size) throws IOException {
        int mrow=0;
        int mcol=0;
        int buttonID = 0;
        if (Size == 3){mrow = 0; mcol = 2;} else{mrow=0;mcol=3;}
        //создаем таблицу под кнопки в зависимости от их кол-ва
        TableLayout table = (TableLayout) findViewById(R.id.tableForButtons);
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


                URL url = new URL(queueList.get(col).get(TAG_IMAGEURL));

                InputStream is = url.openConnection().getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);

                if (Size == 3){bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);}
                    else {bitmap = Bitmap.createScaledBitmap(bitmap, 170, 200, false);}


                Button.setImageBitmap(bitmap);
                Button.setBackgroundColor(12);
                //imageButton.setPadding


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

}