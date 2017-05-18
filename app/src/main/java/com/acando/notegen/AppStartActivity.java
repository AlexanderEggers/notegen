package com.acando.notegen;

import android.annotation.TargetApi;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class AppStartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean needsLegacyBackup = Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
        if (needsLegacyBackup) {
            checkForLegacyBackup();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            createShortcuts();
        }

        Intent intent = new Intent(this, NoteListActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkForLegacyBackup() {
        SharedPreferences preferences = getSharedPreferences("BackupInfo", Context.MODE_PRIVATE);
        long lastBackup = preferences.getLong("lastBackup", 0);
        long now = System.currentTimeMillis();

        if (now - lastBackup >= TimeUnit.HOURS.toMillis(24)) {
            BackupManager bm = new BackupManager(this);
            bm.dataChanged();
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("lastBackup", now);
        editor.apply();
    }

    @TargetApi(25)
    private void createShortcuts() {
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

        ShortcutInfo searchShortcut = new ShortcutInfo.Builder(this, "SearchAction")
                .setShortLabel("Search")
                .setLongLabel("Search for Notes")
                .setIcon(Icon.createWithResource(this, R.mipmap.ic_search))
                .setIntent(new Intent(Intent.ACTION_DEFAULT, Uri.EMPTY, this, SearchActivity.class))
                .build();

        ShortcutInfo recentShortcut = new ShortcutInfo.Builder(this, "AddNoteAction")
                .setShortLabel("Add Note")
                .setLongLabel("Add new Note")
                .setIcon(Icon.createWithResource(this, R.mipmap.ic_add_note))
                .setIntent(new Intent(Intent.ACTION_DEFAULT, Uri.EMPTY, this, DetailActivity.class))
                .build();

        shortcutManager.setDynamicShortcuts(Arrays.asList(searchShortcut, recentShortcut));
    }
}
