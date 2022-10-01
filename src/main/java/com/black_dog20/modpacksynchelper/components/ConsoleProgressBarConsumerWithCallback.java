package com.black_dog20.modpacksynchelper.components;

import me.tongfei.progressbar.ConsoleProgressBarConsumer;

import java.io.PrintStream;

/**
 * Custom class record the last progressbar message such that it can be written to my message console
 */
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
        if (!str.replace("\r", "").trim().isEmpty()) //The last line must not be just a blank line
            last = str;
        super.accept(str);
    }

    @Override
    public void close() {
        super.close();
        System.out.println(last.replace("\r", ""));
    }
}
