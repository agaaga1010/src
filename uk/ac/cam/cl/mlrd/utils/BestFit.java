package uk.ac.cam.cl.mlrd.utils;

import java.util.Map;
import java.util.Map.Entry;

public class BestFit {

    /**
     * Wrapper for two doubles denoting X and Y values of a point in 2d space.
     * Used in parameter for leastSquares algorithm.
     */
    public static class Point {
        public final double x;
        public final double y;

        /**
         * Creates a new point with the given coordinates
         *
         * @param x
         *            <code>double</code> The X coordinate
         * @param y
         *            <code>double</code> The Y coordinate
         */
        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(x) ^ Double.hashCode(y);
        }

        @Override
        public String toString() {
            return "[" + x + ", " + y + "]";
        }
    }

    /**
     * Wrapper for two doubles denoting gradient and Y intercept of a 2d line.
     * Returned by leastSquares algorithm.
     */
    public static class Line {
        public final double gradient;
        public final double yIntercept;

        /**
         * Creates a new line with the given gradient and intercept
         *
         * @param gradient
         *            <code>double</code> The gradient
         * @param yIntercept
         *            <code>double</code> The Y intercept
         */
        public Line(double gradient, double yIntercept) {
            this.gradient = gradient;
            this.yIntercept = yIntercept;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(gradient) ^ Double.hashCode(yIntercept);
        }

        @Override
        public String toString() {
            return "[" + gradient + ", " + yIntercept + "]";
        }
    }

    /**
     * Uses linear least squares regression to calculate a line of best fit for
     * the given points. Points can be weighted so that some points are given
     * more importance than others - you should use this in your Zipf estimate
     * to weight words by their frequency.
     *
     * @param series
     *            {@link Map}<{@link Point}, {@link Double}> The points to fit,
     *            along with a weight
     * @return {@link Line} The resulting line of best fit
     */
    public static Line leastSquares(Map<Point, Double> series) {
        if (series.containsKey(null)) {
            throw new IllegalArgumentException();
        }
        if (series.containsValue(null)) {
            throw new IllegalArgumentException();
        }
        double xMean = 0.0;
        double yMean = 0.0;
        double totalFrequency = 0.0;
        for (Entry<Point, Double> pointFrequency : series.entrySet()) {
            Point point = pointFrequency.getKey();
            double frequency = pointFrequency.getValue();
            totalFrequency += frequency;
            double xCoord = point.x;
            double yCoord = point.y;
            xMean += xCoord * frequency;
            yMean += yCoord * frequency;
        }

        xMean /= totalFrequency;
        yMean /= totalFrequency;

        double covariance = 0.0;
        double xVariance = 0.0;
        for (Entry<Point, Double> pointFrequency : series.entrySet()) {
            Point point = pointFrequency.getKey();
            double frequency = pointFrequency.getValue();
            double xDiff = point.x - xMean;
            double yDiff = point.y - yMean;
            covariance += xDiff * yDiff * frequency;
            xVariance += xDiff * xDiff * frequency;
        }

        final double gradient = covariance / xVariance;
        final double yIntercept = yMean - xMean * gradient;
        return new Line(gradient, yIntercept);
    }
}
