package com.artursworld.reactiontest.view.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.app.Fragment;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.UtilsRG;


public class MedicalUserDetailsFragmentView extends Fragment {

    public static final String[] HOMEPAGES = {
            "http://www.cs.hhu.de/lehrstuehle-und-arbeitsgruppen/algorithmen-fuer-schwere-probleme.html",
            "http://www.cs.hhu.de/lehrstuehle-und-arbeitsgruppen/algorithmen-fuer-schwere-probleme.html",
            "http://www.cs.hhu.de/lehrstuehle-und-arbeitsgruppen/algorithmen-fuer-schwere-probleme.html"
    };

    public static MedicalUserDetailsFragmentView newInstance(int index) {
        MedicalUserDetailsFragmentView f = new MedicalUserDetailsFragmentView();

        // Create arguments bundle
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }

    // Retrieve the index of the currently shown work group
    public int getShownIndex() {
        try {
            return getArguments().getInt("index", 0);
        }
        catch (Exception e){
            return 0;
        }

    }

    @Override
    public void onAttach(Context context) {
        UtilsRG.info(MedicalUserDetailsFragmentView.class.getSimpleName()+ " onAttach");
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        UtilsRG.info(MedicalUserDetailsFragmentView.class.getSimpleName()+ " onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UtilsRG.info(MedicalUserDetailsFragmentView.class.getSimpleName()+" onCreateView");
        // Create our WebView layout
        View layout = inflater.inflate(R.layout.fragment_medical_user_details_fragment_view, container, false);
        WebView webView = (WebView) layout.findViewById(R.id.web_view);
        // Load web page
        webView.loadUrl(HOMEPAGES[getShownIndex()]);
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        UtilsRG.info(MedicalUserDetailsFragmentView.class.getSimpleName()+ " onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        UtilsRG.info(MedicalUserDetailsFragmentView.class.getSimpleName()+ " onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        UtilsRG.info(MedicalUserDetailsFragmentView.class.getSimpleName()+  "onViewStateRestored");
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart() {
        UtilsRG.info(MedicalUserDetailsFragmentView.class.getSimpleName()+ " onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        UtilsRG.info(MedicalUserDetailsFragmentView.class.getSimpleName()+ " onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        UtilsRG.info(MedicalUserDetailsFragmentView.class.getSimpleName() + " onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        UtilsRG.info(MedicalUserDetailsFragmentView.class.getSimpleName() + " onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        UtilsRG.info(MedicalUserDetailsFragmentView.class.getSimpleName() + " onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        UtilsRG.info(MedicalUserDetailsFragmentView.class.getSimpleName() + " onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        UtilsRG.info(MedicalUserDetailsFragmentView.class.getSimpleName() + " onDetach");
        super.onDetach();
    }

    // use startActivityForResult(...) and not getActivity().startActivityForResult(...)
    // public void clickEventOrSomething() {
    //     getActivity().startActivityForResult(...); // will only call onActivityResult(...) in Activity
    //     startActivityForResult(...); // will call onActivityResult(...) in Activity and because of the super-call also onActivityResult(...) here in Fragment
    // }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // use it here
    }
}
