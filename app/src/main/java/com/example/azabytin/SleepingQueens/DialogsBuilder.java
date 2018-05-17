package com.example.azabytin.SleepingQueens;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

public class DialogsBuilder {

    private static ProgressDialog networkConnectProgressDialog = null;
    public static ProgressDialog buildNetworkConnectProgressDialog(Context context, DialogInterface.OnClickListener listener ){
        networkConnectProgressDialog = new ProgressDialog(context);
        networkConnectProgressDialog.setMessage("Жду второго игрока... ");
        networkConnectProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        networkConnectProgressDialog.setIndeterminate(true);
        networkConnectProgressDialog.setCancelable(false);

        networkConnectProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Отмена", listener);

        return networkConnectProgressDialog;
    }

    public static void dismissNetworkConnectProgressDialog(){
        if( networkConnectProgressDialog != null ){
            networkConnectProgressDialog.dismiss();
        }
    }

    public static AlertDialog buildGameTypeSelectorDialog(Context context, DialogInterface.OnClickListener listener ){
        CharSequence[] items = {"Играть с Андроидом", "Играть по сети вдвоем"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Выбери режим игры");

        builder.setItems(items, listener);
        builder.setCancelable(false);

        return builder.create();
    }
    public static AlertDialog buildGameoverDialog(Context context, String message, DialogInterface.OnClickListener listener ){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("Игра окончена");
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton( "Новая игра",listener );
        return builder.create();
    }
    public static AlertDialog buildSendFeedbackDialog(Activity parentActivity ){
        Intent feedbackEmail = new Intent(Intent.ACTION_SEND);
        feedbackEmail.setType("text/email");
        feedbackEmail.putExtra(Intent.EXTRA_EMAIL, new String[] {"azabytin@gmail.com"});
        feedbackEmail.putExtra(Intent.EXTRA_SUBJECT, "Отзыв");
        parentActivity.startActivity(Intent.createChooser(feedbackEmail, "Отправить отзыв ( Нужно выбрать Gmail ):"));

        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("Отправка отзыва");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage("Для отправки отзыва нужно выбрать Gmail");
        builder.setCancelable(false);
        builder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });
        return builder.create();
    }
}
