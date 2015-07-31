package com.example.aleksey.pkdwithapi;

/**
 * Created by Aleksey on 27.07.15.
 */
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;

import static com.example.aleksey.pkdwithapi.R.xml.custompopup;

/**
 * Created by Aleksey on 25.07.15.
 */
public class dialog extends DialogFragment implements View.OnClickListener {

    final String LOG_TAG = "myLogs";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //View view = inflater.inflate(R.id.txtInfo, container, false);
        //наполняем контент окна
        String Name = getArguments().getString("line");
        String Code = getArguments().getString("code");
        //getDialog().setTitle("Вы записались в очередь: " + Name);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);
        View v = inflater.inflate(R.layout.popup, null);
        //TextView text = (TextView)findViewById(R.id.txtInfo);
        TextView txtline = (TextView) v.findViewById(R.id.txtLine);
        TextView txtinfo = (TextView) v.findViewById(R.id.txtInfo);
        TextView txtcode = (TextView) v.findViewById(R.id.txtCode);
        txtinfo.setTextSize(30);
        txtline.setTextSize(30);
        txtcode.setTextSize(30);
        txtline.append("На ваш номер телефона отпавлено СМС с информацией.");
        txtinfo.append("Ожидайте приглашение оператора по номеру: ");
        txtcode.append(Code);
        txtcode.setTextColor(Color.RED);
        //v.findViewById(R.id.confirm).setOnClickListener(this);
        new CloseThread(this).start();
        return v;
    }

    public void onClick(View v) {
        //dismiss();
        //((PhoneInput)getActivity()).finish();
    }
    public void Close(){
        dismiss();
        ((PhoneInput)getActivity()).finish();
    }


}
//Закрываем всплывающее окно с сообщением
class CloseThread extends Thread {

    private dialog dialog;

    public CloseThread(dialog dialog) {
        super();
        this.dialog = dialog;
    }

    @Override
    public void run() {
        try {
            sleep(5500);//показываем пакет в течении этого времени
            dialog.Close();
            //dialog.dismiss();
            //((PhoneInput)getActivity()).finish();
            Log.i("sample_autoclosedialog", "dialog dismissed");
        } catch (InterruptedException e) {
            Log.e("sample_autoclosedialog", "exception", e);
        }
    }

}