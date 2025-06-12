package fi.panukorpela.sheetslatex.webapp;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fi.panukorpela.sheetslatex.service.GoogleSheetsService;

@RestController
@RequestMapping("/sheets")
public class SheetsController {
    private final GoogleSheetsService sheetsService;

    public SheetsController(GoogleSheetsService sheetsService) {
        this.sheetsService = sheetsService;
    }

    @GetMapping("/read")
    public List<Object> readCell(
            @RequestParam String range
    ) throws Exception {
        return sheetsService.readRange(range);
    }
}
