package com.andrea.materialfilediff;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class Async2 {

    private final CompositeDisposable disposables = new CompositeDisposable();
    private ArrayList<FileRepresentation> fileRepresentationList = null;


    public Async2() {
        fileRepresentationList = new ArrayList<>();

    }

    public ArrayList<FileRepresentation> getFileRepresentationList() {
        return fileRepresentationList;
    }

    public void dispose(){
        disposables.dispose();

    }


    public Observable<FileRepresentation> calcObservable(Uri uri, Context context) {
        return Observable.defer(new Callable<ObservableSource<? extends FileRepresentation>>() {
            @Override
            public ObservableSource<? extends FileRepresentation> call() {

                FileUtils fu = new FileUtils();

                FileRepresentation fileRepresentation = FileUtils.calcolaChecksumFromUri(uri, context);

                Log.d("test-0X", fileRepresentation.nome);
                Log.d("test-0X", fileRepresentation.hash);
                Log.d("Thread name: ", Thread.currentThread().getName());


                FileRepresentation finalFileRepresentation = fileRepresentation;
                //return Observable.defer(() -> Observable.just(finalFileRepresentation));
                return Observable.just(finalFileRepresentation);
            }
        });
    }



    //


    public void addWorks(List<Uri> uriList, Context context, CommunicationInterface com){

        fileRepresentationList.clear();

        int nObservable = uriList.size();
        AtomicInteger remainings = new AtomicInteger(nObservable);

        disposables.clear();
        com.enableProgressBar();

        Disposable[] disposableArr = new Disposable[nObservable];
        Log.d("addworks", "addWorks method (nObservable var): "+nObservable);
        Log.d("addworks", "addWorks method (disposable.size() ): "+disposables.size());
        for (int i= 0; i<nObservable; i++){
            Disposable disposable = calcObservable(uriList.get(i), context)
                    // Run on a background thread
                    .subscribeOn(Schedulers.single())
                    // Be notified on the main thread
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<FileRepresentation>() {
                        @Override
                        public void onComplete() {
                            if(remainings.decrementAndGet() == 0){
                                Log.d("Metodo onComplete", "elementi lista: "+fileRepresentationList.size());
                                Log.d("Metodo onComplete", "Fine porcoddio!!");
                                com.disableProgressBar();
                                com.notifyCompletion();
                            }
                            com.updateProgress();

                        }

                        @Override
                        public void onError(Throwable e) {
                            if(remainings.decrementAndGet() == 0){
                                Log.d("Metodo onError", "elementi lista: "+fileRepresentationList.size());
                                Log.d("Metodo onError", "Fine porcoddio!!");
                                com.disableProgressBar();
                                com.notifyCompletion();
                            }

                            com.updateProgress();

                            Log.d("Metodo onError", "metodo onError chiamato");

                        }

                        @Override
                        public void onNext(FileRepresentation value) {

                            fileRepresentationList.add(value);
                        }
                    });

            disposableArr[i] = disposable;

        }
        disposables.addAll(disposableArr);
        Log.d("addworks", "addWorks method (disposable.size() ): "+disposables.size());

    }

    //


}
