import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ElectionApp {
    private static final ConcurrentHashMap<String, Character> votes = new ConcurrentHashMap<>();
    private static final Set<String> registeredVoters = ConcurrentHashMap.newKeySet();
    private static final AtomicInteger totalVotesCast = new AtomicInteger(0);

    private static void registerVoters() {
        // Simulate voter registration
        registeredVoters.add("Voter1");
        registeredVoters.add("Voter2");
        registeredVoters.add("Voter3");
        registeredVoters.add("Voter4");
        registeredVoters.add("Voter5");
    }

    private static boolean isRegistered(String voterId) {
        return registeredVoters.contains(voterId);
    }

    private static void castVote(String voterId, char candidate) {
        if (!isRegistered(voterId)) {
            System.out.println(voterId + " is not registered.");
            return;
        }
        if (votes.putIfAbsent(voterId, candidate) == null) {
            System.out.println(voterId + " voted successfully.");
            totalVotesCast.incrementAndGet();
        } else {
            System.out.println(voterId + " has already voted.");
        }
    }

    private static void determineWinner() {
        int votesForA = 0, votesForB = 0;
        for (char vote : votes.values()) {
            if (vote == 'A') votesForA++;
            else if (vote == 'B') votesForB++;
        }

        System.out.println("Votes for A: " + votesForA);
        System.out.println("Votes for B: " + votesForB);
        if (votesForA > votesForB) System.out.println("Candidate A wins!");
        else if (votesForB > votesForA) System.out.println("Candidate B wins!");
        else System.out.println("It's a tie!");
    }

    public static void main(String[] args) {
        registerVoters();

        // Simulating the voting process using multithreading
        ExecutorService executor = Executors.newFixedThreadPool(registeredVoters.size());
        for (String voterId : registeredVoters) {
            executor.submit(() -> {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Welcome " + voterId + ". Please type the letter of your candidate (A or B): ");
                String input = scanner.nextLine().trim().toUpperCase();
                if ("A".equals(input) || "B".equals(input)) {
                    castVote(voterId, input.charAt(0));
                } else {
                    System.out.println("Invalid candidate. Voting aborted for " + voterId);
                }
            });
        }
        
        executor.shutdown();
        try {
            boolean finished = executor.awaitTermination(1, TimeUnit.MINUTES);
            if (finished) {
                determineWinner();
            } else {
                System.out.println("Not all votes were cast in time.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}