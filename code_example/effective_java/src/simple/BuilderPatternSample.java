package simple;

/**
 * @author SeungminLee
 * project effective_java
 * date 2020-09-26
 * description
 */
public class BuilderPatternSample {
    // final 을 쓴 이유는 사용자들에게 생성자 사용을 강요하기 위해
    // final 필드는 초기화 이후에는 값이 바뀌지 않으니까
    private final int age;
    private final String name;

    private final String country;
    private final boolean isMarried;

   // inner builder class 를 만들어준다
    public static class Builder {
       private final int age;
       private final String name;

       // 선택 필드들은 일단 임의로 값을 할당해준다.
       private String country = "";
       private boolean isMarried = false;

       public Builder(int age, String name) {
           this.age = age;
           this.name = name;
       }

       // 선택 필드를 받아주는 메소드들. 이렇게 사용하면 나중에 생성자 체인을 할 수 있다.
       public Builder country(String val) {
           country = val;
           return this;
       }

       public Builder isMarried(boolean val) {
           isMarried = val;
           return this;
       }

       public BuilderPatternSample build() {
           return new BuilderPatternSample(this);
       }
   }

   private BuilderPatternSample(Builder builder) {
        age = builder.age;
        name = builder.name;
        country = builder.country;
        isMarried = builder.isMarried;
   }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public boolean isMarried() {
        return isMarried;
    }

    public String toStringValue() {
        return "BuilderPatternSample: "
                + " name: " + name
                + " age: " + age
                + " country: " + country
                + " isMarried: "+ isMarried;
   }
}
