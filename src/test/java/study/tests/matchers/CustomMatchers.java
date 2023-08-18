package study.tests.matchers;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;

public class CustomMatchers {
    public static DiaSemanaMatcher isDayOfWeek(DayOfWeek diaSemana){
        return new DiaSemanaMatcher(diaSemana);
    }

    public static DiaSemanaMatcher isMonday(){
        return new DiaSemanaMatcher(DayOfWeek.MONDAY);
    }

    public static SameDateMatcher isToday() {
        return new SameDateMatcher(OffsetDateTime.now());
    }

    public static SameDateMatcher todayPlusDays(Long days){
        return new SameDateMatcher(OffsetDateTime.now().plusDays(days));
    }
}
