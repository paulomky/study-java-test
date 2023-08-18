package study.tests.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import study.tests.services.LocacaoServiceTest;
import study.tests.services.LocacaoServiceValidarDescontosTest;

//@RunWith(Suite.class)
@Suite.SuiteClasses({
        LocacaoServiceTest.class
        , LocacaoServiceValidarDescontosTest.class
})
public class SuiteExecucao {
}
