package com.github.jordyh297.jrender;

/**
 * Represents a 4x4 matrix for 3D transformations.
 * Used for translation, rotation, scaling, and projection.
 */
public class Matrix4x4 {
    public double[][] m;
    
    public Matrix4x4() {
        m = new double[4][4];
    }
    
    // Create identity matrix
    public static Matrix4x4 identity() {
        Matrix4x4 matrix = new Matrix4x4();
        matrix.m[0][0] = 1;
        matrix.m[1][1] = 1;
        matrix.m[2][2] = 1;
        matrix.m[3][3] = 1;
        return matrix;
    }
    
    // Create translation matrix
    public static Matrix4x4 translation(double x, double y, double z) {
        Matrix4x4 matrix = identity();
        matrix.m[0][3] = x;
        matrix.m[1][3] = y;
        matrix.m[2][3] = z;
        return matrix;
    }
    
    // Create rotation matrix around X axis
    public static Matrix4x4 rotationX(double angle) {
        Matrix4x4 matrix = identity();
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        matrix.m[1][1] = cos;
        matrix.m[1][2] = -sin;
        matrix.m[2][1] = sin;
        matrix.m[2][2] = cos;
        return matrix;
    }
    
    // Create rotation matrix around Y axis
    public static Matrix4x4 rotationY(double angle) {
        Matrix4x4 matrix = identity();
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        matrix.m[0][0] = cos;
        matrix.m[0][2] = sin;
        matrix.m[2][0] = -sin;
        matrix.m[2][2] = cos;
        return matrix;
    }
    
    // Create rotation matrix around Z axis
    public static Matrix4x4 rotationZ(double angle) {
        Matrix4x4 matrix = identity();
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        matrix.m[0][0] = cos;
        matrix.m[0][1] = -sin;
        matrix.m[1][0] = sin;
        matrix.m[1][1] = cos;
        return matrix;
    }
    
    // Create scaling matrix
    public static Matrix4x4 scaling(double x, double y, double z) {
        Matrix4x4 matrix = identity();
        matrix.m[0][0] = x;
        matrix.m[1][1] = y;
        matrix.m[2][2] = z;
        return matrix;
    }
    
    // Create perspective projection matrix
    public static Matrix4x4 perspective(double fov, double aspectRatio, double near, double far) {
        Matrix4x4 matrix = new Matrix4x4();
        double tanHalfFov = Math.tan(fov / 2.0);
        
        matrix.m[0][0] = 1.0 / (aspectRatio * tanHalfFov);
        matrix.m[1][1] = 1.0 / tanHalfFov;
        matrix.m[2][2] = -(far + near) / (far - near);
        matrix.m[2][3] = -(2.0 * far * near) / (far - near);
        matrix.m[3][2] = -1.0;
        matrix.m[3][3] = 0.0;
        
        return matrix;
    }
    
    // Create look-at matrix (view matrix)
    public static Matrix4x4 lookAt(Vector3D eye, Vector3D target, Vector3D up) {
        Vector3D zAxis = eye.subtract(target).normalize();
        Vector3D xAxis = up.cross(zAxis).normalize();
        Vector3D yAxis = zAxis.cross(xAxis);
        
        Matrix4x4 matrix = identity();
        matrix.m[0][0] = xAxis.x;
        matrix.m[0][1] = xAxis.y;
        matrix.m[0][2] = xAxis.z;
        matrix.m[0][3] = -xAxis.dot(eye);
        
        matrix.m[1][0] = yAxis.x;
        matrix.m[1][1] = yAxis.y;
        matrix.m[1][2] = yAxis.z;
        matrix.m[1][3] = -yAxis.dot(eye);
        
        matrix.m[2][0] = zAxis.x;
        matrix.m[2][1] = zAxis.y;
        matrix.m[2][2] = zAxis.z;
        matrix.m[2][3] = -zAxis.dot(eye);
        
        return matrix;
    }
    
    // Matrix multiplication
    public Matrix4x4 multiply(Matrix4x4 other) {
        Matrix4x4 result = new Matrix4x4();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.m[i][j] = 0;
                for (int k = 0; k < 4; k++) {
                    result.m[i][j] += m[i][k] * other.m[k][j];
                }
            }
        }
        return result;
    }
    
    // Transform a 3D vector
    public Vector3D transform(Vector3D v) {
        double w = m[3][0] * v.x + m[3][1] * v.y + m[3][2] * v.z + m[3][3];
        if (Math.abs(w) < 0.0001) w = 1.0;
        
        return new Vector3D(
            (m[0][0] * v.x + m[0][1] * v.y + m[0][2] * v.z + m[0][3]) / w,
            (m[1][0] * v.x + m[1][1] * v.y + m[1][2] * v.z + m[1][3]) / w,
            (m[2][0] * v.x + m[2][1] * v.y + m[2][2] * v.z + m[2][3]) / w
        );
    }
}
