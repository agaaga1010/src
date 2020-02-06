package uk.ac.cam.cl.mlrd.utils;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;
import uk.ac.cam.cl.mlrd.utils.BestFit.Point;

public class ChartPlotter {

    /**
     * Takes any number of lines in the form of a list of points, and plots the
     * lines on a chart
     *
     * @param lines
     *            {@link List}<{@link Point}><code>[]</code> The lines to be
     *            drawn
     */
    @SafeVarargs
    public static void plotLines(List<Point>... lines) { //Takes any number of arguments of type List<Point> called lines
        // ... = SPREAD OPERATOR
        List<Series<Number, Number>> serieses = new ArrayList<Series<Number, Number>>();
        double maxY = lines[0].get(0).y;
        double minY = maxY;
        for (List<Point> line : lines) {
            // defining a series
            Series<Number, Number> series = new Series<Number, Number>();
            ObservableList<Data<Number, Number>> data = series.getData();

            for (Point point : line) {
                Double xCoord = point.x;
                Double yCoord = point.y;
                if (maxY < point.y) {
                    maxY = point.y;
                }
                if (minY > point.y) {
                    minY = point.y;
                }
                data.add(new Data<Number, Number>(xCoord, yCoord));
            }

            serieses.add(series);
        }
        Chart.displayContent(serieses);
    }

    protected static class Chart extends Application implements Runnable {

        private static Chart graph = null;

        public Chart() {
            graph = this;
        }

        public static void displayContent(List<Series<Number, Number>> lineList) {
            if (graph == null) {
                GraphSync.getInstance().waitForInitialise();
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Stage stage = new Stage();

                    final NumberAxis xAxis = new NumberAxis();
                    final NumberAxis yAxis = new NumberAxis();


                    final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);

                    for (Series<Number, Number> line : lineList) {
                        lineChart.getData().add(line);
                    }

                    lineChart.setLegendVisible(false);

                    stage.setScene(new Scene(lineChart, 800, 600));
                    stage.show();
                }
            });
        }

        @Override
        public void start(Stage stage) {
            GraphSync.getInstance().initialisationComplete();
        }

        @Override
        public void run() {
            try {
                launch();
            } catch (IllegalStateException e) {
                start(new Stage());
            }
        }

        private static class GraphSync {
            private static Boolean initialised = false;
            private static Boolean initialising = false;
            private static GraphSync sync = null;

            public static synchronized GraphSync getInstance() {
                if (sync == null) {
                    sync = new GraphSync();
                }
                return sync;
            }

            synchronized void waitForInitialise() {
                if (!initialised) {
                    if (!initialising) {
                        new Thread(new Chart()).start();
                        initialising = true;
                    }
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            synchronized void initialisationComplete() {
                initialised = true;
                notify();
            }
        }
    }
}
