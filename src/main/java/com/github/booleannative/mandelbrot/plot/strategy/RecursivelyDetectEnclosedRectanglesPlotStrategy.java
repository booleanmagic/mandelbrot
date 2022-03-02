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
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;

@RequiredArgsConstructor
@Slf4j
public class RecursivelyDetectEnclosedRectanglesPlotStrategy implements MandelbrotPlotStrategy {
    private final MandelbrotCoordinateColorCalculator color;
    private final boolean debugEnclosedRectangleDetection = System.getProperty("debugEnclosedRectangleDetection") != null;

    @Override
    public void paintManelbrot(Complex center, int width, int height, double pixelIncrement, Graphics g) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        paintManelbrotImage(center, width, height, pixelIncrement, image);
        g.drawImage(image, 0, 0, (img, flags, x, y, w, h) -> false);
        log.debug("mandelbrot painted: w: {} h: {}", width, height);
    }

    protected void paintManelbrotImage(Complex center, int width, int height, double pixelIncrement, BufferedImage image) {
        paintRectangles(new Point(0, 0), center.add(new Complex(-width * pixelIncrement / 2d, height * pixelIncrement / 2d)), width, height, pixelIncrement, image, false);
    }

    private void paintRectangles(Point screenTopLeft, Complex topLeft, int width, int height, double pixelIncrement, BufferedImage image, boolean isFill) {
        if (width < 1 || height < 1) {
            return;
        }
        IntStream.rangeClosed(1, 4).parallel().forEach(i -> paintQuadrant(i, screenTopLeft, topLeft, width, height, pixelIncrement, image, isFill));
    }

    private void paintQuadrant(int quadrant, Point parentScreenTopLeft, Complex parentTopLeft, int parentWidth, int parentHeight, double pixelIncrement, BufferedImage image, boolean isFill) {
        int topLeftOffsetX;
        int topLeftOffsetY;
        int width;
        int height;
        switch (quadrant) {
            case 1:
                width = (int) (floor(parentWidth / 2d));
                height = (int) (floor(parentHeight / 2d));
                topLeftOffsetX = parentWidth - width;
                topLeftOffsetY = 0;
                break;
            case 2:
                width = (int) (ceil(parentWidth / 2d));
                height = (int) (floor(parentHeight / 2d));
                topLeftOffsetX = 0;
                topLeftOffsetY = 0;
                break;
            case 3:
                width = (int) (ceil(parentWidth / 2d));
                height = (int) (ceil(parentHeight / 2d));
                topLeftOffsetX = 0;
                topLeftOffsetY = parentHeight - height;
                break;
            case 4:
                width = (int) (floor(parentWidth / 2d));
                height = (int) (ceil(parentHeight / 2d));
                topLeftOffsetX = parentWidth - width;
                topLeftOffsetY = parentHeight - height;
                break;
            default:
                throw new IllegalArgumentException();
        }
        if (width < 1 || height < 1) {
            return;
        }
        Point screenTopLeft = new Point(parentScreenTopLeft.x + topLeftOffsetX, parentScreenTopLeft.y + topLeftOffsetY);
        Complex topLeft = parentTopLeft.add(new Complex(topLeftOffsetX * pixelIncrement, -topLeftOffsetY * pixelIncrement));
        int screenLeftX = screenTopLeft.x;
        int screenRightX = screenTopLeft.x + width - 1;
        int screenTopY = screenTopLeft.y;
        int screenBottomY = screenTopLeft.y + height - 1;
        if (log.isTraceEnabled()) {
            log.trace("painting quadrant: {} Screentopleft: {}} w: {}} h: {}", quadrant, screenTopLeft, width, height);
            log.trace("screenLeftX: {} screenRightX: {} screenTopY: {} screenBottomY: {}", screenLeftX, screenRightX, screenTopY, screenBottomY);
            log.trace("topLeft: {}", topLeft);
        }
        boolean horizontalAllMandelbrot = drawHorizontalLines(width, screenLeftX, screenTopY, screenBottomY, pixelIncrement, topLeft, image, isFill);
        boolean verticalAllMandelbrot = drawVerticalLines(height, screenLeftX, screenTopY, screenRightX, pixelIncrement, topLeft, image, isFill);
        isFill = isFill || (horizontalAllMandelbrot && verticalAllMandelbrot);
        paintRectangles(new Point(screenTopLeft.x + 1, screenTopLeft.y + 1), topLeft.add(new Complex(pixelIncrement, -pixelIncrement)), width - 2, height - 2, pixelIncrement, image, isFill);
    }

    private boolean drawVerticalLines(int height, int screenLeftX, int screenTopY, int screenRightX, double pixelIncrement, Complex topLeft, BufferedImage image, boolean isFill) {
        boolean isAllPointsInMandelbrot = true;
        Complex leftPosition = topLeft.add(new Complex(0d, -pixelIncrement));
        Complex rightPosition = topLeft.add(new Complex(pixelIncrement * (screenRightX - screenLeftX), -pixelIncrement));
        //start at topY+1 and use height-2, as corner point are already drawn in horizontal lines
        int screenY = screenTopY + 1;
        for (int i = 0; i < height - 2; i++) {
            isAllPointsInMandelbrot=plotPixel(leftPosition,isAllPointsInMandelbrot,isFill,screenLeftX,screenY,image);
            if (screenLeftX != screenRightX) {
                isAllPointsInMandelbrot=plotPixel(rightPosition,isAllPointsInMandelbrot,isFill,screenRightX,screenY,image);
            }
            screenY++;
            leftPosition = leftPosition.subtract(new Complex(0d, pixelIncrement));
            rightPosition = rightPosition.subtract(new Complex(0d, pixelIncrement));
        }
        return isAllPointsInMandelbrot;
    }

    private boolean drawHorizontalLines(int width, int screenLeftX, int screenTopY, int screenBottomY, double pixelIncrement, Complex topLeft, BufferedImage image, boolean isFill) {
        boolean isAllPointsInMandelbrot = true;
        Complex topPosition = topLeft;
        Complex bottomPosition = topLeft.add(new Complex(0d, -pixelIncrement * (screenBottomY - screenTopY)));
        int screenX = screenLeftX;
        for (int i = 0; i < width; i++) {
            isAllPointsInMandelbrot=plotPixel(topPosition,isAllPointsInMandelbrot,isFill,screenX,screenTopY,image);
            if (screenTopY != screenBottomY) {
                isAllPointsInMandelbrot=plotPixel(bottomPosition,isAllPointsInMandelbrot,isFill,screenX,screenBottomY,image);
            }
            screenX++;
            topPosition = topPosition.add(pixelIncrement);
            bottomPosition = bottomPosition.add(pixelIncrement);
        }
        return isAllPointsInMandelbrot;
    }

    private boolean plotPixel(Complex complexCoordinates, boolean isAllPointsInMandelbrot, boolean isFill, int x, int y, BufferedImage image) {
        Color color = determineColor(complexCoordinates, isFill);
        image.setRGB(x, y, color.getRGB());
        return isAllPointsInMandelbrot && (isFill || color.equals(Color.BLACK));
    }

    private Color determineColor(Complex position, boolean isFill) {
        if (isFill) {
            if (debugEnclosedRectangleDetection) {
                return Color.PINK;
            }
            return Color.BLACK;
        }
        return color.determineColor(position);
    }
}
