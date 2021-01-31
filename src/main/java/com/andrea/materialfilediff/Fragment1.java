package com.andrea.materialfilediff;
import android.content.ClipData;
import android.content.Intent;
import android.content.ClipboardManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CLIPBOARD_SERVICE;

public class Fragment1 extends Fragment implements View.OnClickListener, CommunicationInterfaceFrag1 {

    Frag1ViewModel mViewModel;

    private static final int PICK_SINGLE_FILE = 1;
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
    Uri uri = null;
    AsyncCallRXJava2 asyncCallRXJava2;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(Frag1ViewModel.class);

        View view = inflater.inflate(R.layout.fragment1_layout, container, false);

        //Inizializzo widgets
        confronta_checksum_button = (Button) view.findViewById(R.id.jsonExportButton);
        calcola_checksum_button = (Button) view.findViewById(R.id.calcola_checksum_button);
        file_pick_button = (ImageButton) view.findViewById(R.id.file_pick_button);

        checksum_calculated_tv = (TextView) view.findViewById(R.id.checksum_calculated_tv);
        checksum_match_tv = (TextView) view.findViewById(R.id.checksum_match_tv);
        file_name_tv = (TextView) view.findViewById(R.id.file_name_tv);
        checksum_et = (EditText) view.findViewById(R.id.checksum_et);


        file_name_tv.setClickable(true);


        //Set onclick listener
        confronta_checksum_button.setOnClickListener(this);
        calcola_checksum_button.setOnClickListener(this);
        file_pick_button.setOnClickListener(this);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        file_name_tv.setOnClickListener(this);


        // Inizializzo variabili con ViewModel
        showFile_name_tv(mViewModel.getFile_name_tv());
        showChecksum_calculated_tv(mViewModel.getChecksum_calculated_tv());
        showChecksum_match_tv(mViewModel.getChecksum_match_tv());
        checksum_match_tv_text_color(mViewModel.getChecksum_match_text_color());
        enableProgressBar(mViewModel.getProgressBarVisibility());
        setCompare_checksum_button_status(mViewModel.getIsCompareChecksumButtonEnabled());


        if(mViewModel.getCalcola_checksum_button().equals("")){
            Log.d("test", "string button vuota"); //TEST
            showCalcola_checksum_button(getString(R.string.calcola_checksum));
        } else {
            showCalcola_checksum_button(mViewModel.getCalcola_checksum_button());
        }


        return view;

    }

    public void showFile_name_tv(String value){
        file_name_tv.setText(value);
        mViewModel.setFile_name_tv(value);
    }

    public void showChecksum_calculated_tv(String value){
        checksum_calculated_tv.setText(value);
        mViewModel.setChecksum_calculated_tv(value);
    }

    public void showChecksum_match_tv(String value){
        checksum_match_tv.setText(value);
        mViewModel.setChecksum_match_tv(value);
    }

    public void checksum_match_tv_text_color(int value){ //aaaa
        checksum_match_tv.setTextColor(value);
        mViewModel.setChecksum_match_text_color(value);
    }


    public void showCalcola_checksum_button(String value){
        calcola_checksum_button.setText(value);
        mViewModel.setCalcola_checksum_button(value);
    }

    public void showChecksum_et(String value){
        mViewModel.setChecksum_et(value);
        checksum_et.setText(value);

    }


    public void setCompare_checksum_button_status(boolean value){
        confronta_checksum_button.setEnabled(value);
        mViewModel.setIsCompareChecksumButtonEnabled(value);
    }

    public void switch_calcola_checksum_button(){ // Cambio scritta bottone da "calcola checksum" a "copia"
        showCalcola_checksum_button(getString(R.string.calcola_checksum));
    }


    public void switch_copia_checksum_button(){  // Cambio scritta bottone da "copia" a "calcola checksum"
        showCalcola_checksum_button(getString(R.string.copia));
    }




    /**
     * Metodo per la copia del testo negli appunti di Android
     */
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
                FragmentUtils.pickFile(this, false);
            break;
            case R.id.file_name_tv:
                FragmentUtils.pickFile(this, false);
                break;

            case R.id.calcola_checksum_button: {
                if(getCalcola_checksum_button().equalsIgnoreCase(getString(R.string.calcola_checksum))){
                    if(uri == null){
                        showChecksum_calculated_tv("Seleziona prima un file! Riprova...");
                    } else{
                        asyncCallRXJava2 = new AsyncCallRXJava2();
                        ArrayList<Uri> data =  new ArrayList<Uri>();
                        data.add(uri);
                        asyncCallRXJava2.addWorks3(data,getActivity(),this);
                    }


                } else if (getCalcola_checksum_button().equalsIgnoreCase(getString(R.string.copia))){
                    copyToClipboard();
                }
                else{
                    //
                }

            }
            break;

            case R.id.jsonExportButton: confrontaChecksum();
            break;
        }
    }

    public void enableProgressBar(int visible) {
        mViewModel.setProgressBarVisibility(visible);
        progressBar.setVisibility(visible);
    }



    @Override
    public void enableProgressBar() {
        mViewModel.setProgressBarVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void disableProgressBar() {
        mViewModel.setProgressBarVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void sendTestData(String s) {
        //
    }

    @Override
    public void notifyCompletion() {
        showChecksum_calculated_tv(asyncCallRXJava2.getFileRepresentationList().get(0).hash);
        showFile_name_tv(asyncCallRXJava2.getFileRepresentationList().get(0).nome);
        switch_copia_checksum_button();
        setCompare_checksum_button_status(true);
        disableProgressBar();
        Toast.makeText(this.getActivity(), "Hash calcolato", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void notifyError() {
        disableProgressBar();
        Toast.makeText(this.getActivity(), "Errore, hash non calcolato!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void sendResult(FileRepresentation fileRepresentation) {
        showChecksum_calculated_tv(fileRepresentation.hash);
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





    public void setchecksum_et(String s){
        checksum_et.setText(s);
    }

    private void confrontaChecksum(){
        String userChecksum = checksum_et.getText().toString();
        String checksum_calculated = checksum_calculated_tv.getText().toString();
        if(!userChecksum.equals("") && (!checksum_calculated.equals(""))){ //se l'input dell'utente non è vuoto
            if(userChecksum.equals(checksum_calculated)){
                showChecksum_match_tv(getString(R.string.checksum_ok));
                checksum_match_tv_text_color(Color.GREEN);
            } else{
                showChecksum_match_tv(getString(R.string.checksum_ko));
                checksum_match_tv_text_color(Color.RED);
            }
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_SINGLE_FILE) {
            if (resultCode == RESULT_OK) {
                showChecksum_match_tv("");
                showChecksum_match_tv("");
                //startActivity(new Intent(Intent.ACTION_VIEW, data));
                Uri fileUri = data.getData();
                String fileName = FileUtils.UriToFileName(fileUri, getContext());
                showFile_name_tv(fileName); // imposto il nuovo nome del file visualizzato
                switch_calcola_checksum_button(); //ripristino scritta pulsante a "calcola checksum" se è stato selezionato un nuovo file
                showChecksum_calculated_tv(""); //cancello checksum precedentemente calcolato
                this.uri = fileUri;
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        /**
         * Utilizzo il view Model al posto dei metodi onSaveInstanceState e OnActivityCreated
         */
        //outState.putCharSequence("file_name_tv", file_name_tv.getText());
//        outState.putCharSequence("checksum_calculated_tv", checksum_calculated_tv.getText());
//        outState.putCharSequence("checksum_match_tv", checksum_match_tv.getText());
//        outState.putCharSequence("calcola_checksum_button", calcola_checksum_button.getText());

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            /**
             * Utilizzo il view Model al posto dei metodi onSaveInstanceState e OnActivityCreated
             */
            //file_name_tv.setText(String.valueOf(savedInstanceState.get("file_name_tv")));
//            checksum_calculated_tv.setText(String.valueOf(savedInstanceState.get("checksum_calculated_tv")));
//            checksum_match_tv.setText(String.valueOf(savedInstanceState.get("checksum_match_tv")));
//            calcola_checksum_button.setText(String.valueOf(savedInstanceState.get("calcola_checksum_button")));

        }
    }






    @Override
    public void updateProgress(int progress) {
        //
    }

    @Override
    public void updateProgress() {
        //
    }
}
