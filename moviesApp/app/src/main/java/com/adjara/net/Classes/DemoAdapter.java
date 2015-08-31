package com.adjara.net.Classes;

/**
 * Created by vakhtanggelashvili on 8/9/15.
 */
import android.widget.ListAdapter;


import com.adjara.net.model.DemoItem;

import java.util.List;

public interface DemoAdapter extends ListAdapter {

    void appendItems(List<DemoItem> newItems);

    void setItems(List<DemoItem> moreItems);
}
