package uk.co.donotpassgo.bogstandard;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import uk.co.donotpassgo.bogstandard.config.Config;
import uk.co.donotpassgo.bogstandard.db.Database;
import uk.co.donotpassgo.bogstandard.db.Migrator;
import uk.co.donotpassgo.bogstandard.mail.Mailer;
import uk.co.donotpassgo.bogstandard.mail.Templates;

import java.nio.file.Files;
import java.nio.file.Path;

@WebListener
public class AppListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Config cfg = new Config();
            Database db = new Database(cfg);
            new Migrator(db).apply();
            Path uploads = Path.of(cfg.uploadDir);
            Files.createDirectories(uploads);
            Mailer mailer = new Mailer(cfg);
            Templates templates = new Templates(cfg);
            sce.getServletContext().setAttribute("cfg", cfg);
            sce.getServletContext().setAttribute("db", db);
            sce.getServletContext().setAttribute("mailer", mailer);
            sce.getServletContext().setAttribute("templates", templates);
            System.out.println("[bogstandard] boot ok version=" + Version.VERSION + " stamp=" + Version.BUILD_STAMP);
        } catch (Exception e) {
            System.err.println("[bogstandard] FATAL during boot: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalStateException("Bog Standard failed to start", e);
        }
    }
}
