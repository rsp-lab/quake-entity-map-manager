package pl.radek.ent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityFile
{
    private EntNode worldSpawnNode;
    private List<EntNode> nodes = new ArrayList<>();
    
    protected void addNode(EntNode node) {
        nodes.add(node);
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("// Quake Entity file").append("\n");
        builder.append("// Number of nodes: ").append(nodes.size() + 1).append("\n");
        builder.append(worldSpawnNode);
        for (EntNode node : nodes)
            builder.append(node);
        return builder.toString();
    }
    
    protected class EntNode implements Comparable<EntNode>
    {
        private final Map<String, String> properties;
        private float originX = 0.0F;
        private float originY = 0.0F;
        private float originZ = 0.0F;
        
        @Override
        public int compareTo(EntNode entNode) {
            return this.properties.get("classname").compareTo(entNode.getProperties().get("classname"));
        }
        
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            
            builder.append("{\n");
            for (Map.Entry<String, String> entry : properties.entrySet())
                builder.append(String.format("\t\"%s\" \"%s\"\n", entry.getKey(), entry.getValue()));
            builder.append("}\n");
            
            return builder.toString();
        }
        
        EntNode(Map<String, String> properties) {
            this.properties = properties;
        }
        
        Map<String, String> getProperties() {
            return properties;
        }
    
        public float getOriginX() {
            return originX;
        }
        public float getOriginY() {
            return originY;
        }
        public float getOriginZ() {
            return originZ;
        }
        public void setOriginX(float originX) {
            this.originX = originX;
        }
        public void setOriginY(float originY) {
            this.originY = originY;
        }
        public void setOriginZ(float originZ) {
            this.originZ = originZ;
        }
    }
    
    protected EntNode getWorldSpawnNode() {
        return worldSpawnNode;
    }
    protected void setWorldSpawnNode(EntNode worldSpawnNode) {
        this.worldSpawnNode = worldSpawnNode;
    }
    protected List<EntNode> getNodes() {
        return nodes;
    }
    protected void setNodes(List<EntNode> nodes) {
        this.nodes = nodes;
    }
}
