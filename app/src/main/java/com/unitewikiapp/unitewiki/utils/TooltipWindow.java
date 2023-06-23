package com.unitewikiapp.unitewiki.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.unitewikiapp.unitewiki.R;

public class TooltipWindow {

    //This class is designed to display a small tooltip with a custom UI.

    private static final int MSG_DISMISS_TOOLTIP = 100;
    private Context ctx;
    private PopupWindow tipWindow;
    private PopupWindow checkingViewWindow;
    private View contentView;
    private LayoutInflater inflater;
    private ImageView checkingView;


    public TooltipWindow(Context ctx) {
        this.ctx = ctx;
        tipWindow = new PopupWindow(ctx);
        checkingViewWindow = new PopupWindow(ctx);

        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.tooltip_layout, null);

        checkingView = new ImageView(ctx);
        checkingView.setImageResource(R.drawable.ic_checked);
        checkingView.setBackgroundResource(R.color.fui_transparent);
    }

    public void setText(String text1,String text2,String text3){
        TextView skill_name = contentView.findViewById(R.id.skill_name);
        TextView skill_cooltime = contentView.findViewById(R.id.skill_cooltime);
        TextView skill_description = contentView.findViewById(R.id.skill_description);

        skill_name.setText(text1);
        skill_cooltime.setText(text2);
        skill_description.setText(text3);
    }

    public void showToolTip(View anchor) {

        tipWindow.setHeight(ConstraintLayout.LayoutParams.WRAP_CONTENT);
        tipWindow.setWidth(ConstraintLayout.LayoutParams.MATCH_PARENT);

        tipWindow.setOutsideTouchable(true);
        tipWindow.setTouchable(true);

        tipWindow.setContentView(contentView);

        int screen_pos[] = new int[2];
        // Get location of anchor view on screen
        anchor.getLocationOnScreen(screen_pos);

        // Get rect for anchor view
        Rect anchor_rect = new Rect(screen_pos[0], screen_pos[1], screen_pos[0]
                + anchor.getWidth(), screen_pos[1] + anchor.getHeight());

        // Call view measure to calculate how big your view should be.
        contentView.measure(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        int position_x = 0;
        int position_y = anchor_rect.bottom + 20 ;

        tipWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, position_x, position_y);
    }

    //This shows 'Checked' emoji on picture
    public void showChecking(View anchor){
        checkingViewWindow.setHeight(100);
        checkingViewWindow.setWidth(100);

        checkingViewWindow.setOutsideTouchable(true);
        checkingViewWindow.setTouchable(true);
        checkingViewWindow.setBackgroundDrawable(new BitmapDrawable());

        int screen_pos[] = new int[2];

        anchor.getLocationOnScreen(screen_pos);

        Rect anchor_rect = new Rect(screen_pos[0], screen_pos[1], screen_pos[0]
                + anchor.getWidth(), screen_pos[1] + anchor.getHeight());

        int position_x_ = ( anchor_rect.centerX() - 50 ); // this means center
        int position_y_ = ( anchor_rect.centerY() - 50 ); // this means center

        checkingViewWindow.setContentView(checkingView);
        checkingViewWindow.showAtLocation(anchor,Gravity.NO_GRAVITY,position_x_,position_y_);
    }

    public boolean isTooltipShown() {
        if (tipWindow != null && tipWindow.isShowing()
            &&checkingViewWindow != null && checkingViewWindow.isShowing())
            return true;
        return false;
    }

    public void dismissTooltip() {
        if (tipWindow != null && tipWindow.isShowing()
            && checkingViewWindow != null && checkingViewWindow.isShowing()){
            checkingViewWindow.dismiss();
            tipWindow.dismiss();
        }
    }

}