package me.nelly.hackerdetector;

import me.nelly.hackerdetector.checks.*;

import java.util.ArrayList;
import java.util.Arrays;

public class DetectionManager {

    private ArrayList<Detection> detections = new ArrayList<>();

    public DetectionManager() {
        addDetections(

                // Combat
                new ReachA(),
                new KillAuraA(),
                new VelocityA(),

                // Movement
                new FlightA(),
                new FlightB(),

                // Player

                // Misc
                new MessageA()

                // Exploit

        );
    }

    public void addDetections(Detection... detections) {
        this.detections.addAll(Arrays.asList(detections));
    }

    public ArrayList<Detection> getDetections() {
        return detections;
    }
}
