package uiuc.dm.moveMine.domain;

import java.util.ArrayList;

public class IndexBox {

    private int[][] box;
    private double distThres;
    private long totalNumRelatedPoints;
    private BoxRange boxRange;
    private long boxHeight;
    private long boxWidth;

    public IndexBox(BoxRange boxRange, double distThres, ArrayList<Point> points) {
        this.distThres = distThres;
        this.boxRange = boxRange;

//        double maxX = points.get(0).getX();
//        double minX = points.get(0).getX();
//        double maxY = points.get(0).getY();
//        double minY = points.get(0).getY();
//
//        for (Point p : points) {
//            if (p.isValid()) {
//                if (p.getX() > maxX) {
//                    maxX = p.getX();
//                }
//                if (p.getX() < minX) {
//                    minX = p.getX();
//                }
//                if (p.getY() > maxY) {
//                    maxY = p.getY();
//                }
//                if (p.getY() < minY) {
//                    minY = p.getY();
//                }
//            }
//        }
//
//        System.out.println("maxX: " + maxX + " minX: " + minX);
//        System.out.println("maxY: " + maxY + " minY: " + minY);
//        System.out.println();
//        System.out.println("maxX: " + boxRange.getMaxX() + " minX: " + boxRange.getMinX());
//        System.out.println("maxY: " + boxRange.getMaxY() + " minY: " + boxRange.getMinY());

        int height = (int) Math.ceil((boxRange.getMaxY() - boxRange.getMinY()) / distThres);
        int width = (int) Math.ceil((boxRange.getMaxX() - boxRange.getMinX()) / distThres);
        this.boxHeight = height;
        this.boxWidth = width;
        System.out.println("box size: " + height + " x " + width);
        box = new int[height][width];
        for (int i = 0; i < height; i++) {
            box[i] = new int[width];
        }

        for (Point p : points) {
            if (p.isValid()) {
                int wIndex = getWidthIndex(p.getX());
                int hIndex = getHeightIndex(p.getY());
                if (wIndex == -1 || hIndex == -1) {
                    System.out.println("ERROR!!!");
                    System.exit(0);
                } else {
                    box[hIndex][wIndex]++;
                    totalNumRelatedPoints++;
                }
            }
        }
    }

    // assume: minX <= x <= maxX
    private int getWidthIndex(double x) {
        int index = (int) Math.floor((x - boxRange.getMinX()) / distThres);
        if (index > boxWidth - 1) {
            return -1;
        } else {
            return index;
        }
    }

    // assume: minY <= y <= maxY
    private int getHeightIndex(double y) {
        int index = (int) Math.floor((y - boxRange.getMinY()) / distThres);
        if (index > boxHeight - 1) {
            return -1;
        } else {
            return index;
        }
    }

    public long getNumPointsWithinThreshold(Point p) {
        if (!p.isValid() || p.getX() > boxRange.getMaxX()
                || p.getX() < boxRange.getMinX()
                || p.getY() > boxRange.getMaxY()
                || p.getY() < boxRange.getMinY()) {
            return 0;
        }
        int newX = getWidthIndex(p.getX());
        int newY = getHeightIndex(p.getY());
        if (newX == -1 || newY == -1) {
            return 0;
        } else {
            return box[newY][newX];
        }
    }

    public long getTotalNumRelatedPoints() {
        return totalNumRelatedPoints;
    }
}
