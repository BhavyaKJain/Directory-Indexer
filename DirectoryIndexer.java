import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public final class DirectoryIndexer {
    private static final String INDENT = "    ";
    private static final String INDEX_FILE_NAME_FORMAT = "Directory Index - %d.txt";

    public static void main(String[] args) {
        try {
            if (args.length == 0 || args[0].isBlank()) {
                throw new IllegalArgumentException("The path cannot be empty");
            }
            String path = args[0].trim();
            String indexFilePath = indexDirectory(path);
            System.out.println("Index file path : \"" + indexFilePath + "\"");
        } catch (IOException | IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private static String indexDirectory(String path) throws IOException {
        File directory = getDirectory(path);
        System.out.println("Indexing the directory \"" + directory.getPath() + "\"");
        File indexFile = getIndexFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(indexFile))) {
            writer.write(directory.getPath());
            writer.newLine();
            indexDirectory(directory, writer, INDENT);
        }
        return indexFile.getPath();
    }

    private static File getDirectory(String path) throws IOException {
        File directory = new File(path).getCanonicalFile();
        if (!directory.exists()) {
            throw new IllegalArgumentException(
                    "The path \"" + directory.getPath() + "\" does not exist");
        }
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(
                    "The path \"" + directory.getPath() + "\" is not a directory");
        }
        return directory;
    }

    private static File getIndexFile() throws IOException {
        String name = String.format(INDEX_FILE_NAME_FORMAT, System.currentTimeMillis());
        return new File(name).getCanonicalFile();
    }

    private static void indexDirectory(File directory, BufferedWriter writer, String indent)
            throws IOException {
        for (File file : listFiles(directory)) {
            writer.write(indent + file.getName());
            writer.newLine();
            if (file.isDirectory()) {
                indexDirectory(file, writer, indent + INDENT);
            }
        }
    }

    private static File[] listFiles(File directory) {
        File[] files = directory.listFiles();
        if (files == null) {
            return new File[0];
        }
        sortFiles(files);
        return files;
    }

    private static void sortFiles(File[] files) {
        Arrays.sort(files, (file1, file2) -> {
            boolean isFile1Directory = file1.isDirectory();
            boolean isFile2Directory = file2.isDirectory();
            if (isFile1Directory && !isFile2Directory) {
                return -1;
            }
            if (!isFile1Directory && isFile2Directory) {
                return 1;
            }
            return file1.compareTo(file2);
        });
    }
}
