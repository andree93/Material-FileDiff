package com.andrea.materialfilediff;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Utils {
    public static int calcolaPercentuale(int n, int totale){
        int risultato = 0;
        if (n != 0){
            risultato = (n/totale)*100;
        }
        return risultato;
    }


    /**Confronta l'hash l'uguaglianza dell'attributo hash, di ogni file presente in una hashmap con quello del corrispondente (se esistente) della seconda hashmap.
     * Restituisce una hashmap contenete due liste: una contenente i nomi dei file con cheksum diverso, e l'altra con i nomi dei file non trovati nella seconda hashmap
     * @param hashMapJson
     * @param hashMapResults
     * @return HashMap<String, List<String>>
     */
    public static HashMap<String, List<String>> confrontaHashMap(HashMap<String,FileRepresentation> hashMapJson, HashMap<String,FileRepresentation> hashMapResults) {
        HashMap<String, List<String>> results = new HashMap<>();

        List<String> listaFileMancanti = new ArrayList<>();
        List<String> listaFileChecksumDiverso = new ArrayList<>();
        for (String fileName : hashMapResults.keySet()) {
            if (hashMapJson.get(fileName) == null) {
                listaFileMancanti.add(fileName);
            } else if (!(hashMapResults.get(fileName).hash.equals(hashMapJson.get(fileName).hash))) {
                listaFileChecksumDiverso.add(fileName);
            }
        }
        results.put("nonesistenti", listaFileMancanti);
        results.put("diversi", listaFileChecksumDiverso);

        return results;
    }
}
