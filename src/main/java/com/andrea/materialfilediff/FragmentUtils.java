package com.andrea.materialfilediff;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class FragmentUtils {
    private static final int PICK_SINGLE_FILE_RESULT_CODE = 1;
    private static final int PICK_MULTIPLE_FILES_RESULT_CODE = 2;
    private static final int SAVE_FILE_RESULT_CODE = 3;


    public static void pickFile(Fragment fr, boolean allowMultiple){
        if(allowMultiple)
            pickFiles(fr);
        else
            FragmentUtils.pickFile(fr);
    }


    public static void pickFile(Fragment fr) {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        fr.startActivityForResult(intent, PICK_SINGLE_FILE_RESULT_CODE);

    }


    public static void pickFiles(Fragment fr) {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        fr.startActivityForResult(intent, PICK_MULTIPLE_FILES_RESULT_CODE);

    }

    public static void writeFile(Fragment fr){
        String fileName="exportedResults.json";

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        fr.startActivityForResult(intent, SAVE_FILE_RESULT_CODE);

    }
}
