package com.andrea.filediff;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_FILE = 1;
    ParcelFileDescriptor pfd;

    TextView file_input;
    TextView risultato_textview_large;
    EditText risultato_compara_textview_large;
    TextView test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        file_input = findViewById(R.id.file_input);
        risultato_textview_large = findViewById(R.id.risultato_textview_large);
        risultato_compara_textview_large = findViewById(R.id.risultato_compara_textview_large);
        test = findViewById(R.id.test);
    }

    public void pickFile(View view) {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, PICK_FILE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE) {
            if (resultCode == RESULT_OK) {
                // A contact was picked.  Here we will just display it
                // to the user.
                //startActivity(new Intent(Intent.ACTION_VIEW, data));
                Uri fileUri = data.getData();
                File file= new File(fileUri.getPath());
                String percorso = file.getName();
                file_input.setText(percorso);
                try {
                    /*
                     * Get the content resolver instance for this context, and use it
                     * to get a ParcelFileDescriptor for the file.
                     */
                    pfd = getContentResolver().openFileDescriptor(fileUri, "r");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e("MainActivity", "File not found.");
                    return;
                }
                // Get a regular file descriptor for the file

            }

        }

    }

    public void calcolaChecksum(View view) {
        String md5=null;
        try(FileInputStream fis = new ParcelFileDescriptor.AutoCloseInputStream(pfd)){
            md5 = DigestUtils.md5Hex(fis);

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            test.setText(e.getMessage());
        }
        if (md5 != null){
            risultato_textview_large.setText(md5);
        }

    }

    public void confrontaCheckSum(View view) {
        //
    }
}