package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class MealsUtil {
    public static void main(String[] args) {
        List<Meal> meals = Arrays.asList(
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<MealTO> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<MealTO> filteredByStreams(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumByDate = meals.stream()
                .collect(Collectors.groupingBy(Meal::getDate,
                        Collectors.summingInt(Meal::getCalories)));

        return meals.stream()
                .filter(meal -> TimeUtil.isBetweenInclusive(meal.getDateTime().toLocalTime(), startTime, endTime))
                .map(meal -> new MealTO(meal.getDateTime(), meal.getDescription(), meal.getCalories(),
                        caloriesSumByDate.get(meal.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<MealTO> filteredByCycles(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        final Map<LocalDate, Integer> caloriesSumByDate = new HashMap<>();
        for (Meal meal : meals) {
            LocalDate mealDate = meal.getDateTime().toLocalDate();
            caloriesSumByDate.put(mealDate, caloriesSumByDate.getOrDefault(mealDate, 0)
                    + meal.getCalories());
        }

        final List<MealTO> mealsTO = new ArrayList<>();
        for (Meal meal : meals) {
            LocalDateTime dateTime = meal.getDateTime();
            if (TimeUtil.isBetweenInclusive(dateTime.toLocalTime(), startTime, endTime)) {
                mealsTO.add(new MealTO(dateTime, meal.getDescription(),
                        meal.getCalories(),
                        caloriesSumByDate.get(dateTime.toLocalDate()) > caloriesPerDay));
            }
        }

        return mealsTO;
    }
}
