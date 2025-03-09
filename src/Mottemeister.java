import java.util.*;

// Peaklass
public class Mottemeister {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Tere tulemast Mastermind mängu!");
        System.out.println("Sisesta oma nimi: ");
        String playerName = scanner.nextLine();

        System.out.println("Vali salakoodi pikkus (nt 4): ");
        int codeLength = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        ManguJuhtimine game = new ManguJuhtimine(playerName, codeLength);
        game.käivitaMäng();
    }
}

// MänguJuhtimine klass
class ManguJuhtimine {
    private Salakood salakood;
    private Mängija mängija;
    private boolean onLäbi;

    public ManguJuhtimine(String playerName, int codeLength) {
        this.salakood = new Salakood(codeLength);
        this.mängija = new Mängija(playerName);
        this.onLäbi = false;
    }

    public void käivitaMäng() {
        Scanner scanner = new Scanner(System.in);
        while (!onLäbi) {
            System.out.println("Sisesta oma pakkumine (nt: punane sinine kollane roheline must valge): ");
            String input = scanner.nextLine();
            List<String> pakkumine = Arrays.asList(input.split(" "));

            Tagasiside tagasiside = salakood.kontrolliPakkumist(pakkumine);
            mängija.lisaPakkumine(pakkumine);
            System.out.println("Tagasiside: " + tagasiside);

            if (tagasiside.onVõit(salakood.getCodeLength())) {
                System.out.println("Palju õnne! Oled salakoodi ära arvanud.");
                onLäbi = true;
            }
        }
    }
}

// Salakood klass
class Salakood {
    private List<String> code;
    private static final List<String> värvid = Arrays.asList("punane", "sinine", "kollane", "roheline", "must", "valge");

    public Salakood(int length) {
        this.code = generaatoriKood(length);
    }

    private List<String> generaatoriKood(int length) {
        List<String> shuffledColors = new ArrayList<>(värvid);
        Collections.shuffle(shuffledColors);
        return shuffledColors.subList(0, length);
    }

    public int getCodeLength() {
        return code.size();
    }

    public Tagasiside kontrolliPakkumist(List<String> pakkumine) {
        int olemasoigekoht = 0;
        int olemasvalekoht = 0;
        int õigedVärvid = 0;
        List<String> tempCode = new ArrayList<>(code);
        List<String> remainingGuesses = new ArrayList<>();
        List<String> unmatchedCode = new ArrayList<>();

        // Step 1: Find exact matches
        for (int i = 0; i < pakkumine.size(); i++) {
            if (pakkumine.get(i).equals(tempCode.get(i))) {
                olemasoigekoht++;
                tempCode.set(i, null); // Mark as matched
            } else {
                remainingGuesses.add(pakkumine.get(i));
                unmatchedCode.add(tempCode.get(i));
            }
        }

        // Step 2: Find correct colors in wrong places
        for (String guess : remainingGuesses) {
            if (unmatchedCode.contains(guess)) {
                olemasvalekoht++;
                unmatchedCode.remove(guess); // Prevent double counting
            }
        }

        // Step 3: Count correctly guessed colors (ignoring positions)
        Set<String> uniqueGuesses = new HashSet<>(pakkumine);
        for (String color : uniqueGuesses) {
            if (code.contains(color)) {
                õigedVärvid++;
            }
        }

        return new Tagasiside(olemasvalekoht, olemasoigekoht, õigedVärvid);
    }
}

// Mängija klass
class Mängija {
    private String name;
    private List<List<String>> eelnevadpakkumised;

    public Mängija(String name) {
        this.name = name;
        this.eelnevadpakkumised = new ArrayList<>();
    }

    public void lisaPakkumine(List<String> pakkumine) {
        eelnevadpakkumised.add(pakkumine);
    }
}

// Tagasiside klass
class Tagasiside {
    private int olemasvalekoht;
    private int olemasoigekoht;
    private int õigedVärvid;

    public Tagasiside(int olemasvalekoht, int olemasoigekoht, int õigedVärvid) {
        this.olemasvalekoht = olemasvalekoht;
        this.olemasoigekoht = olemasoigekoht;
        this.õigedVärvid = õigedVärvid;
    }

    public boolean onVõit(int codeLength) {
        return olemasoigekoht == codeLength;
    }

    @Override
    public String toString() {
        return "Õiges kohas: " + olemasoigekoht + ", Vale kohas: " + olemasvalekoht + ", Õiged värvid kokku: " + õigedVärvid;
    }
}