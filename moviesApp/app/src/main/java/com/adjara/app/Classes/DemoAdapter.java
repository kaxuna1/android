package com.adjara.app.Classes;

/**
 * Created by vakhtanggelashvili on 8/9/15.
 */

import android.widget.ListAdapter;

import com.adjara.app.model.DemoItem;

import java.util.List;

public interface DemoAdapter extends ListAdapter {

    void appendItems(List<DemoItem> newItems);

    void setItems(List<DemoItem> moreItems);
}
