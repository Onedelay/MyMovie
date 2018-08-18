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
import com.onedelay.mymovie.Constants;
import com.onedelay.mymovie.R;
import com.onedelay.mymovie.database.MovieEntity;

public class PosterFragment extends Fragment {
    PosterFragmentCallback callback;

    public interface PosterFragmentCallback {
        void onChangeFragment();

        void setData(int id, String title, int grade, float rating);
    }

    public static PosterFragment newInstance(int index, MovieEntity item) {
        PosterFragment fragment = new PosterFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_INDEX, index);
        bundle.putInt(Constants.KEY_MOVIE_ID, item.getId());
        bundle.putString(Constants.KEY_IMAGE_URL, item.getImage());
        bundle.putString(Constants.KEY_TITLE, item.getTitle());
        bundle.putFloat(Constants.KEY_RATE, item.getReservation_rate()); // 예매율
        bundle.putInt(Constants.KEY_GRADE, item.getGrade());
        bundle.putString(Constants.KEY_DATE, item.getDate());
        bundle.putFloat(Constants.KEY_RATING, item.getAudience_rating());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_poster, container, false);

        ImageView imageView = rootView.findViewById(R.id.list_image);
        TextView title = rootView.findViewById(R.id.list_title);
        TextView rate = rootView.findViewById(R.id.rate);
        TextView grade = rootView.findViewById(R.id.grade);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            Glide.with(this).load(bundle.getString(Constants.KEY_IMAGE_URL)).into(imageView);
            title.setText(String.format(getString(R.string.list_fragment_title), bundle.getInt(Constants.KEY_INDEX), bundle.getString(Constants.KEY_TITLE)));
            rate.setText(String.format(getString(R.string.list_fragment_rate), bundle.getFloat(Constants.KEY_RATE)));
            grade.setText(String.format(getString(R.string.list_fragment_grade), bundle.getInt(Constants.KEY_GRADE)));
        }

        rootView.findViewById(R.id.detailButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 상세보기 버튼을 클릭하면 viewPager 를 숨기고 DetailFragment 를 띄우도록 콜백 메소드를 호출한다.
                callback.onChangeFragment();
                if (getArguments() != null) {
                    Bundle bundle = getArguments();
                    callback.setData(bundle.getInt(Constants.KEY_MOVIE_ID), bundle.getString(Constants.KEY_TITLE), bundle.getInt(Constants.KEY_GRADE), bundle.getFloat(Constants.KEY_RATING));
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof PosterFragmentCallback) {
            callback = (PosterFragmentCallback) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (callback != null) callback = null;
    }
}
