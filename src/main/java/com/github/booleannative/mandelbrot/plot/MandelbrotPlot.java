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

import com.github.booleannative.mandelbrot.MandelbrotSet;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import com.github.booleannative.mandelbrot.color.MandelbrotColorCalculator;
import com.github.booleannative.mandelbrot.plot.strategy.RecursivelyDetectEnclosedRectanglesPlotStrategy;
import org.apache.commons.math3.complex.Complex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

@Slf4j
public class MandelbrotPlot {

    @Getter
    private final MandelbrotSet mandelBrotSet = new MandelbrotSet();
    private final MandelbrotColorCalculator mandelbrotColor = new MandelbrotColorCalculator(mandelBrotSet);
    private final MandelbrotPanel mandelbrotPanel = new MandelbrotPanel(new RecursivelyDetectEnclosedRectanglesPlotStrategy(mandelbrotColor));
    private final int movePixels = 40;
    private Complex center = new Complex(-0.5, 0);
    private double pixelIncrement = 0.004d;


    public MandelbrotPlot() {
        JFrame mandelFrame = new JFrame();
        mandelbrotPanel.setPreferredSize(new Dimension(750, 500));
        mandelFrame.add(mandelbrotPanel);
        mandelFrame.pack();
        mandelFrame.setVisible(true);
        mandelFrame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        left();
                        break;
                    case KeyEvent.VK_RIGHT:
                        rigth();
                        break;
                    case KeyEvent.VK_UP:
                        up();
                        break;
                    case KeyEvent.VK_DOWN:
                        down();
                        break;
                    case KeyEvent.VK_PLUS:
                    case KeyEvent.VK_I:
                        zoomIn();
                        break;
                    case KeyEvent.VK_MINUS:
                    case KeyEvent.VK_O:
                        zoomOut();
                        break;
                    case KeyEvent.VK_F:
                        finer();
                        break;
                    case KeyEvent.VK_C:
                        coarser();
                        break;
                    case KeyEvent.VK_S:
                        colorShift();
                        break;
                    case KeyEvent.VK_R:
                        increaseColorRange();
                        break;
                    case KeyEvent.VK_E:
                        decreaseColorRange();
                        break;
                    case KeyEvent.VK_B:
                        mandelbrotColor.increaseBrightness();
                        plot();
                        break;
                    case KeyEvent.VK_V:
                        mandelbrotColor.decreaseBrightness();
                        plot();
                        break;
                    case KeyEvent.VK_X:
                        mandelbrotColor.increaseSaturation();
                        plot();
                        break;
                    case KeyEvent.VK_Y:
                        mandelbrotColor.decreaseSaturation();
                        plot();
                        break;
                }
            }
        });
    }

    private void decreaseColorRange() {
        mandelbrotColor.decreaseColorRange();
        plot();
    }

    private void increaseColorRange() {
        mandelbrotColor.increaseColorRange();
        plot();
    }

    private void colorShift() {
        mandelbrotColor.shiftBaseColor();
        plot();
    }

    private void finer() {
        mandelBrotSet.setIterations((int) (mandelBrotSet.getIterations() * 1.5));
        plot();
    }

    private void coarser() {
        if (mandelBrotSet.getIterations() < 5) {
            return;
        }
        mandelBrotSet.setIterations((int) (mandelBrotSet.getIterations() / 1.5));
        plot();
    }

    public void zoomIn() {
        pixelIncrement /= 1.2;
        plot();
    }

    public void zoomOut() {
        pixelIncrement *= 1.2;
        plot();
    }

    public void up() {
        center = center.add(new Complex(0, movePixels * pixelIncrement));
        plot();
    }

    public void down() {
        center = center.add(new Complex(0, -movePixels * pixelIncrement));
        plot();
    }

    public void left() {
        center = center.add(new Complex(movePixels * -pixelIncrement, 0));
        plot();
    }

    public void rigth() {
        center = center.add(new Complex(movePixels * pixelIncrement, 0));
        plot();
    }

    public void plot() {
        log.info("plotting, increment: {} iterations: {} center: {}", pixelIncrement, mandelBrotSet.getIterations(), center);
        mandelbrotPanel.plot(center, pixelIncrement);
    }

}
