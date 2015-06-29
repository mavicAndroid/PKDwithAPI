package com.example.aleksey.pkdwithapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksey on 19.06.15.
 */
public class PhoneInput extends MainActivity {

    String ID;

    String phone = "+7";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_input);


        Intent intent = getIntent();
        ID = intent.getStringExtra("id");
        String Name = intent.getStringExtra("name");

        TextView text = (TextView)findViewById(R.id.textView);
        text.append(" "+Name);



    }

    public void onOKClick(View view) {

        URL url = null;
        try {
            url = new URL("http://frontend.queue.petrocrypt.local:80/api/organizations/"+orgid+"/departments/"+depid+"/queue/isMobilePhoneNotBinded");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        EditText phtxt = (EditText)findViewById(R.id.editText);
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


            if (jsonObject.getString("result") == "true") {
                Toast.makeText(this, jsonObject.getString("code"), Toast.LENGTH_LONG).show();
                finish();
            }else {
                if (jsonObject.getString("reason").contains("validation.phone")){
                Toast.makeText(this, "Номер телефона не существует", Toast.LENGTH_LONG).show();}

                else{
                    Toast.makeText(this, "Ваш номер в очереди. Ожидайте вызова.", Toast.LENGTH_LONG).show();}
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

    public void onCancelClick(View view)
    {
        finish();
    }
}