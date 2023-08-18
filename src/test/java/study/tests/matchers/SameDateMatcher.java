package study.tests.matchers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import study.tests.utils.DateUtils;

import java.time.OffsetDateTime;

@AllArgsConstructor
@Getter
@Setter
public class SameDateMatcher extends TypeSafeMatcher<OffsetDateTime> {

    private OffsetDateTime date;

    @Override
    protected boolean matchesSafely(OffsetDateTime date) {
        return DateUtils.isMesmaData(this.date, date);
    }

    @Override
    public void describeTo(Description description) {

    }
}
