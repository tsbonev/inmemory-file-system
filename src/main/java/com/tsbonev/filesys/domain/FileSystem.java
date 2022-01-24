package com.tsbonev.filesys.domain;

import java.util.Optional;

public interface FileSystem {
    File createFile(Directory directory, String fileName, String content);

    Directory createDirectory(String directoryName);
    Directory createDirectory(Directory parent, String directoryName);

    Optional<File> findFile(String path);

    boolean deleteFile(File file);
    boolean deleteDirectory(Directory directory);
}
