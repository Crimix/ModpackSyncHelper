package com.black_dog20.modpacksynchelper.components;

import me.tongfei.progressbar.ConsoleProgressBarConsumer;

import java.io.PrintStream;

public class ConsoleProgressBarConsumerWithCallback extends ConsoleProgressBarConsumer {

    private String last;

    public ConsoleProgressBarConsumerWithCallback(PrintStream out) {
        super(out);
    }

    public ConsoleProgressBarConsumerWithCallback(PrintStream out, int maxRenderedLength) {
        super(out, maxRenderedLength);
    }

    @Override
    public void accept(String str) {
        if (!str.replace("\r", "").trim().isEmpty())
            last = str;
        super.accept(str);
    }

    @Override
    public void close() {
        super.close();
        System.out.println(last.replace("\r", ""));
    }
}
