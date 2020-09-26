package item55;

import item64.IBank;
import item64.WoriBank;
import sun.java2d.loops.ProcessPath;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.function.Supplier;

/**
 * @author SeungminLee
 * project effective_java
 * date 2020-08-25
 * description
 */
public class MainItem55 {

    /*
    지금 보니 result 의 null 체크를 하는 코드였다. Objects 는 Util 코드들을 모아두었는데 requireNonNull 은 Null 이면 npe 를 발생, 아니면 그대로 값 반환
     */
    /*
    Optional 을 사용하지 않고 에외를 던져준다
     */
    public static <E extends Comparable<E>> E max(Collection<E> c) {
        if (c.isEmpty())
            throw new IllegalArgumentException("빈 컬렉션");

        E result = null;
        for (E e: c) {
            if(result == null || e.compareTo(result) > 0)
                result = Objects.requireNonNull(e);

        }
        return result;
    }

    /*
    Optional 을 사용
     */
    public static <E extends Comparable<E>> Optional<E> maxOptional(Collection<E> c) {
        if (c.isEmpty())
            return Optional.empty();

        E result = null;
        for (E e: c) {
            if(result == null || e.compareTo(result) > 0)
                result = Objects.requireNonNull(e);

        }
        return Optional.of(result);
    }

    /*
    Optional + stream 사용
     */
    public static <E extends Comparable<E>> Optional<E> maxStream(Collection<E> c) {
        return c.stream().max(Comparator.naturalOrder());
    }

    public static void main(String[] args) {
        Set<Integer> number1 = new HashSet<Integer>() {{
            add(1);
            add(1000);
            add(10);
        }};
        System.out.println(max(number1));

/*        IllegalArgumentException
        List<Integer> empty = new ArrayList<>();
        max(empty); */

        List<Double> number2 =  Arrays.asList(32.43,0.1,2.01,3.32);
        System.out.println(maxOptional(number2));


/*        // Empty Optional
        System.out.println(Optional.empty());*/

        System.out.println(maxStream(number1));
        System.out.println(maxStream(number2));

        List<Integer> empty = new ArrayList<>();

        // Optional 에서 값을 안 줄 때 방어코드 1.orElse 를 사용
        int result = maxOptional(empty).orElse(-1);
        System.out.println(result);

        // 방어코드 2.orElseThrow 호출한 메소드에서 예외가 발생하면 그 예외 종류와 상관없이 호출에서 걸어둔 orElseThrow 안의 예외가 발생
        int result2 = maxOptional(number1).orElseThrow(IllegalArgumentException::new);
//        int result3 = maxOptional(empty).orElseThrow(NullPointerException::new);
        System.out.println(result2);

        // Optional 을 unwrap 하는데 항상 값이 있을거라고 생각해서 get 해버리기
        int result4 = maxOptional(number1).get();
        System.out.println("여기"+ result4);


        // Optional 을 안전하게 unwrap 하는 방법.
        Optional<Integer> resultNumber1 = maxOptional(number1);

        Supplier<Integer> defaultSupplier = () -> -1;
        int unWrap = resultNumber1.orElseGet(defaultSupplier);
        System.out.println(resultNumber1);
        System.out.println(unWrap);

        OptionalInt i = OptionalInt.of(1);
        System.out.println(i);


        List<String> list = Arrays.asList("Mike", "Nicki", "John");
        String s = list.stream().collect(StringBuilder::new,
                (sb, s1) -> sb.append(" ").append(s1),
                (sb1, sb2) -> sb1.append(sb2.toString())).toString();
        System.out.println(s);
    }
}
