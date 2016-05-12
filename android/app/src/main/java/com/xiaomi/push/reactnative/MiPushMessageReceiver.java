package com.xiaomi.push.reactnative;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.os.Bundle;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.util.List;
import java.util.HashMap;

public class MiPushMessageReceiver extends PushMessageReceiver {
    private String mRegId;
    private long mResultCode = -1;
    private String mReason;
    private String mCommand;
    private String mMessage;
    private String mTopic;
    private String mAlias;
    private String mStartTime;
    private String mEndTime;
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        if(!TextUtils.isEmpty(message.getTopic())) {
            mTopic=message.getTopic();
        } else if(!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }

        broadcastMessage(context, "RECEIVE_PASS_THROUGH", message.toBundle());
    }
    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        if(!TextUtils.isEmpty(message.getTopic())) {
            mTopic=message.getTopic();
        } else if(!TextUtils.isEmpty(message.getAlias())) {
            mAlias=message.getAlias();
        }

        broadcastMessage(context, "NOTIFICATION_MESSAGE_CLICKED", message.toBundle());
    }
    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        if(!TextUtils.isEmpty(message.getTopic())) {
            mTopic=message.getTopic();
        } else if(!TextUtils.isEmpty(message.getAlias())) {
            mAlias=message.getAlias();
        }

        broadcastMessage(context, "NOTIFICATION_MESSAGE_ARRIVED", message.toBundle());
    }
    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
            }
        }

        broadcastMessage(context, "COMMAND_RESULT");
    }
    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
            }
        }

        broadcastMessage(context, "RECEIVE_REGISTER_RESULT");
    }

    private void broadcastMessage(Context context, String type) {
        Bundle bundle = new Bundle();
        bundle.putString("regId", mRegId);
        bundle.putInt("resultCode", (int)mResultCode);
        bundle.putString("reason", mReason);
        bundle.putString("command", mCommand);
        bundle.putString("message", mMessage);
        bundle.putString("topic", mTopic);
        bundle.putString("alias", mAlias);
        bundle.putString("startTime", mStartTime);
        bundle.putString("endTime", mEndTime);

        broadcastMessage(context, type, bundle);
    }

    private void broadcastMessage(Context context, String type, Bundle bundle) {
        bundle.putString("type", type);
        HashMap<String, String> extra = (HashMap<String, String>)bundle.getSerializable("extra");
        if (extra != null) {
          Bundle extraBundle = new Bundle();
          for (String key: extra.keySet()) {
            String value = extra.get(key);
            extraBundle.putString(key, value);
          }
          bundle.putBundle("extra", extraBundle);
        }
        Intent intent = new Intent("xiaomipush");
        intent.putExtras(bundle);

        LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(context);
        mgr.sendBroadcast(intent);
    }
}
