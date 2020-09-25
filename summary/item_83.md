# item 83

> 지연 초기화는 신중히 사용하라

- 지연초기화는 필드의 초기화 시점을 그 값이 처음 필요할 때까지 늦추는 기법
- 값이 전혀 쓰이지 않으면 초기화도 결코 일어나지 않음
- 이 기법은 정적 필드와 인스턴스 필드 모두에 사용 가능
- 주로 최적화 용도로 사용하지만, 클래스와 인스턴스 초기화 때 발생하는 위험한 순환 문제를 해결할 수 있음

lazy initalization /lazy loading /lazy evaluation

## 지연 초기화를 위한 최선의 조언 "필요할 때까지는 하지 말라"

지연 초기화는 양날의 검이다. 클래스 혹은 인스턴스 생성 시의 초기화 비용은 줄지만 그 대신 지연 초기화하는 필드에 접근하는 비용은 커진다. 

→ 필드들 중 초기화가 이뤄지는 비율에 따라 
→ 실제 초기화에 드는 비용에 따라 
→ 초기화된 각 필드를 얼마나 빈번히 호출하나
에 따라 지연초기화가 실제로는 성능을 느려지게 할 수도 있다. 

**클래스의 인스턴스 중 그 필드를 사용하는 인스턴스의 비율이 낮은 반면, 그 필드를 초기화하는 비용이 크다면 지연 초기화가 제 역할을 해줄 것이다**. (지연초기화가 효과가 있는지 알아보기 위해서는 적용 전후의 성능을 측정하는 길 뿐이다)

## 멀티스레드 환경에서의 지연 초기화

멀티 스레드 환경에서의 지연 초기화는 까다롭다. 지연 초기화하는 필드를 둘 이상의 스레드가 공유한다면 어떤 형태고든 반드시 동기화 해야 한다. 이를 어길시 심각한 버그를 초래한다. **대부분의 상황에서 일반적인 초기화가 지연 초기화 보다 낫다**는 것을 기억하고 예제를 보도록 하자. 

일반적인 인스턴스 필드를 선언할 때 초기화를 하는 예제

```java
private final FieldType field = computeFieldValue();
```

지연초기화로 인해 초기화 순환성(initalization circularity)가 깨질 것 같으면 **synchronized** 를 사용하자. (초기화 순환성이 깨진 예: class A 의 생성자는 class B 인스턴스가 필요하고 class B 생성자는 class C 인스턴스가 필요하고  class C 생성자는  class A 인스턴스가 필요하다)

```java
private FieldType field;

private synchronized FieldType getField() {
	if (field == null)
		field = computeFieldValue();
	return field;
}
```

정적필드도 필드와 메서드 선언에 static 을 선언하면 똑같이 일반 초기화와 sunchronized를 사용한 초기화를 사용할 수 있지만 성능을 고려한 더 세련된 방법이 있다. 

## 정적필드 지연초기화에는 지연 초기화 홀더 관용구를 사용하자.

```java
private static class FieldHolder {
	static final FieldType field = computeFieldValue();
}

private static FieldType getField() { return FieldHolder.field; }
```

- getField가 처음 호출되는 순간 FieldHolder.field가 처음 읽히면서 FieldHolder 클래스 초기화를 촉발한다.
- 멋진 점: getField 메서드가 필드에 접근하면서 동기화를 전혀 하지 않으니 성능이 느려질 거리가 전혀 없다!
- 일반적인 VM은 오직 클래스를 초기화할 때만 필드 접근을 동기화 한다 → 초기화가 끝난 후에는 VM이 동기화 코드를 제거한다 → 이후 아무런 검사나 동기화 없이 필드에 접근하게 된다.

## 성능 때문에 인스턴스 필드를 지연 초기화해야 한다면 이중검사 관용구를 사용하라

이 관용구는 초기화된 필드에 접근할 때의 동기화 비용을 없애준다. 필드의 값을 두 번 검사하는 방식으로, 한 번은 동기화 없이 검사하고 (필드가 아직 초기화 되지 않았다면) 두 번째는 동기화하여 검사한다. 두 번째 검사에서도 필드가 초기화되지 않았을 때만 필드를 초기화한다. 

→ **필드가 초기화된 후로는 동기화하지 않으므로 해당 필드는 반드시 volatile로 선언해야 한다.** 

```java
private volatile FieldType field;

private FieldType getField() {
	FieldType result = field;
	if (result != null )  // 첫 번째 검사 (락 X)
		return result;

	synchronized(this) {
		if (field == null) // 두 번째 검사 (락 O)
			field = computeFieldValue();
		return field;
	}
}
```

- **result 지역변수가 필요한 이유는 무엇인가?** 
이 변수가 이미 초기화된 일반적인 상황에서 그 필드를 딱 한 번만 읽도록 보장하는 역할을 한다. 
반드시 필요하지는 않지만 성능을 높여주는 우아한 방법이다.
- 이중검사 관용구를 정적 필드에도 적용할 수 있으나 지연 초기화 홀더 관용구가 더 낫다.
- 변종: 때때로 **반복해서 초기화해도 상관없는 인스턴스 필드를 지연 초기화해야 한다면 두 번째 검사를 생략할 수 있다**. (단일 검사 관용구)

## 단일 검사 관용구

```java
private volatile FieldType field;

private FieldType getField() {
	FieldType result = field;
	if (result == null) 
		field = result = computeFieldValue();
	return result;
}

```

- 이번 아이템에서 이야기한 모든 초기화 기법은 기본 타입 필드와 객체 참조 필드 모두에 적용할 수 있다. 이중검사와 단일검사 관용구를 수치 기본 타입 필드에 적용한다면 필드의 값을 null 대신 (숫자 기본 타입 변수의 기본값인) 0과 비교하면 된다.
- 모든 스레드가 필드의 값을 다시 계산해도 상관없고 필드의 타입이 long과 double을 제외한 다른 기본 타입이라면 단일검사의 필드 선언에서 volatile을 없애도 된다. (racy single-check)

    (long 과 double은 꼭 volatile을 써야 하는 이유:  한번에 32bits씩 읽고 쓰는데 long과 double은 8byte 라서 32 bits는 한 쓰레드에, 나머지 32bits 는 다른 쓰레드에 써질 수 있다. (word taering)

- racy single-check 은 어떤 환경에서는 필드 접근 속도를 높여주지만, 초기화가 스레드당 최대 한 번 더 이뤄질 수 있는 아주 이례적인 기법으로, 잘 쓰지 않는다.

## 핵심 정리

1. **대부분의 필드는 지연시키지 말고 곧바로 초기화해야 한다.** 
2. 성능 때문에 지연초기화를 꼭 써야 한다면 올바른 지연 초기화 기법을 사용하자. 
3. 인스턴스 필드에는 이중검사 관용구를 사용하자. 
4. 정적 필드에는 지연 초기화 홀더 클래스 관용구를 사용하자. 
5. 반복해서 초기화해도 괜찮은 인스턴스 필드에는 단일 검사 관용구 사용을 고려해보자.