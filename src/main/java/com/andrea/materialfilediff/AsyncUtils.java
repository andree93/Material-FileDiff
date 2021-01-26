package com.andrea.materialfilediff;
import android.os.ParcelFileDescriptor;
import java.util.concurrent.Callable;
import io.reactivex.*;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class AsyncUtils {
    private String md5 = null;
    ParcelFileDescriptor pfd= null;
    private final CompositeDisposable disposables = new CompositeDisposable();
    CommunicationInterface com = null;

    public AsyncUtils(CommunicationInterface com, ParcelFileDescriptor pfd) {
        this.com = com;
        this.pfd = pfd;
    }



    void calcolaChecksumRXJava() {
        com.enableSpinner();
        disposables.add(calcObservable()
                // Run on a background thread
                .subscribeOn(Schedulers.computation())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {
                    private String md5 = null;

                    @Override
                    public void onComplete() {
                        if (md5 != null)
                            com.setResultMD5(md5);
                        com.disableSpinner();
                        com.switch_copia_checksum_button();
                    }

                    @Override
                    public void onError(Throwable e) {
                        com.disableSpinner();

                    }

                    @Override
                    public void onNext(String value) {
                        this.md5 = value;
                    }
                }));
    }


    public Observable<String> calcObservable() {
        return Observable.defer(new Callable<ObservableSource<? extends String>>() {
            @Override
            public ObservableSource<? extends String> call() {
                // Do some long running operation
                FileUtils fu = new FileUtils();
                String result = fu.calcolaChecksum(pfd, com);
                return Observable.defer(() -> Observable.just(result));

            }
        });
    }
}
