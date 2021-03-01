package com.andrea.materialfilediff;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class Fragment3 extends Fragment implements CommunicationInterface, View.OnClickListener{



    private static final int PICK_JSON_FILE = 1;
    private static final int PICK_MULTIPLE_FILE = 2;

    private Uri uri = null;
    ArrayList<Uri> uriList = null;
    HashMap<String, FileRepresentation> hashMapJson;
    HashMap<String, FileRepresentation> hashMapResults;

    Frag3ViewModel mViewModel;

    ProgressBar progressBar = null;

    ImageButton pick_file_json_button = null;
    ImageButton pick_multiple_files_button = null;
    Button json_read_button = null;
    Button confronta_checksum_button = null;
    Button cancel_btn = null;
    TextView risultato_confronto_tv = null;
    TextView selected_files_tv2 = null;
    TextView json_file_name = null;
    TextView ris_letti_tv = null;

    AsyncCallRXJava2 asyncCallRXJava2 = new AsyncCallRXJava2();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(Frag3ViewModel.class);

        View view = inflater.inflate(R.layout.fragment3_layout, container, false);

        pick_file_json_button = (ImageButton) view.findViewById(R.id.pick_file_json_button);
        pick_multiple_files_button = (ImageButton) view.findViewById(R.id.pick_multiple_files_button);
        json_read_button = (Button) view.findViewById(R.id.json_read_button);
        cancel_btn = (Button) view.findViewById(R.id.cancel_btn);

        risultato_confronto_tv = (TextView) view.findViewById(R.id.risultato_confronto_tv);
        confronta_checksum_button = (Button) view.findViewById(R.id.confronta_checksum_button);
        selected_files_tv2 = (TextView) view.findViewById(R.id.selected_files_tv2);
        json_file_name = (TextView) view.findViewById(R.id.json_file_name);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar3);
        progressBar.setVisibility(View.VISIBLE);
        ris_letti_tv = (TextView) view.findViewById(R.id.ris_letti_tv);


        pick_multiple_files_button.setOnClickListener(this);
        pick_file_json_button.setOnClickListener(this);
        json_read_button.setOnClickListener(this);
        confronta_checksum_button.setOnClickListener(this);


        showRisultato_confronto_tv(mViewModel.getRisultato_confronto_tv());
        showSelected_files_counter_tv(mViewModel.getSelected_files_counter_tv());
        setCompare_checksum_button_status(mViewModel.getIsCompareChecksumButtonEnabled());
        setProgressBarVisibility(mViewModel.getProgressBarVisibility());
        setReadJsonButtonEnabled_status(mViewModel.getIsReadJsonButtonEnabled());
        showRis_letti_tv(mViewModel.getRis_letti_tv());
        showFileName_tv(mViewModel.getFile_name_tv());
        setCancelButtonEnabled(mViewModel.getCancelButtonStatus());

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pick_file_json_button: {
                FragmentUtils.pickFile(this, false);
            } break;

            case R.id.json_read_button: {
                if(uri != null){
                    hashMapJson = FileUtils.jsonObjectParser(uri, getContext());
                    Toast.makeText(this.getActivity(), ""+hashMapJson.size()+" File letti", Toast.LENGTH_SHORT).show();
                    showRis_letti_tv("Caricati: "+hashMapJson.size()+" risultati");

                    //BEING test
                    for (String key : hashMapJson.keySet()){
                        Log.d("test","nome: "+hashMapJson.get(key).nome+" hash: "+hashMapJson.get(key).hash);
                    }
                    //end TEST
                } else {
                    Toast.makeText(this.getActivity(), "Nessun file JSON selezionato!", Toast.LENGTH_SHORT).show();
                }


                //
            } break;


            case R.id.pick_multiple_files_button:{
                FragmentUtils.pickFile(this, true);
            } break;

            case R.id.confronta_checksum_button:{
                if (uriList != null && uriList.size()>0){
                    Log.d("TAG", "onClick: ");
                    setCancelButtonEnabled(View.VISIBLE);
                    setShowProgressBar(uriList.size());
                    progressBar.setVisibility(View.VISIBLE);
                    asyncCallRXJava2.addWorks3(uriList, getActivity().getApplicationContext(),this);
                } else{
                    Log.d("Errore", "Lista URI Vuota");
                }

            } break;

            case R.id.button_stop:{
                disableProgressBar();
                setCancelButtonEnabled(View.GONE);
                set_uriList(null);
                asyncCallRXJava2.dispose();
            }
            break;

        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_JSON_FILE) {
            if (resultCode == RESULT_OK) {
                //showChecksum_match_tv("");
                //showChecksum_match_tv("");
                //startActivity(new Intent(Intent.ACTION_VIEW, data));

                Uri fileUri = data.getData();
                this.uri = fileUri;
                String fileName = FileUtils.UriToFileName(fileUri, getContext());
                setReadJsonButtonEnabled_status(true);
                showFileName_tv(fileName);

            }

        } else if (requestCode == PICK_MULTIPLE_FILE){
            if (resultCode == RESULT_OK){
                if (data != null) { // checking empty selection
                    set_uriList((ArrayList<Uri>) FileUtils.clipDataToUriList(data));
                    //set_fileNames((ArrayList<String>) FileUtils.uriListToFileNameList(uriList, getContext())); //ricavo la lista dei nomi dei file selezionati a partire dagli URI
                    showSelected_files_counter_tv(""+uriList.size()+" File selezionati"); //Aggiorno il contatore che mostra il numero di file selezionati
                    if(hashMapJson != null && hashMapJson.size() > 0){
                        setCompare_checksum_button_status(true);
                    }



                }
            }
        }

    }


    public void showRisultato_confronto_tv(String value){
        risultato_confronto_tv.setText(value);
        mViewModel.setRisultato_confronto_tv(value);
    }


    public void showFileName_tv(String value){
        json_file_name.setText(value);
        mViewModel.setFile_name_tv(value);
    }



    public void showRis_letti_tv(String value){
        ris_letti_tv.setText(value);
        mViewModel.setRis_letti_tv(value);
    }



    public void set_uriList(ArrayList<Uri> uriList){
        this.uriList = uriList;
        mViewModel.setUriList(uriList);
    }



    public void setCompare_checksum_button_status(boolean value){
        confronta_checksum_button.setEnabled(value);
        mViewModel.setIsCompareChecksumButtonEnabled(value);
    }



    public void setReadJsonButtonEnabled_status(boolean value){
        json_read_button.setEnabled(value);
        mViewModel.setIsReadJsonButtonEnabled(value);
    }


    public void showSelected_files_counter_tv(String files_counter_tv){
        selected_files_tv2.setText(files_counter_tv);
        mViewModel.setSelected_files_counter_tv(files_counter_tv);
    }

    public void setProgressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
        mViewModel.setProgressBarVisibility(visibility);

    }

    public void setCancelButtonEnabled(int status){
        cancel_btn.setVisibility(status);
        mViewModel.setcancelButtonStatus(status);
    }


    @Override
    public void updateProgress(int progress) {

    }

    @Override
    public void updateProgress() {
        progressBar.incrementProgressBy(1);
    }

    public void enableProgressBar(){
        mViewModel.setProgressBarVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void disableProgressBar(){
        setProgressBarVisibility(View.GONE);
    }

    @Override
    public void sendTestData(String s) {

    }

    @Override
    public void notifyCompletion() {
        setCancelButtonEnabled(View.GONE);
        Toast.makeText(this.getActivity(), "Hash calcolato: "+ asyncCallRXJava2.getFileRepresentationList().size() + "/" + uriList.size()+ " File!", Toast.LENGTH_SHORT).show();
        disableProgressBar();
        setCancelButtonEnabled(getView().GONE);
        Log.d("test", "Completato!");
        hashMapResults= asyncCallRXJava2.getFileRepresentationHashMap();
        HashMap<String, List<String>> results = Utils.confrontaHashMap(hashMapJson, hashMapResults);
        int filenonesistenti = results.get("nonesistenti").size();
        int fileChecksumDiverso = results.get("diversi").size();
        Log.d("TAG", "listsize: "+filenonesistenti);
        if(filenonesistenti == 0 && fileChecksumDiverso == 0){
            showRisultato_confronto_tv("Il checksum dei file confrontati coincide");
        }
        if(filenonesistenti > 0 && fileChecksumDiverso > 0){
            showRisultato_confronto_tv("Il checksum di "+ (hashMapResults.keySet().size() - filenonesistenti - fileChecksumDiverso) +" / "+hashMapResults.keySet().size()+ " File coincide "+"\n"+filenonesistenti+" File non trovati nel JSON");
        }
        else if (filenonesistenti > 0 && fileChecksumDiverso == 0){
            showRisultato_confronto_tv("Il checksum di "+ (hashMapResults.keySet().size() - filenonesistenti) +" / "+hashMapResults.keySet().size()+ " File coincide"+"\n"+filenonesistenti+" File non trovati nel JSON");
        }
        else if (filenonesistenti == 0 && fileChecksumDiverso > 0){
            showRisultato_confronto_tv("Il checksum di "+ (hashMapResults.keySet().size() - fileChecksumDiverso) +" / "+hashMapResults.keySet().size()+ " File coincide");
        }
        else{
            showRisultato_confronto_tv("Il checksum di "+ (hashMapResults.keySet().size() - fileChecksumDiverso) +" / "+hashMapResults.keySet().size()+ " File coincide");
        }
        hashMapResults.clear();
        setCompare_checksum_button_status(false);

    }

    @Override
    public void notifyError() {
        disableProgressBar();
        updateProgress(0);
        setCancelButtonEnabled(getView().GONE);
        Toast.makeText(this.getActivity(), "Si son verificati degli errori, riprova!!", Toast.LENGTH_SHORT).show();
    }


    public void setShowProgressBar(int works){
        progressBar.setMax(works);
    }



}
