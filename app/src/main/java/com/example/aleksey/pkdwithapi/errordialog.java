package com.example.aleksey.pkdwithapi;

/**
 * Created by Aleksey on 28.07.15.
 */
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
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
public class errordialog extends DialogFragment implements View.OnClickListener {

    final String LOG_TAG = "myLogs";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //наполняем контент окна с ошибками
        String Error = getArguments().getString("ErrorText");
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);
        View v = inflater.inflate(R.layout.popup, null);
        TextView errortext = (TextView) v.findViewById(R.id.txtLine);
        errortext.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
        errortext.setTextSize(50);
        if (Error == "validation") {
            errortext.append("Номер телефона \n не существует.");
        }else {

            errortext.append("Ваш номер в очереди.\n Ожидайте вызова.");
        }
        new CloseThread2(this).start();
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
//закрываем окно с ошибками
class CloseThread2 extends Thread {

    private com.example.aleksey.pkdwithapi.errordialog errordialog;

    public CloseThread2(com.example.aleksey.pkdwithapi.errordialog errordialog) {
        super();
        this.errordialog = errordialog;
    }

    @Override
    public void run() {
        try {
            sleep(3000);//показываем в течении этого времени
            errordialog.Close();
            //dialog.dismiss();
            //((PhoneInput)getActivity()).finish();
            Log.i("sample_autoclosedialog", "dialog dismissed");
        } catch (InterruptedException e) {
            Log.e("sample_autoclosedialog", "exception", e);
        }
    }

}
