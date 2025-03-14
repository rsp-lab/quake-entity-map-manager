package pl.radek.ent;

import pl.radek.ent.exception.InvalidFormatException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class EntityHandler
{
    private final EntityFile entityFile;
    private ActionType[] actions;
    
    public EntityHandler(String filepath, ActionType... actions) throws Exception {
        if (filepath.isBlank())
            throw new IllegalArgumentException("Path to ent file is blank!");
        if (Files.notExists(Path.of(filepath)))
            throw new IllegalArgumentException(filepath + " file doesn't exist!");
        this.actions = actions;
        entityFile = convertToObjectModel(filepath);
    }
    
    public EntityHandler process() {
        if (actions.length == 0)
            throw new IllegalStateException("Ent Handler needs at least one action type!");
        for (ActionType action : actions)
            action.doAction(entityFile);
        return this;
    }
    
    public void processAndSave(String newFilePath) throws IOException {
        process();
        saveToFile(newFilePath);
    }
    
    public void saveToFile(String newFilePath) throws IOException {
        if (newFilePath.isBlank())
            throw new IllegalArgumentException("Path to ent file is blank!");
        Files.writeString(Path.of(newFilePath), getStringBody());
    }
    
    public void setActions(ActionType... actions) {
        this.actions = actions;
    }
    
    public String getStringBody() {
        return entityFile.toString();
    }
    
    public EntityFile convertToObjectModel(String filepath) throws Exception {
        // ISO-8859-1 is all-inclusive charsets. It's guaranteed not to throw MalformedInputException. So it's good for debugging.
        EntityFile entityFile = EntityParser.parseEntFile(Files.readString(Path.of(filepath), StandardCharsets.ISO_8859_1));
        if (entityFile.getWorldSpawnNode() == null)
            throw new InvalidFormatException("Ent file has no WORLDSPAWN property!");
        return entityFile;
    }
}
