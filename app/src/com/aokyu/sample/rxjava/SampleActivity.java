/*
 * Copyright (c) 2015 Yu AOKI
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package com.aokyu.sample.rxjava;

import com.aokyu.sample.rxjava.util.DebugLog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SampleActivity extends AppCompatActivity {

    private CompositeSubscription mSubscription;

    @Bind(R.id.start_button) Button mStartButton;

    @Bind(R.id.image_view) ImageView mImageView;

    @Bind(R.id.progress_view) ProgressBar mProgressView;

    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_sample);
        ButterKnife.bind(this);
        mSubscription = new CompositeSubscription();
    }

    @OnClick(R.id.start_button) void onStartButtonClicked() {
        startLoading("http://dummyimage.com/100x100");
    }

    private void startLoading(String imageUrl) {
        Observable.just(imageUrl)
                .lift(ObservableOperations.composer(mSubscription))
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String urlString) {
                        DebugLog.d("CALL : [" + Thread.currentThread().getName() + "]");
                        URL url = null;
                        InputStream stream = null;
                        Bitmap bitmap = null;
                        try {
                            url = new URL(urlString);
                            stream = url.openStream();
                            bitmap = BitmapFactory.decodeStream(stream);
                            stream.close();
                        } catch (MalformedURLException e) {
                            throw OnErrorThrowable.from(e);
                        } catch (IOException e) {
                            throw OnErrorThrowable.from(e);
                        }
                        return bitmap;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Bitmap>() {

                    @Override
                    public void onStart() {
                        mProgressView.setVisibility(View.VISIBLE);
                        mImageView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        DebugLog.d("NEXT : [" + Thread.currentThread().getName() + "]");
                        Bitmap old = mBitmap;
                        mBitmap = bitmap;
                        if (old != null) {
                            old.recycle();
                        }
                    }

                    @Override
                    public void onCompleted() {
                        DebugLog.d("COMPLETED : [" + Thread.currentThread().getName() + "]");
                        mImageView.setImageBitmap(mBitmap);
                        mProgressView.setVisibility(View.GONE);
                        mImageView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        DebugLog.d("ERROR : [" + Thread.currentThread().getName() + "]");
                        mImageView.setImageBitmap(null);
                        mProgressView.setVisibility(View.GONE);
                        mImageView.setVisibility(View.VISIBLE);
                    }
                });
    }

    protected void onDestroy() {
        if (mBitmap != null) {
            mBitmap.recycle();
        }
        mSubscription.unsubscribe();
        super.onDestroy();
    }
}
