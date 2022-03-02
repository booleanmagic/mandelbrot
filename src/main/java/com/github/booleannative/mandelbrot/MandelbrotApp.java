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

import com.github.booleannative.mandelbrot.plot.MandelbrotPlot;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

@Slf4j
public class MandelbrotApp {
    private static final String INTERACTIVE_COMMANDS_MESSAGE = "Use the following keys to interactively change the plot while the application is running:" +
            "\n\tchange the center coordinates of the plot:\t CURSOR-keys" +
            "\n\tzoom in:\t\t\t\t\t '+' or 'I'" +
            "\n\tzoom out:\t\t\t\t\t '-' or 'O'" +
            "\n\tincrease number of iterations:\t\t\t 'F'" +
            "\n\tdecrease number of iterations:\t\t\t 'C'" +
            "\n\tshift hue of the base color:\t\t\t 'S'" +
            "\n\tincrease color range: \t\t\t\t 'R'" +
            "\n\tdecrease color range: \t\t\t\t 'E'" +
            "\n\tincrease brightness: \t\t\t\t 'B'" +
            "\n\tdecrease brightness: \t\t\t\t 'V'" +
            "\n\tincrease saturation: \t\t\t\t 'X'" +
            "\n\tdecrease saturation: \t\t\t\t 'Y'";
    private final Option iterations = Option.builder().option("i").longOpt("iterations").hasArg(true).argName("number").desc("set initial number of iterations").build();
    private final Option help = Option.builder().option("h").longOpt("help").hasArg(false).desc("show this help message").build();
    private final Options options = buildOptions();
    private final MandelbrotPlot mandelbrotPlot = new MandelbrotPlot();
    private String programName;

    public static void main(String[] args) {
        new MandelbrotApp().start(args);
    }

    private Options buildOptions() {
        Options options = new org.apache.commons.cli.Options();
        options.addOption(iterations);
        options.addOption(help);
        return options;
    }

    private void start(String[] args) {
        programName = new java.io.File(MandelbrotApp.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getName();

        try {
            CommandLine cmd = new DefaultParser().parse(options, args);
            handleHelp(cmd);
            handleIterations(cmd);
        } catch (ParseException e) {
            log.error("invalid options specified: {}", e.getMessage());
            System.exit(1);
        }
        mandelbrotPlot.plot();
    }

    private void handleIterations(CommandLine cmd) {
        if (cmd.hasOption(iterations)) {
            String iterationsArgument = cmd.getOptionValue(iterations);
            try {
                int initialIterations = Integer.parseInt(iterationsArgument);
                mandelbrotPlot.getMandelBrotSet().setIterations(initialIterations);
            } catch (NumberFormatException e) {
                log.error("invalid number of iterations was specifIed: '{}'", iterationsArgument);
                printHelp();
                System.exit(1);
            }
        }
    }

    private void handleHelp(CommandLine cmd) {
        if (cmd.hasOption(help)) {
            printHelp();
            System.exit(0);
        }
    }

    private void printHelp() {
        new HelpFormatter().printHelp("java -jar " + programName, options);
        System.out.println("\n\n"+INTERACTIVE_COMMANDS_MESSAGE);
    }
}
