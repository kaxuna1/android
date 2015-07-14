package com.kgelashvili.moviesapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.cards.actions.BaseSupplementalAction;
import it.gmariotti.cardslib.library.cards.actions.TextSupplementalAction;
import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardView;
import it.gmariotti.cardslib.library.view.CardViewNative;

public class settingsActivity extends Activity {

    protected ActionMode mActionMode;
    protected Card card;

    protected CardView cardViewCab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getActionBar().setTitle("პარამეტრები");

        /*card = new Card(settingsActivity.this);

        //Create a CardHeader
        CardHeader header = new CardHeader(settingsActivity.this);

        //Set the header title
        header.setTitle("სახელი");
        header.setButtonExpandVisible(true);

        card.addCardHeader(header);
        CardExpand expand = new CardExpand(settingsActivity.this);
        expand.setTitle("გაშლილი ტექსტი");
        card.addCardExpand(expand);



        //Set the card inner text
        card.setTitle("შიდა ტექსტი");


        card.setOnLongClickListener(new Card.OnLongCardClickListener() {
            @Override
            public boolean onLongClick(Card card, View view) {
                if (mActionMode != null) {
                    view.setActivated(false);
                    mActionMode.finish();
                    return false;
                }
                // Start the CAB using the ActionMode.Callback defined above
                mActionMode = settingsActivity.this.startActionMode(mActionModeCallback);
                view.setActivated(true);
                return true;
            }
        });

        //Set card in the cardView
        cardViewCab = (CardView) settingsActivity.this.findViewById(R.id.carddemo_card_id2);
        cardViewCab.setCard(card);*/

        ArrayList<BaseSupplementalAction> actions = new ArrayList<BaseSupplementalAction>();

        // Set supplemental actions
        TextSupplementalAction t1 = new TextSupplementalAction(settingsActivity.this, R.id.text1);
        t1.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Toast.makeText(settingsActivity.this, " Click on Text SHARE ", Toast.LENGTH_SHORT).show();
            }
        });
        actions.add(t1);

        TextSupplementalAction t2 = new TextSupplementalAction(settingsActivity.this, R.id.text2);
        t2.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Toast.makeText(settingsActivity.this," Click on Text LEARN ",Toast.LENGTH_SHORT).show();
            }
        });
        actions.add(t2);

        //Create a Card, set the title over the image and set the thumbnail
        MaterialLargeImageCard card =
                MaterialLargeImageCard.with(settingsActivity.this)
                        .setTextOverImage("Italian Beaches")
                        .setTitle("This is my favorite local beach")
                        .setSubTitle("A wonderful place")
                        .useDrawableUrl("http://static.adjaranet.com/moviecontent/10553/covers/214x321-10553.jpg")
                        .setupSupplementalActions(R.layout.carddemo_native_material_supplemental_actions_large, actions)
                        .build();

        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Toast.makeText(settingsActivity.this," Click on ActionArea ",Toast.LENGTH_SHORT).show();
            }
        });

        //Set card in the CardViewNative
        CardViewNative cardView = (CardViewNative) settingsActivity.this.findViewById(R.id.carddemo_largeimage_text2);
        cardView.setCard(card);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.mainmovielongpressed, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.addtofav:
                    //TODO add to favorites
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            if (card!=null)
                cardViewCab.setActivated(false);
        }
    };

}
