/*
 * Copyright (c) 2015 Yu AOKI
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package com.aokyu.sample.rxjava;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

/**
 * Provides utility methods for observable operations.
 */
public final class ObservableOperations {

    private ObservableOperations() {}

    public static <T> Observable.Operator<T, T> composer(CompositeSubscription subscription) {
        return new Observable.Operator<T, T>() {
            @Override
            public Subscriber<? super T> call(Subscriber<? super T> subscriber) {
                if (subscription.isUnsubscribed()) {
                    throw new IllegalStateException("Parent subscription was unsubscribed");
                } else {
                    subscription.add(subscriber);
                    subscriber.add(Subscriptions.create(new Action0() {
                        @Override
                        public void call() {
                            subscription.remove(subscriber);
                        }
                    }));
                }
                return subscriber;
            }
        };
    }
}
