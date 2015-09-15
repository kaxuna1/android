package com.adjara.adjaranet.Classes;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adjara.adjaranet.R;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by KGelashvili on 7/15/2015.
 */
public class CustomHeaderMainMovieItem extends CardHeader {
    String text3="text3";
    String text1="text1",text2="text2";

    public CustomHeaderMainMovieItem(Context context,String text1,String text2,String text3) {
        super(context, R.layout.main_movie_item_header);
        this.text1=text1;
        this.text2=text2;
        this.text3=text3;
    }
    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (view != null) {
            TextView t1 = (TextView) view.findViewById(R.id.main_movie_title);
            if (t1 != null)
                t1.setText(text1);


           /* TextView t3 = (TextView) view.findViewById(R.id.main_movie_description);
            if (t3 != null)
                t3.setText(text3);*/
        }
    }
}
