package org.jajaho.util;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static org.jajaho.util.MathUtil.determinant;

class MathUtilTest {
    double delta = 1e-10;       // double limited precision threshold

    // @Test
    // public void testDeterminant2x2() {
    //     double[][] a = { {1, 2}, {2, 1} };
    //     Assert.assertEquals(-3.0, determinant(a), delta);
    //     double[][] b = { {0, 0}, {0, 0} };
    //     Assert.assertEquals(0.0, determinant(b), delta);
    // }

    // @Test
    // public void testDeterminant3x3() {
    //     double[][] a = { {0, 0, 0}, {0, 0, 0}, {0, 0, 0} };
    //     Assert.assertEquals(0.0, determinant(a), delta);
    //     double[][] b = { {1, 2, 3}, {4, 5, 6}, {7, 8, 9} };
    //     Assert.assertEquals(0.0, determinant(b), delta);
    //     double[][] c = { {2, 2, -5}, {4, 5.6, -6}, {6.9, 8, 9} };
    //     Assert.assertEquals(75.2, determinant(c), delta);
    // }
    // @Test
    // public void testDeterminant4x4() {
    //     double[][] a = { {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0} };
    //     Assert.assertEquals(0.0, determinant(a), delta);
    //     double[][] b = { {2, 2, -5, 2.9}, {4, 5.6, -6, 9.233}, {6.9, 8, 9, -6}, {4, 4, 4, 4} };
    //     Assert.assertEquals(687.4004, determinant(b), delta);
    //     double[][] c = { {2, 2, -5, 2.9}, {4, 5.6, -6, 0}, {6.9, 8.0001, 9, -6}, {4, 0, 4, 4} };
    //     Assert.assertEquals(-302.8308, determinant(c), delta);
    // }
}