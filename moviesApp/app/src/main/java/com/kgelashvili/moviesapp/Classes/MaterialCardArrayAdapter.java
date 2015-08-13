package com.kgelashvili.moviesapp.Classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.base.CardViewWrapper;

/**
 * Created by vakhtanggelashvili on 8/12/15.
 */
public class MaterialCardArrayAdapter extends CardArrayAdapter {
    public MaterialCardArrayAdapter(Context context, List<Card> cards) {
        super(context, cards);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;
        LayoutInflater mInflater = (LayoutInflater)this.mContext.getSystemService("layout_inflater");
        Card mCard = (Card)this.getItem(position);
        if(mCard != null) {
            int layout = this.mRowLayoutId;
            boolean recycle = false;
            if(convertView == null) {
                recycle = false;
                view = mInflater.inflate(layout, parent, false);
            } else {
                recycle = true;
            }

            CardViewWrapper mCardView = (CardViewWrapper)view.findViewById(it.gmariotti.cardslib.library.R.id.list_cardId);
            if(mCardView != null) {
                mCardView.setForceReplaceInnerLayout(Card.equalsInnerLayout(mCardView.getCard(), mCard));
                mCardView.setRecycle(recycle);
                boolean origianlSwipeable = mCard.isSwipeable();
                mCard.setSwipeable(false);
                mCardView.setCard(mCard);
                mCard.setSwipeable(origianlSwipeable);
                if(mCard.getCardHeader() != null && mCard.getCardHeader().isButtonExpandVisible() || mCard.getViewToClickToExpand() != null) {
                    this.setupExpandCollapseListAnimation(mCardView);
                }

                this.setupSwipeableAnimation(mCard, mCardView);
                this.setupMultichoice(view, mCard, mCardView, (long)position);
            }
        }

        return view;

    }
}
