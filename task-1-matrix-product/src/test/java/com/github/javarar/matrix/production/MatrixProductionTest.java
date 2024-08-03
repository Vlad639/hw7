package com.github.javarar.matrix.production;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MatrixProductionTest {

    @DisplayName("Задание 2. Вычисление произведения матриц")
    @ParameterizedTest
    @MethodSource("matrixProducer")
    public void validateMatrixProduction(double[][] a, double[][] b, double[][] expected) {
        double[][] actual = MatrixUtils.product(a, b);
        assertTrue(Arrays.deepEquals(expected, actual));
    }

    private static Stream<Arguments> matrixProducer() {
        return Stream.of(
                Arguments.of(
                        new double[][]{
                                new double[]{1d, 5d},
                                new double[]{2d, 3d},
                                new double[]{1d, 7d}
                        },

                        new double[][]{
                                new double[]{1d, 2d, 3d, 7d},
                                new double[]{5d, 2d, 8d, 1d}
                        },

                        new double[][]{
                                new double[]{26d, 12d, 43d, 12d},
                                new double[]{17d, 10d, 30d, 17d},
                                new double[]{36d, 16d, 59d, 14d}
                        }
                ),

                Arguments.of(
                        new double[][]{
                                new double[]{2d, 1d, 1d},
                                new double[]{0d, 3d, 2d}
                        },

                        new double[][]{
                                new double[]{0d, 3d},
                                new double[]{1d, 5d},
                                new double[]{-1d, 1d},
                        },

                        new double[][]{
                                new double[]{0d, 12d},
                                new double[]{1d, 17d},
                        }
                ),

                Arguments.of(
                        new double[][]{
                                new double[]{1d, 2d},
                                new double[]{3d, 4d}
                        },

                        new double[][]{
                                new double[]{2d, 1d, 3d},
                                new double[]{0d, 0d, 1d},
                        },

                        new double[][]{
                                new double[]{2d, 1d, 5d},
                                new double[]{6d, 3d, 13d},
                        }
                )
        );
    }
}
