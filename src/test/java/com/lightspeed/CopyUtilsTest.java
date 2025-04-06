package com.lightspeed;

import java.util.*;

public class CopyUtilsTest {

    public static void main(String[] args) {

        // 1. String (immutable)
        String s = "hello";
        Object copiedS = CopyUtils.deepCopy(s);
        assertSame(s, copiedS, "String reference should remain the same");

        // 2. Array
        String[] arr = {"a", "b", "c"};
        String[] copiedArr = CopyUtils.deepCopy(arr);
        assertNotSame(arr, copiedArr, "Array should be a new instance");
        assertEquals(arr[0], copiedArr[0], "Array elements should match");

        // 3. ArrayList
        List<String> list = new ArrayList<>(List.of("one", "two"));
        List<?> copiedList = CopyUtils.deepCopy(list);
        assertNotSame(list, copiedList, "List should be a new object");
        assertEquals(list, copiedList, "List contents should match");

        // 4. HashMap
        Map<String, Integer> map = new HashMap<>();
        map.put("age", 42);
        Map<?, ?> copiedMap = CopyUtils.deepCopy(map);
        assertNotSame(map, copiedMap, "Map should be a new instance");
        assertEquals(map.get("age"), copiedMap.get("age"), "Map contents should match");

        // 5. Man object
        Man originalMan = new Man("Dima", 30, List.of("Clean Code", "Kafka Streams"));
        Man copiedMan = CopyUtils.deepCopy(originalMan);
        assertNotSame(originalMan, copiedMan, "Man instances should be different");
        assertEquals(originalMan.getName(), copiedMan.getName(), "Names should match");
        assertEquals(originalMan.getFavoriteBooks(), copiedMan.getFavoriteBooks(), "Favorite books should match");

        // 6. Cyclic reference
        Node node = new Node();
        node.self = node;
        Node copiedNode = CopyUtils.deepCopy(node);
        assertNotSame(node, copiedNode, "Cyclic object should be a new instance");
        assertSame(copiedNode.self, copiedNode, "Cyclic reference should be preserved");

        // 7. Inheritance
        ExtendedMan extMan = new ExtendedMan("Mike", 25, List.of("Book"), "USA");
        ExtendedMan copiedExtMan = CopyUtils.deepCopy(extMan);
        assertEquals(extMan.getCountry(), copiedExtMan.getCountry(), "Fields of superclass should be copied");

        // 8. Anonim class
        Object anon = new Object() {
            String message = "I am anonymous";
        };
        Object copied = CopyUtils.deepCopy(anon);
        assertSame(anon, copied, "Should get the sane anonim class object");


        System.out.println("\n✅ All tests passed successfully!");
    }

    static void assertEquals(Object a, Object b, String msg) {
        if (!Objects.equals(a, b)) throw new AssertionError(msg);
    }

    static void assertSame(Object a, Object b, String msg) {
        if (a != b) throw new AssertionError(msg);
    }

    static void assertNotSame(Object a, Object b, String msg) {
        if (a == b) throw new AssertionError(msg);
    }

    static class Node {
        Node self;
    }

    static class Man {
        private String name;
        private int age;
        private List<String> favoriteBooks;

        public Man(String name, int age, List<String> favoriteBooks) {
            this.name = name;
            this.age = age;
            this.favoriteBooks = favoriteBooks;
        }

        public String getName() {
            return name;
        }

        public List<String> getFavoriteBooks() {
            return favoriteBooks;
        }
    }

    static class ExtendedMan extends Man {
        private String country;

        public ExtendedMan(String name, int age, List<String> books, String country) {
            super(name, age, books);
            this.country = country;
        }

        public String getCountry() {
            return country;
        }
    }
}
