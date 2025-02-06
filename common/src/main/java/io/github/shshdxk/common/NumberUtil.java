package io.github.shshdxk.common;

public class NumberUtil {
    /**
     * 循环左移
     * @param value
     * @param positions
     * @return
     */
    public static byte rotateLeft(byte value, int positions) {
        // 保证 positions 在 0-7 范围内
        positions = positions & 7;
        return (byte) ((value << positions) | ((0xFF & value) >>> (8 - positions)));
    }

    /**
     * 循环右移
     * @param value
     * @param positions
     * @return
     */
    public static byte rotateRight(byte value, int positions) {
        // 保证 positions 在 0-7 范围内
        positions = positions & 7;
        return (byte) (((0xFF & value) >>> positions) | (value << (8 - positions)));
    }

    /**
     * 循环左移
     * @param value
     * @param positions
     * @return
     */
    public static int rotateLeft(int value, int positions) {
        // 保证 positions 在 0-31 范围内
        positions = positions & 31;
        return (value << positions) | (value >>> (32 - positions));
    }

    /**
     * 循环右移
     * @param value
     * @param positions
     * @return
     */
    public static int rotateRight(int value, int positions) {
        // 保证 positions 在 0-31 范围内
        positions = positions & 31;
        return (byte) ((value >>> positions) | (value << (32 - positions)));
    }


    public static void main(String[] args) {
        byte value = (byte) 0b11001101; // 0x0D (13 in decimal)

        byte rotatedLeft = rotateLeft(value, 3);  // 左移3位
        byte rotatedRight = rotateRight(value, 3);  // 右移3位

        System.out.println("Original value: " + Integer.toBinaryString(value & 0xFF));
        System.out.println("Rotated Left by 3: " + Integer.toBinaryString(rotatedLeft & 0xFF));
        System.out.println("Rotated Right by 3: " + Integer.toBinaryString(rotatedRight & 0xFF));
    }
}
