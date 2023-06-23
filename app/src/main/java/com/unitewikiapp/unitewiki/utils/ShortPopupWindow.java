package com.unitewikiapp.unitewiki.utils;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.unitewikiapp.unitewiki.R;

public class ShortPopupWindow {

    // This class is designed to display a Popup window (similar to a toast message) , but with a custom UI.

    private static final int MSG_DISMISS_TOOLTIP = 100;
    private Context ctx;
    private PopupWindow tipWindow;
    private View contentView;
    private LayoutInflater inflater;

    public ShortPopupWindow(Context ctx) {
        this.ctx = ctx;
        tipWindow = new PopupWindow(ctx);

        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.popup_layout, null);
    }

    public void setText(String text1){
        TextView popup_text = contentView.findViewById(R.id.popup_text);

        popup_text.setText(text1);
    }

    public void showToolTip(View anchor) {

        tipWindow.setHeight(ConstraintLayout.LayoutParams.WRAP_CONTENT);
        tipWindow.setWidth(ConstraintLayout.LayoutParams.MATCH_PARENT);

        tipWindow.setOutsideTouchable(true);
        tipWindow.setTouchable(true);
        tipWindow.setFocusable(true);
        tipWindow.setBackgroundDrawable(new BitmapDrawable());

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

        int contentViewHeight = contentView.getMeasuredHeight();
        int contentViewWidth = contentView.getMeasuredWidth();
        int position_x = 0; // if you want to center value, use this : anchor_rect.centerX() - (contentViewWidth / 2)
        int position_y = 100 + anchor_rect.height() ; // if you want to center value, use this : anchor_rect.bottom - (anchor_rect.height()/2)

        tipWindow.showAtLocation(anchor, Gravity.BOTTOM, position_x, position_y);

        // send message to handler to dismiss tipWindow after X milliseconds
        handler.sendEmptyMessageDelayed(MSG_DISMISS_TOOLTIP, 1000);
    }

    public boolean isTooltipShown() {
        if (tipWindow != null && tipWindow.isShowing())
            return true;
        return false;
    }

    void dismissTooltip() {
        if (tipWindow != null && tipWindow.isShowing())
            tipWindow.dismiss();
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_DISMISS_TOOLTIP:
                    if (tipWindow != null && tipWindow.isShowing())
                        tipWindow.dismiss();
                    break;
            }
        }
        ;
    };


}