package com.ubl.cfg.engine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.ubl.cfg.data.Constants;

import soot.Unit;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import soot.toolkits.scalar.Pair;

/**
 * Copyright (c) 2018, Zhijun Yin. All rights reserved.
 */
public class PermissionFlowGraph {

    private Map<Long, FlowGraphNode> nodes;
    private Map<Long, List<Long>> outgoingEdges;
    private Map<Long, List<Long>> incomingEdges;
    private Set<FlowGraphNode> entries;

    private boolean condensed;
    private PermissionFlowGraph condensedGraph;

    public PermissionFlowGraph() {
        nodes = new HashMap<>();
        outgoingEdges = new HashMap<>();
        incomingEdges = new HashMap<>();
        entries = new HashSet<>();
        condensed = false;
    }

    public void addNode(FlowGraphNode graphNode) {
        nodes.put(graphNode.getId(), graphNode);
        entries.add(graphNode);
    }

    public void addEdge(long from, long to) {
        if (!outgoingEdges.containsKey(from)) {
            outgoingEdges.put(from, new ArrayList<>());
        }
        outgoingEdges.get(from).add(to);

        if (!incomingEdges.containsKey(to)) {
            incomingEdges.put(to, new ArrayList<>());
        }
        incomingEdges.get(to).add(from);

        entries.remove(nodes.get(to));
    }

    public long findNodeByComponent(Unit v) {
        for (FlowGraphNode node : nodes.values()) {
            if (node.containsComponent(v)) {
                return node.getId();
            }
        }
        return -1;
    }

    public PermissionFlowGraph condenseGraph() {
        if (condensed) {
            return this;
        }

        condensedGraph = new PermissionFlowGraph();

        for (FlowGraphNode node : entries) {
            dfs(new FlowGraphNode(), node.getId(), new ArrayList<>());
        }

        condensed = true;
        return condensedGraph;
    }

    private Long dfs(FlowGraphNode container, Long nodeId, List<Long> visited) {
        // check if nodeId has been visited
        if (visited.contains(nodeId)) {
            return container.getId();
        }

        visited.add(nodeId);

        FlowGraphNode node = nodes.get(nodeId);
        if (outgoingEdges.getOrDefault(nodeId, new ArrayList<>()).size() <= 1
                && incomingEdges.getOrDefault(nodeId, new ArrayList<>()).size() <= 1
                && node.getPermissions().equals(container.getPermissions())) {

            container.merge(node);
            List<Long> children = outgoingEdges.getOrDefault(nodeId, new ArrayList<>());
            if (children.size() == 0) {
                return container.getId();
            }
            return dfs(container, children.get(0), visited);
        }

        condensedGraph.addNode(node);
        for (Long childNode : outgoingEdges.getOrDefault(nodeId, new ArrayList<>())) {
            condensedGraph.addEdge(nodeId, dfs(new FlowGraphNode(), childNode, visited));
        }

        if (!container.isEmpty()) {
            condensedGraph.addNode(container);
            condensedGraph.addEdge(container.getId(), nodeId);
            return container.getId();
        }
        return node.getId();
    }

    public int size() {
        return nodes.size();
    }

    public Set<FlowGraphNode> getEntries() {
        return entries;
    }

    public Map<Long, FlowGraphNode> getNodes() {
        return nodes;
    }

    public Map<Long, List<Long>> getOutgoingEdges() {
        return outgoingEdges;
    }

	public void printGraph(InfoflowCFG infoflowCFG) {
		System.out.printf("There are %d nodes in the permission flow graph\n", nodes.size());
		for (FlowGraphNode node : nodes.values()) {
			System.out.println(node);
		}

		for (long from : outgoingEdges.keySet()) {
			System.out.printf("Edge from node %d to node(s) %s\n", from, outgoingEdges.get(from));
		}

		List<FlowGraphNode> n = nodes.values().stream()
				.filter(p -> Constants.API_LIST_ARR.stream().anyMatch(q -> p.containsApi(q)))
				.collect(Collectors.toList());
		
		System.out.println();
		
		for (long from : outgoingEdges.keySet()) {
			if (n.stream().anyMatch(p -> p.getId() == from)
					|| outgoingEdges.get(from).stream().anyMatch(p -> n.stream().anyMatch(q -> q.getId() == p))) {
				System.out.printf("N Edge from node %d to node(s) %s\n", from, outgoingEdges.get(from));
			}

		}
	}
	
	public String printGraph() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("There are %d nodes in the permission flow graph\n", nodes.size()));
		return sb.toString();
	}
    
    public List<Pair<String, Boolean>> ApiUsageDataset () {
    	List<Pair<String, Boolean>> set = new ArrayList<Pair<String,Boolean>>();
    	for(String col : Constants.API_LIST_ARR) {
    		boolean res = nodes.values().stream().anyMatch(p -> p.containsApi(col));
    		set.add(new Pair<String, Boolean>(col, res));
    	}
    	return set;
    }
    
    public List<Pair<String, Long>> ApiFrequenceDataset() {
    	List<Pair<String, Long>> set = new ArrayList<Pair<String,Long>>();
    	for(String col : Constants.API_LIST_ARR) {
    		long res = nodes.values().stream().filter(p -> p.containsApi(col)).count();
    		set.add(new Pair<String, Long>(col, res));
    	}
    	return set;
    }
    
    public List<Long> ApiSequenceDataset() {
    	List<Long> set = new ArrayList<Long>();
    	List<FlowGraphNode> n = nodes.values().stream()
				.filter(p -> Constants.API_LIST_ARR.stream().anyMatch(q -> p.containsApi(q)))
				.collect(Collectors.toList());
    	List<Long> seqs = new ArrayList<Long>();
    	List<Long> newSeqs = new ArrayList<Long>();
    	for (long from : outgoingEdges.keySet()) {
    		if (n.stream().anyMatch(p -> p.getId() == from)
    				|| outgoingEdges.get(from).stream().anyMatch(p -> n.stream().anyMatch(q -> q.getId() == p))) {
    			int size = outgoingEdges.get(from).size();
    			Long id = outgoingEdges.get(from).get(size-1);
    			seqs.add(id);
    		}
    	}
    	
    	for(Long id : seqs) {
    		if(!newSeqs.contains(id)) {
    			newSeqs.add(id);
    		}
    	}
    	
    	for (int i=0; i<newSeqs.size(); i++) {
    		Integer innerI = Integer.valueOf(i);
    		FlowGraphNode node = n.stream().filter(p -> p.getId() == newSeqs.get(innerI)).findFirst().orElse(null);
    		if(node != null) {
    			int idx = IntStream.range(0, Constants.API_LIST_ARR.size()).filter(p -> node.containsApi(Constants.API_LIST_ARR.get(p))).findFirst().orElse(-1);
    			if (idx > 0) {
    				set.add(Long.valueOf((long) idx));
    			}
    		}
    	}
    	
    	return set;
    }
    
    
}
