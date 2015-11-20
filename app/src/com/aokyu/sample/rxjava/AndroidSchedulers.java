/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * NOTE: This file has been modified by Yu AOKI.
 * Modifications are licensed under the License.
 */
package com.aokyu.sample.rxjava;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.TimeUnit;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.internal.schedulers.ScheduledAction;
import rx.plugins.RxJavaPlugins;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

/**
 * Schedulers that have Android-specific functionality
 */
public final class AndroidSchedulers extends Scheduler {

    private final Handler mHandler;

    private static final Scheduler MAIN_THREAD_SCHEDULER =
            new AndroidSchedulers(new Handler(Looper.getMainLooper()));

    /**
     * {@link Scheduler} which uses the provided {@link Handler} to execute actions.
     */
    public static Scheduler handlerThread(final Handler handler) {
        return new AndroidSchedulers(handler);
    }

    /**
     * {@link Scheduler} which will execute actions on the Android UI thread.
     */
    public static Scheduler mainThread() {
        return MAIN_THREAD_SCHEDULER;
    }

    private AndroidSchedulers(Handler handler) {
        mHandler = handler;
    }

    @Override
    public Worker createWorker() {
        return new InnerHandlerThreadScheduler(mHandler);
    }

    private static class InnerHandlerThreadScheduler extends Worker {

        private final Handler mHandler;

        private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();

        public InnerHandlerThreadScheduler(Handler handler) {
            mHandler = handler;
        }

        @Override
        public void unsubscribe() {
            mCompositeSubscription.unsubscribe();
        }

        @Override
        public boolean isUnsubscribed() {
            return mCompositeSubscription.isUnsubscribed();
        }

        @Override
        public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
            action = RxJavaPlugins.getInstance().getSchedulersHook().onSchedule(action);

            final ScheduledAction scheduledAction = new ScheduledAction(action);
            scheduledAction.add(Subscriptions.create(new Action0() {
                @Override
                public void call() {
                    mHandler.removeCallbacks(scheduledAction);
                }
            }));
            scheduledAction.addParent(mCompositeSubscription);
            mCompositeSubscription.add(scheduledAction);

            mHandler.postDelayed(scheduledAction, unit.toMillis(delayTime));

            return scheduledAction;
        }

        @Override
        public Subscription schedule(final Action0 action) {
            return schedule(action, 0, TimeUnit.MILLISECONDS);
        }

    }
}