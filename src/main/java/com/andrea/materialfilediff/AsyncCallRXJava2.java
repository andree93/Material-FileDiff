package com.andrea.materialfilediff;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
//import io.reactivex.rxjava3.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class AsyncCallRXJava2 {

    private final CompositeDisposable disposables = new CompositeDisposable();
    private ArrayList<FileRepresentation> fileRepresentationList = null;


    public AsyncCallRXJava2() {
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

                FileRepresentation fileRepresentation = FileUtils.calcolaChecksumFromUri(uri, context);

                Log.d("test-0X", fileRepresentation.nome); // test
                Log.d("test-0X", fileRepresentation.hash); // test
                Log.d("Thread name: ", Thread.currentThread().getName()); // test


                FileRepresentation finalFileRepresentation = fileRepresentation;
                //return Observable.defer(() -> Observable.just(finalFileRepresentation));
                return Observable.just(finalFileRepresentation);
            }
        });
    }



    //













    public void addWorks(List<Uri> uriList, Context context, CommunicationInterface com){

        fileRepresentationList.clear();

        int nObservable = uriList.size(); //numero observable
        AtomicInteger remainings = new AtomicInteger(nObservable); //mi serve per tenere traccia del numero di lavori rimanenti

        disposables.clear();
        com.enableProgressBar();

        Disposable[] disposableArr = new Disposable[nObservable];
        Log.d("addworks", "addWorks method (nObservable var): "+nObservable); // test
        Log.d("addworks", "addWorks method (disposable.size() ): "+disposables.size()); // test
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
                                Log.d("Method onComplete called", "elementi lista: "+fileRepresentationList.size()); // test
                                Log.d("Method onComplete called", "End!!"); // test
                                com.disableProgressBar();
                                com.notifyCompletion();
                            }
                            com.updateProgress();

                        }

                        @Override
                        public void onError(Throwable e) {
                            if(remainings.decrementAndGet() == 0){
                                Log.d("Method onError", "elementi lista: "+fileRepresentationList.size()); // test
                                Log.d("Method onError", "End!!"); // test
                                com.disableProgressBar();
                                com.notifyCompletion();
                            }

                            com.updateProgress();

                            Log.d("method onError", "method onError called"); // test

                        }

                        @Override
                        public void onNext(FileRepresentation value) {

                            fileRepresentationList.add(value);
                        }
                    });

            disposableArr[i] = disposable;

        }
        disposables.addAll(disposableArr);
        Log.d("addworks", "addWorks method (disposable.size() ): "+disposables.size());  // test

    }


    // test 3


    public void addWorks3(List<Uri> uriList, Context context, CommunicationInterface com) {
        fileRepresentationList.clear();
        disposables.clear();
        com.enableProgressBar();

        disposables.add(
                Observable.fromIterable(uriList)
                        .subscribeOn(Schedulers.single())
                        .flatMap(new Function<Uri, Observable<FileRepresentation>>() {
                            @Override
                            public Observable<FileRepresentation> apply(Uri uri) {
                                return calcObservable(uri, context);
                            }
                        }, /*delayErrors */ true)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<FileRepresentation>() {
                            @Override
                            public void onComplete() {
                                Log.d("Method onComplete called", "elementi lista: "+fileRepresentationList.size());
                                Log.d("Method onComplete called", "End!!");
                                com.disableProgressBar();
                                com.notifyCompletion();
                                com.updateProgress();
                                Log.d("Method onComplete called", "End!!");

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("Method onError", "elementi lista: "+fileRepresentationList.size());
                                Log.d("Method onError", "End!!");
                                com.disableProgressBar();
                                com.notifyCompletion();

                                Log.d("method onError", "method onError called");

                            }

                            @Override
                            public void onNext(FileRepresentation value) {

                                fileRepresentationList.add(value);

                                com.updateProgress();
                            }
                        })
        );



    }







//



}
