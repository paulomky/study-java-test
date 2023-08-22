package study.tests;


import org.junit.Assert;
import org.junit.Test;
import study.tests.entities.Usuario;

public class AssertsTest {
    @Test
    public void Test(){
        Assert.assertTrue(true);
        Assert.assertFalse(false);

        Assert.assertEquals(1L, 1L);
        Assert.assertEquals(0.5134, 0.513, 0.01);
        Assert.assertEquals(Math.PI, 3.14, 0.01);

        int i1 = 5;
        Integer i2 = 5;
        Assert.assertEquals(Integer.valueOf(i1), i2);
        Assert.assertEquals(i1, i2.intValue());

        Assert.assertEquals("bola", "bola");
        Assert.assertTrue("bola".equalsIgnoreCase("Bola"));
        Assert.assertTrue("bola".startsWith("bo"));

        var u1 = new Usuario("Usuario 1");
        var u2 = new Usuario("Usuario 1");
        Usuario u3 = null;

        Assert.assertEquals(u1, u2);

        Assert.assertSame(u1, u1);
        Assert.assertNotSame(u1, u2);

        Assert.assertNull(u3);
    }
}
