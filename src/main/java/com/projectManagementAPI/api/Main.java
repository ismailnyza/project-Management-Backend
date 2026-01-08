// java
package com.projectManagementAPI.api;

import com.projectManagementAPI.api.config.AppConfig;
import com.projectManagementAPI.api.controller.BoardController;
import com.projectManagementAPI.api.repository.BoardRepository;
import com.projectManagementAPI.api.service.BoardService;
import com.projectManagementAPI.api.error.StartupException;
import com.projectManagementAPI.api.util.EnvLoader;
import com.projectManagementAPI.api.util.EnvValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            System.out.println("PWD = " + Paths.get(".").toAbsolutePath());
            EnvLoader.loadDotEnv(".env");
            EnvValidator.ensure("APP_PORT","DB_URL","DB_USER","DB_PASS");

            AppConfig.load();

            BoardRepository boardRepository = new BoardRepository();
            BoardService boardService = new BoardService(boardRepository);
            new BoardController(boardService);
            log.info("main started");
        } catch (StartupException se) {
            log.error("Startup error", se);
            System.exit(2); // distinct exit code for startup/config errors
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            log.error("Unhandled exception in main", t);
            System.exit(1);
        }
    }
}
