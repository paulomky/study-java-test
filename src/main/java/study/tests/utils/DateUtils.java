package study.tests.utils;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    public static OffsetDateTime adicionarDias(OffsetDateTime data, Long dias){
        return data.plusDays(dias);
    }

    public static boolean isMesmaData(OffsetDateTime data1, OffsetDateTime data2){
        return data1.truncatedTo(ChronoUnit.HOURS).equals(data2.truncatedTo(ChronoUnit.HOURS));
    }

    public static OffsetDateTime obterDataComDiferencaDias(Long dia){
        return adicionarDias(OffsetDateTime.now(), dia);
    }

    public static boolean verificaDiaSemana(OffsetDateTime date, DayOfWeek diaSemana){
        return date.getDayOfWeek().equals(diaSemana);
    }
}
