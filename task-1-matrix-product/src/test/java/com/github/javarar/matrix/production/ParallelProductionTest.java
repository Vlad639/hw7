package com.github.javarar.matrix.production;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParallelProductionTest {

    // Увеличение скорости работы наблюдается при вычислении произведения матриц размером 1000 и больше.
    // На малых размерах эффективнее использовать наивный подход.
    // При малых размерах на создание пула потоков тратится больше времени, чем на сами вычисления.
    // На больших размерах наблюдается прирост скорости вычисления в 1,5-2 раза.
    @DisplayName("Задание 2. Вычисление произведения квадратных матриц параллельно")
    @ParameterizedTest
    @ValueSource(ints = {3, 10, 100, 1000, 1500})
    void testParallelCalc(int size) throws InterruptedException, TimeoutException {
        double[][] a = MatrixUtils.generateRandomMatrix(size, size);
        double[][] b = MatrixUtils.generateRandomMatrix(size, size);

        long startTime = System.currentTimeMillis();
        double[][] expected = MatrixUtils.product(a, b);
        System.out.printf("Умножение матрицы размером %dX%<d в однопоточном режиме заняло %d млс. %n", size , System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        double[][] actual = MatrixUtils.productParallel(a, b, 1, TimeUnit.MINUTES);
        System.out.printf("Умножение матрицы размером %dX%<d в многопоточном режиме заняло %d млс. %n", size, System.currentTimeMillis() - startTime);

        assertTrue(Arrays.deepEquals(expected, actual));
    }
}
