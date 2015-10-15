package com.btmessenger.khe.btmessenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsListener extends BroadcastReceiver {
    /*private MessengerActivity _parent;
    public SmsListener(MessengerActivity parent) {
        _parent=parent;
    }*/

    @Override
    public void onReceive(Context context, Intent intent)
    {
        SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        msgs[0].getOriginatingAddress();
        Toast.makeText(context, msgs[0].getOriginatingAddress(), Toast.LENGTH_SHORT).show();
        //_parent.onReceive(msgs[0].getOriginatingAddress().toString(), msgs[0].getMessageBody().toString());
////—get the SMS message passed in—
//        Bundle bundle = intent.getExtras();
//        SmsMessage[] msgs = null;
//        String str = "";
//        if (bundle != null)
//        {
////—retrieve the SMS message received—
//            Object[] pdus = (Object[]) bundle.get("pdus");
//            msgs = new SmsMessage[pdus.length];
//            for (int i=0; i<msgs.length; i++){
//                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i],);
//                str += "SMS from " + msgs[i].getOriginatingAddress();
//                str += " :";
//                str += msgs[i].getMessageBody().toString();
//                str += "\n";
//                _parent.onReceive(msgs[i].getOriginatingAddress().toString(), msgs[i].getMessageBody().toString());
//
//
//            }
//—display the new SMS message—
 //        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
   //     }
    }
}



