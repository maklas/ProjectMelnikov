package ru.maklas.melnikov.mnw;

import ru.maklas.melnikov.utils.Log;

public class NoCrashReport implements CrashReport {

    @Override
    public void report(Exception e) {
        Log.error("CRASH REPORT", e.getMessage() == null ? "" : e.getMessage(), e);
    }

    @Override
    public void report(String error) {
        Log.error("CRASH REPORT", error);
    }
}
