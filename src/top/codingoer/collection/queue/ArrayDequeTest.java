package top.codingoer.collection.queue;

import java.util.ArrayDeque;

public class ArrayDequeTest {

    public static void main(String[] args) {
        Person p1 = new Person("小明", 25);
        Person p2 = new Person("小红", 25);
        Person p3 = new Person("小绿", 25);
        Person p4 = new Person("小紫", 25);

        ArrayDeque<Person> personArrayDeque = new ArrayDeque<>(4);
        personArrayDeque.add(p1);
        personArrayDeque.add(p2);
        personArrayDeque.add(p3);
        personArrayDeque.add(p4);

        while (!personArrayDeque.isEmpty()) {
            Person firstElement = personArrayDeque.removeFirst();
            System.out.println("first " + firstElement.getName());

            for (Person person : personArrayDeque) {
                System.out.println(person.getName());
            }
        }
    }
}
