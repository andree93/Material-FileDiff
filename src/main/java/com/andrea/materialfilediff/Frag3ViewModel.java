package com.andrea.materialfilediff;

import android.net.Uri;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class Frag3ViewModel extends ViewModel {
    private SavedStateHandle state;
    String file_name_tv="JSON File...";
    String selected_files_counter="";
    String checksum_calculated_tv="";
    String checksum_match_tv="";
    String calcola_checksum_button="";
    int checksum_match_text_color;
    int progressBarVisibility = View.GONE;
    boolean isCompareChecksumButtonEnabled=false;
    boolean isReadJsonButtonEnabled=false;


    private MutableLiveData<ArrayList<Uri>> uriList;


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


    public void setIsCompareChecksumButtonEnabled(boolean status) {
        this.isCompareChecksumButtonEnabled = status;
    }

    public boolean getIsCompareChecksumButtonEnabled() {
        return this.isCompareChecksumButtonEnabled;
    }


    public void setIsReadJsonButtonEnabled(boolean status) {
        this.isReadJsonButtonEnabled = status;
    }

    public boolean getIsReadJsonButtonEnabled() {
        return this.isReadJsonButtonEnabled;
    }



    public void setProgressBarVisibility(int visibility) {
        this.progressBarVisibility = visibility;
    }

    public int getProgressBarVisibility() {
        return this.progressBarVisibility;
    }


    public int getChecksum_match_text_color() {
        return checksum_match_text_color;
    }

    public void setChecksum_match_text_color(int checksum_match_text) {
        this.checksum_match_text_color = checksum_match_text;
    }

    public Frag3ViewModel(SavedStateHandle savedStateHandle) {
        state = savedStateHandle;
    }



    public String getJson_load_tv() {
        return file_name_tv;
    }

    public void SetJson_load_tv(String file_name_tv) {
        this.file_name_tv = file_name_tv;
    }

    public String getChecksum_calculated_tv() {
        return checksum_calculated_tv;
    }

    public void setChecksum_calculated_tv(String checksum_calculated_tv) {
        this.checksum_calculated_tv = checksum_calculated_tv;
    }

    public String getChecksum_match_tv() {
        return checksum_match_tv;
    }

    public void setChecksum_match_tv(String checksum_match_tv) {
        this.checksum_match_tv = checksum_match_tv;
    }

    public String getCalcola_checksum_button() {
        return calcola_checksum_button;
    }

    public void setCalcola_checksum_button(String calcola_checksum_button) {
        this.calcola_checksum_button = calcola_checksum_button;
    }


    public String getSelected_files_counter_tv() {
        return selected_files_counter;
    }

    public void setSelected_files_counter_tv(String selected_files_counter) {
        this.selected_files_counter = selected_files_counter;
    }


}