package com.skydhs.czclan.clan;

import java.util.logging.Logger;

public class Log {
    private static Logger logger;

    public Log(Core core) {
        Log.logger = core.getLogger();
    }
}