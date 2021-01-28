package com.andrea.materialfilediff;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.SavedStateHandle;

public class Frag1ViewModel extends ViewModel {
    private SavedStateHandle state;
    String file_name_tv="";
    String checksum_calculated_tv="";
    String checksum_match_tv="";
    String calcola_checksum_button="";
    String checksum_et="";
    int checksum_match_text_color;


    public int getChecksum_match_text_color() {
        return checksum_match_text_color;
    }

    public void setChecksum_match_text_color(int checksum_match_text) {
        this.checksum_match_text_color = checksum_match_text;
    }

    public String getChecksum_et() {
        return checksum_et;
    }

    public void setChecksum_et(String checksum_et) {
        this.checksum_et = checksum_et;
    }

    public Frag1ViewModel(SavedStateHandle savedStateHandle) {
        state = savedStateHandle;
    }



    public String getFile_name_tv() {
        return file_name_tv;
    }

    public void setFile_name_tv(String file_name_tv) {
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


}