package com.betelgeuse.corp.shotmessage;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.EditText;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    static final int SECOND_REQUEST_CODE = 1;
    static final int RECOGNIZER_REQUEST_CODE = 2;
    static String text;
    EditText editSubject;
    EditText editText;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editSubject = findViewById(R.id.edit_Subject);
        editText = findViewById(R.id.edit_Text);

        findViewById(R.id.send_message).setOnClickListener(v -> {
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setType("text/plain");
//            intent.putExtra(Intent.EXTRA_SUBJECT,"Theme message");
//            intent.putExtra(Intent.EXTRA_TEXT,"Text message");
//            startActivity(intent);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT,editSubject.getText().toString());
            intent.putExtra(Intent.EXTRA_TEXT,editText.getText().toString() + " " + text);
            startActivity(intent);
        });

        findViewById(R.id.send_message_TG).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT,editSubject.getText().toString());
            intent.putExtra(Intent.EXTRA_TEXT,editText.getText().toString());
            List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(intent, 0);

            StringBuilder result = new StringBuilder(1000);
            for (ResolveInfo resolveInfo:resolveInfoList) {
                String curentAPP = resolveInfo.activityInfo.packageName;
                result.append(curentAPP + "\n");
                if (curentAPP.contains("telegram")){
                    PackageManager pm = getApplicationContext().getPackageManager();
                    Intent intent1 = pm.getLaunchIntentForPackage(curentAPP);
                    startActivity(intent1);
                }
            }
            Log.i("Intent list : ", result.toString());
        });

        findViewById(R.id.btn_rec).setOnClickListener(view -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please talk.");
            startActivityForResult(intent, 2);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SECOND_REQUEST_CODE && resultCode == 200 && data !=null){
            count = data.getIntExtra("count", 0);
            getSupportActionBar().setTitle("Count : " + count);
        } else if (requestCode == RECOGNIZER_REQUEST_CODE && resultCode == RESULT_OK && data!=null) {
            List<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Log.i("recognizer : " , result.get(0));
            text = result.get(0);
            editText.setText(editText.getText() + " " + text);
        }
//        if (requestCode == RECOGNIZER_REQUEST_CODE && requestCode == RESULT_OK && data!=null) {
//            List<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//            Log.i("recognizer : " , result.get(0));
//            text = result.get(0);
//            editText.setText(editText.getText() + " " + text);
//        }
    }
}