package com.github.javarar.animal.faces;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final String source = "./dataset";
    private static final String target = "./handled_images";

    private static final long TIMEOUT_MIN = 5;
    private static final int GC_CALL_PERIOD_MLS = 1000;
    private static final int THREADS = 500;

    public static void main(String[] args) throws InterruptedException, IOException {
        Path targetPath = Paths.get(target);
        Files.createDirectories(targetPath);
        clearTarget();

        AnimalFacesPipeline filePipeline = new AnimalFacesPipeline(
                source,
                () -> new BlackWhiteHandler(target),
                TIMEOUT_MIN,
                TimeUnit.MINUTES,
                GC_CALL_PERIOD_MLS,
                THREADS);

        filePipeline.start();
        filePipeline.join();

        System.out.printf("Handled files store in %s %n", targetPath.toFile().getAbsolutePath());

        System.exit(0);
    }

    private static void clearTarget() throws IOException {
        Files.walk(Paths.get(target))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .forEach(File::delete);

        System.out.println("Target directory cleaned");
    }
}
