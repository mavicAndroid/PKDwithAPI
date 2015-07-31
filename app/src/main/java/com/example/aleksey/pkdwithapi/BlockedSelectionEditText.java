package com.example.aleksey.pkdwithapi;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

//класс наследник edittext без возможности редактирования и выделения текста в поле ввода
public class BlockedSelectionEditText extends EditText {

/** Standard Constructors */

public BlockedSelectionEditText (Context context) {
    super(context);
}

public BlockedSelectionEditText (Context context,
                                 AttributeSet attrs) {
    super(context, attrs);
}
public BlockedSelectionEditText (Context context,
                                 AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
}

@Override
protected void onSelectionChanged(int selStart, int selEnd) {
    //on selection move cursor to end of text
    setSelection(this.length());
}
}
