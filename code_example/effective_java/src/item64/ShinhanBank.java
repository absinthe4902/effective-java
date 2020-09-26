package item64;

/**
 * @author SeungminLee
 * project effective_java
 * date 2020-05-13
 * description
 */

public class ShinhanBank implements IBank {

    private static ShinhanBank shinhanBank;
    private String userName;

    private ShinhanBank(String userName) {
        this.userName = userName;
    }

    public static ShinhanBank newInstance(String userName) {
        shinhanBank = new ShinhanBank(userName);
        return shinhanBank;
    }

    @Override
    public void openAccount() {
        System.out.println("신한은행에서 계좌를 개설하셨습니다.");
    }

    @Override
    public void closeAccount() {
        System.out.println("신한은행에서 계좌를 닫으셨습니다.");
    }


    public void printUserName() {
        System.out.println(this.userName);
    }
}
