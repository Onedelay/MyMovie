package com.onedelay.mymovie.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onedelay.mymovie.R;
import com.onedelay.mymovie.adapter.MovieListPagerAdapter;

public class ViewPagerFragment extends Fragment {
    // 번들에 저장하고 프래그먼트에  setArgument
    private final int[] IMAGE_RESOURCES = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5, R.drawable.image6};
    private final String[] TITLES = {"군 도", "공 조", "더 킹", "레지던트 이블", "럭 키", "아수라"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_viewpager, container, false);
        ViewPager viewPager = rootView.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(5);
        //viewPager.setPageMargin(dpToPx(5)); // 리뷰어님이 이거로 하라하셨는데 적용이 안됩니다!ㅠㅠ
        viewPager.setPadding(dpToPx(45), 0, dpToPx(45), 0);

        MovieListPagerAdapter adapter = new MovieListPagerAdapter(getFragmentManager());
        for (int i = 0; i < IMAGE_RESOURCES.length; i++) {
            adapter.addItem(setData(i + 1, IMAGE_RESOURCES[i], TITLES[i]));
        }
        viewPager.setAdapter(adapter);

        return rootView;
    }

    public PosterFragment setData(int index, int resId, String title) {
        PosterFragment fragment = new PosterFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("image", resId);
        bundle.putString("title", index + ". " + title);
        fragment.setArguments(bundle);
        return fragment;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
