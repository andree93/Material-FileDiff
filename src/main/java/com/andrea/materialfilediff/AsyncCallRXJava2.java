package com.andrea.materialfilediff;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
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
    private HashMap<String, FileRepresentation> fileRepresentationHashMap = null;

    public AsyncCallRXJava2() {
        fileRepresentationHashMap = new HashMap<>();

    }

    public ArrayList<FileRepresentation> getFileRepresentationList() {
        return new ArrayList<FileRepresentation>(fileRepresentationHashMap.values());
    }

    public HashMap<String, FileRepresentation> getFileRepresentationHashMap() {
        return fileRepresentationHashMap;
    }


    public void dispose(){
        disposables.dispose();
    }

    public Observable<FileRepresentation> calcObservable(Uri uri, Context context) {
        return Observable.defer(new Callable<ObservableSource<? extends FileRepresentation>>() {
            @Override
            public ObservableSource<? extends FileRepresentation> call() {

                FileRepresentation fileRepresentation = FileUtils.calcolaChecksumFromUri(uri, context);
                Log.d("Thread name: ", Thread.currentThread().getName()); // test


                FileRepresentation finalFileRepresentation = fileRepresentation;
                //return Observable.defer(() -> Observable.just(finalFileRepresentation));
                return Observable.just(finalFileRepresentation);
            }
        });
    }


    public void addWorks3(List<Uri> uriList, Context context, CommunicationInterface com) {
        AtomicInteger errors = new AtomicInteger(0);
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
                            public void onComplete() { //metodo chiamato SE tutti gli Observable sono eseguiti con successo
                                //Log.d("Method onComplete called", "elementi lista: "+fileRepresentationList.size());
                                com.updateProgress();
                                com.notifyCompletion();
                                Log.d("onComplete", " method onComplete called End!!");
                            }

                            @Override
                            public void onError(Throwable e) { // metodo chiamato una sola volta, appena si verifica un'eccezione
                                //Log.d("Method onError", "elementi lista: "+fileRepresentationList.size());
                                Log.d("method onError", " method onError called"+e);
                                fileRepresentationHashMap.clear();
                                com.notifyError();
                            }

                            @Override
                            public void onNext(FileRepresentation value) { //metodo chiamato per ogni valore  emesso dall'observable (se non vengono sollevate eccezioni)

                                Log.d("onNext", " method onNext called");
                                if(value.nome != null && value.hash != null){
                                    fileRepresentationHashMap.put(value.nome, value);
                                } else{
                                    errors.incrementAndGet();
                                }
                                com.updateProgress();
                            }
                        })
        );
    }
}
