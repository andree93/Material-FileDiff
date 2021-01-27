package com.andrea.materialfilediff;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import java.util.concurrent.Callable;

public class AsyncExecutors implements Callable<FileRepresentation> {

    private final Uri uri;
    private final Activity activity;

    public AsyncExecutors(Uri uri, Activity activity) {
        this.uri = uri;
        this.activity = activity;
    }


    @Override
    public FileRepresentation call() throws Exception {
        FileRepresentation fileRepresentation = new FileRepresentation();


        FileUtils fu = new FileUtils();
        fileRepresentation = FileUtils.calcolaChecksumFromUri(uri, activity);

        Log.d("test-0X", fileRepresentation.nome);
        Log.d("test-0X", fileRepresentation.hash);
        Log.d("Thread name: ", Thread.currentThread().getName());

        return fileRepresentation;
    }
}
