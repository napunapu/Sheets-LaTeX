package fi.panukorpela.sheetslatex.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PgfPlotsChartWriterServiceTest {

    @Test
    void latexAxisLabelLineBreak() throws Exception {
        String lineBreaksAdded = PgfPlotsChartWriterService.latexAxisLabelLineBreak(
                "completion: certain amount of activities", 15);
        assertEquals("completion:\\\\certain amount\\\\of activities", lineBreaksAdded);
    }
}

