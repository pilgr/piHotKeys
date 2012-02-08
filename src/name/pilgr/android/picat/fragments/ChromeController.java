package name.pilgr.android.picat.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import name.pilgr.android.picat.R;
import name.pilgr.android.picat.model.areas.ChromeArea;
import name.pilgr.android.picat.utils.Log;

public class ChromeController extends CommonController {
    private Button btnNextTab, btnPreviousTab, btnHistory;
    private int horizontalScrollCount = 0;

    public ChromeController(OnActionListener listener_) {
        super(ChromeArea.class, listener_);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frg_chrome, container, false);

        btnNextTab = (Button) v.findViewById(R.id.btn_chrome_next_tab);
        btnPreviousTab = (Button) v.findViewById(R.id.btn_chrome_previous_tab);

        return v;
    }

    void setActionListener() {
        setOnClickAction(btnNextTab, ChromeArea.SWITCH_TO_NEXT_TAB);
        setOnClickAction(btnPreviousTab, ChromeArea.SWITCH_TO_PREVIOUS_TAB);
    }

    @Override
    public void onScrollLeft(float distance) {
        horizontalScrollCount = horizontalScrollCount < 0 ? horizontalScrollCount - 1 : -1;
        if (horizontalScrollCount % 5 == 0) {
            doAction(ChromeArea.SWITCH_TO_PREVIOUS_TAB);
            Log.d("scroll left");
        }
        Log.d("horizontalScrollCount = " + horizontalScrollCount);
    }

    @Override
    public void onScrollRight(float distance) {
        horizontalScrollCount = horizontalScrollCount > 0 ? horizontalScrollCount + 1 : 1;
        if (horizontalScrollCount % 5 == 0) {
            doAction(ChromeArea.SWITCH_TO_NEXT_TAB);
            Log.d("scroll right");
        }
        Log.d("horizontalScrollCount = " + horizontalScrollCount);
    }

}
