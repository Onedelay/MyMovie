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

import com.onedelay.mymovie.R;

public class PosterFragment extends Fragment {
    PosterFragmentCallback callback;

    public interface PosterFragmentCallback {
        void onChangeFragment();
        void setData(int resId, String title);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_poster, container, false);

        ImageView imageView = rootView.findViewById(R.id.list_image);
        TextView title = rootView.findViewById(R.id.list_title);
        TextView rate = rootView.findViewById(R.id.rate);
        TextView level = rootView.findViewById(R.id.level);
        TextView Dday = rootView.findViewById(R.id.d_day);

        if(getArguments() != null) {
            Bundle bundle = getArguments();
            imageView.setImageResource(bundle.getInt("image"));
            title.setText(bundle.getString("title"));
        }

        rootView.findViewById(R.id.detailButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onChangeFragment();
                if(getArguments() != null) {
                    Bundle bundle = getArguments();
                    callback.setData(bundle.getInt("image"), bundle.getString("title"));
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
