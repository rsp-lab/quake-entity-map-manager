package pl.radek.ent;

import java.util.*;
import java.util.function.Predicate;

public enum ActionType
{
    RemoveLights {
        @Override
        void doAction(EntityFile entityFile) {
            removeLightsAction(entityFile, properties -> {
                boolean hasLight = Objects.equals(properties.get("classname"), "light");
                return hasLight;
            });
        }
    },
    RemoveLightsSafe {
        @Override
        void doAction(EntityFile entityFile) {
            removeLightsAction(entityFile, properties -> {
                boolean hasLight = Objects.equals(properties.get("classname"), "light");
                boolean containsTargetName = properties.containsKey("targetname");
                return (hasLight && !containsTargetName);
            });
        }
    },
    Sort {
        @Override
        void doAction(EntityFile entityFile) {
            List<EntityFile.EntNode> sourceNodes = entityFile.getNodes();
            sourceNodes.sort(COMPARATOR);
        }
    };
    
    private static final Comparator<EntityFile.EntNode> COMPARATOR =
            Comparator
                .comparing((EntityFile.EntNode node) -> node.getProperties().get("classname"))
                .thenComparingDouble(EntityFile.EntNode::getOriginX)
                .thenComparingDouble(EntityFile.EntNode::getOriginY)
                .thenComparingDouble(EntityFile.EntNode::getOriginZ);
    
    void removeLightsAction(EntityFile entityFile, Predicate<Map<String, String>> removeNodePredicate) {
        List<EntityFile.EntNode> sourceNodes = entityFile.getNodes();
        List<EntityFile.EntNode> newNodes = new ArrayList<>();
        for (EntityFile.EntNode node : sourceNodes) {
            Map<String, String> properties = node.getProperties();
            if (!removeNodePredicate.test(properties))
                newNodes.add(node);
        }
        entityFile.setNodes(newNodes);
    }
    
    
    abstract void doAction(EntityFile entityFile);
}