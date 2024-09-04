import java.io.*;
import java.util.*;

 class AprioriAlgorithm {
    private static final int MIN_SUPPORT = 2;

    public static void main(String[] args) {
        String filename = "input.txt";
        try {
            List<Set<Integer>> transactions = readTransactions(filename);
            
            int k = 1;
            Set<Set<Integer>> frequentItemsets = new HashSet<>();
            Set<Set<Integer>> currentCandidates = generateSingleItemCandidates(transactions);

            while (!currentCandidates.isEmpty()) {
                Map<Set<Integer>, Integer> supportCounts = countSupport(transactions, currentCandidates);
                Set<Set<Integer>> frequentItemsetsCurrent = filterFrequentItemsets(supportCounts, MIN_SUPPORT);

                if (frequentItemsetsCurrent.isEmpty()) {
                    break;
                }

                printItemsets(supportCounts, "Candidate itemsets of size " + k, currentCandidates);
                printItemsets(supportCounts, "Frequent itemsets of size " + k, frequentItemsetsCurrent);

                frequentItemsets.addAll(frequentItemsetsCurrent);
                currentCandidates = generateNewCandidates(frequentItemsets, k + 1);
                k++;
            }
        } catch (IOException e) {
            System.err.println("Error reading the transaction file: " + e.getMessage());
        }
    }

    private static List<Set<Integer>> readTransactions(String filename) throws IOException {
        List<Set<Integer>> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] items = line.trim().split("\\s+");
                Set<Integer> transaction = new HashSet<>();
                for (String item : items) {
                    transaction.add(Integer.parseInt(item));
                }
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    private static Set<Set<Integer>> generateSingleItemCandidates(List<Set<Integer>> transactions) {
        Set<Set<Integer>> candidates = new HashSet<>();
        for (Set<Integer> transaction : transactions) {
            for (Integer item : transaction) {
                candidates.add(Collections.singleton(item));
            }
        }
        return candidates;
    }

    private static Map<Set<Integer>, Integer> countSupport(List<Set<Integer>> transactions, Set<Set<Integer>> candidates) {
        Map<Set<Integer>, Integer> supportCounts = new HashMap<>();
        for (Set<Integer> candidate : candidates) {
            supportCounts.put(candidate, 0);
        }
        for (Set<Integer> transaction : transactions) {
            for (Set<Integer> candidate : candidates) {
                if (transaction.containsAll(candidate)) {
                    supportCounts.put(candidate, supportCounts.get(candidate) + 1);
                }
            }
        }
        return supportCounts;
    }

    private static Set<Set<Integer>> filterFrequentItemsets(Map<Set<Integer>, Integer> supportCounts, int minSupport) {
        Set<Set<Integer>> frequentItemsets = new HashSet<>();
        for (Map.Entry<Set<Integer>, Integer> entry : supportCounts.entrySet()) {
            if (entry.getValue() >= minSupport) {
                frequentItemsets.add(entry.getKey());
            }
        }
        return frequentItemsets;
    }

    private static Set<Set<Integer>> generateNewCandidates(Set<Set<Integer>> frequentItemsets, int size) {
        Set<Set<Integer>> candidates = new HashSet<>();
        List<Set<Integer>> frequentItemsetsList = new ArrayList<>(frequentItemsets);
        for (int i = 0; i < frequentItemsetsList.size(); i++) {
            for (int j = i + 1; j < frequentItemsetsList.size(); j++) {
                Set<Integer> itemset1 = frequentItemsetsList.get(i);
                Set<Integer> itemset2 = frequentItemsetsList.get(j);
                Set<Integer> union = new HashSet<>(itemset1);
                union.addAll(itemset2);
                if (union.size() == size) {
                    candidates.add(union);
                }
            }
        }
        return candidates;
    }

    private static void printItemsets(Map<Set<Integer>, Integer> supportCounts, String title, Set<Set<Integer>> itemsets) {
        System.out.println(title + ":");
        for (Set<Integer> itemset : itemsets) {
            System.out.println(itemset + " (support: " + supportCounts.get(itemset) + ")");
        }
        System.out.println();
    }
}
