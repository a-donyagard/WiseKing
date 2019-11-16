package com.example.android.wiseking;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


public class PurchaseCoinDialog extends DialogFragment implements View.OnClickListener {

    public static final int TEN_COINS = 0;
    public static final int FIFTY_COINS = 1;
    public static final int HUNDRED_COINS = 2;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_purchase_coins, null);
        builder.setView(view);
        Button tenCoinsButton = (Button) view.findViewById(R.id.button_purchaseCoins_tenCoins);
        tenCoinsButton.setTag(TEN_COINS);
        tenCoinsButton.setOnClickListener(this);

        Button fiftyCoinsButton = (Button) view.findViewById(R.id.button_purchaseCoins_fiftyCoins);
        fiftyCoinsButton.setTag(FIFTY_COINS);
        fiftyCoinsButton.setOnClickListener(this);

        Button hundredCoinsButton = (Button) view.findViewById(R.id.button_purchaseCoins_hundredCoins);
        hundredCoinsButton.setTag(HUNDRED_COINS);
        hundredCoinsButton.setOnClickListener(this);

        return builder.create();
    }

    private OnProductSelected onProductSelected;

    public void setOnProductSelected(OnProductSelected onProductSelected) {
        this.onProductSelected = onProductSelected;
    }

    @Override
    public void onClick(View view) {
        if (onProductSelected != null) {
            onProductSelected.onProductSelected((Integer) view.getTag());
            dismiss();
        }
    }

    public interface OnProductSelected {
        void onProductSelected(int productId);
    }
}
