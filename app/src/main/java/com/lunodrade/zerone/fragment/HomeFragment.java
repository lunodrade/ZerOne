package com.lunodrade.zerone.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lunodrade.zerone.ExerciseActivity;
import com.lunodrade.zerone.LoginActivity;
import com.lunodrade.zerone.MainActivity;
import com.lunodrade.zerone.R;
import com.lunodrade.zerone.bookcards.CardItem;
import com.lunodrade.zerone.bookcards.CardPagerAdapter;
import com.lunodrade.zerone.bookcards.ShadowTransformer;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private MainActivity mActivity;

    private ViewPager mViewPager;
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mActivity = (MainActivity) getActivity();

        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);

        mCardAdapter = new CardPagerAdapter(this, mActivity.getUserClass().getLvlForXP());
        mCardAdapter.addCardItem(new CardItem(0, 16, R.string.book_title_1, R.string.book_desc_1));
        mCardAdapter.addCardItem(new CardItem(5, 50, R.string.book_title_2, R.string.book_desc_2));
        mCardAdapter.addCardItem(new CardItem(15, 150, R.string.book_title_3, R.string.book_desc_2));
        mCardAdapter.addCardItem(new CardItem(20, 250, R.string.book_title_4, R.string.book_desc_3));

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        mCardShadowTransformer.enableScaling(true);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);

        //TODO: carregar o último feito
        //mViewPager.setCurrentItem(2);

        /*
        mViewPager.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mViewPager.setCurrentItem(2, true);
            }
        }, 500);
        */

        return view;
    }



    public void clickBookButton(int position) {

        CardItem card = mCardAdapter.getCardItemAt(position);
        int level = card.getLevel();
        int points = card.getPoints();
        String title = getResources().getString(card.getTitle());

        Log.d("HomeFragment", "clickBookButton: " + level + " | " + points);

        Intent intent = new Intent(mActivity, ExerciseActivity.class);
        intent.putExtra("level", level);
        intent.putExtra("points", points);
        intent.putExtra("title", title);
        startActivity(intent);
        mActivity.finish();
    }

}
