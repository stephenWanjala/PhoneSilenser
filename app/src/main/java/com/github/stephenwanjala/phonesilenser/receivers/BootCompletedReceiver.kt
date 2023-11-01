package com.github.stephenwanjala.phonesilenser.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.stephenwanjala.phonesilenser.MainActivity

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // get the action from the intent
        val action = intent.action

        // check if the action is equal to boot completed
        if (action == Intent.ACTION_BOOT_COMPLETED) {
            // create an intent for the main activity
            val mainActivityIntent = Intent(context, MainActivity::class.java)
            // set the intent flags to start a new task
            mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            // start the main activity
            context.startActivity(mainActivityIntent)
        }
    }
}