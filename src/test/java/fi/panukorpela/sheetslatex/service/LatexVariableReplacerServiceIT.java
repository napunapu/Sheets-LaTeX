package fi.panukorpela.sheetslatex.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LatexVariableReplacerServiceIT {

    @Autowired
    private LatexVariableReplacerService latexVariableReplacerService;

    @Test
    void testReadLatexVariablesWithRealSheet() throws Exception {
        // ACT: Call the real service method
        latexVariableReplacerService.replaceVarsInLatexFile();
    }
}

