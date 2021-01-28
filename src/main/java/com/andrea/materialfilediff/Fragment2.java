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
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class Fragment2 extends Fragment  implements View.OnClickListener, CommunicationInterface {
    Frag2ViewModel mViewModel;

    public static final int PICK_MULTIPLE_FILES_RESULT_CODE = 2;
    public static final int SAVE_FILE_RESULT_CODE = 3;
    ImageButton files_pick_button = null;
    TextView selected_files_counter = null;
    Button calcola_checksum_button = null;
    Button showProgressBar = null;
    Button jsonExportButton = null;
    Button button_stop = null;
    ProgressBar progressBar = null;
    //CircleProgressBar circleProgressBar = null;
    List<Uri> uriList = null;
    List<String> fileNames = null;
    Async2 async2 = new Async2();




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment2_layout, container, false);
        mViewModel = new ViewModelProvider(this).get(Frag2ViewModel.class);

        uriList = new ArrayList<Uri>();

        files_pick_button = (ImageButton) view.findViewById(R.id.file_pick_button);
        selected_files_counter = (TextView) view.findViewById(R.id.selected_files_counter);
        calcola_checksum_button = (Button) view.findViewById(R.id.calcola_checksum_button);
        jsonExportButton = (Button) view.findViewById(R.id.jsonExportButton);

        button_stop = (Button) view.findViewById(R.id.button_stop);


        //circleProgressBar = view.findViewById(R.id.line_progress);

        button_stop.setOnClickListener(this);






        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        showProgressBar  = (Button) view.findViewById(R.id.showProgressBar);
        showProgressBar.setOnClickListener(this);
        files_pick_button.setOnClickListener(this);
        calcola_checksum_button.setOnClickListener(this);
        jsonExportButton.setOnClickListener(this);

        showSelected_files_counter_tv(mViewModel.getSelected_files_counter_tv());
        setJsonExportButtonEnabled(mViewModel.isJsonExportButton());





        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.file_pick_button:
                FragmentUtils.pickFile(this, true);
                break;
            case R.id.showProgressBar:
                this.enableProgressBar();
                this.testProgressbar();
                break;
            case R.id.calcola_checksum_button:{
                if (uriList != null && uriList.size()>0){
                    setShowProgressBar(uriList.size());
                    async2.addWorks(uriList, getActivity().getApplicationContext(),this);
                } else{
                    Log.d("Errore", "Lista URI Vuota");
                }
                break;

                }
            case R.id.jsonExportButton:
                FragmentUtils.writeFile(this);
                break;

            case R.id.button_stop:{
                disableProgressBar();
                setJsonExportButtonEnabled(false);
                this.uriList = null;
                async2.dispose();
                async2 = new Async2(); //Devo capire il motivo. Pur chiamando i metodi dispose() e clear(), la successiva aggiunta di nuovi lavori dà luogo a comportamenti anomali
            }
            break;


            default:
                break;
        }
    }

    @Override
    public void sendTestData(String s) {
        //
    }

    public void showSelected_files_counter_tv(int files){
        selected_files_counter.setText(Integer.toString(files));
        mViewModel.setSelected_files_counter_tv(files);
    }

    public void setJsonExportButtonEnabled(boolean enabled){
        jsonExportButton.setEnabled(enabled);
        mViewModel.setJsonExportButton(enabled);
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_MULTIPLE_FILES_RESULT_CODE) {
            if (data != null) { // checking empty selection
                uriList = FileUtils.clipDataToUriList(data);
                fileNames = FileUtils.uriListToFileNameList(uriList, getContext());
                showSelected_files_counter_tv(uriList.size());
            }
        } else if(requestCode == SAVE_FILE_RESULT_CODE){

            if ( data != null){
                Uri uri = data.getData();
                ArrayList<FileRepresentation> li = async2.getFileRepresentationList();
                JSONObject jo=FileUtils.createJSON(li);
                FileUtils.saveJSONExternalStorageFromUri(uri,jo,getActivity().getApplicationContext());

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


    @Override
    public void notifyCompletion() {
        setJsonExportButtonEnabled(true);
        Log.d("test", "Completato!");
    }

    public void enableProgressBar(){
        this.progressBar.setVisibility(View.VISIBLE);
        //circleProgressBar.setActivated(true);
    }

    public void disableProgressBar(){
        this.progressBar.setVisibility(View.GONE);
        this.progressBar.setProgress(0);
//        circleProgressBar.setActivated(true);
//        circleProgressBar.setProgress(0);


    }


    @Override
    public void updateProgress(int progress) {
        progressBar.incrementProgressBy(progress);
        //circleProgressBar.setProgress(progress);

    }

    public void updateProgress() {
        progressBar.incrementProgressBy(1);

    }

    public void setShowProgressBar(int works){
        progressBar.setMax(works);
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