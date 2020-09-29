# item 90

> 직렬화된 인스턴스 대신 직렬화 프록시 사용을 검토하라

Serializable을 구현하기로 결정한 순간 언어의 정상 메커니즘인 생성자 이외의 방법으로 인스턴스를 생성할 수 있게 된다. **버그와 보안 문제가 일어날 가능성이 커진다**는 뜻이다. 하지만 이 위험을 크게 줄여줄 기법이 하나 있다. 바로 **직렬화 프록시 패턴**이다. 

# 직렬화 프록시 패턴

- 바깥 클래스의 논리적 상태를 정밀하게 표현하는 중첩 클래스를 설계해 private static 으로 선언한다. → 이 중첩 클래스가 바로 바깥 클래스의 직렬화 프록시다.
- 중첩 클래스의 생성자는 단 하나여야 하며, 바깥 클래스를 매개변수로 받아야 한다. → 이 생성자는 단순히 인수로 넘어온 인스턴스의 데이터를 복사한다.
- 일관성 검사나 방어적 복사가 필요 없으며 설계상, 직렬화 프록시의 기본 직렬화 형태는 바깥 클래스의 직렬화 형태로 쓰기에 이상적이다. (?)
- 바깥 클래스와 직렬화 프록시 모두 Serializable을 구현한다고 선언해야 한다.

## Period 클래스를 이용해 직렬화 프록시 패턴을 구현해보자.

- Period 클래스를 위한 직렬화 프록시 class SerializationProxy

```java
private static class SerializationProxy implements Serializable {
	private final Date start;
	private final Date end;

	SerializationProxy(Period p) {
		this.start = p.start;
		this.end = p.end;
	}

	private static final long serialVersionID = 24398393023209203L; // 아무 숫자나 다 된다
}
```

- 직렬화 프록시를 사용하는 모든 클래스에 범용적으로 써줘야 하는 메서드 writeReplace를 바깥 클래스에 추가

```java
private Object writeReplace() {
	return new SerializationProxy(this);
}
```

→ writeReplace는 **자바의 직렬화 시스템이 바깥 클래스의 인스턴스 대신 SerializationProxy의 인스턴스를 반환하게 하는 역할을 한다**. (직렬화가 이뤄지기 전에 바깥 클래스의 인스턴스를 직렬화 프록시로 변환해준다) 
→ 이 메소드 덕분에 **직렬화 시스템은 결코 바깥 클래스의 직렬화된 인스턴스를 생성해낼 수 없지**만 불변식을 훼손하고바깥 클래스의 직렬화된 인스턴스를 생성하고자 하는 시도에는 어떻게 해야 하는가?

- 불변식을 훼손하고자 하는 시도를 막기 위해 메서드 readObject를 바깥 클래스에 추가

```java
private void readObject(ObjectInputStream stream) {
	throws InvalidObjectException {
		throw new InvalidObjectException("프록시가 필요합니다.");
}
```

- 마지막으로, 바깥 클래스와 **논리적으로 동일한 인스턴스를 반환**하는 메서드 readResolve를 추가한다.
→  이 메서드는 역직렬화 시에 직렬화 시스템이 직렬화 프록시를 다시 바깥 클래스의 인스턴스로 변환하게 해준다. 
→ 공개된 API만을 사용해 바깥 클래스의 인스턴스를 생성한다. 
→ 직렬화는 생성자를 이용하지 않고도 인스턴스를 생성하는 기능을 제공하는데, 이 패넡은 직렬화의 이런 규칙에 어긋난 특성을 상당 부분 제거한다. 
→즉, 일반 인스턴스를 만들 때와 똑같은 생성자, 정적 팩터리, 혹은 다른 메서드를 사용해서 역직렬화된 인스턴스 를 생성한다.

> "역직렬화된 인스턴스가 해당 클래스의 불변식을 만족하는지 검사할 또 다른 수단을 강구하지 않아도 된다. 그 클래스의 정적 팩터리나 생성자가 불변식을 확인해주고 인스턴스 메서드들이 불변식을 잘 지켜준다면, 더 해줘야 할 일은 없다."

```java
private Object readResolve() {
	return new Period(start, end);
}
```

방어적 복사처럼, 직렬화 프록시 패턴은 가짜 바이트 스트림 공격과 내부 필드 탈취 공격을 프록시 수준에서 차단해준다. 또한 직렬화 프록시는 예시의 Period 필드를 final로 선언해도 되므로 Period 클래스를 진정한 불변으로 만들 수도 있다. 

---

**"프록시 패턴을 사용하면 이리저리 고민할 필요가 거의 없다! 어떤 필드가 기만적인 직렬화 공격의 목표가 될지 고민하지 않아도 되며, 역직렬화 때 유효성 검사를 수행하지 않아도 된다."**

## 직렬화 프록시 패턴은 역직렬화한 인스턴스와 원래의 직렬화된 인스턴스의 클래스가 달라도 정상 작동한다.

이게 무슨 용도일까? 

> EnumSet은 public 생성자 없이 정적 팩터리들만 제공한다. 클라이언트의 입장에서는 똑같지만 OpenJDK를 보면 열거 타입의 원소가 64갸 이하면 RegularEnumSet을 사용하고, 그보다 크면 JumboEnumSet를 사용한다.

1. 원소가 64개짜리 열거 타입을 가진 EnumSet을 직렬화 한다. 
2. 원소 5개를 추가하고 (원소가 64개 이상이 됨) 역직렬화를 해보자. 
3. 처름 직렬화 된 것은 RegularEnumSet이지만, 역직렬화는 64개 이상인 JumboEnumSet 인스턴스로 하면 좋을 것이다. 
4. 이는 실제로 EnumSet 직렬화에 사용되고 있는 패턴이다.

```java
private static class SerializationProxy <E extends Enum<E>> implements Serializable {
	// EnumSet 원소 타입
	private final Class<E> elementType;
	
	// EnumSet 안의 원소들 
	private final Enum<?>[] elements;

	SerializationProxy(EnumSet<E> set) {
		elementType = set.elementType;
		elements = set.toArray(new Enum<?>[0]);
	}
	
	private Object readResolve() {
		EnumSet<E> result = EnumSet.noneOf(elementType);
		for (Enum<?> e : elements) 
			result.add((E)e);
		return result;
	}

	private static final long serialVersionID = 928891839302L;
}
```

## 프록시 패턴의 한계

1. 클라이언트가 멋대로 확장할 수 있는 클래스에는 적용할 수 없다. 
2. 객체 그래프에 순환이 있는 클래스에도 적용할 수 없다. (직렬화 프록시만 가졌지 아직 객체가 만들어지지 않아서 readReolve 안에서 호출하면 ClassCastException이 발생한다) 
3. 직렬화 프록시 패턴은 아주 강력한 안전성을 제공하지만 방어적 복사 때보다 속도가 느리다. (환경에 따라 얼마나 느린지는 다름)

## 핵심정리

 제 3자가 확장할 수 없는 클래스라면 가능한 한 직렬화 프록시 패턴을 사용하자. 이 패턴이 아마도 중요한 불변식을 안정적으로 직렬화해주는 가장 쉬운 방법일 것이다. 

- [ ]  아예 직접 다 구현해보기
- [ ]  직렬화 하는 김에 메세징도 해서 직렬화형태로 보내면 얼마나 좋을까...
- [ ]  예시 코드 88-5, 88-4 뭔지 찾아보기
- [ ]  아이템 17도 보기 진정한 불변식이 뭐지...
- [ ]  아이템 19 대띠용 클라이언트가 멋대로 확장할 수 있는 클래스라니??
- [ ]  객체 그래프에 순환이 있는 클래스는 또 뭐야