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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class Fragment3 extends Fragment implements View.OnClickListener{



    private static final int PICK_JSON_FILE = 1;
    private static final int PICK_MULTIPLE_FILE = 2;

    private Uri uri = null;
    ArrayList<Uri> uriList = null;
    HashMap<String, FileRepresentation> hashMapJson;

    Frag3ViewModel mViewModel;

    ProgressBar progressBar = null;

    ImageButton pick_file_json_button = null;
    ImageButton pick_multiple_files_button = null;
    Button json_read_button = null;
    Button confronta_checksum_button = null;
    TextView json_load_tv = null;
    TextView selected_files_tv2 = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(Frag3ViewModel.class);

        View view = inflater.inflate(R.layout.fragment3_layout, container, false);

        pick_file_json_button = (ImageButton) view.findViewById(R.id.pick_file_json_button);
        pick_multiple_files_button = (ImageButton) view.findViewById(R.id.pick_multiple_files_button);
        json_read_button = (Button) view.findViewById(R.id.json_read_button);
        json_load_tv = (TextView) view.findViewById(R.id.json_load_tv);
        confronta_checksum_button = (Button) view.findViewById(R.id.confronta_checksum_button);
        selected_files_tv2 = (TextView) view.findViewById(R.id.selected_files_tv2);

        pick_multiple_files_button.setOnClickListener(this);
        pick_file_json_button.setOnClickListener(this);
        json_read_button.setOnClickListener(this);


        showJson_load_tv(mViewModel.getJson_load_tv());
        showSelected_files_counter_tv(mViewModel.getSelected_files_counter_tv());
        setCompare_checksum_button_status(mViewModel.getIsCompareChecksumButtonEnabled());



        //progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pick_file_json_button: {
                FragmentUtils.pickFile(this, false);
            } break;

            case R.id.json_read_button: {
                //
            } break;
            case R.id.pick_multiple_files_button:{
                FragmentUtils.pickFile(this, true);


            } break;
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
                showJson_load_tv(fileName); // imposto il nuovo nome del file visualizzato
                //switch_calcola_checksum_button(); //ripristino scritta pulsante a "calcola checksum" se è stato selezionato un nuovo file
                //showChecksum_calculated_tv(""); //cancello checksum precedentemente calcolato
                hashMapJson = FileUtils.jsonObjectParser(fileUri, getContext());



                for (String key : hashMapJson.keySet()){
                    Log.d("test","nome: "+hashMapJson.get(key).nome+" hash: "+hashMapJson.get(key).hash);
                }





            }
        } else if (requestCode == PICK_MULTIPLE_FILE){
            if (resultCode == RESULT_OK){
                if (data != null) { // checking empty selection
                    set_uriList((ArrayList<Uri>) FileUtils.clipDataToUriList(data));
                    //set_fileNames((ArrayList<String>) FileUtils.uriListToFileNameList(uriList, getContext())); //ricavo la lista dei nomi dei file selezionati a partire dagli URI
                    showSelected_files_counter_tv(""+uriList.size()+" File selezionati"); //Aggiorno il contatore che mostra il numero di file selezionati

                }
            }
        }

    }


    public void showJson_load_tv(String value){
        json_load_tv.setText(value);
        mViewModel.SetJson_load_tv(value);
    }

    public void set_uriList(ArrayList<Uri> uriList){
        this.uriList = uriList;
        mViewModel.setUriList(uriList);
    }

    public void setReadJsonButtonEnabled(boolean status){
        json_read_button.setEnabled(status);
        mViewModel.setIsReadJsonButtonEnabled(status);
    }

    public void setCompare_checksum_button_status(boolean value){
        confronta_checksum_button.setEnabled(value);
        mViewModel.setIsCompareChecksumButtonEnabled(value);
    }


    public void showSelected_files_counter_tv(String files_counter_tv){
        selected_files_tv2.setText(files_counter_tv);
        mViewModel.setSelected_files_counter_tv(files_counter_tv);
    }





}
