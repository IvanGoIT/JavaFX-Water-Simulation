package simulation.water;

import javafx.scene.paint.Color;

import java.util.concurrent.Semaphore;

public class WaterSimulator {
    private static final int FPS = 25;
    private static final float CLICK_WATER_VALUE = 5000;
    private static final float MAX_STEP = 1000f;
    private static final float C = 1f;
    private static final float DECREASER = 0.95f;

    private static final int CORES = 1; // Runtime.getRuntime().availableProcessors();

    private Semaphore semaphore = new Semaphore(CORES);
    private final Water water;
    private boolean execute = false;

    public WaterSimulator(Water water) {
        this.water = water;
    }

    public void launch() {
        execute = true;
        for(int i = 0; i < CORES; i++) {
            final int heightFrom = water.getHeightMapHeight() / CORES * i;
            final int heightTo = i == CORES - 1 ?
                    water.getHeightMapHeight() :
                    heightFrom + water.getHeightMapHeight() / CORES;

            System.out.printf("From %d to %d\n", heightFrom, heightTo);
            new Thread(() -> {
                long calcTime, sleepTime;
                float dt = 1f;
                while (execute) {
                    try {
                        calcTime = Math.max(calc(heightFrom, heightTo, dt), 1);
                        sleepTime = 1000 / (calcTime * FPS);
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        dt = (calcTime + sleepTime) / 100f;
                    } catch (Exception e) {
                        System.err.printf("From %d to %d\n", heightFrom, heightTo);
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public void stop() {
        execute = false;
    }

    public void click(double x, double y) {
        try {
            semaphore.acquire(CORES);
            int pX = (int) (x * water.getHeightMapWidth());
            int pY = (int) (y * water.getHeightMapHeight());
            water.setHeightMapHard(pX, pY, water.getHeightMap(pX, pY) + CLICK_WATER_VALUE);
            semaphore.release(CORES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private long calc(float dt) {
        return calc(0, water.getHeightMapHeight(), dt);
    }

    private long calc(int heightFrom, int heightTo, float dt) {
        long startTime = System.currentTimeMillis();
        try {
            semaphore.acquire();
            for (int j = 1; j < water.getHeightMapHeight() - 1; j++) {
                for (int i = 1; i < water.getHeightMapWidth() - 1; i++) {
                    float current = water.getHeightMap(i, j);
                    float right = water.getHeightMap(i + 1, j);
                    float left = water.getHeightMap(i - 1, j);
                    float top = water.getHeightMap(i, j - 1);
                    float bottom = water.getHeightMap(i, j + 1);
                    float f = ((C * C) * ((right + left + bottom + top)) - (4.0f * current)) / 4.0f;
                    f = Math.min(f, MAX_STEP);
                    f = Math.max(f, -MAX_STEP);

                    water.velocityMap[j][i] = water.velocityMap[j][i] + f * dt;
                    water.setHeightMap(i, j, current + water.velocityMap[j][i] * dt);
                    water.velocityMap[j][i] *= DECREASER;
                }
            }
            water.applyHeightMapChanges(heightFrom, heightTo);
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis() - startTime;
    }
}
