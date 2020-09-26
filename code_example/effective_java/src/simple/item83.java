package simple;

import java.util.concurrent.*;

/**
 * @author SeungminLee
 * project effective_java
 * date 2020-09-26
 * description
 */
public class item83 {
    // 이 패턴이 싱글톤 객체 만들때 가장 보편적으로 쓰는 생성 방법인것도 공부하다 보니까 알았네...
    private static class FieldHolder {
        static final BuilderPatternSample sampleClass = new BuilderPatternSample
                .Builder(26, "Jamie Lee")
                .country("Korea")
                .isMarried(false)
                .build();
    }

    private static BuilderPatternSample getField() {
        return FieldHolder.sampleClass;
    }

    public static void main(String[] args) {

        // 싱글톤 방식이라서 모두 똑같은 객체이다.
        BuilderPatternSample singleTon1 = getField();
        BuilderPatternSample singleTon2 = getField();

        System.out.println(singleTon1);
        System.out.println(singleTon2);

//        concurrent 예제 참고. 여기서 다 안 다뤘다. 쓰레드 동작 필요할 떄 봐보자.
//        https://winterbe.com/posts/2015/04/07/java8-concurrency-tutorial-thread-executor-examples/
        //자바의 오래된 동기화 방식인 Threads, Runnables, Callcables 를 새롭게 커버해주는 패키지 import java.util.concurrent

        // Runnable cover
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            BuilderPatternSample simple = getField();
            System.out.println(simple.toString());
        });

        try {
          System.out.println("attempt to shutdown executor");
          executor.shutdown();
          executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("tasks interrupted");
            System.out.println(e.getMessage());
        } finally {
            if (!executor.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }
            executor.shutdown();
            System.out.println("shutdown finished");
        }

        // Callable cover
        Callable<BuilderPatternSample> task = () -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                return getField();
            } catch (InterruptedException e) {
                throw new IllegalStateException("task interrupted", e);
            }
        };

        ExecutorService excutor2 = Executors.newFixedThreadPool(1);

        // callable의 비동기적 특성 상 실행을 한다고 해도 바로 값을 사용할 수 없다.
        // 대신 executor 에서 특별히게 Future 이라는 객체를 사용하게 해줬다. 이건 이후에 값이 들어오면 꺼내서 쓸 수 있다.
        Future<BuilderPatternSample> future = excutor2.submit(task);

        System.out.println("future down? " + future.isDone());

        try {
            BuilderPatternSample result = future.get();
            System.out.println("future done? " + future.isDone());
            System.out.println("result: " + result.toStringValue());
            excutor2.shutdown();
            excutor2.awaitTermination(5, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException e) {
            System.out.println("Exception happened when getting value");
            e.printStackTrace();
        } finally {
            if (!excutor2.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }
            excutor2.shutdown();
            System.out.println("shutdown finished");
        }
    }

}
