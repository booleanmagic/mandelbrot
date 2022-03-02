# @booleannative Mandelbrot renderer

A java application that renders the mandelbrot set.
It features parallelization using parallel streams and a number of optimizations:
* the main optimization is to avoid calculation of set membership for points that are surrounded entirely by points that belong to the set
  * because any such point always belongs to the set as well
  * therefore the the area to be rendered is recursively split into four rectangles. For each rectangle being drawn, the set membership is then calculated for the points in the outline of the rectangle. If all points in the outline are part of the set, then set membership calculation for the remaining points in the rectangle is skipped, as those all belong to the set. Otherwise, the renderer continues to recursively sub-divides the rectangle into smaller rectangles and calculate the set membership of their outlines.
* further optimization is achieved by avoiding recursive method calls and any heap allocations in the iterative set membership calculation


## Usage

## Building and running
Build it: `gradle jar`

Run it: `java -jar build/libs/Mandelbrot-1.0.jar`

## Controlling the rendering of the Mandelbrot set
The rendering of the Mandelbrot set can be controlled using the following keys while the application is running:
```
change the center coordinates:      CURSOR-keys
zoom in:                            '+' or 'I'
zoom out:                           '-' or 'O'
increase number of iterations:	    'F'
decrease number of iterations:	    'C'
shift hue of the base color:	    'S'
increase color range: 	            'R'
decrease color range: 	            'E'
increase brightness: 	            'B'
decrease brightness: 	            'V'
increase saturation:                'X'
decrease saturation:                'Y'
```
## License
Copyright 2022 Mark Spoerndli

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
