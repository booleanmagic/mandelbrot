/*
 * Copyright 2022 Mark Spoerndli
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.booleannative.mandelbrot;

import lombok.Data;
import org.apache.commons.math3.complex.Complex;

public class MandelbrotSet {
    private int iterations = 1000;

    public boolean isMemberOfMandelbrotSet(Complex c) {
        return checkIsMemberOfMandelbrotSet(c).isMemberOfMandelbrotSet();
    }

    public MandelbrotSetMembership checkIsMemberOfMandelbrotSet(Complex c) {
        int iteration = 0;
        double lastReal = 0d;
        double zImaginary = 0d;
        double cReal = c.getReal();
        double cImaginary = c.getImaginary();
        while (iteration < iterations) {
            double zReal = lastReal * lastReal - zImaginary * zImaginary + cReal;
            if (zReal < -2d || zReal > 2d) {
                return new MandelbrotSetMembership(false, iteration, iterations);
            }
            zImaginary = 2 * lastReal * zImaginary + cImaginary;
            lastReal = zReal;
            iteration++;
        }
        return new MandelbrotSetMembership(true, iterations, iterations);
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    @Data
    public static class MandelbrotSetMembership {
        final boolean isMemberOfMandelbrotSet;
        final int iterationsNeeded;
        final int maxIterations;
    }

}
