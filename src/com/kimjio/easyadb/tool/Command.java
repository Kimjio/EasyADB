package com.kimjio.easyadb.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public interface Command<T> {
    T runProcess(String... command) throws IOException, InterruptedException;
}
