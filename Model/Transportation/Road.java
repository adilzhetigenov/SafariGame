package Model.Transportation;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Road {
    private Point2D.Float startPosition;
    private Point2D.Float endPosition;
    private float length;
    private boolean isNavigable;
    private List<Road> connectedRoads;

    public Road(Point2D.Float startPosition, Point2D.Float endPosition, boolean isNavigable) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.length = calculateDistance();
        this.isNavigable = isNavigable;
        this.connectedRoads = new ArrayList<>();
    }

    public Road(Point2D.Float startPosition, Point2D.Float endPosition) {
        this(startPosition, endPosition, true);
    }

    public void connectTo(Road road) {
        if (road != null && !connectedRoads.contains(road)) {
            connectedRoads.add(road);
            road.connectedRoads.add(this); // Mutual connection
        }
    }

    public List<Road> getConnectedRoads() {
        return connectedRoads;
    }

    public boolean isValidPath() {
        return isNavigable && !connectedRoads.isEmpty();
    }

    public float calculateDistance() {
        return (float) startPosition.distance(endPosition);
    }

    public void allowNavigation() {
        isNavigable = true;
    }

    public void blockNavigation() {
        isNavigable = false;
    }

    // Getters and setters
    public Point2D.Float getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Point2D.Float startPosition) {
        this.startPosition = startPosition;
    }

    public Point2D.Float getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(Point2D.Float endPosition) {
        this.endPosition = endPosition;
    }

    public float getLength() {
        return length;
    }

    public boolean isNavigable() {
        return isNavigable;
    }
} 