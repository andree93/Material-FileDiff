package com.andrea.materialfilediff;

public class Utils {
    public static int calcolaPercentuale(int n, int totale){
        int risultato = 0;
        if (n != 0){
            risultato = (n/totale)*100;
        }
        return risultato;
    }
}
