package com.mocomp.smartassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int IMAGE_REQUEST = 1;
    private SQLiteDatabase mDatabase;
    private TextToSpeech mTTS;
    RecyclerView recyclerView;
    List<Word> myWords;
    CardView addWord;
    private CustomDialogClass errorDialog;
    MyWordsAdapter wordsAdapter;
    CircleImageView UploadPhoto;
    ImageView empty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializer();
        listener();
        setData();


        //mButtonSpeak = findViewById(R.id.button_speak);

//        mEditText = findViewById(R.id.edit_text);
//        mSeekBarPitch = findViewById(R.id.seek_bar_pitch);
//        mSeekBarSpeed = findViewById(R.id.seek_bar_speed);

//        mButtonSpeak.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                speak();
//            }
//        });
    }


    private void initializer() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));

        addWord=findViewById(R.id.addWord);
        recyclerView = findViewById(R.id.recycleView);
        UploadPhoto = findViewById(R.id.profile_image);
        empty = findViewById(R.id.empty);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        myWords=new ArrayList<>();
        mTTS = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = mTTS.setLanguage(Locale.ENGLISH);

                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported");
                } else {
//                    mButtonSpeak.setEnabled(true);
                }
            } else {
                Log.e("TTS", "Initialization failed");
            }
        });

        GroceryDBHelper dbHelper = new GroceryDBHelper(this);
        mDatabase = dbHelper.getWritableDatabase();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Log.e("error", "onSwiped: ");
                removeItem((long) viewHolder.itemView.getTag());
            }
        }).attachToRecyclerView(recyclerView);


    }

    private void listener() {
        addWord.setOnClickListener(v -> {
            errorDialog=new CustomDialogClass(MainActivity.this);
            if(!((Activity) MainActivity.this).isFinishing())
            {
                errorDialog.show();
            }
            errorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        });
        UploadPhoto.setOnClickListener(v -> openImage());
    }

    private void speak(String text) {
        float pitch = (float) 1;
//        float pitch = (float) mSeekBarPitch.getProgress() / 50;
        if (pitch < 0.1) pitch = 0.1f;
        float speed = (float) 1;
//        float speed = (float) mSeekBarSpeed.getProgress() / 50;
        if (speed < 0.1) speed = 0.1f;

        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);

        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void openImage() {
        if (CheckPermission()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, IMAGE_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            UploadPhoto.setImageURI(imageUri);
//            if (uploadTask != null && uploadTask.isInProgress()) {
//                Toast.makeText(AccountInfo.this, getResources().getString(R.string.upload_in_progress), Toast.LENGTH_SHORT).show();
//            } else {
//                uploadImage();
//            }
        }
    }
    private void setData(){
        if (getAllItems().getCount()!=0){
            empty.setVisibility(View.GONE);
        }else {
            empty.setVisibility(View.VISIBLE);
        }
        wordsAdapter = new MyWordsAdapter(MainActivity.this, myWords,getAllItems());
        recyclerView.setAdapter(wordsAdapter);
        wordsAdapter.swapCursor(getAllItems());
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String flag =intent.getStringExtra("flag");
            if (flag.equals("add")){
                String english = intent.getStringExtra("eng");
                String arabic = intent.getStringExtra("ar");

                ContentValues cv = new ContentValues();
                cv.put(GroceryContract.GroceryEntry.COLUMN_ENGLISH, english);
                cv.put(GroceryContract.GroceryEntry.COLUMN_ARABIC, arabic);
                mDatabase.insert(GroceryContract.GroceryEntry.TABLE_NAME, null, cv);
                wordsAdapter.swapCursor(getAllItems());
                if (getAllItems().getCount()!=0){
                    empty.setVisibility(View.GONE);
                }else {
                    empty.setVisibility(View.VISIBLE);
                }
            }else if (flag.equals("sound")){
                String word = intent.getStringExtra("word");
                Log.e("word is: ",word);
                speak(word);
            }else if (flag.equals("edit")){

                int id =intent.getIntExtra("id",0);
                long colId=intent.getLongExtra("colId",0);
                Log.d("id", String.valueOf(id));


                Cursor TuplePointer = mDatabase.query(
                        GroceryContract.GroceryEntry.TABLE_NAME,
                        new String[]{GroceryContract.GroceryEntry.COLUMN_ARABIC,GroceryContract.GroceryEntry.COLUMN_ENGLISH},
                        null,
                        null,
                        null,
                        null,
                        GroceryContract.GroceryEntry.COLUMN_TIMESTAMP + " DESC"
                );
                TuplePointer.moveToPosition(id);


                String engVal=TuplePointer.getString(TuplePointer.getColumnIndex(GroceryContract.GroceryEntry.COLUMN_ENGLISH));
                String arValue=TuplePointer.getString(TuplePointer.getColumnIndex(GroceryContract.GroceryEntry.COLUMN_ARABIC));


                errorDialog=new CustomDialogClass(MainActivity.this,engVal,arValue,colId);
                if(!((Activity) MainActivity.this).isFinishing()) {
                    errorDialog.show();
                }
                errorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }else if (flag.equals("confirmEdit")){
                String english = intent.getStringExtra("eng");
                String arabic = intent.getStringExtra("ar");

                long id =intent.getLongExtra("id",0);

                ContentValues cv = new ContentValues();
                cv.put(GroceryContract.GroceryEntry.COLUMN_ENGLISH, english);
                cv.put(GroceryContract.GroceryEntry.COLUMN_ARABIC, arabic);
                mDatabase.update(GroceryContract.GroceryEntry.TABLE_NAME, cv, "_id = ? ", new String[] { Integer.toString((int) id) } );

                //mDatabase.update(GroceryContract.GroceryEntry.TABLE_NAME, null, cv);
                wordsAdapter.swapCursor(getAllItems());
            }

        }
    };

    private void removeItem(long id) {

        mDatabase.delete(GroceryContract.GroceryEntry.TABLE_NAME,
                GroceryContract.GroceryEntry._ID + "=" + id, null);
        wordsAdapter.swapCursor(getAllItems());

        if (getAllItems().getCount()!=0){
            empty.setVisibility(View.GONE);
        }else {
            empty.setVisibility(View.VISIBLE);
        }
    }

    private Cursor getAllItems() {
        return mDatabase.query(
                GroceryContract.GroceryEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                GroceryContract.GroceryEntry.COLUMN_TIMESTAMP + " DESC"
        );
    }

    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }


    public boolean CheckPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getResources().getString(R.string.permission))
                        .setMessage(getResources().getString(R.string.Please_accept_permissions))
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_LOCATION);


                                startActivity(new Intent(MainActivity
                                        .this, MainActivity.class));
                                MainActivity.this.overridePendingTransition(0, 0);
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }

            return false;
        } else {

            return true;

        }
    }
}