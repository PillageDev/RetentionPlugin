package dev.pillage.retention.utils;

import co.aikar.idb.DB;

public class SQLInit {

    public static void initSql() {
        DB.executeUpdateAsync("CREATE TABLE IF NOT EXISTS regions(id TEXT PRIMARY KEY, data TEXT);");
        DB.executeUpdateAsync("CREATE TABLE IF NOT EXISTS players(uuid TEXT PRIMARY KEY, data TEXT);");
    }
}
