package item74;

import java.util.HashMap;
import java.util.Map;

/**
 * JavadocTest class is made to write Javadoc. Sample code:
 * <pre>
 *      JavadocTest test = new JavadocTest();
 *      test.setFirstField("first field");
 *      test.getFirstField();
 * </pre>
 * @author SeungminLee
 * @version 0.0.1
 * @since 2020-05-22
 */
public class JavadocTest {

    /**
     * First field in JavadocTest
     *
     * @see #getFirstField()
     * @see #setFirstField(String)
     */
    private String firstField;

    /**
     * Second field in JavadocTest
     *
     * @see #getSecondField()
     * @see #setSecondField(int)
     */
    private int secondField;


    /**
     * @return FirstField, SecondField in Map
     * @throws IllegalAccessError if other class try to call this method
     */
    private Map<String, Object> getBothField() throws NullPointerException {
        Map<String, Object> map = new HashMap<>();
        map.put("firstField", this.firstField);
        map.put("secondField", this.secondField);

        return map;
    }

    /**
     * Get firstField in JavadocTest class
     *
     * @return firstField
     */
    public String getFirstField() {
        return firstField;
    }

    /**
     * Set firstField in JavadocTest class
     *
     * @param firstField firstField of JavadocTest
     * @throws NullPointerException if parameter is null or empty
     */
    public void setFirstField(String firstField) {
        this.firstField = firstField;
    }

    /**
     *
     * @return secondField
     * @throws NullPointerException if parameter is null or empty 비검사예외
     * @throws IllegalAccessError if someone access 검사예외
     */
    public int getSecondField() throws IllegalAccessError {
        return secondField;
    }

    public void setSecondField(int secondField) {
        this.secondField = secondField;
    }


}
