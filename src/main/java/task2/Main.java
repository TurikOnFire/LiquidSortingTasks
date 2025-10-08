package task2;

import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import io.javalin.http.staticfiles.Location;
import task2.storage.FileService;

import java.io.InputStream;
import java.nio.file.Path;
import java.time.Duration;

public class Main {
    public static void main(String[] args) {

        Path storageDir = Path.of("data/uploads");
        int port = 8080;
        Duration retention = Duration.ofDays(30); // файлы не скачанные в течение 30 дней будут удаляться

        FileService fileService = new FileService(storageDir, retention);

        Javalin app = Javalin.create(cfg -> {

            cfg.staticFiles.add(staticFiles -> {
                staticFiles.directory = "/public"; // папка в resources
                staticFiles.location = Location.CLASSPATH;
            });

            cfg.plugins.enableCors(cors -> {
                cors.add(it -> {
                    it.anyHost();
                });
            });
        }).start(port);

        // Эндпоинт загрузки
        app.post("/upload", ctx -> {
            UploadedFile uf = ctx.uploadedFile("file");
            if (uf == null) {
                ctx.status(400).json(java.util.Map.of("error", "no file field 'file'"));
                return;
            }
            String originalName = uf.filename();
            long size = uf.size();
            try (InputStream is = uf.content()) {
                var meta = fileService.store(is, originalName, size, uf.contentType());
                String downloadUrl = ctx.req().getScheme() + "://" + ctx.req().getServerName() + ":" + ctx.req().getServerPort()
                        + "/d/" + meta.getToken();
                ctx.json(java.util.Map.of(
                        "token", meta.getToken(),
                        "downloadUrl", downloadUrl,
                        "originalName", meta.getOriginalFilename(),
                        "size", meta.getSize()
                ));
            }
        });

        // Эндпоинт скачивания
        app.get("/d/{token}", ctx -> {
            String token = ctx.pathParam("token");
            var entry = fileService.getForDownload(token);
            if (entry == null) {
                ctx.status(404).result("Not found");
                return;
            }
            // обновляем метаданные
            fileService.recordDownload(token);

            ctx.header("Content-Disposition", "attachment; filename=\"" + entry.getMeta().getOriginalFilename().replace("\"","'") + "\"");
            ctx.contentType(entry.getMeta().getContentType() == null ? "application/octet-stream" : entry.getMeta().getContentType());
            ctx.result(entry.getInputStream());
        });

        // Эндпоинт метаданных
        app.get("/meta/{token}", ctx -> {
            String token = ctx.pathParam("token");
            var meta = fileService.readMeta(token);
            if (meta == null) {
                ctx.status(404).json(java.util.Map.of("error", "not found"));
                return;
            }
            ctx.json(meta);
        });

        // Админский эндпоинт
        app.get("/admin/list", ctx -> {
            ctx.json(fileService.listAllMeta());
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            fileService.shutdown();
            app.stop();
        }));

        System.out.println("Server started at http://localhost:" + port + " (uploads -> " + storageDir.toAbsolutePath() + ")");
    }
}

