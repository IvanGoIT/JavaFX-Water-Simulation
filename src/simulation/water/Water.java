package simulation.water;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Water {
    private static final float DEFAULT_WATER_HEIGHT_VALUE = 0;
    private static final float MAX_VALUE = 80f;

    private final float[][] heightMap;
    private final float[][] heightMapTemp;
    public final float[][] velocityMap;
    private final Rectangle[][] water;

    public Water(int width, int height, int size) {
        if (width <= 0 || height <= 0 || size <= 0)
            throw new IllegalArgumentException();

        heightMap = new float[height][width];
        heightMapTemp = new float[height][width];
        velocityMap = new float[height][width];
        water = new Rectangle[height][width];
        for(int j = 0; j < heightMap.length ; j++) {
            for(int i = 0; i < heightMap[j].length; i++) {
                water[j][i] = new Rectangle(size, size);
                water[j][i].setTranslateX(i * size);
                water[j][i].setTranslateY(j * size);
                setHeightMap(i, j, DEFAULT_WATER_HEIGHT_VALUE);
            }
        }
    }

    public float getHeightMap(int x, int y) {
        return heightMap[y][x];
    }

    public int getHeightMapHeight() {
        return heightMap.length;
    }

    public int getHeightMapWidth() {
        return heightMap[0].length;
    }

    public void setHeightMap(int x, int y, float value) {
        heightMapTemp[y][x] = value;
    }

    public void setHeightMapHard(int x, int y, float value) {
        // setHeightMap(x, y, value);
        heightMap[y][x] = value;
    }

    public void applyHeightMapChanges() {
        applyHeightMapChanges(0, getHeightMapHeight());
    }

    public void applyHeightMapChanges(int heightFrom, int heightTo) {
        float saturation;
        for(int j = heightFrom; j < heightTo; j++) {
            for (int i = 0; i < getHeightMapWidth(); i++) {
                heightMap[j][i] = heightMapTemp[j][i];

                saturation = heightMapTemp[j][i];

                saturation = Math.min(saturation, MAX_VALUE);
                saturation = Math.max(saturation, -MAX_VALUE);

                saturation = (saturation + MAX_VALUE) / (MAX_VALUE * 2);

                // saturation = Math.min(saturation + 1f, 1f);

                water[j][i].setFill(Color.hsb(210, saturation, 1));
            }
        }
    }

    public List<Rectangle> getRectangles() {
        ArrayList<Rectangle> rectangles = new ArrayList<>(water.length * water[0].length);
        for(Rectangle[] line : water)
            rectangles.addAll(Arrays.asList(line));
        return rectangles;
    }
}
