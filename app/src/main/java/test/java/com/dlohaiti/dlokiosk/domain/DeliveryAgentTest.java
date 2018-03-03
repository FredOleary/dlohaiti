package test.java.com.dlohaiti.dlokiosk.domain;

import org.junit.Test;

import main.java.com.dlohaiti.dlokiosk.domain.DeliveryAgent;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DeliveryAgentTest {

    @Test
    public void shouldHaveNaturalSortOrderOfAlphabeticalCaseInsensitive() {
        DeliveryAgent a = new DeliveryAgent("a");
        DeliveryAgent b = new DeliveryAgent("B");
        DeliveryAgent blank = new DeliveryAgent("");
        DeliveryAgent nil = new DeliveryAgent(null);

        assertThat(a.compareTo(b), is(-1));
        assertThat(b.compareTo(a), is(1));
        assertThat(blank.compareTo(a), is(-1));
        assertThat(nil.compareTo(a), is(-1));
        assertThat(nil.compareTo(blank), is(0));
    }

}
