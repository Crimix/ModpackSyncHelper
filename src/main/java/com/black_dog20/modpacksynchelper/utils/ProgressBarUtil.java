package com.black_dog20.modpacksynchelper.utils;

import com.black_dog20.modpacksynchelper.ComponentBuilder;
import com.black_dog20.modpacksynchelper.components.ConsoleProgressBarConsumerWithCallback;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

import java.util.stream.Stream;

public class ProgressBarUtil {

    public static <T> Stream<T> wrapWithProgressBar(Stream<T> stream, String title) {
        ProgressBarBuilder progressBarBuilder = new ProgressBarBuilder()
                .setTaskName(title)
                .setConsumer(new ConsoleProgressBarConsumerWithCallback(ComponentBuilder.PROGRESS_BAR_STREAM, 70))
                .setStyle(ProgressBarStyle.ASCII)
                .hideETA()
                .clearDisplayOnFinish();
        return ProgressBar.wrap(stream, progressBarBuilder);
    }
}
