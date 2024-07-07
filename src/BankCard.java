import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class BankCard {
    private String number;
    private int pinCode;
    private BigDecimal balance;
    private boolean isBlocked;
    private Instant blockedUntil;

    public BankCard(String number,
                    int pinCode,
                    BigDecimal balance,
                    boolean isBlocked,
                    Instant blockedUntil) {

        this.number = number;
        this.pinCode = pinCode;
        this.balance = balance;
        this.isBlocked = isBlocked;
    }

    public String getNumber() {
        return number;
    }

    public int getPinCode() {
        return pinCode;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBalance(BigDecimal newBalance) {
        this.balance = newBalance;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setPinCode(int pinCode) {
        this.pinCode = pinCode;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public Instant getBlockedUntil() {
        return blockedUntil;
    }

    public void setBlockedUntil(Instant blockedUntil) {
        this.blockedUntil = blockedUntil;
    }

    public void block() {
        this.isBlocked = true;
        blockedUntil = Instant.now().plus(Duration.ofDays(1));
    }

    public void unblock() {
        this.isBlocked = false;
        blockedUntil = null;

    }

    public boolean validatePinCode(int pinCode) {
        return this.pinCode == pinCode;
    }

    @Override
    public String toString() {
        return "BankCard{" +
               "number='" + number + '\'' +
               ", balance=" + balance +
               ", isBlocked=" + isBlocked +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankCard bankCard = (BankCard) o;
        return Objects.equals(number, bankCard.number);

    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
