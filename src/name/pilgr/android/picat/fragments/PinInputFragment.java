package name.pilgr.android.picat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import name.pilgr.android.picat.ConnectivityManager;
import name.pilgr.android.picat.PiApplication;
import name.pilgr.android.picat.R;

/**
 * Created by IntelliJ IDEA.
 * User: pilgr
 * Date: 09.08.11
 * Time: 22:55
 * To change this template use File | Settings | File Templates.
 */
public class PinInputFragment extends Fragment {
    private ConnectivityManager connectivityManager;
    private Button btnEnterPin;
    private EditText edtPinValue;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PiApplication application = (PiApplication) getActivity().getApplication();
        connectivityManager = application.getConnectivityManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.pin_fragment, container, false);
        btnEnterPin = (Button) v.findViewById(R.id.btn_enter_pin);
        edtPinValue = (EditText) v.findViewById(R.id.edt_pin_value);
        btnEnterPin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view_) {
                hideKeyboard();
                connectivityManager.sendPin(edtPinValue.getText().toString(), true);
            }
        });
        return v;
    }

    public void incorrectPin() {
        Toast.makeText(getActivity(), R.string.toast_incorrect_pin, Toast.LENGTH_LONG).show();
        if (edtPinValue != null) {
            edtPinValue.setText("");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (edtPinValue != null) {
            edtPinValue.setText("");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        hideKeyboard();
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
