package org.poo.core;

import org.poo.fileio.ExchangeInput;

import java.util.*;

public class CurrencyExchange {
    private final Map<String, List<Pair<String, Double>>> exchangeGraph;

    public CurrencyExchange(ExchangeInput[] exchangeRates) {
        exchangeGraph = new HashMap<>();
        buildGraph(exchangeRates);
    }

    private record Pair<K, V>(K key, V value) {}

    private void buildGraph(ExchangeInput[] exchangeRates) {
        for (ExchangeInput rate : exchangeRates) {
            exchangeGraph.computeIfAbsent(rate.getFrom(), k -> new ArrayList<>())
                    .add(new Pair<>(rate.getTo(), rate.getRate()));

            exchangeGraph.computeIfAbsent(rate.getTo(), k -> new ArrayList<>())
                    .add(new Pair<>(rate.getFrom(), 1 / rate.getRate()));
        }
    }

    public double findRate(String from, String to) {
        if (!exchangeGraph.containsKey(from) || !exchangeGraph.containsKey(to)) {
            return -1;
        }

        Set<String> visited = new HashSet<>();
        return dfs(from, to, 1.0, visited);
    }

    private double dfs(String current, String target, double accumulatedRate, Set<String> visited) {
        if (current.equals(target)) {
            return accumulatedRate;
        }

        visited.add(current);

        for (Pair<String, Double> neighbor : exchangeGraph.get(current)) {
            if (!visited.contains(neighbor.key())) {
                double rate = dfs(neighbor.key(), target,
                        accumulatedRate * neighbor.value(), visited);
                if (rate != -1) {
                    return rate;
                }
            }
        }

        return -1;
    }
}
