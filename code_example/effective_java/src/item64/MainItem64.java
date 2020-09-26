package item64;

import com.sun.xml.internal.ws.developer.Serialization;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * @author SeungminLee
 * project effective_java
 * date 2020-05-13
 * description: main page for itel64 객체는 인터페이스를 사용해 참조하라
 */
public class MainItem64  implements Serializable {

    public static void main(String[] args) {


        // 인터페이스 타입 사용 해서 변경
        // IBank myBank = ShinhanBank.newInstance("이승민");
        // myBank.openAccount();

        IBank myBank = WoriBank.newInstance(123);
        myBank.openAccount();
        // 클래스 타입 사용 해서 변경
        // ShinhanBank myBank2 = ShinhanBank.newInstance("이승민2");
        // myBank2.openAccount();
        // myBank2.printUserName();

        WoriBank myBank2 = WoriBank.newInstance(1234);
        myBank2.openAccount();
        myBank2.printUserId();

    }
}
