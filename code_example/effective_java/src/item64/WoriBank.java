package item64;

/**
 * @author SeungminLee
 * project effective_java
 * date 2020-05-13
 * description
 */
public class WoriBank implements IBank{

    private static WoriBank woriBank;
    private int userId;

    private WoriBank(int userId) {
        this.userId = userId;
    }

    public static WoriBank newInstance (int userId) {
        woriBank = new WoriBank(userId);
        return woriBank;
    }

    @Override
    public void openAccount() {
        System.out.println("우리은행에서 계좌를 개설하셨습니다.");
    }

    @Override
    public void closeAccount() {
        System.out.println("우리은행에서 계좌를 닫으셨습니다.");
    }

    public void printUserId() {
        System.out.println(this.userId);
    }
}
