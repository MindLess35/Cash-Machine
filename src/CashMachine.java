import java.math.BigDecimal;
import java.time.Instant;

public class CashMachine {

    private DataStorage dataStorage;

    private BankCard currentBankCard;
    public static final String CARD_NUMBER_PATTERN = "^\\d{4}-\\d{4}-\\d{4}-\\d{4}$";
    private static BigDecimal totalMoneyInCashMachine = BigDecimal.valueOf(1_000_00);
    public static final BigDecimal MAXIMUM_AMOUNT_OF_REPLENISHMENT_BALANCE = BigDecimal.valueOf(1_000_000);

    public static final int MAXIMUM_COUNT_OF_FAILED_ATTEMPTS_ENTER_PIN_CODE = 3;

    public CashMachine(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    public boolean withdrawMoney(BigDecimal amount) {
        BigDecimal currentCardBalance = getCurrentCardBalance();
        if (currentCardBalance.compareTo(amount) >= 0
            && totalMoneyInCashMachine.compareTo(amount) >= 0) {
            currentBankCard.setBalance(currentCardBalance.subtract(amount));
            totalMoneyInCashMachine = totalMoneyInCashMachine.subtract(amount);
            return true;
        }
        return false;
    }

    public boolean depositMoney(BigDecimal amount) {
        if (MAXIMUM_AMOUNT_OF_REPLENISHMENT_BALANCE.compareTo(amount) >= 0) {
            currentBankCard.setBalance(getCurrentCardBalance().add(amount));
            totalMoneyInCashMachine = totalMoneyInCashMachine.add(amount);
            return true;
        }
        return false;
    }
    public void blockCurrentCard() {
        currentBankCard.block();
        updateDataInBank();
    }


    public boolean isCardNumberFormatValid(String cardNumber) {
        return cardNumber.matches(CARD_NUMBER_PATTERN);
    }

    public boolean isCardExistsInBank(String cardNumber) {
        currentBankCard = dataStorage.extractCardFromBank(cardNumber);
        return true;
    }

    public boolean isPinCodeCorrect(int pinCode) {
        return pinCode == currentBankCard.getPinCode();
    }

    public BigDecimal getCurrentCardBalance() {
        return currentBankCard.getBalance();
    }

    public boolean updateDataInBank() {
       return dataStorage.updateDataStorage(currentBankCard);
    }

    public boolean isCurrentCardBlocked() {
        return currentBankCard.isBlocked();
    }
    public void checkCardBlockingStatus() {
        boolean isCardBlocked = currentBankCard.isBlocked();
        Instant blockedUntil = currentBankCard.getBlockedUntil();

        if (isCardBlocked && blockedUntil != null && Instant.now().isAfter(blockedUntil)) {
            currentBankCard.unblock();
            System.out.println("Ваша карта была разблокирована.");
        }
    }

}
