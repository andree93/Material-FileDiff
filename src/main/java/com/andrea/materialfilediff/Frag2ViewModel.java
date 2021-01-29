package com.andrea.materialfilediff;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.SavedStateHandle;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class Frag2ViewModel extends ViewModel {
    private SavedStateHandle state;


    int selected_files_counter=0;
    boolean jsonExportButton=false;


    private MutableLiveData<ArrayList<String>> fileNameList;
    private MutableLiveData<ArrayList<Uri>> uriList;




    public int getSelected_files_counter_tv() {
        return selected_files_counter;
    }

    public void setSelected_files_counter_tv(int selected_files_counter) {
        this.selected_files_counter = selected_files_counter;
    }

    public boolean isJsonExportButton() {
        return jsonExportButton;
    }

    public void setJsonExportButton(boolean jsonExportButton) {
        this.jsonExportButton = jsonExportButton;
    }


    /**LiveData<ArrayList<String>> getFileNameList() {
        if (fileNameList == null) {
            fileNameList = new MutableLiveData<>();
            //loadNameList();
        }
        return fileNameList;
    } */



    public void setFileNameList(ArrayList<String> fileNameList){
        if(this.fileNameList == null){
            this.fileNameList = new MutableLiveData<>();
        }
        this.fileNameList.postValue(fileNameList);
    }

    public ArrayList<String> getFileNameList(){
        if(this.fileNameList == null){
            this.fileNameList = new MutableLiveData<>();
        }
        return this.fileNameList.getValue();
    }


    public ArrayList<Uri> getUriList(){
        if(this.uriList == null){
            this.uriList = new MutableLiveData<>();
        }
        return this.uriList.getValue();
    }

    public void setUriList(ArrayList<Uri> uriList){
        if(this.uriList == null){
            this.uriList = new MutableLiveData<>();
        }
        this.uriList.postValue(uriList);
    }


    /**private void loadData() {
        // do async operation to fetch users
    } */

}