package com.artursworld.reactiontest.view.user;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.artursworld.reactiontest.R;

/**
 * TODO: Delete this class. Unused!
 */
public class MedicalUserFragmentRetainInstance extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // First time init, create the UI.
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(android.R.id.content, new UiFragment()).commit();
        }
    }

    /**
     * This is a fragment showing UI that will be updated from work done
     * in the retained fragment.
     */
    public static class UiFragment extends Fragment {
        RetainedFragment mWorkFragment;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.activity_medical_user_fragment_retain_instance, container, false);
            // Watch for button clicks.
            Button button = (Button) v.findViewById(R.id.restart);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mWorkFragment.restart();
                }
            });
            return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            FragmentManager fm = getFragmentManager();
            // Check to see if we have retained the worker fragment.
            mWorkFragment = (RetainedFragment) fm.findFragmentByTag("work");
            // If not retained (or first time running), we need to create it.
            if (mWorkFragment == null) {
                mWorkFragment = new RetainedFragment();
                // Tell it who it is working with.
                mWorkFragment.setTargetFragment(this, 0);
                fm.beginTransaction().add(mWorkFragment, "work").commit();
            }
        }
    }

    /**
     * This is the Fragment implementation that will be retained across
     * activity instances.  It represents some ongoing work, here a thread
     * we have that sits around incrementing a progress indicator.
     */
    public static class RetainedFragment extends Fragment {
        ProgressBar mProgressBar;
        int mPosition;
        boolean mReady = false;
        boolean mQuiting = false;
        /**
         * This is the thread that will do our work.  It sits in a loop running
         * the progress up until it has reached the top, then stops and waits.
         */
        final Thread mThread = new Thread() {
            @Override
            public void run() {
                // We'll figure the real value out later.
                int max = 10000;
                // This thread runs almost forever.
                while (true) {
                    // Update our shared state with the UI.
                    synchronized (this) {
                        // Our thread is stopped if the UI is not ready
                        // or it has completed its work.
                        while (!mReady || mPosition >= max) {
                            if (mQuiting) {
                                return;
                            }
                            try {
                                wait();
                            } catch (InterruptedException e) {
                            }
                        }
                        // Now update the progress.  Note it is important that
                        // we touch the progress bar with the lock held, so it
                        // doesn't disappear on us.
                        mPosition++;
                        max = mProgressBar.getMax();
                        mProgressBar.setProgress(mPosition);
                    }
                    // Normally we would be doing some work, but put a kludge
                    // here to pretend like we are.
                    synchronized (this) {
                        try {
                            wait(50);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
        };

        /**
         * Fragment initialization.  We way we want to be retained and
         * start our thread.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Tell the framework to try to keep this fragment around
            // during a configuration change.
            setRetainInstance(true);

            // Start up the worker thread.
            mThread.start();
        }

        /**
         * This is called when the Fragment's Activity is ready to go, after
         * its content view has been installed; it is called both after
         * the initial fragment creation and after the fragment is re-attached
         * to a new activity.
         */
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Retrieve the progress bar from the target's view hierarchy.
            mProgressBar = (ProgressBar) getTargetFragment().getView().findViewById(R.id.progress_horizontal);

            // We are ready for our thread to go.
            synchronized (mThread) {
                mReady = true;
                mThread.notify();
            }
        }

        /**
         * This is called when the fragment is going away.  It is NOT called
         * when the fragment is being propagated between activity instances.
         */
        @Override
        public void onDestroy() {
            // Make the thread go away.
            synchronized (mThread) {
                mReady = false;
                mQuiting = true;
                mThread.notify();
            }

            super.onDestroy();
        }

        /**
         * This is called right before the fragment is detached from its
         * current activity instance.
         */
        @Override
        public void onDetach() {
            // This fragment is being detached from its activity.  We need
            // to make sure its thread is not going to touch any activity
            // state after returning from this function.
            synchronized (mThread) {
                mProgressBar = null;
                mReady = false;
                mThread.notify();
            }

            super.onDetach();
        }

        /**
         * API for our UI to restart the progress thread.
         */
        public void restart() {
            synchronized (mThread) {
                mPosition = 0;
                mThread.notify();
            }
        }
    }
}