package pl.radek.ent;

import pl.radek.ent.exception.InvalidFormatException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EntityParser
{
    private EntityParser() {}
    
    static EntityFile parseEntFile(String stringBody) {
        StringTokenizer entFileTokens = new StringTokenizer(stringBody, "}");
        EntityFile entityFile = new EntityFile();
        // Ten regex jest najlepszy od innych
        Pattern compiledPattern = Pattern.compile("[^\"]*\"([^\"]*)\"[^\"]*");
        boolean isCommentedToken = false;
        
        while (entFileTokens.hasMoreTokens()) {
            String token = entFileTokens.nextToken();
            if (token.isBlank())
                continue;
            else if (token.contains("*/"))
                isCommentedToken = false;
            else if (token.contains("/*") || isCommentedToken) {
                isCommentedToken = true;
                continue;
            }
            
            String preparedToken = prepareToken(token);
            List<String> propertyList = getPropertiesFromEntToken(preparedToken, compiledPattern);
            TreeMap<String, String> properties = null;
            try {
                if (propertyList.size() != 0) {
                    properties = convertPropertiesToMap(propertyList);
                    saveProtertiesToEntityFile(properties, entityFile);
                }
            }
            catch (Exception e) {
                throw new InvalidFormatException(preparedToken);
            }
        }
        return entityFile;
    }
    
    private static void saveProtertiesToEntityFile(TreeMap<String, String> properties, EntityFile entityFile) {
        if (properties.size() == 0)
            return;
        
        EntityFile.EntNode entNode = entityFile.new EntNode(properties);
        if (Objects.equals(properties.get("classname"), "worldspawn"))
            entityFile.setWorldSpawnNode(entNode);
        else {
            if (properties.containsKey("origin")) {
                String originString = properties.get("origin");
                String[] originProperties = originString.split(" ");
                entNode.setOriginX(Float.parseFloat(originProperties[0]));
                entNode.setOriginY(Float.parseFloat(originProperties[1]));
                entNode.setOriginZ(Float.parseFloat(originProperties[2]));
            }
            entityFile.addNode(entNode);
        }
    }
    
    private static String prepareToken(String token) {
        String uncommentedToken = token.lines()
                .filter(line -> !line.contains("//"))
                .collect(Collectors.joining());
        return uncommentedToken.replace("{", "");
    }
    
    private static List<String> getPropertiesFromEntToken(String token) {
        Matcher matcher = Pattern.compile("[^\"]*\"([^\"]*)\"[^\"]*")
                .matcher(token);
        return matcher.results()
                .map(matchResult -> matchResult.group(1))
                .collect(Collectors.toList());
    }
    
    private static List<String> getPropertiesFromEntToken(String token, Pattern compiledPattern) {
        Matcher matcher = compiledPattern.matcher(token);
        return matcher.results()
                .map(matchResult -> matchResult.group(1))
                .collect(Collectors.toList());
    }
    
    private static TreeMap<String, String> convertPropertiesToMap(List<String> propertyList) throws Exception {
        return IntStream.iterate(0, i -> i < propertyList.size(), i -> i + 2).collect(TreeMap::new, (map, i) -> map.put(propertyList.get(i), propertyList.get(i + 1)), TreeMap::putAll);
    }
}
