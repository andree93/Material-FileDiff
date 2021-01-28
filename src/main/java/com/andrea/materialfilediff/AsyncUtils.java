package com.andrea.materialfilediff;
import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.reactivex.*;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class AsyncUtils {
    public static final int FRAGMENT_1_ID = 1;
    public static final int FRAGMENT_2_ID = 2;
    public static final int FRAGMENT_3_ID = 3;


    private final CompositeDisposable disposables = new CompositeDisposable();
    private ArrayList<FileRepresentation> fileRepresentationList = null;
    private final ConcurrentHashMap<String, FileRepresentation> fileRepresentationMap = new ConcurrentHashMap<>();


    public AsyncUtils() {
    }



    public ConcurrentHashMap<String, FileRepresentation> getResults(){
        return this.fileRepresentationMap;
    }

    public ArrayList<FileRepresentation> getFileRepresentationList(){
        return this.fileRepresentationList;
    }


    void calcolaChecksumRXJava2(Uri uri, Activity activity, CommunicationInterface com) {
        int fragmentID = 0;
        CommunicationInterfaceFrag1 comFrag1 = null;
        CommunicationInterfaceFrag2 comFrag2 = null;
        if (com instanceof CommunicationInterfaceFrag1){
            comFrag1 = (CommunicationInterfaceFrag1) com;
            fragmentID = FRAGMENT_1_ID;
        }
        else if (com instanceof CommunicationInterfaceFrag2){
            comFrag2 = (CommunicationInterfaceFrag2) com;
            fragmentID = FRAGMENT_2_ID;
        }

        int finalFragmentID = fragmentID;

        disposables.clear();

        CommunicationInterfaceFrag1 finalComFrag1 = comFrag1;
        CommunicationInterfaceFrag2 finalComFrag2 = comFrag2;

        if(finalFragmentID == FRAGMENT_1_ID){
            finalComFrag1.enableProgressBar();
        }
        else {
            //
        }
        disposables.add(calcObservable2(uri,activity)
                // Run on a background thread
                .subscribeOn(Schedulers.computation())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<FileRepresentation>() {
                    private FileRepresentation fileRepresentation = null;
                    @Override
                    public void onComplete() {
                        if(finalFragmentID == FRAGMENT_1_ID){
                            if (fileRepresentation != null){
                                finalComFrag1.sendResult(fileRepresentation);
                            }

                            finalComFrag1.disableProgressBar();
                            finalComFrag1.switch_copia_checksum_button();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (finalFragmentID == FRAGMENT_1_ID){
                            finalComFrag1.disableProgressBar();
                        }
                        }


                    @Override
                    public void onNext(FileRepresentation value) {
                        this.fileRepresentation = value;
                    }
                }));
    }



    public Observable<FileRepresentation> calcObservable2(Uri uri, Activity activity) {
        return Observable.defer(new Callable<ObservableSource<? extends FileRepresentation>>() {
            @Override
            public ObservableSource<? extends FileRepresentation> call() {
                // Do some long running operation
                FileUtils fu = new FileUtils();
                FileRepresentation fileRepresentation = FileUtils.calcolaChecksumFromUri(uri, activity);
                return Observable.defer(() -> Observable.just(fileRepresentation));
            }
        });
    }



    public void submitWork(List<Uri> uriList, Activity activity, CommunicationInterface com) {
        com.enableProgressBar();

        ArrayList<FileRepresentation> fileRepresentationList = new ArrayList<>();
        ExecutorService executorService = Executors.newWorkStealingPool(20);
        List<Callable<FileRepresentation>> mythreads = new ArrayList<>();
        List<Future<FileRepresentation>> futureList = new ArrayList<Future<FileRepresentation>>();



        for (Uri uri : uriList) {
            mythreads.add(new AsyncExecutors(uri, activity));
        }

        try {
            futureList = executorService.invokeAll(mythreads);

        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("Eccezione Submit Work: ", e.getMessage());
        }


        try {
            for (Future<FileRepresentation> future : futureList) {
                fileRepresentationList.add(future.get());
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.shutdown();

        Log.d("tag-test", "elementi lista: "+ fileRepresentationList.size());
        this.fileRepresentationList = fileRepresentationList;
    }
    
}














