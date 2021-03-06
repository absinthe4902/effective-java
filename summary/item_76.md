# item 76

> 가능한 한 실패 원자적으로 만들라

Database transaction 
A: Atomicity (원자성)
C: Consistency (일관성)
I: Isolation (독립성)
D: Durability (지속성) 

실패를 원자적으로 만들면 작업 도중 예외가 발생해도 그 객체는 여전히 정상적으로 사용할 수 있는 상태가 된다. 검사 예외를 던지는 상황이라면 호출자가 오류 상태를 복구할 수 있으니 더 유용하다. 

## 호출된 메서드가 실패하더라도 해당 객체는 메서드 호출 전 상태를 유지해야 한다. (failure-atomic)

메서드를 실패 원자적으로 만드는 방법

1. 불편 객체로 설계한다. 
**불편 객체는 태생적으로 실패 원자적이다**. 메서드가 실패하면 새로운 객체가 만들어지지 않는 일이 있어도 기존에 있던 객체가 불안정한 상태에 빠지지 않는다. (item 17)

    ```java
    public final Class Complex {
    	private final double re;
    	private final double im;

    public Complex(double re, double im) {
    	this.re = re;
    	this.im = im;
    }

    public Complex plus (Complex c) {
    	return new Complex(re + c.re, im + c.im);
    }

    ...
    ```

2. 작업 수행 전, 매개변수의 유효성 검사를 한다. 
객체 내부 상태를 변경하기 전에 잠재적 예외 가능성을 대부분 걸러내면서도 **가장 쉬운 구현법**이다.

    ```java
    public Object pop() {
    	if (size == 0) 
    			throws new EmptyStackException();
    	Object result = elements[--size];
    	elements[size] null; // 사용을 마친 참조 해제
    	return result;

    // size를 검사하지 않아도 스택이 비어있다면 예외를 던지나, 이는 ArrayIndexOutOfBoundsException이다.
    // 우리가 의도하는 스택이 비어있어서 발생하는 예외와는 다소 맞지 않는다. (추상화 수준이 상황에 어울리지 않는다. item 73) 
    ```

    실패할 가능성이 있는 모든 코르를 객체의 상태를 바꾸는 코드보다 앞에 배치한다. 계산을 수행해보기 전에는 인수의 유효성을 검사해볼 수 없을 때 사용하기 좋은 방법이다. 

     예시: TreeMap 
    1. TreeMap은 원소들을 정해진 기준으로 정렬한다. 
    2. TreeMap에 원소를 추가하려면 그 원소는 TreeMap의 기준에 따라 비교할 수 있는 타입이어야 한다. 
    3. 만약 엉뚱한 타입을 추가하려면 트리에 추가되기 전에 추가될 위치를 찾는 과정에서 ClassCastException이 발생한다. 

3. 객체의 임시 복사본에서 작업을 수행한 다음, 작업이 성공적으로 완료되면 원래의 객체와 교체한다. **데이터를 임시 자료구조에 저장해 작업하는 게 더 빠를 때 적용하기 좋은 방식이다.**
정렬 메서드에서 정렬을 수행하지 전에 입력 리스트의 원소들을 배열로 옮겨 담는다 → 배열을 사용하면 정렬 알고리즘의 반복문에서 원소들에 훨씬 빠르게 접근할 수 있다. → 성능을 위한 구현이지만 혹시 정렬에 실패하더라도 입력 라스트는 변하지 않는다.
4. 작업 도중 발생하는 실패를 가로채는 복구 코드를 작성하여 작업 전 상태로 돌린다. 
주로 디스크 지속성을 보장해야하는 자료구조에서 자주 쓰이는데 자주 쓰이는 방식이다. (db의 지속성을 보장하기 위한 transcation에서는 빈번하게 쓰인다).

## 실패 원자성은 일반적으로 권장되는 덕목이지만 항상 달성할 수 있는 것은 아니다.

예를 들어 두 스레드가 동기화 없이 같은 객체를 동시에 수정한다면 그 객체의 일관성이 깨질 수 있다. 이를 위해 ConcurrentModificationException을 잡아내지만 그 후에 그 객체가 여전히 쓸 수 있는 상태라고 생각해서는 안된다. 

한편, Error는 복구할 수 없으므로 실패원자적으로 만들려는 시도조차 할 필요가 없다. 

**실패 원자적으로 만들 수 있더라도 항상 그리 해야 하는 것도 아니다**. 실패 원자성을 달성하기 위한 비용이나 복잡도가 아주 큰 연산도 있기 때문이다. 

→ 그래도 문제가 무엇인지 알고 나면 실패 원자성을 공으로 얻을 수 있는 경우가 많다! 

## 핵심정리

메서드 명세에 기술한 예외라면 설혹 예외가 발생하더라도 객체의 상태는 메서드 호출 전과 똑같이 유지돼야 한다는 것이 기본 규칙이다. 이 규칙을 지키지 못한다면 실패 시의 객체 상태를 API 설명에 명시해야 한다. (API 문서 상당 부분이 잘 지키지 않고 있다).