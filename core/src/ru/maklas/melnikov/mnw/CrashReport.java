package ru.maklas.melnikov.mnw;

public interface CrashReport {

    void report(Exception e);

    void report(String error);

}
