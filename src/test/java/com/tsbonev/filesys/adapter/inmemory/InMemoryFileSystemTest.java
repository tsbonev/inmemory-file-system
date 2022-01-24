package com.tsbonev.filesys.adapter.inmemory;

import static org.junit.jupiter.api.Assertions.fail;

import com.tsbonev.filesys.adapter.inmemory.exceptions.CannotDeleteRootDirectoryException;
import com.tsbonev.filesys.adapter.inmemory.exceptions.DirectoryAlreadyExistsException;
import com.tsbonev.filesys.adapter.inmemory.exceptions.FileAlreadyExistsException;
import com.tsbonev.filesys.adapter.inmemory.exceptions.InvalidNameException;
import com.tsbonev.filesys.adapter.inmemory.exceptions.InvalidPathException;
import com.tsbonev.filesys.domain.Directory;
import com.tsbonev.filesys.domain.File;
import com.tsbonev.filesys.domain.FileSystem;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryFileSystemTest {

    private FileSystem fileSystem;

    @BeforeEach
    public void setUp() {
        fileSystem = new InMemoryFileSystem();
    }

    @Test
    public void happyPath() {
        Directory home = fileSystem.createDirectory("home");
        File fileOne = fileSystem.createFile(home, "fileOne", "::content::");

        Optional<File> foundFile = fileSystem.findFile("/home/fileOne");

        boolean deletedFile = fileSystem.deleteFile(fileOne);

        Optional<File> notFoundFile = fileSystem.findFile("/home/fileOne");

        Assertions.assertTrue(foundFile.isPresent());
        Assertions.assertEquals(foundFile.get(), fileOne);
        Assertions.assertTrue(deletedFile);
        Assertions.assertTrue(notFoundFile.isEmpty());
    }

    @Test
    public void createFile_inDirectoryWithSameName() {
        Directory home = fileSystem.createDirectory("home");
        Directory homeSecond = fileSystem.createDirectory(home, "home");
        File fileOne = fileSystem.createFile(home, "home", "::content::");

        Optional<File> foundFile = fileSystem.findFile("/home/home");

        boolean deletedFile = fileSystem.deleteFile(fileOne);

        Optional<File> notFoundFile = fileSystem.findFile("/home/home");

        Assertions.assertTrue(foundFile.isPresent());
        Assertions.assertEquals(foundFile.get(), fileOne);
        Assertions.assertTrue(deletedFile);
        Assertions.assertTrue(notFoundFile.isEmpty());
    }

    @Test
    public void createFile_alreadyExists() {
        Directory home = fileSystem.createDirectory("home");
        fileSystem.createFile(home, "fileOne", "::content::");

        try {
            fileSystem.createFile(home, "fileOne", "::content::");
            fail();
        } catch (FileAlreadyExistsException ex) {}
    }

    @Test
    public void createFile_emptyName() {
        Directory home = fileSystem.createDirectory("home");
        try {
            fileSystem.createFile(home, "", "::content::");
            fail();
        } catch (InvalidNameException ex) {}
    }

    @Test
    public void createFile_nullName() {
        Directory home = fileSystem.createDirectory("home");
        try {
            fileSystem.createFile(home, null, "::content::");
            fail();
        } catch (InvalidNameException ex) {}
    }

    @Test
    public void createDirectory_nullDirectory() {
        try {
            fileSystem.createFile(null, "fileOne", "::content::");
            fail();
        } catch (InvalidPathException ex) {}
    }

    @Test
    public void createDirectory_directoryAlreadyExistsAtRoot() {
        Directory home = fileSystem.createDirectory("home");

        try {
            fileSystem.createDirectory("home");
            fail();
        } catch (DirectoryAlreadyExistsException ex) {}
    }

    @Test
    public void createDirectory_directoryAlreadyExists() {
        Directory home = fileSystem.createDirectory("home");
        Directory tsvetozar = fileSystem.createDirectory(home, "tsvetozar");

        try {
            fileSystem.createDirectory(home, "tsvetozar");
            fail();
        } catch (DirectoryAlreadyExistsException ex) {}
    }

    @Test
    public void createDirectory_nullParent() {
        try {
            fileSystem.createDirectory(null, "home");
            fail();
        } catch (InvalidPathException ex) {}
    }

    @Test
    public void createDirectory_emptyName() {
        try {
            fileSystem.createDirectory("");
            fail();
        } catch (InvalidNameException ex) {}
    }

    @Test
    public void createDirectory_nullName() {
        try {
            fileSystem.createDirectory(null);
            fail();
        } catch (InvalidNameException ex) {}
    }

    @Test
    public void findFile_longPath() {
        Directory home = fileSystem.createDirectory("home");
        Directory tsvetozar = fileSystem.createDirectory(home, "tsvetozar");
        Directory homework = fileSystem.createDirectory(tsvetozar, "homework");

        File fileOne = fileSystem.createFile(homework, "fileOne", "::content::");
        File fileTwo = fileSystem.createFile(homework, "fileTwo", "::content::");

        Optional<File> foundFile = fileSystem.findFile("/home/tsvetozar/homework/fileOne");
        Optional<File> foundFileTwo = fileSystem.findFile("/home/tsvetozar/homework/fileTwo");

        Assertions.assertTrue(foundFile.isPresent());
        Assertions.assertEquals(foundFile.get(), fileOne);
        Assertions.assertTrue(foundFileTwo.isPresent());
        Assertions.assertEquals(foundFileTwo.get(), fileTwo);
    }

    @Test
    public void findFile_emptyPath() {
        try {
            fileSystem.findFile("/");
            fail();
        } catch (InvalidPathException ex) {}
    }

    @Test
    public void findFile_nullPath() {
        Directory home = fileSystem.createDirectory("home");
        Directory tsvetozar = fileSystem.createDirectory(home, "tsvetozar");
        Directory homework = fileSystem.createDirectory(tsvetozar, "homework");

        File fileOne = fileSystem.createFile(homework, "fileOne", "::content::");
        File fileTwo = fileSystem.createFile(homework, "fileTwo", "::content::");

        try {
            fileSystem.findFile(null);
            fail();
        } catch (InvalidPathException ex) {}
    }

    @Test
    public void deleteFile() {
        Directory home = fileSystem.createDirectory("home");
        Directory tsvetozar = fileSystem.createDirectory(home, "tsvetozar");
        Directory homework = fileSystem.createDirectory(tsvetozar, "homework");

        File fileOne = fileSystem.createFile(homework, "fileOne", "::content::");
        File fileTwo = fileSystem.createFile(homework, "fileTwo", "::content::");

        Optional<File> foundFile = fileSystem.findFile("/home/tsvetozar/homework/fileOne");

        boolean deletedFile = fileSystem.deleteFile(fileOne);

        Optional<File> notFoundFile = fileSystem.findFile("/home/tsvetozar/homework/fileOne");
        Optional<File> foundFileTwo = fileSystem.findFile("/home/tsvetozar/homework/fileTwo");

        Assertions.assertTrue(foundFile.isPresent());
        Assertions.assertEquals(foundFile.get(), fileOne);
        Assertions.assertTrue(deletedFile);
        Assertions.assertTrue(notFoundFile.isEmpty());
        Assertions.assertTrue(foundFileTwo.isPresent());
        Assertions.assertEquals(foundFileTwo.get(), fileTwo);
    }

    @Test
    public void deleteFile_deleteTwice() {
        Directory home = fileSystem.createDirectory("home");

        File fileOne = fileSystem.createFile(home, "fileOne", "::content::");

        boolean deletedFile = fileSystem.deleteFile(fileOne);
        boolean deletedFileSecond = fileSystem.deleteFile(fileOne);

        Optional<File> notFoundFile = fileSystem.findFile("/home/tsvetozar/homework/fileOne");

        Assertions.assertTrue(deletedFile);
        Assertions.assertFalse(deletedFileSecond);
        Assertions.assertTrue(notFoundFile.isEmpty());
    }

    @Test
    public void deleteFile_deleteNull() {
        boolean deletedFile = fileSystem.deleteFile(null);

        Assertions.assertFalse(deletedFile);
    }

    @Test
    public void findFile_invalidPathStart() {
        Directory home = fileSystem.createDirectory("home");
        fileSystem.createFile(home, "fileOne", "::content::");

        try {
            fileSystem.findFile("home/fileOne");
            fail();
        } catch (InvalidPathException exception) {}
    }

    @Test
    public void findFile_blankName() {
        Directory home = fileSystem.createDirectory("home");
        fileSystem.createFile(home, "fileOne", "::content::");

        try {
            fileSystem.findFile("/home/fileOne/ ");
            fail();
        } catch (InvalidPathException exception) {}
    }

    @Test
    public void findFile_emptyName() {
        Directory home = fileSystem.createDirectory("home");
        fileSystem.createFile(home, "fileOne", "::content::");

        try {
            fileSystem.findFile("/home/fileOne/");
            fail();
        } catch (InvalidPathException exception) {}
    }

    @Test
    public void deleteDirectory() {
        Directory home = fileSystem.createDirectory("home");
        fileSystem.createFile(home, "fileOne", "::content::");

        boolean deletedDirectory = fileSystem.deleteDirectory(home);

        Optional<File> foundFile = fileSystem.findFile("/home/fileOne");

        Assertions.assertTrue(foundFile.isEmpty());
        Assertions.assertTrue(deletedDirectory);
    }

    @Test
    public void deleteDirectory_deleteNull() {
        boolean deletedDirectory = fileSystem.deleteDirectory(null);
        Assertions.assertFalse(deletedDirectory);
    }

    @Test
    public void deleteDirectory_deleteTwice() {
        Directory home = fileSystem.createDirectory("home");
        fileSystem.createFile(home, "fileOne", "::content::");

        boolean deletedDirectory = fileSystem.deleteDirectory(home);
        boolean deletedDirectorySecond = fileSystem.deleteDirectory(home);

        Optional<File> foundFile = fileSystem.findFile("/home/fileOne");

        Assertions.assertTrue(foundFile.isEmpty());
        Assertions.assertTrue(deletedDirectory);
        Assertions.assertFalse(deletedDirectorySecond);
    }

    @Test
    public void deleteDirectory_cannotDeleteRoot() {
        Directory home = fileSystem.createDirectory("home");

        try {
            fileSystem.deleteDirectory((Directory) home.getParent());
            fail();
        } catch (CannotDeleteRootDirectoryException ex) {}
    }
}
