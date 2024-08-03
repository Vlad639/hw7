package com.github.javarar.matrix.production;

import it.unimi.dsi.util.XorShift1024StarPhiRandom;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MatrixUtils {

    private final static Random random = new XorShift1024StarPhiRandom();

    public static double[][] generateRandomMatrix(int n, int m) {
        if (n < 1 || m < 1) {
            throw new IllegalArgumentException("Size of matrix must be positive");
        }

        double[][] matrix = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                matrix[i][j] = random.nextDouble();
            }
        }

        return matrix;
    }

    public static double[][] productParallel(double[][] a, double[][] b, long timeout, TimeUnit timeUnit) throws InterruptedException, TimeoutException {
        checkIsMatrixValid(a);
        checkIsMatrixValid(b);

        int aColumn = a[0].length;
        int bRow = b.length;

        if (aColumn != bRow) {
            throw new IllegalArgumentException("Number of column in first matrix must be equal second matrix row number");
        }

        int aRows = a.length;
        int bColumns = b[0].length;
        double[][] result = new double[aRows][bColumns];

        ExecutorService executorService = Executors.newFixedThreadPool(aColumn);
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                int finalI = i;
                int finalJ = j;
                executorService.execute(() -> result[finalI][finalJ] = calcCell(a, b, finalI, finalJ));
            }
        }

        executorService.shutdown();
        boolean isAllSuccess = executorService.awaitTermination(timeout, timeUnit);
        if (!isAllSuccess) {
            throw new TimeoutException("Timeout calc matrix");
        }

        return result;
    }

    public static double[][] product(double[][] a, double[][] b) {
        checkIsMatrixValid(a);
        checkIsMatrixValid(b);

        int aColumn = a[0].length;
        int bRow = b.length;

        if (aColumn != bRow) {
            throw new IllegalArgumentException("Number of column in first matrix must be equal second matrix row number");
        }

        int aRows = a.length;
        int bColumns = b[0].length;
        double[][] result = new double[aRows][bColumns];
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                result[i][j] = calcCell(a, b, i, j);
            }
        }


        return result;
    }

    private static double calcCell(double[][] a, double[][] b, int row, int col) {
        double cell = 0;
        for (int i = 0; i < b.length; i++) {
            cell += a[row][i] * b[i][col];
        }
        return cell;
    }

    public static void checkIsMatrixValid(double[][] matrix) {
        // Проверяем, что все строки имеют одинаковую длину
        int columnNumber = matrix[0].length;
        for (double[] row : matrix) {
            if (row.length != columnNumber) {
                throw new IllegalArgumentException("Incorrect matrix");
            }
        }
    }

    private MatrixUtils() {
        throw new IllegalStateException("Utility class");
    }
}
