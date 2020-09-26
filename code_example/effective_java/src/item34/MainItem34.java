package item34;

/**
 * @author SeungminLee
 * project effective_java
 * date 2020-08-13
 * description
 */
public class MainItem34 {

    enum InnerEnum {
        ONE, TWO, THREE, FOUR
    };

    public static void main(String [] args) {
        for(InnerEnum i : InnerEnum.values()) {
            System.out.println(InnerEnum.class);
        }

    }
}
