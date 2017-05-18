package com.acando.notegen.database;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;

public class BackupAgent extends BackupAgentHelper {

    private static final String HELPER_KEY_PREFIX = "database", FILE_NAME = "content.db";

    @Override
    public void onCreate() {
        addHelper(HELPER_KEY_PREFIX, new FileBackupHelper(this, FILE_NAME));
    }
}
