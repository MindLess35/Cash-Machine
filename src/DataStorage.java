import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DataStorage {
    private Path filePath;

    private Set<BankCard> cards;

    public DataStorage(Path filePath) {
        this.filePath = filePath;
    }

    public boolean loadCardsData() {
        cards = new HashSet<>();
        try {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                String[] parts = line.split(" ");
                String cardNumber = parts[0];
                int pinCode = Integer.parseInt(parts[1]);
                BigDecimal balance = new BigDecimal(parts[2]);
                boolean isBlocked = Boolean.parseBoolean(parts[3]);
                Instant blockedUntil = null;
                if (parts.length == 5)
                    blockedUntil = Instant.ofEpochSecond(Long.parseLong(parts[4]));
                cards.add(new BankCard(cardNumber, pinCode, balance, isBlocked, blockedUntil));
            }
            return true;
        } catch (IOException e) {
            System.out.println("Извините, в данный момент банковские операции недоступны. Попробуйте чуть позже.");
            return false;
        }
    }

    public BankCard extractCardFromBank(String cardNumber) {
        return cards.stream()
                .filter(card -> card.getNumber().equals(cardNumber))
                .findFirst()
                .orElseThrow();
    }

    public boolean updateDataStorage(BankCard currentBankCard) {
        cards.add(currentBankCard); // текущая карта перезапишет саму себя в сете, тк по методам иквелз и хешкод
        // в классе BankCard карты сравниваются только по номеру, тк он уникален. Другое дело, что в файле уже должны
        // лежать все уникальные номера карт, иначе повторяющихся не будет - их последняя с таким же номером перепишет.
        try {
            Locale.setDefault(Locale.US);
            List<String> lines = new ArrayList<>();
            for (BankCard card : cards) {
                String line = String.format("%s %d %.2f %b", card.getNumber(), card.getPinCode(),
                        card.getBalance().doubleValue(), card.isBlocked());
                if (card.getBlockedUntil() != null) {
                    line += " " + card.getBlockedUntil().getEpochSecond();
                }
                lines.add(line);
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (IOException e) {
            System.out.println("Извините, в данный момент невозможно сохранить данные по банковским картам. Попробуйте чуть позже.");
            return false;
        }
    }

}

