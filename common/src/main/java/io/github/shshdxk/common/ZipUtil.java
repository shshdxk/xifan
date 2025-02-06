package io.github.shshdxk.common;


import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 */
public class ZipUtil {

    private final static byte[] UTF_8_BOM = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    /**
     * 压缩文件夹
     * @param fileToZip 需要压缩的文件或文件夹
     * @param fileName 第一层目录名
     * @param destZip 目标文件
     * @throws IOException
     */
    public static void zipFile(File fileToZip, String fileName, File destZip) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(destZip);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            zipFile(fileToZip, fileName, zipOut);
        }
    }

    /**
     * 压缩文件夹
     * @param fileToZip 需要压缩的文件或文件夹
     * @param fileName 层级目录名
     * @param zipOut 输出zip流
     * @throws IOException
     */
    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isDirectory()) {
            File[] children = fileToZip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    if (StringUtils.isBlank(fileName)) {
                        zipFile(childFile, childFile.getName(), zipOut);
                    } else {
                        zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
                    }
                }
            }
            return;
        }

        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            String name = StringUtils.isBlank(fileName) ? fileToZip.getName() : fileName;
            ZipEntry zipEntry = new ZipEntry(name);
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }
    }

    /**
     * 将文件流写入压缩包
     * @param inputStream
     * @param fileName 压缩包内文件目录和名称
     * @param zipOut
     * @throws IOException
     */
    public static void writeFile(InputStream inputStream, String fileName, ZipOutputStream zipOut) throws IOException {
        writeFile(inputStream, fileName, zipOut, false);
    }

    /**
     * 将文件流写入压缩包
     * @param inputStream
     * @param fileName 压缩包内文件目录和名称
     * @param zipOut
     * @param withUTF8BOM inputStream是否额外写入utf8BOM头{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}
     * @throws IOException
     */
    public static void writeFile(InputStream inputStream, String fileName, ZipOutputStream zipOut, boolean withUTF8BOM) throws IOException {
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        if (withUTF8BOM) {
            zipOut.write(UTF_8_BOM);
        }
        byte[] bytes = new byte[1024];
        int length;
        while ((length = inputStream.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
    }

    /**
     * 解压文件
     * @param zipFilePath
     * @param destDir
     * @throws IOException
     */
    public static void unzip(String zipFilePath, String destDir) throws IOException {
        File destDirFile = new File(destDir);
        if (!destDirFile.exists()) {
            destDirFile.mkdirs();
        }

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                File outputFile = new File(destDir, entry.getName());

                // Create parent directories if they don't exist
                if (entry.isDirectory()) {
                    outputFile.mkdirs();
                } else {
                    // Create file and write contents
                    File parentDir = outputFile.getParentFile();
                    if (!parentDir.exists()) {
                        parentDir.mkdirs();
                    }

                    try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zipInputStream.read(buffer)) > 0) {
                            bufferedOutputStream.write(buffer, 0, length);
                        }
                    }
                }
                zipInputStream.closeEntry();
            }
        }
    }

}
