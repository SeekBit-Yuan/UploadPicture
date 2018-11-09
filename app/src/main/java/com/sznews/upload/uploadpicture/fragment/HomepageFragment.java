package com.sznews.upload.uploadpicture.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sznews.upload.uploadpicture.R;
import com.sznews.upload.uploadpicture.activity.NewBuildActivity;

import org.xutils.view.annotation.ViewInject;

public class HomepageFragment extends Fragment {

    private Button start;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, null);

        start = view.findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewBuildActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}
