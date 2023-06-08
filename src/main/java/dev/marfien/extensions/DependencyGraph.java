package dev.marfien.extensions;

import com.google.common.collect.Maps;
import dev.marfien.extensions.extension.DiscoveredExtension;
import dev.marfien.extensions.extension.ExtensionDescription;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DependencyGraph {

    private final Map<String, Collection<String>> graph;

    private final Map<String, DiscoveredExtension> idToExtensionMapper;

    public DependencyGraph(@NotNull Collection<DiscoveredExtension> extensions) {
        this.idToExtensionMapper = createIdMap(extensions);
        this.graph = Maps.newHashMapWithExpectedSize(extensions.size());

        // save some power
        if (!extensions.isEmpty())
            this.fillDependencyGraph(extensions);
    }

    private static Map<String, DiscoveredExtension> createIdMap(@NotNull Collection<DiscoveredExtension> extensions) {
        if (extensions.isEmpty()) return Maps.newHashMap();

        // I'd love to use guavas Maps.uniqueIndex(Collection<T>, Function<T, I>), but I need a mutable version
        // to be able to add new edges later on
        Map<String, DiscoveredExtension> idMap = Maps.newHashMapWithExpectedSize(extensions.size());

        for (DiscoveredExtension extension : extensions) {
            idMap.put(extension.getDescription().id(), extension);
        }

        return idMap;
    }

    public void addEdge(@NotNull DiscoveredExtension extension) {
        ExtensionDescription description = extension.getDescription();
        // mapping the dependencies to their id
        this.graph.put(description.id(),
                description.dependencies()
                        .stream()
                        .map(ExtensionDescription.Dependency::id)
                        .collect(Collectors.toSet()));
    }

    public void removeEdge(@NotNull String id) {
        this.graph.remove(id);
        this.idToExtensionMapper.remove(id);
    }

    public void removeEdge(@NotNull DiscoveredExtension extension) {
        this.removeEdge(extension.getDescription().id());
    }

    public List<DiscoveredExtension> sort() {
        if (this.graph.isEmpty()) return List.of();

        // linked list is way more performed for adding elements many times to the first index
        List<String> result = new LinkedList<>();
        Set<String> nodes = this.graph.keySet();
        // create a map to keep track of which nodes are already checked
        Map<String, NodeState> nodeVisitingStates = nodes.stream()
                .collect(Collectors.toMap(Function.identity(), ignored -> NodeState.NOT_VISITED));

        for (String currentNode : nodes) {
            // no need to visit a node twice
            if (nodeVisitingStates.get(currentNode) == NodeState.VISITED) continue;

            this.visitNode(currentNode, nodeVisitingStates, result, new Stack<>());
        }

        return result.stream()
                .map(this.idToExtensionMapper::get)
                .toList();
    }

    private void visitNode(
            @NotNull String currentNode,
            @NotNull Map<String, NodeState> nodeVisitingStates,
            @NotNull List<String> result,
            // used to keep track of cycle dependencies
            @NotNull Stack<String> stackTrace) {
        NodeState state = nodeVisitingStates.get(currentNode);

        if (state == null) throw new RuntimeException(); // TODO exception for unknown dependency

        // early return, all work was already done previously
        if (state == NodeState.VISITED) return;

        stackTrace.push(currentNode);

        switch (state) {
            // we found a cycle dependency
            case VISITING -> { throw new RuntimeException(); } // TODO exception for cycle dependencies
            case NOT_VISITED -> {
                // mark as currently visiting
                nodeVisitingStates.put(currentNode, NodeState.VISITING);

                for (String node : this.graph.get(currentNode)) {
                    this.visitNode(node, nodeVisitingStates, result, stackTrace);
                }

                // mark as done
                nodeVisitingStates.put(currentNode, NodeState.VISITED);

                // clear up the stack from this node
                stackTrace.pop();
                // add this node to the result
                result.add(currentNode);
            }
        }
    }

    private void fillDependencyGraph(@NotNull Collection<DiscoveredExtension> extensions) {
        for (DiscoveredExtension extension : extensions) {
            this.addEdge(extension);
        }
    }

    private enum NodeState {

        // already looked up
        VISITED,

        // currently looking up -> cycle dependency
        VISITING,

        // not visited yet
        NOT_VISITED

    }
}