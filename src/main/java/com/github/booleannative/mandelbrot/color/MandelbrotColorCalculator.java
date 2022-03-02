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
package com.github.booleannative.mandelbrot.color;

import com.github.booleannative.mandelbrot.MandelbrotSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.complex.Complex;

import java.awt.*;

@RequiredArgsConstructor
@Slf4j
public class MandelbrotColorCalculator implements MandelbrotCoordinateColorCalculator {
    private float colorOffset = 0.4f;
    private float brightnessFactor =2.0f;
    private float saturation=0.4f;
    private float colorRange=0.5f;
    private final MandelbrotSet mandelBrotSet;

    public Color determineColor(Complex current) {
        return determineColor(mandelBrotSet.checkIsMemberOfMandelbrotSet(current));
    }

    public Color determineColor(MandelbrotSet.MandelbrotSetMembership mandelBrotSetMembership) {
        if (mandelBrotSetMembership.isMemberOfMandelbrotSet()) {
            return Color.BLACK;
        }
        float colorFactor=1.0f-(float)(Math.log(mandelBrotSetMembership.getIterationsNeeded())/Math.log(mandelBrotSetMembership.getMaxIterations()));
        return Color.getHSBColor(colorFactor*colorRange + colorOffset, saturation, 1 - (colorFactor / brightnessFactor));
    }

    public void shiftBaseColor() {
        colorOffset += 0.01;
        log.info("colorShift to {}", colorOffset);
    }

    public void increaseBrightness(){
        brightnessFactor *=1.5f;
        log.info("brightnessFactor = {}", brightnessFactor);
    }

    public void decreaseBrightness(){
        brightnessFactor *=0.75f;
        log.info("brightnessFactor = {}", brightnessFactor);
    }

    public void increaseSaturation(){
        saturation+=0.1;
        log.info("saturation = {}", saturation);
    }

    public void decreaseSaturation(){
        saturation-=0.1;
        log.info("saturation = {}", saturation);
    }

    public void increaseColorRange(){
        colorRange+=0.1;
        log.info("colorRange = {}", colorRange);
    }

    public void decreaseColorRange(){
        colorRange-=0.1;
        log.info("colorRange = {}", colorRange);
    }


}

