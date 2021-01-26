package com.andrea.materialfilediff;
import android.content.ClipData;
import android.content.Intent;
import android.content.ClipboardManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.CLIPBOARD_SERVICE;

public class Fragment1 extends Fragment implements View.OnClickListener, CommunicationInterface {

    private static final int PICK_SINGLE_FILE = 1;
    CommunicationInterface com = null;
    FragmentUtils fu = null;
    ParcelFileDescriptor pfd = null;
    Button confronta_checksum_button;
    Button calcola_checksum_button;
    ImageButton file_pick_button;

    TextView checksum_calculated_tv;
    TextView checksum_match_tv;
    TextView file_name_tv;
    EditText checksum_et;
    ProgressBar progressBar;
    private ClipboardManager myClipboard;
    private ClipData myClip;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment1_layout, container, false);

        fu = new FragmentUtils();

        confronta_checksum_button = (Button) view.findViewById(R.id.confronta_checksum_button);
        calcola_checksum_button = (Button) view.findViewById(R.id.calcola_checksum_button);
        file_pick_button = (ImageButton) view.findViewById(R.id.file_pick_button);

        checksum_calculated_tv = (TextView) view.findViewById(R.id.checksum_calculated_tv);
        checksum_match_tv = (TextView) view.findViewById(R.id.checksum_match_tv);
        file_name_tv = (TextView) view.findViewById(R.id.file_name_tv);
        checksum_et = (EditText) view.findViewById(R.id.checksum_et);

        confronta_checksum_button.setOnClickListener(this);
        calcola_checksum_button.setOnClickListener(this);
        file_pick_button.setOnClickListener(this);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        return view;
    }


    public void switch_calcola_checksum_button(){
        calcola_checksum_button.setText(R.string.calcola_checksum);
    }


    public void switch_copia_checksum_button(){
        calcola_checksum_button.setText(R.string.copia);
    }

    public void copyToClipboard(){
        myClipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
        String text;
        text = checksum_calculated_tv.getText().toString();

        myClip = ClipData.newPlainText("text", text);
        myClipboard.setPrimaryClip(myClip);

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.file_pick_button:
                setChecksum_match_tv("");
                fu.pickFile(this);
            break;

            case R.id.calcola_checksum_button: {
                //calcolaChecksumThread();
                //calcolaChecksumRXJava();
                if(getCalcola_checksum_button().equalsIgnoreCase(getString(R.string.calcola_checksum))){
                    AsyncUtils at = new AsyncUtils(this, this.pfd);
                    at.calcolaChecksumRXJava();
                } else if (getCalcola_checksum_button().equalsIgnoreCase(getString(R.string.copia))){
                    copyToClipboard();
                }
                else{
                    //
                }

            }
            break;

            case R.id.confronta_checksum_button: confrontaChecksum();
            break;
        }
    }

    @Override
    public void enableSpinner() {
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void disableSpinner() {
        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void setResultMD5(String md5) {
        checksum_calculated_tv.setText(md5);
    }

    public String getCalcola_checksum_button(){
        return calcola_checksum_button.getText().toString();
    }

/** Simple thread java version
    private void calcolaChecksumThread() {
        progressBar.setVisibility(View.VISIBLE);
        FileUtils fu = new FileUtils();
        AtomicReference<String> result = new AtomicReference<>();
        new Thread(() -> {
            // do background stuff here
            result.set(fu.calcolaChecksum(pfd));
            getActivity().runOnUiThread(()->{
                // OnPostExecute stuff here
                checksum_calculated_tv.setText(result.get());
                progressBar.setVisibility(View.GONE);
            });
        }).start();
    }


 Calcola Checksum con Thread JAVA FINE */



    public void setChecksum_match_tv(String s){
        checksum_match_tv.setText(s);
    }

    private void confrontaChecksum(){
        String userChecksum = checksum_et.getText().toString();
        if(userChecksum != null){ //se l'input dell'utente non è vuoto
            if(userChecksum.equals(checksum_calculated_tv.getText())){
                setChecksum_match_tv(getString(R.string.checksum_ok));
                checksum_match_tv.setTextColor(Color.GREEN);
            } else{
                setChecksum_match_tv(getString(R.string.checksum_ko));
                checksum_match_tv.setTextColor(Color.RED);
            }
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_SINGLE_FILE) {
            if (resultCode == RESULT_OK) {
                //startActivity(new Intent(Intent.ACTION_VIEW, data));
                Uri fileUri = data.getData();
                File file= new File(fileUri.getPath());
                String percorso = file.getName(); //ottengoil nome del file scelto, a partire dall'URI
                file_name_tv.setText(percorso); // imposto il nuovo nome del file visualizzato
                switch_calcola_checksum_button(); //ripristino scritta pulsante a "calcola checksum" se è stato selezionato un nuovo file
                try {
                    pfd = getActivity().getApplicationContext().getContentResolver().openFileDescriptor(fileUri, "r");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e("MainActivity", "File not found.");
                    return;
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("file_name_tv", file_name_tv.getText());
        outState.putCharSequence("checksum_calculated_tv", checksum_calculated_tv.getText());
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            file_name_tv.setText(String.valueOf(savedInstanceState.get("file_name_tv")));
            checksum_calculated_tv.setText(String.valueOf(savedInstanceState.get("checksum_calculated_tv")));
        }
    }
}
