package com.andrea.materialfilediff;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.SavedStateHandle;

import java.util.ArrayList;
import java.util.List;

public class Frag2ViewModel extends ViewModel {
    private SavedStateHandle state;


    int selected_files_counter=0;
    boolean jsonExportButton=false;

    ArrayList<Uri> uriList = null;
    ArrayList<String> fileNames;

//    MutableLiveData<ArrayList<Uri>> uriList;
//    MutableLiveData<ArrayList<String>> fileNames;


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


}