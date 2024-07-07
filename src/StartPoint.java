import java.nio.file.Path;

public class StartPoint {
    public static void main(String[] args) {
        DataStorage dataStorage = new DataStorage(Path.of("cardsData.txt"));
        if (!dataStorage.loadCardsData())
            System.exit(0); // не нашёлся текстовый файл или ещё что с ним
        CashMachine cashMachine = new CashMachine(dataStorage);
        ConsoleMenu consoleMenu = new ConsoleMenu(cashMachine);
        consoleMenu.initCashMachine();
    }
}