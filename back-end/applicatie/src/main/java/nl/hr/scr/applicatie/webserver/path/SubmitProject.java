package nl.hr.scr.applicatie.webserver.path;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.validation.BodyValidator;
import nl.hr.scr.applicatie.Main;
import nl.hr.scr.applicatie.webserver.Webserver;
import nl.hr.scr.applicatie.webserver.json.ProjectDetails;

import java.util.Map;
import java.util.Optional;

/* Eli van der Does (1061322) @ December 01, 2023 */
public class SubmitProject {
    private final Main main;

    public SubmitProject(Webserver webserver, Main main) {
        this.main = main;
        webserver.http().post("api/submit-project", this::handle);
    }

    public void handle(Context context) {
        BodyValidator<ProjectDetails> dataValidator = context.bodyValidator(ProjectDetails.class);

        if (!dataValidator.errors().isEmpty()) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        ProjectDetails details = dataValidator.get();
        Optional<Map<String, Object>> inserted = main.sql().statement(
            "INSERT INTO projects (name, description, creator_name, active) VALUES (?, ?, ?, ?)",
            details.name(),
            details.description(),
            details.creatorName(),
            details.active()
        ).update().complete(data -> {
            if (data.next()) {
                return Map.of(
                    "id", data.getInt("id"),
                    "name", data.getString("name"),
                    "creation_date", data.getInt("creation_date"),
                    "description", data.getString("description"),
                    "creator_name", data.getString("creator_name"),
                    "active", data.getBoolean("active")
                );
            }
            return null;
        });

        if (inserted.isEmpty()) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        context.json(inserted.get());
    }
}