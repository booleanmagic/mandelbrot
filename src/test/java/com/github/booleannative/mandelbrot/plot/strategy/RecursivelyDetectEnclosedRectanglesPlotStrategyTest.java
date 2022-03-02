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

import com.github.booleannative.mandelbrot.MandelbrotSet;
import com.github.booleannative.mandelbrot.color.MandelbrotCoordinateColorCalculator;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.math3.complex.Complex;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecursivelyDetectEnclosedRectanglesPlotStrategyTest {

    @Test
    void checkCorrectNumberOfPixelsDrawn() {
        RecursivelyDetectEnclosedRectanglesPlotStrategy strategy = new RecursivelyDetectEnclosedRectanglesPlotStrategy(new MandelbrotCoordinateColorCalculator() {

            @Override
            public Color determineColor(Complex current) {
                return Color.BLUE;
            }

            @Override
            public Color determineColor(MandelbrotSet.MandelbrotSetMembership mandelBrotResult) {
                return Color.BLUE;
            }
        });
        MutableObject<Integer> numberOfPixelsDrawn=new MutableObject<>(0);

        BufferedImage dummyImage=new BufferedImage(1,1,  BufferedImage.TYPE_INT_ARGB){
            @Override
            public void setRGB(int x, int y, int color){
                synchronized (this) {
                    numberOfPixelsDrawn.setValue(numberOfPixelsDrawn.getValue()+1);
                }
            }
        };
        strategy.paintManelbrotImage(new Complex(0, 0), 750, 500, 0.0001d, dummyImage);
        assertEquals(750 * 500, numberOfPixelsDrawn.getValue());
        numberOfPixelsDrawn.setValue(0);
        strategy.paintManelbrotImage(new Complex(0, 0), 898, 517, 0.0001d, dummyImage);
        assertEquals(898 * 517, numberOfPixelsDrawn.getValue());
        numberOfPixelsDrawn.setValue(0);
        strategy.paintManelbrotImage(new Complex(0, 0), 331, 673, 0.0001d, dummyImage);
        assertEquals(331 * 673, numberOfPixelsDrawn.getValue());
        numberOfPixelsDrawn.setValue(0);
    }
}