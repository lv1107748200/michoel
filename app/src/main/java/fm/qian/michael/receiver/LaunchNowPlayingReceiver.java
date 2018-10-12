/*
 * Copyright (C) 2014 Saravan Pantham
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fm.qian.michael.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import fm.qian.michael.ui.activity.MainActivity;


public class LaunchNowPlayingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

//        if (MusicPlayer.isPlaying()) {
        Intent activityIntent = new Intent(context.getApplicationContext(), MainActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(activityIntent);
        Intent intent1 = new Intent();
        intent1.setComponent(new ComponentName("fm.qian.michael", "fm.qian.michael.ui.activity.dim.PlayActivity.class"));
        context.sendBroadcast(intent1);
//        }

    }

}
