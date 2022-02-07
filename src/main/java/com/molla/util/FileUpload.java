package com.molla.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
public class FileUpload {

    // save file process
    public static void saveFile(String uploadDir, String fileName,
                                MultipartFile multipartFile) throws IOException {

        log.debug("FileUploadUtil | saveFile is started");

        Path uploadPath = Paths.get(uploadDir);

        log.debug("FileUploadUtil | saveFile | uploadPath : " + uploadPath);

        log.debug("FileUploadUtil | saveFile | Files.exists(uploadPath) : " + Files.exists(uploadPath));

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.debug("FileUploadUtil | saveFile | Files.createDirectories is called");
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {

            Path filePath = uploadPath.resolve(fileName);

            log.debug("FileUploadUtil | saveFile | filePath : " + filePath);

            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            log.debug("FileUploadUtil | saveFile | Files.copy is called");

        } catch (IOException ex) {

            log.error("FileUploadUtil | saveFile | ex.getMessage() : " + ex.getMessage());

            throw new IOException("Could not save file: " + fileName, ex);
        }
    }

    // delete existing directory
    public static void cleanDir(String dir) {

        log.debug("FileUploadUtil | cleanDir is started");

        log.debug("FileUploadUtil | cleanDir | dir : " + dir);

        Path dirPath = Paths.get(dir);

        log.debug("FileUploadUtil | cleanDir | dirPath : " + dirPath);

        try {
            Files.list(dirPath).forEach(file -> {

                log.debug("FileUploadUtil | cleanDir | file : " + file.toString());

                log.debug("FileUploadUtil | cleanDir | Files.isDirectory(file) : " + Files.isDirectory(file));

                if (!Files.isDirectory(file)) {
                    try {
                        Files.delete(file);

                        log.debug("FileUploadUtil | cleanDir | delete is completed");

                    } catch (IOException ex) {

                        log.error("FileUploadUtil | cleanDir | ex.getMessage() : " + ex.getMessage());
                        log.error("Could not delete file: " + file);

                    }
                }
            });
        } catch (IOException ex) {

            log.error("FileUploadUtil | cleanDir | ex.getMessage() : " + ex.getMessage());
            log.error("Could not list directory: " + dirPath);

        }
    }

    public static void removeDir(String dir) {

        log.debug("FileUploadUtil | removeDir is started");

        log.debug("FileUploadUtil | removeDir | dir : " + dir);

        cleanDir(dir);

        log.debug("FileUploadUtil | cleanDir(dir) is over");

        try {
            Files.delete(Paths.get(dir));
        } catch (IOException e) {
            log.error("Could not remove directory: " + dir);
        }

    }

}
