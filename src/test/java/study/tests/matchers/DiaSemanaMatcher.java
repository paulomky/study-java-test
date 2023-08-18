package study.tests.matchers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import study.tests.utils.DateUtils;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

@AllArgsConstructor
@Getter
@Setter
public class DiaSemanaMatcher extends TypeSafeMatcher<OffsetDateTime> {

    private DayOfWeek diaSemana;

    @Override
    protected boolean matchesSafely(OffsetDateTime date) {
        return DateUtils.verificaDiaSemana(date, diaSemana);
    }

    @Override
    public void describeTo(Description desc) {
        desc.appendText(diaSemana.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
    }
}
