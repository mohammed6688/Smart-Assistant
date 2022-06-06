package com.mocomp.smartassistant;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, add;
    ImageButton exit;
    EditText english,arabic;
    String eng,ar;
    long id;
    TextView title;
    public CustomDialogClass(Activity a ,String eng,String ar,long id) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.eng = eng;
        this.ar = ar;
        this.id = id;

    }
    public CustomDialogClass(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.eng=this.ar="";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog);
        //Typeface Maintypeface = Typeface.createFromAsset(c.getAssets(), "Cairo-Regular.ttf");
        english =findViewById(R.id.english);
        arabic =findViewById(R.id.arabic);
        //textView.setTypeface(Maintypeface);
        add = (Button) findViewById(R.id.btn_add);
        title=findViewById(R.id.title);

        exit=findViewById(R.id.exit);
        add.setOnClickListener(this);
        exit.setOnClickListener(this);
        if (!eng.equals("")&&!ar.equals("")){
            title.setText("Edit Word");
            english.setText(eng);
            arabic.setText(ar);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                if (english.getText().toString().equals("")||arabic.getText().toString().equals("")){
                    Toast.makeText(c, "fill all fields", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent("custom-message");
                    intent.putExtra("eng",english.getText().toString());
                    intent.putExtra("ar",arabic.getText().toString());
                    if (!eng.equals("")&&!ar.equals("")){
                        intent.putExtra("flag","confirmEdit");
                        intent.putExtra("id",id);

                    }else {
                        intent.putExtra("flag","add");
                    }
                    LocalBroadcastManager.getInstance(c).sendBroadcast(intent);
                    break;
                }


            case R.id.exit:
               break;
            default:
                break;
        }
        dismiss();
    }
}

