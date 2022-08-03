package org.jajaho.math;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static org.jajaho.math.MathUtil.determinant;

class MathUtilTest {
    double delta = 1e-10;

    @Test
    public void testDeterminant2x2() {
        double[][] a = { {1, 2}, {2, 1} };
        Assert.assertEquals(-3.0, determinant(a), delta);
        double[][] b = { {0, 0}, {0, 0} };
        Assert.assertEquals(0.0, determinant(b), delta);
    }

    @Test
    public void testDeterminant3x3() {
        double[][] a = { {0, 0, 0}, {0, 0, 0}, {0, 0, 0} };
        Assert.assertEquals(0.0, determinant(a), delta);
        // double[][] b = {{0, 1},{2, 0}};
        // Assert.assertEquals(0.0, determinant(b), delta);
    }
    @Test
    void cramersRule() {
    }
}