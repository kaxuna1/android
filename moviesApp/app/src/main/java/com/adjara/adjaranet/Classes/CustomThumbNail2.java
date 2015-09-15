package com.adjara.adjaranet.Classes;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import it.gmariotti.cardslib.library.internal.CardThumbnail;

/**
 * Created by vakhtanggelashvili on 8/13/15.
 */
public class CustomThumbNail2 extends CardThumbnail {
    public CustomThumbNail2(Context context) {
        super(context);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View viewImage) {
        if (viewImage != null) {

            if (parent!=null && parent.getResources()!=null){
                DisplayMetrics metrics=parent.getResources().getDisplayMetrics();

                int base = 125;

                if (metrics!=null){
                    viewImage.getLayoutParams().width = (int)(base*metrics.density*1.4);
                    viewImage.getLayoutParams().height = (int)(base*metrics.density);
                }else{
                    viewImage.getLayoutParams().width = 250;
                    viewImage.getLayoutParams().height = 350;
                }
            }
        }
    }
}