package task2.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

public class FileService {
    private final Path storageDir;
    private final Path filesDir;
    private final Path metaDir;
    private final ObjectMapper mapper;
    private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();
    private final Duration retention;

    public FileService(Path storageDir, Duration retention) {
        this.storageDir = storageDir;
        this.filesDir = storageDir.resolve("files");
        this.metaDir = storageDir.resolve("meta");
        this.retention = retention;

        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        try {
            Files.createDirectories(filesDir);
            Files.createDirectories(metaDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        cleaner.scheduleWithFixedDelay(this::cleanupOld, 1, 24, TimeUnit.HOURS);
    }

    public FileMeta store(InputStream is, String originalName, long size, String contentType) throws IOException {
        String token = generateToken(originalName);
        String ext = extractExt(originalName);
        Path filePath = filesDir.resolve(token + (ext.isEmpty() ? "" : ("." + ext)));

        try (OutputStream os = Files.newOutputStream(filePath, StandardOpenOption.CREATE_NEW)) {
            is.transferTo(os);
        }
        FileMeta meta = new FileMeta();
        meta.setToken(token);
        meta.setOriginalFilename(originalName);
        meta.setStoredFilename(filePath.getFileName().toString());
        meta.setSize(size);
        meta.setCreatedAt(Instant.now());
        meta.setDownloadCount(0);
        meta.setLastDownloaded(null);
        meta.setContentType(contentType);
        writeMeta(meta);
        return meta;
    }

    public MetaWithStream getForDownload(String token) throws IOException {
        FileMeta meta = readMeta(token);
        if (meta == null) return null;
        Path p = filesDir.resolve(meta.getStoredFilename());
        if (!Files.exists(p)) return null;
        InputStream is = Files.newInputStream(p, StandardOpenOption.READ);
        return new MetaWithStream(meta, is);
    }

    public synchronized void recordDownload(String token) throws IOException {
        FileMeta meta = readMeta(token);
        if (meta == null) return;
        meta.setDownloadCount(meta.getDownloadCount() + 1);
        meta.setLastDownloaded(Instant.now());
        writeMeta(meta);
    }

    public FileMeta readMeta(String token) {
        Path m = metaDir.resolve(token + ".json");
        if (!Files.exists(m)) return null;
        try {
            return mapper.readValue(Files.readString(m), FileMeta.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void writeMeta(FileMeta meta) throws IOException {
        Path m = metaDir.resolve(meta.getToken() + ".json");
        Files.writeString(m, mapper.writeValueAsString(meta), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public List<FileMeta> listAllMeta() {
        try {
            return Files.list(metaDir)
                    .filter(p -> p.toString().endsWith(".json"))
                    .map(p -> {
                        try {
                            return mapper.readValue(Files.readString(p), FileMeta.class);
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    private void cleanupOld() {
        try {
            Instant cutoff = Instant.now().minus(retention);
            for (FileMeta m : listAllMeta()) {
                Instant last = m.getLastDownloaded() != null ? m.getLastDownloaded() : m.getCreatedAt();
                if (last.isBefore(cutoff)) {
                    try {
                        Files.deleteIfExists(filesDir.resolve(m.getStoredFilename()));
                        Files.deleteIfExists(metaDir.resolve(m.getToken() + ".json"));
                        System.out.println("Deleted expired file: " + m.getToken());
                    } catch (IOException e) {
                        System.err.println("Failed deleting " + m.getToken() + ": " + e.getMessage());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void shutdown() {
        cleaner.shutdown();
        try {
            if (!cleaner.awaitTermination(5, TimeUnit.SECONDS)) cleaner.shutdownNow();
        } catch (InterruptedException e) {
            cleaner.shutdownNow();
        }
    }

    private String extractExt(String name) {
        int i = name.lastIndexOf('.');
        if (i < 0) return "";
        return name.substring(i + 1);
    }

    private String generateToken(String filename) {

        String uuid = UUID.randomUUID().toString().replace("-", "");
        CRC32 crc = new CRC32();
        crc.update(filename.getBytes());
        return uuid.substring(0, 16) + Long.toHexString(crc.getValue());
    }

    public static class MetaWithStream {
        private final FileMeta meta;
        private final InputStream inputStream;

        public MetaWithStream(FileMeta meta, InputStream inputStream) {
            this.meta = meta;
            this.inputStream = inputStream;
        }

        public FileMeta getMeta() {
            return meta;
        }

        public InputStream getInputStream() {
            return inputStream;
        }
    }
}

