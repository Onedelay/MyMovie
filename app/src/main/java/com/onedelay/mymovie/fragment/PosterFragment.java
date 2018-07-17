package com.onedelay.mymovie.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.onedelay.mymovie.R;

public class PosterFragment extends Fragment {
    PosterFragmentCallback callback;

    public interface PosterFragmentCallback {
        void onChangeFragment();
        void setData(int id);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_poster, container, false);

        ImageView imageView = rootView.findViewById(R.id.list_image);
        TextView title = rootView.findViewById(R.id.list_title);
        TextView rate = rootView.findViewById(R.id.rate);
        TextView grade = rootView.findViewById(R.id.grade);
        //TextView Dday = rootView.findViewById(R.id.d_day);

        if(getArguments() != null) {
            Bundle bundle = getArguments();
            Glide.with(this).load(bundle.getString("imageUrl")).into(imageView);
            title.setText(String.format(getString(R.string.list_fragment_title), bundle.getInt("index"), bundle.getString("title")));
            rate.setText(String.format(getString(R.string.list_fragment_rate), bundle.getFloat("rate")));
            grade.setText(String.format(getString(R.string.list_fragment_grade), bundle.getInt("grade")));
        }

        rootView.findViewById(R.id.detailButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onChangeFragment();
                if(getArguments() != null) {
                    Bundle bundle = getArguments();
                    callback.setData(bundle.getInt("id"));
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof PosterFragmentCallback)
            callback = (PosterFragmentCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (callback != null) callback = null;
    }
}
