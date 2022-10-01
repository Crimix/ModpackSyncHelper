package com.black_dog20.modpacksynchelper.utils;

import com.black_dog20.modpacksynchelper.ComponentBuilder;
import com.black_dog20.modpacksynchelper.components.ConsoleProgressBarConsumerWithCallback;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

import java.util.stream.Stream;

/**
 * Util for wrapping objects with a progress bar configured for use with my app
 */
public class ProgressBarUtil {

    /**
     * Wrap an iterable with a progressbar
     * @param iterable the iterable
     * @param title the title of the progressbar
     * @return a wrapped iterable
     */
    public static <T> Iterable<T> wrapWithProgressBar(Iterable<T> iterable, String title) {
        ProgressBarBuilder progressBarBuilder = getProgressBarBuilder(title);
        return ProgressBar.wrap(iterable, progressBarBuilder);
    }

    /**
     * Wrap a stream with a progressbar
     * @param stream the stream
     * @param title the title of the progressbar
     * @return a wrapped stream
     */
    public static <T> Stream<T> wrapWithProgressBar(Stream<T> stream, String title) {
        ProgressBarBuilder progressBarBuilder = getProgressBarBuilder(title);
        return ProgressBar.wrap(stream, progressBarBuilder);
    }

    private static ProgressBarBuilder getProgressBarBuilder(String title) {
        return new ProgressBarBuilder()
                .setTaskName(title)
                .setConsumer(new ConsoleProgressBarConsumerWithCallback(ComponentBuilder.PROGRESS_BAR_STREAM, 70))
                .setStyle(ProgressBarStyle.ASCII)
                .hideETA()
                .clearDisplayOnFinish();
    }
}
