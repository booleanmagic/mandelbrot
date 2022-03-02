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

package com.github.booleannative.mandelbrot.plot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.github.booleannative.mandelbrot.plot.strategy.MandelbrotPlotStrategy;
import org.apache.commons.math3.complex.Complex;

import javax.swing.*;
import java.awt.*;

@RequiredArgsConstructor
@Slf4j
public class MandelbrotPanel extends JComponent {

    private final MandelbrotPlotStrategy paintStrategy;
    private Complex center;
    private double pixelIncrement;

    public void plot(Complex center, double pixelIncrement) {
        this.center = center;
        this.pixelIncrement = pixelIncrement;
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        if (center == null) {
            return;
        }
        long startTime = System.currentTimeMillis();
        try {
            paintStrategy.paintManelbrot(center, getWidth(), getHeight(), pixelIncrement, g);
        } finally {
            long stopTime = System.currentTimeMillis();
            log.info("frametime: {}s", (stopTime - startTime) / 1000.0);
        }
    }

}
