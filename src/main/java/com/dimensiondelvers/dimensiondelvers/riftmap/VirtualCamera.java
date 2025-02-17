package com.dimensiondelvers.dimensiondelvers.riftmap;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Virtual Camera class for RiftMap 3D renderer
 * Holds position and rotation of the camera and provides view and projection matrix for rendering
 */
public class VirtualCamera {
    private Vector3f position;
    private float pitch, yaw, roll;
    private float fov, aspectRatio, nearPlane, farPlane;

    public VirtualCamera(float fov, float aspectRatio, float nearPlane, float farPlane) {
        this.position = new Vector3f(0, 0, 0);
        this.pitch = 0;
        this.yaw = 0;
        this.roll = 0;
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setRotation(float pitch, float yaw, float roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    /**
     * Positions the camera on a sphere of radius around the origin
     * and orients it so that it always looks at (0,0,0).
     *
     * @param orbitPitch  the vertical orbit angle in degrees (elevation)
     * @param orbitYaw    the horizontal orbit angle in degrees (azimuth)
     * @param distance    the distance from the origin
     */
    public void orbitAroundOrigin(float orbitPitch, float orbitYaw, float distance) {
        // convert degrees to radians for computing stuffs
        float radOrbitPitch = (float) Math.toRadians(Math.clamp(orbitPitch, -89.99, 89.99)); // float math sometimes results in parameters slightly beyond -90 and 90 which leads to weird behavior
        float radOrbitYaw   = (float) Math.toRadians(orbitYaw);

        // calculate the camera's position on the sphere.
        // technically adding position to these should move all the stuffs properly..
        float x = distance * (float)(Math.cos(radOrbitPitch) * Math.sin(radOrbitYaw));
        float y = distance * (float)Math.sin(radOrbitPitch);
        float z = distance * (float)(Math.cos(radOrbitPitch) * Math.cos(radOrbitYaw));

        // update the position
        setPosition(x, y, z);

        // compute the pitch and yaw angles, note
        float computedPitch = Math.clamp((float) -Math.toDegrees(Math.asin(-y / distance)), -90, 90); //just make sure no bad stuff happens
        float computedYaw   = (float) Math.toDegrees(Math.atan2(-x, z));

        // update the rotation
        setRotation(computedPitch, computedYaw, 0);
    }

    public Matrix4f getViewMatrix() {
        Matrix4f view = new Matrix4f();
        view.identity();
        view.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0))
                .rotate((float) Math.toRadians(roll), new Vector3f(0, 0, 1));
        view.translate(-position.x, -position.y, -position.z);
        return view;
    }

    public Matrix4f getProjectionMatrix() {
        return new Matrix4f().perspective((float) Math.toRadians(fov), aspectRatio, nearPlane, farPlane);
    }
}
