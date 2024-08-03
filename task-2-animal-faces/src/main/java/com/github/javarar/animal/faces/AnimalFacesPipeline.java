package com.github.javarar.animal.faces;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class AnimalFacesPipeline extends Thread {

    private final String source;
    private final Supplier<Consumer<File>> fileHandlerGenerator;
    private final long timeout;
    private final TimeUnit timeUnit;
    private final ExecutorService executorService;

    private long allFilesNumber;

    public AnimalFacesPipeline(String source, Supplier<Consumer<File>> fileHandlerGenerator, long timeout, TimeUnit timeUnit, long gcCallPeriodMls, int threads) {
        this.source = source;
        this.fileHandlerGenerator = fileHandlerGenerator;
        this.timeout = timeout;
        this.timeUnit = timeUnit;

        // Из-за большого количества мелких файлов в памяти получаем предупреждения от GC и можем упасть с OutOfMemory.
        // Поэтому периодически явно вызываем сборщик мусора.
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.gc();
            }
        }, 0, gcCallPeriodMls);

        this.executorService = Executors.newFixedThreadPool(threads);
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        Path sourcePath = Paths.get(source);

        try (Stream<Path> paths = Files.walk(sourcePath)) {
            paths.filter(path -> path.toFile().isFile())
                    .forEach(path -> {
                        allFilesNumber++;
                        executorService.execute(() -> fileHandlerGenerator.get().accept(path.toFile()));
                    });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        executorService.shutdown();
        try {
            boolean isAllSuccess = executorService.awaitTermination(timeout, timeUnit);
            if (isAllSuccess) {
                System.out.printf("All %d files from %s was handled %n", allFilesNumber, sourcePath.toFile().getAbsolutePath());
            } else {
                System.out.println("Timeout of files handled");
            }

            long duration = System.currentTimeMillis() - start;
            System.out.printf("Execution time %d mls. %n", duration);
            System.out.printf("Average file handle time = %d mls. %n", duration / allFilesNumber);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
