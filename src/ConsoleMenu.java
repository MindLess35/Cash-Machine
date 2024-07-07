import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConsoleMenu {
    private CashMachine cashMachine;
    private static final String MAIN_MENU = """
            \nГлавное меню
            1. Проверить баланс
            2. Снять деньги
            3. Пополнить баланс
            4. Выйти
            5. Выключить банкомат
            Введите цифру, соответствующую желаемому действию:\s""";
    private static final String BACK_OPTION = "\nВведите \"0\", чтобы вернуться в главное меню: ";


    public ConsoleMenu(CashMachine cashMachine) {
        this.cashMachine = cashMachine;
    }

    public void initCashMachine() {
        try (Scanner scanner = new Scanner(System.in)) {
            String cardNumber = readCardNumber(scanner);
            isCardExistsInBank(cardNumber);
            cashMachine.checkCardBlockingStatus();
            checkCardStatus();
            checkCorrectPinCode(scanner);
            displayMainMenu();
        } catch (InterruptedException e) {
            // это исключение в этой программе никак выпасть не может.
        }
    }

    private void checkCardStatus() {

        if (cashMachine.isCurrentCardBlocked()) {
            System.out.println("Извините, ваша карта заблокирована.");
            initCashMachine();
        }

    }

    private void isCardExistsInBank(String cardNumber) throws InterruptedException {
        try {
            cashMachine.isCardExistsInBank(cardNumber);
        } catch (NoSuchElementException ex) {
            System.out.println("""
                    К сожалению, карты с таким номером в нашем банке не зарегистрированно.
                    Убедитесь в правильности введённого номера. Если вы всё ещё не можете войти в систему,
                    то напишите на почту в нашу техническую поддержку - liroy2468@gmail.com\n
                    """);
            Thread.sleep(2000);
            initCashMachine();
        }

    }

    private void checkCorrectPinCode(Scanner scanner) {
        int countOfFailedAttempts = 0;
        while (countOfFailedAttempts < CashMachine.MAXIMUM_COUNT_OF_FAILED_ATTEMPTS_ENTER_PIN_CODE) {
            System.out.print("Введите ПИН-код: ");
            int pinCode = readIntInput();

            if (cashMachine.isPinCodeCorrect(pinCode)) {
                return;
            } else {
                System.out.println("Введённый ПИН-код не правильный, попробуйте ещё раз.");
                countOfFailedAttempts++;
            }
        }
        cashMachine.blockCurrentCard();
        System.out.println("Извините, ваша карта заблокирована.");
        initCashMachine();
    }

    private String readCardNumber(Scanner scanner) throws InterruptedException {
        String cardNumber;
        while (true) {
            System.out.print("Введите номер вашей банковской карты в формате ХХХХ-ХХХХ-ХХХХ-ХХХХ. " +
                             "К примеру: 1111-2222-3333-4444.\nНомер вашей карты: ");
            cardNumber = scanner.nextLine();

            if (cashMachine.isCardNumberFormatValid(cardNumber)) {
                System.out.print("\nПожалуйста, подождите. Выполняется запрос в банк...\n\n");
                Thread.sleep(1000);
                return cardNumber;
            } else {
                System.out.print("\nВведённый вами номер карты не соответствует необходимому формату или содержит " +
                                 "недопустиые символы. Пожалуйста, попробуйте снова.\n\n");
            }
        }
    }

    private void displayMainMenu() {
        displayMenu(MAIN_MENU);
        handleMenuInput();
    }

    private void displayMenu(String menu) {
        System.out.print(menu);
    }

    private void handleMenuInput() {
        int choice = readIntInput();
        long amount;
        BigDecimal currentCardBalance = cashMachine.getCurrentCardBalance();
        BigDecimal resultAmount;
        switch (choice) {
            case 1:
                System.out.printf("\nВаш текущий баланс: %,.2f рублей%n", currentCardBalance);
                displayMenu(BACK_OPTION);
                handleMenuInput();
                break;
            case 2:
                System.out.print("\nВведите сумму, которую вы хотите снять со счёта: ");
                amount = readIntInput();
                resultAmount = BigDecimal.valueOf(amount);

                if (cashMachine.withdrawMoney(resultAmount)) {
                    System.out.printf("%d рублей успешно сняты со счёта.", amount);
                    System.out.printf("\nВаш текущий баланс: %,.2f рублей.%n", cashMachine.getCurrentCardBalance());

                } else if (currentCardBalance.compareTo(resultAmount) < 0) {
                    System.out.print("\nНа вашем балансе не хватает средств.");
                    System.out.printf("\nВаш текущий баланс: %,.2f рублей.%n", currentCardBalance);

                } else
                    System.out.println("Извините, на данный момент в банкомате не хватает средств.");

                displayMenu(BACK_OPTION);
                handleMenuInput();
                break;
            case 3:
                System.out.print("\nВведите сумму, которую вы хотите положить на счёт: ");
                amount = readIntInput();
                resultAmount = BigDecimal.valueOf(amount);
                if (cashMachine.depositMoney(resultAmount)) {
                    System.out.printf("Баланс вашего счёта успешно пополнен на %d рублей.", amount);
                    System.out.printf("\nВаш текущий баланс: %,.2f рублей.%n", cashMachine.getCurrentCardBalance());
                } else
                    System.out.printf("Пополнять баланс счёта можно не более чем на %,.0f рублей за раз.%n",
                            CashMachine.MAXIMUM_AMOUNT_OF_REPLENISHMENT_BALANCE);

                displayMenu(BACK_OPTION);
                handleMenuInput();
                break;
            case 4:
                cashMachine.updateDataInBank();
                initCashMachine();
                handleMenuInput();
                break;
            case 5:
                cashMachine.updateDataInBank();
                System.out.println("Выход из приложения...");
                break;
            case 0:
                displayMainMenu();
                break;
            default:
                System.out.println("Неверный выбор. Попробуйте еще раз.");
                handleMenuInput();
        }
    }

    private int readIntInput() {
        Scanner scanner = new Scanner(System.in);
        int result;

        while (true) {
            try {
                result = scanner.nextInt();
                scanner.nextLine();
                return result;
            } catch (InputMismatchException ex) {
                System.out.println("Введённый вами результат не является положительным числом, или содержит " +
                                   "недопустимые символы. Пожалуйста, попробуйте снова.");
                scanner.nextLine();
            }
        }
    }

}
