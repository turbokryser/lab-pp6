package org.example.api.Misc;
import java.io.*;
import java.util.jar.*;
import java.util.zip.*;

public class Archiver {

    public void createJarArchive(String archiveFileName, String[] files) throws IOException {
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(archiveFileName))) {
            for (String file : files) {
                addToArchive(jos, file);
            }
        }
    }

    public void createZipArchive(String archiveFileName, String[] files) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(archiveFileName))) {
            for (String file : files) {
                addToArchive(zos, file);
            }
        }
    }

    private void addToArchive(ZipOutputStream zos, String file) throws IOException {
        File inputFile = new File(file);
        try (FileInputStream fis = new FileInputStream(inputFile)) {
            ZipEntry entry = new ZipEntry(inputFile.getName());
            zos.putNextEntry(entry);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
        }
    }
}