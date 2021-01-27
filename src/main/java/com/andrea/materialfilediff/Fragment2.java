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

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Fragment2 extends Fragment  implements View.OnClickListener, CommunicationInterface {
    public static final int PICK_MULTIPLE_FILES_RESULT_CODE = 2;
    public static final int SAVE_FILE_RESULT_CODE = 3;
    ImageButton files_pick_button = null;
    TextView selected_files_counter = null;
    Button calcola_checksum_button = null;
    Button showProgressBar = null;
    Button jsonExportButton = null;
    ProgressBar progressBar = null;
    FragmentUtils fu = null;
    List<Uri> uriList;
    List<String> fileNames = null;
    int remainingItems = 0;

    AsyncUtils at = new AsyncUtils();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment2_layout, container, false);
        fu = new FragmentUtils();
        uriList = new ArrayList<Uri>();

        files_pick_button = (ImageButton) view.findViewById(R.id.file_pick_button);
        selected_files_counter = (TextView) view.findViewById(R.id.selected_files_counter);
        calcola_checksum_button = (Button) view.findViewById(R.id.calcola_checksum_button);
        jsonExportButton = (Button) view.findViewById(R.id.jsonExportButton);
        jsonExportButton.setEnabled(false);



        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        showProgressBar  = (Button) view.findViewById(R.id.showProgressBar);
        showProgressBar.setOnClickListener(this);
        files_pick_button.setOnClickListener(this);
        calcola_checksum_button.setOnClickListener(this);
        jsonExportButton.setOnClickListener(this);


        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.file_pick_button:
                fu.pickFiles(this);
                break;
            case R.id.showProgressBar:
                this.enableProgressBar();
                this.testProgressbar();
                break;
            case R.id.calcola_checksum_button:{

                at.submitWork(uriList,getActivity(),this);
                jsonExportButton.setEnabled(true);

                }
            case R.id.jsonExportButton:
                fu.writeFile(this);
                break;

            default:
                break;
        }
    }

    @Override
    public void sendTestData(String s) {
        //
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_MULTIPLE_FILES_RESULT_CODE) {
            if (data != null) { // checking empty selection
                uriList = FileUtils.clipDataToUriList(data.getClipData(), getActivity());
                fileNames = FileUtils.uriListToFileNameList(uriList, getContext());
                setSelected_files_counter(uriList.size());
            }
        } else if(requestCode == SAVE_FILE_RESULT_CODE){

            if ( data != null){
                Uri uri2 = data.getData();
                ArrayList<FileRepresentation> li = at.getFileRepresentationList();
                JSONObject jo=FileUtils.createJSON(li);
                FileUtils.saveJSONExternalStorageFromUri(uri2,jo,this);

            }
        }



    }






    /**


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_MULTIPLE_FILES_RESULT_CODE) {
            if(data != null ) { // checking empty selection
                uriList.clear(); //clear uriList before add new items
                //if(data.getClipData() != null ) { // checking multiple selection or not
                    for(int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        uriList.add(uri);
                        fileNames.add(FileUtils.UriToFileName(uri, getActivity()));
                    }
                //} else {
                //    Uri uri = data.getData();
            //    }
            }
        }
        setSelected_files_counter(uriList.size());
    }

     */


    public void setSelected_files_counter(int n){
        selected_files_counter.setText(Integer.toString(n));
    }

    public void setRemainingItems(int n){
        this.remainingItems = n;
    }

    public int getRemainingItems(int n){
        return this.remainingItems;
    }

    public void decrementRemainingItems(int n){
        this.remainingItems--;
    }

    public void enableProgressBar(){
        this.progressBar.setVisibility(View.VISIBLE);
    }

    public void disableProgressBar(){
        this.progressBar.setVisibility(View.GONE);
    }


    @Override
    public void updateProgress(int progress) {
        progressBar.incrementProgressBy(progress);

    }

    public void testProgressbar(){
        new Thread(() ->{
                for (int i=0; i<=100; i++) {
                    progressBar.setProgress(i);
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
    }
}
