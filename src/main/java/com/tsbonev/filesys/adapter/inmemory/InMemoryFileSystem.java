package com.tsbonev.filesys.adapter.inmemory;

import com.tsbonev.filesys.adapter.inmemory.exceptions.CannotDeleteRootDirectoryException;
import com.tsbonev.filesys.adapter.inmemory.exceptions.DirectoryAlreadyExistsException;
import com.tsbonev.filesys.adapter.inmemory.exceptions.FileAlreadyExistsException;
import com.tsbonev.filesys.adapter.inmemory.exceptions.InvalidNameException;
import com.tsbonev.filesys.adapter.inmemory.exceptions.InvalidPathException;
import com.tsbonev.filesys.domain.Directory;
import com.tsbonev.filesys.domain.File;
import com.tsbonev.filesys.domain.FileSystem;
import com.tsbonev.filesys.domain.Node;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class InMemoryFileSystem implements FileSystem {
    private final Directory rootDirectory = new Directory(null, "");

    @Override
    public File createFile(Directory directory, String fileName, String content) {
        if(directory == null) throw new InvalidPathException();
        if(fileName == null || fileName.isBlank()) throw new InvalidNameException();

        if(directory.getChildren().stream().anyMatch(it -> it.getName().equals(fileName) && it instanceof File)) {
            throw new FileAlreadyExistsException();
        }

        return new File(directory, fileName, content);
    }

    @Override
    public Directory createDirectory(String directoryName) {
        return createDirectory(rootDirectory, directoryName);
    }

    @Override
    public Directory createDirectory(Directory parent, String directoryName) {
        if(parent == null) throw new InvalidPathException();

        if(directoryName == null || directoryName.isBlank()) throw new InvalidNameException();

        if(parent.getChildren().stream().anyMatch(it -> it.getName().equals(directoryName) && it instanceof Directory)) {
            throw new DirectoryAlreadyExistsException();
        }

        return new Directory(parent, directoryName);
    }

    @Override
    public Optional<File> findFile(String path) {
        if(path == null || path.charAt(0) != '/' || path.charAt(path.length() - 1) == '/') throw new InvalidPathException();

        Queue<String> pathQueue = new LinkedList<>();

        String[] pathNames = path.split("/");

        for(int i = 1; i < pathNames.length; i++){
            pathQueue.add(pathNames[i]);
        }

        Node currentNode = rootDirectory;

        while (!pathQueue.isEmpty()) {
            String seeking = pathQueue.poll();

            if(seeking.isBlank()) throw new InvalidPathException();

            Optional<Node> possibleNode = currentNode.getChildren().stream().filter(it ->
                    it.getName().equals(seeking)
                    && (!pathQueue.isEmpty() || it instanceof File)
            ).findFirst();

            if(possibleNode.isEmpty()) return Optional.empty();
            currentNode = possibleNode.get();

            if(pathQueue.isEmpty() && currentNode.getName().equals(seeking) && currentNode instanceof File) {
                return Optional.of((File) currentNode);
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean deleteFile(File file) {
        if(file == null) return false;

        return file.getParent().removeChild(file);
    }

    @Override
    public boolean deleteDirectory(Directory directory) {
        if(directory == null) return false;

        if(directory.getParent() == null) throw new CannotDeleteRootDirectoryException();

        return directory.getParent().removeChild(directory);
    }
}
