package com.iotalabs.geoar.view.main.util.swipe_menu_list;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.example.lotalabsappui.R;

public class SwipeMenuListCreator {
    private Resources resources;
    public SwipeMenuListCreator(Resources resources){
        this.resources=resources;
    }
    public int dpToPx(int dp) {
        float density = resources.getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
    public SwipeMenuCreator getCreator(Context context){
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override// list 땡가는 메뉴 만들기
            public void create(SwipeMenu menu) {
                // create "첫번째" item
                SwipeMenuItem openItem = new SwipeMenuItem(context);
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(context);
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dpToPx(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_baseline_delete_forever_24);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        return creator;
    }

}
