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

package com.github.booleannative.mandelbrot.plot.strategy;

import com.github.booleannative.mandelbrot.color.MandelbrotCoordinateColorCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.complex.Complex;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Slf4j
public class ParallelPlotStrategy implements MandelbrotPlotStrategy {

    private final MandelbrotCoordinateColorCalculator color;

    @Override
    public void paintManelbrot(Complex center, int width, int height, double pixelIncrement, Graphics g) {
        Complex topLeft = center.add(new Complex((-width / 2) * pixelIncrement, (-height / 2) * pixelIncrement));
        log.info("w: {}} h: {} topLeft: {}", width, height, topLeft);
        IntStream.range(0, height).parallel().forEach(row -> plotRow(row, width, pixelIncrement, topLeft, g));
    }

    private void plotRow(int row, int width, double pixelIncrement, Complex topLeft, Graphics g) {
        Complex current = new Complex(topLeft.getReal(), topLeft.getImaginary() + row * pixelIncrement);
        java.util.List<Color> colors = new ArrayList<>(width);
        for (int col = 0; col < width; col++) {
            colors.add(color.determineColor(current));
            log.trace("plotting ({},{}): {} -> {}", col, row, current, color);
            current = new Complex(current.getReal() + pixelIncrement, current.getImaginary());
        }
        synchronized (this) {
            for (int col = 0; col < width; col++) {
                g.setColor(colors.get(col));
                g.drawLine(col, row, col, row);
            }
        }
    }

}
