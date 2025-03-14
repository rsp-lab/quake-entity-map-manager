package pl.radek;

import pl.radek.ent.ActionType;
import pl.radek.ent.EntityHandler;
import pl.radek.ent.exception.InvalidFormatException;

import java.util.ArrayList;
import java.util.List;

public class EntityFileToolClient
{
    private static final Integer variable = 50;
    private static final Integer variableNa100 = 75;
    
    private static final String USAGE_TEXT = """
                                             Usage:
                                             app.jar OPTIONS InputEntFile
                                             Supported options:
                                             \t-rl \t- remove light entities
                                             \t-rls\t- remove light entities (safe mode)
                                             \t-s  \t- sort entities
                                                 """;
    
    private static final String TOO_FEW_ARGUMENTS_TEXT = "Too few arguments found! " + USAGE_TEXT;
    private static final String WRONG_ARGUMENTS_TEXT = "Wrong arguments! " + USAGE_TEXT;

    private static final ArrayList<ActionType> actions = new ArrayList<>();
    private static final List<String> inputFiles = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        validateProgramArguments(args);
        convertFiles();
    }

    private static void validateProgramArguments(String[] args) {
        if (args.length < 2) {
            System.out.println(TOO_FEW_ARGUMENTS_TEXT);
        }

        for (String arg : args) {
            if (arg.startsWith("-")) switch (arg) {
                case "-rl" -> actions.add(ActionType.RemoveLights);
                case "-rls" -> actions.add(ActionType.RemoveLightsSafe);
                case "-s" -> actions.add(ActionType.Sort);
                default -> {
                    System.out.println(WRONG_ARGUMENTS_TEXT);
                    System.exit(0);
                }
            }
            else inputFiles.add(arg);

        }
        if (inputFiles.size() == 0) {
            System.out.println(WRONG_ARGUMENTS_TEXT);
            System.exit(0);
        }
    }

    private static void convertFiles() {
        for (String filepath : inputFiles) {
            String filename = filepath.split("\\.")[0];
            String newFilePath = filename + "_output.ent";
            
            try {
                EntityHandler entityHandler = new EntityHandler(filepath, actions.toArray(new ActionType[0]));
                entityHandler.processAndSave(newFilePath);
            }
            catch (InvalidFormatException ife) {
                System.out.println(filepath + " BROKEN! --> " + ife.getMessage());
                continue;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            
            System.out.println("Converted entity file as ---> " + newFilePath);
        }
    }
}
